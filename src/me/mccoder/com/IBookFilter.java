package me.mccoder.com;

import java.util.List;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

public abstract interface IBookFilter
{
  public abstract void setLogger(Logger paramLogger);
  
  public abstract void setHoverMsg(String paramString);
  
  public abstract void setFilterActions(List<String> paramList);
  
  public abstract ItemStack filterBook(ItemStack paramItemStack);
  
  public abstract Object filterBook(Object paramObject);
}
