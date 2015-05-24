package me.mccoder.com.v1_8_R3;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.mccoder.com.IBookFilter;
import net.minecraft.server.v1_8_R3.ChatClickable;
import net.minecraft.server.v1_8_R3.ChatClickable.EnumClickAction;
import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.ChatHoverable;
import net.minecraft.server.v1_8_R3.ChatHoverable.EnumHoverAction;
import net.minecraft.server.v1_8_R3.ChatModifier;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.ItemWrittenBook;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

public class BookFilter
  implements IBookFilter
{
  private static final String PAGES_KEY = "pages";
  private static final int PAGES_KEY_VALUE = 9;
  private static final int PAGES_LIST_VALUE = 8;
  private Logger logger = Logger.getLogger(BookFilter.class.getName());
  private Set<ChatClickable.EnumClickAction> filterActions = EnumSet.of(ChatClickable.EnumClickAction.RUN_COMMAND, ChatClickable.EnumClickAction.OPEN_FILE, ChatClickable.EnumClickAction.OPEN_URL, ChatClickable.EnumClickAction.SUGGEST_COMMAND);
  private String hoverMsg = "Illegal content was filtered from this book";
  
  public org.bukkit.inventory.ItemStack filterBook(org.bukkit.inventory.ItemStack filterItem)
  {
    Object result = filterBook(CraftItemStack.asNMSCopy(filterItem));
    if (result != null) {
      return CraftItemStack.asBukkitCopy((net.minecraft.server.v1_8_R3.ItemStack)result);
    }
    return null;
  }
  
  public Object filterBook(Object filterItem)
  {
    if (filterItem == null) {
      return null;
    }
    if (!(filterItem instanceof net.minecraft.server.v1_8_R3.ItemStack)) {
      throw new IllegalArgumentException("Expected object of type net.minecraft.server.v1_8_R3.ItemStack. Received " + filterItem.getClass());
    }
    net.minecraft.server.v1_8_R3.ItemStack mcStack = (net.minecraft.server.v1_8_R3.ItemStack)filterItem;
    try
    {
      if (!(mcStack.getItem() instanceof ItemWrittenBook)) {
        return null;
      }
      mcStack = mcStack.cloneItemStack();
      
      NBTTagCompound tag = mcStack.hasTag() ? mcStack.getTag() : null;
      NBTTagList pages = (tag != null) && (tag.hasKeyOfType("pages", 9)) ? tag.getList("pages", 8) : null;
      
      boolean filtered = false;
      for (int i = 0; (pages != null) && (i < pages.size()); i++)
      {
        String jsonPage = pages.getString(i);
        IChatBaseComponent component;
        try
        {
          component = jsonPage != null ? IChatBaseComponent.ChatSerializer.a(jsonPage) : null;
        }
        catch (Exception e)
        {
          component = null;
          this.logger.log(Level.WARNING, "Unable to parse page {0} of book type {1} to IChatBaseComponent.This means it is probably not a JSON book and can be ignored.", new Object[] { Integer.valueOf(i), mcStack.getItem().getClass() });
          
          this.logger.log(Level.WARNING, "Page: {0}", jsonPage);
          this.logger.log(Level.WARNING, "Original Exception:", e);
        }
        List<IChatBaseComponent> subComponents = new ArrayList();
        addComponents(component, subComponents);
        
        boolean changedPage = false;
        for (IChatBaseComponent subComponent : subComponents)
        {
          ChatModifier modifier = subComponent.getChatModifier();
          ChatClickable clickable = modifier.h();
          if ((clickable != null) && (this.filterActions.contains(clickable.a())))
          {
            this.logger.log(Level.WARNING, "Filtered illegal content from item!");
            this.logger.log(Level.WARNING, subComponent.toString());
            
            modifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, new ChatComponentText(this.hoverMsg)));
            
            modifier.setChatClickable(null);
            subComponent.setChatModifier(modifier);
            
            changedPage = true;
            filtered = true;
          }
        }
        if (changedPage)
        {
          jsonPage = IChatBaseComponent.ChatSerializer.a(component);
          pages.a(i, new NBTTagString(jsonPage));
        }
      }
      if (filtered)
      {
        tag.set("pages", pages);
        mcStack.setTag(tag);
        return mcStack;
      }
      return null;
    }
    catch (Throwable e)
    {
      this.logger.log(Level.SEVERE, "Failed to filter book due to an unexpected exception", e);
    }
    return null;
  }
  
  private static void addComponents(IChatBaseComponent component, List<IChatBaseComponent> list)
  {
    if (component != null)
    {
      list.add(component);
      
      List<IChatBaseComponent> children = component.a();
      if (children != null) {
        for (IChatBaseComponent child : children) {
          addComponents(child, list);
        }
      }
    }
  }
  
  public void setLogger(Logger logger)
  {
    this.logger = logger;
  }
  
  public void setHoverMsg(String hoverMsg)
  {
    this.hoverMsg = hoverMsg;
  }
  
  public void setFilterActions(List<String> actions)
  {
    this.filterActions = EnumSet.noneOf(ChatClickable.EnumClickAction.class);
    for (String actionString : actions) {
      try
      {
        this.filterActions.add(ChatClickable.EnumClickAction.valueOf(actionString.toUpperCase()));
      }
      catch (IllegalArgumentException e)
      {
        this.logger.log(Level.WARNING, "Invalid ActionEnum: {0}", actionString.toUpperCase());
      }
    }
  }
}
