package net.stirdrem.overgeared.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * {@code overgeared:crafting_shaped}: a vanilla shaped crafting recipe that passes the forging quality / creator
 * of its ingredients on to the result, exactly like {@link OvergearedShapelessRecipe}.
 * JSON: vanilla {@code minecraft:crafting_shaped} fields.
 */
public class OvergearedShapedRecipe extends ShapedRecipe {

    private final Recipe.CommonInfo info;
    private final CraftingRecipe.CraftingBookInfo book;
    private final ShapedRecipePattern pattern;
    private final ItemStackTemplate resultTemplate;

    public OvergearedShapedRecipe(Recipe.CommonInfo commonInfo, CraftingRecipe.CraftingBookInfo bookInfo,
                                  ShapedRecipePattern pattern, ItemStackTemplate result) {
        super(commonInfo, bookInfo, pattern, result);
        this.info = commonInfo;
        this.book = bookInfo;
        this.pattern = pattern;
        this.resultTemplate = result;
    }

    /** A fresh copy of the plain result (no quality applied). */
    public ItemStack getResultItem() {
        return resultTemplate.create();
    }

    public ItemStackTemplate result() {
        return resultTemplate;
    }

    @Override
    public ItemStack assemble(CraftingInput container) {
        return OvergearedShapelessRecipe.applyIngredientQuality(resultTemplate.create(), container);
    }

    @Override
    public RecipeSerializer<ShapedRecipe> getSerializer() {
        @SuppressWarnings({"unchecked", "rawtypes"})
        RecipeSerializer<ShapedRecipe> s = (RecipeSerializer) ModRecipes.CRAFTING_SHAPED;
        return s;
    }

    public static final MapCodec<OvergearedShapedRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.info),
            CraftingRecipe.CraftingBookInfo.MAP_CODEC.forGetter(o -> o.book),
            ShapedRecipePattern.MAP_CODEC.forGetter(o -> o.pattern),
            RecipeCodecs.RESULT.fieldOf("result").forGetter(o -> o.resultTemplate)
    ).apply(i, OvergearedShapedRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OvergearedShapedRecipe> STREAM_CODEC = StreamCodec.composite(
            Recipe.CommonInfo.STREAM_CODEC, o -> o.info,
            CraftingRecipe.CraftingBookInfo.STREAM_CODEC, o -> o.book,
            ShapedRecipePattern.STREAM_CODEC, o -> o.pattern,
            ItemStackTemplate.STREAM_CODEC, o -> o.resultTemplate,
            OvergearedShapedRecipe::new);

    public static final RecipeSerializer<OvergearedShapedRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);
}
