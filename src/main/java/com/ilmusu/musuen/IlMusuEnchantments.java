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
		ModCustomRecipes.register();
		ModEnchantmentTargets.register();
		ModEnchantments.register();
		ModParticles.register();
		ModSoundEvents.register();
		ModCriteria.register();
		ModConfigurations.write();
		ModEntities.register();
		ModMessages.ServerHandlers.register();
	}

	@Override
	public void onInitializeClient()
	{
		ModKeybindings.register();
		ModRenderings.register();
		ModParticles.registerFactories();
		ModEntities.registerRenders();
		ModMessages.ClientHandlers.register();
	}
}
