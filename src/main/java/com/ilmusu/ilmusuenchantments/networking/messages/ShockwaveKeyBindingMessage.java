package com.ilmusu.ilmusuenchantments.networking.messages;

import com.ilmusu.ilmusuenchantments.enchantments.ShockwaveEnchantment;
import net.minecraft.entity.player.PlayerEntity;

public class ShockwaveKeyBindingMessage extends _KeyBindingMessage
{
    public ShockwaveKeyBindingMessage()
    {
        super("shockwave_binding");
    }

    public ShockwaveKeyBindingMessage(int action, int modifiers)
    {
        super("shockwave_binding", action, modifiers);
    }

    @Override
    public void handle(PlayerEntity player)
    {
        ShockwaveEnchantment.onShockwaveKeyBindingPress(player, this.modifiers);
    }
}
