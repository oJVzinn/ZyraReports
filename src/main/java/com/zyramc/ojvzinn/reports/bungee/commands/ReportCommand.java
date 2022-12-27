package com.zyramc.ojvzinn.reports.bungee.commands;

import com.zyramc.ojvzinn.reports.bungee.reports.ReportManager;
import dev.slickcollections.kiwizin.player.role.Role;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ReportCommand extends Commands {

    private static final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yy 'às' hh:mm");

    public ReportCommand() {
        super("report");
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (args.length < 1) {
            player.sendMessage(TextComponent.fromLegacyText("§cUso incorreto! Tente /report [player]"));
            return;
        }
        String target = args[0];

        if (args.length > 1) {
            String motivo = args[1];
            Date date = new Date();
            sf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
            String data = sf.format(date);
            ReportManager.createReport(Role.getColored(target), Role.getColored(player.getName()), data, motivo);
        } else {
            Date date = new Date();
            sf.setTimeZone(TimeZone.getTimeZone("GMT-3"));
            String data = sf.format(date);
            ReportManager.createReport(Role.getColored(target), Role.getColored(player.getName()), data, "Não Informado");
        }

        player.sendMessage(TextComponent.fromLegacyText("§aReport enviado com sucesso!"));
    }
}
