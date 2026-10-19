package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class DirectionModule extends TextHudModule {
    private final BoolSetting angles = addBool("angles", "Afficher yaw / pitch", true);

    public DirectionModule() { super("Direction", "Direction regardée", 4, 78); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        String facing = mc.player.getHorizontalFacing().asString();
        if (!angles.get()) return List.of("Facing: " + facing);
        float yaw = mc.player.getYaw() % 360f;
        if (yaw > 180f) yaw -= 360f;
        if (yaw < -180f) yaw += 360f;
        return List.of("Facing: " + facing, String.format("Yaw %.0f  Pitch %.0f", yaw, mc.player.getPitch()));
    }
}
