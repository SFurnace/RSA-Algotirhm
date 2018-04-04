package app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class Config {
    private static Preferences preferences;

    static {
        Preferences root = Preferences.userRoot();
        Preferences preferences = root.node("pers.drcz.RSA");
        preferences.put("LastUsageTime", LocalDateTime.now().toString());

        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

        Config.preferences = preferences;
    }

    public static Optional<String> getLastUsageTime() {
        return Optional.ofNullable(preferences.get("LastUsageTime", null));
    }

    public static Optional<Path> getLastRSAKeyPath() {
        String path = preferences.get("LastRSAKeyPath", null);
        return Optional.ofNullable((path != null) ? Paths.get(path) : null);
    }

    static void setLastRSAKeyPath(Path path) {
        preferences.put("LastRSAKeyPath", path.toAbsolutePath().toString());
        try {
            preferences.sync();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}
