package com.zeusallmighty11.Spawn;


import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** Why are you viewing this? */
public class Spawn extends JavaPlugin
{
    Map<String, Warp> warps;



    public void onEnable()
    {
        // init map
        warps = new HashMap<>();

        // create default dir
        File dir = new File(getDataFolder() + "");
        if (!dir.exists())
            dir.mkdir();

        // create config.yml
        File config = new File(getDataFolder() + "/config.yml");
        if (!config.exists())
            saveDefaultConfig();

        // set spawn command
        getCommand("setlocation").setExecutor(new CMD_SetLocation());


        ConfigurationSection cs = getConfig().getConfigurationSection("locations");
        for (String key : cs.getKeys(false))
        {
            String name = key + "";
            String msg = cs.getString(key + ".message").replace("&", "§");
            String perm = cs.getString(key + ".permission").replace("&", "§");
            List<String> commands = cs.getStringList(key + ".commands");

            double x = cs.getDouble(key + ".location.x");
            double y = cs.getDouble(key + ".location.y");
            double z = cs.getDouble(key + ".location.z");
            float pitch = cs.getLong(key + ".pitch");
            float yaw = cs.getLong(key + ".yaw");
            String world = cs.getString(key + ".location.world");

            Location loc = new Location(getServer().getWorld(world), x, y, z);
            loc.setPitch(pitch);
            loc.setYaw(yaw);

            Warp warp = new Warp(name, msg, perm, loc, commands);
            warps.put(name, warp);
        }

        getServer().getPluginManager().registerEvents(new EVT_Warp(), this);
    }



    public void onDisable()
    {
        for (Warp warp : warps.values())
        {
            getConfig().set("locations." + warp.getName() + ".message", warp.getMessage());
            getConfig().set("locations." + warp.getName() + ".permission", warp.getPermission());
            getConfig().set("locations." + warp.getName() + ".commands", warp.getCommands());
            getConfig().set("locations." + warp.getName() + ".location.x", warp.getLocation().getX());
            getConfig().set("locations." + warp.getName() + ".location.y", warp.getLocation().getY());
            getConfig().set("locations." + warp.getName() + ".location.z", warp.getLocation().getZ());
            getConfig().set("locations." + warp.getName() + ".location.pitch", warp.getLocation().getPitch());
            getConfig().set("locations." + warp.getName() + ".location.yaw", warp.getLocation().getYaw());
            getConfig().set("locations." + warp.getName() + ".location.world", warp.getLocation().getWorld().getName());
        }
        saveConfig();
    }



    class CMD_SetLocation implements CommandExecutor
    {
        public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
        {
            if (!(cs instanceof Player))
                return false;
            Player p = (Player) cs;

            if (!p.hasPermission("spawn.setlocation"))
            {
                p.sendMessage("§cYou do not have permission to set locations!");
                return false;
            }

            if (args.length != 1)
            {
                p.sendMessage("§cInvalid command! Try /setlocation <name>");
                return false;
            }

            if (!warps.containsKey(args[0]))
            {
                warps.put(args[0], new Warp(args[0], "abc", "test.perm", p.getLocation(), Arrays.asList("")));
                p.sendMessage("§aCreated location: " + args[0] + "!");
            } else
            {
                warps.get(args[0]).setLocation(p.getLocation());
                p.sendMessage("§aUpdated location for " + args[0] + "!");
            }

            return false;
        }
    }



    class EVT_Warp implements Listener
    {
        @EventHandler
        public void onCommand(PlayerCommandPreprocessEvent e)
        {
            for (Warp warp : warps.values())
            {
                for (String cmd : warp.getCommands())
                {
                    if (e.getMessage().equals(cmd))
                    {
                        if (e.getPlayer().hasPermission(warp.getPermission()))
                        {
                            e.getPlayer().teleport(warp.getLocation());
                            e.getPlayer().sendMessage(warp.getMessage());
                            e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }



    class Warp
    {
        String name;
        String message;
        String permission;
        Location location;
        List<String> commands;



        public Warp(String name, String msg, String perm, Location loc, List<String> commands)
        {
            this.name = name;
            this.message = msg;
            this.permission = perm;
            this.location = loc;
            this.commands = commands;
        }



        public String getName()
        {
            return name;
        }



        public String getMessage()
        {
            return message;
        }



        public String getPermission()
        {
            return permission;
        }



        public Location getLocation()
        {
            return location;
        }



        public List<String> getCommands()
        {
            return commands;
        }



        public void setLocation(Location loc)
        {
            this.location = loc;
        }
    }


}