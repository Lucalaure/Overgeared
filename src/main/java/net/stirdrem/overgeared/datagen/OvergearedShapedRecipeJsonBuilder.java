package net.stirdrem.overgeared.datagen;

import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.stirdrem.overgeared.recipe.OvergearedShapedRecipe;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/** Datagen builder for {@code overgeared:crafting_shaped} recipes. */
public class OvergearedShapedRecipeJsonBuilder implements RecipeBuilder {

    private final RecipeCategory category;
    private final Item output;
    private final int count;
    private final List<String> rows = new ArrayList<>();
    private final Map<Character, Function<HolderGetter<Item>, Ingredient>> key = new LinkedHashMap<>();
    private final RecipeBuilderSupport.Unlocks unlocks = new RecipeBuilderSupport.Unlocks();

    @Nullable
    private String group;

    public OvergearedShapedRecipeJsonBuilder(RecipeCategory category, ItemLike output, int count) {
        this.category = category;
        this.output = output.asItem();
        this.count = count;
    }

    public static OvergearedShapedRecipeJsonBuilder create(RecipeCategory category, ItemLike output) {
        return new OvergearedShapedRecipeJsonBuilder(category, output, 1);
    }

    public static OvergearedShapedRecipeJsonBuilder create(RecipeCategory category, ItemLike output, int count) {
        return new OvergearedShapedRecipeJsonBuilder(category, output, count);
    }

    public OvergearedShapedRecipeJsonBuilder pattern(String row) {
        if (!rows.isEmpty() && row.length() != rows.getFirst().length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        rows.add(row);
        return this;
    }

    public OvergearedShapedRecipeJsonBuilder define(char symbol, TagKey<Item> tag) {
        return define(symbol, RecipeBuilderSupport.lazy(tag));
    }

    public OvergearedShapedRecipeJsonBuilder define(char symbol, ItemLike item) {
        return define(symbol, Ingredient.of(item));
    }

    public OvergearedShapedRecipeJsonBuilder define(char symbol, Ingredient ingredient) {
        return define(symbol, RecipeBuilderSupport.lazy(ingredient));
    }

    private OvergearedShapedRecipeJsonBuilder define(char symbol, Function<HolderGetter<Item>, Ingredient> ingredient) {
        if (key.containsKey(symbol)) {
            throw new IllegalArgumentException("Symbol '" + symbol + "' is already defined!");
        }
        if (symbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        key.put(symbol, ingredient);
        return this;
    }

    @Override
    public OvergearedShapedRecipeJsonBuilder unlockedBy(String name, Criterion<?> criterion) {
        unlocks.add(name, criterion);
        return this;
    }

    @Override
    public OvergearedShapedRecipeJsonBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public ResourceKey<Recipe<?>> defaultId() {
        return RecipeBuilderSupport.key(RecipeBuilderSupport.defaultId(output));
    }

    @Override
    public void save(RecipeOutput recipeOutput, ResourceKey<Recipe<?>> id) {
        HolderGetter<Item> items = RecipeBuilderSupport.items(recipeOutput);
        Map<Character, Ingredient> resolved = new LinkedHashMap<>();
        key.forEach((symbol, ingredient) -> resolved.put(symbol, ingredient.apply(items)));
        OvergearedShapedRecipe recipe = new OvergearedShapedRecipe(
                RecipeBuilder.createCraftingCommonInfo(true),
                RecipeBuilder.createCraftingBookInfo(category, group),
                ShapedRecipePattern.of(resolved, rows),
                new ItemStackTemplate(output, count));
        recipeOutput.accept(id, recipe, unlocks.build(recipeOutput, id, category.getFolderName()));
    }
}
