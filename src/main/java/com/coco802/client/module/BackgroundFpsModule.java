package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

/** Limite les FPS quand la fenêtre du jeu n'est pas au premier plan. */
public final class BackgroundFpsModule extends Module802 {
    private final NumberSetting limit = addNumber("limit", "FPS en arrière-plan", 10, 60, 5, 15);
    private boolean limited;
    private int saved;

    public BackgroundFpsModule() { super("Background FPS", "Économise le PC quand le jeu est en arrière-plan"); }

    @Override public String category() { return "Optimizer"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.options == null) return;
        boolean focused = mc.isWindowFocused();
        if (!focused && !limited) {
            saved = mc.options.getMaxFps().getValue();
            limited = true;
        }
        if (!focused) {
            if (mc.options.getMaxFps().getValue() != limit.getInt()) mc.options.getMaxFps().setValue(limit.getInt());
        } else if (limited) {
            mc.options.getMaxFps().setValue(saved);
            limited = false;
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (limited && mc.options != null) mc.options.getMaxFps().setValue(saved);
        limited = false;
    }
}
