package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

/** Compteurs d'items PvP (totems, crystals, obsidienne, ancres...). */
public final class PvpCountersModule extends Module802 {
    private record Entry(BoolSetting setting, Item item) {}

    private final List<Entry> entries = new ArrayList<>();

    public PvpCountersModule() {
        super("PvP Counters", "Totems, crystals, obsidienne, ancres...");
        addPosition(4, 220);
        add("totem", "Totems", Items.TOTEM_OF_UNDYING, true);
        add("crystal", "End crystals", Items.END_CRYSTAL, true);
        add("obsidian", "Obsidienne", Items.OBSIDIAN, true);
        add("anchor", "Respawn anchors", Items.RESPAWN_ANCHOR, false);
        add("glowstone", "Glowstone", Items.GLOWSTONE, false);
        add("pearl", "Ender pearls", Items.ENDER_PEARL, false);
        add("gapple", "Golden apples", Items.ENCHANTED_GOLDEN_APPLE, false);
    }

    private void add(String key, String label, Item item, boolean def) {
        entries.add(new Entry(addBool(key, label, def), item));
    }

    @Override public String category() { return "PvP"; }
    @Override public int boxWidth() { return 44; }
    @Override public int boxHeight() { return 36; }

    @Override
    public void onHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.player == null) return;
        int y = posY.getInt();
        for (Entry e : entries) {
            if (!e.setting().get()) continue;
            int total = 0;
            for (int i = 0; i < mc.player.getInventory().size(); i++) {
                ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack.isOf(e.item())) total += stack.getCount();
            }
            ctx.drawItem(new ItemStack(e.item()), posX.getInt(), y);
            ctx.drawTextWithShadow(mc.textRenderer, String.valueOf(total), posX.getInt() + 20, y + 5, 0xFFFFFFFF);
            y += 18;
        }
    }
}
