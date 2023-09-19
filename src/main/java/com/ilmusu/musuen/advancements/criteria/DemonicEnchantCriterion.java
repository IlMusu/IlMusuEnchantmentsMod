package com.ilmusu.musuen.advancements.criteria;

import com.google.gson.JsonObject;
import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.enchantments._IDemonicEnchantment;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class DemonicEnchantCriterion extends AbstractCriterion<DemonicEnchantCriterion.DemonicEnchantConditions>
{
    protected static final Identifier ID = Resources.identifier("demonic_enchantment");

    @Override
    public Identifier getId()
    {
        return ID;
    }

    public void trigger(ServerPlayerEntity player, Enchantment enchantment)
    {
        this.trigger(player, conditions -> conditions.matches(enchantment));
    }

    @Override
    protected DemonicEnchantConditions conditionsFromJson(JsonObject obj, EntityPredicate.Extended playerPredicate, AdvancementEntityPredicateDeserializer predicateDeserializer)
    {
        return new DemonicEnchantConditions(this.getId(), playerPredicate);
    }

    public static class DemonicEnchantConditions extends AbstractCriterionConditions
    {
        public DemonicEnchantConditions(Identifier id, EntityPredicate.Extended entity)
        {
            super(id, entity);
        }

        public boolean matches(Enchantment enchantment)
        {
            return enchantment instanceof _IDemonicEnchantment;
        }
    }
}
