# Overgeared Fabric 1.20.1 -> Minecraft 26.3 port: shared notes

Branch `fabric-26.3`. Source: upstream `overgeared-fabric-1.20.1`, migrated to Mojang names, now targeting
Minecraft 26.3 / Fabric Loader 0.19.5 / Fabric API 0.161.0+26.3 / Java 25 (unobfuscated MC, Loom
`net.fabricmc.fabric-loom` 1.18, no mappings line, plain `implementation`/`compileOnly`).

## Building
The user's global ~/.gradle/gradle.properties forces Java 17 - DO NOT edit it. Always run:

    ./gradlew -Dorg.gradle.java.home=/Library/Java/JavaVirtualMachines/temurin-25.jdk/Contents/Home compileJava --no-daemon -q 2>&1 | grep -E 'error:' -A3

javac is configured with -Xmaxerrs 5000. Errors in packages you don't own are expected while others
work in parallel - filter to your own files.

## Reference sources (read-only)
- Minecraft 26.3 decompiled: /private/tmp/claude-501/-Users-lucanlaurence-Documents-GitHub-Overgeared/c0f1e0c2-3ec3-4b6b-a933-35e1dd9dacad/scratchpad/mc
- Fabric API 0.161.0+26.3 sources: /private/tmp/claude-501/-Users-lucanlaurence-Documents-GitHub-Overgeared/c0f1e0c2-3ec3-4b6b-a933-35e1dd9dacad/scratchpad/fabric-src
- Upstream NeoForge 1.21.1 port of the SAME mod (already migrated to data components, codecs,
  RecipeInput, StreamCodec networking, 1.21 APIs): `git show upstream/1.21.1:src/main/java/net/stirdrem/overgeared/<path>`
  and `git ls-tree -r --name-only upstream/1.21.1`. Great reference for logic shape - but it's NeoForge
  and 1.21.1, so APIs still differ from 26.3. Always confirm against the 26.3 sources.
- Vanilla is the best guide: find the vanilla class that does the same thing (furnace, smithing table,
  tipped arrow, brewing stand...) and copy its 26.3 patterns.

## Rules
- Preserve gameplay behaviour. Port, don't redesign. Keep class names and public method names where
  practical so other packages keep compiling. If you must change a public signature others call, note it
  in your final report.
- Only edit files in the packages you own (listed in your task). If you need something in a shared file
  (ModComponents, ModItems, util/, recipe/ItemListInput, recipe/RecipeLookup, ...) describe it in your report
  instead of editing - the coordinator merges. Adding NEW files inside your own packages is fine.
- When a feature has no 26.3 equivalent, keep the code compiling with the closest behaviour and leave a
  short `// 26.3 port:` comment explaining; list it in your report.
- Match surrounding code style. No need to write tests.
- Commit your work on your worktree branch when done (message ending with
  `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`).

## Already done (foundation, by coordinator)
- `ResourceLocation` -> `net.minecraft.resources.Identifier` everywhere (`Identifier.fromNamespaceAndPath`,
  `Identifier.parse`, `tryParse`, `withDefaultNamespace`). `Overgeared.id(path)` returns Identifier.
- `GuiGraphics` -> `GuiGraphicsExtractor` (renamed only; method names changed too, e.g. drawString -> text,
  renderItem -> item, renderTooltip -> setTooltipForNextFrame; see the class).
- Many import package moves applied (AbstractArrow/Arrow -> projectile.arrow, data.models -> client.data.models,
  VillagerTrades -> item.trading, critereon triggers -> advancements.triggers, GameRules -> level.gamerules, ...).
