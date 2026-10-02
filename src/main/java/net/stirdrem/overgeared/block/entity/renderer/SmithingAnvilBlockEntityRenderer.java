package net.stirdrem.overgeared.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.stirdrem.overgeared.block.entity.AbstractSmithingAnvilBlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 26.x splits block entity rendering into extractRenderState (gather data from the block entity
 * into a render state) and submit (draw from the state). Item placement math is unchanged.
 * Lighting uses the render state's light coords (light at the anvil position, same as before).
 */
public class SmithingAnvilBlockEntityRenderer
        implements BlockEntityRenderer<AbstractSmithingAnvilBlockEntity, SmithingAnvilBlockEntityRenderer.AnvilRenderState> {
    private final ItemModelResolver itemModelResolver;

    public SmithingAnvilBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    private static final float BASE_Y = 1.01f;
    private static final float ITEM_HEIGHT = 0.02f;
    private static final float BLOCK_HEIGHT = 0.2f;
    private static final float BLOCK_BASE_Y_OFFSET = 0.09f;

    public static class AnvilRenderState extends BlockEntityRenderState {
        public final List<PlacedItem> items = new ArrayList<>();
        public Direction facing = Direction.NORTH;
    }

    public record PlacedItem(ItemStackRenderState item, boolean isBlock, float xOffset, float yOffset, float zOffset,
                             float rotationDegrees, float scale, float heightScale) {
    }

    @Override
    public AnvilRenderState createRenderState() {
        return new AnvilRenderState();
    }

    @Override
    public void extractRenderState(AbstractSmithingAnvilBlockEntity blockEntity, AnvilRenderState state, float partialTicks,
                                   Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.items.clear();

        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)
                ? blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                : Direction.NORTH;

        int seed = (int) blockEntity.getBlockPos().asLong();

        // Output item from slot 10
        ItemStack output = blockEntity.getRenderStack(10);
        boolean inputsEmpty = areInputSlotsEmpty(blockEntity);
        float zOffset = inputsEmpty ? 0f : -0.43f;

        float heightScale;
        int progress = blockEntity.getContainerData().get(0);
        int max = blockEntity.getContainerData().get(1);

        if (max <= 0) {
            heightScale = 1.0f; // default when no recipe / not started
        } else {
            heightScale = 1.0f - ((float) progress / max);
        }
        if (!output.isEmpty()) {
            float yOffset = isBlockItem(output) ? 1.05f : 1.02f;
            addStack(state, blockEntity, output, seed + 10, 0.0f, yOffset, zOffset, 110f, 0.4f, 1.0f);
        }

        // First pass: up to three unique input items
        Set<Item> renderedItems = new HashSet<>();
        Set<Integer> renderedSlots = new HashSet<>();
        int rendered = extractPass(state, blockEntity, seed, renderedItems, renderedSlots, 0f, 0, true, heightScale);

        // Second pass: fill remaining slots with any items
        if (rendered < 3) {
            extractPass(state, blockEntity, seed, renderedItems, renderedSlots, 0f, rendered, false, heightScale);
        }

        // Hammer from slot 9
        ItemStack hammer = blockEntity.getRenderStack(9);
        addStack(state, blockEntity, hammer, seed + 9, 0f, 1.025f, 0.43f, 135f, 0.5f, 1.0f);
    }

    private boolean areInputSlotsEmpty(AbstractSmithingAnvilBlockEntity be) {
        for (int i = 0; i < 9; i++) { // slots 0-8 are inputs
            if (!be.getRenderStack(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private int extractPass(AnvilRenderState state, AbstractSmithingAnvilBlockEntity blockEntity, int seed,
                            Set<Item> renderedItems, Set<Integer> renderedSlots,
                            float zOffset, int renderedCount, boolean checkUniqueness, float heightScale) {

        float currentHeight = BASE_Y;

        for (int i : renderedSlots) {
            ItemStack prev = blockEntity.getRenderStack(i);
            currentHeight += isBlockItem(prev) ? BLOCK_HEIGHT : ITEM_HEIGHT;
        }

        int rendered = renderedCount;

        for (int i = 0; i < 9 && rendered < 3; i++) {
            if (renderedSlots.contains(i)) {
                continue;
            }

            ItemStack stack = blockEntity.getRenderStack(i);
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();

            if (checkUniqueness && renderedItems.contains(item)) {
                continue;
            }

            float scale = isBlockItem(stack) ? 0.4f : 0.35f;
            float rotation = 96f + (rendered * 14f);

            float yOffset = currentHeight;

            if (isBlockItem(stack)) {
                yOffset += BLOCK_BASE_Y_OFFSET;
            }
            addStack(state, blockEntity, stack, seed + i, 0.0f, yOffset, zOffset, rotation, scale, heightScale);

            currentHeight += isBlockItem(stack) ? BLOCK_HEIGHT : ITEM_HEIGHT;

            renderedItems.add(item);
            renderedSlots.add(i);
            rendered++;
        }

        return rendered;
    }

    // Helper method to determine if an ItemStack is a block item
    private boolean isBlockItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        Block block = Block.byItem(stack.getItem());
        return block != Blocks.AIR;
    }

    private void addStack(AnvilRenderState state, AbstractSmithingAnvilBlockEntity blockEntity, ItemStack stack, int seed,
                          float xOffset, float yOffset, float zOffset, float rotationDegrees, float scale, float heightScale) {
        if (stack == null || stack.isEmpty()) return;
        ItemStackRenderState itemState = new ItemStackRenderState();
        this.itemModelResolver.updateForTopItem(itemState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, seed);
        state.items.add(new PlacedItem(itemState, stack.getItem() instanceof BlockItem,
                xOffset, yOffset, zOffset, rotationDegrees, scale, heightScale));
    }

    @Override
    public void submit(AnvilRenderState state, PoseStack matrices, SubmitNodeCollector collector, CameraRenderState camera) {
        float facingRotationDegrees = switch (state.facing) {
            case NORTH -> 180f;
            case SOUTH -> 0f;
            case WEST -> 270f;
            case EAST -> 90f;
            default -> 0f;
        };

        double radians = Math.toRadians(facingRotationDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        for (PlacedItem placed : state.items) {
            if (placed.item().isEmpty()) continue;

            matrices.pushPose();

            float rotatedX = (float) (placed.xOffset() * cos - placed.zOffset() * sin);
            float rotatedZ = (float) (placed.xOffset() * sin + placed.zOffset() * cos);

            matrices.translate(0.5f - rotatedX, placed.yOffset() - (0.01 * (1 - placed.heightScale())), 0.5f + rotatedZ);
            matrices.rotateDegrees(Axis.YP, facingRotationDegrees);
            matrices.rotateDegrees(Axis.YP, placed.rotationDegrees());
            matrices.rotateDegrees(Axis.XP, placed.isBlock() ? 0 : 90);
            matrices.scale(placed.scale(), placed.scale(), placed.scale() * placed.heightScale());

            placed.item().submit(matrices, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);

            matrices.popPose();
        }
    }
}
