package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

/** Vision nocturne permanente, côté client uniquement (effet local sans particules ni icône). */
public final class FullbrightModule extends Module802 {
    private final BoolSetting hideIcon = addBool("hideicon", "Cacher l'icône de l'effet", true);

    public FullbrightModule() { super("Fullbright", "Voir comme en plein jour, partout"); }

    @Override public String category() { return "Visuals"; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) return;
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, true, false, !hideIcon.get()));
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
    }
}
