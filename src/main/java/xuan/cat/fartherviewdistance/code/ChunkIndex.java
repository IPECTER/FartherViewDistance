package xuan.cat.fartherviewdistance.code;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import xuan.cat.fartherviewdistance.api.branch.BranchMinecraft;
import xuan.cat.fartherviewdistance.api.branch.BranchPacket;
import xuan.cat.fartherviewdistance.code.branch.v17.Branch_17_Minecraft;
import xuan.cat.fartherviewdistance.code.branch.v17.Branch_17_Packet;
import xuan.cat.fartherviewdistance.code.branch.v18.Branch_18_Minecraft;
import xuan.cat.fartherviewdistance.code.branch.v18.Branch_18_Packet;
import xuan.cat.fartherviewdistance.code.branch.v19.Branch_19_Minecraft;
import xuan.cat.fartherviewdistance.code.branch.v19.Branch_19_Packet;
import xuan.cat.fartherviewdistance.code.branch.v20.Branch_20_Minecraft;
import xuan.cat.fartherviewdistance.code.branch.v20.Branch_20_Packet;
import xuan.cat.fartherviewdistance.code.command.Command;
import xuan.cat.fartherviewdistance.code.command.CommandSuggest;
import xuan.cat.fartherviewdistance.code.data.ConfigData;
import xuan.cat.fartherviewdistance.code.data.viewmap.ViewShape;

public final class ChunkIndex extends JavaPlugin {
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
            case "v1_17_R1" -> {
                branchPacket = new Branch_17_Packet();
                branchMinecraft = new Branch_17_Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.SQUARE, branchMinecraft, branchPacket);
            }
            case "v1_18_R1" -> {
                branchPacket = new Branch_18_Packet();
                branchMinecraft = new Branch_18_Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_19_R3" -> {
                branchPacket = new Branch_19_Packet();
                branchMinecraft = new Branch_19_Minecraft();
                chunkServer = new ChunkServer(configData, this, ViewShape.ROUND, branchMinecraft, branchPacket);
            }
            case "v1_20_R1" -> {
                branchPacket = new Branch_20_Packet();
                branchMinecraft = new Branch_20_Minecraft();
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
        if (chunkServer != null)
            chunkServer.close();
    }

}
