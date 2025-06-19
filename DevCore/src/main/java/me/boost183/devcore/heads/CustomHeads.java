package me.boost183.devcore.heads;

import net.minecraft.server.v1_8_R3.MojangsonParser;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Class with a utility method to get custom heads as ItemStacks with Spigot.
 * The code below can easily be adjusted to work with any version
 * (only the craftbukkit/nms package names and perhaps the material need to be changed).
 *
 * If you want to compile it once and be compatible with any version, you only need to
 * find a way around the obfuscated packages. You can either use reflection for this
 * (as is common practice), or <a href="https://github.com/johnnyjayjay/compatre">compatre</a>
 * (much simpler).
 *
 * As it stands, the code is compiled against 1.8.8.
 *
 * @author JohnnyJayJay (https://github.com/johnnyjayjay)
 */

public class CustomHeads {

    /**
     * Creates a skull item stack that uses the given base64-encoded texture
     *
     * @param texture The texture value. Can be found on e.g. https://minecraft-heads.com/custom-heads/
     *                in the "Value" field.
     * @return an ItemStack with this texture.
     */
    public static ItemStack create(String texture) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
        try {
            String nbtString = String.format(
                    "{SkullOwner:{Id:%s,Properties:{textures:[{Value:\"%s\"}]}}}",
                    serializeUuid(UUID.randomUUID()), texture
            );
            NBTTagCompound nbt = MojangsonParser.parse(nbtString);
            nmsItem.setTag(nbt);
        } catch (Exception e) {
            throw new AssertionError("NBT Tag parsing failed - This should never happen.", e);
        }
        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    private static String serializeUuid(UUID uuid) {
        return '"' + uuid.toString() + '"';
    }
}