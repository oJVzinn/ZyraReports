package com.zyramc.ojvzinn.reports.report;

import com.google.common.annotations.Beta;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.zyramc.ojvzinn.reports.Main;
import database.DataBase;
import database.databases.MySQL;
import dev.slickcollections.kiwizin.database.Database;
import dev.slickcollections.kiwizin.player.fake.FakeManager;
import dev.slickcollections.kiwizin.player.role.Role;
import dev.slickcollections.kiwizin.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReportManagerBukkit {

    private static final List<ReportManagerBukkit> REPORTS_CACHE = new ArrayList<>();
    private final String target;
    private final String accuser;
    private final String date;
    private final String reason;
    private Long totalReports;
    private String server = "Nenhum";
    private String lastViwer = "Ninguém";

    public static void setupReports() {
        for (ReportManagerBukkit reportManagerBukkit : Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).getAllReports("ProfileReports")) {
            ReportManagerBukkit a = createReport(reportManagerBukkit.getTarget(), reportManagerBukkit.accuser, reportManagerBukkit.getDate(), reportManagerBukkit.getReason(), true);
            a.setLastViwer(reportManagerBukkit.getLastViwer());
        }
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).closeConnection();
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).connection = null;

        Main.getInstance().getLogger().info("Foram carregados " + REPORTS_CACHE.size() + " reports no banco de dados!");
    }

    public static ReportManagerBukkit createReport(String target, String author, String date, String reason) {
        long totalReportsForPlayer = 0L;
        if (findByTarget(StringUtils.stripColors(target)) != null) {
            totalReportsForPlayer = findByTarget(StringUtils.stripColors(target)).getTotalReports();
        }
        totalReportsForPlayer++;
        if (findByTarget(StringUtils.stripColors(target)) != null) {
            findByTarget(StringUtils.stripColors(target)).setTotalReports(totalReportsForPlayer);
        }

        ReportManagerBukkit reportManagerBukkit = new ReportManagerBukkit(target, author, date, reason, totalReportsForPlayer);
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).addStatusDefaultPlayer(target, "ProfileReports");
        new BukkitRunnable() {
            @Override
            public void run() {
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "AUTHOR", author);
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "DATE ", date);
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "REASON ", reason);
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "LASTVIEWER ", "Ninguém");
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "TOTALREPORTS", "1");
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).closeConnection();
                Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).connection = null;
            }
        }.runTaskLater(Main.getInstance(), 20L);
        if (findByTarget(StringUtils.stripColors(target)) == null) {
            REPORTS_CACHE.add(reportManagerBukkit);
        }
        return reportManagerBukkit;
    }

    public static ReportManagerBukkit createReport(String target, String author, String date, String reason, boolean isload) {
        long totalReportsForPlayer = 0L;
        if (findByTarget(StringUtils.stripColors(target)) != null) {
            totalReportsForPlayer = findByTarget(StringUtils.stripColors(target)).getTotalReports();
        }
        totalReportsForPlayer++;
        if (findByTarget(StringUtils.stripColors(target)) != null) {
            findByTarget(StringUtils.stripColors(target)).setTotalReports(totalReportsForPlayer);
        }

        ReportManagerBukkit reportManagerBukkit = new ReportManagerBukkit(target, author, date, reason, totalReportsForPlayer);
        if (findByTarget(StringUtils.stripColors(target)) == null) {
            REPORTS_CACHE.add(reportManagerBukkit);
        }
        return reportManagerBukkit;
    }

    public static void deleteReport(String target) {
        try {
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).deleteProfiler(target, "ProfileReports");
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).closeConnection();
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).connection = null;
            findByTarget(StringUtils.stripColors(target)).destroy();
        } catch (Exception ignored) {}
    }

    public static void deleteReport(String target, boolean isPluginMessage) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("REPORT");
        output.writeUTF("DELETE");
        output.writeUTF(target);
        Bukkit.getServer().sendPluginMessage(Main.getInstance(), "zReports", output.toByteArray());
    }

    public static void deleteAllReports() {
        for (String target : getTargets()) {
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).deleteProfiler(target, "ProfileReports");
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).closeConnection();
            Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).connection = null;
            ByteArrayDataOutput output = ByteStreams.newDataOutput();
            output.writeUTF("REPORT");
            output.writeUTF("DELETE");
            output.writeUTF(target);
            Bukkit.getServer().sendPluginMessage(Main.getInstance(), "zReports", output.toByteArray());
        }
        REPORTS_CACHE.clear();
    }

    public static ReportManagerBukkit findByTarget(String target) {
        return REPORTS_CACHE.stream().filter(reportManagerBukkit -> StringUtils.stripColors(reportManagerBukkit.getTarget()).equals(target)).findFirst().orElse(null);
    }

    public static List<ReportManagerBukkit> getReportsCache() {
        return REPORTS_CACHE;
    }

    public static List<String> getTargets() {
        return REPORTS_CACHE.stream().map(ReportManagerBukkit::getTarget).collect(Collectors.toList());
    }

    public ReportManagerBukkit(String target, String accuser, String date, String reason, Long totalReports) {
        this.target = target;
        this.accuser = accuser;
        this.date = date;
        this.reason = reason;
        this.totalReports = totalReports;
    }

    public String getAccuser() {
        return accuser;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public String getTarget() {
        return target;
    }

    public Long getTotalReports() {
        return totalReports;
    }

    public String getLastViwer() {
        return lastViwer;
    }

    public void setLastViwer(String lastViwer) {
        this.lastViwer = lastViwer;
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).updateStatusPlayer(target, "ProfileReports", "LASTVIEWER", lastViwer);
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).closeConnection();
        Objects.requireNonNull(DataBase.getDatabase(MySQL.class)).connection = null;
    }

    public ItemStack getIcon(String online) {
        String nick = FakeManager.getFake(target) != null ? FakeManager.getFake(target) : target;
        ItemStack itemStack = new ItemStack(Material.SKULL_ITEM);
        itemStack.setDurability((short) 3);
        SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
        meta.setOwner(StringUtils.stripColors(target));
        meta.setDisplayName(Role.getColored(target, true) + " §e(" + Role.getPrefixed(nick) + "§e)");
        List<String> lore = new ArrayList<>();
        lore.add("§fTotal de reports: §e§n" + totalReports);
        lore.add("§fVisualizado por: ");
        lore.add("§f■ §7" + lastViwer);
        lore.add("");
        lore.add("§fAutor: " + accuser);
        lore.add("§fMotivo: §a" + reason);
        lore.add("§fData do ocorrido: §a" + date);
        lore.add("");
        lore.add("§8Ações:");
        lore.add(" §8* §7Botão esquerdo teleporta até o jogador");
        lore.add(" §8* §7Botão direito deleta este report");
        lore.add("");
        lore.add(online.contains(StringUtils.stripColors(target)) ? "§aOnline" : "§cOffline");
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public void destroy() {
        REPORTS_CACHE.remove(this);
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @Beta
    public void teleportePlayer(Player player) {
        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("ASSISTIR");
        output.writeUTF(StringUtils.stripColors(target));
        output.writeUTF(player.getName());
        player.sendPluginMessage(Main.getInstance(), "zReports", output.toByteArray());
    }

    public void setTotalReports(Long totalReports) {
        this.totalReports = totalReports;
    }
}
