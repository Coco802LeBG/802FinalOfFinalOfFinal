package com.coco802.client.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class DurabilityModule extends TextHudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET, EquipmentSlot.MAINHAND
    };
    private static final String[] NAMES = {"Casque", "Plastron", "Jambières", "Bottes", "Main"};
    private final BoolSetting percent = addBool("percent", "Afficher en pourcentage", false);

    public DurabilityModule() { super("Durability", "Durabilité de l'armure et de l'item tenu", 4, 132); }

    @Override
    protected List<String> lines(MinecraftClient mc) {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(SLOTS[i]);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            int left = stack.getMaxDamage() - stack.getDamage();
            out.add(NAMES[i] + ": " + (percent.get() ? (left * 100 / stack.getMaxDamage()) + "%" : left + "/" + stack.getMaxDamage()));
        }
        return out;
    }
}
