package com.zyramc.ojvzinn.reports.bungee.reports;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.zyramc.ojvzinn.reports.bungee.Bungee;
import dev.slickcollections.kiwizin.utils.StringUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ReportManager {

    public static void createReport(String target, String author, String date, String reason) {
        sendReport(target, author, date, reason);
        sendReportToStaffers(author, target, reason);
    }

    public static void sendReportToStaffers(String autor, String target, String reason) {
        Bungee.getPlugin().getProxy().getPlayers().forEach(player -> {
            TextComponent textComponent = new TextComponent();
            textComponent.setText("§e§lNOVO REPORT\n \n§fNick: §a" + target + "\n§fAcusado: §a" + autor + "\n§fMotivo: §a" + reason + "\n \n§eClique para ver!");
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClique para ver os reports")));
            textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reports"));
           if (player.hasPermission("sreport.see")) {
               player.sendMessage(textComponent);
           }
        });
    }

    public static void sendReport(String target, String author, String date, String reason) {
        ByteArrayDataOutput byteArrayDataInput = ByteStreams.newDataOutput();
        byteArrayDataInput.writeUTF("REPORT");
        byteArrayDataInput.writeUTF("NEW");
        byteArrayDataInput.writeUTF(target);
        byteArrayDataInput.writeUTF(author);
        byteArrayDataInput.writeUTF(date);
        byteArrayDataInput.writeUTF(reason);
        byteArrayDataInput.writeUTF(Bungee.getPlugin().getProxy().getPlayer(StringUtils.stripColors(target)).getServer().getInfo().getName());
        for (ServerInfo serverInfo : Bungee.getPlugin().getProxy().getServersCopy().values()) {
            serverInfo.sendData("zReports", byteArrayDataInput.toByteArray());
        }
    }

    public static void sendMenuReport(ProxiedPlayer player) {
        ByteArrayDataOutput byteArrayDataInput = ByteStreams.newDataOutput();
        byteArrayDataInput.writeUTF("REPORT");
        byteArrayDataInput.writeUTF("MENU");
        byteArrayDataInput.writeUTF(player.getName());
        byteArrayDataInput.writeUTF(Bungee.getPlugin().getProxy().getPlayers().toString());
        player.getServer().sendData("zReports", byteArrayDataInput.toByteArray());
    }

    public static void sendCommandExecutor(ProxiedPlayer player, String command) {
        ByteArrayDataOutput byteArrayDataInput = ByteStreams.newDataOutput();
        byteArrayDataInput.writeUTF("COMMAND");
        byteArrayDataInput.writeUTF("EXECUTE");
        byteArrayDataInput.writeUTF(command);
        byteArrayDataInput.writeUTF(player.getName());
        player.getServer().sendData("zReports", byteArrayDataInput.toByteArray());
    }
}
