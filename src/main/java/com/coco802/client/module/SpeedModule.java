package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class SpeedModule extends TextHudModule {
    private final BoolSetting kmh = addBool("kmh", "Afficher en km/h", false);
    private final BoolSetting vertical = addBool("vertical", "Inclure la vitesse verticale", false);
    private double lastX, lastY, lastZ;
    private double speed;
    private boolean init;

    public SpeedModule() { super("Speed", "Vitesse en blocs par seconde", 4, 64); }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) return;
        double x = mc.player.getX(), y = mc.player.getY(), z = mc.player.getZ();
        if (init) {
            double dx = x - lastX, dz = z - lastZ;
            double dy = vertical.get() ? y - lastY : 0;
            speed = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
        }
        lastX = x; lastY = y; lastZ = z;
        init = true;
    }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        return List.of(kmh.get() ? String.format("Speed: %.1f km/h", speed * 3.6) : String.format("Speed: %.2f b/s", speed));
    }
}
