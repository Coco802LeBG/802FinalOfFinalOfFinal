package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.ParticlesMode;
import net.minecraft.client.option.SimpleOption;

/** Réglages de performance appliqués d'un coup (et restaurés quand on coupe le module). Aucun avantage en combat. */
public final class PerformanceModule extends Module802 {
    private final NumberSetting view = addNumber("view", "Distance de rendu (chunks)", 2, 32, 1, 8);
    private final NumberSetting sim = addNumber("sim", "Distance de simulation (chunks)", 5, 32, 1, 6);
    private final NumberSetting fps = addNumber("fps", "FPS max (260 = illimité)", 10, 260, 10, 260);
    private final NumberSetting entity = addNumber("entity", "Distance des entités (%)", 50, 200, 5, 75);
    private final NumberSetting blend = addNumber("blend", "Biome blend", 0, 7, 1, 1);
    private final BoolSetting particles = addBool("particles", "Particules minimales", true);
    private final BoolSetting shadows = addBool("shadows", "Sans ombres d'entités", true);
    private final BoolSetting bob = addBool("bob", "Sans balancement de vue", false);

    private boolean applied;
    private int ticks;
    private int oView, oSim, oFps, oBlend;
    private double oEntity;
    private ParticlesMode oParticles;
    private boolean oShadows, oBob;

    public PerformanceModule() { super("Performance Boost", "Réglages de performance en un clic"); }

    @Override public String category() { return "Optimizer"; }

    @Override public void onEnable() { applied = false; ticks = 0; }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.options == null) return;
        if (!applied) {
            oView = mc.options.getViewDistance().getValue();
            oSim = mc.options.getSimulationDistance().getValue();
            oFps = mc.options.getMaxFps().getValue();
            oBlend = mc.options.getBiomeBlendRadius().getValue();
            oEntity = mc.options.getEntityDistanceScaling().getValue();
            oParticles = mc.options.getParticles().getValue();
            oShadows = mc.options.getEntityShadows().getValue();
            oBob = mc.options.getBobView().getValue();
            applied = true;
            ticks = 20;
        }
        if (++ticks < 20) return;
        ticks = 0;
        set(mc.options.getViewDistance(), view.getInt());
        set(mc.options.getSimulationDistance(), sim.getInt());
        set(mc.options.getMaxFps(), fps.getInt());
        set(mc.options.getBiomeBlendRadius(), blend.getInt());
        set(mc.options.getEntityDistanceScaling(), entity.get() / 100.0);
        if (particles.get()) set(mc.options.getParticles(), ParticlesMode.MINIMAL);
        if (shadows.get()) set(mc.options.getEntityShadows(), false);
        if (bob.get()) set(mc.options.getBobView(), false);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!applied || mc.options == null) return;
        set(mc.options.getViewDistance(), oView);
        set(mc.options.getSimulationDistance(), oSim);
        set(mc.options.getMaxFps(), oFps);
        set(mc.options.getBiomeBlendRadius(), oBlend);
        set(mc.options.getEntityDistanceScaling(), oEntity);
        set(mc.options.getParticles(), oParticles);
        set(mc.options.getEntityShadows(), oShadows);
        set(mc.options.getBobView(), oBob);
        applied = false;
    }

    private static <T> void set(SimpleOption<T> option, T value) {
        if (!option.getValue().equals(value)) option.setValue(value);
    }
}
