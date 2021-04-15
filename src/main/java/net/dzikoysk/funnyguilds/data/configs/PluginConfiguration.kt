package net.dzikoysk.funnyguilds.data.configs

import com.google.common.collect.ImmutableMap
import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.basic.guild.GuildRegex
import net.dzikoysk.funnyguilds.basic.rank.RankSystem
import net.dzikoysk.funnyguilds.basic.rank.RankUtils
import net.dzikoysk.funnyguilds.element.notification.NotificationStyle
import net.dzikoysk.funnyguilds.element.notification.bossbar.provider.BossBarOptions
import net.dzikoysk.funnyguilds.util.Cooldown
import net.dzikoysk.funnyguilds.util.IntegerRange
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemBuilder
import net.dzikoysk.funnyguilds.util.commons.bukkit.ItemUtils
import net.dzikoysk.funnyguilds.util.commons.bukkit.MaterialUtils
import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.diorite.cfg.annotations.*
import org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle
import org.diorite.cfg.annotations.CfgStringStyle.StringStyle
import org.diorite.cfg.annotations.defaults.CfgDelegateDefault
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

net.dzikoysk.funnyguilds.util.commons.*import org.apache.commons.lang3.tuple.Pairimport

org.bukkit.Materialimport org.bukkit.entity.EntityTypeimport org.bukkit.entity.Playerimport org.bukkit.inventory.ItemStackimport java.io.*import java.lang.Exception
import java.lang.StackTraceElementimport

java.util.*import java.util.concurrent.TimeUnit

@CfgClass(name = "PluginConfiguration")
@CfgDelegateDefault("{new}")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("                                #")
@CfgComment("          FunnyGuilds           #")
@CfgComment("         4.9.4 Tribute          #")
@CfgComment("                                #")
@CfgComment("~-~-~-~-~-~-~-~-~-~-~-~~-~-~-~~ #")
@CfgComment("FunnyGuilds wspiera PlaceholderAPI, lista dodawanych placeholderow znajduje sie tutaj:")
@CfgComment("https://www.spigotmc.org/wiki/placeholderapi-plugin-placeholders-page-2/#funnyguilds")
@CfgComment(" ")
@CfgComment("FunnyGuilds wspiera takze placeholdery w BungeeTabListPlus i MVdWPlaceholderAPI")
@CfgComment("Placeholdery sa dokladnie takie same jak w przypadku PlaceholderAPI (bez znaku % oczywiscie)")
@CfgComment(" ")
@CfgComment("Jezeli chcesz, aby dana wiadomosc byla pusta, zamiast wiadomosci umiesc: ''")
@CfgComment(" ")
class PluginConfiguration {
    @CfgExclude
    val informationMessageCooldowns = Cooldown<Player?>()

    @CfgExclude
    var dateFormat: SimpleDateFormat? = null

