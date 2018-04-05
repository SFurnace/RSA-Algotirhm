package app

import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

object Config {
    private var preferences: Preferences? = null

    val lastUsageTime: Optional<String>
        get() = Optional.ofNullable(preferences!!.get("LastUsageTime", null))

    val lastRSAKeyPath: Optional<Path>
        get() {
            val path = preferences!!.get("LastRSAKeyPath", null)
            return Optional.ofNullable(if (path != null) Paths.get(path) else null)
        }

    init {
        val root = Preferences.userRoot()
        val preferences = root.node("pers.drcz.RSA")
        preferences.put("LastUsageTime", LocalDateTime.now().toString())

        try {
            preferences.sync()
        } catch (e: BackingStoreException) {
            e.printStackTrace()
        }

        Config.preferences = preferences
    }

    internal fun setLastRSAKeyPath(path: Path) {
        preferences!!.put("LastRSAKeyPath", path.toAbsolutePath().toString())
        try {
            preferences!!.sync()
        } catch (e: BackingStoreException) {
            e.printStackTrace()
        }

    }
}
