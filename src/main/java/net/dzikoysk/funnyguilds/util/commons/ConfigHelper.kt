package net.dzikoysk.funnyguilds.util.commons

import net.dzikoysk.funnyguilds.util.nms.Reflections
import org.apache.commons.lang3.Validate
import org.diorite.cfg.system.TemplateCreator
import java.io.File
import java.io.IOException
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

object ConfigHelper {
    fun <T> loadConfig(file: File, implementationFile: Class<T>): T? {
        val implementationFileConstructor = Reflections.getConstructor(implementationFile) as Constructor<T>
        val template = TemplateCreator.getTemplate(implementationFile)
        var config: T?
        if (!file.exists()) {
            try {
                config = try {
                    template.fillDefaults(implementationFileConstructor.newInstance())
                } catch (e: InstantiationException) {
                    throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
                } catch (e: IllegalArgumentException) {
                    throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
                }
                Validate.isTrue(file.createNewFile(), "Couldn't create " + file.absolutePath + " config file")
            } catch (e: IOException) {
                throw RuntimeException("IO exception when creating config file: " + file.absolutePath, e)
            }
        } else {
            try {
                try {
                    config = template.load(file)
                    if (config == null) {
                        config = template.fillDefaults(implementationFileConstructor.newInstance())
                    }
                } catch (e: IOException) {
                    throw RuntimeException("IO exception when loading config file: " + file.absolutePath, e)
                } catch (e: IllegalArgumentException) {
                    throw RuntimeException("IO exception when loading config file: " + file.absolutePath, e)
                } catch (e: InvocationTargetException) {
                    throw RuntimeException("IO exception when loading config file: " + file.absolutePath, e)
                }
            } catch (e: InstantiationException) {
                throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Couldn't get access to " + implementationFile.name + "  constructor", e)
            }
        }
        try {
            template.dump(file, config, false)
        } catch (e: IOException) {
            throw RuntimeException("Can't dump configuration file!", e)
        }
        return config
    }

    fun configToString(config: Any?): String {
        val template = TemplateCreator.getTemplate(config!!.javaClass as Class<Any?>)
        return template.dumpAsString(config)
    }
}