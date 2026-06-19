package iquldev.fpsoverlay.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import iquldev.fpsoverlay.config.YACLConfigScreen;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Shadow
    protected abstract int getHorizontalPosition(int currentButton, int numberOfButtons, int buttonWidth);

    @Definition(id = "numberOfButtons", local = @Local(type = int.class, name = "numberOfButtons"))
    @Expression("numberOfButtons = ?")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER))
    private void adjustAmountOfIconButtons(CallbackInfo ci, @Local(name = "numberOfButtons") LocalIntRef numberOfButtons) {
        numberOfButtons.set(numberOfButtons.get() + 1);
    }

    @WrapOperation(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/TitleScreen;getHorizontalPosition(III)I"))
    private int replaceInlinedConstant(TitleScreen instance, int currentButton, int numberOfButtons, int buttonWidth, Operation<Integer> original, @Local(name = "numberOfButtons") int actualNumberOfButtons) {
        return original.call(instance, currentButton, actualNumberOfButtons, buttonWidth);
    }

    @Definition(id = "width", field = "Lnet/minecraft/client/gui/screens/TitleScreen;width:I")
    @Expression("this.width / 2 - 100")
    @Inject(method = "init", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private void addConfigButton(CallbackInfo ci, @Local(name = "currentButton") LocalIntRef currentButton, @Local(name = "topPos") int topPos, @Local(name = "numberOfButtons") int numberOfButtons) {
        currentButton.set(currentButton.get() + 1);
        Screen screen = (TitleScreen) (Object) this;
        
        ConfigButton configButton = new ConfigButton(
            this.getHorizontalPosition(currentButton.get(), numberOfButtons, 20),
            topPos,
            20,
            20,
            Component.translatable("fpsoverlay.title"),
            button -> this.minecraft.gui.setScreen(YACLConfigScreen.createScreen(screen))
        );
        configButton.setTooltip(Tooltip.create(Component.translatable("fpsoverlay.title")));
        Screens.getWidgets(screen).add(configButton);
    }

    private static class ConfigButton extends Button.Plain {
        @org.jspecify.annotations.NonNull
        private static final Identifier ICON = Identifier.fromNamespaceAndPath("fpsoverlay", "textures/icon.png");

        public ConfigButton(int x, int y, int width, int height, Component message, OnPress onPress) {
            super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
        }

        @Override
        protected void extractContents(@org.jspecify.annotations.NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            this.extractDefaultSprite(graphics);
            
            int iconSize = 16;
            int iconX = this.getX() + (this.width - iconSize) / 2;
            int iconY = this.getY() + (this.height - iconSize) / 2;
            
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON,
                iconX,
                iconY,
                0.0f,
                0.0f,
                iconSize,
                iconSize,
                iconSize,
                iconSize
            );
        }
    }
}
