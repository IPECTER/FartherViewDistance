package xuan.cat.fartherviewdistance.code;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xuan.cat.fartherviewdistance.api.branch.BranchMinecraft;
import xuan.cat.fartherviewdistance.api.branch.BranchPacket;
import xuan.cat.fartherviewdistance.api.event.PlayerSendExtendChunkEvent;
import xuan.cat.fartherviewdistance.code.command.Command;
import xuan.cat.fartherviewdistance.code.command.CommandSuggest;
import xuan.cat.fartherviewdistance.code.data.ConfigData;
import xuan.cat.fartherviewdistance.code.data.viewmap.ViewShape;

public final class ChunkIndex extends JavaPlugin implements Listener {
    //    private static ProtocolManager protocolManager;
    private static Plugin plugin;
    private static ChunkServer chunkServer;
    private static ConfigData configData;
    private static BranchPacket branchPacket;
    private static BranchMinecraft branchMinecraft;

    public static ChunkServer getChunkServer() {
        return chunkServer;
    }

    public static ConfigData getConfigData() {
        return configData;
    }

    public static Plugin getPlugin() {
        return plugin;
    }


    @Override
    public void onEnable() {
        plugin = this;
//        protocolManager = ProtocolLibrary.getProtocolManager();

        saveDefaultConfig();
        configData = new ConfigData(this, getConfig());

        // 檢測版本
        String version = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit", "").replace(".", "");

        switch (version) {
            case "v1_18_R2" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_18_R2.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_18_R2.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_19_R1" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_19_R1.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_19_R1.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_19_R2" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_19_R2.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_19_R2.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_19_R3" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_19_R3.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_19_R3.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_20_R1" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_20_R1.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_20_R1.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_20_R2" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_20_R2.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_20_R2.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_20_R3" -> {
                branchPacket = new xuan.cat.fartherviewdistance.code.branch.v1_20_R3.Packet();
                branchMinecraft = new xuan.cat.fartherviewdistance.code.branch.v1_20_R3.Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            default -> {
                Bukkit.getLogger().warning("[ FartherViewDistance ] Server version is unsupported version, Disabling FartherViewDistance...");
                this.getServer().getPluginManager().disablePlugin(this);
            }
        }

        // 初始化一些資料
        for (Player player : Bukkit.getOnlinePlayers())
            chunkServer.initView(player);
        for (World world : Bukkit.getWorlds())
            chunkServer.initWorld(world);

        Bukkit.getPluginManager().registerEvents(new ChunkEvent(chunkServer, branchPacket, branchMinecraft), this);
//        protocolManager.addPacketListener(new ChunkPacketEvent(plugin, chunkServer));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            ChunkPlaceholder.registerPlaceholder();
        }

        // 指令
        PluginCommand command = getCommand("viewdistance");
        if (command != null) {
            command.setExecutor(new Command(chunkServer, configData));
            command.setTabCompleter(new CommandSuggest(chunkServer, configData));
        }
    }

    @Override
    public void onDisable() {
//        ChunkPlaceholder.unregisterPlaceholder();
        if (chunkServer != null) chunkServer.close();
    }

}
