package se.gory_moon.armorstandtest;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import se.gory_moon.armorstandtest.wrapper.WrapperPlayServerEntityDestroy;
import se.gory_moon.armorstandtest.wrapper.WrapperPlayServerEntityEquipment;
import se.gory_moon.armorstandtest.wrapper.WrapperPlayServerEntityMetadata;
import se.gory_moon.armorstandtest.wrapper.WrapperPlayServerSpawnEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Command implements CommandExecutor, TabCompleter {

    private static final List<String> COMMANDS = Arrays.asList("add", "remove");

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (args.length >= 1) {
                switch (args[0]) {
                    case "add":
                        add((Player) sender);
                        return true;
                    case "remove":
                        remove((Player) sender);
                        return true;
                }
            }
        }
        return false;
    }

    private void add(Player player) {
        WrapperPlayServerSpawnEntity packet = new WrapperPlayServerSpawnEntity();
        Location pos = player.getLocation();
        int entityID = 65000;

        packet.setX(pos.getX() + 1);
        packet.setY(pos.getY());
        packet.setZ(pos.getZ());
        packet.setUniqueId(UUID.randomUUID());
        packet.setType(EntityType.ARMOR_STAND);
        packet.setEntityID(entityID);
        packet.setPitch(0);
        packet.setYaw(0);
        packet.sendPacket(player);


        WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20); // Invisible
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true); // No Gravity
        byte statusMask = 0x08; // no base plate
        dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(14, WrappedDataWatcher.Registry.get(Byte.class)), statusMask); // Armor Stand status

        WrapperPlayServerEntityMetadata metadataPacket = new WrapperPlayServerEntityMetadata();
        metadataPacket.setMetadata(dataWatcher.getWatchableObjects());
        metadataPacket.setEntityID(entityID);
        metadataPacket.sendPacket(player);

        WrapperPlayServerEntityEquipment equipmentPacket = new WrapperPlayServerEntityEquipment();
        equipmentPacket.setEntityID(entityID);
        equipmentPacket.setSlotStackPair(EnumWrappers.ItemSlot.HEAD, new ItemStack(Material.RED_WOOL));
        equipmentPacket.sendPacket(player);
    }

    private void remove(Player player) {
        WrapperPlayServerEntityDestroy destroyPacket = new WrapperPlayServerEntityDestroy();
        destroyPacket.setEntityIds(new int[] { 65000 });
        destroyPacket.sendPacket(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
            Collections.sort(completions);
        }
        return completions;
    }
}
