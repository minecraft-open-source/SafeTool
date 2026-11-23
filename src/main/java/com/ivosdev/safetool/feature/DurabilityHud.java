package com.ivosdev.safetool.feature;

import com.ivosdev.safetool.config.SafeToolConfig;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DurabilityHud {
	private static final int ICON_SIZE = 16;
	private static final int ROW_HEIGHT = 18;
	private static final int PADDING_X = 4;
	private static final int PADDING_Y = 3;
	private static final int ICON_TEXT_GAP = 4;

	private static final int COLOR_SAFE = 0xFF55FF55;
	private static final int COLOR_WARNING = 0xFFFFFF55;
	private static final int COLOR_CRITICAL = 0xFFFF4444;
	private static final int BACKDROP_COLOR = 0x8010141A;

	private static final EquipmentSlot[] ARMOR_SLOTS = {
		EquipmentSlot.HEAD,
		EquipmentSlot.CHEST,
		EquipmentSlot.LEGS,
		EquipmentSlot.FEET
	};

	public void onRenderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
		SafeToolConfig config = SafeToolConfig.get();
		if (!config.showHud) {
			return;
		}

		Minecraft client = Minecraft.getInstance();
		LocalPlayer player = client.player;
		if (player == null) {
			return;
		}

		List<ItemStack> trackedItems = collectTrackedItems(player);
		if (trackedItems.isEmpty()) {
			return;
		}

		Font font = client.font;
		int maxTextWidth = 0;
		String[] labels = new String[trackedItems.size()];
		int[] colors = new int[trackedItems.size()];

		for (int i = 0; i < trackedItems.size(); i++) {
			ItemStack stack = trackedItems.get(i);
			int remaining = ProtectionHandler.getRemainingDurability(stack);
			labels[i] = Integer.toString(remaining);
			colors[i] = getDurabilityColor(remaining, stack.getMaxDamage(), config.minDurabilityThreshold);
			maxTextWidth = Math.max(maxTextWidth, font.width(labels[i]));
		}

		int panelWidth = PADDING_X * 2 + ICON_SIZE + ICON_TEXT_GAP + maxTextWidth;
		int panelHeight = PADDING_Y * 2 + trackedItems.size() * ROW_HEIGHT;

		// hudX and hudY act as margins from the bottom-right corner by default
		int panelX = Math.max(2, graphics.guiWidth() - config.hudX - panelWidth);
		int panelY = Math.max(2, graphics.guiHeight() - config.hudY - panelHeight);

		graphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, BACKDROP_COLOR);

		int iconX = panelX + PADDING_X;
		int textRightX = panelX + panelWidth - PADDING_X;

		for (int i = 0; i < trackedItems.size(); i++) {
			int rowY = panelY + PADDING_Y + i * ROW_HEIGHT;
			graphics.fakeItem(trackedItems.get(i), iconX, rowY);

			int textWidth = font.width(labels[i]);
			int textX = textRightX - textWidth;
			int textY = rowY + (ICON_SIZE - font.lineHeight) / 2 + 1;
			graphics.text(font, labels[i], textX, textY, colors[i]);
		}
	}

	private static List<ItemStack> collectTrackedItems(LocalPlayer player) {
		List<ItemStack> items = new ArrayList<>(6);

		addIfDamageable(items, player.getMainHandItem());
		addIfDamageable(items, player.getOffhandItem());

		for (EquipmentSlot slot : ARMOR_SLOTS) {
			addIfDamageable(items, player.getItemBySlot(slot));
		}

		return items;
	}

	private static void addIfDamageable(List<ItemStack> target, ItemStack stack) {
		if (!stack.isEmpty() && stack.isDamageableItem()) {
			target.add(stack);
		}
	}

	private static int getDurabilityColor(int remaining, int maxDamage, int criticalThreshold) {
		if (remaining <= criticalThreshold || maxDamage <= 0) {
			return COLOR_CRITICAL;
		}
		float ratio = (float) remaining / (float) maxDamage;
		if (ratio <= 0.15F) {
			return COLOR_CRITICAL;
		}
		if (ratio <= 0.30F) {
			return COLOR_WARNING;
		}
		return COLOR_SAFE;
	}
}
