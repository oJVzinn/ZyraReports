package com.zyramc.ojvzinn.reports.bungee.commands;

import com.zyramc.ojvzinn.reports.bungee.reports.ReportManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportsCommand extends Commands {

    public ReportsCommand() {
        super("reports");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("reports.see.menu")) {
            player.sendMessage(TextComponent.fromLegacyText("§cExclusivo para o grupo §eAjudante §cou superior."));
            return;
        }

        ReportManager.sendMenuReport(player);
    }
}
