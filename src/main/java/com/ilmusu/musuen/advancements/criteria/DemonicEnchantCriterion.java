package com.ilmusu.musuen.advancements.criteria;

import com.ilmusu.musuen.enchantments._IDemonicEnchantment;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public class DemonicEnchantCriterion extends AbstractCriterion<DemonicEnchantCriterion.Conditions>
{
    public void trigger(ServerPlayerEntity player, Enchantment enchantment)
    {
        this.trigger(player, conditions -> conditions.matches(enchantment));
    }

    @Override
    public Codec<Conditions> getConditionsCodec()
    {
        return Conditions.CODEC;
    }

    public record Conditions(Optional<LootContextPredicate> player) implements AbstractCriterion.Conditions
    {
        public static final Codec<Conditions> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                Codecs.createStrictOptionalFieldCodec(EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC, "player")
                    .forGetter(Conditions::player))
                .apply(instance, Conditions::new));

        public boolean matches(Enchantment enchantment)
        {
            return enchantment instanceof _IDemonicEnchantment;
        }
    }
}
