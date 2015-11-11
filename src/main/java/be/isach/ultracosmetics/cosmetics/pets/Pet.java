package be.isach.ultracosmetics.cosmetics.pets;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.cosmetics.pets.customentities.Pumpling;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.PathEntity;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.UnsafeList;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class Pet implements Listener {

    public ArrayList<Item> items = new ArrayList<>();
    public static List<net.minecraft.server.v1_8_R3.Entity> customEntities = new ArrayList();

    private Material material;
    private Byte data;
    private String name;

    private PetType type = PetType.DEFAULT;

    public EntityType entityType = EntityType.HORSE;

    private String permission;

    public ArmorStand armorStand;

    private UUID owner;

    private Listener listener;

    private String description;

    public Entity ent;
    public net.minecraft.server.v1_8_R3.Entity customEnt;

    public Pet(final EntityType entityType, Material material, Byte data, String configName, String permission, final UUID owner, final PetType type, String defaultDesc) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.entityType = entityType;
        if (SettingsManager.getConfig().get("Pets." + configName + ".Description") == null) {
            this.description = defaultDesc;
            SettingsManager.getConfig().set("Pets." + configName + ".Description", getDescription());
        } else {
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Pets." + configName + ".Description")));
        }
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (Core.getCustomPlayer(getPlayer()).currentPet != null)
                Core.getCustomPlayer(getPlayer()).removePet();

            final Pet pet = this;
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (entityType == EntityType.ZOMBIE) {
                            if (!customEnt.valid) {
                                if (armorStand != null)
                                    armorStand.remove();
                                customEnt.dead = true;
                                if (getPlayer() != null)
                                    Core.getCustomPlayer(getPlayer()).currentPet = null;
                                for (Item i : items) {
                                    i.remove();
                                }
                                items.clear();
                                try {
                                    HandlerList.unregisterAll(pet);
                                    HandlerList.unregisterAll(listener);
                                } catch (Exception exc) {
                                }
                                cancel();
                                return;
                            }
                        } else {
                            if (!ent.isValid()) {
                                if (armorStand != null)
                                    armorStand.remove();
                                ent.remove();
                                if (getPlayer() != null)
                                    Core.getCustomPlayer(getPlayer()).currentPet = null;
                                for (Item i : items) {
                                    i.remove();
                                }
                                items.clear();
                                try {
                                    HandlerList.unregisterAll(pet);
                                    HandlerList.unregisterAll(listener);
                                } catch (Exception exc) {
                                }
                                cancel();
                                return;
                            }
                        }
                        if (Bukkit.getPlayer(owner) != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentPet.getType() == type) {
                            if (SettingsManager.getConfig().get("Pets-Drop-Items"))
                                onUpdate();
                            followPlayer();
                        } else {
                            cancel();
                        }

                    } catch (NullPointerException exc) {
                        exc.printStackTrace();
                        cancel();
                        if (armorStand != null)
                            armorStand.remove();
                        clear();
                    }
                }
            };
            runnable.runTaskTimer(Core.getPlugin(), 0, 6);
            listener = new PetListener(this);

            if (entityType != EntityType.ZOMBIE) {

                this.ent = getPlayer().getWorld().spawnEntity(getPlayer().getLocation(), getEntityType());
                //ent.setCustomNameVisible(true);
                //ent.setCustomName(getName());
                if (ent instanceof Ageable) {
                    if (SettingsManager.getConfig().get("Pets-Are-Babies"))
                        ((Ageable) ent).setBaby();
                    else
                        ((Ageable) ent).setAdult();
                    ((Ageable) ent).setAgeLock(true);
                }
                net.minecraft.server.v1_8_R3.Entity entity = ((CraftEntity) ent).getHandle();
                try {
                    Field bField = PathfinderGoalSelector.class.getDeclaredField("b");
                    bField.setAccessible(true);
                    Field cField = PathfinderGoalSelector.class.getDeclaredField("c");
                    cField.setAccessible(true);
                    bField.set(((EntityInsentient) entity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
                    bField.set(((EntityInsentient) entity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
                    cField.set(((EntityInsentient) entity).goalSelector, new UnsafeList<PathfinderGoalSelector>());
                    cField.set(((EntityInsentient) entity).targetSelector, new UnsafeList<PathfinderGoalSelector>());
                } catch (Exception exc) {
                    exc.printStackTrace();
                }

                if (getEntityType() != EntityType.WITHER) {

                    armorStand = (ArmorStand) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.ARMOR_STAND);
                    armorStand.setVisible(false);
                    armorStand.setSmall(true);
                    armorStand.setCustomName(getName());
                    armorStand.setCustomNameVisible(true);

                    if (Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()) != null)
                        armorStand.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()));

                    ent.setPassenger(armorStand);
                } else {
                    ent.setCustomName(getName());
                    ent.setCustomNameVisible(true);

                    if (Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()) != null)
                        ent.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()));
                }
                ent.setMetadata("Pet", new FixedMetadataValue(Core.getPlugin(), "UltraCosmetics"));

            } else {
                customEnt = new Pumpling(((CraftPlayer) getPlayer()).getHandle().getWorld());
                customEntities.add(customEnt);
                double x = getPlayer().getLocation().getX();
                double y = getPlayer().getLocation().getY();
                double z = getPlayer().getLocation().getZ();
                customEnt.setLocation(x, y, z, 0, 0);
                armorStand = (ArmorStand) customEnt.getBukkitEntity().getWorld().spawnEntity(customEnt.getBukkitEntity().getLocation(), EntityType.ARMOR_STAND);
                armorStand.setVisible(false);
                armorStand.setSmall(true);
                armorStand.setCustomName(getName());
                armorStand.setCustomNameVisible(true);

                if (Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()) != null)
                    armorStand.setCustomName(Core.getCustomPlayer(getPlayer()).getPetName(getConfigName()));

                customEnt.getBukkitEntity().setPassenger(armorStand);
                ((CraftWorld) getPlayer().getWorld()).getHandle().addEntity(customEnt);
            }
            getPlayer().sendMessage(MessageManager.getMessage("Pets.Spawn").replace("%petname%", (Core.placeHolderColor) ? getMenuName() : Core.filterColor(getMenuName())));
            Core.getCustomPlayer(getPlayer()).currentPet = this;
        }
    }

    private void followPlayer() {
        if (Core.getCustomPlayer(getPlayer()).currentTreasureChest != null)
            return;

        net.minecraft.server.v1_8_R3.Entity pett = getEntityType() == EntityType.ZOMBIE ? customEnt : ((CraftEntity) ent).getHandle();
        ((EntityInsentient) pett).getNavigation().a(2);
        Location targetLocation = getPlayer().getLocation();
        PathEntity path;
        path = ((EntityInsentient) pett).getNavigation().a(targetLocation.getX() + 1, targetLocation.getY(), targetLocation.getZ() + 1);
        try {
            int distance = (int) Bukkit.getPlayer(getPlayer().getName()).getLocation().distance(pett.getBukkitEntity().getLocation());
            if (distance > 10 && pett.valid && getPlayer().isOnGround()) {
                pett.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
            }
            if (path != null && distance > 3.3) {
                double speed = 1.05d;
                if (entityType == EntityType.ZOMBIE)
                    speed *= 1.5;
                ((EntityInsentient) pett).getNavigation().a(path, speed);
                ((EntityInsentient) pett).getNavigation().a(speed);
            }
        } catch (IllegalArgumentException exception) {
            pett.setLocation(targetLocation.getBlockX(), targetLocation.getBlockY(), targetLocation.getBlockZ(), 0, 0);
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
        if (getEntityType() != EntityType.ZOMBIE)
            ent.remove();
        else {
            customEnt.dead = true;
            customEntities.remove(customEnt);
        }
        if (getPlayer() != null && Core.getCustomPlayer(getPlayer()) != null)
            Core.getCustomPlayer(getPlayer()).currentPet = null;
        for (Item i : items)
            i.remove();
        items.clear();
        if (armorStand != null)
            armorStand.remove();
        try {
            HandlerList.unregisterAll(this);
            HandlerList.unregisterAll(listener);
        } catch (Exception exc) {
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Pets.Despawn").replace("%petname%", (Core.placeHolderColor) ? getMenuName() : Core.filterColor(getMenuName())));
        owner = null;
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
            if (pet.entityType == EntityType.ZOMBIE) {
                if (event.getEntity() == pet.customEnt.getBukkitEntity())
                    event.setCancelled(true);
            } else {
                if (event.getEntity() == pet.ent)
                    event.setCancelled(true);
            }
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
        return SettingsManager.getConfig().getBoolean("Pets." + getConfigName() + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Pets." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public enum PetType {

        DEFAULT("", ""),
        PIGGY("ultracosmetics.pets.piggy", "Piggy"),
        SHEEP("ultracosmetics.pets.sheep", "Sheep"),
        EASTERBUNNY("ultracosmetics.pets.easterbunny", "EasterBunny"),
        COW("ultracosmetics.pets.cow", "Cow"),
        KITTY("ultracosmetics.pets.kitty", "Kitty"),
        DOG("ultracosmetics.pets.dog", "Dog"),
        CHICK("ultracosmetics.pets.chick", "Chick"),
        WITHER("ultracosmetics.pets.wither", "Wither"),
        PUMPLING("ultracosmetics.pets.pumpling", "Pumpling");


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
