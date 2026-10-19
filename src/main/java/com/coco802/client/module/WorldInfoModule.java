package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public final class WorldInfoModule extends TextHudModule {
    private final BoolSetting biome = addBool("biome", "Biome", true);
    private final BoolSetting light = addBool("light", "Niveau de lumière", true);
    private final BoolSetting target = addBool("target", "Bloc visé", false);
    private final BoolSetting chunk = addBool("chunk", "Chunk", false);
    private final BoolSetting players = addBool("players", "Joueurs chargés", false);

    public WorldInfoModule() { super("World Info", "Biome, lumière, bloc visé, chunk, joueurs", 4, 228); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        List<String> out = new ArrayList<>();
        if (mc.world == null) return out;
        BlockPos pos = mc.player.getBlockPos();
        if (biome.get()) {
            out.add("Biome: " + mc.world.getBiome(pos).getKey().map(k -> k.getValue().getPath()).orElse("?"));
        }
        if (light.get()) out.add("Light: " + mc.world.getLightLevel(pos));
        if (target.get()) {
            String name = "-";
            if (mc.crosshairTarget instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
                name = mc.world.getBlockState(hit.getBlockPos()).getBlock().getName().getString();
            }
            out.add("Target: " + name);
        }
        if (chunk.get()) out.add("Chunk: " + (pos.getX() >> 4) + " " + (pos.getZ() >> 4));
        if (players.get()) out.add("Players: " + mc.world.getPlayers().size());
        return out;
    }
}
