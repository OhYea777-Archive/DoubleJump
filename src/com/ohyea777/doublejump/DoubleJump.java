package com.ohyea777.doublejump;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class DoubleJump extends JavaPlugin implements Listener {

    private Map<UUID, String> allowFly = new HashMap<UUID, String>();
    private Map<UUID, String> firstJumps = new HashMap<UUID, String>();
    private Map<UUID, String> secondJumps = new HashMap<UUID, String>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            if (event.getPlayer().hasPermission("doublejump.doublejump")) {
                if (secondJumps.containsKey(event.getPlayer().getUniqueId()) && !event.getPlayer().getLocation().add(0, -1, 0).getBlock().getType().equals(Material.AIR)) {
                    secondJumps.remove(event.getPlayer().getUniqueId());
                } else if (!secondJumps.containsKey(event.getPlayer().getUniqueId())) {
                    event.getPlayer().setAllowFlight(true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (allowFly.containsKey(event.getPlayer().getUniqueId()) && (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) && event.getPlayer().hasPermission("doublejump.doublejump")) {
            event.setCancelled(true);
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);

            if (secondJumps.containsKey(event.getPlayer().getUniqueId())) {
                return;
            } else if (firstJumps.containsKey(event.getPlayer().getUniqueId())) {
                firstJumps.remove(event.getPlayer().getUniqueId());
                secondJumps.put(event.getPlayer().getUniqueId(), "");
            } else {
                firstJumps.put(event.getPlayer().getUniqueId(), "");
            }

            event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(2).setY(1));
        } else if (allowFly.containsKey(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL) || event.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }

        allowFly.put(event.getPlayer().getUniqueId(), "");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (allowFly.containsKey(event.getPlayer().getUniqueId())) {
            allowFly.remove(event.getPlayer().getUniqueId());
            event.getPlayer().setAllowFlight(false);
            event.getPlayer().setFlying(false);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().startsWith("/fly")) {
            String[] args = event.getMessage().split(" ");

            if (args != null && args.length == 2) {
                if (event.getPlayer().hasPermission("essentials.fly.others")) {
                    Player player = getServer().getPlayerExact(args[1]);

                    if (player != null) {
                        if (player.getAllowFlight()) allowFly.put(player.getUniqueId(), "");
                        else allowFly.remove(player.getUniqueId());
                    }
                }
            } else if (event.getPlayer().hasPermission("essentials.fly")) {
                if (event.getPlayer().getAllowFlight()) allowFly.put(event.getPlayer().getUniqueId(), "");
                else allowFly.remove(event.getPlayer().getUniqueId());
            }
        }
    }

}
