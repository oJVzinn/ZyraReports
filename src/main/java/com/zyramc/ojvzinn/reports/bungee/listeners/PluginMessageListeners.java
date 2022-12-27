package com.zyramc.ojvzinn.reports.bungee.listeners;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.zyramc.ojvzinn.reports.bungee.Bungee;
import com.zyramc.ojvzinn.reports.bungee.reports.ReportManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

public class PluginMessageListeners implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent evt) {
        if (evt.getTag().equalsIgnoreCase("zReports")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(evt.getData());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("Command")) {
                String type = in.readUTF();
                if (type.equalsIgnoreCase("Execute")) {
                    String command = in.readUTF();
                    String player = in.readUTF();
                    ProxiedPlayer proxiedPlayer = Bungee.getPlugin().getProxy().getPlayer(player);
                    if (proxiedPlayer != null) {
                        proxiedPlayer.chat(command);
                        proxiedPlayer.chat(command.replace("/", ""));
                    }
                }
            } else if (subChannel.equalsIgnoreCase("assistir")) {
                String target = in.readUTF();
                String playerSender = in.readUTF();
                ProxiedPlayer player = Bungee.getPlugin().getProxy().getPlayer(playerSender);
                ProxiedPlayer proxiedPlayerTarget = Bungee.getPlugin().getProxy().getPlayer(target);
                if (proxiedPlayerTarget == null) {
                    player.sendMessage(TextComponent.fromLegacyText("§cEste jogador não está online no momento."));
                    return;
                }

                if (!player.getServer().getInfo().equals(proxiedPlayerTarget.getServer().getInfo())) {
                    player.connect(proxiedPlayerTarget.getServer().getInfo());
                }


                Bungee.getPlugin().getProxy().getScheduler().schedule(Bungee.getPlugin(), ()-> {
                    if (!proxiedPlayerTarget.getServer().getInfo().getName().equalsIgnoreCase("RankUP")) {
                        ReportManager.sendCommandExecutor(player, "assistir " + target);
                    } else {
                        ReportManager.sendCommandExecutor(player, "tp " + target);
                    }
                }, 1L, TimeUnit.SECONDS);
            }
        }
    }

}
