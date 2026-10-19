package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

/** Zoom en maintenant une touche (C par défaut, code GLFW réglable). */
public final class ZoomModule extends Module802 {
    private final NumberSetting key = addNumber("key", "Touche (code GLFW, 67 = C)", 32, 348, 1, 67);
    private final NumberSetting fov = addNumber("fov", "FOV pendant le zoom", 10, 70, 1, 30);
    private boolean zooming;
    private int saved;

    public ZoomModule() { super("Zoom", "Maintiens une touche pour zoomer"); }

    @Override public String category() { return "Visuals"; }

    @Override
    public void onTick(MinecraftClient mc) {
        boolean down = mc.currentScreen == null
            && GLFW.glfwGetKey(mc.getWindow().getHandle(), key.getInt()) == GLFW.GLFW_PRESS;
        if (down && !zooming) {
            saved = mc.options.getFov().getValue();
            zooming = true;
        }
        if (down) {
            mc.options.getFov().setValue(Math.max(30, fov.getInt()));
        } else if (zooming) {
            mc.options.getFov().setValue(saved);
            zooming = false;
        }
    }

    @Override
    public void onDisable() {
        if (zooming) {
            MinecraftClient.getInstance().options.getFov().setValue(saved);
            zooming = false;
        }
    }
}
