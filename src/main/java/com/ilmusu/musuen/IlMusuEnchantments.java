package com.ilmusu.musuen;

import com.ilmusu.musuen.registries.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class IlMusuEnchantments implements ModInitializer, ClientModInitializer, Runnable
{
	@Override
	public void run()
	{
		ModEnchantmentTargets.initialize();
	}

	@Override
	public void onInitialize()
	{
		ModConfigurations.load();
		ModEnchantmentTargets.register();
		ModEnchantments.register();
		ModParticles.register();
		ModSoundEvents.register();
		ModMessages.ServerHandlers.register();
		ModConfigurations.write();
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
