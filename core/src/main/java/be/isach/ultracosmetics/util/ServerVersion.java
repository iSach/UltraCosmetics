package be.isach.ultracosmetics.util;

/**
 * Created by Sacha on 6/03/16.
 */
public enum ServerVersion {

    v1_8_R1("1.8"),
    v1_8_R2("1.8.3"),
    v1_8_R3("1.8.8"),
    v1_9_R1("1.9"),
    v1_9_R2("1.9.4"),
    v1_10_R1("1.10"),
    v1_11_R1("1.11"),
    v1_12_R1("1.12"),
    v1_13_R1("1.13"),
    v1_13_R2("1.13.2"),
    v1_14_R1("1.14.4"),
    v1_15_R1("1.15.2"),
    v1_16_R1("1.16.1"),
    v1_16_R2("1.16.2"),
    v1_16_R3("1.16.4");

    String name;

    ServerVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
