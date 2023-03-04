package com.ilmusu.musuen;

import com.ilmusu.musuen.registries.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class IlMusuEnchantments implements ModInitializer, ClientModInitializer
{
	@Override
	public void onInitialize()
	{
		ModEnchantments.register();
		ModParticles.register();
		ModSoundEvents.register();
		ModMessages.ServerHandlers.register();
	}

	@Override
	public void onInitializeClient()
	{
		ModKeybindings.register();
		ModRenderings.register();
		ModParticles.registerFactories();
		ModMessages.ClientHandlers.register();
	}
}
