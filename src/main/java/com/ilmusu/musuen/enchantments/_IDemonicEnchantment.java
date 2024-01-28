package com.ilmusu.musuen.enchantments;

import com.ilmusu.musuen.networking.messages.PlayerDemonicDamageMessage;
import com.ilmusu.musuen.registries.ModDamageSources;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

public interface _IDemonicEnchantment
{
    default Text getName(String translationKey, int level, int maxLevel)
    {
        // Making enchantment name as purple
        MutableText mutableText = Text.translatable(translationKey);
        mutableText.formatted(Formatting.DARK_PURPLE);
        // Appending enchantment level if necessary
        if (level != 1 || maxLevel != 1)
            mutableText.append(" ").append(Text.translatable("enchantment.level." + level));

        return mutableText;
    }

    static float consumeHealthValue(LivingEntity entity, float percentage, boolean safe)
    {
        return consumeHealth(entity, percentage, safe).getRight();
    }

    static float consumeHealthPercentage(LivingEntity entity, float percentage, boolean safe)
    {
        Pair<Float, Float> consumed = consumeHealth(entity, percentage, safe);
        return consumed.getRight() / consumed.getLeft();
    }

    private static Pair<Float, Float> consumeHealth(LivingEntity entity, float percentage, boolean safe)
    {
        float healthToConsume = entity.getMaxHealth() * percentage;

        if(entity instanceof PlayerEntity player && player.isCreative())
            return new Pair<>(healthToConsume, healthToConsume);

        // The health is not consumed if this is safe and the player does not have more that half hearth
        float initialHealth = entity.getHealth();
        if(safe && initialHealth < 1.0F)
            return new Pair<>(healthToConsume, 0.0F);

        // The health consumed leaves the player with half hearth if this is safe
        float remaining = Math.max(safe ? 1.0F : 0.0F, initialHealth-healthToConsume);

        damageEntityWithDemonicDamage(entity, initialHealth-remaining);

        // Returning the health that would be consumed, notice that now there is the demonction
        // enchantment, therefore returning the actual health after the damage would not work
        return new Pair<>(healthToConsume, initialHealth-remaining);
    }

    static void damageEntityWithDemonicDamage(LivingEntity entity, float damage)
    {
        if(entity instanceof ServerPlayerEntity player)
            new PlayerDemonicDamageMessage().sendToClient(player);
        entity.damage(ModDamageSources.DEMONIC_DAMAGE, damage);
    }
}
