package me.isach.ultracosmetics.cosmetics.gadgets;

import me.isach.ultracosmetics.Core;
import me.isach.ultracosmetics.config.MessageManager;
import me.isach.ultracosmetics.util.MathUtils;
import me.isach.ultracosmetics.util.UtilParticles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by sacha on 07/08/15.
 */
public class GadgetPortalGun extends Gadget {

    boolean teleported = false;

    Location loc1;
    BlockFace loc1BlockFace;

    Location loc2;
    BlockFace loc2BlockFace;

    public GadgetPortalGun(UUID owner) {
        super(Material.REDSTONE_COMPARATOR, (byte) 0x0, "PortalGun", "ultracosmetics.gadgets.portalgun", 1, owner, GadgetType.PORTALGUN);
        displayCountdownMessage = false;
        useTwoInteractMethods = true;
    }

    @Override
    void onInteractRightClick() {
        getPlayer().playSound(getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 0.2f, 1.5f);
        UtilParticles.drawParticleLine(getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.6)), getPlayer().getTargetBlock((HashSet<Material>) null, 20).getLocation(), Effect.COLOURED_DUST, 100, -255, -255, 255);
        loc1 = getPlayer().getTargetBlock((Set<Material>) null, 20).getLocation();
        List<Block> b = getPlayer().getLastTwoTargetBlocks((Set<Material>) null, 20);
        loc1BlockFace = getBlockFace(b.get(0), b.get(1));
        loc1 = loc1.getBlock().getRelative(loc1BlockFace).getLocation().add(0, -0.5, -1);
        if (loc1BlockFace == BlockFace.UP || loc1BlockFace == BlockFace.DOWN) {
            loc1.add(0.5, 0.7, 0.5);
        } else if (loc1BlockFace == BlockFace.WEST) {
            loc1.add(0.6, 0.8, 0.5);
        } else if (loc1BlockFace == BlockFace.EAST) {
            loc1.add(.3, 0.8, 0.5);
        } else if (loc1BlockFace == BlockFace.NORTH) {
            loc1.add(0.4, 1.8, 1.75);
        } else if (loc1BlockFace == BlockFace.SOUTH) {
            loc1.add(0.4, 1.8, 1.2);
        }
    }


    @Override
    void onInteractLeftClick() {
        getPlayer().playSound(getPlayer().getLocation(), Sound.ENDERMAN_TELEPORT, 0.2f, 1.5f);
        UtilParticles.drawParticleLine(getPlayer().getEyeLocation().add(getPlayer().getEyeLocation().getDirection().multiply(0.6)), getPlayer().getTargetBlock((HashSet<Material>) null, 20).getLocation(), Effect.COLOURED_DUST, 100, 255, -255, -255);
        loc2 = getPlayer().getTargetBlock((Set<Material>) null, 20).getLocation();
        List<Block> b = getPlayer().getLastTwoTargetBlocks((Set<Material>) null, 20);
        loc2BlockFace = getBlockFace(b.get(0), b.get(1));
        loc2 = loc2.clone().getBlock().getRelative(loc2BlockFace).getLocation().add(0, -0.5, -1);
        if (loc2BlockFace == BlockFace.UP || loc2BlockFace == BlockFace.DOWN) {
            loc2.add(0.5, 0.7, 0.5);
        } else if (loc2BlockFace == BlockFace.WEST) {
            loc2.add(0.6, 0.8, 0.5);
        } else if (loc2BlockFace == BlockFace.EAST) {
            loc2.add(.3, 0.8, 0.5);
        } else if (loc2BlockFace == BlockFace.NORTH) {
            loc2.add(0.4, 1.8, 1.75);
        } else if (loc2BlockFace == BlockFace.SOUTH) {
            loc2.add(0.4, 1.8, 1.2);
        }
    }

    public Vector getVectorFromBlockFace(BlockFace bf) {
        Vector v = new Vector(0, 0, 0);
        if (bf == BlockFace.UP) {
            v.add(new Vector(0, 0.3, 0));
        } else if (bf == BlockFace.DOWN) {
            v.add(new Vector(0, -0.3, 0));
        } else if (bf == BlockFace.WEST) {
            v.add(new Vector(-0.3, 0, 0));
        } else if (bf == BlockFace.EAST) {
            v.add(new Vector(0.3, 0, 0));
        } else if (bf == BlockFace.NORTH) {
            v.add(new Vector(-0.3, 0, 0));
        } else if (bf == BlockFace.SOUTH) {
            v.add(new Vector(0.3, 0, 0));
        }
        return v;
    }

    public float getPitch(BlockFace bf) {
        float pitch = 0;
        if (bf == BlockFace.UP) {
            pitch = -90;
        } else if (bf == BlockFace.DOWN) {
            pitch = 90;
        }
        return pitch;
    }

    public float getYaw(BlockFace bf) {
        float yaw = 90;
        if (bf == BlockFace.WEST) {
            yaw = 90;
        } else if (bf == BlockFace.EAST) {
            yaw = -90;
        } else if (bf == BlockFace.NORTH) {
            yaw = 180;
        } else if (bf == BlockFace.SOUTH) {
            yaw = 0;
        }
        return yaw;
    }

    public BlockFace getBlockFace(Block a, Block b) {
        for (BlockFace bf : BlockFace.values()) {
            if (a.getRelative(bf).getLocation().equals(b.getLocation())) {
                return bf.getOppositeFace();
            }
        }
        return null;
    }

    @Override
    void onUpdate() {
        if (loc1 != null) {
            Location portalCenter = loc1.clone();
            if (loc2 != null && !teleported) {
                Location toDistance;
                if (loc1BlockFace == BlockFace.DOWN) {
                    toDistance = getPlayer().getEyeLocation().clone();
                } else if (loc1BlockFace == BlockFace.UP) {
                    toDistance = getPlayer().getLocation().clone();
                } else {
                    toDistance = getPlayer().getLocation().add(0, 1.03, 0);
                }
                if (loc1BlockFace == BlockFace.UP || loc1BlockFace == BlockFace.DOWN) {
                    portalCenter.add(0, 0, 1);
                } else if (loc1BlockFace == BlockFace.NORTH || loc1BlockFace == BlockFace.SOUTH) {
                    portalCenter.add(0, -1, 0);
                } else if (loc1BlockFace == BlockFace.EAST || loc1BlockFace == BlockFace.WEST) {
                    portalCenter.add(0, 0, 1);
                }
                if (toDistance.distance(loc1) < 1.01) {
                    teleported = true;
                    getPlayer().teleport(loc2);
                    getPlayer().setVelocity(getVectorFromBlockFace(loc2BlockFace));
                    if (loc2BlockFace == BlockFace.UP || loc2BlockFace == BlockFace.DOWN) {
                        Location loc = getPlayer().getLocation().clone();
                        loc.setPitch(getPitch(loc2BlockFace));
                        getPlayer().teleport(loc);
                    } else {
                        Location loc = getPlayer().getLocation().clone();
                        loc.setYaw(getYaw(loc2BlockFace));
                        getPlayer().teleport(loc);
                    }
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            teleported = false;
                        }
                    }, 20);
                }
            }
            Location loc = loc1.clone();
            for (int i = 0; i < 100; i++) {
                double inc = (2 * Math.PI) / 20;
                double angle = i * inc;
                Vector v = new Vector();
                v.setX(Math.cos(angle) * 0.3f);
                v.setZ(Math.sin(angle) * 0.3f);
                double x = 0;
                double z = 0;
                if (loc1BlockFace != BlockFace.UP && loc1BlockFace != BlockFace.DOWN) {
                    if (loc1BlockFace == BlockFace.EAST || loc1BlockFace == BlockFace.WEST) {
                        x = 0;
                        z = 1.5;
                    } else if (loc1BlockFace == BlockFace.NORTH || loc1BlockFace == BlockFace.SOUTH) {
                        z = 0;
                        x = 1.5;
                    }
                }
                MathUtils.rotateVector(v, x, 0, z);
                float finalR = -255 / 255;
                float finalG = -255 / 255;
                float finalB = 255 / 255;
                UtilParticles.play(loc.add(v), Effect.COLOURED_DUST, 0, 0, finalR, finalG, finalB, 1f, 0);
            }
        }
        if (loc2 != null) {
            if (loc1 != null && !teleported) {
                Location toDistance;
                if (loc2BlockFace == BlockFace.DOWN) {
                    toDistance = getPlayer().getEyeLocation().clone();
                } else if (loc2BlockFace == BlockFace.UP) {
                    toDistance = getPlayer().getLocation().clone();
                } else {
                    toDistance = getPlayer().getLocation().add(0, 1.1, 0);
                }
                if (toDistance.distance(loc2) < 1.1) {
                    teleported = true;
                    getPlayer().teleport(loc1);
                    getPlayer().setVelocity(getVectorFromBlockFace(loc1BlockFace));
                    if (loc1BlockFace == BlockFace.UP || loc1BlockFace == BlockFace.DOWN) {
                        Location loc = getPlayer().getLocation().clone();
                        loc.setPitch(getPitch(loc1BlockFace));
                        getPlayer().teleport(loc);
                    } else {
                        Location loc = getPlayer().getLocation().clone();
                        loc.setYaw(getYaw(loc1BlockFace));
                        getPlayer().teleport(loc);
                    }
                    Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            teleported = false;
                        }
                    }, 20);
                }
            }
            Location loc = loc2.clone();
            for (int i = 0; i < 100; i++) {
                double inc = (2 * Math.PI) / 20;
                double angle = i * inc;
                Vector v = new Vector();
                v.setX(Math.cos(angle) * 0.3f);
                v.setZ(Math.sin(angle) * 0.3f);
                double x = 0;
                double z = 0;
                if (loc2BlockFace != BlockFace.UP && loc2BlockFace != BlockFace.DOWN) {
                    if (loc2BlockFace == BlockFace.EAST || loc2BlockFace == BlockFace.WEST) {
                        x = 0;
                        z = 1.5;
                    } else if (loc2BlockFace == BlockFace.NORTH || loc2BlockFace == BlockFace.SOUTH) {
                        z = 0;
                        x = 1.5;
                    }
                }
                MathUtils.rotateVector(v, x, 0, z);
                float finalR = 255 / 255;
                float finalG = -255 / 255;
                float finalB = -255 / 255;
                UtilParticles.play(loc.add(v), Effect.COLOURED_DUST, 0, 0, finalR, finalG, finalB, 1f, 0);
            }
        }

    }

    public BlockFace[] axis = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
    public BlockFace[] radial = {BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST, BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST};

    public BlockFace yawToFace(float yaw) {
        return yawToFace(yaw, true);
    }

    public BlockFace yawToFace(float yaw, boolean useSubCardinalDirections) {
        if (useSubCardinalDirections) {
            return radial[Math.round(yaw / 45f) & 0x7];
        } else {
            return axis[Math.round(yaw / 90f) & 0x3];
        }
    }

    @Override
    public void clear() {
        loc1 = null;
        loc2 = null;
    }
}
