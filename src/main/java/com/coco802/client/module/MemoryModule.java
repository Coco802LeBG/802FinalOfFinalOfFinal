package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class MemoryModule extends TextHudModule {
    public MemoryModule() { super("Memory", "Mémoire utilisée par le jeu", 4, 104); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        Runtime rt = Runtime.getRuntime();
        long used = (rt.totalMemory() - rt.freeMemory()) / 1048576L;
        long max = rt.maxMemory() / 1048576L;
        return List.of("Mem: " + used + " / " + max + " MB");
    }
}
