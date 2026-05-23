package iquldev.fpsoverlay;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import iquldev.fpsoverlay.config.YACLConfigScreen;

public class InfoOverlayModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return YACLConfigScreen::createScreen;
    }
}