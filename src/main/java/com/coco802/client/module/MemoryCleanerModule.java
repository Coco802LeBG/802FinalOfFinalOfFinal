package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

/** Demande un nettoyage mémoire quand le jeu approche de sa limite. */
public final class MemoryCleanerModule extends Module802 {
    private final NumberSetting threshold = addNumber("threshold", "Seuil d'utilisation (%)", 50, 95, 5, 85);
    private final NumberSetting cooldown = addNumber("cooldown", "Délai minimum entre deux nettoyages (s)", 30, 600, 30, 180);
    private long last;
    private int ticks;

    public MemoryCleanerModule() { super("Memory Cleaner", "Nettoie la mémoire quand elle est presque pleine"); }

    @Override public String category() { return "Optimizer"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (++ticks < 100) return;
        ticks = 0;
        Runtime rt = Runtime.getRuntime();
        double used = (rt.totalMemory() - rt.freeMemory()) * 100.0 / rt.maxMemory();
        long now = System.currentTimeMillis();
        if (used >= threshold.get() && now - last > cooldown.getInt() * 1000L) {
            last = now;
            System.gc();
        }
    }
}
