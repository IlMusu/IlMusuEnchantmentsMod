package com.ilmusu.musuen;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources
{
    public static final String MOD_ID = "musuen";
    public static final String MOD_NAME = "IlMusu's Enchantments";

    public static final String DONT_ANIMATE_TAG = Resources.MOD_ID+".dont_animate_nbt";

    public static final Identifier POCKETS_TEXTURE = identifier("textures/gui/container/pockets/pockets.png");
    public static final Identifier POCKETS_BUTTON_NORMAL_TEXTURE = identifier("container/pockets/pockets_button_normal");
    public static final Identifier POCKETS_BUTTON_HIGHLIGHT_TEXTURE = identifier("container/pockets/pockets_button_highlight");
    public static final Identifier DEMONIC_ENCHANTING_TABLE_TEXTURE = identifier("textures/gui/container/enchanting_table/enchanting_table.png");
    public static final Identifier BERSERKER_OUTLINE_TEXTURE = identifier("textures/misc/berserker_outline2.png");

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static Identifier identifier(String string)
    {
        return new Identifier(MOD_ID, string);
    }

    public static String key(String name)
    {
        return "key."+MOD_ID+"."+name;
    }
}
