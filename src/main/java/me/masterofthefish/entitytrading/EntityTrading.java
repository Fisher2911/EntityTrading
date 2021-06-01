package me.masterofthefish.entitytrading;

import io.lumine.xikage.mythicmobs.MythicMobs;
import me.masterofthefish.entitytrading.config.TradeLoader;
import me.masterofthefish.entitytrading.listeners.TradeListener;
import me.masterofthefish.entitytrading.trades.TradeManager;
import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.adapters.BaseAdapter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class EntityTrading extends JavaPlugin {

    // From Villager Trading API
    private static Class<? extends BaseAdapter> versionAdapter = null;
    private MythicMobs mythicMobs;
//    private AdapterLoader loader = null;

    private TradeManager tradeManager;

    @Override
    public void onEnable() {
        // From Villager Trading API
//        try {
//            loader = new AdapterLoader(this);
//            loader.reflectivelyLoad();
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
//            e.printStackTrace();
//        }
        load();
        registerListeners();
    }

    @Override
    public void onDisable() {

        // From Villager Trading API
//        loader.close();
    }

    private void load() {
        this.mythicMobs = MythicMobs.inst();
        this.tradeManager = new TradeManager(this);
        new TradeLoader(this).load();
    }

    private void registerListeners() {
        List.of(new TradeListener(this)).forEach(listener -> {
            getServer().getPluginManager().registerEvents(listener, this);
        });
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public MythicMobs getMythicMobs() {
        return mythicMobs;
    }

    //    // From Villager Trading API
//    public void swapAdapter(Class<? extends BaseAdapter> adapter) {
//        versionAdapter = adapter;
//    }
//
//    // From Villager Trading API
//    public static Class<? extends BaseAdapter> getAdapter() {
//        return versionAdapter;
//    }

}
