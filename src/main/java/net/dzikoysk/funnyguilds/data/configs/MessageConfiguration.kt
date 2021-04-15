package net.dzikoysk.funnyguilds.data.configs

import net.dzikoysk.funnyguilds.FunnyGuilds
import net.dzikoysk.funnyguilds.util.commons.ChatUtils
import org.diorite.cfg.annotations.CfgCollectionStyle
import org.diorite.cfg.annotations.CfgCollectionStyle.CollectionStyle
import org.diorite.cfg.annotations.CfgComment

java.lang.Exception
import java.lang.StackTraceElementimport

java.util.*
class MessageConfiguration {
    @CfgComment("<------- Global Date Format -------> #")
    var dateFormat = "dd.MM.yyyy HH:mm:ss"

    @CfgComment("<------- No Value Messages -------> #")
    var gNameNoValue = "Brak (G-NAME/NAME)"
    var gTagNoValue = "Brak (G-TAG/TAG)"
    var gOwnerNoValue = "Brak (G-OWNER)"
    var gDeputiesNoValue = "Brak (G-DEPUTIES)"
    var gDeputyNoValue = "Brak (G-DEPUTY)"
    var gValidityNoValue = "Brak (G-VALIDITY)"
    var gRegionSizeNoValue = "Brak (G-REGION-SIZE)"
    var alliesNoValue = "Brak (ALLIES)"
    var enemiesNoValue = "Brak (ENEMIES)"
    var gtopNoValue = "Brak (GTOP-x)"
    var ptopNoValue = "Brak (PTOP-x)"
    var wgRegionNoValue = "Brak (WG-REGION)"
    var minMembersToIncludeNoValue = "Brak (guild-min-members w config.yml)"

    @CfgComment("<------- Permission Messages -------> #")
    var permission = "&cNie masz wystarczajacych uprawnien do uzycia tej komendy!"
    var blockedWorld = "&cZarzadzanie gildiami jest zablokowane na tym swiecie!"
    var playerOnly = "&cKomenda dostepna tylko dla graczy!"

    @CfgComment("<------- Rank Messages -------> #")
    var rankLastVictimV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!"
    var rankLastVictimA = "&7Ostatnio zabiles tego samego gracza, punkty nie zostaja dodane!"
    var rankLastAttackerV = "&7Ostatnio zostales zabity przez tego samego gracza, punkty nie zostaja odebrane!"
    var rankLastAttackerA = "&7Ten gracz byl ostatnio zabity przez Ciebie, punkty nie zostaja dodane!"
    var rankIPVictim = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja odjete!"
    var rankIPAttacker = "&7Ten gracz ma taki sam adres IP, punkty nie zostaja dodane!"

    @CfgComment("Dostepne zmienne: {ATTACKER}, {VICTIM}, {-}, {+}, {POINTS}, {POINTS-FORMAT}, {VTAG}, {ATAG}, {WEAPON}, {REMAINING-HEALTH}, {REMAINING-HEARTS}, {ASSISTS}")
    var rankDeathMessage = "{ATAG}&b{ATTACKER} &7(&a+{+}&7) zabil {VTAG}&b{VICTIM} &7(&c-{-}&7) uzywajac &b{WEAPON}"
    var rankKillTitle = "&cZabiles gracza {VICTIM}"
    var rankKillSubtitle = "&7+{+}"

    @CfgComment("Zamiast zmiennej {ASSISTS} wstawiane sa kolejne wpisy o asystujacych graczach")
    var rankAssistMessage = "&7Asystowali: {ASSISTS}"

    @CfgComment("Dostepne zmienne: {PLAYER}, {+}, {SHARE}")
    var rankAssistEntry = "&b{PLAYER} &7(&a+{+}&7, {SHARE}% dmg)"

    @CfgComment("Znaki oddzielajace kolejne wpisy o asystujacych graczach")
    var rankAssistDelimiter = "&8, "

    @CfgComment("Dostepne zmienne: {LAST-RANK}, {CURRENT-RANK}")
    var rankResetMessage = "&7Zresetowales swoj ranking z poziomu &c{LAST-RANK} &7do poziomu &c{CURRENT-RANK}&7."

    @CfgComment("<------- Ban Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}, {REASON}, {DATE}, {NEWLINE}")
    var banMessage = "&7Zostales zbanowany do &b{DATE}{NEWLINE}{NEWLINE}&7za: &b{REASON}"

    @CfgComment("<------- Region Messages -------> #")
    var regionOther = "&cTen teren nalezy do innej gildii!"
    var regionCenter = "&cNie mozesz zniszczyc srodka swojej gildii!"

    @CfgComment("Dostepne zmienne: {TIME}")
    var regionExplode = "&cBudowanie na terenie gildii zablokowane na czas &4{TIME} sekund&c!"

