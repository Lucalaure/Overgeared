package net.stirdrem.overgeared.item;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.stirdrem.overgeared.util.ModTags;

public class ModToolTiers {
    // Mines like iron (steel ≈ iron in the mineable/needs_*_tool tags, see ModTags.Blocks).
    public static final ToolMaterial STEEL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 7.0F, 3.0F, 12, ModTags.Items.STEEL_TOOL_MATERIALS);
}
