package com.ivosdev.safetool.compat;

import com.ivosdev.safetool.config.SafeToolConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SafeToolConfigScreen::new;
	}
}
