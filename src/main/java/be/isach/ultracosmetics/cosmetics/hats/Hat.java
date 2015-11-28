package be.isach.ultracosmetics.cosmetics.hats;

import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sacha on 15/10/15.
 */
public enum Hat {

    // Most horrible cosmetic to register.
    ASTRONAUT(ItemFactory.createSkull("M2U4YWFkNjczMTU3YzkyMzE3YTg4YjFmODZmNTI3MWYxY2Q3Mzk3ZDdmYzhlYzMyODFmNzMzZjc1MTYzNCJ9fX0="), "Astronaut", "&7&oHouston, we have got a problem."),
    SCARED(ItemFactory.createSkull("NjM2ZTI2YzQ0NjU5ZTgxNDhlZDU4YWE3OWU0ZDYwZGI1OTVmNDI2NDQyMTE2ZjgxYjU0MTVjMjQ0NmVkOCJ9fX0="), "Scared", "&7&oOh gawd, that scared me!"),
    ANGEL(ItemFactory.createSkull("M2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ=="), "Angel", "&7&oDid it hurt when you fell from heaven?"),
    EMBARASSED(ItemFactory.createSkull("ZjcyMGRmOTExYzA1MjM3NzA2NTQwOGRiNzhhMjVjNjc4Zjc5MWViOTQ0YzA2MzkzNWFlODZkYmU1MWM3MWIifX19"), "Embarassed", "&7&oI am kinda embarassed by that."),
    KISSY(ItemFactory.createSkull("NTQ1YmQxOGEyYWFmNDY5ZmFkNzJlNTJjZGU2Y2ZiMDJiZmJhYTViZmVkMmE4MTUxMjc3Zjc3OWViY2RjZWMxIn19fQ=="), "Kissy", "&7&oWanna kiss?"),
    SAD(ItemFactory.createSkull("MTQ5NjhhYzVhZjMxNDY4MjZmYTJiMGQ0ZGQxMTRmZGExOTdmOGIyOGY0NzUwNTUzZjNmODg4MzZhMjFmYWM5In19fQ=="), "Sad", "&7&oI am so sad."),
    COOL(ItemFactory.createSkull("ODY4ZjRjZWY5NDlmMzJlMzNlYzVhZTg0NWY5YzU2OTgzY2JlMTMzNzVhNGRlYzQ2ZTViYmZiN2RjYjYifX19"), "Cool", "&7&oI am such a cool guy."),
    SURPRISED(ItemFactory.createSkull("YmMyYjliOWFlNjIyYmQ2OGFkZmY3MTgwZjgyMDZlYzQ0OTRhYmJmYTEzMGU5NGE1ODRlYzY5MmU4OTg0YWIyIn19fQ=="), "Surprised", "&7&oWow, did not expect that!"),
    DEAD(ItemFactory.createSkull("YjM3MWU0ZTFjZjZhMWEzNmZkYWUyNzEzN2ZkOWI4NzQ4ZTYxNjkyOTk5MjVmOWFmMmJlMzAxZTU0Mjk4YzczIn19fQ=="), "Dead", "&7&ogot rekt"),
    CRYING(ItemFactory.createSkull("MWYxYjg3NWRlNDljNTg3ZTNiNDAyM2NlMjRkNDcyZmYyNzU4M2ExZjA1NGYzN2U3M2ExMTU0YjViNTQ5OCJ9fX0="), "Crying", "&7&oi cri evrytiem"),
    BIGSMILE(ItemFactory.createSkull("NTA1OWQ1OWViNGU1OWMzMWVlY2Y5ZWNlMmY5Y2YzOTM0ZTQ1YzBlYzQ3NmZjODZiZmFlZjhlYTkxM2VhNzEwIn19fQ=="), "BigSmile", "&7&oUh, because I am really happy!"),
    WINK(ItemFactory.createSkull("ZjRlYTJkNmY5MzlmZWZlZmY1ZDEyMmU2M2RkMjZmYThhNDI3ZGY5MGIyOTI4YmMxZmE4OWE4MjUyYTdlIn19fQ=="), "Wink", "&7&oYou know what I mean ;)"),
    DERP(ItemFactory.createSkull("M2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ=="), "Derp", "&7&oDerp Derp Derping all around"),
    SMILE(ItemFactory.createSkull("NTJlOTgxNjVkZWVmNGVkNjIxOTUzOTIxYzFlZjgxN2RjNjM4YWY3MWMxOTM0YTQyODdiNjlkN2EzMWY2YjgifX19"), "Smile", "&7&oUh, because I am happy"),
    IRON(ItemFactory.createSkull("YmJhODQ1OTE0NWQ4M2ZmYzQ0YWQ1OGMzMjYwZTc0Y2E1YTBmNjM0YzdlZWI1OWExYWQzMjM0ODQ5YzkzM2MifX19"), "Iron", "&7&oAs hard as iron!"),
    GOLD(ItemFactory.createSkull("YjZkMWNlNjk3ZTlkYmFhNGNjZjY0MjUxNmFhYTU5ODEzMzJkYWMxZDMzMWFmZWUyZWUzZGNjODllZmRlZGIifX19"), "Gold", "&7&oMy precious!"),
    DIAMOND(ItemFactory.createSkull("YzAxNDYxOTczNjM0NTI1MTk2ZWNjNzU3NjkzYjE3MWFkYTRlZjI0YWE5MjgzNmY0MmVhMTFiZDc5YzNhNTAyZCJ9fX0="), "Diamond", "&7&oThis is really strong!"),
    PISTON(ItemFactory.createSkull("YWE4NjhjZTkxN2MwOWFmOGU0YzM1MGE1ODA3MDQxZjY1MDliZjJiODlhY2E0NWU1OTFmYmJkN2Q0YjExN2QifX19"), "Piston", "&7&oHave you got the redstone?"),
    COMMANDBLOCK(ItemFactory.createSkull("ODUxNGQyMjViMjYyZDg0N2M3ZTU1N2I0NzQzMjdkY2VmNzU4YzJjNTg4MmU0MWVlNmQ4YzVlOWNkM2JjOTE0In19fQ=="), "CommandBlock", "&7&oControl the world with it!"),
    MUSIC(ItemFactory.createSkull("NGNlZWI3N2Q0ZDI1NzI0YTljYWYyYzdjZGYyZDg4Mzk5YjE0MTdjNmI5ZmY1MjEzNjU5YjY1M2JlNDM3NmUzIn19fQ=="), "Music", "&7&oYou are so musical."),
    SQUID(ItemFactory.createSkull("MDE0MzNiZTI0MjM2NmFmMTI2ZGE0MzRiODczNWRmMWViNWIzY2IyY2VkZTM5MTQ1OTc0ZTljNDgzNjA3YmFjIn19fQ=="), "Squid", "&7&oBloop Bloop!"),
    CHICKEN(ItemFactory.createSkull("MTYzODQ2OWE1OTljZWVmNzIwNzUzNzYwMzI0OGE5YWIxMWZmNTkxZmQzNzhiZWE0NzM1YjM0NmE3ZmFlODkzIn19fQ=="), "Chicken", "&7&oBwwaaaaaaaaaaaakkkkk!"),
    PIG(ItemFactory.createSkull("NjIxNjY4ZWY3Y2I3OWRkOWMyMmNlM2QxZjNmNGNiNmUyNTU5ODkzYjZkZjRhNDY5NTE0ZTY2N2MxNmFhNCJ9fX0="), "Pig", "&7&oOink Oink!"),
    BLAZE(ItemFactory.createSkull("Yjc4ZWYyZTRjZjJjNDFhMmQxNGJmZGU5Y2FmZjEwMjE5ZjViMWJmNWIzNWE0OWViNTFjNjQ2Nzg4MmNiNWYwIn19fQ=="), "Blaze", "&7&oWatch out for the fire!"),
    SHEEP(ItemFactory.createSkull("ZjMxZjljY2M2YjNlMzJlY2YxM2I4YTExYWMyOWNkMzNkMThjOTVmYzczZGI4YTY2YzVkNjU3Y2NiOGJlNzAifX19"), "Sheep", "&7&oBaaaa, baa"),
    GOLEM(ItemFactory.createSkull("ODkwOTFkNzllYTBmNTllZjdlZjk0ZDdiYmE2ZTVmMTdmMmY3ZDQ1NzJjNDRmOTBmNzZjNDgxOWE3MTQifX19"), "Golem", "&7&oI am your guard."),
    ENDERMAN(ItemFactory.createSkull("N2E1OWJiMGE3YTMyOTY1YjNkOTBkOGVhZmE4OTlkMTgzNWY0MjQ1MDllYWRkNGU2YjcwOWFkYTUwYjljZiJ9fX0="), "Enderman", "&7&oNow I am here, now I am there."),
    MARIO(ItemFactory.createSkull("ZGJhOGQ4ZTUzZDhhNWE3NTc3MGI2MmNjZTczZGI2YmFiNzAxY2MzZGU0YTliNjU0ZDIxM2Q1NGFmOTYxNSJ9fX0="), "Mario", "&7&oIt is me! Mario!"),
    LUIGI(ItemFactory.createSkull("ZmYxNTMzODcxZTQ5ZGRhYjhmMWNhODJlZGIxMTUzYTVlMmVkMzc2NGZkMWNlMDI5YmY4MjlmNGIzY2FhYzMifX19"), "Luigi", "&7&oLuigi time!"),
    BATMAN(ItemFactory.createSkull("ZjI1NmY3MTczNWVmNDU4NTgxYzlkYWNmMzk0MTg1ZWVkOWIzM2NiNmVjNWNkNTk0YTU3MTUzYThiNTY2NTYwIn19fQ=="), "Batman", "&7&oI am batman!"),
    CHEST(ItemFactory.createSkull("NmY2OGQ1MDliNWQxNjY5Yjk3MWRkMWQ0ZGYyZTQ3ZTE5YmNiMWIzM2JmMWE3ZmYxZGRhMjliZmM2ZjllYmYifX19"), "Chest", "&7&oOpen, and close"),
    SKULL(ItemFactory.createSkull("MTFmNTRmZjliYjQyODUxOTEyYWE4N2ExYmRhNWI3Y2Q5ODE0Y2NjY2ZiZTIyNWZkZGE4ODdhZDYxODBkOSJ9fX0="), "Skull", "&7&oWho iss headless now?"),
    GHOST(ItemFactory.createSkull("NjhkMjE4MzY0MDIxOGFiMzMwYWM1NmQyYWFiN2UyOWE5NzkwYTU0NWY2OTE2MTllMzg1NzhlYTRhNjlhZTBiNiJ9fX0="), "Ghost", "&7&o2spooky4u"),
    JACKOLANTERN(ItemFactory.createSkull("MDI4OWQ0YjRjOTYyOTU5MTVmMDY4Yjk5YzI3ZDM5NDI3M2Y5ZjI2NGZjOTY4YzVkNWM0N2RmMmY1YmUyIn19fQ=="), "JackOLantern", "&7&oA little pumkin"),
    SCARYCLOW(ItemFactory.createSkull("ODZkYmMxZGViYzU3NDM4YTVkZTRiYTkxNTE1MTM4MmFiYzNkOGYxMzE4ZTJhMzVlNzhkZmIzMGYwNGJjNDY3In19fQ=="), "ScaryClown", "&7&oHope you are not scared of clowns."),
    SANTA(ItemFactory.createSkull("MmQ2MWNjYmZkY2RmODk0MWFkYWY3NmM2YzBlMDE4MmQyYzhiYmI1ZGMxOGYzNzQ4OTU2NTJiYzY2MWI2ZWQifX19"), "Santa", "&7&oOh oh oh! Merry Christmas!"),
    SNOWMAN(ItemFactory.createSkull("OThlMzM0ZTRiZWUwNDI2NDc1OWE3NjZiYzE5NTVjZmFmM2Y1NjIwMTQyOGZhZmVjOGQ0YmYxYmIzNmFlNiJ9fX0="), "Snowman", "&7&oI don't have a skull.. or bones"),
    PRESENT(ItemFactory.createSkull("ZjBhZmE0ZmZmZDEwODYzZTc2YzY5OGRhMmM5YzllNzk5YmNmOWFiOWFhMzdkODMxMjg4MTczNDIyNWQzY2EifX19"), "Present", "&7&oFrom Santa, to you!"),
    ELF(ItemFactory.createSkull("ODJhYjZjNzljNjNiODMzNGIyYzAzYjZmNzM2YWNmNjFhY2VkNWMyNGYyYmE3MmI3NzdkNzdmMjhlOGMifX19"), "Elf", "&7&oI work for Santa!");

    private String configName;
    private ItemStack itemStack;
    private String permission;
    private String description;

    Hat(ItemStack itemStack, String configName, String defaultDesc) {
        this.itemStack = itemStack;
        this.configName = configName;
        this.permission = configName.toLowerCase();
        if (SettingsManager.getConfig().get("Hats." + configName + ".Description") == null) {
            this.description = defaultDesc;
            SettingsManager.getConfig().set("Hats." + configName + ".Description", getDescription());
        } else {
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Hats." + configName + ".Description")));
        }
    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Hats." + configName + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Hats." + configName + ".Can-Be-Found-In-Treasure-Chests");
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public String getPermission() {
        return "ultracosmetics.hats." + permission;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean isEnabled() {
        return SettingsManager.getConfig().getBoolean("Hats." + configName + ".Enabled");
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return MessageManager.getMessage("Hats." + configName + ".Name");
    }

}
