package com.ivosdev.safetool;

import com.ivosdev.safetool.config.SafeToolConfig;
import com.ivosdev.safetool.feature.DurabilityHud;
import com.ivosdev.safetool.feature.ProtectionHandler;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SafeToolClient implements ClientModInitializer {
	public static final String MOD_ID = "safetool";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static final KeyMapping.Category KEY_CATEGORY =
		KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MOD_ID, "general"));

	private static KeyMapping toggleKey;
	private final DurabilityHud durabilityHud = new DurabilityHud();

	@Override
	public void onInitializeClient() {
		SafeToolConfig.load();

		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.safetool.toggle",
			InputConstants.Type.KEYBOARD,
			InputConstants.KEY_K,
			KEY_CATEGORY
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				SafeToolConfig config = SafeToolConfig.get();
				config.modEnabled = !config.modEnabled;
				SafeToolConfig.save();

				if (client.player != null) {
					Component msg = config.modEnabled
						? Component.translatable("message.safetool.toggle.enabled").withStyle(ChatFormatting.GREEN)
						: Component.translatable("message.safetool.toggle.disabled").withStyle(ChatFormatting.RED);
					client.player.sendOverlayMessage(msg);
				}
			}
		});

		ProtectionHandler.register();

		HudElementRegistry.attachElementBefore(
			VanillaHudElements.CHAT,
			Identifier.fromNamespaceAndPath(MOD_ID, "durability_hud"),
			durabilityHud::onRenderHud
		);

		LOGGER.info("Initialized SafeTool (client-side protection active).");
	}
}
