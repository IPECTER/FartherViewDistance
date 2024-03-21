package xuan.cat.fartherviewdistance.code;

import org.bukkit.Bukkit;

import java.util.List;

public class VersionUtil {

    private static final String VERSION_STR = fromAPI(Bukkit.getBukkitVersion());
    private static final Version VERSION = new Version(VERSION_STR);

    public static String fromAPI(String version) {
        return version.split("-")[0];
    }

    /***
     * if you use Bukkit#getBukkitVersion, use fromAPI
     * @param minVersion ex) 1.14.0
     * @param maxVersion ex) 1.21.0
     * @return isSupportVersion
     */
    public static boolean isSupportVersion(String minVersion, String maxVersion) {
        Version min = new Version(minVersion);
        Version max = new Version(maxVersion);
        return min.getVersion() <= VERSION.getVersion() && max.getVersion() >= VERSION.getVersion();
    }

    public static boolean isSupportVersion(List<String> versions) {
        return versions.contains(VERSION_STR);
    }

    /***
     * check server is > 1.14.4
     * @return isSupportVersion
     */
    public static boolean isSupportVersion(String minVersion) {
        Version min = new Version(minVersion);
        return min.getVersion() <= VERSION.getVersion();
    }

    @Deprecated
    public static boolean isLegacy() {
        return hasClass("net.minecraft.server.MinecraftServer");
    }

    public static boolean isPaper() {
        return hasClass("com.destroystokyo.paper.PaperConfig") || hasClass("io.papermc.paper.configuration.Configuration");
    }

    private static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }


    public static String getVersionStr() {
        return VERSION_STR;
    }

    public static Version getVersion() {
        return VERSION;
    }

    public static String getNMSVersion(String versionStr) {
        Version version = new Version(versionStr);
        return switch (version.getVersion()) {
            case 1171 -> "v1_17_R1";
            case 1180, 1181 -> "v1_18_R1";
            case 1182 -> "v1_18_R2";
            case 1190, 1191, 1192 -> "v1_19_R1";
            case 1193 -> "v1_19_R2";
            case 1194 -> "v1_19_R3";
            case 1200, 1201 -> "v1_20_R1";
            case 1202 -> "v1_20_R2";
            case 1203, 1204 -> "v1_20_R3";
            default -> "";
        };
    }

    public static class Version {

        private final int major;
        private final int minor;
        private final int patch;
        private final int version;

        public int getVersion() {
            return this.version;
        }

        public Version(String version) {
            String[] numbers = version.split("\\.");
            String majorStr = numbers.length == 0 ? "0" : numbers[0];
            String minorStr = numbers.length <= 1 ? "00" : numbers[1];
            String patchStr = numbers.length <= 2 ? "0" : numbers[2];
            this.major = Integer.parseInt(majorStr);
            this.minor = Integer.parseInt(minorStr);
            this.patch = Integer.parseInt(patchStr);
            this.version = Integer.parseInt(majorStr + minorStr + patchStr);
        }

    }


}
