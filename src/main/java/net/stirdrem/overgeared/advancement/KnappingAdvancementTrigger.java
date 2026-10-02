package net.stirdrem.overgeared.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.triggers.SimpleCriterionTrigger;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.stirdrem.overgeared.Overgeared;

public class KnappingAdvancementTrigger extends SimpleCriterionTrigger<KnappingAdvancementTrigger.Conditions> {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Overgeared.MOD_ID, "finished_knapping");

    @Override
    public Identifier getId() {
        return ID;
    }

    @Override
    protected Conditions createInstance(JsonObject json,
                                             ContextAwarePredicate playerPredicate,
                                             DeserializationContext context) {
        return new Conditions(playerPredicate);
    }

    public void trigger(ServerPlayer player) {
        this.trigger(player, instance -> true);
    }

    // ---------------- Conditions ----------------

    public static class Conditions extends AbstractCriterionTriggerInstance {

        public Conditions(ContextAwarePredicate playerPredicate) {
            super(ID, playerPredicate);
        }

        public static Conditions instance() {
            return new Conditions(ContextAwarePredicate.ANY);
        }
    }
}
