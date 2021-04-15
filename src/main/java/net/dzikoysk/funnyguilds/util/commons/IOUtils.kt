package net.dzikoysk.funnyguilds.util.commons

import com.google.common.base.Throwables
import net.dzikoysk.funnyguilds.FunnyGuilds
import java.io.*
import java.net.URL

object IOUtils {
    fun initialize(file: File, b: Boolean): File {
        if (!file.exists()) {
            try {
                file.parentFile.mkdirs()
                if (b) {
                    file.createNewFile()
                } else {
                    file.mkdir()
                }
            } catch (ex: IOException) {
                FunnyGuilds.Companion.getPluginLogger().error("Could not initialize file: " + file.absolutePath, ex)
            }
        }
        return file
    }

    fun getContent(s: String): String? {
        var body: String? = null
        var `in`: InputStream? = null
        try {
            val con = URL(s).openConnection()
            con.setRequestProperty("User-Agent", "Mozilla/5.0")
            `in` = con.getInputStream()
            var encoding = con.contentEncoding
            encoding = encoding ?: "UTF-8"
            body = toString(`in`, encoding)
            `in`.close()
        } catch (ex: Exception) {
            FunnyGuilds.Companion.getPluginLogger().update("Connection to the update server ($s) failed!")
            FunnyGuilds.Companion.getPluginLogger().update("Reason: " + Throwables.getStackTraceAsString(ex))
        } finally {
            close(`in`)
        }
        return body
    }

    fun getFile(s: String?, folder: Boolean): File {
        val file = File(s)
        try {
            if (!file.exists()) {
                if (folder) {
                    file.mkdirs()
                } else {
                    file.parentFile.mkdirs()
                    file.createNewFile()
                }
            }
        } catch (exception: Exception) {
            FunnyGuilds.Companion.getPluginLogger().error("The file could not be created!", exception)
        }
        return file
    }

    fun delete(f: File) {
        if (!f.exists()) {
            return
        }
        if (f.isDirectory) {
            for (c in f.listFiles()) {
                delete(c)
            }
        }
        if (!f.delete()) {
            try {
                throw FileNotFoundException("Failed to delete file: $f")
            } catch (exception: FileNotFoundException) {
                FunnyGuilds.Companion.getPluginLogger().error("The file could not be deleted!", exception)
            }
        }
    }

    @Throws(IOException::class)
    fun toString(`in`: InputStream?, encoding: String?): String {
        val baos = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        var len: Int
        while (`in`!!.read(buf).also { len = it } != -1) {
            baos.write(buf, 0, len)
        }
        return baos.toString(encoding)
    }

    fun close(closeable: Closeable?) {
        if (closeable == null) {
            return
        }
        try {
            closeable.close()
        } catch (exception: IOException) {
            FunnyGuilds.Companion.getPluginLogger().error("Could not close IO", exception)
        }
    }
}