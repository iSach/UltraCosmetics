package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.Area;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.MathUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an instance of a parachute gadget summoned by a player.
 *
 * @author iSach
 * @since 10-12-2015
 */
public class GadgetParachute extends Gadget {

    private Set<Chicken> chickens = new HashSet<>();
    private boolean active;

    public GadgetParachute(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("parachute"), ultraCosmetics);
    }


    @Override
    public void onRightClick() {
        Location loc = getPlayer().getLocation();

        getPlayer().teleport(loc.clone().add(0, 35, 0));
        getPlayer().setVelocity(new Vector(0, 0, 0));

        getOwner().setCanBeHitByOtherGadgets(false);

        for (int i = 0; i < 20; i++) {
            int x = i % 5 - 2;
            int z = i / 4 - 2;
            Chicken chicken = (Chicken) getPlayer().getWorld().spawnEntity(getPlayer().getLocation().add(x, 3, z), EntityType.CHICKEN);
            chickens.add(chicken);
            chicken.setLeashHolder(getPlayer());
        }
        Bukkit.getScheduler().runTaskLater(getUltraCosmetics(), () -> active = true, 5);
    }

    private void killParachute() {
        for (Chicken chicken : chickens) {
            chicken.setLeashHolder(null);
            chicken.remove();
        }
        MathUtils.applyVelocity(getPlayer(), new Vector(0, 0.15, 0));
        active = false;
        getOwner().setCanBeHitByOtherGadgets(true);
    }

    @EventHandler
    public void onChickenDeath(EntityDeathEvent event) {
        // can't just cancel the event for some reason, so just eliminate the effects
        if (chickens.contains(event.getEntity())) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            event.getEntity().setLeashHolder(null);
        }
    }

    @EventHandler
    public void onChickenUnleash(EntityUnleashEvent event) {
        // can't cancel this either, but setting the leash holder to null prevents the lead from dropping
        if (chickens.contains(event.getEntity())) {
            ((Chicken)event.getEntity()).setLeashHolder(null);
            event.getEntity().remove();
        }
    }

    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        Location loc1 = getPlayer().getLocation().add(2, 28, 2);
        Location loc2 = getPlayer().getLocation().clone().add(-2, 40, -2);
        Area checkArea = new Area(loc1, loc2);

        if (!checkArea.isEmpty()) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.Rocket.Not-Enough-Space"));
            return false;
        }
        return true;
    }

    @Override
    public void onUpdate() {
        if (active) {
            // isOnGround returns true if they're on a solid block and doesn't account for non-solid blocks (#362)
            if (!isNotOnAir(getPlayer()) && getPlayer().getVelocity().getY() < -0.3)
                // Intentionally omitted check for canAffect
                MathUtils.applyVelocity(getPlayer(), getPlayer().getVelocity().add(new Vector(0, 0.1, 0)));
            if (isNotOnAir(getPlayer()))
                killParachute();
        }
    }

    private boolean isNotOnAir(Player p) {
        return !BlockUtils.isAir(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType());
    }

    @Override
    public void onClear() {
        killParachute();
        HandlerList.unregisterAll(this);
    }
}
