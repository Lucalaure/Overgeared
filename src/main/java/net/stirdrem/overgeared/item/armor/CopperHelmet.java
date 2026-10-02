package net.stirdrem.overgeared.item.armor;

import net.minecraft.world.item.Item;

// Armor stats come from Item.Properties#humanoidArmor. The custom Blockbench model is rendered
// client-side - see item.armor.model.CustomCopperHelmet.
public class CopperHelmet extends Item {
    public CopperHelmet(Properties settings) {
        super(settings);
    }
}
