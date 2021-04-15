package net.dzikoysk.funnyguilds.util.metrics

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.net.URL
import java.util.*
import java.util.logging.Level
import java.util.zip.GZIPOutputStream
import javax.net.ssl.HttpsURLConnection

/**
 * bStats collects some data for plugin authors.
 *
 *
 * Check out https://bStats.org/ to learn more about bStats!
 */
class BStats(plugin: JavaPlugin?) {
    // The plugin
    private val plugin: JavaPlugin

    // A list with all custom charts
    private val charts: MutableList<CustomChart> = ArrayList()

    /**
     * Adds a custom chart.
     *
     * @param chart The chart to add.
     */
    fun addCustomChart(chart: CustomChart?) {
        requireNotNull(chart) { "Chart cannot be null!" }
        charts.add(chart)
    }

    /**
     * Starts the Scheduler which submits our data every 30 minutes.
     */
    private fun startSubmitting() {
        val timer = Timer(true) // We use a timer cause the Bukkit scheduler is affected by server lags
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (!plugin.isEnabled) { // Plugin was disabled
                    timer.cancel()
                    return
                }
                // Nevertheless we want our code to run in the Bukkit main thread, so we have to use the Bukkit scheduler
                // Don't be afraid! The connection to the bStats server is still async, only the stats collection is sync ;)
                Bukkit.getScheduler().runTask(plugin, Runnable { submitData() })
            }
        }, (1000 * 60 * 5).toLong(), (1000 * 60 * 30).toLong())
        // Submit the data every 30 minutes, first time after 5 minutes to give other plugins enough time to start
        // WARNING: Changing the frequency has no effect but your plugin WILL be blocked/deleted!
        // WARNING: Just don't do it!
    }

    /**
     * Collects the data and sends it afterwards.
     */
    private fun submitData() {
        val data = serverData
        val pluginData = JSONArray()
        // Search for all other bStats Metrics classes to get their plugin data
        for (service in Bukkit.getServicesManager().knownServices) {
            try {
                service.getField("B_STATS_VERSION") // Our identifier :)
            } catch (ignored: NoSuchFieldException) {
                continue  // Continue "searching"
            }
            // Found one!
            try {
                pluginData.add(service.getMethod("getPluginData").invoke(Bukkit.getServicesManager().load(service)))
            } catch (ignored: NoSuchMethodException) {
            } catch (ignored: IllegalAccessException) {
            } catch (ignored: InvocationTargetException) {
            }
        }
        data["plugins"] = pluginData

        // Create a new thread for the connection to the bStats server
        Thread {
            try {
                // Send the data
                sendData(data)
            } catch (e: Exception) {
                // Something went wrong! :(
                if (logFailedRequests) {
                    plugin.logger.log(Level.WARNING, "Could not submit plugin stats of FunnyGuilds", e)
                }
            }
        }.start()
    }// If the chart is null, we skip it// Add the data of the custom charts// Append the name of the plugin
    // Append the version of the plugin
    /**
     * Gets the plugin specific data.
     * This method is called using Reflection.
     *
     * @return The plugin specific data.
     */
    // called by reflections
    val pluginData: JSONObject
        get() {
            val data = JSONObject()
            data["pluginName"] = "FunnyGuilds" // Append the name of the plugin
            data["pluginVersion"] = FunnyGuilds.Companion.getInstance().getVersion().getMainVersion() // Append the version of the plugin
            val customCharts = JSONArray()
            for (customChart in charts) {
                // Add the data of the custom charts
                val chart = customChart.requestJsonObject
                    ?: // If the chart is null, we skip it
                    continue
                customCharts.add(chart)
            }
            data["customCharts"] = customCharts
            return data
        }// Minecraft specific data

    // OS/Java specific data
    /**
     * Gets the server specific data.
     *
     * @return The server specific data.
     */
    private val serverData: JSONObject
        private get() {
            // Minecraft specific data
            val playerAmount = Bukkit.getOnlinePlayers().size
            val onlineMode = if (Bukkit.getOnlineMode()) 1 else 0
            var bukkitVersion = Bukkit.getVersion()
            bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length - 1)

            // OS/Java specific data
            val javaVersion = System.getProperty("java.version")
            val osName = System.getProperty("os.name")
            val osArch = System.getProperty("os.arch")
            val osVersion = System.getProperty("os.version")
            val coreCount = Runtime.getRuntime().availableProcessors()
            val data = JSONObject()
            data["serverUUID"] = serverUUID
            data["playerAmount"] = playerAmount
            data["onlineMode"] = onlineMode
            data["bukkitVersion"] = bukkitVersion
            data["javaVersion"] = javaVersion
            data["osName"] = osName
            data["osArch"] = osArch
            data["osVersion"] = osVersion
            data["coreCount"] = coreCount
            return data
        }

    /**
     * A enum which is used for custom maps.
     */
    enum class Country(
        /**
         * Gets the iso tag of the country.
         *
         * @return The iso tag of the country.
         */
        val countryIsoTag: String,
        /**
         * Gets the name of the country.
         *
         * @return The name of the country.
         */
        val countryName: String
    ) {
        /**
         * bStats will use the country of the server.
         */
        AUTO_DETECT("AUTO", "Auto Detected"), ANDORRA("AD", "Andorra"), UNITED_ARAB_EMIRATES("AE", "United Arab Emirates"), AFGHANISTAN("AF", "Afghanistan"), ANTIGUA_AND_BARBUDA(
            "AG",
            "Antigua and Barbuda"
        ),
        ANGUILLA("AI", "Anguilla"), ALBANIA("AL", "Albania"), ARMENIA("AM", "Armenia"), NETHERLANDS_ANTILLES("AN", "Netherlands Antilles"), ANGOLA("AO", "Angola"), ANTARCTICA(
            "AQ",
            "Antarctica"
        ),
        ARGENTINA("AR", "Argentina"), AMERICAN_SAMOA("AS", "American Samoa"), AUSTRIA("AT", "Austria"), AUSTRALIA("AU", "Australia"), ARUBA("AW", "Aruba"), ALAND_ISLANDS(
            "AX",
            "Åland Islands"
        ),
        AZERBAIJAN("AZ", "Azerbaijan"), BOSNIA_AND_HERZEGOVINA("BA", "Bosnia and Herzegovina"), BARBADOS("BB", "Barbados"), BANGLADESH("BD", "Bangladesh"), BELGIUM("BE", "Belgium"), BURKINA_FASO(
            "BF",
            "Burkina Faso"
        ),
        BULGARIA("BG", "Bulgaria"), BAHRAIN("BH", "Bahrain"), BURUNDI("BI", "Burundi"), BENIN("BJ", "Benin"), SAINT_BARTHELEMY("BL", "Saint Barthélemy"), BERMUDA("BM", "Bermuda"), BRUNEI(
            "BN",
            "Brunei"
        ),
        BOLIVIA("BO", "Bolivia"), BONAIRE_SINT_EUSTATIUS_AND_SABA("BQ", "Bonaire, Sint Eustatius and Saba"), BRAZIL("BR", "Brazil"), BAHAMAS("BS", "Bahamas"), BHUTAN(
            "BT",
            "Bhutan"
        ),
        BOUVET_ISLAND("BV", "Bouvet Island"), BOTSWANA("BW", "Botswana"), BELARUS("BY", "Belarus"), BELIZE("BZ", "Belize"), CANADA("CA", "Canada"), COCOS_ISLANDS(
            "CC",
            "Cocos Islands"
        ),
        THE_DEMOCRATIC_REPUBLIC_OF_CONGO("CD", "The Democratic Republic Of Congo"), CENTRAL_AFRICAN_REPUBLIC("CF", "Central African Republic"), CONGO("CG", "Congo"), SWITZERLAND(
            "CH",
            "Switzerland"
        ),
        COTE_D_IVOIRE("CI", "Côte d'Ivoire"), COOK_ISLANDS("CK", "Cook Islands"), CHILE("CL", "Chile"), CAMEROON("CM", "Cameroon"), CHINA("CN", "China"), COLOMBIA("CO", "Colombia"), COSTA_RICA(
            "CR",
            "Costa Rica"
        ),
        CUBA("CU", "Cuba"), CAPE_VERDE("CV", "Cape Verde"), CURACAO("CW", "Curaçao"), CHRISTMAS_ISLAND("CX", "Christmas Island"), CYPRUS("CY", "Cyprus"), CZECH_REPUBLIC(
            "CZ",
            "Czech Republic"
        ),
        GERMANY("DE", "Germany"), DJIBOUTI("DJ", "Djibouti"), DENMARK("DK", "Denmark"), DOMINICA("DM", "Dominica"), DOMINICAN_REPUBLIC("DO", "Dominican Republic"), ALGERIA(
            "DZ",
            "Algeria"
        ),
        ECUADOR("EC", "Ecuador"), ESTONIA("EE", "Estonia"), EGYPT("EG", "Egypt"), WESTERN_SAHARA("EH", "Western Sahara"), ERITREA("ER", "Eritrea"), SPAIN("ES", "Spain"), ETHIOPIA(
            "ET",
            "Ethiopia"
        ),
        FINLAND("FI", "Finland"), FIJI("FJ", "Fiji"), FALKLAND_ISLANDS("FK", "Falkland Islands"), MICRONESIA("FM", "Micronesia"), FAROE_ISLANDS("FO", "Faroe Islands"), FRANCE(
            "FR",
            "France"
        ),
        GABON("GA", "Gabon"), UNITED_KINGDOM("GB", "United Kingdom"), GRENADA("GD", "Grenada"), GEORGIA("GE", "Georgia"), FRENCH_GUIANA("GF", "French Guiana"), GUERNSEY("GG", "Guernsey"), GHANA(
            "GH",
            "Ghana"
        ),
        GIBRALTAR("GI", "Gibraltar"), GREENLAND("GL", "Greenland"), GAMBIA("GM", "Gambia"), GUINEA("GN", "Guinea"), GUADELOUPE("GP", "Guadeloupe"), EQUATORIAL_GUINEA(
            "GQ",
            "Equatorial Guinea"
        ),
        GREECE("GR", "Greece"), SOUTH_GEORGIA_AND_THE_SOUTH_SANDWICH_ISLANDS("GS", "South Georgia And The South Sandwich Islands"), GUATEMALA("GT", "Guatemala"), GUAM(
            "GU",
            "Guam"
        ),
        GUINEA_BISSAU("GW", "Guinea-Bissau"), GUYANA("GY", "Guyana"), HONG_KONG("HK", "Hong Kong"), HEARD_ISLAND_AND_MCDONALD_ISLANDS("HM", "Heard Island And McDonald Islands"), HONDURAS(
            "HN",
            "Honduras"
        ),
        CROATIA("HR", "Croatia"), HAITI("HT", "Haiti"), HUNGARY("HU", "Hungary"), INDONESIA("ID", "Indonesia"), IRELAND("IE", "Ireland"), ISRAEL("IL", "Israel"), ISLE_OF_MAN(
            "IM",
            "Isle Of Man"
        ),
        INDIA("IN", "India"), BRITISH_INDIAN_OCEAN_TERRITORY("IO", "British Indian Ocean Territory"), IRAQ("IQ", "Iraq"), IRAN("IR", "Iran"), ICELAND("IS", "Iceland"), ITALY(
            "IT",
            "Italy"
        ),
        JERSEY("JE", "Jersey"), JAMAICA("JM", "Jamaica"), JORDAN("JO", "Jordan"), JAPAN("JP", "Japan"), KENYA("KE", "Kenya"), KYRGYZSTAN("KG", "Kyrgyzstan"), CAMBODIA("KH", "Cambodia"), KIRIBATI(
            "KI",
            "Kiribati"
        ),
        COMOROS("KM", "Comoros"), SAINT_KITTS_AND_NEVIS("KN", "Saint Kitts And Nevis"), NORTH_KOREA("KP", "North Korea"), SOUTH_KOREA("KR", "South Korea"), KUWAIT("KW", "Kuwait"), CAYMAN_ISLANDS(
            "KY",
            "Cayman Islands"
        ),
        KAZAKHSTAN("KZ", "Kazakhstan"), LAOS("LA", "Laos"), LEBANON("LB", "Lebanon"), SAINT_LUCIA("LC", "Saint Lucia"), LIECHTENSTEIN("LI", "Liechtenstein"), SRI_LANKA(
            "LK",
            "Sri Lanka"
        ),
        LIBERIA("LR", "Liberia"), LESOTHO("LS", "Lesotho"), LITHUANIA("LT", "Lithuania"), LUXEMBOURG("LU", "Luxembourg"), LATVIA("LV", "Latvia"), LIBYA("LY", "Libya"), MOROCCO(
            "MA",
            "Morocco"
        ),
        MONACO("MC", "Monaco"), MOLDOVA("MD", "Moldova"), MONTENEGRO("ME", "Montenegro"), SAINT_MARTIN("MF", "Saint Martin"), MADAGASCAR("MG", "Madagascar"), MARSHALL_ISLANDS(
            "MH",
            "Marshall Islands"
        ),
        MACEDONIA("MK", "Macedonia"), MALI("ML", "Mali"), MYANMAR("MM", "Myanmar"), MONGOLIA("MN", "Mongolia"), MACAO("MO", "Macao"), NORTHERN_MARIANA_ISLANDS(
            "MP",
            "Northern Mariana Islands"
        ),
        MARTINIQUE("MQ", "Martinique"), MAURITANIA("MR", "Mauritania"), MONTSERRAT("MS", "Montserrat"), MALTA("MT", "Malta"), MAURITIUS("MU", "Mauritius"), MALDIVES("MV", "Maldives"), MALAWI(
            "MW",
            "Malawi"
        ),
        MEXICO("MX", "Mexico"), MALAYSIA("MY", "Malaysia"), MOZAMBIQUE("MZ", "Mozambique"), NAMIBIA("NA", "Namibia"), NEW_CALEDONIA("NC", "New Caledonia"), NIGER("NE", "Niger"), NORFOLK_ISLAND(
            "NF",
            "Norfolk Island"
        ),
        NIGERIA("NG", "Nigeria"), NICARAGUA("NI", "Nicaragua"), NETHERLANDS("NL", "Netherlands"), NORWAY("NO", "Norway"), NEPAL("NP", "Nepal"), NAURU("NR", "Nauru"), NIUE(
            "NU",
            "Niue"
        ),
        NEW_ZEALAND("NZ", "New Zealand"), OMAN("OM", "Oman"), PANAMA("PA", "Panama"), PERU("PE", "Peru"), FRENCH_POLYNESIA("PF", "French Polynesia"), PAPUA_NEW_GUINEA(
            "PG",
            "Papua New Guinea"
        ),
        PHILIPPINES("PH", "Philippines"), PAKISTAN("PK", "Pakistan"), POLAND("PL", "Poland"), SAINT_PIERRE_AND_MIQUELON("PM", "Saint Pierre And Miquelon"), PITCAIRN(
            "PN",
            "Pitcairn"
        ),
        PUERTO_RICO("PR", "Puerto Rico"), PALESTINE("PS", "Palestine"), PORTUGAL("PT", "Portugal"), PALAU("PW", "Palau"), PARAGUAY("PY", "Paraguay"), QATAR("QA", "Qatar"), REUNION(
            "RE",
            "Reunion"
        ),
        ROMANIA("RO", "Romania"), SERBIA("RS", "Serbia"), RUSSIA("RU", "Russia"), RWANDA("RW", "Rwanda"), SAUDI_ARABIA("SA", "Saudi Arabia"), SOLOMON_ISLANDS("SB", "Solomon Islands"), SEYCHELLES(
            "SC",
            "Seychelles"
        ),
        SUDAN("SD", "Sudan"), SWEDEN("SE", "Sweden"), SINGAPORE("SG", "Singapore"), SAINT_HELENA("SH", "Saint Helena"), SLOVENIA("SI", "Slovenia"), SVALBARD_AND_JAN_MAYEN(
            "SJ",
            "Svalbard And Jan Mayen"
        ),
        SLOVAKIA("SK", "Slovakia"), SIERRA_LEONE("SL", "Sierra Leone"), SAN_MARINO("SM", "San Marino"), SENEGAL("SN", "Senegal"), SOMALIA("SO", "Somalia"), SURINAME(
            "SR",
            "Suriname"
        ),
        SOUTH_SUDAN("SS", "South Sudan"), SAO_TOME_AND_PRINCIPE("ST", "Sao Tome And Principe"), EL_SALVADOR("SV", "El Salvador"), SINT_MAARTEN_DUTCH_PART(
            "SX",
            "Sint Maarten (Dutch part)"
        ),
        SYRIA("SY", "Syria"), SWAZILAND("SZ", "Swaziland"), TURKS_AND_CAICOS_ISLANDS("TC", "Turks And Caicos Islands"), CHAD("TD", "Chad"), FRENCH_SOUTHERN_TERRITORIES(
            "TF",
            "French Southern Territories"
        ),
        TOGO("TG", "Togo"), THAILAND("TH", "Thailand"), TAJIKISTAN("TJ", "Tajikistan"), TOKELAU("TK", "Tokelau"), TIMOR_LESTE("TL", "Timor-Leste"), TURKMENISTAN("TM", "Turkmenistan"), TUNISIA(
            "TN",
            "Tunisia"
        ),
        TONGA("TO", "Tonga"), TURKEY("TR", "Turkey"), TRINIDAD_AND_TOBAGO("TT", "Trinidad and Tobago"), TUVALU("TV", "Tuvalu"), TAIWAN("TW", "Taiwan"), TANZANIA("TZ", "Tanzania"), UKRAINE(
            "UA",
            "Ukraine"
        ),
        UGANDA("UG", "Uganda"), UNITED_STATES_MINOR_OUTLYING_ISLANDS("UM", "United States Minor Outlying Islands"), UNITED_STATES("US", "United States"), URUGUAY("UY", "Uruguay"), UZBEKISTAN(
            "UZ",
            "Uzbekistan"
        ),
        VATICAN("VA", "Vatican"), SAINT_VINCENT_AND_THE_GRENADINES("VC", "Saint Vincent And The Grenadines"), VENEZUELA("VE", "Venezuela"), BRITISH_VIRGIN_ISLANDS(
            "VG",
            "British Virgin Islands"
        ),
        U_S__VIRGIN_ISLANDS("VI", "U.S. Virgin Islands"), VIETNAM("VN", "Vietnam"), VANUATU("VU", "Vanuatu"), WALLIS_AND_FUTUNA("WF", "Wallis And Futuna"), SAMOA("WS", "Samoa"), YEMEN(
            "YE",
            "Yemen"
        ),
        MAYOTTE("YT", "Mayotte"), SOUTH_AFRICA("ZA", "South Africa"), ZAMBIA("ZM", "Zambia"), ZIMBABWE("ZW", "Zimbabwe");

        companion object {
            /**
             * Gets a country by it's iso tag.
             *
             * @param isoTag The iso tag of the county.
             * @return The country with the given iso tag or `null` if unknown.
             */
            fun byIsoTag(isoTag: String): Country? {
                for (country in values()) {
                    if (country.countryIsoTag == isoTag) {
                        return country
                    }
                }
                return null
            }

            /**
             * Gets a country by a locale.
             *
             * @param locale The locale.
             * @return The country from the giben locale or `null` if unknown country or
             * if the locale does not contain a country.
             */
            fun byLocale(locale: Locale): Country? {
                return byIsoTag(locale.country)
            }
        }
    }

    /**
     * Represents a custom chart.
     */
    abstract class CustomChart(chartId: String?) {
        // The id of the chart
        protected val chartId: String

        // If the data is null we don't send the chart.
        val requestJsonObject: JSONObject?
            get() {
                val chart = JSONObject()
                chart["chartId"] = chartId
                try {
                    val data = chartData
                        ?: // If the data is null we don't send the chart.
                        return null
                    chart["data"] = data
                } catch (t: Throwable) {
                    if (logFailedRequests) {
                        Bukkit.getLogger().log(Level.WARNING, "Failed to get data for custom chart with id $chartId", t)
                    }
                    return null
                }
                return chart
            }
        protected abstract val chartData: JSONObject?

        /**
         * Class constructor.
         *
         * @param chartId The id of the chart.
         */
        init {
            require(!(chartId == null || chartId.isEmpty())) { "ChartId cannot be null or empty!" }
            this.chartId = chartId
        }
    }

    /**
     * Represents a custom simple pie.
     */
    abstract class SimplePie
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the pie.
         *
         * @return The value of the pie.
         */
        abstract val value: String?

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val value = value
                if (value == null || value.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                data["value"] = value
                return data
            }
    }

    /**
     * Represents a custom advanced pie.
     */
    abstract class AdvancedPie
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the values of the pie.
         *
         * @param valueMap Just an empty map. The only reason it exists is to make your life easier.
         * You don't have to create a map yourself!
         * @return The values of the pie.
         */
        abstract fun getValues(valueMap: HashMap<String?, Int?>?): HashMap<String, Int>?// Null = skip the chart// Skip this invalid

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map = getValues(HashMap())
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var allSkipped = true
                for ((key, value) in map) {
                    if (value == 0) {
                        continue  // Skip this invalid
                    }
                    allSkipped = false
                    values[key] = value
                }
                if (allSkipped) {
                    // Null = skip the chart
                    return null
                }
                data["values"] = values
                return data
            }
    }

    /**
     * Represents a custom single line chart.
     */
    abstract class SingleLineChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the chart.
         *
         * @return The value of the chart.
         */
        abstract val value: Int

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val value = value
                if (value == 0) {
                    // Null = skip the chart
                    return null
                }
                data["value"] = value
                return data
            }
    }

    /**
     * Represents a custom multi line chart.
     */
    abstract class MultiLineChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the values of the chart.
         *
         * @param valueMap Just an empty map. The only reason it exists is to make your life easier.
         * You don't have to create a map yourself!
         * @return The values of the chart.
         */
        abstract fun getValues(valueMap: HashMap<String?, Int>): HashMap<String?, Int>?// Null = skip the chart// Skip this invalid

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map = getValues(HashMap())
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var allSkipped = true
                for ((key, value) in map) {
                    if (value == 0) {
                        continue  // Skip this invalid
                    }
                    allSkipped = false
                    values[key] = value
                }
                if (allSkipped) {
                    // Null = skip the chart
                    return null
                }
                data["values"] = values
                return data
            }
    }

    /**
     * Represents a custom simple bar chart.
     */
    abstract class SimpleBarChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the chart.
         *
         * @param valueMap Just an empty map. The only reason it exists is to make your life easier.
         * You don't have to create a map yourself!
         * @return The value of the chart.
         */
        abstract fun getValues(valueMap: HashMap<String?, Int?>?): HashMap<String, Int>?

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map = getValues(HashMap())
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                for ((key, value) in map) {
                    val categoryValues = JSONArray()
                    categoryValues.add(value)
                    values[key] = categoryValues
                }
                data["values"] = values
                return data
            }
    }

    /**
     * Represents a custom advanced bar chart.
     */
    abstract class AdvancedBarChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the chart.
         *
         * @param valueMap Just an empty map. The only reason it exists is to make your life easier.
         * You don't have to create a map yourself!
         * @return The value of the chart.
         */
        abstract fun getValues(valueMap: HashMap<String?, IntArray?>?): HashMap<String, IntArray>?// Null = skip the chart// Skip this invalid

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map = getValues(HashMap())
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var allSkipped = true
                for ((key, value) in map) {
                    if (value.length == 0) {
                        continue  // Skip this invalid
                    }
                    allSkipped = false
                    val categoryValues = JSONArray()
                    for (categoryValue in value) {
                        categoryValues.add(categoryValue)
                    }
                    values[key] = categoryValues
                }
                if (allSkipped) {
                    // Null = skip the chart
                    return null
                }
                data["values"] = values
                return data
            }
    }

    /**
     * Represents a custom simple map chart.
     */
    abstract class SimpleMapChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the chart.
         *
         * @return The value of the chart.
         */
        abstract val value: Country?

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val value = value
                    ?: // Null = skip the chart
                    return null
                data["value"] = value.countryIsoTag
                return data
            }
    }

    /**
     * Represents a custom advanced map chart.
     */
    abstract class AdvancedMapChart
    /**
     * Class constructor.
     *
     * @param chartId The id of the chart.
     */
        (chartId: String?) : CustomChart(chartId) {
        /**
         * Gets the value of the chart.
         *
         * @param valueMap Just an empty map. The only reason it exists is to make your life easier.
         * You don't have to create a map yourself!
         * @return The value of the chart.
         */
        abstract fun getValues(valueMap: HashMap<Country?, Int?>?): HashMap<Country, Int>?// Null = skip the chart// Skip this invalid

        // Null = skip the chart
        override val chartData: JSONObject?
            protected get() {
                val data = JSONObject()
                val values = JSONObject()
                val map = getValues(HashMap())
                if (map == null || map.isEmpty()) {
                    // Null = skip the chart
                    return null
                }
                var allSkipped = true
                for ((key, value) in map) {
                    if (value == 0) {
                        continue  // Skip this invalid
                    }
                    allSkipped = false
                    values[key.getCountryIsoTag()] = value
                }
                if (allSkipped) {
                    // Null = skip the chart
                    return null
                }
                data["values"] = values
                return data
            }
    }

    companion object {
        /*static {
        // Maven's Relocate is clever and markChanged strings, too. So we have to use this little "trick" ... :D
        final String defaultPackage = new String(new byte[] { 'o', 'r', 'g', '.', 'b', 's', 't', 'a', 't', 's' });
        final String examplePackage = new String(new byte[] { 'y', 'o', 'u', 'r', '.', 'p', 'a', 'c', 'k', 'a', 'g', 'e' });
        // We want to make sure nobody just copy & pastes the example and use the wrong package names
        if (Metrics.class.getPackage().getName().equals(defaultPackage) || Metrics.class.getPackage().getName().equals(examplePackage)) {
            throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
        }
    }*/
        // The version of this bStats class
        const val B_STATS_VERSION = 1

        // The url to which the data is sent
        private const val URL = "https://bStats.org/submitData/bukkit"

        // Should failed requests be logged?
        private var logFailedRequests: Boolean

        // The uuid of the server
        private var serverUUID: String

        /**
         * Sends the data to the bStats server.
         *
         * @param data The data to send.
         * @throws Exception If the request failed.
         */
        @Throws(Exception::class)
        private fun sendData(data: JSONObject?) {
            requireNotNull(data) { "InvitationPersistenceHandler cannot be null!" }
            if (Bukkit.isPrimaryThread()) {
                throw IllegalAccessException("This method must not be called from the main thread!")
            }
            val connection = URL(URL).openConnection() as HttpsURLConnection

            // Compress the data to save bandwidth
            val compressedData = compress(data.toString())

            // Add headers
            connection.requestMethod = "POST"
            connection.addRequestProperty("Accept", "application/json")
            connection.addRequestProperty("Connection", "close")
            connection.addRequestProperty("Content-Encoding", "gzip") // We gzip our request
            connection.addRequestProperty("Content-Length", compressedData!!.size.toString())
            connection.setRequestProperty("Content-Type", "application/json") // We send our data in JSON format
            connection.setRequestProperty("User-Agent", "MC-Server/" + B_STATS_VERSION)

            // Send data
            connection.doOutput = true
            val outputStream = DataOutputStream(connection.outputStream)
            outputStream.write(compressedData)
            outputStream.flush()
            outputStream.close()
            connection.inputStream.close() // We don't care about the response - Just send our data :)
        }

        /**
         * Gzips the given String.
         *
         * @param str The string to gzip.
         * @return The gzipped String.
         * @throws IOException If the compression failed.
         */
        @Throws(IOException::class)
        private fun compress(str: String?): ByteArray? {
            if (str == null) {
                return null
            }
            val outputStream = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(outputStream)
            gzip.write(str.toByteArray(charset("UTF-8")))
            gzip.close()
            return outputStream.toByteArray()
        }
    }

    /**
     * Class constructor.
     *
     * @param plugin The plugin which stats should be submitted.
     */
    init {
        requireNotNull(plugin) { "Plugin cannot be null!" }
        this.plugin = plugin

        // Get the config file
        val bStatsFolder = File(plugin.dataFolder.parentFile, "bStats")
        val configFile = File(bStatsFolder, "config.yml")
        val config = YamlConfiguration.loadConfiguration(configFile)

        // Check if the config file exists
        if (!config.isSet("serverUuid")) {

            // Add default values
            config.addDefault("enabled", true)
            // Every server gets it's unique random id.
            config.addDefault("serverUuid", UUID.randomUUID().toString())
            // Should failed request be logged?
            config.addDefault("logFailedRequests", false)

            // Inform the server owners about bStats
            config.options().header(
                """
                    bStats collects some data for plugin authors like how many servers are using their plugins.
                    To honor their work, you should not disable it.
                    This has nearly no effect on the server performance!
                    Check out https://bStats.org/ to learn more :)
                    """.trimIndent()
            ).copyDefaults(true)
            try {
                config.save(configFile)
            } catch (ignored: IOException) {
            }
        }

        // Load the data
        serverUUID = config.getString("serverUuid")!!
        logFailedRequests = config.getBoolean("logFailedRequests", false)
        if (config.getBoolean("enabled", true)) {
            var found = false
            // Search for all other bStats Metrics classes to see if we are the first one
            for (service in Bukkit.getServicesManager().knownServices) {
                try {
                    service.getField("B_STATS_VERSION") // Our identifier :)
                    found = true // We aren't the first
                    break
                } catch (ignored: NoSuchFieldException) {
                }
            }
            // Register our service
            Bukkit.getServicesManager().register(BStats::class.java, this, plugin, ServicePriority.Normal)
            if (!found) {
                // We are the first!
                startSubmitting()
            }
        }
    }
}