    @CfgComment("Wyswietlana nazwa pluginu")
    @CfgName("plugin-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var pluginName = "FunnyGuilds"

    @CfgComment("Czy plugin ma dzialac w trybie debug. Sluzy on do wysylania dodatkowych wiadomosci w celu zdiagnozowania bledow itp.")
    @CfgName("debug-mode")
    var debugMode = false

    @CfgComment("Czy informacje o aktualizacji maja byc widoczne podczas wejscia na serwer")
    @CfgName("update-info")
    var updateInfo = true

    @CfgComment("Czy informacje o aktualizacji wersji nightly maja byc widoczne podczas wejscia na serwer")
    @CfgComment("Ta opcja działa tylko wtedy, gdy także jest włączona opcja 'update-info'")
    @CfgName("update-nightly-info")
    var updateNightlyInfo = true

    @CfgComment("Mozliwosc zakladania gildii. Mozna ja zmienic takze za pomoca komendy /ga enabled")
    @CfgName("guilds-enabled")
    var guildsEnabled = true

    @CfgComment("Czy tworzenie regionow gildii, oraz inne zwiazane z nimi rzeczy, maja byc wlaczone")
    @CfgComment("UWAGA - dobrze przemysl decyzje o wylaczeniu regionow!")
    @CfgComment("Gildie nie beda mialy w sobie zadnych informacji o regionach, a jesli regiony sa wlaczone - te informacje musza byc obecne")
    @CfgComment("Jesli regiony mialyby byc znowu wlaczone - bedzie trzeba wykasowac WSZYSTKIE dane pluginu")
    @CfgComment("Wylaczenie tej opcji nie powinno spowodowac zadnych bledow, jesli juz sa utworzone regiony gildii")
    @CfgName("regions-enabled")
    var regionsEnabled = true

    @CfgComment("Zablokuj rozlewanie się wody i lawy poza terenem gildii")
    @CfgComment("Dziala tylko jesli regiony sa wlaczone")
    @CfgName("water-and-lava-flow-only-for-regions")
    var blockFlow = false

    @CfgComment("Czy gracz po smierci ma sie pojawiac w bazie swojej gildii")
    @CfgComment("Dziala tylko jesli regiony sa wlaczone")
    @CfgName("respawn-in-base")
    var respawnInBase = true

    @CfgComment("Maksymalna dlugosc nazwy gildii")
    @CfgName("name-length")
    var createNameLength = 22

    @CfgComment("Minimalna dlugosc nazwy gildii")
    @CfgName("name-min-length")
    var createNameMinLength = 4

    @CfgComment("Maksymalna dlugosc tagu gildii")
    @CfgName("tag-length")
    var createTagLength = 4

    @CfgComment("Minimalna dlugosc tagu gildii")
    @CfgName("tag-min-length")
    var createTagMinLength = 2

    @CfgComment("Zasada sprawdzania nazwy gildii przy jej tworzeniu")
    @CfgComment("Dostepne zasady:")
    @CfgComment("LOWERCASE - umozliwia uzycie tylko malych liter")
    @CfgComment("UPPERCASE - umozliwia uzycie tylko duzych liter")
    @CfgComment("DIGITS - umozliwia uzycie tylko cyfr")
    @CfgComment("LOWERCASE_DIGITS - umozliwia uzycie malych liter i cyfr")
    @CfgComment("UPPERCASE_DIGITS - umozliwia uzycie duzych liter i cyfr")
    @CfgComment("LETTERS - umozliwia uzycie malych i duzych liter")
    @CfgComment("LETTERS_DIGITS - umozliwia uzycie malych i duzych liter oraz cyrf")
    @CfgComment("LETTERS_DIGITS_UNDERSCORE - umozliwia uzycie malych i duzych liter, cyrf oraz podkreslnika")
    @CfgName("name-regex")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var nameRegex_ = "LETTERS"

    @CfgExclude
    var nameRegex: GuildRegex? = null

    @CfgComment("Zasada sprawdzania tagu gildii przy jej tworzeniu")
    @CfgComment("Mozliwe zasady sa takie same jak w przypadku name-regex")
    @CfgName("tag-regex")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var tagRegex_ = "LETTERS"

    @CfgExclude
    var tagRegex: GuildRegex? = null

    @CfgComment("Minimalna liczba graczy w gildii, aby zaliczala sie ona do rankingu")
    @CfgName("guild-min-members")
    var minMembersToInclude = 1

    @CfgComment("Czy wiadomosci o braku potrzebnych przedmiotow maja zawierac elementy, na ktore mozna najechac")
    @CfgComment("Takie elementy pokazuja informacje o przedmiocie, np. jego typ, nazwe czy opis")
    @CfgComment("Funkcja jest obecnie troche niedopracowana i moze powodowac problemy na niektorych wersjach MC, np. 1.8.8")
    @CfgName("enable-item-component")
    var enableItemComponent = false

    @CfgComment("Przedmioty wymagane do zalozenia gildii")
    @CfgComment("Tylko wartosci ujete w <> sa wymagane, reszta, ujeta w [], jest opcjonalna")
    @CfgComment("Wzor: <ilosc> <przedmiot>:[metadata] [name:lore:enchant:eggtype:skullowner:armorcolor:flags]")
    @CfgComment("Przyklad: \"5 stone name:&bFunnyGuilds lore:&eJestem_najlepszym#&6pluginem!\"")
    @CfgComment("")
    @CfgComment("Zamiast spacji wstawiaj podkreslnik: _")
    @CfgComment("Aby zrobic nowa linie lore wstaw hash: #")
    @CfgComment("Aby w lore uzyc znaku # wstaw {HASH}")
    @CfgComment("")
    @CfgComment("eggtype to typ jajka do spawnu moba, uzywane tylko gdy typem przedmiotu jest MONSTER_EGG")
    @CfgComment("skullowner to nick gracza, ktorego glowa jest tworzona, uzywane tylko gdy typem przedmiotu jest SKULL_ITEM")
    @CfgComment("armorcolor to kolor, w ktorym bedzie przedmiot, uzywane tylko gdy przedmiot jest czescia zbroi skorzanej")
    @CfgComment("flags to flagi, ktore maja byc nalozone na przedmiot. Dostepne flagi: HIDE_ENCHANTS, HIDE_ATTRIBUTES, HIDE_UNBREAKABLE, HIDE_DESTROYS, HIDE_PLACED_ON, HIDE_POTION_EFFECTS")
    @CfgComment("Kolor musi byc podany w postaci: \"R_G_B\"")
    @CfgComment("")
    @CfgComment("UWAGA: Nazwy przedmiotow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgComment("UWAGA: Typ jajka musi pasowac do typow entity podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @CfgName("items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var items_ = Arrays.asList("5 stone", "5 dirt", "5 tnt")

    @CfgExclude
    var createItems: List<ItemStack?>? = null

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii")
    @CfgName("required-experience")
    var requiredExperience = 0

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money")
    var requiredMoney = 0.0

    @CfgComment("Przedmioty wymagane do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("items-vip")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var itemsVip_ = listOf("1 gold_ingot")

    @CfgExclude
    var createItemsVip: List<ItemStack?>? = null

    @CfgComment("Wymagana ilosc doswiadczenia do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgName("required-experience-vip")
    var requiredExperienceVip = 0

    @CfgComment("Wymagana ilosc pieniedzy do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.items")
    @CfgComment("UWAGA: Aby ta opcja mogla dzialac, na serwerze musi byc plugin Vault oraz plugin dodajacy ekonomie")
    @CfgName("required-money-vip")
    var requiredMoneyVip = 0.0

    @CfgComment("Czy opcja wymaganego rankingu do zalozenia gildii ma byc wlaczona?")
    @CfgName("rank-create-enable")
    var rankCreateEnable = true

    @CfgComment("Minimalny ranking wymagany do zalozenia gildii")
    @CfgName("rank-create")
    var rankCreate = 1000

    @CfgComment("Minimalny ranking wymagany do zalozenia gildii dla osoby z uprawnieniem funnyguilds.vip.rank")
    @CfgName("rank-create-vip")
    var rankCreateVip = 800

    @CfgComment("Czy GUI z przedmiotami na gildie ma byc wspolne dla wszystkich?")
    @CfgComment("Jesli wlaczone - wszyscy gracze beda widzieli GUI stworzone w sekcji gui-items, a GUI z sekcji gui-items-vip bedzie ignorowane")
    @CfgName("use-common-gui")
    var useCommonGUI = false

    @CfgComment("GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Jesli wlaczone jest use-common-gui - ponizsze GUI jest uzywane takze dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Kazda linijka listy oznacza jeden slot, liczba slotow powinna byc wielokrotnoscia liczby 9 i nie powinna byc wieksza niz 54")
    @CfgComment("Aby uzyc przedmiotu stworzonego w jednym slocie w innym mozna uzyc {GUI-nr}, np. {GUI-1} wstawi ten sam przedmiot, ktory jest w pierwszym slocie")
    @CfgComment("Aby wstawic przedmiot na gildie nalezy uzyc {ITEM-nr}, np. {ITEM-1} wstawi pierwszy przedmiot na gildie")
    @CfgComment("Aby wstawic przedmiot na gildie z listy vip nalezy uzyc {VIPITEM-nr}")
    @CfgName("gui-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var guiItems_ = Arrays.asList(
        "1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
        "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{ITEM-1}", "{ITEM-2}", "{ITEM-3}", "{GUI-1}",
        "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}"
    )

    @CfgExclude
    var guiItems: List<ItemStack?>? = null

    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob bez uprawnienia funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki, wliczajac w to kody kolorow")
    @CfgName("gui-items-title")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guiItemsTitle_ = "&5&lPrzedmioty na gildie"

    @CfgExclude
    var guiItemsTitle: String? = null

    @CfgComment("GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Zasada tworzenia GUI jest taka sama jak w przypadku sekcji gui-items")
    @CfgComment("Ponizsze GUI bedzie ignorowane jesli wlaczone jest use-common-gui")
    @CfgName("gui-items-vip")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var guiItemsVip_ = Arrays.asList(
        "1 glass name:&r", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}",
        "{GUI-1}", "{GUI-1}", "{GUI-1}", "1 paper name:&b&lItemy_na_gildie", "{GUI-1}", "{GUI-1}", "{VIPITEM-1}", "{GUI-3}", "{GUI-1}",
        "{GUI-11}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}", "{GUI-1}"
    )

    @CfgExclude
    var guiItemsVip: List<ItemStack?>? = null

    @CfgComment("Nazwa GUI z przedmiotami na gildie dla osob z uprawnieniem funnyguilds.vip.items")
    @CfgComment("Nazwa moze zawierac max. 32 znaki, wliczajac w to kody kolorow")
    @CfgName("gui-items-vip-title")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guiItemsVipTitle_ = "&5&lPrzedmioty na gildie (VIP)"

    @CfgExclude
    var guiItemsVipTitle: String? = null

    @CfgComment("Zmiana nazwy i koloru przedmiotow na gildie (nie ma znaczenia uprawnienie funnyguilds.vip.items)")
    @CfgComment("Jesli nie chcesz uzywać tej funkcji, to pozostaw gui-items-name: \"\"")
    @CfgComment("{ITEM} - nazwa przedmiotu (np. 1 golden_apple)")
    @CfgComment("{ITEM-NO-AMOUNT} - nazwa przedmiotu bez liczby. (np. golden_apple)")
    @CfgName("gui-items-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guiItemsName_ = "&7>> &a{ITEM-NO-AMOUNT} &7<<"

    @CfgExclude
    var guiItemsName: String? = null

    @CfgComment("Czy do przedmiotow na gildie, ktore sa w GUI, maja byc dodawane dodatkowe linie opisu?")
    @CfgComment("Linie te mozna ustawic ponizej")
    @CfgName("add-lore-lines")
    var addLoreLines = true

    @CfgComment("Dodatkowe linie opisu, dodawane do kazdego przedmiotu, ktory jest jednoczesnie przedmiotem na gildie")
    @CfgComment("Dodawane linie nie zaleza od otwieranego GUI - sa wspolne dla zwyklego i VIP")
    @CfgComment("Mozliwe do uzycia zmienne:")
    @CfgComment("{REQ-AMOUNT} - calkowita wymagana ilosc przedmiotu")
    @CfgComment("{PINV-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie")
    @CfgComment("{PINV-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie")
    @CfgComment("{EC-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma w enderchescie")
    @CfgComment("{EC-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma w enderchescie")
    @CfgComment("{ALL-AMOUNT} - ilosc danego przedmiotu, jaka gracz ma przy sobie i w enderchescie")
    @CfgComment("{ALL-PERCENT} - procent wymaganej ilosci danego przedmiotu, jaki gracz ma przy sobie i w enderchescie")
    @CfgName("gui-items-lore")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var guiItemsLore_ = Arrays.asList(
        "", "&aPosiadzasz juz:", "&a{PINV-AMOUNT} przy sobie &7({PINV-PERCENT}%)",
        "&a{EC-AMOUNT} w enderchescie &7({EC-PERCENT}%)", "&a{ALL-AMOUNT} calkowicie &7({ALL-PERCENT}%)"
    )

    @CfgExclude
    var guiItemsLore: List<String?>? = null

    @CfgComment("Minimalna odleglosc od spawnu")
    @CfgName("create-distance")
    var createDistance = 100

    @CfgComment("Minimalna odleglosc od granicy mapy, na ktorej znajduje sie gracz")
    @CfgName("create-guild-min-distance")
    var createMinDistanceFromBorder = 50.0

    @CfgComment("Blok lub entity, ktore jest sercem gildii")
    @CfgComment("Zmiana entity wymaga pelnego restartu serwera")
    @CfgComment("Bloki musza byc podawane w formacie - material:metadata")
    @CfgComment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgComment("Typ entity musi byc zgodny z ta lista (i zdrowym rozsadkiem) - https://spigotdocs.okaeri.eu/select/org/bukkit/entity/EntityType.html")
    @CfgComment("UWAGA: Zmiana bloku, gdy sa juz zrobione jakies gildie, spowoduje niedzialanie ich regionow")
    @CfgComment(" ")
    @CfgComment("UWAGA: Jesli jako serca gildii chcesz uzyc bloku, ktory spada pod wplywem grawitacji - upewnij sie, ze bedzie on stal na jakims bloku!")
    @CfgComment("Jesli pojawi sie w powietrzu - spadnie i plugin nie bedzie odczytywal go poprawnie!")
    @CfgName("create-type")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var createType = "ender_crystal"

    @CfgExclude
    var createMaterial: Pair<Material?, Byte?>? = null

    @CfgExclude
    var createEntityType: EntityType? = null

    @CfgComment("Na jakim poziomie ma byc wyznaczone centrum gildii")
    @CfgComment("Wpisz 0 jesli ma byc ustalone przez pozycje gracza")
    @CfgName("create-center-y")
    var createCenterY = 60

    @CfgComment("Czy ma sie tworzyc kula z obsydianu dookola centrum gildii")
    @CfgName("create-center-sphere")
    var createCenterSphere = true

    @CfgComment("Czy przy tworzeniu gildii powinien byc wklejany schemat")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-on-creation")
    var pasteSchematicOnCreation = false

    @CfgComment("Nazwa pliku ze schematem poczatkowym gildii")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgComment("Schemat musi znajdować się w folderze FunnyGuilds")
    @CfgName("guild-schematic-file-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guildSchematicFileName: String? = "funnyguilds.schematic"

    @CfgComment("Czy schemat przy tworzeniu gildii powinien byc wklejany razem z powietrzem?")
    @CfgComment("Przy duzych schematach ma to wplyw na wydajnosc")
    @CfgComment("Wklejenie schematu wymaga pluginu WorldEdit")
    @CfgName("paste-schematic-with-air")
    var pasteSchematicWithAir = true

    @CfgExclude
    var guildSchematicFile: File? = null

    @CfgComment("Typy blokow, z ktorymi osoba spoza gildii NIE moze prowadzic interakcji na terenie innej gildii")
    @CfgName("blocked-interact")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var _blockedInteract = Arrays.asList("CHEST", "TRAPPED_CHEST")

    @CfgExclude
    var blockedInteract: MutableSet<Material?>? = null

    @CfgComment("Czy funkcja efektu 'zbugowanych' klockow ma byc wlaczona (dziala tylko na terenie wrogiej gildii)")
    @CfgName("bugged-blocks")
    var buggedBlocks = false

    @CfgComment("Czas po ktorym 'zbugowane' klocki maja zostac usuniete")
    @CfgComment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @CfgName("bugged-blocks-timer")
    var buggedBlocksTimer = 20L

    @CfgComment("Bloki, ktorych nie mozna 'bugowac'")
    @CfgComment("Nazwy blokow musza pasowac do nazw podanych tutaj: https://spigotdocs.okaeri.eu/select/org/bukkit/Material.html")
    @CfgName("bugged-blocks-exclude")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var buggedBlocksExclude_ = Arrays.asList( // Ban basic
        "TNT", "STATIONARY_LAVA", "STATIONARY_WATER",  // Ban TNT Minecart placement
        "RAILS", "DETECTOR_RAIL", "ACTIVATOR_RAIL", "POWERED_RAIL",  // Ban gravity blocks that won't be removed when fallen
        "ANVIL", "GRAVEL", "SAND", "DRAGON_EGG",  // Ban pistons and other components that may produce redstone output or interact with it
        "PISTON_BASE", "PISTON_STICKY_BASE",
        "REDSTONE_BLOCK", "REDSTONE_TORCH_ON", "REDSTONE_TORCH_OFF", "DIODE", "REDSTONE_COMPARATOR", "DAYLIGHT_DETECTOR",
        "DISPENSER", "HOPPER", "DROPPER", "OBSERVER",
        "STONE_PLATE", "WOOD_PLATE", "GOLD_PLATE", "IRON_PLATE", "LEVER", "TRIPWIRE_HOOK", "TRAP_DOOR", "IRON_TRAPDOOR", "WOOD_BUTTON", "STONE_BUTTON",
        "WOOD_DOOR", "IRON_DOOR", "SPRUCE_DOOR_ITEM", "BIRCH_DOOR_ITEM", "JUNGLE_DOOR_ITEM", "ACACIA_DOOR_ITEM", "DARK_OAK_DOOR_ITEM",
        "FENCE_GATE", "SPRUCE_FENCE_GATE", "JUNGLE_FENCE_GATE", "DARK_OAK_FENCE_GATE", "BIRCH_FENCE_GATE",
        "REDSTONE_LAMP_ON", "REDSTONE_LAMP_OFF",
        "TRAPPED_CHEST", "CHEST"
    )

    @CfgExclude
    var buggedBlocksExclude: MutableSet<Material?>? = null

    @CfgComment("Czy klocki po 'zbugowaniu' maja zostac oddane")
    @CfgName("bugged-blocks-return")
    var buggedBlockReturn = false

    @CfgComment("Maksymalna liczba czlonkow w gildii")
    @CfgName("max-members")
    var maxMembersInGuild = 15

    @CfgComment("Maksymalna liczba sojuszy miedzy gildiami")
    @CfgName("max-allies")
    var maxAlliesBetweenGuilds = 15

    @CfgComment("Maksymalna liczba wojen miedzy gildiami")
    @CfgName("max-enemies")
    var maxEnemiesBetweenGuilds = 15

    @CfgComment("Lista nazw swiatow, na ktorych mozliwosc utworzenia gildii powinna byc zablokowana")
    @CfgName("blocked-worlds")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var blockedWorlds = listOf("some_world")

    @CfgComment("Mozliwosc ucieczki z terenu innej gildii")
    @CfgComment("Funkcja niedostepna jesli mozliwosc teleportacji do gildii jest wylaczona")
    @CfgName("escape-enable")
    var escapeEnable = true

    @CfgComment("Czas, w sekundach, jaki musi uplynac od wlaczenia ucieczki do teleportacji")
    @CfgName("escape-delay")
    var escapeDelay = 120

    @CfgComment("Mozliwosc ucieczki na spawn dla graczy bez gildii")
    @CfgName("escape-spawn")
    var escapeSpawn = true

    @CfgComment("Mozliwosc teleportacji do gildii")
    @CfgName("base-enable")
    var baseEnable = true

    @CfgComment("Czas oczekiwania na teleportacje, w sekundach")
    @CfgName("base-delay")
    var baseDelay = 5

    @CfgComment("Czas oczekiwania na teleportacje, w sekundach, dla graczy posiadajacych uprawnienie funnyguilds.vip.baseTeleportTime")
    @CfgName("base-delay-vip")
    var baseDelayVip = 3

    @CfgComment("Koszt teleportacji do gildii. Jezeli teleportacja ma byc darmowa, wystarczy wpisac: base-items: []")
    @CfgName("base-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var baseItems_ = Arrays.asList("1 diamond", "1 emerald")

    @CfgExclude
    var baseItems: List<ItemStack?>? = null

    @CfgComment("Koszt dolaczenia do gildii. Jezeli dolaczenie ma byc darmowe, wystarczy wpisac: join-items: []")
    @CfgName("join-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var joinItems_ = listOf("1 diamond")

    @CfgExclude
    var joinItems: List<ItemStack?>? = null

    @CfgComment("Mozliwosc powiekszania gildii")
    @CfgName("enlarge-enable")
    var enlargeEnable = true

    @CfgComment("O ile powieksza teren gildii 1 poziom")
    @CfgName("enlarge-size")
    var enlargeSize = 5

    @CfgComment("Koszt powiekszania gildii")
    @CfgComment("- kazdy myslnik, to 1 poziom gildii")
    @CfgName("enlarge-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var enlargeItems_ = Arrays.asList("8 diamond", "16 diamond", "24 diamond", "32 diamond", "40 diamond", "48 diamond", "56 diamond", "64 diamond", "72 diamond", "80 diamond")

    @CfgExclude
    var enlargeItems: List<ItemStack?>? = null

    @CfgComment("Wielkosc regionu gildii")
    @CfgName("region-size")
    var regionSize = 50

    @CfgComment("Minimalna odleglosc miedzy terenami gildii")
    @CfgName("region-min-distance")
    var regionMinDistance = 10

    @CfgComment("Czas wyswietlania powiadomienia na pasku powiadomien, w sekundach")
    @CfgName("region-notification-time")
    var regionNotificationTime = 15

    @CfgComment("Co ile moze byc wywolywany pasek powiadomien przez jednego gracza, w sekundach")
    @CfgName("region-notification-cooldown")
    var regionNotificationCooldown = 60

    @CfgComment("Blokowane komendy dla graczy spoza gildii na jej terenie")
    @CfgName("region-commands")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var regionCommands = listOf("sethome")

    @CfgComment("Czy proces usuniecia gildii powinien zostac przerwany jezeli ktos spoza gildii jest na jej terenie")
    @CfgName("guild-delete-cancel-if-someone-is-on-region")
    var guildDeleteCancelIfSomeoneIsOnRegion = false

    @CfgComment("Czy wlaczyc ochrone przed TNT w gildiach w podanych godzinach")
    @CfgName("guild-tnt-protection-enabled")
    var guildTNTProtectionEnabled = false

    @CfgComment("Czy wlaczyc ochrone przed TNT na całym serwerze w podanych godzinach")
    @CfgName("guild-tnt-protection-global")
    var guildTNTProtectionGlobal = false

    @CfgComment("O której godzinie ma sie zaczac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-start-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guildTNTProtectionStartTime_ = "22:00"

    @CfgExclude
    var guildTNTProtectionStartTime: LocalTime? = null

    @CfgComment("Do której godziny ma dzialac ochrona przed TNT w gildii")
    @CfgComment("Godzina w formacie HH:mm")
    @CfgName("guild-tnt-protection-end-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var guildTNTProtectionEndTime_ = "06:00"

    @CfgExclude
    var guildTNTProtectionEndTime: LocalTime? = null

    @CfgExclude
    var guildTNTProtectionPassingMidnight = false

    @CfgComment("Przez ile sekund nie mozna budowac na terenie gildii po wybuchu")
    @CfgName("region-explode")
    var regionExplode = 120

    @CfgComment("Czy blokada po wybuchu ma obejmowac rowniez budowanie")
    @CfgName("region-explode-block-breaking")
    var regionExplodeBlockBreaking = false

    @CfgComment("Czy blokada po wybuchu ma obejmowac rowniez interakcje z blocked-interact")
    @CfgName("region-explode-block-interactions")
    var regionExplodeBlockInteractions = false

    @CfgComment("Zasieg pobieranych przedmiotow po wybuchu. Jezeli chcesz wylaczyc, wpisz 0")
    @CfgName("explode-radius")
    var explodeRadius = 3

    @CfgComment("Jakie materialy i z jaka szansa maja byc niszczone po wybuchu")
    @CfgComment("<material>: <szansa (w %)")
    @CfgComment("Jeżeli wszystkie materialy maja miec okreslony % na wybuch, uzyj specjalnego znaku '*'")
    @CfgName("explode-materials")
    var explodeMaterials_: Map<String, Double> = ImmutableMap.of(
        "ender_chest", 20.0,
        "enchantment_table", 20.0,
        "obsidian", 20.0,
        "water", 33.0,
        "lava", 33.0
    )

    @CfgExclude
    var explodeMaterials: Map<Material, Double>? = null

    @CfgExclude
    var allMaterialsAreExplosive = false

    @CfgExclude
    var defaultExplodeChance = -1.0

    @CfgComment("Czy powstale wybuchy powinny niszczyc bloki wylacznie na terenach gildii")
    @CfgName("explode-should-affect-only-guild")
    var explodeShouldAffectOnlyGuild = false

    @CfgComment("Możliwość podbijania gildii")
    @CfgName("war-enabled")
    var warEnabled = true

    @CfgComment("Ile zyc ma gildia")
    @CfgName("war-lives")
    var warLives = 3

    @CfgComment("Po jakim czasie od zalozenia mozna zaatakowac gildie")
    @CfgName("war-protection")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var warProtection_ = "24h"

    @CfgExclude
    var warProtection: Long = 0

    @CfgComment("Ile czasu trzeba czekac do nastepnego ataku na gildie")
    @CfgName("war-wait")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var warWait_ = "24h"

    @CfgExclude
    var warWait: Long = 0

    @CfgComment("Czy gildia podczas okresu ochronnego ma posiadac ochrone przeciw TNT")
    @CfgName("war-tnt-protection")
    var warTntProtection = true

    @CfgComment("Czy zwierzeta na terenie gildii maja byc chronione przed osobami spoza gildii")
    @CfgName("animals-protection")
    var animalsProtection = false

    @CfgComment("Jaka waznosc ma gildia po jej zalozeniu")
    @CfgName("validity-start")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var validityStart_ = "14d"

    @CfgExclude
    var validityStart: Long = 0

    @CfgComment("Ile czasu dodaje przedluzenie gildii")
    @CfgName("validity-time")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var validityTime_ = "14d"

    @CfgExclude
    var validityTime: Long = 0

    @CfgComment("Ile dni przed koncem wygasania mozna przedluzyc gildie. Wpisz 0, jezeli funkcja ma byc wylaczona")
    @CfgName("validity-when")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var validityWhen_ = "14d"

    @CfgExclude
    var validityWhen: Long = 0

    @CfgComment("Koszt przedluzenia gildii")
    @CfgName("validity-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var validityItems_ = listOf("10 diamond")

    @CfgExclude
    var validityItems: List<ItemStack?>? = null

    @CfgComment("Czy wiadomosc o zabiciu gracza powinna byc pokazywana wszystkim")
    @CfgComment("Jesli wylaczone - bedzie pokazywana tylko graczom, ktorzy brali udzial w walce")
    @CfgName("broadcast-death-message")
    var broadcastDeathMessage = true

    @CfgComment("Czy wiadomosc o zabiciu gracza powinna byc wyswietlana bez wzgledu na wylaczone wiadomosci o smierci")
    @CfgName("ignore-death-messages-disabled")
    var ignoreDisabledDeathMessages = false

    @CfgComment("Ranking od ktorego rozpoczyna gracz")
    @CfgName("rank-start")
    var rankStart = 1000

    @CfgComment("Czy blokada nabijania rankingu na tych samych osobach powinna byc wlaczona")
    @CfgName("rank-farming-protect")
    var rankFarmingProtect = true

    @CfgComment("Czy ostatni gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CfgName("rank-farming-last-attacker-as-killer")
    var considerLastAttackerAsKiller = false

    @CfgComment("Przez ile sekund gracz, ktory zaatakowal gracza, ktory zginal ma byc uznawany jako zabojca")
    @CfgName("rank-farming-consideration-timeout")
    var lastAttackerAsKillerConsiderationTimeout = 30

    @CfgExclude
    var lastAttackerAsKillerConsiderationTimeout_: Long = 0

    @CfgComment("Czas w sekundach blokady nabijania rankingu po walce dwoch osob")
    @CfgName("rank-farming-cooldown")
    var rankFarmingCooldown = 7200

    @CfgComment("Czy ma byc zablokowana zmiana rankingu, jesli obie osoby z walki maja taki sam adres IP")
    @CfgName("rank-ip-protect")
    var rankIPProtect = false

    @CfgComment("Czy gracze z uprawnieniem 'funnyguilds.ranking.exempt' powinni byc uwzglednieni przy wyznaczaniu pozycji gracza w rankingu")
    @CfgName("skip-privileged-players-in-rank-positions")
    var skipPrivilegedPlayersInRankPositions = false

    @CfgComment("Co ile ticków ranking graczy oraz gildii powinien zostać odświeżony")
    @CfgName("ranking-update-interval")
    var rankingUpdateInterval = 40

    @CfgExclude
    var rankingUpdateInterval_: Long = 0

    @CfgComment("Czy system asyst ma byc wlaczony")
    @CfgName("rank-assist-enable")
    var assistEnable = true

    @CfgComment("Limit asyst. Wpisz liczbe ujemna aby wylaczyc")
    @CfgName("assists-limit")
    var assistsLimit = -1

    @CfgComment("Jaka czesc rankingu za zabicie idzie na konto zabojcy")
    @CfgComment("1 to caly ranking, 0 to nic")
    @CfgComment("Reszta rankingu rozdzielana jest miedzy osoby asystujace w zaleznosci od zadanych obrazen")
    @CfgName("rank-assist-killer-share")
    var assistKillerShare = 0.5

    @CfgComment("Na jakich regionach ma ignorowac nadawanie asyst")
    @CfgComment("UWAGA: Wymagany plugin WorldGuard")
    @CfgName("assists-regions-ignored")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var assistsRegionsIgnored = listOf("spawnGuildHeart")

    @CfgComment("System rankingowy uzywany przez plugin, do wyboru:")
    @CfgComment("ELO - system bazujacy na rankingu szachowym ELO, najlepiej zbalansowany ze wszystkich trzech")
    @CfgComment("PERCENT - system, ktory obu graczom zabiera procent rankingu osoby zabitej")
    @CfgComment("STATIC - system, ktory zawsze zabiera iles rankingu zabijajacemu i iles zabitemu")
    @CfgName("rank-system")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var rankSystem_ = "ELO"

    @CfgExclude
    var rankSystem: RankSystem? = null

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Lista stalych obliczen rankingowych ELO, uzywanych przy zmianach rankingu - im mniejsza stala, tym mniejsze zmiany rankingu")
    @CfgComment("Stale okreslaja tez o ile maksymalnie moze zmienic sie ranking pochodzacy z danego przedzialu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank stala\", np.: \"0-1999 32\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"2401-* 16\"")
    @CfgName("elo-constants")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var eloConstants_ = Arrays.asList("0-1999 32", "2000-2400 24", "2401-* 16")

    @CfgExclude
    var eloConstants: Map<IntegerRange, Int>? = null

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Dzielnik obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Dzielnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-divider")
    var eloDivider = 400.0

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest ELO!")
    @CfgComment("Wykladnik potegi obliczen rankingowych ELO - im mniejszy, tym wieksze zmiany rankingu")
    @CfgComment("Wykladnik powinien byc liczba dodatnia, niezerowa")
    @CfgName("elo-exponent")
    var eloExponent = 10.0

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest PERCENT!")
    @CfgComment("Procent rankingu osoby zabitej o jaki zmienia sie rankingi po walce")
    @CfgName("percent-rank-change")
    var percentRankChange = 1.0

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty dawane osobie, ktora wygrywa walke")
    @CfgName("static-attacker-change")
    var staticAttackerChange = 15

    @CfgComment("Sekcja uzywana TYLKO jesli wybranym rank-system jest STATIC!")
    @CfgComment("Punkty zabierane osobie, ktora przegrywa walke")
    @CfgName("static-victim-change")
    var staticVictimChange = 10

    @CfgComment("Czy pokazywac informacje przy kliknieciu PPM na gracza")
    @CfgName("info-player-enabled")
    var infoPlayerEnabled = true

    @CfgComment("Czy pokazac informacje z komendy /gracz przy kliknieciu PPM")
    @CfgComment("Jesli wylaczone - pokazywane beda informacje z sekcji \"playerRightClickInfo\" z messages.yml")
    @CfgName("info-player-command")
    var infoPlayerCommand = true

    @CfgComment("Cooldown pomiedzy pokazywaniem informacji przez PPM (w sekundach)")
    @CfgName("info-player-cooldown")
    var infoPlayerCooldown = 5

    @CfgComment("Czy trzeba kucac, zeby przy klikniecu PPM na gracza wyswietlilo informacje o nim")
    @CfgName("info-player-sneaking")
    var infoPlayerSneaking = true

    @CfgComment("Czy czlonkowie gildii moga sobie zadawac obrazenia (domyslnie)")
    @CfgName("damage-guild")
    var damageGuild = false

    @CfgComment("Czy sojuszniczy moga sobie zadawac obrazenia")
    @CfgName("damage-ally")
    var damageAlly = false

    @CfgComment("Wyglad znaczika {POS} wstawionego w format chatu")
    @CfgComment("Znacznik ten pokazuje czy ktos jest liderem, zastepca czy zwyklym czlonkiem gildii")
    @CfgName("chat-position")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPosition_ = "&b{POS} "

    @CfgExclude
    var chatPosition: String? = null

    @CfgComment("Znacznik dla lidera gildii")
    @CfgName("chat-position-leader")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPositionLeader = "**"

    @CfgComment("Znacznik dla zastepcy gildii")
    @CfgName("chat-position-deputy")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPositionDeputy = "*"

    @CfgComment("Znacznik dla czlonka gildii")
    @CfgName("chat-position-member")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPositionMember = ""

    @CfgComment("Wyglad znacznika {TAG} wstawionego w format chatu")
    @CfgName("chat-guild")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatGuild_ = "&b{TAG} "

    @CfgExclude
    var chatGuild: String? = null

    @CfgComment("Wyglad znacznika {RANK} wstawionego w format chatu")
    @CfgName("chat-rank")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatRank_ = "&b{RANK} "

    @CfgExclude
    var chatRank: String? = null

    @CfgComment("Wyglad znacznika {POINTS} wstawionego w format chatu")
    @CfgComment("Mozesz tu takze uzyc znacznika {POINTS-FORMAT}")
    @CfgName("chat-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPoints_ = "&b{POINTS} "

    @CfgExclude
    var chatPoints: String? = null

    @CfgComment("Wyglad znacznika {POINTS-FORMAT} i {G-POINTS-FORMAT} w zaleznosci od wartosci punktow")
    @CfgComment("{G-POINTS-FORMAT}, tak samo jak {G-POINTS} jest uzywane jedynie na liscie graczy")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych rankingow i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minRank-maxRank wyglad\", np.: \"0-750 &4{POINTS}\"")
    @CfgComment("Pamietaj, aby kazdy mozliwy ranking mial ustalony format!")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minRank w gore, np.: \"1500-* &6&l{POINTS}\"")
    @CfgName("points-format")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var pointsFormat_ = Arrays.asList("0-749 &4{POINTS}", "750-999 &c{POINTS}", "1000-1499 &a{POINTS}", "1500-* &6&l{POINTS}")

    @CfgExclude
    var pointsFormat: Map<IntegerRange, String?>? = null

    @CfgComment("Znacznik z punktami dodawany do zmiennej {PTOP-x} i {ONLINE-PTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam nick - nie podawaj tu nic")
    @CfgName("ptop-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var ptopPoints_ = " &7[{POINTS}&7]"

    @CfgExclude
    var ptopPoints: String? = null

    @CfgComment("Znacznik z punktami dodawany do zmiennej {GTOP-x}")
    @CfgComment("Uzywaj zmiennych {POINTS} i {POINTS-FORMAT}")
    @CfgComment("Jesli nie chcesz wyswietlac punktow, tylko sam tag - nie podawaj tu nic")
    @CfgName("gtop-points")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var gtopPoints_ = " &7[&b{POINTS-FORMAT}&7]"

    @CfgExclude
    var gtopPoints: String? = null

    @CfgComment("Wyglad znacznika {PING-FORMAT} w zaleznosci od wartosci pingu")
    @CfgComment("Lista powinna byc podana od najmniejszych do najwiekszych wartosci i zawierac tylko liczby naturalne, z zerem wlacznie")
    @CfgComment("Elementy listy powinny byc postaci: \"minPing-maxPing wyglad\", np.: \"0-75 &a{PING}\"")
    @CfgComment("* uzyta w zapisie elementu listy oznacza wszystkie wartosci od danego minPing w gore, np.: \"301-* &c{PING}\"")
    @CfgName("ping-format")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var pingFormat_ = Arrays.asList("0-75 &a{PING}", "76-150 &e{PING}", "151-300 &c{PING}", "301-* &c{PING}")

    @CfgExclude
    var pingFormat: Map<IntegerRange, String?>? = null

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do gildii")
    @CfgName("chat-priv")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPriv = "!"

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do sojusznikow gildii")
    @CfgName("chat-ally")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatAlly = "!!"

    @CfgComment("Symbol od ktorego zaczyna sie wiadomosc do wszystkich gildii")
    @CfgName("chat-global")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatGlobal = "!!!"

    @CfgComment("Wyglad wiadomosci wysylanej na czacie gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-priv-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatPrivDesign_ = "&8[&aChat gildii&8] &7{POS}{PLAYER}&8:&f {MESSAGE}"

    @CfgExclude
    var chatPrivDesign: String? = null

    @CfgComment("Wyglad wiadomosci wysylanej na czacie sojusznikow dla sojusznikow")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-ally-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatAllyDesign_ = "&8[&6Chat sojuszniczy&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}"

    @CfgExclude
    var chatAllyDesign: String? = null

    @CfgComment("Wyglad wiadomosci wysylanej na czacie globalnym gildii")
    @CfgComment("Zmienne: {PLAYER}, {TAG}, {MESSAGE}, {POS}")
    @CfgName("chat-global-design")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var chatGlobalDesign_ = "&8[&cChat globalny gildii&8] &8{TAG} &7{POS}{PLAYER}&8:&f {MESSAGE}"

    @CfgExclude
    var chatGlobalDesign: String? = null

    @CfgComment("Czy wiadomosci z chatow gildyjnych powinny byc wyswietlane w logach serwera")
    @CfgName("log-guild-chat")
    var logGuildChat = false

    @CfgComment("Wyglad tagu osob w gildii")
    @CfgName("prefix-our")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var prefixOur_ = "&a{TAG}&f "

    @CfgExclude
    var prefixOur: String? = null

    @CfgComment("Wyglad tagu gildii sojuszniczej")
    @CfgName("prefix-allies")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var prefixAllies_ = "&6{TAG}&f "

    @CfgExclude
    var prefixAllies: String? = null

    @CfgComment("Wyglad tagu wrogiej gildii")
    @CfgName("prefix-enemies")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var prefixEnemies_ = "&c{TAG}&f "

    @CfgExclude
    var prefixEnemies: String? = null

    @CfgComment("Wyglad tagu gildii neutralnej. Widziany rowniez przez graczy bez gildii")
    @CfgName("prefix-other")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var prefixOther_ = "&7{TAG}&f "

    @CfgExclude
    var prefixOther: String? = null

    @CfgComment("Kolory dodawane przed nickiem gracza online przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-offline) pusta")
    @CfgName("ptop-online")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var ptopOnline_ = "&a"

    @CfgExclude
    var ptopOnline: String? = null

    @CfgComment("Kolory dodawane przed nickiem gracza offline przy zamianie zmiennej {PTOP-x}")
    @CfgComment("Jesli nie chcesz kolorowania zaleznego od statusu online - pozostaw te sekcje (i ptop-online) pusta")
    @CfgName("ptop-offline")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var ptopOffline_ = "&c"

    @CfgExclude
    var ptopOffline: String? = null

    @CfgName("use-shared-scoreboard")
    @CfgComment("Czy FunnyGuilds powinno korzystac z wspoldzielonego scoreboarda")
    @CfgComment("Ta opcja pozwala na dzialanie pluginu FunnyGuilds oraz innych pluginow modyfikujacych scoreboard ze soba")
    @CfgComment("UWAGA: Opcja eksperymentalna i moze powodowac bledy przy wyswietlaniu rzeczy zaleznych od scoreboardow!")
    var useSharedScoreboard = false

    @CfgComment("Czy wlaczyc dummy z punktami")
    @CfgComment("UWAGA - zalecane jest wylaczenie tej opcji w przypadku konfliktow z BungeeCord'em, wiecej szczegolow tutaj: https://github.com/FunnyGuilds/FunnyGuilds/issues/769")
    @CfgName("dummy-enable")
    var dummyEnable = true

    @CfgComment("Wyglad nazwy wyswietlanej (suffix, za punktami)")
    @CfgName("dummy-suffix")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var dummySuffix_ = "pkt"

    @CfgExclude
    var dummySuffix: String? = null

    @CfgComment("Wyglad listy graczy, przedzial slotow - od 1 do 80")
    @CfgComment("Schemat wygladu listy: https://github.com/FunnyGuilds/FunnyGuilds/blob/master/assets/tab-scheme.png")
    @CfgComment("> Spis zmiennych gracza:")
    @CfgComment("{PLAYER} - nazwa gracza")
    @CfgComment("{WORLD} - swiat, w ktorym znajduje sie gracz")
    @CfgComment("{PING} - ping gracza")
    @CfgComment("{PING-FORMAT} - ping gracza z formatowaniem")
    @CfgComment("{POINTS} - punkty gracza")
    @CfgComment("{POINTS-FORMAT} - punkty gracza z formatowaniem")
    @CfgComment("{POSITION} - pozycja gracza w rankingu")
    @CfgComment("{KILLS} - liczba zabojstw gracza")
    @CfgComment("{DEATHS} - liczba smierci gracza")
    @CfgComment("{KDR} - stosunek zabojstw do smierci gracza")
    @CfgComment("{WG-REGION} - region WorldGuard'a, na ktorym znajduje sie gracz (pierwszy, jesli jest ich kilka)")
    @CfgComment("{WG-REGIONS} - regiony WorldGuard'a, na ktorych znajduje sie gracz (oddzielone przecinkami)")
    @CfgComment("{VAULT-MONEY} - balans konta gracza pobierany z pluginu Vault")
    @CfgComment("> Spis zmiennych gildyjnych:")
    @CfgComment("{G-NAME} - nazwa gildii do ktorej nalezy gracz")
    @CfgComment("{G-TAG} - tag gildii gracza")
    @CfgComment("{G-OWNER} - wlasciciel gildii")
    @CfgComment("{G-DEPUTIES} - zastepcy gildii")
    @CfgComment("{G-DEPUTY} - losowy z zastepcow gildii")
    @CfgComment("{G-LIVES} - liczba zyc gildii")
    @CfgComment("{G-ALLIES} - liczba sojusznikow gildii")
    @CfgComment("{G-POINTS} - punkty gildii")
    @CfgComment("{G-POINTS-FORMAT} - punkty gildii z formatowaniem")
    @CfgComment("{G-POSITION} - pozycja gildii gracza w rankingu")
    @CfgComment("{G-KILLS} - suma zabojstw czlonkow gildii")
    @CfgComment("{G-DEATHS} - suma smierci czlonkow gildii")
    @CfgComment("{G-KDR} - stosunek zabojstw do smierci czlonkow gildii")
    @CfgComment("{G-MEMBERS-ONLINE} - liczba czlonkow gildii online")
    @CfgComment("{G-MEMBERS-ALL} - liczba wszystkich czlonkow gildii")
    @CfgComment("{G-VALIDITY} - data wygasniecia gildii")
    @CfgComment("{G-REGION-SIZE} - rozmiar gildii")
    @CfgComment("> Spis pozostalych zmiennych:")
    @CfgComment("{GUILDS} - liczba gildii na serwerze")
    @CfgComment("{USERS} - liczba uzytkownikow serwera")
    @CfgComment("{ONLINE} - liczba graczy online")
    @CfgComment("{TPS} - TPS serwera (wspierane tylko od wersji 1.8.8+ spigot/paperspigot)")
    @CfgComment("{SECOND} - Sekunda")
    @CfgComment("{MINUTE} - Minuta")
    @CfgComment("{HOUR} - Godzina")
    @CfgComment("{DAY_OF_WEEK} - Dzien tygodnia wyrazony w postaci nazwy dnia")
    @CfgComment("{DAY_OF_MONTH} - Dzien miesiaca wyrazony w postaci liczby")
    @CfgComment("{MONTH} - Miesiac wyrazony w postaci nazwy miesiaca")
    @CfgComment("{MONTH_NUMBER} - Miesiac wyrazony w postaci liczby")
    @CfgComment("{YEAR} - Rok")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu (np. {PTOP-1}, {PTOP-60})")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu (np. {GTOP-1}, {PTOP-50})")
    @CfgName("player-list")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var playerList: Map<Int, String> = ImmutableMap.builder<Int, String>()
        .put(1, "&7Nick: &b{PLAYER}")
        .put(2, "&7Ping: &b{PING}")
        .put(3, "&7Punkty: &b{POINTS}")
        .put(4, "&7Zabojstwa: &b{KILLS}")
        .put(5, "&7Smierci: &b{DEATHS}")
        .put(6, "&7KDR: &b{KDR}")
        .put(7, "&7Gildia: &b{G-NAME}")
        .put(9, "&7TAG: &b{G-TAG}")
        .put(10, "&7Punkty gildii: &b{G-POINTS-FORMAT}")
        .put(11, "&7Pozycja gildii: &b{G-POSITION}")
        .put(12, "&7Liczba graczy online: &b{G-MEMBERS-ONLINE}")
        .put(21, "&7Online: &b{ONLINE}")
        .put(22, "&7TPS: &b{TPS}")
        .put(41, "&bTop 3 Gildii")
        .put(42, "&71. &b{GTOP-1}")
        .put(43, "&72. &b{GTOP-2}")
        .put(44, "&73. &b{GTOP-3}")
        .put(61, "&bTop 3 Graczy")
        .put(62, "&71. &b{PTOP-1}")
        .put(63, "&72. &b{PTOP-2}")
        .put(64, "&73. &b{PTOP-3}")
        .build()

    @CfgComment("Wyglad naglowka w liscie graczy.")
    @CfgName("player-list-header")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var playerListHeader = "&7FunnyGuilds &b4.9.4 Tribute"

    @CfgComment("Wyglad stopki w liscie graczy.")
    @CfgName("player-list-footer")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var playerListFooter = "&c&lWiadomosci braku (pokazujace sie, gdy gracz nie ma gildii) mozna zmienic w pliku messages.yml!"

    @CfgComment("Liczba pingu pokazana przy kazdej komorce.")
    @CfgName("player-list-ping")
    var playerListPing = 0

    @CfgComment("Czy wszystkie mozliwe komorki maja zostac zapelnione, nie zwazywszy na liczbe graczy online")
    @CfgName("player-list-fill-cells")
    var playerListFillCells = true

    @CfgComment("Czy tablista ma byc wlaczona")
    @CfgName("player-list-enable")
    var playerListEnable = true

    @CfgComment("Co ile tickow lista graczy powinna zostac odswiezona")
    @CfgName("player-list-update-interval")
    var playerListUpdateInterval = 20

    @CfgExclude
    var playerListUpdateInterval_: Long = 0

    @CfgComment("Czy zmienne typu {PTOP-%} oraz {GTOP-%} powinny byc pokolorowane w zaleznosci od relacji gildyjnych")
    @CfgName("player-list-use-relationship-colors")
    var playerListUseRelationshipColors = false

    @CfgComment("Czy tagi gildyjne obok nicku gracza maja byc wlaczone")
    @CfgName("guild-tag-enabled")
    var guildTagEnabled = true

    @CfgComment("Czy tag gildii podany przy tworzeniu gildii powinien zachowac forme taka, w jakiej zostal wpisany")
    @CfgComment("UWAGA: Gdy ta opcja jest wlaczona, opcja \"guild-tag-uppercase\" nie bedzie miala wplywu na tag gildii")
    @CfgName("guild-tag-keep-case")
    var guildTagKeepCase = true

    @CfgComment("Czy tagi gildii powinny byc pokazywane wielka litera")
    @CfgComment("Dziala dopiero po zrestartowaniu serwera")
    @CfgName("guild-tag-uppercase")
    var guildTagUppercase = false

    @CfgComment("Czy wlaczyc tlumaczenie nazw przedmiotow?")
    @CfgName("translated-materials-enable")
    var translatedMaterialsEnable = true

    @CfgComment("Tlumaczenia nazw przedmiotow dla znacznikow {ITEM}, {ITEMS}, {ITEM-NO-AMOUNT}, {WEAPON}")
    @CfgComment("Wypisywac w formacie nazwa_przedmiotu: \"tlumaczona nazwa przedmiotu\"")
    @CfgName("translated-materials-name")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var translatedMaterials_: Map<String, String> = ImmutableMap.builder<String, String>()
        .put("diamond_sword", "&3diamentowy miecz")
        .put("iron_sword", "&7zelazny miecz")
        .put("gold_ingot", "&eZloto")
        .build()

    @CfgExclude
    var translatedMaterials: MutableMap<Material?, String?>? = null

    @CfgComment("Wyglad znacznikow {ITEM} i {ITEMS} (suffix, za iloscia przedmiotu)")
    @CfgComment("Dla np. item-amount-suffix: \"szt.\" 1szt. golden_apple")
    @CfgName("item-amount-suffix")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    var itemAmountSuffix_ = "x"

    @CfgExclude
    var itemAmountSuffix: String? = null

    @CfgComment("Czy filtry nazw i tagow gildii powinny byc wlaczone")
    @CfgName("check-for-restricted-guild-names")
    var checkForRestrictedGuildNames = false

    @CfgComment("Niedozwolone nazwy przy zakladaniu gildii")
    @CfgName("restricted-guild-names")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var restrictedGuildNames = listOf("Administracja")

    @CfgComment("Niedozwolone tagi przy zakladaniu gildii")
    @CfgName("restricted-guild-tags")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var restrictedGuildTags = listOf("TEST")

    @CfgComment("Czy powiadomienie o zabojstwie gracza powinno sie wyswietlac na title dla zabojcy")
    @CfgName("display-title-notification-for-killer")
    var displayTitleNotificationForKiller = false

    @CfgComment("Czy powiadomienia o wejsciu na teren gildii czlonka gildii powinny byc wyswietlane")
    @CfgName("notification-guild-member-display")
    var regionEnterNotificationGuildMember = false

    @CfgComment("Gdzie maja pojawiac sie wiadomosci zwiazane z poruszaniem sie po terenach gildii")
    @CfgComment("Mozliwe miejsca wyswietlania: ACTIONBAR, BOSSBAR, CHAT, TITLE")
    @CfgName("region-move-notification-style")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var regionEnterNotificationStyle_ = Arrays.asList("ACTIONBAR", "BOSSBAR")

    @CfgExclude
    var regionEnterNotificationStyle: MutableList<NotificationStyle> = ArrayList()

    @CfgComment("Jak dlugo title/subtitle powinien sie pojawiac")
    @CfgComment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-in")
    var notificationTitleFadeIn = 10

    @CfgComment("Jak dlugo title/subtitle powinien pozostac na ekranie gracza")
    @CfgComment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-stay")
    var notificationTitleStay = 10

    @CfgComment("Jak dlugo title/subtitle powinien znikac")
    @CfgComment("Czas podawany w tickach. 1 sekunda = 20 tickow")
    @CfgComment("Opcja dziala tylko gdy aktywne jest powiadamianie w trybie TITLE")
    @CfgName("notification-title-fade-out")
    var notificationTitleFadeOut = 10

    @CfgComment("Jakiego koloru powinien byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne kolory: PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE")
    @CfgName("notification-boss-bar-color")
    var bossBarColor = "RED"

    @CfgComment("Jakiego stylu powinien byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne style: SOLID, SEGMENTED_6, SEGMENTED_10, SEGMENTED_12, SEGMENTED_20")
    @CfgName("notification-boss-bar-style")
    var bossBarStyle = "SOLID"

    @CfgComment("Jakie flagi powinny byc nalozone na byc boss bar podczas wyswietlania notyfikacji")
    @CfgComment("Dostepne flagi: DARKEN_SKY, PLAY_BOSS_MUSIC, CREATE_FOG")
    @CfgName("notification-boss-bar-flags")
    var bossBarFlags = listOf("CREATE_FOG")

    @CfgExclude
    var bossBarOptions_: BossBarOptions? = null

    @CfgComment("Czy osoba, ktora zalozyla pierwsza gildie na serwerze powinna dostac nagrode")
    @CfgName("should-give-rewards-for-first-guild")
    var giveRewardsForFirstGuild = false

    @CfgComment("Przedmioty, ktore zostana nadane graczowi, ktory pierwszy zalozyl gildie na serwerze")
    @CfgComment("Dziala tylko w wypadku, gdy opcja \"should-give-rewards-for-first-guild\" jest wlaczona")
    @CfgName("rewards-for-first-guild")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var firstGuildRewards_ = listOf("1 diamond name:&bNagroda_za_pierwsza_gildie_na_serwerze")

    @CfgExclude
    var firstGuildRewards: List<ItemStack?>? = null

    @CfgComment("Zbior przedmiotow potrzebnych do resetu rankingu")
    @CfgName("rank-reset-needed-items")
    @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var rankResetItems_ = listOf("1 diamond")

    @CfgExclude
    var rankResetItems: List<ItemStack?>? = null

    @CfgComment("Czy przy szukaniu danych o graczu ma byc pomijana wielkosc znakow")
    @CfgName("player-lookup-ignorecase")
    var playerLookupIgnorecase = false

    @CfgComment("Nazwy komend")
    @CfgName("commands")
    var commands = Commands()

    @CfgComment("Czy event PlayMoveEvent ma byc aktywny (odpowiada za wyswietlanie powiadomien o wejsciu na teren gildii)")
    @CfgName("event-move")
    var eventMove = true

    @CfgExclude
    var eventPhysics = false

    @CfgComment("Czy System Security ma byc wlaczony?")
    @CfgName("system-security-enable")
    var systemSecurityEnable = true

    @CfgComment("Margines sprawdzania jak daleko uderzył gracz serce gildii")
    @CfgComment("Jeśli dostajesz fałszywe alarmy od Security zwiększ tę wartość do około 0.50 lub więcej")
    @CfgName("reach-compensation")
    var reachCompensation = 0.26

    @CfgComment("Margines sprawdzania przez ile bloków uderzył gracz w serce gildii")
    @CfgName("freeCam-compensation")
    var freeCamCompensation = 0

    @CfgComment("Ilość wątków używanych przez ConcurrencyManager")
    @CfgName("concurrency-threads")
    var concurrencyThreads = 1

    @CfgComment("Co ile minut ma automatycznie zapisywac dane")
    @CfgName("data-interval")
    var dataInterval = 1

    @CfgComment("Jak dlugo plugin powinien czekac na zatrzymanie wszystkich biezacych zadan przy wylaczaniu pluginu")
    @CfgComment("Czas podawany w sekundach")
    @CfgName("plugin-task-termination-timeout")
    var pluginTaskTerminationTimeout_: Long = 30

    @CfgExclude
    var pluginTaskTerminationTimeout: Long = 0

    @CfgComment("Typ zapisu danych")
    @CfgComment("FLAT - Lokalne pliki")
    @CfgComment("MYSQL - baza danych")
    @CfgName("data-model")
    var dataModel = DataModel.FLAT

    @CfgComment("Dane wymagane do polaczenia z baza")
    @CfgComment("UWAGA: connectionTimeout jest w milisekundach!")
    @CfgComment("Sekcja poolSize odpowiada za liczbe zarezerwowanych polaczen, domyslna wartosc 5 powinna wystarczyc")
    @CfgComment("Aby umozliwic FG automatyczne zarzadzanie liczba polaczen - ustaw poolSize na -1")
    @CfgComment("Sekcje usersTableName, guildsTableName i regionsTableName to nazwy tabel z danymi FG w bazie danych")
    @CfgComment("Najlepiej zmieniac te nazwy tylko wtedy, gdy jest naprawde taka potrzeba (np. wystepuje konflikt z innym pluginem)")
    @CfgComment("Aby zmienic nazwy tabel, gdy masz juz w bazie jakies dane z FG:")
    @CfgComment("1. Wylacz serwer")
    @CfgComment("2. Zmien dane w configu FG")
    @CfgComment("3. Zmien nazwy tabel w bazie uzywajac np. phpMyAdmin")
    @CfgName("mysql")
    var mysql = MySQL("localhost", 3306, "db", "root", "passwd", 5, 30000, true, "users", "guilds", "regions")
    private fun loadItemStackList(strings: List<String>): List<ItemStack?> {
        val items: MutableList<ItemStack?> = ArrayList()
        for (item in strings) {
            if (item == null || "" == item) {
                continue
            }
            val itemstack = ItemUtils.parseItem(item)
            if (itemstack != null) {
                items.add(itemstack)
            }
        }
        return items
    }

    private fun loadGUI(contents: List<String>): List<ItemStack?> {
        val items: MutableList<ItemStack?> = ArrayList()
        for (`var` in contents) {
            var item: ItemStack? = null
            if (`var`.contains("GUI-")) {
                val index = RankUtils.getIndex(`var`)
                if (index > 0 && index <= items.size) {
                    item = items[index - 1]
                }
            } else if (`var`.contains("VIPITEM-")) {
                try {
                    val index = RankUtils.getIndex(`var`)
                    if (index > 0 && index <= createItemsVip!!.size) {
                        item = createItemsVip!![index - 1]
                    }
                } catch (e: IndexOutOfBoundsException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Index given in " + `var` + " is > " + createItemsVip!!.size + " or <= 0")
                }
            } else if (`var`.contains("ITEM-")) {
                try {
                    val index = RankUtils.getIndex(`var`)
                    if (index > 0 && index <= createItems!!.size) {
                        item = createItems!![index - 1]
                    }
                } catch (e: IndexOutOfBoundsException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("Index given in " + `var` + " is > " + createItems!!.size + " or <= 0")
                }
            } else {
                item = ItemUtils.parseItem(`var`)
            }
            if (item == null) {
                item = ItemBuilder(MaterialUtils.matchMaterial("stained_glass_pane"), 1, 14).setName("&c&lERROR IN GUI CREATION: $`var`", true).item
            }
            items.add(item)
        }
        return items
    }

    fun load() {
        dateFormat = SimpleDateFormat(FunnyGuilds.Companion.getInstance().getMessageConfiguration().dateFormat)
        try {
            nameRegex = GuildRegex.valueOf(nameRegex_.toUpperCase())
        } catch (e: Exception) {
            nameRegex = GuildRegex.LETTERS
            FunnyGuilds.Companion.getPluginLogger().error("\"" + nameRegex_ + "\" is not a valid regex option!")
        }
        try {
            tagRegex = GuildRegex.valueOf(tagRegex_.toUpperCase())
        } catch (e: Exception) {
            tagRegex = GuildRegex.LETTERS
            FunnyGuilds.Companion.getPluginLogger().error("\"" + tagRegex_ + "\" is not a valid regex option!")
        }
        createItems = loadItemStackList(items_)
        createItemsVip = loadItemStackList(itemsVip_)
        guiItems = loadGUI(guiItems_)
        if (!useCommonGUI) {
            guiItemsVip = loadGUI(guiItemsVip_)
        }
        guiItemsTitle = ChatUtils.colored(guiItemsTitle_)
        guiItemsVipTitle = ChatUtils.colored(guiItemsVipTitle_)
        guiItemsName = ChatUtils.colored(guiItemsName_)
        guiItemsLore = ChatUtils.colored(guiItemsLore_)
        try {
            createEntityType = EntityType.valueOf(createType.toUpperCase().replace(" ", "_"))
        } catch (materialThen: Exception) {
            createMaterial = MaterialUtils.parseMaterialData(createType, true)
        }
        if (createMaterial != null && MaterialUtils.hasGravity(createMaterial!!.left)) {
            eventPhysics = true
        }
        if (enlargeEnable) {
            enlargeItems = loadItemStackList(enlargeItems_)
        } else {
            enlargeSize = 0
            enlargeItems = null
        }
        if (buggedBlocksTimer < 0L) {
            FunnyGuilds.Companion.getPluginLogger().error("The field named \"bugged-blocks-timer\" can not be less than zero!")
            buggedBlocksTimer = 20L // default value
        }
        blockedInteract = HashSet()
        for (s in _blockedInteract) {
            blockedInteract.add(MaterialUtils.parseMaterial(s, false))
        }
        buggedBlocksExclude = HashSet()
        for (s in buggedBlocksExclude_) {
            buggedBlocksExclude.add(MaterialUtils.parseMaterial(s, false))
        }
        try {
            rankSystem = RankSystem.valueOf(rankSystem_.toUpperCase())
        } catch (ex: Exception) {
            rankSystem = RankSystem.ELO
            FunnyGuilds.Companion.getPluginLogger().error("\"" + rankSystem_ + "\" is not a valid rank system!")
        }
        if (rankSystem == RankSystem.ELO) {
            val parsedData: MutableMap<IntegerRange, Int> = HashMap()
            for (entry in IntegerRange.Companion.parseIntegerRange(eloConstants_, false).entries) {
                try {
                    parsedData[entry.key] = entry.value.toInt()
                } catch (e: NumberFormatException) {
                    FunnyGuilds.Companion.getPluginLogger().parser("\"" + entry.value + "\" is not a valid elo constant!")
                }
            }
            eloConstants = parsedData
        }
        val map: MutableMap<Material, Double> = EnumMap(Material::class.java)
        for ((key, chance) in explodeMaterials_) {
            if (chance < 0) {
                continue
            }
            if (key.equals("*", ignoreCase = true)) {
                allMaterialsAreExplosive = true
                defaultExplodeChance = chance
                continue
            }
            val material = MaterialUtils.parseMaterial(key, true)
            if (material == null || material == Material.AIR) {
                continue
            }
            map[material] = chance
        }
        explodeMaterials = map
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        guildTNTProtectionStartTime = LocalTime.parse(guildTNTProtectionStartTime_, timeFormatter)
        guildTNTProtectionEndTime = LocalTime.parse(guildTNTProtectionEndTime_, timeFormatter)
        guildTNTProtectionPassingMidnight = guildTNTProtectionStartTime.isAfter(guildTNTProtectionEndTime)
        translatedMaterials = HashMap()
        for (materialName in translatedMaterials_.keys) {
            val material = MaterialUtils.matchMaterial(materialName.toUpperCase()) ?: continue
            translatedMaterials[material] = translatedMaterials_[materialName]
        }
        itemAmountSuffix = ChatUtils.colored(itemAmountSuffix_)
        for (s in regionEnterNotificationStyle_) {
            regionEnterNotificationStyle.add(NotificationStyle.valueOf(s.toUpperCase()))
        }
        if (notificationTitleFadeIn <= 0) {
            FunnyGuilds.Companion.getPluginLogger().error("The field named \"notification-title-fade-in\" can not be less than or equal to zero!")
            notificationTitleFadeIn = 10
        }
        if (notificationTitleStay <= 0) {
            FunnyGuilds.Companion.getPluginLogger().error("The field named \"notification-title-stay\" can not be less than or equal to zero!")
            notificationTitleStay = 10
        }
        if (notificationTitleFadeOut <= 0) {
            FunnyGuilds.Companion.getPluginLogger().error("The field named \"notification-title-fade-out\" can not be less than or equal to zero!")
            notificationTitleFadeOut = 10
        }
        if ("v1_8_R1" != Reflections.SERVER_VERSION && "v1_8_R3" != Reflections.SERVER_VERSION) {
            bossBarOptions_ = BossBarOptions.Companion.builder()
                .color(bossBarColor)
                .style(bossBarStyle)
                .flags(bossBarFlags)
                .build()
        }
        rankResetItems = loadItemStackList(rankResetItems_)
        firstGuildRewards = loadItemStackList(firstGuildRewards_)
        warProtection = TimeUtils.parseTime(warProtection_)
        warWait = TimeUtils.parseTime(warWait_)
        validityStart = TimeUtils.parseTime(validityStart_)
        validityTime = TimeUtils.parseTime(validityTime_)
        validityWhen = TimeUtils.parseTime(validityWhen_)
        validityItems = loadItemStackList(validityItems_)
        joinItems = loadItemStackList(joinItems_)
        baseItems = loadItemStackList(baseItems_)
        prefixOur = ChatUtils.colored(prefixOur_)
        prefixAllies = ChatUtils.colored(prefixAllies_)
        prefixOther = ChatUtils.colored(prefixOther_)
        prefixEnemies = ChatUtils.colored(prefixEnemies_)
        ptopOnline = ChatUtils.colored(ptopOnline_)
        ptopOffline = ChatUtils.colored(ptopOffline_)
        dummySuffix = ChatUtils.colored(dummySuffix_)
        chatPosition = ChatUtils.colored(chatPosition_)
        chatGuild = ChatUtils.colored(chatGuild_)
        chatRank = ChatUtils.colored(chatRank_)
        chatPoints = ChatUtils.colored(chatPoints_)
        pointsFormat = IntegerRange.Companion.parseIntegerRange(pointsFormat_, true)
        pingFormat = IntegerRange.Companion.parseIntegerRange(pingFormat_, true)
        ptopPoints = ChatUtils.colored(ptopPoints_)
        gtopPoints = ChatUtils.colored(gtopPoints_)
        chatPrivDesign = ChatUtils.colored(chatPrivDesign_)
        chatAllyDesign = ChatUtils.colored(chatAllyDesign_)
        chatGlobalDesign = ChatUtils.colored(chatGlobalDesign_)
        if (pasteSchematicOnCreation) {
            if (guildSchematicFileName == null || guildSchematicFileName!!.isEmpty()) {
                FunnyGuilds.Companion.getPluginLogger().error("The field named \"guild-schematic-file-name\" is empty, but field \"paste-schematic-on-creation\" is set to true!")
                pasteSchematicOnCreation = false
            } else {
                guildSchematicFile = File(FunnyGuilds.Companion.getInstance().getDataFolder(), guildSchematicFileName)
                if (!guildSchematicFile!!.exists()) {
                    FunnyGuilds.Companion.getPluginLogger().error("File with given name in field \"guild-schematic-file-name\" does not exist!")
                    pasteSchematicOnCreation = false
                }
            }
        }
        playerListUpdateInterval_ = Math.max(1, playerListUpdateInterval).toLong()
        lastAttackerAsKillerConsiderationTimeout_ = TimeUnit.SECONDS.toMillis(lastAttackerAsKillerConsiderationTimeout.toLong())
        rankingUpdateInterval_ = Math.max(1, rankingUpdateInterval).toLong()
        pluginTaskTerminationTimeout = Math.max(1, pluginTaskTerminationTimeout_)
    }

    class Commands {
        var funnyguilds = FunnyCommand("funnyguilds", listOf("fg"))
        var guild = FunnyCommand("gildia", Arrays.asList("gildie", "g"))
        var create = FunnyCommand("zaloz")
        var delete = FunnyCommand("usun")
        var confirm = FunnyCommand("potwierdz")
        var invite = FunnyCommand("zapros")
        var join = FunnyCommand("dolacz")
        var leave = FunnyCommand("opusc")
        var kick = FunnyCommand("wyrzuc")
        var base = FunnyCommand("baza")
        var enlarge = FunnyCommand("powieksz")
        var ally = FunnyCommand("sojusz")
        var war = FunnyCommand("wojna")
        var items = FunnyCommand("przedmioty")
        var escape = FunnyCommand("ucieczka", listOf("escape"))
        var rankReset = FunnyCommand("rankreset", listOf("resetrank"))
        var tnt = FunnyCommand("tnt")

        @CfgName("break")
        var break_ = FunnyCommand("rozwiaz")
        var info = FunnyCommand("info")
        var player = FunnyCommand("gracz")
        var top = FunnyCommand("top", listOf("top10"))
        var validity = FunnyCommand("przedluz")
        var leader = FunnyCommand("lider", listOf("zalozyciel"))
        var deputy = FunnyCommand("zastepca")
        var ranking = FunnyCommand("ranking")
        var setbase = FunnyCommand("ustawbaze", listOf("ustawdom"))
        var pvp = FunnyCommand("pvp", listOf("ustawpvp"))

        @CfgComment("Komendy administratora")
        var admin = AdminCommands()

        class FunnyCommand {
            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var name: String? = null

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
            var aliases: List<String>? = null
            var enabled = false

            constructor() {}

            @JvmOverloads
            constructor(name: String?, aliases: List<String>? = emptyList(), enabled: Boolean = true) {
                this.name = name
                this.aliases = aliases
                this.enabled = enabled
            }
        }

        class AdminCommands {
            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var main = "ga"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var add = "ga dodaj"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var delete = "ga usun"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var kick = "ga wyrzuc"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var teleport = "ga tp"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var points = "ga points"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var kills = "ga kills"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var deaths = "ga deaths"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var ban = "ga ban"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var lives = "ga zycia"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var move = "ga przenies"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var unban = "ga unban"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var validity = "ga przedluz"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var name = "ga nazwa"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var tag = "ga tag"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var spy = "ga spy"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var enabled = "ga enabled"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var leader = "ga lider"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var deputy = "ga zastepca"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var protection = "ga ochrona"

            @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
            var base = "ga baza"
        }
    }

    enum class DataModel {
        FLAT, MYSQL
    }

    class MySQL {
        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var hostname: String? = null
        var port = 0

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var database: String? = null

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var user: String? = null

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var password: String? = null
        var poolSize = 0
        var connectionTimeout = 0
        var useSSL = false

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var usersTableName: String? = null

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var guildsTableName: String? = null

        @CfgStringStyle(StringStyle.ALWAYS_QUOTED)
        var regionsTableName: String? = null

        constructor() {}
        constructor(
            hostname: String?,
            port: Int,
            database: String?,
            user: String?,
            password: String?,
            poolSize: Int,
            connectionTimeout: Int,
            useSSL: Boolean,
            usersTableName: String?,
            guildsTableName: String?,
            regionsTableName: String?
        ) {
            this.hostname = hostname
            this.port = port
            this.database = database
            this.user = user
            this.password = password
            this.poolSize = poolSize
            this.connectionTimeout = connectionTimeout
            this.useSSL = useSSL
            this.usersTableName = usersTableName
            this.guildsTableName = guildsTableName
            this.regionsTableName = regionsTableName
        }
    }
}