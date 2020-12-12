package models.items;

import java.text.DecimalFormat;

public class ConsumableOffensiveItem extends ConsumableItem {

    public ConsumableOffensiveItem(String iName, int iBuyingPrice, int iSellingPrice, double iDamageBoost){
        super(iName, iBuyingPrice, iSellingPrice, iDamageBoost);
    }

    public String getShopSummary(){
        return "Name: " + name + " | Type: Offensive consumable | Boost: " +getBoost()*100+ "% damage increase when used.";
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("##.#");
        return "You will deal " + df.format(getBoost() * 100) + "% more damage on your next turn!";
    }
}