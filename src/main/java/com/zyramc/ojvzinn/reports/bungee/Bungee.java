package com.zyramc.ojvzinn.reports.bungee;

import com.zyramc.ojvzinn.reports.bungee.commands.Commands;
import com.zyramc.ojvzinn.reports.bungee.listeners.PluginMessageListeners;
import com.zyramc.ojvzinn.reports.bungee.reports.ReportManager;
import net.md_5.bungee.api.plugin.Plugin;

public class Bungee extends Plugin {

    private static Bungee plugin;

    @Override
    public void onLoad() {
        plugin = this;
        getProxy().registerChannel("zReports");
        getProxy().getPluginManager().registerListener(this, new PluginMessageListeners());
    }

    @Override
    public void onEnable() {
        Commands.setupCommands();

        getLogger().info("O plugin iniciou com sucesso!");
    }

    @Override
    public void onDisable() {
        getLogger().info("O plugin desligou com sucesso!");
    }

    public static Bungee getPlugin() {
        return plugin;
    }
}
