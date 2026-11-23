package com.ivosdev.safetool.feature;

import com.ivosdev.safetool.config.SafeToolConfig;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public final class ProtectionHandler {
	private static final long SOUND_COOLDOWN_MS = 500L;
	private static long lastSoundTimeMs = 0L;

	private ProtectionHandler() {
	}

	public static void register() {
		AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) ->
			evaluateAction(player, level, hand, false)
		);

		AttackEntityCallback.EVENT.register((player, level, hand, entity, hitResult) ->
			evaluateAction(player, level, hand, false)
		);

		UseBlockCallback.EVENT.register((player, level, hand, hitResult) ->
			evaluateAction(player, level, hand, true)
		);

		UseItemCallback.EVENT.register((player, level, hand) ->
			evaluateAction(player, level, hand, true)
		);
	}

	private static InteractionResult evaluateAction(Player player, Level level, InteractionHand hand, boolean isUseAction) {
		if (!level.isClientSide()) {
			return InteractionResult.PASS;
		}

		SafeToolConfig config = SafeToolConfig.get();
		if (!config.modEnabled || isOverrideActive(player)) {
			return InteractionResult.PASS;
		}

		int threshold = config.minDurabilityThreshold;
		ItemStack activeStack = player.getItemInHand(hand);

		// Elytra firework boost protection when using a Firework Rocket
		if (isUseAction && isFireworkRocket(activeStack)) {
			ItemStack chestStack = player.getItemBySlot(EquipmentSlot.CHEST);
			if (isElytra(chestStack) && isCriticallyDamaged(chestStack, threshold)) {
				notifyBlocked(player, "message.safetool.elytra_critical");
				return InteractionResult.FAIL;
			}
		}

		// Protect damageable item in the interacting hand (main hand or offhand)
		if (isCriticallyDamaged(activeStack, threshold)) {
			notifyBlocked(player, "message.safetool.tool_critical");
			return InteractionResult.FAIL;
		}

		return InteractionResult.PASS;
	}

	private static boolean isOverrideActive(Player player) {
		return player.isShiftKeyDown() || player.isCrouching();
	}

	private static boolean isFireworkRocket(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.FIREWORK_ROCKET;
	}

	private static boolean isElytra(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem() == Items.ELYTRA;
	}

	public static int getRemainingDurability(ItemStack stack) {
		return Math.max(0, stack.getMaxDamage() - stack.getDamageValue());
	}

	public static boolean isCriticallyDamaged(ItemStack stack, int threshold) {
		return !stack.isEmpty()
			&& stack.isDamageableItem()
			&& getRemainingDurability(stack) <= threshold;
	}

	private static void notifyBlocked(Player player, String translationKey) {
		Component warning = Component.translatable(translationKey).withStyle(ChatFormatting.RED);
		player.sendOverlayMessage(warning);

		long now = System.currentTimeMillis();
		if (now - lastSoundTimeMs >= SOUND_COOLDOWN_MS) {
			lastSoundTimeMs = now;
			Minecraft.getInstance().getSoundManager().play(
				SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_BASS.value(), 0.75F, 0.65F)
			);
		}
	}
}
