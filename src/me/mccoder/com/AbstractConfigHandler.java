package me.mccoder.com;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.FileConfigurationOptions;
import org.bukkit.plugin.Plugin;

public abstract class AbstractConfigHandler
{
  protected final Plugin plugin;
  protected FileConfiguration config;
  
  public AbstractConfigHandler(Plugin plugin)
  {
    this.plugin = plugin;
  }
  
  public void loadConfig()
  {
    this.config = this.plugin.getConfig();
    this.config.options().copyDefaults(true);
    addDefaults();
    saveConfig();
    loadData();
  }
  
  public void saveConfig()
  {
    this.plugin.saveConfig();
  }
  
  protected abstract void loadData();
  
  protected abstract void addDefaults();
}
