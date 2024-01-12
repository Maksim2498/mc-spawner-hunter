package space.moontalk.mc.spawner_hunter;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;

import org.jetbrains.annotations.NotNull;

import lombok.val;

public class SpawnerHunter extends    JavaPlugin 
                           implements Listener {
    @EventHandler
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        val block = event.getBlock();
        val type  = block.getType();
        
        if (type != Material.SPAWNER) 
            return;

        val player   = event.getPlayer();
        val gameMode = player.getGameMode();

        if (gameMode == GameMode.CREATIVE)
            return;

        val inventory    = player.getInventory();
        val item         = inventory.getItemInMainHand();
        val enchantments = item.getEnchantments();

        if (!enchantments.containsKey(Enchantment.SILK_TOUCH)) 
            return;

        val world    = block.getWorld();
        val location = block.getLocation();
        val state    = (CreatureSpawner) block.getState();
        val dropItem = new ItemStack(type);
        val meta     = (BlockStateMeta) dropItem.getItemMeta();

        meta.setBlockState(state);
        dropItem.setItemMeta(meta);
        world.dropItemNaturally(location, dropItem);
        event.setExpToDrop(0);
    }

    @EventHandler
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        val block = event.getBlock();

        if (block.getType() != Material.SPAWNER)
            return;

        val item        = event.getItemInHand();
        val meta        = (BlockStateMeta) item.getItemMeta();
        val state       = (CreatureSpawner) meta.getBlockState();
        val newState    = (CreatureSpawner) block.getState(false);
        val spawnedType = state.getSpawnedType();

        newState.setSpawnedType(spawnedType);
    }

    @Override
    public void onEnable() {
        val server  = getServer();
        val manager = server.getPluginManager();

        manager.registerEvents(this, this);
    }
}
