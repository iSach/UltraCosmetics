package be.isach.ultracosmetics.util;

/**
 * Created by Sacha on 6/03/16.
 */
public enum ServerVersion {

    v1_8_R1("1.8 -> 1.8.3"),
    v1_8_R2("1.8.4"),
    v1_8_R3("1.8.8"),
    v1_9_R1("1.9 -> 1.9.2"),
    v1_9_R2("1.9.4");

    String name;

    ServerVersion(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