    @CfgComment("Dostepne zmienne: {TIME}")
    var regionExplodeInteract = "&cNie mozna budowac jeszcze przez &4{TIME} sekund&c!"
    var regionCommand = "&cTej komendy nie mozna uzyc na terenie innej gildii!"
    var regionExplosionHasProtection = "&cEksplozja nie spowodowala zniszczen na terenie gildii, poniewaz jest ona chroniona!"
    var regionsDisabled = "&cRegiony gildii sa wylaczone!"

    @CfgComment("<------- ActionBar Region Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}")
    var notificationActionbarIntruderEnterGuildRegion = "&7Gracz &c{PLAYER} &7wkroczyl na teren &cTwojej &7gildii!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationActionbarEnterGuildRegion = "&7Wkroczyles na teren gildii &c{TAG}&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationActionbarLeaveGuildRegion = "&7Opusciles teren gildii &c{TAG}&7!"

    @CfgComment("<------- Bossbar Region Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}")
    var notificationBossbarIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationBossbarEnterGuildRegion = notificationActionbarEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationBossbarLeaveGuildRegion = notificationActionbarLeaveGuildRegion

    @CfgComment("<------- Chat Region Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}")
    var notificationChatIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationChatEnterGuildRegion = notificationActionbarEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationChatLeaveGuildRegion = notificationActionbarLeaveGuildRegion

    @CfgComment("<------- Title Region Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}")
    var notificationTitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var notificationSubtitleIntruderEnterGuildRegion = notificationActionbarIntruderEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationTitleEnterGuildRegion = notificationActionbarEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationSubtitleEnterGuildRegion = notificationActionbarEnterGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationTitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var notificationSubtitleLeaveGuildRegion = notificationActionbarLeaveGuildRegion

