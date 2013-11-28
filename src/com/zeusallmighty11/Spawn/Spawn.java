package com.zeusallmighty11.Spawn;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


/** Why are you viewing this? */
public class Spawn extends JavaPlugin
{
    Location loc;
    String msg;



    public void onEnable()
    {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new RespawnHandler(), this);

        getCommand("hub").setExecutor(new CMD_Hub());
        getCommand("setspawn").setExecutor(new CMD_SetSpawn());


        File dir = new File(getDataFolder() + "");
        if (!dir.exists())
            dir.mkdir();
        File config = new File(getDataFolder() + "/config.yml");
        if (!config.exists())
            saveDefaultConfig();

        double x = getConfig().getDouble("location.x");
        double y = getConfig().getDouble("location.y");
        double z = getConfig().getDouble("location.z");
        float pitch = getConfig().getLong("location.pitch");
        float yaw = getConfig().getLong("location.yaw");
        String w = getConfig().getString("location.world");

        loc = new Location(Bukkit.getServer().getWorld(w), x, y, z);
        loc.setPitch(pitch);
        loc.setYaw(yaw);
        msg = getConfig().getString("message").replace("&", "§");
    }



    public void onDisable()
    {
        getConfig().set("location.x", loc.getX());
        getConfig().set("location.y", loc.getY());
        getConfig().set("location.z", loc.getZ());
        getConfig().set("location.pitch", loc.getPitch());
        getConfig().set("location.yaw", loc.getYaw());
        getConfig().set("location.world", loc.getWorld().getName());
        saveConfig();
    }



    class RespawnHandler implements Listener
    {
        @EventHandler
        public void onRespawn(PlayerRespawnEvent e)
        {
            e.setRespawnLocation(loc);
        }
    }



    class CMD_Hub implements CommandExecutor
    {
        public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
        {
            if (!(cs instanceof Player))
                return false;
            Player p = (Player) cs;
            p.teleport(loc);
            p.sendMessage(msg);
            return false;
        }
    }



    class CMD_SetSpawn implements CommandExecutor
    {
        public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args)
        {
            if (!(cs instanceof Player))
                return false;
            Player p = (Player) cs;

            if (p.hasPermission("spawn.setspawn"))
            {
                loc = p.getLocation();
                p.sendMessage("§aSet spawn.");
            }
            return false;
        }
    }

}