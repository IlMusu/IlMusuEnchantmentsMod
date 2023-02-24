package com.ilmusu.ilmusuenchantments.registries;

import com.ilmusu.ilmusuenchantments.Resources;
import com.ilmusu.ilmusuenchantments.callbacks.KeyInputCallback;
import com.ilmusu.ilmusuenchantments.networking.messages.PhasingKeyBindingMessage;
import com.ilmusu.ilmusuenchantments.networking.messages.ShockwaveKeyBindingMessage;
import com.ilmusu.ilmusuenchantments.networking.messages._KeyBindingMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class ModKeybindings
{
    protected static final Map<Integer, KeyBinding> defaultKeyMappings = new HashMap<>();

    public static final KeyBinding PHASING_ENCHANTMENT = new KeyBinding(Resources.key("phasing_enchantment"), GLFW.GLFW_KEY_P, Resources.MOD_NAME);
    public static final KeyBinding SHOCKWAVE_ENCHANTMENT = new KeyBinding(Resources.key("shockwave_enchantment"), GLFW.GLFW_MOUSE_BUTTON_LEFT, Resources.MOD_NAME);

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
        KeyInputCallback.EVENT.register((key, scancode, action, modifiers) ->
        {
            // Not interested if not the correct key
            if(!keyBinding.matchesKey(key, scancode))
                return;

            // Consumes the existing clicks
            while (keyBinding.wasPressed());

            // If in a gui, the click is consumed but not used
            if(MinecraftClient.getInstance().currentScreen != null)
                return;
            // The click is consumed but not used if the action is not the desired one
            if(!actions.contains(action))
                return;

            _KeyBindingMessage message = construct.apply(action, modifiers);

            if(receiver == EnvType.SERVER)
                message.sendToServer();
            else
                message.handle(MinecraftClient.getInstance().player);
        });

        defaultKeyMappings.put(keyBinding.getDefaultKey().getCode(), keyBinding);
    }

    public static KeyBinding fromDefaultKey(int key)
    {
        return defaultKeyMappings.get(key);
    }
}
