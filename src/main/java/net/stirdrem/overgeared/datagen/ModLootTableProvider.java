package net.stirdrem.overgeared.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

/**
 * Block loot tables. The 1.20.1 provider only contained commented-out template entries, so this
 * stays an empty provider.
 * 26.3 port: FabricBlockLootTableProvider -> FabricBlockLootSubProvider (needs the registries future);
 * the unused copperLikeOreDrops template helper was dropped.
 */
public class ModLootTableProvider extends FabricBlockLootSubProvider {
    public ModLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
    }
}
