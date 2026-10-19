package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class HudModule extends TextHudModule {
    private static final DateTimeFormatter CLOCK = DateTimeFormatter.ofPattern("HH:mm");
    private final BoolSetting fps = addBool("fps", "FPS", true);
    private final BoolSetting coords = addBool("coords", "Coordonnées", true);
    private final BoolSetting clock = addBool("clock", "Heure réelle", false);

    public HudModule() { super("HUD", "FPS, coordonnées et heure", 4, 4); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        List<String> out = new ArrayList<>();
        if (fps.get()) out.add("FPS: " + mc.getCurrentFps());
        if (coords.get()) out.add(String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()));
        if (clock.get()) out.add(LocalTime.now().format(CLOCK));
        return out;
    }
}
