package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.networking.messages.PhasingKeyBindingMessage;
import com.ilmusu.ilmusuenchantments.networking.messages.ShockwaveKeyBindingMessage;
import com.ilmusu.ilmusuenchantments.networking.messages._KeyBindingMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.function.BiFunction;

public class ModKeybindings
{
    public static final KeyBinding PHASING_ENCHANTMENT = new KeyBinding(Resources.key("phasing_enchantment"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, Resources.MOD_NAME);
    public static final KeyBinding SHOCKWAVE_ENCHANTMENT = new KeyBinding(Resources.key("shockwave_enchantment"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z, Resources.MOD_NAME);

    public static void register()
    {
        register(PHASING_ENCHANTMENT, EnvType.SERVER, PhasingKeyBindingMessage::new, List.of(GLFW.GLFW_PRESS));
        register(SHOCKWAVE_ENCHANTMENT, EnvType.SERVER, ShockwaveKeyBindingMessage::new, List.of(GLFW.GLFW_PRESS));
    }

    protected static void register(KeyBinding keyBinding, EnvType receiver, BiFunction<Integer, Integer, _KeyBindingMessage> construct, List<Integer> actions)
    {
        // Registering the KeyBinding
        if(KeyBindingHelper.registerKeyBinding(keyBinding) == null)
            Resources.LOGGER.error("Could not register KeyBinding: " + keyBinding);

        // Registering the handler for the KeyBinding
        ClientTickEvents.END_CLIENT_TICK.register((client) ->
        {
            // Consumes the existing clicks, but only once is handled
            boolean wasPressed = keyBinding.wasPressed();
            while(keyBinding.wasPressed());
            if(!wasPressed)
                return;

            // If in a gui, the click is consumed but not used
            if(MinecraftClient.getInstance().currentScreen != null)
                return;

            _KeyBindingMessage message = construct.apply(GLFW.GLFW_PRESS, GLFW.GLFW_FALSE);

            if(receiver == EnvType.SERVER)
                message.sendToServer();
            else
                message.handle(MinecraftClient.getInstance().player);
        });
    }
}
