package be.isach.ultracosmetics.cosmetics.gadgets;

import be.isach.ultracosmetics.UltraCosmetics;
import be.isach.ultracosmetics.config.MessageManager;
import be.isach.ultracosmetics.cosmetics.Updatable;
import be.isach.ultracosmetics.cosmetics.type.GadgetType;
import be.isach.ultracosmetics.player.UltraPlayer;
import be.isach.ultracosmetics.util.BlockUtils;
import be.isach.ultracosmetics.util.Particles;
import be.isach.ultracosmetics.util.PortalLoc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import com.cryptomorin.xseries.XSound;

import java.util.List;

/**
 * Represents an instance of a portal gun gadget summoned by a player.
 *
 * @author iSach
 * @since 08-07-2015
 */
public class GadgetPortalGun extends Gadget implements Updatable {
    private static final double CIRCLE_STEP = (2 * Math.PI) / 20;
    private boolean teleported = false;

    private PortalLoc red = new PortalLoc(255, 0, 0);
    private PortalLoc blue = new PortalLoc(31, 0, 127);

    public GadgetPortalGun(UltraPlayer owner, UltraCosmetics ultraCosmetics) {
        super(owner, GadgetType.valueOf("portalgun"), ultraCosmetics);
        displayCooldownMessage = false;
    }

    @Override
    protected void onRightClick() {
        handleClick(blue);
    }

    @Override
    protected void onLeftClick() {
        handleClick(red);
    }

    private void handleClick(PortalLoc portalLoc) {
        XSound.ENTITY_ENDERMAN_TELEPORT.play(getPlayer(), 0.2f, 1.5f);
        List<Block> sight = getPlayer().getLastTwoTargetBlocks(null, 20);
        Block target = sight.get(1);
        Location playerFaceLoc = getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.6));
        Particles.REDSTONE.drawParticleLine(playerFaceLoc, target.getLocation(), 100, portalLoc.getRed(), portalLoc.getGreen(), portalLoc.getBlue());

        BlockFace face = getBlockFace(sight.get(0), target);
        Location loc = target.getRelative(face).getLocation().add(0.5, 0.5, 0.5);
        loc.add(face.getDirection().multiply(-0.25));
        portalLoc.setLocation(loc);
        portalLoc.setFace(face);
    }

    public float getPitch(BlockFace bf) {
        if (bf == BlockFace.UP) {
            return -90;
        } else if (bf == BlockFace.DOWN) {
            return 90;
        }
        return 0;
    }

    public float getYaw(BlockFace bf) {
        if (bf == BlockFace.WEST) {
            return 90;
        } else if (bf == BlockFace.EAST) {
            return -90;
        } else if (bf == BlockFace.NORTH) {
            return 180;
        } else { // BlockFace.SOUTH or something else
            return 0;
        }
    }

    public BlockFace getBlockFace(Block a, Block b) {
        for (BlockFace bf : BlockFace.values()) {
            if (a.getRelative(bf).getLocation().equals(b.getLocation())) {
                return bf.getOppositeFace();
            }
        }
        return null;
    }

    private boolean portalTeleportCheck(PortalLoc portalLoc, PortalLoc dest) {
        BlockFace face = portalLoc.getFace();
        Location playerLoc;
        if (face == BlockFace.DOWN) {
            playerLoc = getPlayer().getEyeLocation();
        } else if (face == BlockFace.UP) {
            playerLoc = getPlayer().getLocation();
        } else {
            playerLoc = getPlayer().getLocation().add(0, 1, 0);
        }

        // 'distanceSquared' is faster than 'distance', and sqrt(1) == 1 anyway
        if (playerLoc.distanceSquared(portalLoc.getLocation()) > 1) return false;
        teleported = true;
        Location loc = dest.getLocation().clone();
        BlockFace destFace = dest.getFace();
        loc.setYaw(getYaw(destFace));
        loc.setPitch(getPitch(destFace));
        teleport(getPlayer(), loc, destFace.getDirection().multiply(0.3));
        Bukkit.getScheduler().runTaskLaterAsynchronously(getUltraCosmetics(), () -> teleported = false, 20);
        return true;
    }

    private void checkPortals() {
        if (!red.isValid() || !blue.isValid() || teleported) return;
        if (red.getLocation().getWorld() != blue.getLocation().getWorld()) {
            red.clear();
            blue.clear();
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.PortalGun.Different-Worlds"));
            return;
        }
        if (!portalTeleportCheck(red, blue)) {
            portalTeleportCheck(blue, red);
        }
    }

    private void showParticles(PortalLoc portalLoc) {
        if (!portalLoc.isValid()) return;
        Location loc = portalLoc.getLocation().clone();
        BlockFace face = portalLoc.getFace();
        for (int i = 0; i < 20; i++) {
            double angle = i * CIRCLE_STEP;
            Vector v = new Vector();
            double a1 = Math.cos(angle);
            double a2 = Math.sin(angle);
            if (face == BlockFace.NORTH || face == BlockFace.SOUTH) {
                v.setX(a1);
                v.setY(a2);
            } else if (face == BlockFace.EAST || face == BlockFace.WEST) {
                v.setY(a1);
                v.setZ(a2);
            } else {
                v.setX(a1);
                v.setZ(a2);
            }
            Particles.REDSTONE.display(portalLoc.getRed(), portalLoc.getGreen(), portalLoc.getBlue(), loc.clone().add(v));
        }
    }

    @Override
    public void onUpdate() {
        checkPortals();
        showParticles(red);
        showParticles(blue);
    }

    @Override
    protected boolean checkRequirements(PlayerInteractEvent event) {
        List<Block> sight = getPlayer().getLastTwoTargetBlocks(null, 20);
        if (sight.size() < 2 || BlockUtils.isAir(sight.get(1).getType())) {
            getPlayer().sendMessage(MessageManager.getMessage("Gadgets.PortalGun.No-Block-Range"));
            return false;
        }
        return true;
    }

    private void teleport(final Entity entity, final Location location, final Vector velocity) {
        Bukkit.getScheduler().runTask(getUltraCosmetics(), () -> {
            entity.teleport(location);
            entity.setVelocity(velocity);
            if (entity instanceof Player) {
                XSound.ENTITY_ENDERMAN_TELEPORT.play(entity);
            }
        });
    }

    @Override
    public void onClear() {
        red.clear();
        blue.clear();
    }
}
