package com.ilmusu.ilmusuenchantments.networking.messages;

import com.ilmusu.ilmusuenchantments.enchantments.PhasingEnchantment;
import net.minecraft.entity.player.PlayerEntity;

public class PhasingKeyBindingMessage extends _KeyBindingMessage
{
    public PhasingKeyBindingMessage()
    {
        super("phase_key_binding");
    }

    public PhasingKeyBindingMessage(int action, int modifiers)
    {
        super("phase_key_binding", action, modifiers);
    }

    @Override
    public void handle(PlayerEntity player)
    {
        PhasingEnchantment.onPhasingKeyBindingPress(player, this.modifiers);
    }
}
