package me.mccoder.com;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

public class BookListener
  implements Listener
{
  private final AntiBook plugin;
  
  public BookListener(AntiBook plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onBookEdit(final PlayerEditBookEvent event)
  {
    if (this.plugin.config.isOnCreation()) {
      Bukkit.getScheduler().runTaskLater(this.plugin, new Runnable()
      {
        public void run()
        {
          Inventory inv = event.getPlayer().getInventory();
          for (int i = 0; i < inv.getSize(); i++)
          {
            ItemStack item = inv.getItem(i);
            if ((item != null) && (item.getType() == Material.WRITTEN_BOOK))
            {
              if (BookListener.this.plugin.config.isDebug()) {
                BookListener.this.plugin.getLogger().log(Level.INFO, "Filtering edited ItemStack: {0}", item);
              }
              ItemStack filtered = BookListener.this.plugin.getFilter().filterBook(item);
              if (filtered != null)
              {
                BookListener.this.plugin.getLogger().log(Level.WARNING, "Player {0} {1} created a book with illegal JSON content!", new Object[] { event.getPlayer().getName(), event.getPlayer().getUniqueId() });
                
                inv.setItem(i, filtered);
              }
            }
          }
        }
      }, 4L);
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void onBookRead(PlayerInteractEvent event)
  {
    if ((this.plugin.config.isOnRead()) && ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK)) && (event.getItem() != null) && (event.getItem().getType() == Material.WRITTEN_BOOK) && (!event.getPlayer().hasPermission("antibook.overridefilter")))
    {
      if (this.plugin.config.isDebug()) {
        this.plugin.getLogger().log(Level.INFO, "Filtering read ItemStack: {0}", event.getPlayer().getItemInHand());
      }
      ItemStack filtered = this.plugin.getFilter().filterBook(event.getPlayer().getItemInHand());
      if (filtered != null)
      {
        this.plugin.getLogger().log(Level.WARNING, "Player {0} {1} just tried to read a book with illegal JSON content!", new Object[] { event.getPlayer().getName(), event.getPlayer().getUniqueId() });
        
        event.getPlayer().setItemInHand(filtered);
        for (String line : this.plugin.config.getChatWarning()) {
          event.getPlayer().sendMessage(line);
        }
      }
    }
  }
}
