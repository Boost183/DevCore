package me.boost183.devcore.menu;

import me.boost183.devcore.colors.ColorTranslator;
import me.boost183.devcore.exceptions.MenuManagerException;
import me.boost183.devcore.exceptions.MenuManagerNotSetupException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/*
    Defines the behavior and attributes of all menus in our plugin
 */
public abstract class Menu implements InventoryHolder {

    //Protected values that can be accessed in the menus
    protected PlayerMenuUtility playerMenuUtility;
    protected Player p;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeItem(Material.STAINED_GLASS_PANE, " ");

    //Constructor for Menu. Pass in a PlayerMenuUtility so that
    // we have information on who's menu this is and
    // what info is to be transferred
    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
        this.p = playerMenuUtility.getOwner();
    }

    //let each menu decide their name
    public abstract String getMenuName();

    //let each menu decide their slot amount
    public abstract int getSlots();

    public abstract boolean cancelAllClicks();

    //let each menu decide how the items in the menu will be handled when clicked
    public abstract void handleMenu(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException;

    //let each menu decide what items are to be placed in the inventory menu
    public abstract void setMenuItems();

    //When called, an inventory is created and opened for the player
    public void open() {
        //The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        //grab all the items specified to be used for this menu and add to inventory
        this.setMenuItems();

        //open the inventory for the player
        playerMenuUtility.getOwner().openInventory(inventory);
        playerMenuUtility.pushMenu(this);
    }

    public void back() throws MenuManagerException, MenuManagerNotSetupException {
        MenuManager.openMenu(playerMenuUtility.lastMenu().getClass(), playerMenuUtility.getOwner());
    }

    protected void reloadItems() {
        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, null);
        }
        setMenuItems();
    }

    protected void reload() throws MenuManagerException, MenuManagerNotSetupException {
        p.closeInventory();
        MenuManager.openMenu(this.getClass(), p);
    }

    //Overridden method from the InventoryHolder interface
    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /**
     * This will fill all of the empty slots with "filler glass"
     */
    //Helpful utility method to fill all remaining slots with "filler glass"
    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    /**
     * @param itemStack Placed into every empty slot when ran
     */
    public void setFillerGlass(ItemStack itemStack) {
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, itemStack);
            }
        }
    }

    /**
     *
     * @param slots
     * @param color
     */

    public void setMenuGlass(Integer slots, Short color){

        ItemStack COLOR_GLASS = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) color);
        ItemStack WHITE_GLASS = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);

        ItemMeta COLOR_GLASS_META = COLOR_GLASS.getItemMeta();
        COLOR_GLASS_META.setDisplayName(" ");
        COLOR_GLASS.setItemMeta(COLOR_GLASS_META);

        ItemMeta WHITE_GLASS_META = WHITE_GLASS.getItemMeta();
        WHITE_GLASS_META.setDisplayName(" ");
        WHITE_GLASS.setItemMeta(WHITE_GLASS_META);

        for (int i = slots-9; i < slots; i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, WHITE_GLASS);
            }
        }

        for (int i = 0; i < 9; i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, COLOR_GLASS);
            }
        }
    }

    /**
     * @param material The material to base the ItemStack on
     * @param displayName The display name of the ItemStack
     * @param lore The lore of the ItemStack, with the Strings being automatically color coded with ColorTranslator
     * @return The constructed ItemStack object
     */
    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);

        //Automatically translate color codes provided
        itemMeta.setLore(Arrays.stream(lore).map(ColorTranslator::translateColorCodes).collect(Collectors.toList()));
        item.setItemMeta(itemMeta);

        return item;
    }

}


