package me.masterofthefish.entitytrading.listeners;

import me.masterofthefish.entitytrading.EntityTrading;
import me.masterofthefish.entitytrading.trades.TradeManager;
import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.classes.VillagerInventory;
import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.events.VillagerTradeCompleteEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TradeListener implements Listener {

    private final EntityTrading plugin;
    private final TradeManager tradeManager;

    private final Map<UUID, UUID> playerTradingWithMap = new HashMap<>();

    public TradeListener(final EntityTrading plugin) {
        this.plugin = plugin;
        this.tradeManager = plugin.getTradeManager();
    }

    @EventHandler
    public void onClick(final PlayerInteractAtEntityEvent event) {
        final Player player = event.getPlayer();
        final Entity entity = event.getRightClicked();
        final VillagerInventory villagerInventory = tradeManager.getEntityTradeMenu(entity, player);
        if (villagerInventory == null) {
            return;
        }
        playerTradingWithMap.put(player.getUniqueId(), entity.getUniqueId());
        villagerInventory.open();
    }

//    @EventHandler
//    public void onClose(final VillagerInventoryCloseEvent event) {
//        final Player player = event.getPlayer();
//        playerTradingWithMap.remove(player.getUniqueId());
//    }

    @EventHandler
    public void onTrade(final VillagerTradeCompleteEvent event) {
        final Player player = event.getPlayer();
        final UUID entityUUID = playerTradingWithMap.get(player.getUniqueId());
        if (entityUUID == null) {
            return;
        }
        final Entity entity = Bukkit.getEntity(entityUUID);
        if (entity == null) {
            return;
        }
        tradeManager.addPoints(entity, event.getTrade());
        player.sendMessage("Trade Completed");
    }
}