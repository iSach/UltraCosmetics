package be.isach.ultracosmetics.player.profile;

public enum ProfileKey {
    KEYS("Keys", "treasureKeys"),
    GADGETS_ENABLED("Gadgets-Enabled", "gadgetsEnabled"),
    MORPH_VIEW("Third-Person-Morph-View", "selfmorphview"),
    TREASURE_NOTIFICATION("Treasure-Notifications", "treasureNotifications"),
    FILTER_OWNED("Filter-By-Owned", "filterByOwned"),
    PET_NAMES("Pet-Names", null),
    AMMO("Ammo", null);
    ;
    private final String file;
    private final String sql;
    private ProfileKey(String file, String sql) {
        this.file = file;
        this.sql = sql;
    }

    public String getFileKey() {
        return file;
    }

    public String getSqlKey() {
        return sql;
    }

    public static ProfileKey fromString(String name) {
        for (ProfileKey key : values()) {
            if (key.toString().equalsIgnoreCase(name)
                    || key.getFileKey().equalsIgnoreCase(name)
                    || key.getSqlKey().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }
}
