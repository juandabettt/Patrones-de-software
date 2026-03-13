package taller6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

/**
 * Floating card with glassmorphism style: rounded corners, state icon, title, description,
 * time ago, close button, and "Ver detalles" action. Dark mode, soft shadows.
 */
public class NotificationCardPanel extends JPanel {

    public enum State { SUCCESS, ERROR, INFO }

    private static final int PAD = 16;
    private static final int GAP = 10;

    private final State state;
    private final String title;
    private final String description;
    private final Instant createdAt;
    private final String detailPayload;

    private final JLabel timeLabel;
    private final JButton closeButton;
    private final JButton detailsButton;
    private boolean hover;

    public NotificationCardPanel(State state, String title, String description, String detailPayload) {
        this.state = state;
        this.title = title;
        this.description = description;
        this.detailPayload = detailPayload != null ? detailPayload : "";
        this.createdAt = Instant.now();

        setOpaque(false);
        setLayout(new BorderLayout(PAD, PAD));
        setBorder(BorderFactory.createEmptyBorder(PAD, PAD, PAD, PAD));
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        timeLabel = new JLabel(formatTimeAgo(createdAt));
        timeLabel.setFont(NotificationTheme.FONT_CAPTION);
        timeLabel.setForeground(NotificationTheme.TEXT_MUTED);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        closeButton = createIconButton("×", 24);
        closeButton.setToolTipText("Cerrar");
        closeButton.addActionListener(e -> removeCard());

        detailsButton = new JButton("Ver detalles");
        detailsButton.setFont(NotificationTheme.FONT_BUTTON);
        detailsButton.setFocusPainted(false);
        detailsButton.setBorderPainted(false);
        detailsButton.setContentAreaFilled(false);
        detailsButton.setForeground(NotificationTheme.ACCENT_INFO);
        detailsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        detailsButton.addActionListener(e -> showDetails());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(timeLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        center.setOpaque(false);
        center.add(createLeftIcon());
        center.add(Box.createHorizontalStrut(GAP));
        center.add(createTextBlock());
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.setOpaque(false);
        bottom.add(detailsButton);
        bottom.add(closeButton);
        add(bottom, BorderLayout.SOUTH);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { hover = false; repaint(); }
        });

        startTimeAgoUpdater();
    }

    private JButton createIconButton(String text, int size) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.PLAIN, size));
        b.setForeground(NotificationTheme.TEXT_SECONDARY);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setPreferredSize(new Dimension(28, 28));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { b.setForeground(NotificationTheme.TEXT_PRIMARY); }
            @Override
            public void mouseExited(MouseEvent e) { b.setForeground(NotificationTheme.TEXT_SECONDARY); }
        });
        return b;
    }

    private JPanel createLeftIcon() {
        JPanel iconWrap = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(stateColor());
                g2.fillOval(0, 0, NotificationTheme.ICON_SIZE, NotificationTheme.ICON_SIZE);
                g2.dispose();
            }
        };
        iconWrap.setOpaque(false);
        iconWrap.setPreferredSize(new Dimension(NotificationTheme.ICON_SIZE, NotificationTheme.ICON_SIZE));
        return iconWrap;
    }

    private Color stateColor() {
        switch (state) {
            case SUCCESS: return NotificationTheme.ACCENT_SUCCESS;
            case ERROR:   return NotificationTheme.ACCENT_ERROR;
            case INFO:    return NotificationTheme.ACCENT_INFO;
            default:      return NotificationTheme.ACCENT_INFO;
        }
    }

    private JPanel createTextBlock() {
        JPanel p = new JPanel(new BorderLayout(4, 0));
        p.setOpaque(false);
        JLabel titleL = new JLabel(title);
        titleL.setFont(NotificationTheme.FONT_TITLE);
        titleL.setForeground(NotificationTheme.TEXT_PRIMARY);
        String descText = description.length() > 120 ? description.substring(0, 117) + "..." : description;
        JLabel descL = new JLabel("<html><body style='width:340px;font-weight:300'>" + htmlEscape(descText) + "</body></html>");
        descL.setFont(NotificationTheme.FONT_BODY);
        descL.setForeground(NotificationTheme.TEXT_SECONDARY);
        p.add(titleL, BorderLayout.NORTH);
        p.add(descL, BorderLayout.CENTER);
        return p;
    }

    private String formatTimeAgo(Instant then) {
        long sec = ChronoUnit.SECONDS.between(then, Instant.now());
        if (sec < 60) return "Ahora";
        long min = TimeUnit.SECONDS.toMinutes(sec);
        if (min < 60) return "hace " + min + " min";
        long h = TimeUnit.MINUTES.toHours(min);
        if (h < 24) return "hace " + h + " h";
        return "hace " + TimeUnit.HOURS.toDays(h) + " d";
    }

    private void startTimeAgoUpdater() {
        Timer t = new Timer(30_000, e -> {
            if (timeLabel != null) timeLabel.setText(formatTimeAgo(createdAt));
        });
        t.setRepeats(true);
        t.start();
    }

    private void removeCard() {
        Container p = getParent();
        if (p != null) {
            p.remove(NotificationCardPanel.this);
            p.revalidate();
            p.repaint();
        }
    }

    private void showDetails() {
        JOptionPane.showMessageDialog(getTopLevelAncestor(),
                detailPayload.isEmpty() ? description : detailPayload,
                title,
                JOptionPane.INFORMATION_MESSAGE);
    }

    private static String htmlEscape(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br/>");
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int w = getWidth();
        int h = getHeight();
        int r = NotificationTheme.CARD_RADIUS;
        int s = NotificationTheme.SHADOW_OFFSET;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fill(new RoundRectangle2D.Float(s, s, w - s, h - s, r + 4, r + 4));

        // Card background (glassmorphism-style semi-transparent)
        g2.setColor(NotificationTheme.CARD_BG);
        g2.fill(new RoundRectangle2D.Float(0, 0, w - s, h - s, r, r));

        // Subtle border
        g2.setColor(hover ? NotificationTheme.CARD_BORDER : new Color(60, 62, 66, 80));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, w - s - 1, h - s - 1, r, r));

        g2.dispose();
        super.paintComponent(g);
    }
}
