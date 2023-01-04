package com.zyramc.ojvzinn.reports.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.zyramc.ojvzinn.reports.menus.MenuReportList;
import com.zyramc.ojvzinn.reports.report.ReportManagerBukkit;
import dev.slickcollections.kiwizin.utils.StringUtils;
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
                        ReportManagerBukkit reportManagerBukkit = ReportManagerBukkit.createReport(target, author, date, reason);
                        reportManagerBukkit.setServer("");
                        break;
                    }

                    if (type.equalsIgnoreCase("delete")) {
                        String target = in.readUTF();
                        ReportManagerBukkit.deleteReport(target);
                        break;
                    }

                    if (type.equalsIgnoreCase("menu")) {
                        Player player = Bukkit.getPlayer(in.readUTF());
                        String online = in.readUTF();
                        new MenuReportList(player, online);
                        break;
                    }

                    if (type.equalsIgnoreCase("message")) {
                        String acusado = in.readUTF();
                        String message = in.readUTF();

                        if (ReportManagerBukkit.findByTarget(StringUtils.stripColors(acusado)) != null) {
                            try {
                                Player reporter = Bukkit.getPlayer(StringUtils.stripColors(ReportManagerBukkit.findByTarget(acusado).getAccuser()));
                                reporter.sendMessage(StringUtils.formatColors(message));
                                break;
                            } catch (Exception e) {
                                break;
                            }
                        }
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
