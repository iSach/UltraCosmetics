package be.isach.ultracosmetics.cosmetics.particleeffects;

import be.isach.ultracosmetics.Core;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.config.SettingsManager;
import be.isach.ultracosmetics.util.MathUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by sacha on 03/08/15.
 */
public abstract class ParticleEffect implements Listener {

    private Material material;
    private Byte data;
    private String name;

    boolean moving;

    private ParticleEffectType type = ParticleEffectType.DEFAULT;

    private String permission;

    int repeatDelay = 1;

    private UUID owner;

    private Listener listener;

    private Particles effect;

    private String description;

    protected boolean ignoreMove = false;

    public ParticleEffect(final Particles effect, Material material, Byte data, String configName, String permission, final UUID owner, final ParticleEffectType type, int repeatDelay, String defaultDesc) {
        this.material = material;
        this.data = data;
        this.name = configName;
        this.permission = permission;
        this.type = type;
        this.effect = effect;
        this.repeatDelay = repeatDelay;
        if (SettingsManager.getConfig().get("Particle-Effects." + configName + ".Description") == null) {
            this.description = defaultDesc;
            SettingsManager.getConfig().set("Particle-Effects." + configName + ".Description", getDescriptionWithColor(), "Description of this particle effect.");
        } else {
            this.description = fromList(((List<String>) SettingsManager.getConfig().get("Particle-Effects." + configName + ".Description")));
        }
        if (owner != null) {
            this.owner = owner;
            if (!getPlayer().hasPermission(permission)) {
                getPlayer().sendMessage(MessageManager.getMessage("No-Permission"));
                return;
            }
            if (Core.getCustomPlayer(getPlayer()).currentParticleEffect != null)
                Core.getCustomPlayer(getPlayer()).removeParticleEffect();
            BukkitRunnable runnable = new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        if (Bukkit.getPlayer(owner) != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentParticleEffect != null
                                && Core.getCustomPlayer(Bukkit.getPlayer(owner)).currentParticleEffect.getType() == type) {
                            if (getType() != ParticleEffectType.FROZENWALK
                                    && getType() != ParticleEffectType.ENCHANTED
                                    && getType() != ParticleEffectType.MUSIC) {
                                if (!moving || ignoreMove)
                                    onUpdate();
                                if (moving) {
                                    boolean c = type == ParticleEffectType.ANGELWINGS;
                                    if (getEffect() == Particles.REDSTONE) {
                                        if (!ignoreMove)
                                            for (int i = 0; i < 15; i++)
                                                if (!c)
                                                    effect.display(new Particles.OrdinaryColor(255, 0, 0), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                                else
                                                    effect.display(new Particles.OrdinaryColor(255, 255, 255), getPlayer().getLocation().add(MathUtils.randomDouble(-0.8, 0.8), 1 + MathUtils.randomDouble(-0.8, 0.8), MathUtils.randomDouble(-0.8, 0.8)), 128);
                                    } else
                                        UtilParticles.display(effect, .4f, .3f, .4f, getPlayer().getLocation().add(0, 1, 0), 3);
                                    moving = false;
                                }
                            } else
                                onUpdate();
                        } else {
                            cancel();
                        }

                    } catch (NullPointerException exc) {
                        clear();
                        cancel();
                    }
                }
            };
            runnable.runTaskTimerAsynchronously(Core.getPlugin(), 0, repeatDelay);
            listener = new ParticleEffectListener(this);

            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Summon").replace("%effectname%", (Core.placeHolderColor) ? getName() : Core.filterColor(getName())));
            Core.getCustomPlayer(getPlayer()).currentParticleEffect = this;
        }
    }

    public Particles getEffect() {
        return effect;
    }

    public String getConfigName() {
        return name;
    }

    public String getName() {
        return MessageManager.getMessage("Particle-Effects." + name + ".name");
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<String> getDescriptionWithColor() {
        return Arrays.asList(description.split("\n"));
    }

    public ParticleEffectType getType() {
        return this.type;
    }

    public Byte getData() {
        return this.data;
    }

    abstract void onUpdate();

    public void clear() {
        Core.getCustomPlayer(getPlayer()).currentParticleEffect = null;
        try {
            HandlerList.unregisterAll(this);
            HandlerList.unregisterAll(listener);
        } catch (Exception exc) {
        }
        if (getPlayer() != null)
            getPlayer().sendMessage(MessageManager.getMessage("Particle-Effects.Unsummon").replace("%mountname%", (Core.placeHolderColor) ? getName() : Core.filterColor(getName())));
        owner = null;
    }

    protected UUID getOwner() {
        return owner;
    }

    protected Player getPlayer() {
        return Bukkit.getPlayer(owner);
    }

    public class ParticleEffectListener implements Listener {
        private ParticleEffect particleEffect;

        public ParticleEffectListener(ParticleEffect particleEffect) {
            this.particleEffect = particleEffect;
            Core.registerListener(this);
        }

        @EventHandler
        public void onMove(PlayerMoveEvent event) {
            if (getPlayer() == event.getPlayer()
                    && (event.getFrom().getX() != event.getTo().getX()
                    || event.getFrom().getY() != event.getTo().getY()
                    || event.getFrom().getZ() != event.getTo().getZ()))
                particleEffect.moving = true;
        }

    }

    public List<String> getDescription() {
        List<String> desc = new ArrayList<>();
        for (String string : description.split("\n")) {
            desc.add(string.replace('&', '§'));
        }
        return desc;
    }

    private String fromList(List<String> description) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < description.size(); i++) {
            stringBuilder.append(description.get(i) + (i < description.size() - 1 ? "\n" : ""));
        }
        return stringBuilder.toString();
    }

    public boolean showsDescription() {
        return SettingsManager.getConfig().getBoolean("Particle-Effects." + getConfigName() + ".Show-Description");
    }

    public boolean canBeFound() {
        return SettingsManager.getConfig().getBoolean("Particle-Effects." + getConfigName() + ".Can-Be-Found-In-Treasure-Chests");
    }

    public enum ParticleEffectType {

        DEFAULT("", ""),
        RAINCLOUD("ultracosmetics.particleeffects.raincloud", "RainCloud"),
        SNOWCLOUD("ultracosmetics.particleeffects.snowcloud", "SnowCloud"),
        BLOODHELIX("ultracosmetics.particleeffects.bloodhelix", "BloodHelix"),
        FROSTLORD("ultracosmetics.particleeffects.frostlord", "FrostLord"),
        FLAMERINGS("ultracosmetics.particleeffects.flamerings", "FlameRings"),
        INLOVE("ultracosmetics.particleeffects.inlove", "InLove"),
        GREENSPARKS("ultracosmetics.particleeffects.greensparks", "GreenSparks"),
        FROZENWALK("ultracosmetics.particleeffects.frozenwalk", "FrozenWalk"),
        MUSIC("ultracosmetics.particleeffects.music", "Music"),
        ENCHANTED("ultracosmetics.particleeffects.enchanted", "Enchanted"),
        INFERNO("ultracosmetics.particleeffects.inferno", "Inferno"),
        ANGELWINGS("ultracosmetics.particleeffects.angelwings", "AngelWings"),
        SUPERHERO("ultracosmetics.particleeffects.superhero", "SuperHero"),
        SANTAHAT("ultracosmetics.particleeffects.santahat", "SantaHat");


        String permission;
        String configName;

        ParticleEffectType(String permission, String configName) {
            this.permission = permission;
            this.configName = configName;
        }

        public String getPermission() {
            return permission;
        }

        public boolean isEnabled() {
            return SettingsManager.getConfig().getBoolean("Particle-Effects." + configName + ".Enabled");
        }

    }

}
