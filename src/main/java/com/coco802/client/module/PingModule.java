package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public final class PingModule extends TextHudModule {
    public PingModule() { super("Ping", "Latence vers le serveur", 4, 50); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null) return List.of();
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        if (entry == null) return List.of();
        return List.of("Ping: " + entry.getLatency() + " ms");
    }
}
