package iquldev.fpsoverlay.render;

import iquldev.fpsoverlay.config.InfoOverlayConfig;
import iquldev.fpsoverlay.stats.OverlayStats;
import iquldev.fpsoverlay.text.DynamicTextManager;
import iquldev.fpsoverlay.util.ColorUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

public class OverlayRenderer {
    private static final int MODERN_H_PADDING = 8;
    private static final int MODERN_V_PADDING = 4;
    private static final int CLASSIC_H_PADDING = 5;
    private static final int CLASSIC_V_PADDING = 4;

    private static final int MEDIA_ICON_TEXT_GAP = 6;
    private static final int MEDIA_LINE_GAP = 2;
    private static final int MEDIA_BEFORE_BAR = 4;
    private static final int MEDIA_BAR_HEIGHT = 2;
    private static final int MEDIA_AFTER_BAR = 2;
    private static final String MEDIA_TIME_WIDTH_SAMPLE = "9:59:59 / 9:59:59";
    private static final String MEDIA_TIME_COMPACT_SAMPLE = " 9:59:59";

    private static int lastScreenWidth = -1, lastScreenHeight = -1;
    private static int lastOverlayTextWidth = -1, lastAdvancedTextWidth = -1;
    private static InfoOverlayConfig.OverlayPosition lastPosition = null;
    private static boolean lastIsVertical = false;
    private static boolean lastIsClassic = false;
    private static int lastHPadding = -1, lastVPadding = -1;
    private static boolean lastMediaCompact = false;
    private static int lastMediaMaxLine = -1;
    private static int lastMediaCompactMax = -1;
    private static int lastMediaLayoutEpoch = Integer.MIN_VALUE;

    private static Position cachedOverlayPos = null;
    private static Position cachedAdvancedPos = null;

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

        int fontHeight = client.textRenderer.fontHeight;
        int overlayContentWidth = measurePlaceholderContentWidth(client, overlayText, stats.media(), InfoOverlayConfig.isOverlayMedia);
        int advancedContentWidth = measurePlaceholderContentWidth(client, advancedText, stats.media(), InfoOverlayConfig.isAdvancedMedia);
        int overlayBlockHeight = measurePlaceholderContentHeight(client, overlayText, stats.media(), fontHeight, InfoOverlayConfig.isOverlayMedia);
        int advancedBlockHeight = measurePlaceholderContentHeight(client, advancedText, stats.media(), fontHeight, InfoOverlayConfig.isAdvancedMedia);

        boolean needsRecalc = screenWidth != lastScreenWidth || screenHeight != lastScreenHeight ||
                            overlayContentWidth != lastOverlayTextWidth || advancedContentWidth != lastAdvancedTextWidth ||
                            overlayPosition != lastPosition || InfoOverlayConfig.isVertical != lastIsVertical ||
                            InfoOverlayConfig.isClassicStyle != lastIsClassic || hPadding != lastHPadding || vPadding != lastVPadding ||
                            InfoOverlayConfig.mediaWidgetCompact != lastMediaCompact ||
                            InfoOverlayConfig.mediaMaxLineChars != lastMediaMaxLine ||
                            InfoOverlayConfig.mediaCompactMaxChars != lastMediaCompactMax ||
                            stats.media().mediaLayoutEpoch() != lastMediaLayoutEpoch;

        if (needsRecalc || cachedOverlayPos == null || cachedAdvancedPos == null) {
            cachedOverlayPos = calculateOverlayPosition(overlayPosition, screenWidth, screenHeight, overlayContentWidth, overlayBlockHeight, hPadding, vPadding);
            cachedAdvancedPos = calculateAdvancedPosition(overlayPosition, cachedOverlayPos,
                    advancedContentWidth, overlayContentWidth, overlayBlockHeight, advancedBlockHeight,
                    screenWidth, screenHeight, isShowed, hPadding, vPadding);

            lastScreenWidth = screenWidth;
            lastScreenHeight = screenHeight;
            lastOverlayTextWidth = overlayContentWidth;
            lastAdvancedTextWidth = advancedContentWidth;
            lastPosition = overlayPosition;
            lastIsVertical = InfoOverlayConfig.isVertical;
            lastIsClassic = InfoOverlayConfig.isClassicStyle;
            lastHPadding = hPadding;
            lastVPadding = vPadding;
            lastMediaCompact = InfoOverlayConfig.mediaWidgetCompact;
            lastMediaMaxLine = InfoOverlayConfig.mediaMaxLineChars;
            lastMediaCompactMax = InfoOverlayConfig.mediaCompactMaxChars;
            lastMediaLayoutEpoch = stats.media().mediaLayoutEpoch();
        }

