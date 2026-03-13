package taller6;

import java.awt.*;

/**
 * Dark mode theme for the notification dashboard (4K-ready, engineering style).
 */
public final class NotificationTheme {

    public static final Color BACKGROUND = new Color(0x1E1E1E);
    public static final Color CARD_BG = new Color(40, 42, 46, 220);
    public static final Color CARD_BORDER = new Color(80, 82, 86, 120);
    public static final Color TEXT_PRIMARY = new Color(0xF0F0F0);
    public static final Color TEXT_SECONDARY = new Color(0x9E9E9E);
    public static final Color TEXT_MUTED = new Color(0x6E6E6E);

    /** State accent colors (soft neon) */
    public static final Color ACCENT_SUCCESS = new Color(0x4ADE80);
    public static final Color ACCENT_ERROR = new Color(0xF87171);
    public static final Color ACCENT_INFO = new Color(0x60A5FA);

    public static final int CARD_RADIUS = 12;
    public static final int ICON_SIZE = 40;
    public static final int SHADOW_OFFSET = 4;
    public static final int SHADOW_BLUR = 12;

    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_BUTTON = new Font("Segoe UI", Font.PLAIN, 12);

    private NotificationTheme() {
    }
}
