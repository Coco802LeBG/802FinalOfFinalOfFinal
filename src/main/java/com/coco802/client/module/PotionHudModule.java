package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;

public final class PotionHudModule extends Module802 {
    public PotionHudModule() {
        super("PotionHud", "Effets de potion actifs et durée");
        addPosition(4, 180);
    }

    @Override public String category() { return "HUD"; }
    @Override public int boxWidth() { return 110; }
    @Override public int boxHeight() { return 36; }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        int y = posY.getInt();
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            int secs = Math.max(0, e.getDuration() / 20);
            String name = e.getEffectType().value().getName().getString();
            String line = name + " " + (e.getAmplifier() + 1) + "  " + (secs / 60) + ":" + String.format("%02d", secs % 60);
            ctx.drawTextWithShadow(mc.textRenderer, line, posX.getInt(), y, 0xFFFFFFFF);
            y += 12;
        }
    }
}
