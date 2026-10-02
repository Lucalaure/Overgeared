package net.stirdrem.overgeared.loot;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.floats.ConstantValue;
import net.stirdrem.overgeared.item.ModItems;

public class ModLootModifiers {

    private static final Identifier SIMPLE_DUNGEON = Identifier.tryBuild("minecraft", "chests/simple_dungeon");
    private static final Identifier ABANDONED_MINESHAFT = Identifier.tryBuild("minecraft", "chests/abandoned_mineshaft");
    private static final Identifier STRONGHOLD_CORRIDOR = Identifier.tryBuild("minecraft", "chests/stronghold_corridor");
    private static final Identifier STRONGHOLD_CROSSING = Identifier.tryBuild("minecraft", "chests/stronghold_crossing");
    private static final Identifier STRONGHOLD_LIBRARY = Identifier.tryBuild("minecraft", "chests/stronghold_library");
    private static final Identifier DESERT_PYRAMID = Identifier.tryBuild("minecraft", "chests/desert_pyramid");
    private static final Identifier JUNGLE_TEMPLE = Identifier.tryBuild("minecraft", "chests/jungle_temple");
    private static final Identifier JUNGLE_TEMPLE_DISPENSER = Identifier.tryBuild("minecraft", "chests/jungle_temple_dispenser");
    private static final Identifier SHIPWRECK_TREASURE = Identifier.tryBuild("minecraft", "chests/shipwreck_treasure");
    private static final Identifier WOODLAND_MANSION = Identifier.tryBuild("minecraft", "chests/woodland_mansion");
    private static final Identifier ANCIENT_CITY = Identifier.tryBuild("minecraft", "chests/ancient_city");
    private static final Identifier PILLAGER_OUTPOST = Identifier.tryBuild("minecraft", "chests/pillager_outpost");
    private static final Identifier BURIED_TREASURE = Identifier.tryBuild("minecraft", "chests/buried_treasure");

    public static void register() {
        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            tableBuilder.apply(QualityLootFunction.INSTANCE);
            // Check if this is one of our target loot tables
            for (Identifier dungeon : getOtherDungeons()) {
                if (id.equals(dungeon)) {
                    String namePrefix = dungeon.getPath().replace("chests/", "");

                    // Add Steel Ingot (75% chance)
                    tableBuilder.pool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ModItems.STEEL_INGOT))
                                    .when(LootItemRandomChanceCondition.randomChance(0.75f))
                                    .build()
                    );

                    // Add Diamond Upgrade Template (50% chance)
                    tableBuilder.pool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ModItems.DIAMOND_UPGRADE_SMITHING_TEMPLATE))
                                    .when(LootItemRandomChanceCondition.randomChance(0.50f))
                                    .build()
                    );
                }
            }

            // Jungle Temple Dispenser
            if (id.equals(JUNGLE_TEMPLE_DISPENSER)) {
                tableBuilder.pool(
                        LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(ModItems.IRON_UPGRADE_ARROW))
                                .when(LootItemRandomChanceCondition.randomChance(0.50f))
                                .build()
                );
            }

            // Less rare dungeons
            for (Identifier dungeon : getLessRareDungeons()) {
                if (id.equals(dungeon)) {
                    String namePrefix = dungeon.getPath().replace("chests/", "");

                    // Steel Ingot (50% chance)
                    tableBuilder.pool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ModItems.STEEL_INGOT))
                                    .when(LootItemRandomChanceCondition.randomChance(0.5f))
                                    .build()
                    );

                    // Steel Ingot second entry (35% chance)
                    tableBuilder.pool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ModItems.STEEL_INGOT))
                                    .when(LootItemRandomChanceCondition.randomChance(0.35f))
                                    .build()
                    );

                    // Diamond Upgrade Template (15% chance)
                    tableBuilder.pool(
                            LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(ModItems.DIAMOND_UPGRADE_SMITHING_TEMPLATE))
                                    .when(LootItemRandomChanceCondition.randomChance(0.15f))
                                    .build()
                    );
                }
            }
        });
    }

    private static Identifier[] getOtherDungeons() {
        return new Identifier[]{
                STRONGHOLD_CORRIDOR,
                STRONGHOLD_CROSSING,
                STRONGHOLD_LIBRARY,
                DESERT_PYRAMID,
                SHIPWRECK_TREASURE,
                WOODLAND_MANSION,
                JUNGLE_TEMPLE,
                ANCIENT_CITY,
                PILLAGER_OUTPOST,
                BURIED_TREASURE
        };
    }

    private static Identifier[] getLessRareDungeons() {
        return new Identifier[]{
                ABANDONED_MINESHAFT,
                SIMPLE_DUNGEON
        };
    }
}