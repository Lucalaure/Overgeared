package net.stirdrem.overgeared.datagen;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.properties.select.TrimMaterialProperty;
import net.minecraft.client.renderer.item.SelectItemModel;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helper layer over Fabric's {@link FabricModelProvider} for 26.3 item model definitions.
 *
 * <p>26.3 port: the old JSON "overrides"/predicate helpers are gone (item model overrides no longer
 * exist - variants are selected by the item model definition under assets/&lt;ns&gt;/items/).
 * The equivalent here is building {@link ItemModel.Unbaked} trees with {@link ItemModelUtils}.
 */
public abstract class FabricModelProviderPlus extends FabricModelProvider {

    public FabricModelProviderPlus(FabricPackOutput output) {
        super(output);
    }

    protected static Identifier itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    /** Creates {@code <ns>:item/<modelName>} with layer0 = {@code <ns>:item/<texture>} and returns its id. */
    protected static Identifier flatModel(ItemModelGenerators generator, Identifier modelId, Identifier texture, ModelTemplate template) {
        return template.create(modelId, TextureMapping.layer0(new Material(texture)), generator.modelOutput);
    }

    /** Two-layer generated item model. */
    protected static Identifier layeredModel(ItemModelGenerators generator, Identifier modelId, Identifier layer0, Identifier layer1) {
        return ModelTemplates.TWO_LAYERED_ITEM.create(modelId,
                TextureMapping.layered(new Material(layer0), new Material(layer1)), generator.modelOutput);
    }

    /** Three-layer generated item model. */
    protected static Identifier layeredModel(ItemModelGenerators generator, Identifier modelId,
                                             Identifier layer0, Identifier layer1, Identifier layer2) {
        return ModelTemplates.THREE_LAYERED_ITEM.create(modelId,
                TextureMapping.layered(new Material(layer0), new Material(layer1), new Material(layer2)), generator.modelOutput);
    }

    /**
     * Trimmable armor piece with an extra untinted overlay layer drawn above the base (and above
     * the trim), e.g. the copper leggings' skirt. Layers: base, [trim], overlay.
     */
    protected static void trimmableItemWithOverlay(ItemModelGenerators generator, Item armor, Identifier slotTrimPrefix) {
        Identifier modelLocation = ModelLocationUtils.getModelLocation(armor);
        Identifier base = TextureMapping.getItemTexture(armor).sprite();
        Identifier overlay = TextureMapping.getItemTexture(armor, "_overlay").sprite();

        List<SelectItemModel.SwitchCase<ResourceKey<TrimMaterial>>> cases = new ArrayList<>();
        for (ItemModelGenerators.TrimMaterialData material : ItemModelGenerators.TRIM_MATERIAL_MODELS) {
            Identifier trimModel = modelLocation.withSuffix("_" + material.palette().suffix() + "_trim");
            Identifier trimTexture = slotTrimPrefix.withSuffix("_" + material.palette().suffix());
            layeredModel(generator, trimModel, base, trimTexture, overlay);
            cases.add(ItemModelUtils.when(material.materialKey(), ItemModelUtils.plainModel(trimModel)));
        }

        layeredModel(generator, modelLocation, base, overlay);
        generator.itemModelOutput.accept(armor,
                ItemModelUtils.select(new TrimMaterialProperty(), ItemModelUtils.plainModel(modelLocation), cases));
    }

    /** Block item that simply shows the given block model. */
    protected static void blockItem(BlockModelGenerators generator, net.minecraft.world.level.block.Block block, Identifier model) {
        generator.registerSimpleItemModel(block, model);
    }
}