- Item NBT is replaced by data components in `net.stirdrem.overgeared.components.ModComponents`:
  | old NBT key                         | 26.3 component                                   |
  |-------------------------------------|--------------------------------------------------|
  | ForgingQuality (string)             | FORGING_QUALITY (ForgingQuality enum; `ForgingQuality.get(stack)`) |
  | Heated (bool)                       | HEATED                                           |
  | HeatedSince (long)                  | HEATED_TIME                                      |
  | Creator (string)                    | CREATOR                                          |
  | Polished (bool)                     | POLISHED                                         |
  | LingeringPotion (bool/string)       | LINGERING_STATUS (bool)                          |
  | TippedUsed (int)                    | TIPPED_USES                                      |
  | failedResult (bool)                 | FAILED_RESULT                                    |
  | Required (bool, blueprint in JEI)   | BLUEPRINT_REQUIRED                               |
  | ReducedMaxDurability (int)          | REDUCED_GRIND_COUNT                              |
  | blueprint Quality/ToolType/Uses     | BLUEPRINT_DATA (record BlueprintData, immutable, with*()) |
  | cast Quality/ToolType/Materials/Amount/MaxAmount/input/Output/Heated | CAST_DATA (record CastData, immutable, with*(); maxAmount 0 = unlimited) |
  | Potion / CustomPotionEffects / CustomPotionColor | vanilla DataComponents.POTION_CONTENTS (PotionContents) |
  Components are immutable values: read with `stack.get(C)`/`getOrDefault`, write with `stack.set(C, v)`,
  remove with `stack.remove(C)`. Never mutate a returned value expecting it to persist.
  `ItemStack.isSameItemSameComponents` replaces "same item same NBT".
- Items (`item/` package) fully ported: ModItems uses `Item.Properties#setId` + `.sword/.pickaxe/.tool/.humanoidArmor`;
  ModToolTiers are `ToolMaterial`s, ModArmorMaterials are `ArmorMaterial`s with EquipmentAsset keys
  `overgeared:steel` / `overgeared:copper`. SwordItem/DiggerItem/ArmorItem/TieredItem no longer exist -
  "is this a sword/armor" checks must use tags (ItemTags.SWORDS, ItemTags.HEAD_ARMOR, ...), the
  DataComponents.TOOL / WEAPON / EQUIPPABLE components, or `stack.is(...)`.
  Tool parts: ModTags.Items.STEEL_TOOL_MATERIALS, REPAIRS_STEEL_ARMOR were added (datagen must fill them).
- `UpgradeArrowEntity` must provide constructor `(ArrowTier tier, Level level, LivingEntity shooter, ItemStack pickup, @Nullable ItemStack firedFromWeapon)`.
- Recipes (contract for everyone):
  - All Overgeared recipe types take `net.stirdrem.overgeared.recipe.ItemListInput` (a RecipeInput over a
    list of stacks; `ItemListInput.of(container)` / `of(container, from, to)` / `of(stacks...)`), indexed the
    same way as the old Container. Exception: recipes extending vanilla classes keep vanilla inputs
    (cooking -> SingleRecipeInput, CustomRecipe/shapeless -> CraftingInput).
  - Look recipes up with `net.stirdrem.overgeared.recipe.RecipeLookup` (`firstMatch`, `firstMatchValue`, `all`,
    `allValues`) - works client- and server-side because every mod serializer is registered with Fabric's
    `RecipeSynchronization.synchronizeRecipeSerializer` in ModRecipes. Results are `RecipeHolder<T>`; use `.value()`.
  - `ModRecipeTypes.<TYPE>` constant names stay the same.
  - Recipe output accessors: keep the existing public getters (getResultItem(...) etc.) where possible.
    `Recipe#getResultItem(RegistryAccess)` no longer exists in vanilla; recipe classes keep a mod-level
    accessor (e.g. `getResultItem()` / `getOutput()` - the recipe agent documents the final names in
    recipe/README.md).

## Key 26.3 API facts (verified)
- `level.isClientSide()` is a method. `player.sendOverlayMessage(Component)` replaces displayClientMessage(msg, true).
- Item: `use(...)` returns `InteractionResult` (SUCCESS/FAIL/PASS/CONSUME; InteractionResultHolder is gone);
  `appendHoverText(ItemStack, Item.TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`;
  `inventoryTick(ItemStack, ServerLevel, Entity, @Nullable EquipmentSlot)`; `stack.hurtAndBreak(int, LivingEntity, EquipmentSlot)`.
- BlockEntity persistence: `saveAdditional(ValueOutput)` / `loadAdditional(ValueInput)`.
- Recipes: `ServerLevel#recipeAccess()` returns RecipeManager; Recipe/RecipeSerializer use MapCodec + StreamCodec;
  see vanilla AbstractCookingRecipe / ShapedRecipe / SmithingTransformRecipe for patterns.
- Fabric API module renames: item groups -> `net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab`,
  screen handlers -> `fabric-menu-api-v1`, key bindings -> `fabric-key-mapping-api-v1`. Check fabric-src.
- Accessories compat is disabled (moved to /disabled-compat). Valkyrien Skies compat stays a stub.
