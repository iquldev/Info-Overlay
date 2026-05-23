package iquldev.fpsoverlay;

import iquldev.fpsoverlay.render.OverlayRenderer;
import iquldev.fpsoverlay.stats.FpsStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;

import iquldev.fpsoverlay.stats.SessionStats;
import iquldev.fpsoverlay.stats.SystemStats;
import iquldev.fpsoverlay.stats.OverlayStats;
import iquldev.fpsoverlay.stats.MediaStats;

public class InfoOverlayClient implements ClientModInitializer {
    private static KeyMapping keyBinding;
    private boolean isHidden = false;
    
    private final FpsStats fpsStats = new FpsStats();
    private final SystemStats systemStats = new SystemStats();
    private final SessionStats sessionStats = new SessionStats();
    private final MediaStats mediaStats = new MediaStats();
    private final OverlayStats overlayStats = new OverlayStats(fpsStats, systemStats, sessionStats, mediaStats);
    private final DynamicTextManager dynamicTextManager = new DynamicTextManager();

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.HOTBAR,
            Identifier.fromNamespaceAndPath("fpsoverlay", "info_overlay"),
            this::renderOverlay
        );

        keyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            "fpsoverlay.keys.show",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_F1,
            KeyMapping.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.consumeClick()) {
                isHidden = !isHidden;
            }
        });
    }

    private void renderOverlay(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        
        systemStats.update();
        dynamicTextManager.updateDynamicText();
        
        OverlayRenderer.render(context, client, overlayStats, dynamicTextManager, isHidden);
    }
}
