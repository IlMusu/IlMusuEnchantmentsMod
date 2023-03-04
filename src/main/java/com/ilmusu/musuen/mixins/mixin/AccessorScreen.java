package com.ilmusu.musuen.mixins.mixin;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface AccessorScreen
{
    @Accessor("height")
    int getHeight();

    @Accessor("drawables")
    List<Drawable> getDrawables();

    @Accessor("children")
    List<Element> getChildren();

    @Accessor("selectables")
    List<Selectable> getSelectables();
}
