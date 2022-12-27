package com.zyramc.ojvzinn.reports.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.zyramc.ojvzinn.reports.menus.MenuReportList;
import com.zyramc.ojvzinn.reports.report.ReportManagerBukkit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageListeners implements PluginMessageListener {

    public void onPluginMessageReceived(String channel, Player receiver, byte[] data) {
        if (channel.equals("zReports")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            String subChannel = in.readUTF();
            switch (subChannel) {
                case "REPORT": {
                    String type = in.readUTF();
                    if (type.equalsIgnoreCase("new")) {
                        String target = in.readUTF();
                        String author = in.readUTF();
                        String date = in.readUTF();
                        String reason = in.readUTF();
                        String servidor = in.readUTF();
                        ReportManagerBukkit reportManagerBukkit = ReportManagerBukkit.createReport(target, author, date, reason);
                        reportManagerBukkit.setServer(servidor);
                        break;
                    }

                    if (type.equalsIgnoreCase("menu")) {
                        Player player = Bukkit.getPlayer(in.readUTF());
                        String online = in.readUTF();
                        new MenuReportList(player, online);
                        break;
                    }
                    break;
                }

                case "COMMAND": {
                    String type = in.readUTF();
                    if (type.equalsIgnoreCase("execute")) {
                        String command = in.readUTF();
                        Player player = Bukkit.getPlayer(in.readUTF());
                        player.performCommand(command);
                    }
                }
            }
        }
    }
}
