package net.stirdrem.overgeared.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.stirdrem.overgeared.Overgeared;
import net.stirdrem.overgeared.item.armor.model.CustomCopperHelmet;
import net.stirdrem.overgeared.item.armor.model.CustomCopperLeggings;

/**
 * Replaces the vanilla humanoid armor model with the Blockbench-authored geometry for the
 * head and leg slots only - the chestplate/boots keep the vanilla equipment rendering
 * (assets/overgeared/equipment/copper.json), matching the original Forge port (which only
 * overrode getHumanoidArmorModel for helmet/leggings).
 *
 * <p>The wearer's pose is copied onto the custom parts through Fabric's TransformCopyingModel
 * (part names match the vanilla humanoid model). Armor trims are not drawn on these two pieces,
 * same as before the port.
 */
public class CopperArmorRenderer implements ArmorRenderer {
    // Same textures the vanilla equipment layer uses for overgeared:copper.
    private static final Identifier HELMET_TEXTURE = Overgeared.id("textures/entity/equipment/humanoid/copper.png");
    private static final Identifier LEGGINGS_TEXTURE = Overgeared.id("textures/entity/equipment/humanoid_leggings/copper.png");

    private final Model.Simple helmetModel;
    private final Model.Simple leggingsModel;

    public CopperArmorRenderer(EntityRendererProvider.Context context) {
        this.helmetModel = new Model.Simple(context.bakeLayer(CustomCopperHelmet.LAYER_LOCATION), RenderTypes::armorCutoutNoCull);
        this.leggingsModel = new Model.Simple(context.bakeLayer(CustomCopperLeggings.LAYER_LOCATION), RenderTypes::armorCutoutNoCull);
    }

    @Override
    public void render(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, ItemStack stack,
                       HumanoidRenderState humanoidRenderState, EquipmentSlot slot, int light,
                       HumanoidModel<HumanoidRenderState> contextModel) {
        Model.Simple model;
        Identifier texture;
        if (slot == EquipmentSlot.HEAD) {
            model = helmetModel;
            texture = HELMET_TEXTURE;
        } else if (slot == EquipmentSlot.LEGS) {
            model = leggingsModel;
            texture = LEGGINGS_TEXTURE;
        } else {
            return;
        }

        RenderType renderType = stack.hasFoil()
                ? RenderTypes.armorCutoutNoCullGlint(texture)
                : RenderTypes.armorCutoutNoCull(texture);

        ArmorRenderer.submitTransformCopyingModel(
                contextModel, humanoidRenderState,
                model, Unit.INSTANCE,
                false,
                submitNodeCollector, poseStack, renderType,
                light, OverlayTexture.NO_OVERLAY, humanoidRenderState.outlineColor);
    }

    @Override
    public boolean shouldRenderDefaultHeadItem(net.minecraft.world.entity.LivingEntity entity, ItemStack stack) {
        return false;
    }
}
