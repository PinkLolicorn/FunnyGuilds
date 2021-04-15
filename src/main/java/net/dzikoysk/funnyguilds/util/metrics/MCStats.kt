package net.dzikoysk.funnyguilds.util.metrics

import net.dzikoysk.funnyguilds.FunnyGuilds
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import java.io.*
import java.net.Proxy
import java.net.URL
import java.net.URLConnection
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.logging.Level
import java.util.zip.GZIPOutputStream

class MCStats(plugin: Plugin?) {
    private val plugin: Plugin
    private val graphs = Collections.synchronizedSet(HashSet<Graph>())
    private val configuration: YamlConfiguration
    private val configurationFile: File
    private val guid: String
    private val debug: Boolean
    private val optOutLock = Any()

    @Volatile
    private var task: BukkitTask? = null
    fun createGraph(name: String?): Graph {
        requireNotNull(name) { "Graph name cannot be null" }

        // Construct the graph object
        val graph = Graph(name)

        // Now we can add our graph
        graphs.add(graph)

        // and return back
        return graph
    }

    fun addGraph(graph: Graph?) {
        requireNotNull(graph) { "Graph cannot be null" }
        graphs.add(graph)
    }

    fun start(): Boolean {
        synchronized(optOutLock) {

            // Did we opt out?
            if (isOptOut) {
                return false
            }

            // Is metrics already running?
            if (task != null) {
                return true
            }

            // Begin hitting the server with glorious data
            task = plugin.server.scheduler.runTaskTimerAsynchronously(plugin, object : Runnable {
                private var firstPost = true
                override fun run() {
                    try {
                        // This has to be synchronized or it can collide with the disable method.
                        synchronized(optOutLock) {
                            // Disable Task, if it is running and the server owner decided to opt-out
                            if (isOptOut && task != null) {
                                task.cancel()
                                task = null
                                // Tell all plotters to stop gathering information.
                                for (graph in graphs) {
                                    graph.onOptOut()
                                }
                            }
                        }

                        // We use the inverse of firstPost because if it is the first time we are posting,
                        // it is not a interval ping, so it evaluates to FALSE
                        // Each time thereafter it will evaluate to TRUE, i.e PING!
                        postPlugin(!firstPost)

                        // After the first post we set firstPost to false
                        // Each post thereafter will be a ping
                        firstPost = false
                    } catch (e: IOException) {
                        if (debug) {
                            Bukkit.getLogger().log(Level.INFO, "[Metrics] " + e.message)
                        }
                    }
                }
            }, 0, (PING_INTERVAL * 1200).toLong())
            return true
        }
    }

    @Throws(IOException::class)
    fun enable() {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized(optOutLock) {

            // Check if the server owner has already set opt-out, if not, set it.
            if (isOptOut) {
                configuration["opt-out"] = false
                configuration.save(configurationFile)
            }

            // Enable Task, if it is not running
            if (task == null) {
                start()
            }
        }
    }

    @Throws(IOException::class)
    fun disable() {
        // This has to be synchronized or it can collide with the check in the task.
        synchronized(optOutLock) {

            // Check if the server owner has already set opt-out, if not, set it.
            if (!isOptOut) {
                configuration["opt-out"] = true
                configuration.save(configurationFile)
            }

            // Disable Task, if it is running
            if (task != null) {
                task!!.cancel()
                task = null
            }
        }
    }

