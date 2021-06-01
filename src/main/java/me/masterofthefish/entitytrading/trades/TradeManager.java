package me.masterofthefish.entitytrading.trades;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import me.masterofthefish.entitytrading.EntityTrading;
import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.classes.VillagerInventory;
import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.classes.VillagerTrade;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class TradeManager {

    private final EntityTrading plugin;
    private final MythicMobs mythicMobs;
    private final NamespacedKey dataKey;
    private final NamespacedKey pointsKey;

    // String is the mythic mobs id, or the entity type
    private final Map<String, EntityTrades> entityTradesMap = new HashMap<>();
    private final Map<UUID, EntityTradeData> entityTradeDataMap = new HashMap<>();

    public TradeManager(final EntityTrading plugin) {
        this.plugin = plugin;
        this.mythicMobs = plugin.getMythicMobs();
        this.dataKey = new NamespacedKey(plugin, "trades");
        this.pointsKey = new NamespacedKey(plugin, "points");
    }

    public void setEntityTrades(final String type, final EntityTrades entityTrades) {
        this.entityTradesMap.put(type, entityTrades);
    }

    public boolean hasTrades(final Entity entity) {
        final PersistentDataContainer container = entity.getPersistentDataContainer();
        return container.has(dataKey, PersistentDataType.STRING)/* && container.get(dataKey, PersistentDataType.STRING) != null*/;
    }


    // Example Id:
    // type=level_tradenum:itemcost,itemcost,itemcost-level_tradenum:itemcost,itemcost,itemcost
    // villager=1-1:5,3,6_
    public void loadTrades(final Entity entity) {
        final UUID uuid = entity.getUniqueId();
        if (entityTradeDataMap.get(uuid) != null) {
            return;
        }
        if (!hasTrades(entity)) {
            if (getEntityTrades(entity) == null) {
                return;
            }
            randomizeEntityTrades(entity);
        }
        final PersistentDataContainer container = entity.getPersistentDataContainer();
        final int points = toInt(container.get(pointsKey, PersistentDataType.INTEGER));
        final String data = container.get(dataKey, PersistentDataType.STRING);
        if (data == null) {
            return;
        }
        final String[] trades = data.split("=")[1].split("-");

        final EntityTrades entityTrades = getEntityTrades(data.split("=")[0], trades);
        this.entityTradeDataMap.put(entity.getUniqueId(), new EntityTradeData(entityTrades, points));
    }

    private EntityTrades getEntityTrades(final String id, final String[] trades) {
        final Map<Integer, TradeLevel> tradeLevelMap = new HashMap<>();
        for (final String tradeData : trades) {
            final String[] levelInfo = tradeData.split("_");

            final int level = Integer.parseInt(levelInfo[0]);
            final int tradeNum = Integer.parseInt(levelInfo[1].split(":")[0]);

            final TradeLevel tradeLevel = getTradeLevel(id, level);
            if (tradeLevel == null) {
                return null;
            }
            final Trade trade = tradeLevel.getTrade(tradeNum);

            final String[] itemCosts = tradeData.split(":")[1].split(",");
            final int firstItemCost = Integer.parseInt(itemCosts[0]);
            final int secondItemCost = Integer.parseInt(itemCosts[1]);
            final int receivedItemCost = Integer.parseInt(itemCosts[2]);

            final Trade.TradeBuilder tradeBuilder = Trade.TradeBuilder.create();
            tradeBuilder.
                    setTradePoints(trade.getTradePoints()).
                    setFirstGivenItem(
                            new TradeItem(trade.getFirstGivenItem().getItemStack(), firstItemCost, firstItemCost + 1)
                    ).setReceivedItem(
                    new TradeItem(trade.getReceivedItem().getItemStack(), receivedItemCost, receivedItemCost + 1)
            );
            if (trade.requiresTwoItems()) {
                tradeBuilder.setSecondGivenItem(new TradeItem(trade.getSecondGivenItem().getItemStack(), secondItemCost, secondItemCost + 1));
            }
            final Trade entityTrade = tradeBuilder.build();
            final TradeLevel entityTradeLevel = tradeLevelMap.computeIfAbsent(level, l -> new TradeLevel(level, tradeLevel.getPointsRequired(), new ArrayList<>(), tradeLevel.getTradesShown()));
            entityTradeLevel.addTrade(entityTrade);
        }
        return new EntityTrades(id, tradeLevelMap);
    }

    public void randomizeEntityTrades(final Entity entity) {
        final PersistentDataContainer container = entity.getPersistentDataContainer();
        container.set(pointsKey, PersistentDataType.INTEGER, 0);
        final EntityTrades entityTrades = getEntityTrades(entity);
        if (entityTrades == null) {
            return;
        }
        final EntityTrades randomTrades = entityTrades.getSetEntityTrades();
        entity.getPersistentDataContainer().set(dataKey, PersistentDataType.STRING, randomTrades.serialize());
    }

    public EntityTrades getEntityTrades(final Entity entity) {
        final Optional<ActiveMob> activeMob = mythicMobs.getMobManager().getActiveMob(entity.getUniqueId());
        if (activeMob.isEmpty()) {
            return entityTradesMap.get(entity.getType().toString());
        }
        return entityTradesMap.get(activeMob.get().getType().getInternalName());
    }

    public VillagerInventory getEntityTradeMenu(final Entity entity, final Player player) {
        final UUID uuid = entity.getUniqueId();
        loadTrades(entity);
        final EntityTradeData tradeData = entityTradeDataMap.get(uuid);
        if (tradeData == null) {
            return null;
        }
        final int entityPoints = getEntityPoints(entity);
        final EntityTrades entityTrades = tradeData.getEntityTrades();
        final List<Integer> levels = entityTrades.getAvailableLevels(entityPoints);
        final List<VillagerTrade> villagerTrades = new ArrayList<>();
        for(final int level : levels) {
            final List<Trade> trades = entityTrades.getTradeLevel(level).getTradeList();
            for (final Trade trade : trades) {
                final VillagerTrade villagerTrade =
                        new VillagerTrade(trade.getFirstGivenItem().getItemStack(),
                        trade.getReceivedItem().getItemStack(), 100);
                if (trade.getSecondGivenItem() != null) {
                    villagerTrade.setItemTwo(trade.getSecondGivenItem().getItemStack());
                }
                villagerTrades.add(villagerTrade);
            }
        }
        return new VillagerInventory(villagerTrades, player);
    }

    public void addPoints(final Entity entity, final VillagerTrade villagerTrade) {
        final EntityTradeData tradeData = entityTradeDataMap.get(entity.getUniqueId());
        if (tradeData == null) {
            return;
        }
        final EntityTrades entityTrades = tradeData.getEntityTrades();
        for (final TradeLevel tradeLevel : entityTrades.getTradeLevels().values()) {
            final Trade trade = tradeLevel.getTradeFromVillagerTrade(villagerTrade);
            if (trade == null) {
                continue;
            }

            final int pointsAmount = villagerTrade.getItemOne().getAmount() / trade.getFirstGivenItem().getItemStack().getAmount();
            final int points = trade.getTradePoints().getRandPoints() * pointsAmount;
            setEntityPoints(entity, points, true);
            return;
        }
    }

    public void setEntityPoints(final Entity entity, final int amount, boolean add) {
        final PersistentDataContainer container = entity.getPersistentDataContainer();
        final Integer current = container.get(pointsKey, PersistentDataType.INTEGER);
        if (add && current != null) {
            container.set(pointsKey, PersistentDataType.INTEGER, current + amount);
        } else {
            container.set(pointsKey, PersistentDataType.INTEGER, amount);
        }
    }

    public int getEntityPoints(final Entity entity) {
        return toInt(entity.getPersistentDataContainer().get(pointsKey, PersistentDataType.INTEGER));
    }

    public TradeLevel getTradeLevel(final String tradeType, int level) {
        final EntityTrades entityTrades = entityTradesMap.get(tradeType);
        if(entityTrades == null) {
            return null;
        }
        return entityTrades.getTradeLevel(level);
    }

    private int toInt(final Integer integer) {
        if(integer == null) {
            return 0;
        }
        return integer;
    }
}