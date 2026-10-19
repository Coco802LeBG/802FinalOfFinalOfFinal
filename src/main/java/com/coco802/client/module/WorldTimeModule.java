package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class WorldTimeModule extends TextHudModule {
    public WorldTimeModule() { super("World Time", "Jour et heure dans le monde", 4, 214); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.world == null) return List.of();
        long t = mc.world.getTimeOfDay();
        long day = t / 24000L + 1;
        long tick = t % 24000L;
        int hours = (int) ((tick / 1000L + 6) % 24);
        int minutes = (int) ((tick % 1000L) * 60L / 1000L);
        return List.of(String.format("Day %d  %02d:%02d", day, hours, minutes));
    }
}