    @Throws(IOException::class)
    private fun postPlugin(isPing: Boolean) {
        // Server software specific section
        val pluginName = "FunnyGuilds"
        val onlineMode = Bukkit.getServer().onlineMode // TRUE if online mode is enabled
        val pluginVersion: String = FunnyGuilds.Companion.getInstance().getVersion().getMainVersion()
        val serverVersion = Bukkit.getVersion()
        val playersOnline = Bukkit.getServer().onlinePlayers.size

        // END server software specific section -- all code below does not use any code outside of this class / Java

        // Construct the post data
        val json = StringBuilder(1024)
        json.append('{')

        // The plugin's description file containg all of the plugin data such as name, version, author, etc
        appendJSONPair(json, "guid", guid)
        appendJSONPair(json, "plugin_version", pluginVersion)
        appendJSONPair(json, "server_version", serverVersion)
        appendJSONPair(json, "players_online", Integer.toString(playersOnline))

        // New data as of R6
        val osname = System.getProperty("os.name")
        var osarch = System.getProperty("os.arch")
        val osversion = System.getProperty("os.version")
        val java_version = System.getProperty("java.version")
        val coreCount = Runtime.getRuntime().availableProcessors()

        // normalize os arch .. amd64 -> x86_64
        if (osarch == "amd64") {
            osarch = "x86_64"
        }
        appendJSONPair(json, "osname", osname)
        appendJSONPair(json, "osarch", osarch)
        appendJSONPair(json, "osversion", osversion)
        appendJSONPair(json, "cores", Integer.toString(coreCount))
        appendJSONPair(json, "auth_mode", if (onlineMode) "1" else "0")
        appendJSONPair(json, "java_version", java_version)

        // If we're pinging, append it
        if (isPing) {
            appendJSONPair(json, "ping", "1")
        }
        if (graphs.size > 0) {
            synchronized(graphs) {
                json.append(',')
                json.append('"')
                json.append("graphs")
                json.append('"')
                json.append(':')
                json.append('{')
                var firstGraph = true
                for (graph in graphs) {
                    val graphJson = StringBuilder()
                    graphJson.append('{')
                    for (plotter in graph.getPlotters()) {
                        appendJSONPair(graphJson, plotter.columnName, Integer.toString(plotter.value))
                    }
                    graphJson.append('}')
                    if (!firstGraph) {
                        json.append(',')
                    }
                    json.append(escapeJSON(graph.name))
                    json.append(':')
                    json.append(graphJson)
                    firstGraph = false
                }
                json.append('}')
            }
        }

        // close json
        json.append('}')

        // Create the url
        val url = URL(BASE_URL + String.format(REPORT_URL, urlEncode(pluginName)))

        // Connect to the website
        val connection: URLConnection

        // Mineshafter creates a socks proxy, so we can safely bypass it
        // It does not reroute POST requests so we need to go around it
        connection = if (isMineshafterPresent) {
            url.openConnection(Proxy.NO_PROXY)
        } else {
            url.openConnection()
        }
        val uncompressed = json.toString().toByteArray()
        val compressed = gzip(json.toString())

        // Headers
        connection.addRequestProperty("User-Agent", "MCStats/" + REVISION)
        connection.addRequestProperty("Content-Type", "application/json")
        connection.addRequestProperty("Content-Encoding", "gzip")
        connection.addRequestProperty("Content-Length", Integer.toString(compressed.size))
        connection.addRequestProperty("Accept", "application/json")
        connection.addRequestProperty("Connection", "close")
        connection.doOutput = true
        if (debug) {
            println("[Metrics] Prepared request for " + pluginName + " uncompressed=" + uncompressed.size + " compressed=" + compressed.size)
        }

        // Write the data
        val os = connection.getOutputStream()
        os.write(compressed)
        os.flush()

        // Now read the response
        val reader = BufferedReader(InputStreamReader(connection.getInputStream()))
        var response = reader.readLine()

        // close resources
        os.close()
        reader.close()
        if (response == null || response.startsWith("ERR") || response.startsWith("7")) {
            if (response == null) {
                response = "null"
            } else if (response.startsWith("7")) {
                response = response.substring(if (response.startsWith("7,")) 2 else 1)
            }
            throw IOException(response)
        } else {
            // Is this the first update this hour?
            if (response == "1" || response.contains("This is your first update this hour")) {
                synchronized(graphs) {
                    for (graph in graphs) {
                        for (plotter in graph.getPlotters()) {
                            plotter.reset()
                        }
                    }
                }
            }
        }
    }

