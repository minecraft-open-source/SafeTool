package com.ivosdev.safetool.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.ivosdev.safetool.SafeToolClient;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class SafeToolConfig {
	public static final int[] THRESHOLD_PRESETS = {1, 3, 5, 10, 15};

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH =
		FabricLoader.getInstance().getConfigDir().resolve("safetool.json");

	private static SafeToolConfig instance = new SafeToolConfig();

	public boolean modEnabled = true;
	public boolean showHud = true;
	public int minDurabilityThreshold = 5;
	public int hudX = 10;
	public int hudY = 10;

	public static SafeToolConfig get() {
		return instance;
	}

	public int cycleMinDurabilityThreshold() {
		for (int i = 0; i < THRESHOLD_PRESETS.length; i++) {
			if (this.minDurabilityThreshold < THRESHOLD_PRESETS[i]) {
				this.minDurabilityThreshold = THRESHOLD_PRESETS[i];
				return this.minDurabilityThreshold;
			}
			if (this.minDurabilityThreshold == THRESHOLD_PRESETS[i]) {
				this.minDurabilityThreshold = THRESHOLD_PRESETS[(i + 1) % THRESHOLD_PRESETS.length];
				return this.minDurabilityThreshold;
			}
		}
		this.minDurabilityThreshold = THRESHOLD_PRESETS[0];
		return this.minDurabilityThreshold;
	}

	private void sanitize() {
		if (this.minDurabilityThreshold < 1) {
			this.minDurabilityThreshold = 1;
		}
		if (this.hudX < 0) {
			this.hudX = 0;
		}
		if (this.hudY < 0) {
			this.hudY = 0;
		}
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			instance = new SafeToolConfig();
			save();
			return;
		}

		try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
			SafeToolConfig loaded = GSON.fromJson(reader, SafeToolConfig.class);
			instance = (loaded != null) ? loaded : new SafeToolConfig();
			instance.sanitize();
		} catch (IOException | JsonParseException e) {
			SafeToolClient.LOGGER.error("Failed to load SafeTool config from {}, using defaults.", CONFIG_PATH, e);
			instance = new SafeToolConfig();
		}
	}

	public static void save() {
		try {
			instance.sanitize();
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
				GSON.toJson(instance, writer);
			}
		} catch (IOException e) {
			SafeToolClient.LOGGER.error("Failed to save SafeTool config to {}.", CONFIG_PATH, e);
		}
	}
}
