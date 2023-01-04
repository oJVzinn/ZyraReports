package com.zyramc.ojvzinn.reports.bungee.commands;

import com.zyramc.ojvzinn.reports.bungee.Bungee;
import com.zyramc.ojvzinn.reports.bungee.reports.ReportManager;
import dev.slickcollections.kiwizin.player.role.Role;
import dev.slickcollections.kiwizin.utils.StringUtils;
import dev.slickcollections.kiwizin.utils.TimeUtils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ReportCommand extends Commands {

    private static final SimpleDateFormat sf = new SimpleDateFormat("dd/MM/yy 'às' hh:mm");
    private static final Map<String, Long> PLAYER_DELAY = new HashMap<>();

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

        if (PLAYER_DELAY.containsKey(player.getName())) {
            Long timing = PLAYER_DELAY.get(player.getName());
            if (timing > System.currentTimeMillis()) {
                if (((double) (timing - System.currentTimeMillis()) / 1000) > 0.1) {
                    player.sendMessage(TextComponent.fromLegacyText("§cPara reportar outro jogador, é necessário aguardar " + TimeUtils.getTimeUntil(timing) + " segundos."));
                    return;
                }
            }
        }

        String target = args[0];
        String realNick = null;
        boolean isfake = false;
        dev.slickcollections.kiwizin.bungee.Bungee bungee = dev.slickcollections.kiwizin.bungee.Bungee.getInstance();

        if (bungee.getFakeNames().values().stream().anyMatch(s -> StringUtils.stripColors(s).equalsIgnoreCase(target))) {
            realNick = bungee.getFakeNames().keySet().stream().filter(s -> StringUtils.stripColors(dev.slickcollections.kiwizin.bungee.Bungee.getFake(s)).equalsIgnoreCase(target)).findFirst().get();
            isfake = true;
        }

        if (isfake ? Bungee.getPlugin().getProxy().getPlayer(realNick) == null : Bungee.getPlugin().getProxy().getPlayer(target) == null) {
            player.sendMessage(TextComponent.fromLegacyText("§cEste jogador no momento se encontra offline."));
            return;
        }

        Bungee.getPlugin().getLogger().info(realNick);

        if (target.equalsIgnoreCase(player.getName())) {
            player.sendMessage(TextComponent.fromLegacyText("§cNão é possível reportar a sí mesmo."));
            return;
        }

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

        player.sendMessage(TextComponent.fromLegacyText(" §a* Você reportou o jogador " + Role.getColored(target) + "§a. Um membro de nossa equipe §afoi notificado e o comportamento deste jogador §aserá analisado em breve.\n\n §a* O uso abusivo deste comando poderá §aresultar em punição."));
        PLAYER_DELAY.put(player.getName(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10L));
    }
}
