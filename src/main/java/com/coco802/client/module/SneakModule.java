package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

/** Sneak maintenu automatiquement (toggle sneak). */
public final class SneakModule extends Module802 {
    public SneakModule() { super("Toggle Sneak", "Reste accroupi sans tenir la touche"); }

    @Override public String category() { return "Player"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player != null && mc.currentScreen == null) mc.options.sneakKey.setPressed(true);
    }

    @Override
    public void onDisable() {
        MinecraftClient.getInstance().options.sneakKey.setPressed(false);
    }
}
