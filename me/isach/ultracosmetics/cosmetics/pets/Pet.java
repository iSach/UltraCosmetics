package me.isach.ultracosmetics.cosmetics.pets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.config.SettingsManager;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Pet implements Listener {

    public ArrayList<Item> items = new ArrayList<>();

    private Material material;
    private Byte data;
    private String name;

    private PetType type = PetType.DEFAULT;

    public EntityType entityType = EntityType.HORSE;

    private String permission;

    public ArmorStand armorStand;

    private UUID owner;

    public Entity ent;

    public Pet(EntityType entityType, Material material, Byte data, String configName, String permission, final UUID owner, final PetType type) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.entityType = entityType;
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (Core.getCustomPlayer(getPlayer()).currentPet != null)
                Core.getCustomPlayer(getPlayer()).removePet();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    if (Bukkit.getPlayer(owner) != null
                            && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet != null
                            && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet.getType() == type) {
                        onUpdate();
                        followPlayer();
                    } else {
                        cancel();
                    }
                }
            };
            runnable.runTaskTimer(Core.getPlugin(), 0, 6);
            new PetListener(this);

            this.ent = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getEntityType());
            ((Ageable) ent).setAdult();
            //ent.setCustomNameVisible(true);
            //ent.setCustomName(getName());
            ((Ageable) ent).setBaby();
            ((Ageable) ent).setAgeLock(true);


            armorStand = (ArmorStand) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.ARMOR_STAND);
            armorStand.setVisible(false);
            armorStand.setSmall(true);
            armorStand.setCustomName(getName());
            armorStand.setCustomNameVisible(true);

            if(Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()) != null)
                armorStand.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()));

            ent.setPassenger(armorStand);

            getPlayer().sendMessage(MessageManager.getMessage("Pets.Spawn").replace("%petname%", getMenuName()));
            Core.getCustomPlayer(getPlayer()).currentPet = this;
        }
    }

    private void followPlayer() {
        if(Core.getCustomPlayer(getPlayer()).currentTreasureChest != null)
            return;
        net.minecraft.server.v1_8_R3.Entity pett = ((CraftEntity) ent).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Object petf = ((CraftEntity) ent).getHandle();
        Location targetLocation = getPlayer().getLocation();
        PathEntity path;
        path = ((EntityInsentient) petf).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        try {
            int distance = (int) Bukkit.getPlayer(getPlayer().getName()).getLocation().distance(ent.getLocation());
            if (distance > 10 && ent.isValid() && getPlayer().isOnGround()) {
                ent.teleport(getPlayer().getLocation());
            }
            if (path != null && distance > 3.3) {
                ((EntityInsentient) petf).getNavigation().a(path, 1.05D);
                ((EntityInsentient) petf).getNavigation().a(1.05D);
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            ent.teleport(getPlayer().getLocation());
        }
    }


    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return MessageManager.getMessage("Pets." + name + ".entity-displayname").replace("%playername%", getPlayer().getName());
    }

    public String getConfigName() {
        return name;
    }

    public String getMenuName() {
        return MessageManager.getMessage("Pets." + name + ".menu-name");
    }

    public Material getMaterial() {
        return this.material;
    }


    public PetType getType() {
        return this.type;
    }

    public Byte getData() {
        return this.data;
    }

    abstract void onUpdate();

    public void clear() {
        getPlayer().sendMessage(MessageManager.getMessage("Pets.Despawn").replace("%petname%", getMenuName()));
        Core.getCustomPlayer(getPlayer()).currentPet = null;
        ent.remove();
        for (Item i : items) {
            i.remove();
        }
        items.clear();
        if (armorStand != null)
            armorStand.remove();
    }

    protected UUID getOwner() {
        return owner;
    }

    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public class PetListener implements Listener {
        private Pet pet;

        public PetListener(Pet pet) {
            this.pet = pet;
            Core.registerListener(this);
        }

        @EventHandler
        public void onEntityDamage(EntityDamageEvent event) {
            if (event.getEntity() == ent)
                event.setCancelled(true);
        }


    }

    public enum PetType {

        DEFAULT("", ""),
        PIGGY("ultracosmetics.pets.piggy", "Piggy"),
        SHEEP("ultracosmetics.pets.sheep", "Sheep"),
        EASTERBUNNY("ultracosmetics.pets.easterbunny", "EasterBunny"),
        COW("ultracosmetics.pets.cow", "Cow"),
        KITTY("ultracosmetics.pets.kitty", "Kitty"),
        DOG("ultracosmetics.pets.dog", "Dog"),
        CHICK("ultracosmetics.pets.chick", "Chick");


        String permission;
        String configName;

        PetType(String permission, String configName) {
            this.permission = permission;
            this.configName = configName;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().get("Pets." + configName + ".Enabled");
        }

    }

}
