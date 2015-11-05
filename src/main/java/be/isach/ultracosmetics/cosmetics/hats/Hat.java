package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Sacha on 15/10/15.
 */
public enum Hat {

    // La joie que j'ai eue à register chaque tête
    ASTRONAUT(ItemFactory.createSkull("M2U4YWFkNjczMTU3YzkyMzE3YTg4YjFmODZmNTI3MWYxY2Q3Mzk3ZDdmYzhlYzMyODFmNzMzZjc1MTYzNCJ9fX0="), "Astronaut"),
    SCARED(ItemFactory.createSkull("NjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0="), "Scared"),
    ANGEL(ItemFactory.createSkull("M2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ=="), "Angel"),
    EMBARASSED(ItemFactory.createSkull("ZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19"), "Embarassed"),
    KISSY(ItemFactory.createSkull("NTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ=="), "Kissy"),
    SAD(ItemFactory.createSkull("MTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ=="), "Sad"),
    COOL(ItemFactory.createSkull("ODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19"), "Cool"),
    SURPRISED(ItemFactory.createSkull("YmMyYjliOWFlNjIyYmQ2OGFkZmY3MTgwZjgyMDZlYzQ0OTRhYmJmYTEzMGU5NGE1ODRlYzY5MmU4OTg0YWIyIn19fQ=="), "Surprised"),
    DEAD(ItemFactory.createSkull("YjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ=="), "Dead"),
    CRYING(ItemFactory.createSkull("MWYxYjg3NWRlNDljNTg3ZTNiNDAyM2NlMjRkNDcyZmYyNzU4M2ExZjA1NGYzN2U3M2ExMTU0YjViNTQ5OCJ9fX0="), "Crying"),
    BIGSMILE(ItemFactory.createSkull("NTA1OWQ1OWViNGU1OWMzMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzEwIn19fQ=="), "BigSmile"),
    WINK(ItemFactory.createSkull("ZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ=="), "Wink"),
    DERP(ItemFactory.createSkull("M2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ=="), "Derp"),
    SMILE(ItemFactory.createSkull("NTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19"), "Smile"),
    IRON(ItemFactory.createSkull("YmJhODQ1OTE0NWQ4M2ZmYzQ0YWQ1OGMzMjYwZTc0Y2E1YTBmNjM0YzdlZWI1OWExYWQzMjM0ODQ5YzkzM2MifX19"), "Iron"),
    GOLD(ItemFactory.createSkull("YjZkMWNlNjk3ZTlkYmFhNGNjZjY0MjUxNmFhYTU5ODEzMzJkYWMxZDMzMWFmZWUyZWUzZGNjODllZmRlZGIifX19"), "Gold"),
    DIAMOND(ItemFactory.createSkull("YzAxNDYxOTczNjM0NTI1MTk2ZWNjNzU3NjkzYjE3MWFkYTRlZjI0YWE5MjgzNmY0MmVhMTFiZDc5YzNhNTAyZCJ9fX0="), "Diamond"),
    PISTON(ItemFactory.createSkull("YWE4NjhjZTkxN2MwOWFmOGU0YzM1MGE1ODA3MDQxZjY1MDliZjJiODlhY2E0NWU1OTFmYmJkN2Q0YjExN2QifX19"), "Piston"),
    COMMANDBLOCK(ItemFactory.createSkull("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ=="), "CommandBlock"),
    MUSIC(ItemFactory.createSkull("NGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ=="), "Music"),
    SQUID(ItemFactory.createSkull("MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="), "Squid"),
    CHICKEN(ItemFactory.createSkull("MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="), "Chicken"),
    PIG(ItemFactory.createSkull("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="), "Pig"),
    BLAZE(ItemFactory.createSkull("Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="), "Blaze"),
    SHEEP(ItemFactory.createSkull("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"), "Sheep"),
    GOLEM(ItemFactory.createSkull("ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"), "Golem"),
    ENDERMAN(ItemFactory.createSkull("N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="), "Enderman"),
    MARIO(ItemFactory.createSkull("ZGJhOGQ4ZTUzZDhhNWE3NTc3MGI2MmNjZTczZGI2YmFiNzAxY2MzZGU0YTliNjU0ZDIxM2Q1NGFmOTYxNSJ9fX0="), "Mario"),
    LUIGI(ItemFactory.createSkull("ZmYxNTMzODcxZTQ5ZGRhYjhmMWNhODJlZGIxMTUzYTVlMmVkMzc2NGZkMWNlMDI5YmY4MjlmNGIzY2FhYzMifX19"), "Luigi"),
    BATMAN(ItemFactory.createSkull("ZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ=="), "Batman"),
    CHEST(ItemFactory.createSkull("NmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19"), "Chest"),
    SKULL(ItemFactory.createSkull("MTFmNTRmZjliYjQyODUxOTEyYWE4N2ExYmRhNWI3Y2Q5ODE0Y2NjY2ZiZTIyNWZkZGE4ODdhZDYxODBkOSJ9fX0="), "Skull"),
    GHOST(ItemFactory.createSkull("NjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0="), "Ghost"),
    JACKOLANTERN(ItemFactory.createSkull("MDI4OWQ0YjRjOTYyOTU5MTVmMDY4Yjk5YzI3ZDM5NDI3M2Y5ZjI2NGZjOTY4YzVkNWM0N2RmMmY1YmUyIn19fQ=="), "JackOLantern"),
    SCARYCLOWN(ItemFactory.createSkull("ODZkYmMxZGViYzU3NDM4YTVkZTRiYTkxNTE1MTM4MmFiYzNkOGYxMzE4ZTJhMzVlNzhkZmIzMGYwNGJjNDY3In19fQ=="), "ScaryClown");

    private String configName;
    private ItemStack itemStack;
    private String permission;

    Hat(ItemStack itemStack, String configName) {
        this.itemStack = itemStack;
        this.configName = configName;
        this.permission = configName.toLowerCase();
    }

    public String getPermission() {
        return "ultracosmetics.hats." + permission;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().get("Hats." + configName + ".Enabled");
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return MessageManager.getMessage("Hats." + configName + ".Name");
    }

}
