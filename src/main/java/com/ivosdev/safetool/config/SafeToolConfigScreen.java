package com.ivosdev.safetool.config;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class SafeToolConfigScreen extends Screen {
	private static final int BUTTON_WIDTH = 220;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ROW_SPACING = 26;

	private final Screen parent;

	public SafeToolConfigScreen(Screen parent) {
		super(Component.translatable("screen.safetool.config.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		SafeToolConfig config = SafeToolConfig.get();
		int leftX = (this.width - BUTTON_WIDTH) / 2;
		int startY = this.height / 4;

		this.addRenderableWidget(
			Button.builder(getEnabledLabel(config.modEnabled), button -> {
				config.modEnabled = !config.modEnabled;
				button.setMessage(getEnabledLabel(config.modEnabled));
			})
			.bounds(leftX, startY, BUTTON_WIDTH, BUTTON_HEIGHT)
			.build()
		);

		this.addRenderableWidget(
			Button.builder(getShowHudLabel(config.showHud), button -> {
				config.showHud = !config.showHud;
				button.setMessage(getShowHudLabel(config.showHud));
			})
			.bounds(leftX, startY + ROW_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT)
			.build()
		);

		this.addRenderableWidget(
			Button.builder(getThresholdLabel(config.minDurabilityThreshold), button -> {
				int next = config.cycleMinDurabilityThreshold();
				button.setMessage(getThresholdLabel(next));
			})
			.bounds(leftX, startY + ROW_SPACING * 2, BUTTON_WIDTH, BUTTON_HEIGHT)
			.build()
		);

		this.addRenderableWidget(
			Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
			.bounds((this.width - 200) / 2, this.height - 28, 200, BUTTON_HEIGHT)
			.build()
		);
	}

	private static Component getEnabledLabel(boolean enabled) {
		return CommonComponents.optionStatus(Component.translatable("option.safetool.mod_enabled"), enabled);
	}

	private static Component getShowHudLabel(boolean showHud) {
		return CommonComponents.optionStatus(Component.translatable("option.safetool.show_hud"), showHud);
	}

	private static Component getThresholdLabel(int threshold) {
		return Component.translatable("option.safetool.min_durability_threshold", threshold);
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
		super.extractRenderState(graphics, mouseX, mouseY, a);
		graphics.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
	}

	@Override
	public void onClose() {
		SafeToolConfig.save();
		this.minecraft.gui.setScreen(this.parent);
	}
}
