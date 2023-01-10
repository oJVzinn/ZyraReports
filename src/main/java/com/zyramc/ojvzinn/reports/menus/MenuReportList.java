package com.zyramc.ojvzinn.reports.menus;

import com.zyramc.ojvzinn.reports.Main;
import com.zyramc.ojvzinn.reports.report.ReportManagerBukkit;
import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.libraries.menu.PagedPlayerMenu;
import dev.slickcollections.kiwizin.player.role.Role;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class MenuReportList extends PagedPlayerMenu {

    private final Map<Integer, ReportManagerBukkit> REPORTS = new HashMap<>();
    private final String online;
    public MenuReportList(Player player, String onlines) {
        super(player, "Lista de Reports", 6);
        this.online = onlines;
        List<Integer> a = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42);
        this.onlySlots(a);
        nextPage = 26;
        previousPage = 18;
        List<ItemStack> itens = new ArrayList<>();

        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§cLimpar todos os Reports");
        itemStack.setItemMeta(meta);
        this.removeSlotsWith(itemStack, 49);

        ItemStack item = new ItemStack(Material.WEB);
        ItemMeta Itemmeta = item.getItemMeta();
        Itemmeta.setDisplayName("§cLista de Reports Vazia");
        item.setItemMeta(Itemmeta);
        this.removeSlotsWith(item, 22);

        int i = 0;
        for (ReportManagerBukkit reportManagerBukkit : ReportManagerBukkit.getReportsCache()) {
            try {
                this.removeSlotsWith(reportManagerBukkit.getIcon(online), 0);
                this.removeSlots(0);
                itens.add(reportManagerBukkit.getIcon(online));
                REPORTS.put(a.get(i), reportManagerBukkit);
                this.removeSlots(22);
            } catch (Exception ignored) {
                continue;
            }
            i++;
        }

        this.setItems(itens);
        open(player);
        register(Core.getInstance());
    }

    public void cancel() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onClickInventory(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            if (event.getInventory().equals(this.getCurrentInventory())) {
                ItemStack itemStack = event.getCurrentItem();
                if (itemStack != null) {
                    ReportManagerBukkit reportManagerBukkit = REPORTS.get(event.getSlot());
                    if (reportManagerBukkit != null) {
                        if (event.getClick().isLeftClick()) {
                            reportManagerBukkit.teleportePlayer(player);
                            reportManagerBukkit.setLastViwer(Role.getColored(player.getName()));
                        } else if (event.getClick().isRightClick()) {
                            ReportManagerBukkit.deleteReport(reportManagerBukkit.getTarget(), true);
                            player.sendMessage("§aReporte deletado com sucesso!");
                            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> new MenuReportList(player, online), 20L);
                        }
                    } else {
                        if (event.getSlot() == 49) {
                            ReportManagerBukkit.deleteAllReports();
                            Bukkit.getScheduler().runTaskLater(Main.getInstance(), ()-> new MenuReportList(player, online), 20L);
                            player.sendMessage("§aLista de reports limpa com sucesso!");
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerQuitListeners(PlayerQuitEvent event) {
        if (event.getPlayer().getOpenInventory().getTopInventory().equals(this.getCurrentInventory())) {
            cancel();
        }
    }

    @EventHandler
    public void onPlayerCloseInventoryListeners(InventoryCloseEvent event) {
        if (event.getInventory().equals(this.getCurrentInventory())) {
            cancel();
        }
    }
}
