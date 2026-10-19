package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;

import java.util.List;

public final class ServerModule extends TextHudModule {
    public ServerModule() { super("Server", "Adresse du serveur actuel", 4, 118); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        if (mc.getCurrentServerEntry() == null) return List.of("Solo");
        return List.of(mc.getCurrentServerEntry().address);
    }
}
