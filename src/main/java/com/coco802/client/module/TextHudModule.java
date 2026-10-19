package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/** Base des modules HUD à texte : position, couleur RGB, ombre, fond et opacité du fond. */
public abstract class TextHudModule extends Module802 {
    protected final NumberSetting r = addNumber("r", "Couleur - Rouge", 0, 255, 1, 255);
    protected final NumberSetting g = addNumber("g", "Couleur - Vert", 0, 255, 1, 255);
    protected final NumberSetting b = addNumber("b", "Couleur - Bleu", 0, 255, 1, 255);
    protected final BoolSetting shadow = addBool("shadow", "Ombre du texte", true);
    protected final BoolSetting background = addBool("bg", "Fond", false);
    protected final NumberSetting bgAlpha = addNumber("bgalpha", "Opacité du fond", 0, 255, 5, 110);
    private int lastW = 60;
    private int lastH = 14;

    protected TextHudModule(String name, String description, int defX, int defY) {
        super(name, description);
        addPosition(defX, defY);
    }

    protected abstract List<String> lines(MinecraftClient mc);

    @Override public String category() { return "HUD"; }
    @Override public int boxWidth() { return lastW; }
    @Override public int boxHeight() { return lastH; }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        List<String> lines = lines(mc);
        if (lines.isEmpty()) return;
        int w = 0;
        for (String s : lines) w = Math.max(w, mc.textRenderer.getWidth(s));
        lastW = w + 6;
        lastH = lines.size() * 11 + 3;
        int x = posX.getInt();
        int y = posY.getInt();
        if (background.get()) {
            ctx.fill(x - 3, y - 3, x - 3 + lastW, y - 3 + lastH, (bgAlpha.getInt() << 24));
        }
        int color = 0xFF000000 | (r.getInt() << 16) | (g.getInt() << 8) | b.getInt();
        for (int i = 0; i < lines.size(); i++) {
            ctx.drawText(mc.textRenderer, lines.get(i), x, y + i * 11, color, shadow.get());
        }
    }
}
