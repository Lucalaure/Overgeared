package net.stirdrem.overgeared.entity.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.stirdrem.overgeared.Overgeared;
import net.stirdrem.overgeared.entity.ArrowTier;
import net.stirdrem.overgeared.entity.custom.UpgradeArrowEntity;

public class UpgradeArrowEntityRenderer extends ArrowRenderer<UpgradeArrowEntity> {
    public UpgradeArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(UpgradeArrowEntity entity) {
        ArrowTier tier = entity.getArrowTier();
        return Overgeared.id("textures/entity/projectiles/arrows/" + tier.getSerializedName() + ".png");
    }
}
