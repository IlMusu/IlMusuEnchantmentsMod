package com.ilmusu.musuen.registries;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ilmusu.musuen.Resources;
import com.ilmusu.musuen.recipes.HeadRecipe;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ModCustomRecipes
{
    public static final Map<Identifier, HeadRecipe> HEAD_RECIPES = new HashMap<>();

    public static void register()
    {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener()
        {
            @Override
            public Identifier getFabricId()
            {
                return Resources.identifier("head_recipes");
            }

            @Override
            public void reload(ResourceManager manager)
            {
                // We are realoding, clearing the previous set recipes
                HEAD_RECIPES.clear();

                // Getting all the files that end with .json in the specified folder and subfolders of other mobs
                Map<Identifier, Resource> resources = manager.findResources("head_recipes",
                        path -> path.toString().endsWith(".json"));

                Gson gson = new Gson();
                for(Identifier id : resources.keySet())
                {
                    try(InputStream stream = manager.getResource(id).orElseThrow().getInputStream())
                    {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                        JsonObject json = gson.fromJson(reader, JsonObject.class);

                        // Obtaining the identifier of the mob
                        Identifier mobIdentifier = new Identifier(json.get("mob").getAsString());
                        // Getting or creating a new identifier
                        HeadRecipe mobHeadRecipe = getOrCreateHeadRecipe(mobIdentifier);
                        // Obtaining the list of heads

                        JsonArray array = json.get("head_items").getAsJsonArray();
                        for(JsonElement element : array)
                            mobHeadRecipe.addHead(new Identifier(element.getAsString()));
                    }
                    catch (IOException e)
                    {
                        Resources.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
                    }
                }

                // Freezing the recipes now that are all loaded
                for(HeadRecipe recipe : HEAD_RECIPES.values())
                    recipe.freeze();
            }
        });
    }

    private static HeadRecipe getOrCreateHeadRecipe(Identifier identifier)
    {
        if(HEAD_RECIPES.containsKey(identifier))
            return HEAD_RECIPES.get(identifier);
        HeadRecipe recipe = new HeadRecipe(identifier);
        HEAD_RECIPES.put(identifier, recipe);
        return recipe;
    }
}
