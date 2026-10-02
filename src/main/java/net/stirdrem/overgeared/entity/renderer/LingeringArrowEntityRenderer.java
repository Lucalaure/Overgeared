package net.stirdrem.overgeared.entity.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.stirdrem.overgeared.Overgeared;
import net.stirdrem.overgeared.entity.custom.LingeringArrowEntity;

public class LingeringArrowEntityRenderer extends ArrowRenderer<LingeringArrowEntity> {
    private static final Identifier TEXTURE = Overgeared.id("textures/entity/projectiles/arrows/flint.png");

    public LingeringArrowEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public Identifier getTextureLocation(LingeringArrowEntity entity) {
        return TEXTURE;
    }
}
