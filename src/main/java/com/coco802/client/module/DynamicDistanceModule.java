package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;

/** Ajuste la distance de rendu toute seule pour tenir un objectif de FPS. */
public final class DynamicDistanceModule extends Module802 {
    private final NumberSetting target = addNumber("target", "FPS visés", 20, 240, 5, 60);
    private final NumberSetting min = addNumber("min", "Distance minimum (chunks)", 2, 32, 1, 6);
    private final NumberSetting max = addNumber("max", "Distance maximum (chunks)", 2, 32, 1, 16);
    private final NumberSetting interval = addNumber("interval", "Vérification (secondes)", 1, 10, 1, 3);
    private int ticks;

    public DynamicDistanceModule() { super("Dynamic Distance", "Adapte la distance de rendu aux FPS"); }

    @Override public String category() { return "Optimizer"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world == null || mc.options == null) return;
        if (++ticks < interval.getInt() * 20) return;
        ticks = 0;
        int fps = mc.getCurrentFps();
        SimpleOption<Integer> opt = mc.options.getViewDistance();
        int cur = opt.getValue();
        int lo = Math.min(min.getInt(), max.getInt());
        int hi = Math.max(min.getInt(), max.getInt());
        if (fps < target.get() * 0.9 && cur > lo) opt.setValue(cur - 1);
        else if (fps > target.get() * 1.3 && cur < hi) opt.setValue(cur + 1);
    }
}