    // Reload the metrics file
    val isOptOut: Boolean
        get() {
            synchronized(optOutLock) {
                try {
                    // Reload the metrics file
                    configuration.load(configFile)
                } catch (ex: IOException) {
                    if (debug) {
                        Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.message)
                    }
                    return true
                } catch (ex: InvalidConfigurationException) {
                    if (debug) {
                        Bukkit.getLogger().log(Level.INFO, "[Metrics] " + ex.message)
                    }
                    return true
                }
                return configuration.getBoolean("opt-out", false)
            }
        }

    // I believe the easiest way to get the base folder (e.g craftbukkit set via -P) for plugins to use
    // is to abuse the plugin object we already have
    // plugin.getDataFolder() => base/plugins/PluginA/
    // pluginsFolder => base/plugins/
    // The base is not necessarily relative to the startup directory.
    val configFile: File
        // return => base/plugins/PluginMetrics/config.yml
        get() {
            // I believe the easiest way to get the base folder (e.g craftbukkit set via -P) for plugins to use
            // is to abuse the plugin object we already have
            // plugin.getDataFolder() => base/plugins/PluginA/
            // pluginsFolder => base/plugins/
            // The base is not necessarily relative to the startup directory.
            val pluginsFolder = plugin.dataFolder.parentFile

            // return => base/plugins/PluginMetrics/config.yml
            return File(File(pluginsFolder, "PluginMetrics"), "config.yml")
        }
    private val isMineshafterPresent: Boolean
        private get() = try {
            Class.forName("mineshafter.MineServer")
            true
        } catch (e: Exception) {
            false
        }

    class Graph(val name: String) {
        private val plotters: MutableSet<Plotter> = LinkedHashSet()
        fun addPlotter(plotter: Plotter) {
            plotters.add(plotter)
        }

        fun removePlotter(plotter: Plotter) {
            plotters.remove(plotter)
        }

        fun onOptOut() {}
        fun getPlotters(): Set<Plotter> {
            return Collections.unmodifiableSet(plotters)
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` !is Graph) {
                return false
            }
            return `object`.name == name
        }
    }

    abstract class Plotter @JvmOverloads constructor(val columnName: String = "Default") {
        fun reset() {}
        abstract val value: Int
        override fun hashCode(): Int {
            return columnName.hashCode()
        }

        override fun equals(`object`: Any?): Boolean {
            if (`object` !is Plotter) {
                return false
            }
            val plotter = `object`
            return plotter.columnName == columnName && plotter.value == value
        }
    }

    companion object {
        private const val REVISION = 7
        private const val BASE_URL = "http://report.mcstats.org"
        private const val REPORT_URL = "/plugin/%s"
        private const val PING_INTERVAL = 10
        fun gzip(input: String): ByteArray {
            val baos = ByteArrayOutputStream()
            try {
                GZIPOutputStream(baos).use { gzos -> gzos.write(input.toByteArray(StandardCharsets.UTF_8)) }
            } catch (exception: IOException) {
                FunnyGuilds.Companion.getPluginLogger().error("MCStats error", exception)
            }
            return baos.toByteArray()
        }

        @Throws(UnsupportedEncodingException::class)
        private fun appendJSONPair(json: StringBuilder, key: String, value: String) {
            var isValueNumeric = false
            try {
                if (value == "0" || !value.endsWith("0")) {
                    value.toDouble()
                    isValueNumeric = true
                }
            } catch (e: NumberFormatException) {
                FunnyGuilds.Companion.getPluginLogger().debug("[MCStats] Value isn't numeric.")
            }
            if (json[json.length - 1] != '{') {
                json.append(',')
            }
            json.append(escapeJSON(key))
            json.append(':')
            if (isValueNumeric) {
                json.append(value)
            } else {
                json.append(escapeJSON(value))
            }
        }

        private fun escapeJSON(text: String): String {
            val builder = StringBuilder()
            builder.append('"')
            for (index in 0 until text.length) {
                val chr = text[index]
                when (chr) {
                    '"', '\\' -> {
                        builder.append('\\')
                        builder.append(chr)
                    }
                    '\b' -> builder.append("\\b")
                    '\t' -> builder.append("\\t")
                    '\n' -> builder.append("\\n")
                    '\r' -> builder.append("\\r")
                    else -> if (chr < ' ') {
                        val t = "000" + Integer.toHexString(chr.toInt())
                        builder.append("\\u").append(t.substring(t.length - 4))
                    } else {
                        builder.append(chr)
                    }
                }
            }
            builder.append('"')
            return builder.toString()
        }

        @Throws(UnsupportedEncodingException::class)
        private fun urlEncode(text: String): String {
            return URLEncoder.encode(text, "UTF-8")
        }
    }

    init {
        requireNotNull(plugin) { "Plugin cannot be null" }
        this.plugin = plugin

        // load the config
        configurationFile = configFile
        configuration = YamlConfiguration.loadConfiguration(configurationFile)

        // add some defaults
        configuration.addDefault("opt-out", false)
        configuration.addDefault("guid", UUID.randomUUID().toString())
        configuration.addDefault("debug", false)

        // Do we need to create the file?
        if (configuration["guid", null] == null) {
            configuration.options().header("http://mcstats.org").copyDefaults(true)
            configuration.save(configurationFile)
        }

        // Load the guid then
        guid = configuration.getString("guid")!!
        debug = configuration.getBoolean("debug", false)
    }
}