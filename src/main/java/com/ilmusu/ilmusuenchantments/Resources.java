package com.ilmusu.ilmusuenchantments;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resources
{
    public static final String MOD_ID = "ilmusuenchantments";
    public static final String MOD_NAME = "IlMusu's Enchantments";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final Identifier POCKETS_TEXTURE = identifier("textures/gui/container/pockets.png");


    public static Identifier identifier(String string)
    {
        return new Identifier(MOD_ID, string);
    }

    public static String key(String name)
    {
        return "key."+MOD_ID+"."+name;
    }
}
