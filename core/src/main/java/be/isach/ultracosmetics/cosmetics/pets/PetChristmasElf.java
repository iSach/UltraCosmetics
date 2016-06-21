package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.util.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Sacha on 29/11/15.
 */
public class PetChristmasElf extends Pet {

    private static List<ItemStack> presents = new ArrayList<>();

    static {
        presents.add(ItemFactory.createSkull("ZjU2MTJkYzdiODZkNzFhZmMxMTk3MzAxYzE1ZmQ5NzllOWYzOWU3YjFmNDFkOGYxZWJkZjgxMTU1NzZlMmUifX19", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("NmI0Y2RlMTZhNDAxNGRlMGE3NjUxZjYwNjdmMTI2OTViYjVmZWQ2ZmVhZWMxZTk0MTNjYTQyNzFlN2M4MTkifX19", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("ZDA4Y2U3ZGViYTU2YjcyNmE4MzJiNjExMTVjYTE2MzM2MTM1OWMzMDQzNGY3ZDVlM2MzZmFhNmZlNDA1MiJ9fX0=", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("OTI4ZTY5MmQ4NmUyMjQ0OTc5MTVhMzk1ODNkYmUzOGVkZmZkMzljYmJhNDU3Y2M5NWE3YWMzZWEyNWQ0NDUifX19", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("MWI2NzMwZGU3ZTViOTQxZWZjNmU4Y2JhZjU3NTVmOTQyMWEyMGRlODcxNzU5NjgyY2Q4ODhjYzRhODEyODIifX19", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("MWFjMTE2M2Y1NGRjYmIwZThlMzFhYzY3NTY5NmYyNDA5Mjk5YzVhYmJmNmMzZmU3M2JmMWNmZTkxNDIyZTEifX19", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("NmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=", "§8§oPresent"));
        presents.add(ItemFactory.createSkull("YWEwNzQ4NDU4ODUyMDJlMTdlZDVjNGJlNDEwMzczMzEyMTIzNWM1NDQwYWUzYTFjNDlmYmQzOTMxN2IwNGQifX19", "§8§oPresent"));
    }

    Random r = new Random();

    public PetChristmasElf(UUID owner) {
        super(owner, PetType.CHRISTMASELF);
    }

    @Override
    protected void onUpdate() {
        final Item ITEM = entity.getWorld().dropItem(((Villager) entity).getEyeLocation(), presents.get(r.nextInt(presents.size())));
        ITEM.setPickupDelay(30000);
        ITEM.setVelocity(new Vector(r.nextDouble() - 0.5, r.nextDouble() / 2.0 + 0.3, r.nextDouble() - 0.5).multiply(0.4));
        Bukkit.getScheduler().runTaskLater(UltraCosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                ITEM.remove();
            }
        }, 5);
    }

}
