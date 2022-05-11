package be.isach.ultracosmetics.util;

/**
 * Created by Sacha on 6/03/16.
 */
public enum ServerVersion {

    v1_8_R3("1.8.8", null),
    v1_12_R1("1.12.2", null),
    v1_16_R3("1.16.5", "d4b392244df170796f8779ef0fc1f2e9"),
    v1_17_R1("1.17.1", "f0e3dfc7390de285a4693518dd5bd126"),
    v1_18_R2("1.18.2", "eaeedbff51b16ead3170906872fda334"),
    ;

    private String name;
    // mappingsVersion is a random string that is changed whenever NMS changes
    // which is more often than actual NMS revisions happen. You can find this
    // value by checking the source code of this method:
    // org.bukkit.craftbukkit.util.CraftMagicNumbers#getMappingsVersion
    // https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/util/CraftMagicNumbers.java#240
    // getMappingsVersion was added in 1.13.2, earlier versions don't have it.
    private String mappingsVersion;

    private ServerVersion(String name, String mappingsVersion) {
        this.name = name;
        this.mappingsVersion = mappingsVersion;
    }

    public String getName() {
        return name;
    }

    public String getMappingsVersion() {
        return mappingsVersion;
    }

    public static ServerVersion earliest() {
        return values()[0];
    }

    public static ServerVersion latest() {
        return values()[values().length - 1];
    }

    public boolean isAtLeast(ServerVersion version) {
        return this.compareTo(version) >= 0;
    }

    public boolean offhandAvailable() {
        return isAtLeast(v1_12_R1);
    }
}
