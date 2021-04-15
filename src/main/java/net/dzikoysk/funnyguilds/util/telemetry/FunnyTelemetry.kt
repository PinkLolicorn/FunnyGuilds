package net.dzikoysk.funnyguilds.util.telemetry

import com.google.gson.Gson
import net.dzikoysk.funnyguilds.util.commons.IOUtils
import org.diorite.utils.network.DioriteURLUtils
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

/**
 * TODO: Move this to a separate library.
 */
object FunnyTelemetry {
    private val gson = Gson()
    const val URL = "https://funnytelemetry.dzikoysk.net"
    const val FUNNYBIN_POST = URL + "/funnybin/api/post"
    const val FUNNYBIN_POST_BUNDLE = URL + "/funnybin/api/bundle/post"
    @Throws(IOException::class)
    fun postToFunnybin(paste: String?, pasteType: PasteType, tag: String?): FunnybinResponse {
        return sendPost(FUNNYBIN_POST + "?type=" + pasteType.toString() + "&tag=" + DioriteURLUtils.encodeUTF8(tag), paste, FunnybinResponse::class.java)
    }

    @Throws(IOException::class)
    fun createBundle(pastes: List<String?>): FunnybinResponse? {
        if (pastes.isEmpty()) {
            return null
        }
        val iterator = pastes.iterator()
        val url = StringBuilder(DioriteURLUtils.createQueryElement("paste", iterator.next()))
        while (iterator.hasNext()) {
            url.append("&")
            DioriteURLUtils.addQueryElement("paste", iterator.next(), url)
        }
        return sendPost(FUNNYBIN_POST_BUNDLE + "?" + url.toString(), "", FunnybinResponse::class.java)
    }

    @Throws(IOException::class)
    private fun <T> sendPost(url: String, body: String?, response: Class<T>): T {
        System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2")
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2")
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doInput = true
        connection.doOutput = true
        val bodyBytes = body!!.toByteArray(StandardCharsets.UTF_8)
        connection.addRequestProperty("User-Agent", "FunnyGuilds")
        connection.addRequestProperty("Content-Type", "text/plain")
        connection.setRequestProperty("Content-Length", bodyBytes.size.toString())
        connection.outputStream.write(bodyBytes)
        return gson.fromJson(IOUtils.toString(connection.inputStream, "UTF-8"), response)
    }
}