package com.zyramc.ojvzinn.reports.menus;

import com.zyramc.ojvzinn.reports.report.ReportManagerBukkit;
import dev.slickcollections.kiwizin.Core;
import dev.slickcollections.kiwizin.libraries.menu.UpdatablePlayerPagedMenu;
import dev.slickcollections.kiwizin.player.role.Role;
import dev.slickcollections.kiwizin.utils.StringUtils;
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

public class MenuReportList extends UpdatablePlayerPagedMenu {

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

        int i = 0;
        for (ReportManagerBukkit reportManagerBukkit : ReportManagerBukkit.getReportsCache()) {
            itens.add(reportManagerBukkit.getIcon(online));
            REPORTS.put(a.get(i), reportManagerBukkit);
            i++;
        }
        this.setItems(itens);

        if (itens.isEmpty()) {
            ItemStack itemStack = new ItemStack(Material.WEB);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName("§cLista de Reports Vazia");
            itemStack.setItemMeta(meta);
            this.removeSlotsWith(itemStack, 22);
        }

        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName("§cLimpar todos os Reports");
        itemStack.setItemMeta(meta);
        this.removeSlotsWith(itemStack, 49);
        update();
        open(player);
        register(Core.getInstance(), 20L);
    }

    @Override
    public void update() {
        List<ItemStack> itens = new ArrayList<>();
        for (ReportManagerBukkit reportManagerBukkit : ReportManagerBukkit.getReportsCache()) {
            itens.add(reportManagerBukkit.getIcon(online));
        }

        this.setItems(itens);
    }

    public void cancel() {
        super.cancel();
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
                            ReportManagerBukkit.deleteReport(reportManagerBukkit.getTarget());
                            player.closeInventory();
                            new MenuReportList(player, online);
                            player.sendMessage("§aReporte deletado com sucesso!");
                        }
                    } else {
                        if (event.getSlot() == 49) {
                            ReportManagerBukkit.deleteAllReports();
                            player.closeInventory();
                            new MenuReportList(player, online);
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
