package me.mccoder.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigHandler
  extends AbstractConfigHandler
{
  private static final String PATH_FILTER = "click_action_filter";
  private static final String PATH_HOVER = "hover_message";
  private static final String PATH_WARNING = "chat_warning";
  private static final String PATH_ON_CREATION = "activate.on_creation";
  private static final String PATH_ON_READ = "activate.on_read";
  private static final String PATH_DEBUG = "log_debug";
  private static final List<String> DEF_FILTER = Arrays.asList(new String[] { "RUN_COMMAND", "OPEN_FILE", "OPEN_URL", "SUGGEST_COMMAND", "TWITCH_USER_INFO" });
  private static final String DEF_HOVER = "Illegal content was filtered from this book";
  private static final List<String> DEF_WARNING = Arrays.asList(new String[] { "  ==== WARNING ==== WARNING ==== WARNING ====", "", "  DO NOT CLICK ON ANYTHING IN THIS BOOK!", "", "  It contains illegal code and is potential harmful", "", "  ==== WARNING ==== WARNING ==== WARNING ====" });
  private List<String> filterActions;
  private String hoverMsg;
  private List<String> chatWarning;
  private boolean onCreation;
  private boolean onRead;
  private boolean debug;
  
  public ConfigHandler(Plugin plugin)
  {
    super(plugin);
  }
  
  protected void loadData()
  {
    this.filterActions = this.config.getStringList("click_action_filter");
    
    this.hoverMsg = ChatColor.translateAlternateColorCodes('&', this.config.getString("hover_message"));
    
    this.chatWarning = new ArrayList(this.config.getStringList("chat_warning"));
    for (int i = 0; i < getChatWarning().size(); i++) {
      getChatWarning().set(i, ChatColor.RED + ChatColor.translateAlternateColorCodes('&', (String)getChatWarning().get(i)));
    }
    this.onCreation = this.config.getBoolean("activate.on_creation");
    
    this.onRead = this.config.getBoolean("activate.on_read");
    
    this.debug = this.config.getBoolean("log_debug");
  }
  
  protected void addDefaults()
  {
    this.config.addDefault("click_action_filter", DEF_FILTER);
    this.config.addDefault("hover_message", "Illegal content was filtered from this book");
    this.config.addDefault("chat_warning", DEF_WARNING);
    this.config.addDefault("activate.on_creation", Boolean.valueOf(true));
    this.config.addDefault("activate.on_read", Boolean.valueOf(true));
    this.config.addDefault("log_debug", Boolean.valueOf(false));
  }
  
  public List<String> getFilterActions()
  {
    return this.filterActions;
  }
  
  public String getHoverMsg()
  {
    return this.hoverMsg;
  }
  
  public List<String> getChatWarning()
  {
    return this.chatWarning;
  }
  
  public boolean isOnCreation()
  {
    return this.onCreation;
  }
  
  public boolean isOnRead()
  {
    return this.onRead;
  }
  
  public boolean isDebug()
  {
    return this.debug;
  }
}
