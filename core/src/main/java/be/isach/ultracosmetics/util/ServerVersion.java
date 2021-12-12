package be.isach.ultracosmetics.util;

/**
 * Created by Sacha on 6/03/16.
 */
public enum ServerVersion {

    v1_8_R1("1.8", null),
    v1_8_R2("1.8.3", null),
    v1_8_R3("1.8.8", null),
    v1_9_R1("1.9.2", null),
    v1_9_R2("1.9.4", null),
    v1_10_R1("1.10.2", null),
    v1_11_R1("1.11.2", null),
    v1_12_R1("1.12.2", null),
    v1_13_R1("1.13", null),
    v1_13_R2("1.13.2", "7dd4b3ec31629620c41553e5c142e454"),
    v1_14_R1("1.14.4", "11ae498d9cf909730659b6357e7c2afa"),
    v1_15_R1("1.15.2", "5684afcc1835d966e1b6eb0ed3f72edb"),
    v1_16_R1("1.16.1", "25afc67716a170ea965092c1067ff439"),
    v1_16_R2("1.16.3", "09f04031f41cb54f1077c6ac348cc220"),
    v1_16_R3("1.16.5", "d4b392244df170796f8779ef0fc1f2e9"),
    v1_17_R1("1.17.1", "f0e3dfc7390de285a4693518dd5bd126"),
    v1_18_R1("1.18.1", "20b026e774dbf715e40a0b2afe114792"),
    ;

    private String name;
    // mappingsVersion is a random string that is changed whenever NMS changes
    // which is more often than actual NMS revisions happen. You can find this
    // value by checking the source code of this method:
    // org.bukkit.craftbukkit.util.CraftMagicNumbers#getMappingsVersion
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
}
