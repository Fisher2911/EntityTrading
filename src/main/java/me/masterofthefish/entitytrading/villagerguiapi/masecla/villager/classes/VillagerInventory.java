package me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.classes;

import me.masterofthefish.entitytrading.villagerguiapi.masecla.villager.adapters.instances.MerchantAdapter_v1_16_R3;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class VillagerInventory {
	private List<VillagerTrade> trades = new ArrayList<>();
	private String name = "Sample text";
	private Player forWho;

	public VillagerInventory(List<VillagerTrade> trades, Player forWho) {
		this.trades = trades;
		this.forWho = forWho;
	}

	public VillagerInventory() {

	}

	public Player getForWho() {
		return forWho;
	}

	public void setForWho(Player forWho) {
		this.forWho = forWho;
	}

	public List<VillagerTrade> getTrades() {
		return trades;
	}

	public void setTrades(List<VillagerTrade> trades) {
		this.trades = trades;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void open() {
		new MerchantAdapter_v1_16_R3(this).openFor(forWho);
//		try {
//			EntityTrading.getAdapter().getConstructor(VillagerInventory.class).newInstance(this).openFor(forWho);
//		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
//				| NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
	}
}
