package me.mccoder.com;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class FilterCommand
  implements CommandExecutor
{
  private final AntiBook plugin;
  
  public FilterCommand(AntiBook plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String s, String[] strings)
  {
    if ((command.getName().equalsIgnoreCase("filter")) && (sender.hasPermission("antibook.filter")))
    {
      if ((sender instanceof Player))
      {
        Player player = (Player)sender;
        ItemStack book = player.getItemInHand();
        if ((book != null) && (book.getType() == Material.WRITTEN_BOOK))
        {
          ItemStack newBook = this.plugin.getFilter().filterBook(book);
          if (newBook != null)
          {
            player.getInventory().addItem(new ItemStack[] { newBook });
            sender.sendMessage(ChatColor.AQUA + "A filtered version of this book has been set to your inventory");
          }
          else
          {
            sender.sendMessage(ChatColor.AQUA + "This book does not contain any illegal JSON code");
          }
        }
        else
        {
          sender.sendMessage(ChatColor.RED + "You must hold a written book in your hand to filter");
        }
      }
      else
      {
        sender.sendMessage(ChatColor.RED + "This command can only be used by a player!");
      }
      return true;
    }
    return false;
  }
}
