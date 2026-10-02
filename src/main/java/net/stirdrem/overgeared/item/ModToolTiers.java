package net.stirdrem.overgeared.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.stirdrem.overgeared.util.ModTags;

public class ModToolTiers {
    // Mines like iron (steel ≈ iron in the mineable/needs_*_tool tags, see ModTags.Blocks).
    public static final ToolMaterial STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 7.0F, 3.0F, 12, ModTags.Items.STEEL_TOOL_MATERIALS);

    // Vanilla gained its own copper tier in 26.x; this keeps Overgeared's original stats
    // (enchantability 12 vs vanilla's 13) but shares vanilla's copper mining/repair tags.
    public static final ToolMaterial COPPER = new ToolMaterial(
            BlockTags.INCORRECT_FOR_COPPER_TOOL, 190, 5.0F, 1.0F, 12,
            net.minecraft.tags.ItemTags.COPPER_TOOL_MATERIALS);
}
