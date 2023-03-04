package com.ilmusu.musuen.networking.messages;

import com.ilmusu.musuen.enchantments.ShockwaveEnchantment;
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
