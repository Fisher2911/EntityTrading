package me.masterofthefish.entitytrading.config;

import dev.lone.itemsadder.api.CustomStack;
import me.masterofthefish.entitytrading.EntityTrading;
import me.masterofthefish.entitytrading.trades.*;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeLoader {

    private final EntityTrading plugin;
    private final TradeManager tradeManager;

    public TradeLoader(final EntityTrading plugin) {
        this.plugin = plugin;
        this.tradeManager = plugin.getTradeManager();
    }

    public void load() {
        final File file = Path.of(plugin.getDataFolder().getPath(), "trades").toFile();
        final File adapterFile = Path.of(plugin.getDataFolder().getPath(), "adapters.zip").toFile();
        if (!file.exists()) {
            plugin.saveResource("trades" + File.separator + "VILLAGER.yml", false);
        }
        if (!adapterFile.exists()) {
            plugin.saveResource("adapters.zip", false);
        }
        final File[] files = file.listFiles();
        if (files == null) {
            plugin.getLogger().severe("No Entity Trade Files Found");
            return;
        }
        for (final File tradeFile : files) {
            loadTrades(tradeFile);
        }
    }

    private void loadTrades(final File file) {
        final YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        final ConfigurationSection levelSection = configuration.getConfigurationSection("levels");
        if (levelSection == null) {
            return;
        }
        final Map<Integer, TradeLevel> tradeLevelMap = new HashMap<>();
        for (final String key : levelSection.getKeys(false)) {
            final int pointsRequired = levelSection.getInt(key + ".required-points");
            final int tradesShown = levelSection.getInt(key + ".trades-shown");
            final ConfigurationSection possibleTrades = levelSection.getConfigurationSection(key + ".possible-trades");
            if (possibleTrades == null) {
                continue;
            }
            final List<Trade> trades = getTrades(possibleTrades);
            try {
                final int level = Integer.parseInt(key);
                final TradeLevel tradeLevel = new TradeLevel(level, pointsRequired, trades, tradesShown);
                tradeLevelMap.put(level, tradeLevel);
            } catch (final NumberFormatException ignored) {
            }
        }
        final String id = file.getName().replace(".yml", "");
        final EntityTrades entityTrades = new EntityTrades(id, tradeLevelMap);
        tradeManager.setEntityTrades(id,
                entityTrades);
    }

    private List<Trade> getTrades(final ConfigurationSection section) {
        final List<Trade> trades = new ArrayList<>();
        for (final String key : section.getKeys(false)) {
            final int minPoints = section.getInt(key + ".points-given-min");
            final int maxPoints = section.getInt(key + ".points-given-max");
            final ConfigurationSection firstItemsSection = section.getConfigurationSection(key + ".sell.1");
            final ConfigurationSection secondItemsSection = section.getConfigurationSection(key + ".sell.2");
            final ConfigurationSection receivedItemSection = section.getConfigurationSection(key + ".receive");
            if (firstItemsSection == null || receivedItemSection == null) {
                continue;
            }
            final TradeItem firstItem = loadTradeItem(firstItemsSection);
            final TradeItem received = loadTradeItem(receivedItemSection);
            final Trade.TradeBuilder tradeBuilder = Trade.TradeBuilder.create().
                    setTradePoints(new TradePoints(minPoints, maxPoints)).
                    setFirstGivenItem(firstItem).
                    setReceivedItem(received);
            if (secondItemsSection != null) {
                final TradeItem secondIem = loadTradeItem(secondItemsSection);
                tradeBuilder.setSecondGivenItem(secondIem);
            }
            trades.add(tradeBuilder.build());
        }
        return trades;
    }

    private TradeItem loadTradeItem(final ConfigurationSection section) {
        final String type = section.getString("type");
        if (type == null) {
            return null;
        }
        ItemStack itemStack;
        if (type.equalsIgnoreCase("vanilla")) {
            itemStack = loadVanillaItem(section);
        } else if (type.equalsIgnoreCase("item-adder")) {
            itemStack = loadItemAdderItem(section);
        } else {
            return null;
        }
        final int min = section.getInt("min-amount");
        final int max = section.getInt("max-amount");
        return new TradeItem(itemStack, min, max);
    }

    private ItemStack loadVanillaItem(final ConfigurationSection section) {
        String materialType = section.getString("material");
        if (materialType == null) {
            plugin.getLogger().severe("Error loading material from section: " + section +
                    ", the material is not defined!");
            return null;
        }
        materialType = materialType.toUpperCase();
        try {
            return new ItemStack(Material.valueOf(materialType));
        } catch (final IllegalArgumentException exception) {
            plugin.getLogger().severe("Error loading material: " + materialType + " from " + section.getName());
            return null;
        }
    }

    private ItemStack loadItemAdderItem(final ConfigurationSection section) {
        final String itemId = section.getString("item-id");
        final CustomStack customStack = CustomStack.getInstance(itemId);
        if (customStack == null) {
            return null;
        }
        return customStack.getItemStack();
    }
}