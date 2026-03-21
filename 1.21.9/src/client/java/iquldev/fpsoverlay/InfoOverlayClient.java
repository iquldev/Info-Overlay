package iquldev.fpsoverlay;

import iquldev.fpsoverlay.render.OverlayRenderer;
import iquldev.fpsoverlay.stats.FpsStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.render.RenderTickCounter;

import iquldev.fpsoverlay.stats.SessionStats;
import iquldev.fpsoverlay.stats.SystemStats;
import iquldev.fpsoverlay.stats.OverlayStats;

public class InfoOverlayClient implements ClientModInitializer {
    private static KeyBinding keyBinding;
    private boolean isHidden = false;
    
    private final FpsStats fpsStats = new FpsStats();
    private final SystemStats systemStats = new SystemStats();
    private final SessionStats sessionStats = new SessionStats();
    private final OverlayStats overlayStats = new OverlayStats(fpsStats, systemStats, sessionStats);
    private final DynamicTextManager dynamicTextManager = new DynamicTextManager();

    @Override
    public void onInitializeClient() {
        HudElementRegistry.attachElementBefore(
            VanillaHudElements.CROSSHAIR,
            Identifier.of("fpsoverlay", "info_overlay"),
            this::renderOverlay
        );

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "fpsoverlay.keys.show",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F1,
            KeyBinding.Category.MISC
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                isHidden = !isHidden;
            }
        });
    }

    private void renderOverlay(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        
        systemStats.update();
        dynamicTextManager.updateDynamicText();
        
        OverlayRenderer.render(context, client, overlayStats, dynamicTextManager, isHidden);
    }
}