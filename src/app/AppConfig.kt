package app

import java.nio.file.Path
import java.nio.file.Paths
import java.util.prefs.Preferences
import kotlin.system.exitProcess

object AppConfig {
    private val preferences: Preferences

    init {
        try {
            preferences = Preferences.userRoot().node("pers.drcz.RSA")
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(-1)
        }
    }

    var lastRSAKeyPath: Path?
        get() = Paths.get(preferences.get("LastRSAKeyPath", null))
        set(value) {
            preferences.put("LastRSAKeyPath", value?.toAbsolutePath().toString())
            preferences.sync()
        }
}
