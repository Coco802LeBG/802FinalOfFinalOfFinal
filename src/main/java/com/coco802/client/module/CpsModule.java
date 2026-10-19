package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayDeque;
import java.util.Deque;

/** Compteur de clics par seconde (LMB / RMB), lu directement via GLFW. */
public final class CpsModule extends Module802 {
    private final Deque<Long> left = new ArrayDeque<>();
    private final Deque<Long> right = new ArrayDeque<>();
    private boolean wasLeft, wasRight;
    private final BoolSetting showRight = addBool("rmb", "Afficher RMB", true);

    public CpsModule() {
        super("CPS", "Clics par seconde (LMB / RMB)");
        addPosition(4, 90);
    }

    @Override public String category() { return "HUD"; }
    @Override public int boxWidth() { return 90; }
    @Override public int boxHeight() { return 24; }

    @Override
    public void onTick(MinecraftClient mc) {
        long handle = mc.getWindow().getHandle();
        boolean l = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
        boolean r = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
        long now = System.currentTimeMillis();
        if (mc.currentScreen == null) {
            if (l && !wasLeft) left.addLast(now);
            if (r && !wasRight) right.addLast(now);
        }
        wasLeft = l;
        wasRight = r;
        while (!left.isEmpty() && now - left.peekFirst() > 1000) left.removeFirst();
        while (!right.isEmpty() && now - right.peekFirst() > 1000) right.removeFirst();
    }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        int x = posX.getInt();
        int y = posY.getInt();
        ctx.drawTextWithShadow(mc.textRenderer, "LMB: " + left.size(), x, y, 0xFFFFFFFF);
        if (showRight.get()) ctx.drawTextWithShadow(mc.textRenderer, "RMB: " + right.size(), x, y + 12, 0xFFFFFFFF);
    }
}
