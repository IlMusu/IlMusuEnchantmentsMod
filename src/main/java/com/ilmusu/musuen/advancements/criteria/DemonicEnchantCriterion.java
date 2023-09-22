package com.ilmusu.musuen.advancements.criteria;

import com.google.gson.JsonObject;
import com.ilmusu.musuen.enchantments._IDemonicEnchantment;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class DemonicEnchantCriterion extends AbstractCriterion<DemonicEnchantCriterion.DemonicEnchantConditions>
{
    public void trigger(ServerPlayerEntity player, Enchantment enchantment)
    {
        this.trigger(player, conditions -> conditions.matches(enchantment));
    }

    @Override
    protected DemonicEnchantConditions conditionsFromJson(JsonObject json, Optional<LootContextPredicate> predicate, AdvancementEntityPredicateDeserializer serializer)
    {
        return new DemonicEnchantConditions(predicate);
    }

    public static class DemonicEnchantConditions extends AbstractCriterionConditions
    {
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private DemonicEnchantConditions(Optional<LootContextPredicate> player)
        {
            super(player);
        }

        public boolean matches(Enchantment enchantment)
        {
            return enchantment instanceof _IDemonicEnchantment;
        }
    }
}
