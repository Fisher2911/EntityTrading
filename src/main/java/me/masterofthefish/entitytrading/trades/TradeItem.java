package me.masterofthefish.entitytrading.trades;

import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TradeItem {

    private final ItemStack itemStack;
    private final int minAmount;
    private final int maxAmount;

    public TradeItem(final ItemStack itemStack, final int minAmount, final int maxAmount) {
        this.itemStack = itemStack;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public ItemStack getItemStack() {
        final ItemStack itemStack = this.itemStack.clone();
        itemStack.setAmount(new Random().nextInt(maxAmount - minAmount) + minAmount);
        return itemStack;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public TradeItem getSetTradeItem() {
        final int rand = new Random().nextInt(maxAmount - minAmount) + minAmount;;
        return new TradeItem(itemStack.clone(), rand, rand + 1);
    }
}
