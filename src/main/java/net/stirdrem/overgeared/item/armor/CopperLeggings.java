package net.stirdrem.overgeared.item.armor;

import net.minecraft.world.item.Item;

// Armor stats come from Item.Properties#humanoidArmor. The custom Blockbench model is rendered
// client-side - see item.armor.model.CustomCopperLeggings.
public class CopperLeggings extends Item {
    public CopperLeggings(Properties settings) {
        super(settings);
    }
}
