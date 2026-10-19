package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class FoodModule extends TextHudModule {
    private final BoolSetting saturation = addBool("sat", "Afficher la saturation", true);

    public FoodModule() { super("Food", "Faim et saturation", 4, 200); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        String s = "Food: " + mc.player.getHungerManager().getFoodLevel();
        if (saturation.get()) s += String.format("  Sat: %.1f", mc.player.getHungerManager().getSaturationLevel());
        return List.of(s);
    }
}