        if (InfoOverlayConfig.isOverlayMedia) {
            renderMediaWidget(context, client, stats.media(), cachedOverlayPos, overlayBackgroundColor, overlayTextColor, hPadding, vPadding, InfoOverlayConfig.overlayRounding);
        } else if (isShowed) {
            drawRoundedRect(context, cachedOverlayPos.x() - hPadding, cachedOverlayPos.y() - vPadding,
                           cachedOverlayPos.x() + overlayContentWidth + hPadding, cachedOverlayPos.y() + fontHeight + vPadding,
                           InfoOverlayConfig.overlayRounding, overlayBackgroundColor);
            context.drawText(client.textRenderer, overlayText, cachedOverlayPos.x(),
                           cachedOverlayPos.y(), overlayTextColor, false);
        }

        if (InfoOverlayConfig.isAdvancedMedia) {
            renderMediaWidget(context, client, stats.media(), cachedAdvancedPos, advancedBackgroundColor, advancedTextColor, hPadding, vPadding, InfoOverlayConfig.advancedRounding);
        } else if (isAdvancedShowed) {
            drawRoundedRect(context, cachedAdvancedPos.x() - hPadding, cachedAdvancedPos.y() - vPadding,
                           cachedAdvancedPos.x() + advancedContentWidth + hPadding, cachedAdvancedPos.y() + fontHeight + vPadding,
                           InfoOverlayConfig.advancedRounding, advancedBackgroundColor);
            context.drawText(client.textRenderer, advancedText, cachedAdvancedPos.x(), 
                           cachedAdvancedPos.y(), advancedTextColor, false);
        }
    }

    private static void renderMediaWidget(DrawContext context, MinecraftClient client, iquldev.fpsoverlay.stats.MediaStats media, Position pos, int bgColor, int textColor, int hPadding, int vPadding, int rounding) {
        iquldev.fpsoverlay.stats.MediaStats.MediaInfo info = media.getInfo();
        int fontHeight = client.textRenderer.fontHeight;
        int iconW = client.textRenderer.getWidth("▶");
        int textX = pos.x() + iconW + MEDIA_ICON_TEXT_GAP;
        int lineMax = Math.max(8, InfoOverlayConfig.mediaMaxLineChars);
        int compactMax = Math.max(12, InfoOverlayConfig.mediaCompactMaxChars);

        if (Util.getOperatingSystem() == Util.OperatingSystem.OSX) {
            int musicIconW = client.textRenderer.getWidth("🎵");
            int rowTextX = pos.x() + musicIconW + MEDIA_ICON_TEXT_GAP;
            String title = ellipsizeUnicode(Text.translatable("fpsoverlay.media.unsupported_os").getString(), lineMax);
            String artist = ellipsizeUnicode(Text.translatable("fpsoverlay.media.unsupported_os_mac").getString(), lineMax);
            int contentW = Math.max(client.textRenderer.getWidth(title), client.textRenderer.getWidth(artist));
            int totalW = musicIconW + MEDIA_ICON_TEXT_GAP + contentW;
            int innerH = fontHeight * 2 + MEDIA_LINE_GAP;
            int y2 = pos.y() + innerH + vPadding;
            drawRoundedRect(context, pos.x() - hPadding, pos.y() - vPadding, pos.x() + totalW + hPadding, y2, rounding, bgColor);
            int iconY = pos.y() + (innerH - fontHeight) / 2;
            context.drawText(client.textRenderer, "🎵", pos.x(), iconY, textColor, false);
            context.drawText(client.textRenderer, title, rowTextX, pos.y(), textColor, false);
            context.drawText(client.textRenderer, "§7" + artist, rowTextX, pos.y() + fontHeight + MEDIA_LINE_GAP, textColor, false);
            return;
        }

        if (info.isEmpty()) {
            int musicIconW = client.textRenderer.getWidth("🎵");
            int rowTextX = pos.x() + musicIconW + MEDIA_ICON_TEXT_GAP;
            String title = info.title().isEmpty() ? Text.translatable("fpsoverlay.media.widget_name").getString() : info.title();
            String artist = info.artist().isEmpty() ? Text.translatable("fpsoverlay.media.not_playing").getString() : info.artist();
            title = ellipsizeUnicode(title, lineMax);
            artist = ellipsizeUnicode(artist, lineMax);
            int contentW = Math.max(client.textRenderer.getWidth(title), client.textRenderer.getWidth(artist));
            int totalW = musicIconW + MEDIA_ICON_TEXT_GAP + contentW;
            int innerH = fontHeight * 2 + MEDIA_LINE_GAP;
            int y2 = pos.y() + innerH + vPadding;
            drawRoundedRect(context, pos.x() - hPadding, pos.y() - vPadding, pos.x() + totalW + hPadding, y2, rounding, bgColor);
            int iconY = pos.y() + (innerH - fontHeight) / 2;
            context.drawText(client.textRenderer, "🎵", pos.x(), iconY, textColor, false);
            context.drawText(client.textRenderer, title, rowTextX, pos.y(), textColor, false);
            context.drawText(client.textRenderer, "§7" + artist, rowTextX, pos.y() + fontHeight + MEDIA_LINE_GAP, textColor, false);
            return;
        }

        String titleRaw = info.title();
        String artistRaw = info.artist();
        String title = ellipsizeUnicode(titleRaw, lineMax);
        String artist = ellipsizeUnicode(artistRaw, lineMax);
        String time = formatTime(info.progressMs()) + " / " + formatTime(info.durationMs());

        if (InfoOverlayConfig.mediaWidgetCompact) {
            String core = titleRaw.isEmpty() ? artistRaw : (artistRaw.isEmpty() ? titleRaw : titleRaw + " · " + artistRaw);
            String line = ellipsizeUnicode(core, compactMax);
            String suffix = info.durationMs() > 0
                    ? ("  " + formatTime(info.progressMs()) + "/" + formatTime(info.durationMs()))
                    : ("  " + formatTime(info.progressMs()));
            int contentW = client.textRenderer.getWidth(line) + client.textRenderer.getWidth("§8" + suffix);
            int totalW = iconW + MEDIA_ICON_TEXT_GAP + contentW;
            String transportIcon = info.isPlaying() ? "▶" : "||";
            int y2 = pos.y() + fontHeight + vPadding;
            drawRoundedRect(context, pos.x() - hPadding, pos.y() - vPadding, pos.x() + totalW + hPadding, y2, rounding, bgColor);
            context.drawText(client.textRenderer, transportIcon, pos.x(), pos.y(), textColor, false);
            context.drawText(client.textRenderer, line, textX, pos.y(), textColor, false);
            context.drawText(client.textRenderer, "§8" + suffix, textX + client.textRenderer.getWidth(line), pos.y(), textColor, false);
            return;
        }

        int titleW = client.textRenderer.getWidth("§l" + title);
        int artistW = client.textRenderer.getWidth("§7" + artist);
        int timeW = client.textRenderer.getWidth("§7" + time);
        int contentW = Math.max(Math.max(titleW, artistW), timeW);
        int barRowTop = pos.y() + fontHeight + MEDIA_LINE_GAP + fontHeight + MEDIA_BEFORE_BAR;
        int y2 = barRowTop + MEDIA_BAR_HEIGHT + MEDIA_AFTER_BAR + fontHeight + vPadding;

        int totalW = iconW + MEDIA_ICON_TEXT_GAP + contentW;
        drawRoundedRect(context, pos.x() - hPadding, pos.y() - vPadding, pos.x() + totalW + hPadding, y2, rounding, bgColor);

        String transportIcon = info.isPlaying() ? "▶" : "||";
        context.drawText(client.textRenderer, transportIcon, pos.x(), pos.y(), textColor, false);
        context.drawText(client.textRenderer, "§l" + title, textX, pos.y(), textColor, false);
        context.drawText(client.textRenderer, "§7" + artist, textX, pos.y() + fontHeight + MEDIA_LINE_GAP, textColor, false);

        int filledWidth = 0;
        if (contentW > 0 && info.durationMs() > 0) {
            double progress = (double) info.progressMs() / info.durationMs();
            filledWidth = (int) Math.round(contentW * Math.clamp(progress, 0.0, 1.0));
        }

        context.fill(textX, barRowTop, textX + contentW, barRowTop + MEDIA_BAR_HEIGHT, 0x44ffffff);
        context.fill(textX, barRowTop, textX + filledWidth, barRowTop + MEDIA_BAR_HEIGHT, textColor);
        context.drawText(client.textRenderer, "§7" + time, textX, barRowTop + MEDIA_BAR_HEIGHT + MEDIA_AFTER_BAR, textColor, false);
    }

    private static String ellipsizeUnicode(String s, int maxCodePoints) {
        if (s == null || maxCodePoints <= 0) {
            return "";
        }
        if (s.isEmpty()) {
            return s;
        }
        int count = s.codePointCount(0, s.length());
        if (count <= maxCodePoints) {
            return s;
        }
        int keep = Math.max(1, maxCodePoints - 1);
        return s.substring(0, s.offsetByCodePoints(0, keep)) + "…";
    }

    private static int measurePlaceholderContentWidth(MinecraftClient client, String text, iquldev.fpsoverlay.stats.MediaStats media, boolean isMedia) {
        if (!isMedia) {
            return client.textRenderer.getWidth(text);
        }
        int iconW = client.textRenderer.getWidth("▶");
        int gap = MEDIA_ICON_TEXT_GAP;
        int lineMax = Math.max(8, InfoOverlayConfig.mediaMaxLineChars);
        int compactMax = Math.max(12, InfoOverlayConfig.mediaCompactMaxChars);
        iquldev.fpsoverlay.stats.MediaStats.MediaInfo info = media.getInfo();
        if (Util.getOperatingSystem() == Util.OperatingSystem.OSX) {
            int musicIconW = client.textRenderer.getWidth("🎵");
            int w = Math.max(
                    client.textRenderer.getWidth(ellipsizeUnicode(Text.translatable("fpsoverlay.media.unsupported_os").getString(), lineMax)),
                    client.textRenderer.getWidth(ellipsizeUnicode(Text.translatable("fpsoverlay.media.unsupported_os_mac").getString(), lineMax))
            );
            return musicIconW + gap + w;
        }
        if (info.isEmpty()) {
            int musicIconW = client.textRenderer.getWidth("🎵");
            String title = info.title().isEmpty() ? Text.translatable("fpsoverlay.media.widget_name").getString() : info.title();
            String artist = info.artist().isEmpty() ? Text.translatable("fpsoverlay.media.not_playing").getString() : info.artist();
            int w = Math.max(
                    client.textRenderer.getWidth(ellipsizeUnicode(title, lineMax)),
                    client.textRenderer.getWidth(ellipsizeUnicode(artist, lineMax))
            );
            return musicIconW + gap + w;
        }
        if (InfoOverlayConfig.mediaWidgetCompact) {
            String core = info.title().isEmpty() ? info.artist() : (info.artist().isEmpty() ? info.title() : info.title() + " · " + info.artist());
            int prefix = client.textRenderer.getWidth(ellipsizeUnicode(core, compactMax));
            int suffix = client.textRenderer.getWidth("§8" + MEDIA_TIME_COMPACT_SAMPLE);
            return iconW + gap + prefix + suffix;
        }
        int tw = client.textRenderer.getWidth("§l" + ellipsizeUnicode(info.title(), lineMax));
        int aw = client.textRenderer.getWidth("§7" + ellipsizeUnicode(info.artist(), lineMax));
        int timeW = client.textRenderer.getWidth(MEDIA_TIME_WIDTH_SAMPLE);
        return iconW + gap + Math.max(Math.max(tw, aw), timeW);
    }

    private static int measurePlaceholderContentHeight(MinecraftClient client, String text, iquldev.fpsoverlay.stats.MediaStats media, int fontHeight, boolean isMedia) {
        if (!isMedia) {
            return fontHeight;
        }
        iquldev.fpsoverlay.stats.MediaStats.MediaInfo info = media.getInfo();
        if (InfoOverlayConfig.mediaWidgetCompact && !info.isEmpty() && Util.getOperatingSystem() != Util.OperatingSystem.OSX) {
            return fontHeight;
        }
        if (Util.getOperatingSystem() == Util.OperatingSystem.OSX || info.isEmpty()) {
            return 2 * fontHeight + MEDIA_LINE_GAP;
        }
        return 3 * fontHeight + MEDIA_LINE_GAP + MEDIA_BEFORE_BAR + MEDIA_BAR_HEIGHT + MEDIA_AFTER_BAR;
    }

    private static String formatTime(long ms) {
        if (ms < 0) {
            ms = 0;
        }
        long totalSec = ms / 1000;
        long hours = totalSec / 3600;
        long minutes = (totalSec % 3600) / 60;
        long seconds = totalSec % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%d:%02d", minutes, seconds);
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
                                                     int overlayBlockHeight, int advancedBlockHeight,
                                                     int screenWidth, int screenHeight,
                                                     boolean isShowed, int hPadding, int vPadding) {
        int boxGap = getBoxGap();

        if (isShowed) {
            if (InfoOverlayConfig.isVertical) {
                int x = switch (position) {
                    case TOP_RIGHT, BOTTOM_RIGHT -> overlayPos.x() + overlayTextWidth - advancedTextWidth;
                    default -> overlayPos.x();
                };
                int y = switch (position) {
                    case BOTTOM_LEFT, BOTTOM_RIGHT -> overlayPos.y() - advancedBlockHeight - vPadding * 2 - boxGap;
                    default -> overlayPos.y() + overlayBlockHeight + vPadding * 2 + boxGap;
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
                                                screenHeight - 10 - advancedBlockHeight - vPadding);
                case BOTTOM_LEFT -> new Position(15, screenHeight - 10 - advancedBlockHeight - vPadding);
                default -> new Position(15, 10);
            };
        }
    }

    private static record Position(int x, int y) {}
}