    @CfgComment("<------- Broadcast Messages -------> #")
    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var broadcastCreate = "&a{PLAYER} &7zalozyl gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var broadcastDelete = "&c{PLAYER} &7rozwiazal gildie &c{TAG}&7!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var broadcastJoin = "&a{PLAYER} &7dolaczyl do gildii &a{TAG}&7!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var broadcastLeave = "&c{PLAYER} &7opuscil gildie &c{TAG}&7!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var broadcastKick = "&c{PLAYER} &7zostal &cwyrzucony &7z gildii &c{TAG}&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}, {REASON}, {TIME}")
    var broadcastBan = "&7Gildia &c{TAG}&7 zostala zbanowana za &c{REASON}&7, gratulacje!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var broadcastUnban = "&7Gildia &a{TAG}&7 zostala &aodbanowana&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}, {X}, {Y}, {Z}")
    var broadcastValidity = "&7Gildia &b{TAG} &7wygasla&b! &7Jej baza znajdowala sie na x: &b{X} &7y: &b{Y} &7z: &b{Z}&7!"

    @CfgComment("Dostepne zmienne: {WINNER}, {LOSER}")
    var broadcastWar = "&7Gildia &4{WINNER}&7 podblila gildie &4{LOSER}&7!!"

    @CfgComment("<------- Help Messages -------> #")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var helpList = Arrays.asList(
        "&7---------------------&8[ &aGildie &8]&7---------------------",
        "&a/zaloz [tag] [nazwa] &8- &7Tworzy gildie",
        "&a/zapros [gracz] &8- &7Zaprasza gracza do gildii",
        "&a/dolacz [tag] &8- &7Przyjmuje zaproszenie do gildii",
        "&a/info [tag] &8- &7Informacje o danej gildii",
        "&a/baza &8- &7Teleportuje do bazy gildii",
        "&a/powieksz &8- &7Powieksza teren gildii",
        "&a/przedluz &8- &7Przedluza waznosc gildii",
        "&a/lider [gracz] &8- &7Oddaje zalozyciela gildii",
        "&a/zastepca [gracz] &8- &7Nadaje zastepce gildii",
        "&a/sojusz [tag] &8- &7Pozwala nawiazac sojusz",
        "&a/opusc &8- &7Opuszcza gildie",
        "&a/wyrzuc [gracz] &8- &7Wyrzuca gracza z gildii",
        "&a/rozwiaz [tag] &8- &7Rozwiazuje sojusz",
        "&a/usun &8- &7Usuwa gildie",
        "&a/przedmioty &8- &7Pokazuje przedmioty potrzebne do zalozenia gildii",
        "&a/ucieczka &8- &7Rozpoczyna ucieczke z terenu innej gildii"
    )

    @CfgComment("<------- Admin Help Messages -------> #")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var adminHelpList = Arrays.asList(
        "&a/ga dodaj [tag] [nick] &8- &7Dodaje gracza do gildii",
        "&a/ga usun [tag] &8- &7Usuwa gildie",
        "&a/ga wyrzuc [nick] &8- &7Wyrzuca gracza z gildii",
        "&a/ga tp [tag] &8- &7Teleportuje do bazy gildii",
        "&a/ga points [nick] [points] &8- &7Ustawia liczbe punktow gracza",
        "&a/ga kills [nick] [kills] &8- &7Ustawia liczbe zabojstw gracza",
        "&a/ga deaths [nick] [deaths] &8- &7Ustawia liczbe smierci gracza",
        "&a/ga ban [tag] [czas] [powod] &8- &7Banuje gildie na okreslony czas",
        "&a/ga unban [tag] &8- &7Odbanowywuje gildie",
        "&a/ga zycia [tag] [zycia] &8- &7Ustawia liczbe zyc gildii",
        "&a/ga przenies [tag] &8- &7Przenosi teren gildii",
        "&a/ga przedluz [tag] [czas] &8- &7Przedluza waznosc gildii o podany czas",
        "&a/ga nazwa [tag] [nazwa] &8- &7Zmienia nazwe gildii",
        "&a/ga tag [tag] [nowy tag] &8- &7Zmienia tag gildii",
        "&a/ga spy &8- &7Szpieguje czat gildii",
        "&a/ga enabled &8- &7Zarzadzanie statusem zakladania gildii",
        "&a/ga lider [tag] [gracz] &8- &7Zmienia lidera gildii",
        "&a/ga zastepca [tag] [gracz] &8- &7Nadaje zastepce gildii",
        "&a/ga baza [gracz] &8- &7Teleportuje gracza do bazy jego gildii"
    )

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {KDR}, {RANK}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var playerInfoList = Arrays.asList(
        "&8--------------.-----------------",
        "&7Gracz: &a{PLAYER}",
        "&7Gildia: &a{TAG}",
        "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
        "&7Zabojstwa: &a{KILLS}",
        "&7Smierci: &a{DEATHS}",
        "&7KDR: &a{KDR}",
        "&8-------------.------------------"
    )

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {KDR}, {RANK}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var playerRightClickInfo = Arrays.asList(
        "&8--------------.-----------------",
        "&7Gracz: &a{PLAYER}",
        "&7Gildia: &a{TAG}",
        "&7Miejsce: &a{RANK} &8(&a{POINTS}&8)",
        "&8-------------.------------------"
    )

    @CfgComment("<------- Info Messages -------> #")
    var infoTag = "&cPodaj tag gildii!"
    var infoExists = "&cGildia o takim tagu nie istnieje!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}, {OWNER}, {DEPUTIES}, {MEMBERS}, {MEMBERS-ONLINE}, {MEMBERS-ALL}, {REGION-SIZE}, {POINTS}, {POINTS-FORMAT}, {KILLS}, {DEATHS}, {KDR}, {ALLIES}, {ALLIES-TAGS}, {ENEMIES}, {ENEMIES-TAGS}, {RANK}, {VALIDITY}, {LIVES}, {GUILD-PROTECTION}, {GUILD-ADDITIONAL-PROTECTION}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var infoList = Arrays.asList(
        "&8-------------------------------",
        "&7Gildia: &c{GUILD} &8[&c{TAG}&8]",
        "&7Zalozyciel: &c{OWNER}",
        "&7Zastepcy: &c{DEPUTIES}",
        "&7Punkty: &c{POINTS} &8[&c{RANK}&8]",
        "&7Ochrona: &c{GUILD-PROTECTION}",
        "&7Zycia: &4{LIVES}",
        "&7Waznosc: &c{VALIDITY}",
        "&7Czlonkowie: &7{MEMBERS}",
        "&7Sojusze: &c{ALLIES}",
        "&7Wojny: &c{ENEMIES}",
        "&8-------------------------------"
    )

    @CfgComment("<------- Top Messages -------> #")
    @CfgComment("{GTOP-<pozycja>} - Gildia na podanej pozycji w rankingu")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var topList = Arrays.asList(
        "&8----------{ &cTOP 10 &8}----------",
        "&71&8. &c{GTOP-1}",
        "&72&8. &c{GTOP-2}",
        "&73&8. &c{GTOP-3}",
        "&74&8. &c{GTOP-4}",
        "&75&8. &c{GTOP-5}",
        "&76&8. &c{GTOP-6}",
        "&77&8. &c{GTOP-7}",
        "&78&8. &c{GTOP-8}",
        "&79&8. &c{GTOP-9}",
        "&710&8. &c{GTOP-10}"
    )

    @CfgComment("<------- Ranking Messages -------> #")
    @CfgComment("{PTOP-<pozycja>} - Gracz na podanej pozycji w rankingu")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var rankingList = Arrays.asList(
        "&8----------{ &cTOP 10 Graczy &8}----------",
        "&71&8. &c{PTOP-1}",
        "&72&8. &c{PTOP-2}",
        "&73&8. &c{PTOP-3}",
        "&74&8. &c{PTOP-4}",
        "&75&8. &c{PTOP-5}",
        "&76&8. &c{PTOP-6}",
        "&77&8. &c{PTOP-7}",
        "&78&8. &c{PTOP-8}",
        "&79&8. &c{PTOP-9}",
        "&710&8. &c{PTOP-10}"
    )

    @CfgComment("<------- General Messages -------> #")
    var generalHasGuild = "&cMasz juz gildie!"
    var generalNoNameGiven = "&cPodaj nazwe gildii!"
    var generalHasNoGuild = "&cNie masz gildii!"
    var generalIsNotOwner = "&cNie jestes zalozycielem gildii!"
    var generalNoTagGiven = "&cPodaj tag gildii!"
    var generalNoNickGiven = "&cPodaj nick gracza!"
    var generalUserHasGuild = "&cTen gracz ma juz gildie!"
    var generalNoGuildFound = "&cTaka gildia nie istnieje!"
    var generalNotPlayedBefore = "&cTen gracz nigdy nie byl na serwerze!"
    var generalNotOnline = "&cTen gracz nie jest obecnie na serwerze!"

    @CfgComment("Dostepne zmienne: {TAG}")
    var generalGuildNotExists = "&7Gildia o tagu &c{TAG} &7nie istnieje!"
    var generalIsNotMember = "&cTen gracz nie jest czlonkiem twojej gildii!"
    var generalPlayerHasNoGuild = "&cTen gracz nie ma gildii!"
    var generalCommandDisabled = "&cTa komenda jest wylaczona!"
    var generalAllyPvpDisabled = "&cPVP pomiedzy sojuszami jest wylaczone w konfiguracji!"

    @CfgComment("<------- Escape Messages -------> #")
    var escapeDisabled = "&cPrzykro mi, ucieczki sa wylaczone!"

    @CfgComment("Dostepne zmienne: {TIME}")
    var escapeStartedUser = "&aDobrze, jesli nikt ci nie przeszkodzi - za {TIME} sekund uda ci sie uciec!"

    @CfgComment("Dostepne zmienne: {TIME}, {X}, {Y}, {Z}, {PLAYER}")
    var escapeStartedOpponents = "&cGracz {PLAYER} probuje uciec z terenu twojej gildii! ({X}  {Y}  {Z})"
    var escapeCancelled = "&cUcieczka zostala przerwana!"
    var escapeInProgress = "&cUcieczka juz trwa!"
    var escapeSuccessfulUser = "&aUdalo ci sie uciec!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var escapeSuccessfulOpponents = "&cGraczowi {PLAYER} udalo sie uciec z terenu twojej gildii!"
    var escapeNoUserGuild = "&cNie masz gildii do ktorej moglbys uciekac!"
    var escapeNoNeedToRun = "&cNie znajdujesz sie na terenie zadnej gildii, po co uciekac?"
    var escapeOnYourRegion = "&cZnajdujesz sie na terenie wlasnej gildii, dokad chcesz uciekac?"

    @CfgComment("<------- Create Guild Messages -------> #")
    @CfgComment("Dostepne zmienne: {LENGTH}")
    var createTagLength = "&7Tag nie moze byc dluzszy niz &c{LENGTH} litery&7!"

    @CfgComment("Dostepne zmienne: {LENGTH}")
    var createNameLength = "&cNazwa nie moze byc dluzsza niz &c{LENGTH} litery&7!"

    @CfgComment("Dostepne zmienne: {LENGTH}")
    var createTagMinLength = "&7Tag nie moze byc krotszy niz &c{LENGTH} litery&7!"

    @CfgComment("Dostepne zmienne: {LENGTH}")
    var createNameMinLength = "&cNazwa nie moze byc krotsza niz &c{LENGTH} litery&7!"
    var createOLTag = "&cTag gildii moze zawierac tylko litery!"
    var createOLName = "&cNazwa gildii moze zawierac tylko litery!"
    var createMore = "&cNazwa gildi nie moze zawierac spacji!"
    var createNameExists = "&cJest juz gildia z taka nazwa!"
    var createTagExists = "&cJest juz gildia z takim tagiem!"
    var restrictedGuildName = "&cPodana nazwa gildii jest niedozwolona."
    var restrictedGuildTag = "&cPodany tag gildii jest niedozwolony."

    @CfgComment("Dostepne zmienne: {DISTANCE}")
    var createSpawn = "&7Jestes zbyt blisko spawnu! Minimalna odleglosc to &c{DISTANCE}"
    var createIsNear = "&cW poblizu znajduje sie jakas gildia, poszukaj innego miejsca!"

    @CfgComment("Dostepne zmienne: {POINTS}, {POINTS-FORMAT}, {REQUIRED}, {REQUIRED-FORMAT}")
    var createRank = "&cAby zalozyc gildie, wymagane jest przynajmniej &7{REQUIRED} &cpunktow."

    @CfgComment("Dostepne zmienne: {ITEM}, {ITEMS}")
    var createItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}&c. Najedz na przedmiot, aby dowiedziec sie wiecej"

    @CfgComment("Dostepne zmienne: {EXP}")
    var createExperience = "&cNie posiadasz wymaganego doswiadczenia do zalozenia gildii: &7{EXP}"

    @CfgComment("Dostepne zmienne: {MONEY}")
    var createMoney = "&cNie posiadasz wymaganej ilosci pieniedzy do zalozenia gildii: &7{MONEY}"

    @CfgComment("Dostepne zmienne: {PLAYER}, {GUILD}, {TAG}")
    var createGuild = "&7Zalozono gildie o nazwie &a{GUILD} &7i tagu &a{TAG}&7!"
    var createGuildCouldNotPasteSchematic = "&cWystapil blad podczas tworzenia terenu gildii, zglos sie do administracji."

    @CfgComment("Dostepne zmienne: {BORDER-MIN-DISTANCE}")
    var createNotEnoughDistanceFromBorder = "&cJestes zbyt blisko granicy mapy aby zalozyc gildie! (Minimalna odleglosc: {BORDER-MIN-DISTANCE})"

    @CfgComment("<------- Delete Guild Messages -------> #")
    var deleteConfirm = "&7Aby potwierdzic usuniecie gildii, wpisz: &c/potwierdz"
    var deleteToConfirm = "&cNie masz zadnych dzialan do potwierdzenia!"
    var deleteSomeoneIsNear = "&cNie mozesz usunac gildii, ktos jest w poblizu!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var deleteSuccessful = "&7Pomyslnie &cusunieto &7gildie!"

    @CfgComment("<------- Invite Messages -------> #")
    var invitePlayerExists = "&cNie ma takiego gracza na serwerze!"

    @CfgComment("Dostepne zmienne: {AMOUNT}")
    var inviteAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe czlonkow w gildii! (&c{AMOUNT}&7)"
    var inviteAllyAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe sojuszy miedzygildyjnych! (&c{AMOUNT}&7)"

    @CfgComment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    var inviteAllyTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe sojuszy! (&c{AMOUNT}&7)"
    var inviteCancelled = "&cCofnieto zaproszenie!"

    @CfgComment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    var inviteCancelledToInvited = "&7Zaproszenie do gildii &c{GUILD} &7zostalo wycofane!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var inviteToOwner = "&7Gracz &a{PLAYER} &7zostal zaproszony do gildii!"

    @CfgComment("Dostepne zmienne: {OWNER}, {GUILD}, {TAG}")
    var inviteToInvited = "&aOtrzymano zaproszenie do gildii &7{TAG}&a!"

    @CfgComment("<------- Join Messages -------> #")
    var joinHasNotInvitation = "&cNie masz zaproszenia do gildii!"
    var joinHasNotInvitationTo = "&cNie otrzymales zaproszenia do tej gildii!"
    var joinHasGuild = "&cMasz juz gildie!"
    var joinTagExists = "&cNie ma gildii o takim tagu!"

    @CfgComment("Dostepne zmienne: {GUILDS}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var joinInvitationList = Arrays.asList(
        "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
        "&7Wpisz &a/dolacz [tag] &7aby dolaczyc do wybranej gildii"
    )

    @CfgComment("Dostepne zmienne: {ITEM}, {ITEMS}")
    var joinItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var joinToMember = "&aDolaczyles do gildii &7{GUILD}"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var joinToOwner = "&a{PLAYER} &7dolaczyl do &aTwojej &7gildii!"

    @CfgComment("<------- Leave Messages -------> #")
    var leaveIsOwner = "&cZalozyciel &7nie moze opuscic gildii!"

    @CfgComment("Dostepne zmienne: {GUILDS}, {TAG}")
    var leaveToUser = "&7Opusciles gildie &a{GUILD}&7!"

    @CfgComment("<------- Kick Messages -------> #")
    var kickOtherGuild = "&cTen gracz nie jest w Twojej gildii!"
    var kickOwner = "&cNie mozna wyrzucic zalozyciela!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}, {PLAYER}")
    var kickToOwner = "&c{PLAYER} &7zostal wyrzucony z gildii!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var kickToPlayer = "&cZostales wyrzucony z gildii!"

    @CfgComment("<------- Enlarge Messages -------> #")
    var enlargeMaxSize = "&cOsiagnieto juz maksymalny rozmiar terenu!"
    var enlargeIsNear = "&cW poblizu znajduje sie jakas gildia, nie mozesz powiekszyc terenu!"

    @CfgComment("Dostepne zmienne: {ITEM}")
    var enlargeItem = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}"

    @CfgComment("Dostepne zmienne: {SIZE}, {LEVEL}")
    var enlargeDone = "&7Teren &aTwojej &7gildii zostal powiekszony i jego wielkosc wynosi teraz &a{SIZE} &7(poz.&a{LEVEL}&7)"

    @CfgComment("<------- Base Messages -------> #")
    var baseTeleportationDisabled = "&cTeleportacja do baz gildyjnych nie jest dostepna"
    var baseHasNotRegion = "&cTwoja gildia nie posiada terenu!"
    var baseHasNotCenter = "&cTwoja gildia nie posiada srodka regionu!"
    var baseIsTeleportation = "&cWlasnie sie teleportujesz!"

    @CfgComment("Dostepne zmienne: {ITEM}, {ITEMS}")
    var baseItems = "&cNie masz wszystkich przedmiotow! Obecnie brakuje Ci &7{ITEM} &cz &7{ITEMS}"
    var baseDontMove = "&7Nie ruszaj sie przez &c{TIME} &7sekund!"
    var baseMove = "&cRuszyles sie, teleportacja przerwana!"
    var baseTeleport = "&aTeleportacja&7..."

    @CfgComment("<------- War Messages -------> #")
    var enemyCorrectUse = "&7Aby rozpoczac wojne z gildia wpisz &c/wojna [tag]"
    var enemySame = "&cNie mozesz rozpoczac wojny z wlasna gildia!"
    var enemyAlly = "&cNie mozesz rozpoczac wojny z ta gildia poniewaz jestescie sojusznikami!"
    var enemyAlready = "&cProwadzisz juz wojne z ta gildia!"

    @CfgComment("Dostepne zmienne: {AMOUNT}")
    var enemyMaxAmount = "&7Osiagnieto juz &cmaksymalna &7liczbe wojen miedzygildyjnych! (&c{AMOUNT}&7)"

    @CfgComment("Dostepne zmienne: {AMOUNT}, {GUILD}, {TAG}")
    var enemyMaxTargetAmount = "&7Gildia {TAG} posiada juz maksymalna liczbe wojen! (&c{AMOUNT}&7)"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var enemyDone = "&7Wypowiedziano gildii &a{GUILD}&7 wojne!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var enemyIDone = "&7Gildia &a{GUILD} &7wypowiedziala twojej gildii wojne!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var enemyEnd = "&7Zakonczono wojne z gildia &a{GUILD}&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var enemyIEnd = "&7Gildia &a{GUILD} &7zakonczyla wojne z twoja gildia!"

    @CfgComment("<------- Ally Messages -------> #")
    var allyHasNotInvitation = "&7Aby zaprosic gildie do sojuszy wpisz &c/sojusz [tag]"

    @CfgComment("Dostepne zmienne: {GUILDS}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var allyInvitationList = Arrays.asList(
        "&7Otrzymano zaproszenia od gildii: &a{GUILDS}",
        "&7Aby zaakceptowac uzyj &a/sojusz [tag]"
    )

    @CfgComment("Dostepne zmienne: {TAG}")
    var allyAlly = "&cMasz juz sojusz z ta gildia!"
    var allyDoesntExist = "&cNie posiadasz sojuszu z ta gildia!"
    var allySame = "&cNie mozesz nawiazac sojuszu z wlasna gildia!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyDone = "&7Nawiazano sojusz z gildia &a{GUILD}&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyIDone = "&7Gildia &a{GUILD} &7przystapila do sojuszu!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyReturn = "&7Wycofano zaproszenie do sojuszu dla gildii &c{GUILD}!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyIReturn = "&7Gildia &c{GUILD} &7wycofala zaprszenie do sojuszu!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyInviteDone = "&7Zaproszono gildie &a{GUILD} &7do sojuszu!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var allyToInvited = "&7Otrzymano zaproszenie do sojuszu od gildii &a{GUILD}&7!"

    @CfgComment("<------- Break Messages -------> #")
    var breakHasNotAllies = "&cTwoja gildia nie posiada sojuszy!"

    @CfgComment("Dostepne zmienne: {GUILDS}")
    @CfgCollectionStyle(CollectionStyle.ALWAYS_NEW_LINE)
    var breakAlliesList = Arrays.asList(
        "&7Twoja gildia nawiazala sojusz z &a{GUILDS}",
        "&7Aby rozwiazac sojusz wpisz &c/rozwiaz [tag]"
    )

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var breakAllyExists = "&7Twoja gildia nie posiada sojuszu z gildia (&c{TAG}&7&c{GUILD}&7)!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var breakDone = "&7Rozwiazano sojusz z gildia &c{GUILD}&7!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var breakIDone = "&7Gildia &c{GUILD} &7rozwiazala sojusz z Twoja gildia!"

    @CfgComment("<------- Validity Messages -------> #")
    @CfgComment("Dostepne zmienne: {TIME}")
    var validityWhen = "&7Gildie mozesz przedluzyc dopiero za &c{TIME}&7!"

    @CfgComment("Dostepne zmienne: {ITEM}")
    var validityItems = "&7Nie masz wystarczajacej liczby przedmiotow! Potrzebujesz &c{ITEM}"

    @CfgComment("Dostepne zmienne: {DATE}")
    var validityDone = "&7Waznosc gildii przedluzona do &a{DATE}&7!"

    @CfgComment("<------- War Messages -------> #")
    var warDisabled = "&cPodbijanie gildii jest wyłączone."
    var warHasNotGuild = "&cMusisz miec gildie, aby zaatkowac inna!"
    var warAlly = "&cNie mozesz zaatakowac sojusznika!"

    @CfgComment("Dostepne zmienne: {TIME}")
    var warWait = "&7Atak na gildie mozliwy za &4{TIME}"

    @CfgComment("Dostepne zmienne: {ATTACKED}")
    var warAttacker = "&7Twoja gildia pozbawila gildie &4{ATTACKED} &7z &41 zycia&7!"

    @CfgComment("Dostepne zmienne: {ATTACKER}")
    var warAttacked = "&7Twoja gildia stracila &41 zycie &7przez &4{ATTACKER}&7!"

    @CfgComment("Dostepne zmienne: {LOSER}")
    var warWin = "&7Twoja gildia &apodbila &7gildie &a{LOSER}&7! Zyskujecie &c1 zycie&7!"

    @CfgComment("Dostepne zmienne: {WINNER}")
    var warLose = "&7Twoja gildia &4przegrala &7wojne z gildia &4{WINNER}&7! &4Gildia zostaje zniszona&7!"

    @CfgComment("<------- Leader Messages -------> #")
    var leaderMustBeDifferent = "&cNie mozesz sobie oddac zalozyciela!"
    var leaderSet = "&7Ustanowiono nowego &alidera &7gildii!"
    var leaderOwner = "&7Zostales nowym &aliderem &7gildii!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var leaderMembers = "&7{PLAYER} zostal nowym &aliderem &7gildii!"

    @CfgComment("<------- TNT Hours Messages -------> #")
    var tntInfo = "&7TNT na teranach gildii działa od {PROTECTION_END} do {PROTECTION_START}"
    var tntProtectDisable = "&7TNT wybucha o każdej porze."
    var tntNowEnabled = "&aTNT aktualnie jest włączone."
    var tntNowDisabled = "&cTNT aktualnie jest wyłączone."

    @CfgComment("<------- Deputy Messages -------> #")
    var deputyMustBeDifferent = "&cNie mozesz mianowac siebie zastepca!"
    var deputyRemove = "&7Zdegradowno gracza z funkcji &czastepcy&7!"
    var deputyMember = "&7Zdegradowano Cie z funkcji &czastepcy&7!"
    var deputySet = "&7Ustanowiono nowego &azastepce &7gildii!"
    var deputyOwner = "&7Zostales nowym &azastepca &7gildii!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var deputyMembers = "&7{PLAYER} zostal nowym &azastepca &7gildii!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var deputyNoLongerMembers = "&7{PLAYER} juz nie jest &azastepca &7gildii!"

    @CfgComment("<------- Setbase Messages -------> #")
    var setbaseOutside = "&cNie mozna ustawic domu gildii poza jej terenem!"
    var setbaseDone = "&7Przeniesiono &adom &7gildii!"

    @CfgComment("<------- PvP Messages -------> #")
    var pvpOn = "&cWlaczono pvp w gildii!"
    var pvpOff = "&aWylaczono pvp w gildii!"

    @CfgComment("Dostepne zmienne: {TAG}")
    var pvpAllyOn = "&cWlaczono pvp z sojuszem &7{TAG}!"
    var pvpAllyOff = "&cWylaczono pvp z sojuszem &7{TAG}!"

    @CfgComment("<------- Admin Messages -------> #")
    @CfgComment("Dostepne zmienne: {ADMIN}")
    var adminGuildBroken = "&cTwoja gildia zostala rozwiazana przez &7{ADMIN}"
    var adminGuildOwner = "&cTen gracz jest zalozycielem gildii, nie mozna go wyrzucic!"
    var adminNoRegionFound = "&cGildia nie posiada terenu!"
    var adminNoPointsGiven = "&cPodaj liczbe punktow!"

    @CfgComment("Dostepne zmienne: {ERROR}")
    var adminErrorInNumber = "&cNieznana jest liczba: {ERROR}"

    @CfgComment("Dostepne zmienne: {PLAYER}, {POINTS}, {POINTS-FORMAT}")
    var adminPointsChanged = "&aUstawiono &7{POINTS} &apunktow dla gracza &7{PLAYER}"
    var adminNoKillsGiven = "&cPodaj liczbe zabojstw!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {KILLS}")
    var adminKillsChanged = "&aUstawiono &7{KILLS} &azabojstw dla gracza &7{PLAYER}"
    var adminNoDeathsGiven = "&cPodaj liczbe zgonow!"

    @CfgComment("Dostepne zmienne: {PLAYER}, {DEATHS}")
    var adminDeathsChanged = "&aUstawiono &7{DEATHS} &azgonow dla gracza &7{PLAYER}"
    var adminNoBanTimeGiven = "&cPodaj czas na jaki ma byc zbanowana gildia!"
    var adminNoReasonGiven = "&cPodaj powod!"
    var adminGuildBanned = "&cTa gildia jest juz zbanowana!"
    var adminTimeError = "&cPodano nieprawidlowy czas!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TIME}")
    var adminGuildBan = "&aZbanowano gildie &a{GUILD} &7na okres &a{TIME}&7!"
    var adminGuildNotBanned = "&cTa gildia nie jest zbanowana!"

    @CfgComment("Dostepne zmienne: {GUILD}")
    var adminGuildUnban = "&aOdbanowano gildie &7{GUILD}&a!"
    var adminNoLivesGiven = "&cPodaj liczbe zyc!"

    @CfgComment("Dostepne zmienne: {GUILD}, {LIVES}")
    var adminLivesChanged = "&aUstawiono &7{LIVES} &azyc dla gildii &7{GUILD}&a!"

    @CfgComment("Dostepne zmienne: {GUILD}")
    var adminGuildRelocated = "&aPrzeniesiono teren gildii &7{GUILD}&a!"
    var adminNoValidityTimeGiven = "&cPodaj czas o jaki ma byc przedluzona waznosc gildii!"

    @CfgComment("Dostepne zmienne: {GUILD}, {VALIDITY}")
    var adminNewValidity = "&aPrzedluzono waznosc gildii &a{GUILD} &7do &a{VALIDITY}&7!"
    var adminNoNewNameGiven = "&cPodaj nowa nazwe!"

    @CfgComment("Dostepne zmienne: {GUILD}, {TAG}")
    var adminNameChanged = "&aZmieniono nazwe gildii na &7{GUILD}&a!"
    var adminTagChanged = "&aZmieniono tag gildii na &7{TAG}&a!"
    var adminStopSpy = "&cJuz nie szpiegujesz graczy!"
    var adminStartSpy = "&aOd teraz szpiegujesz graczy!"
    var adminGuildsEnabled = "&aZakladanie gildii jest wlaczone!"
    var adminGuildsDisabled = "&cZakladanie gildii jest wylaczone!"
    var adminUserNotMemberOf = "&cTen gracz nie jest czlonkiem tej gildii!"
    var adminAlreadyLeader = "&cTen gracz jest juz liderem gildii!"
    var adminNoAdditionalProtectionDateGiven = "&cPodaj date dodatkowej ochrony dla gildii! (W formacie: yyyy/mm/dd hh:mm:ss)"
    var adminInvalidAdditionalProtectionDate = "&cTo nie jest poprawna data! Poprawny format to: yyyy/mm/dd hh:mm:ss"
    var adminAdditionalProtectionSetSuccessfully = "&aPomyslnie nadano dodatkowa ochrone dla gildii &7{TAG} &ado &7{DATE}"
    var adminGuildHasNoHome = "&cGildia gracza nie ma ustawionej bazy!"

    @CfgComment("Dostepne zmienne: {ADMIN}")
    var adminTeleportedToBase = "&aAdmin &7{ADMIN} &ateleportowal cie do bazy gildii!"

    @CfgComment("Dostepne zmienne: {PLAYER}")
    var adminTargetTeleportedToBase = "&aGracz &7{PLAYER} &azostal teleportowany do bazy gildii!"

    @CfgComment("<------- SecuritySystem Messages -------> #")
    @CfgComment("Przedrostek przed wiadomościami systemu bezpieczeństwa")
    var securitySystemPrefix = "&8[&4Security&8] &7"

    @CfgComment("Dostepne zmienne: {PLAYER}, {CHEAT}")
    var securitySystemInfo = "&7Gracz &c{PLAYER}&7 może używać &c{CHEAT}&7 lub innego cheata o podobnym dzialaniu!"

    @CfgComment("Dostepne zmienne: {NOTE}")
    var securitySystemNote = "Notatka: &7{NOTE}"

    @CfgComment("Dostepne zmienne: {DISTANCE}")
    var securitySystemReach = "&7Zaatakowal krysztal z odleglosci &c{DISTANCE} &7kratek!"

    @CfgComment("Dostepne zmienne: {BLOCKS}")
    var securitySystemFreeCam = "Zaatakowal krysztal przez bloki: &c{BLOCKS}"

    @CfgComment("<------- System Messages -------> #")
    var reloadWarn = "&cDziałanie pluginu FunnyGuilds po reloadzie moze byc zaburzone, zalecane jest przeprowadzenie restartu serwera!"
    fun load() {
        try {
            for (field in this.javaClass.declaredFields) {
                if (field.type == String::class.java) {
                    field[this] = ChatUtils.colored(field[this] as String)
                }
                if (field.type == MutableList::class.java) {
                    val list = field[this] as MutableList<String?>
                    for (i in list.indices) {
                        list[i] = ChatUtils.colored(list[i])
                    }
                }
            }
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not load message configuration", ex)
        }
    }
}