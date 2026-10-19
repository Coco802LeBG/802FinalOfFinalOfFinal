package com.coco802.client.gui;

import com.coco802.client.Client802;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.Window;

/** GUI scale différent selon l'écran (inventaire, coffres, chat, pause, autres). */
public final class GuiScaleManager {
    public static final String[] KINDS = {"inventory", "container", "chat", "pause", "other"};
    public static final String[] LABELS = {"Inventaire", "Coffres / tables", "Chat", "Menu pause", "Autres menus"};

    private static boolean applying;

    private GuiScaleManager() {}

    public static String kind(Screen screen) {
        if (screen instanceof InventoryScreen || screen instanceof CreativeInventoryScreen) return "inventory";
        if (screen instanceof HandledScreen) return "container";
        if (screen instanceof ChatScreen) return "chat";
        if (screen instanceof GameMenuScreen) return "pause";
        return "other";
    }

    public static void init() {
        // À l'ouverture d'un écran
        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> apply(client, screen));
        // Vérifié à chaque tick : rattrape les cas où le jeu remet l'échelle d'origine
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.currentScreen == null) restore(client);
            else apply(client, client.currentScreen);
        });
    }

    private static int normal(MinecraftClient client) {
        return client.getWindow().calculateScaleFactor(client.options.getGuiScale().getValue(), client.forcesUnicodeFont());
    }

    private static void apply(MinecraftClient client, Screen screen) {
        if (applying || client.options == null) return;
        int wanted = Client802.CONFIG.scaleFor(kind(screen));
        Window window = client.getWindow();
        int target = wanted <= 0 ? normal(client) : window.calculateScaleFactor(wanted, client.forcesUnicodeFont());
        if (target == (int) window.getScaleFactor()) return;
        window.setScaleFactor(target);
        applying = true;
        try {
            screen.resize(window.getScaledWidth(), window.getScaledHeight());
        } finally {
            applying = false;
        }
    }

    private static void restore(MinecraftClient client) {
        if (client.options == null) return;
        Window window = client.getWindow();
        int normal = normal(client);
        if (normal != (int) window.getScaleFactor()) window.setScaleFactor(normal);
    }
}
