package me.mccoder.com;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class AntiBook
  extends JavaPlugin
{
  ConfigHandler config;
  IBookFilter filter;
  
  public void onDisable()
  {
    Bukkit.getScheduler().cancelTasks(this);
    
    PluginDescriptionFile desc = getDescription();
    getLogger().log(Level.INFO, "{0} vers. {1} by McCoder deactivated", new String[] { desc.getName(), desc.getVersion() });
  }
  
  public void onEnable()
  {
    boolean enable = true;
    
    loadConfig();
    
    String version = Bukkit.getServer().getClass().getCanonicalName();
    version = version.substring(0, version.lastIndexOf('.'));
    version = version.substring(version.lastIndexOf('.') + 1);
    switch (version)
    {
    case "v1_8_R1": 
      this.filter = new me.mccoder.com.v1_8_R1.BookFilter();
      getLogger().log(Level.INFO, "Detected supported server version {0}", version);
      break;
    case "v1_8_R2": 
      this.filter = new me.mccoder.com.v1_8_R2.BookFilter();
      getLogger().log(Level.INFO, "Detected supported server version {0}", version);
      break;
    case "v1_8_R3": 
      this.filter = new me.mccoder.com.v1_8_R3.BookFilter();
      getLogger().log(Level.INFO, "Detected supported server version {0}", version);
      break;
    default: 
      getLogger().log(Level.SEVERE, "Unsupported server version {0}. Disabling plugin.", version);
      getLogger().log(Level.SEVERE, "Please check for updates at {0}", getDescription().getWebsite());
      enable = false;
      Bukkit.getPluginManager().disablePlugin(this);
    }
    if (enable)
    {
      this.filter.setLogger(getLogger());
      this.filter.setHoverMsg(this.config.getHoverMsg());
      this.filter.setFilterActions(this.config.getFilterActions());
      
      registerCommands();
      
      registerEvents();
      
      PluginDescriptionFile desc = getDescription();
      getLogger().log(Level.INFO, "{0} vers. {1} by McCoder activated", new String[] { desc.getName(), desc.getVersion() });
    }
  }
  
  private void loadConfig()
  {
    this.config = new ConfigHandler(this);
    this.config.loadConfig();
  }
  
  public IBookFilter getFilter()
  {
    return this.filter;
  }
  
  private void registerCommands()
  {
    getCommand("filter").setExecutor(new FilterCommand(this));
  }
  
  private void registerEvents()
  {
    Bukkit.getPluginManager().registerEvents(new BookListener(this), this);
  }
}
