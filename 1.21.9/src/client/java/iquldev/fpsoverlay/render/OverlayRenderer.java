package iquldev.fpsoverlay.render;

import iquldev.fpsoverlay.config.InfoOverlayConfig;
import iquldev.fpsoverlay.stats.OverlayStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import iquldev.fpsoverlay.util.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class OverlayRenderer {
    private static final int MODERN_H_PADDING = 8;
    private static final int MODERN_V_PADDING = 4;
    private static final int CLASSIC_H_PADDING = 5;
    private static final int CLASSIC_V_PADDING = 3;

    private static int getHorizontalPadding() {
        return InfoOverlayConfig.isClassicStyle ? CLASSIC_H_PADDING : MODERN_H_PADDING;
    }

    private static int getVerticalPadding() {
        return InfoOverlayConfig.isClassicStyle ? CLASSIC_V_PADDING : MODERN_V_PADDING;
    }

    private static int getBoxGap() {
        if (InfoOverlayConfig.isVertical) return 4;
        return InfoOverlayConfig.isClassicStyle ? 12 : 4;
    }

    public static void render(DrawContext context, MinecraftClient client, 
                                   OverlayStats stats, DynamicTextManager dynamicTextManager, 
                                   boolean isHidden) {
        if (isHidden) return;

        boolean isShowed = InfoOverlayConfig.isShowed;
        boolean isAdvancedShowed = InfoOverlayConfig.isAdvancedShowed;
        InfoOverlayConfig.OverlayPosition overlayPosition = InfoOverlayConfig.overlayPosition;

        int hPadding = getHorizontalPadding();
        int vPadding = getVerticalPadding();

        int overlayBackgroundColor = ColorUtils.parseColor(InfoOverlayConfig.overlayBackgroundColor, InfoOverlayConfig.overlayTransparency);
        int advancedBackgroundColor = ColorUtils.parseColor(InfoOverlayConfig.advancedBackgroundColor, InfoOverlayConfig.advancedTransparency);
        int overlayTextColor = ColorUtils.parseColor(InfoOverlayConfig.overlayTextColor, 100);
        int advancedTextColor = ColorUtils.parseColor(InfoOverlayConfig.advancedTextColor, 100);

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();

        stats.fps().update(client.getCurrentFps());

        String overlayText = dynamicTextManager.getOverlayText(client, stats);
        String advancedText = dynamicTextManager.getAdvancedText(client, stats);

        int overlayTextWidth = client.textRenderer.getWidth(overlayText);
        int fontHeight = client.textRenderer.fontHeight;
        int advancedTextWidth = client.textRenderer.getWidth(advancedText);

        Position overlayPos = calculateOverlayPosition(overlayPosition, screenWidth, screenHeight, overlayTextWidth, fontHeight, hPadding, vPadding);

        if (isShowed) {
            drawRoundedRect(context, overlayPos.x() - hPadding, overlayPos.y() - vPadding, 
                           overlayPos.x() + overlayTextWidth + hPadding, overlayPos.y() + fontHeight + vPadding, 
                           InfoOverlayConfig.overlayRounding, overlayBackgroundColor);
            context.drawText(client.textRenderer, overlayText, overlayPos.x(), 
                           overlayPos.y(), overlayTextColor, false);
        }

        if (isAdvancedShowed) {
            Position advancedPos = calculateAdvancedPosition(overlayPosition, overlayPos, 
                                                                 advancedTextWidth, overlayTextWidth, fontHeight, 
                                                                 screenWidth, screenHeight, isShowed, hPadding, vPadding);
            
            drawRoundedRect(context, advancedPos.x() - hPadding, advancedPos.y() - vPadding, 
                           advancedPos.x() + advancedTextWidth + hPadding, advancedPos.y() + fontHeight + vPadding, 
                           InfoOverlayConfig.advancedRounding, advancedBackgroundColor);
            context.drawText(client.textRenderer, advancedText, advancedPos.x(), 
                           advancedPos.y(), advancedTextColor, false);
        }
    }

    private static void drawRoundedRect(DrawContext context, int x1, int y1, int x2, int y2, int radius, int color) {
        if (InfoOverlayConfig.isClassicStyle) {
            int extraWidth = 3;
            int crossPadding = 3;
            context.fill(x1, y1, x2, y2, color);
            context.fill(x1 - extraWidth, y1 + crossPadding, x1, y2 - crossPadding, color);
            context.fill(x2, y1 + crossPadding, x2 + extraWidth, y2 - crossPadding, color);
            return;
        }

        int width = x2 - x1;
        int height = y2 - y1;
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        
        if (radius <= 0) {
            context.fill(x1, y1, x2, y2, color);
            return;
        }

        context.fill(x1 + radius, y1, x2 - radius, y2, color);
        context.fill(x1, y1 + radius, x1 + radius, y2 - radius, color);
        context.fill(x2 - radius, y1 + radius, x2, y2 - radius, color);

        for (int i = 0; i < radius; i++) {
            double yPos = radius - (i + 0.5);
            int xLen = (int) Math.round(Math.sqrt(radius * radius - yPos * yPos));
            
            context.fill(x1 + radius - xLen, y1 + i, x1 + radius, y1 + i + 1, color);
            context.fill(x2 - radius, y1 + i, x2 - radius + xLen, y1 + i + 1, color);
            context.fill(x1 + radius - xLen, y2 - i - 1, x1 + radius, y2 - i, color);
            context.fill(x2 - radius, y2 - i - 1, x2 - radius + xLen, y2 - i, color);
        }
    }

    private static Position calculateOverlayPosition(InfoOverlayConfig.OverlayPosition position, 
                                                   int screenWidth, int screenHeight, 
                                                   int textWidth, int textHeight, int hPadding, int vPadding) {
        return switch (position) {
            case TOP_RIGHT -> new Position(screenWidth - 15 - textWidth - hPadding, 10);
            case BOTTOM_LEFT -> new Position(15, screenHeight - 10 - textHeight - vPadding);
            case BOTTOM_RIGHT -> new Position(screenWidth - 15 - textWidth - hPadding, 
                                             screenHeight - 10 - textHeight - vPadding);
            default -> new Position(15, 10);
        };
    }

    private static Position calculateAdvancedPosition(InfoOverlayConfig.OverlayPosition position, 
                                                     Position overlayPos, int advancedTextWidth, int overlayTextWidth,
                                                     int textHeight, int screenWidth, int screenHeight, 
                                                     boolean isShowed, int hPadding, int vPadding) {
        int boxGap = getBoxGap();

        if (isShowed) {
            if (InfoOverlayConfig.isVertical) {
                int x = switch (position) {
                    case TOP_RIGHT, BOTTOM_RIGHT -> overlayPos.x() + overlayTextWidth - advancedTextWidth;
                    default -> overlayPos.x();
                };
                int y = switch (position) {
                    case BOTTOM_LEFT, BOTTOM_RIGHT -> overlayPos.y() - textHeight - vPadding * 2 - boxGap;
                    default -> overlayPos.y() + textHeight + vPadding * 2 + boxGap;
                };
                return new Position(x, y);
            } else {
                int x = switch (position) {
                    case TOP_RIGHT, BOTTOM_RIGHT -> overlayPos.x() - advancedTextWidth - hPadding * 2 - boxGap;
                    default -> overlayPos.x() + overlayTextWidth + hPadding * 2 + boxGap;
                };
                return new Position(x, overlayPos.y());
            }
        } else {
            return switch (position) {
                case TOP_RIGHT -> new Position(screenWidth - 10 - advancedTextWidth - hPadding * 2, 10);
                case BOTTOM_RIGHT -> new Position(screenWidth - 10 - advancedTextWidth - hPadding * 2, 
                                                screenHeight - 10 - textHeight - vPadding);
                case BOTTOM_LEFT -> new Position(15, screenHeight - 10 - textHeight - vPadding);
                default -> new Position(15, 10);
            };
        }
    }

    private record Position(int x, int y) {}
}
