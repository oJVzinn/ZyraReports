package com.zyramc.ojvzinn.reports.bungee.commands;

import dev.slickcollections.kiwizin.bungee.Bungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;

public abstract class Commands extends Command {
  
  public Commands(String name, String... aliases) {
    super(name, null, aliases);
    ProxyServer.getInstance().getPluginManager().registerCommand(Bungee.getInstance(), this);
  }
  
  public static void setupCommands() {
    new ReportCommand();
    new ReportsCommand();
  }
  
  public abstract void perform(CommandSender sender, String[] args);
  
  @Override
  public void execute(CommandSender sender, String[] args) {
    this.perform(sender, args);
  }
}
