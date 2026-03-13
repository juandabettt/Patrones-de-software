package taller6;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dark mode dashboard UI for the notification system (Bridge + Adapter).
 * Modern, minimal, 4K-ready with floating glassmorphism cards.
 */
public class NotificationUI extends JFrame {

    private final JComboBox<String> typeCombo;
    private final JComboBox<String> channelCombo;
    private final JTextField recipientField;
    private final JTextArea messageArea;
    private final JPanel cardsContainer;

    private final NotificationChannel emailChannel;
    private final NotificationChannel smsChannel;
    private final NotificationChannel pushChannel;

    public NotificationUI() {
        setTitle("Notification System — Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 720);
        setLocationRelativeTo(null);
        getContentPane().setBackground(NotificationTheme.BACKGROUND);

        emailChannel = new EmailChannel();
        smsChannel = new SmsChannel();
        pushChannel = new PushChannelAdapter(new ExternalPushService(), "App Notification");

        typeCombo = new JComboBox<>(new String[]{"Urgent", "Informational"});
        channelCombo = new JComboBox<>(new String[]{"Email", "SMS", "Push"});
        recipientField = new JTextField(28);
        messageArea = new JTextArea(3, 28);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new BoxLayout(cardsContainer, BoxLayout.Y_AXIS));
        cardsContainer.setBackground(NotificationTheme.BACKGROUND);
        cardsContainer.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        styleCombo(typeCombo);
        styleCombo(channelCombo);
        styleField(recipientField);
        styleArea(messageArea);

        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(NotificationTheme.BACKGROUND);
        main.setBorder(new EmptyBorder(24, 24, 24, 24));

        main.add(buildFormPanel(), BorderLayout.NORTH);
        main.add(buildCardsPanel(), BorderLayout.CENTER);
        main.add(buildButtonPanel(), BorderLayout.SOUTH);

        setContentPane(main);
        addWelcomeCard();
    }

    /** Light background for combo so selected text has clear contrast (black on light). */
    private static final Color COMBO_BG = new Color(0xF5F5F5);

    private void styleCombo(JComboBox<String> combo) {
        combo.setBackground(COMBO_BG);
        combo.setForeground(Color.BLACK);
        combo.setFont(NotificationTheme.FONT_BODY);
        combo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x404040), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        // Editor: the box that shows the selected value when combo is closed
        Component editorComp = combo.getEditor().getEditorComponent();
        if (editorComp instanceof JTextField) {
            JTextField editorField = (JTextField) editorComp;
            editorField.setForeground(Color.BLACK);
            editorField.setBackground(COMBO_BG);
            editorField.setCaretColor(Color.BLACK);
            editorField.setFont(NotificationTheme.FONT_BODY);
        }
        // Dropdown list: black text, light background; selected row with a bit more contrast
        combo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel l = new JLabel(value != null ? value.toString() : "");
            l.setFont(NotificationTheme.FONT_BODY);
            l.setForeground(Color.BLACK);
            l.setBackground(isSelected ? new Color(0xD0D0D0) : Color.WHITE);
            l.setOpaque(true);
            l.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return l;
        });
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(0x2D2D2D));
        field.setForeground(Color.BLACK);
        field.setCaretColor(Color.BLACK);
        field.setFont(NotificationTheme.FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x404040), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private void styleArea(JTextArea area) {
        area.setBackground(new Color(0x2D2D2D));
        area.setForeground(Color.BLACK);
        area.setCaretColor(Color.BLACK);
        area.setFont(NotificationTheme.FONT_BODY);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0x404040), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(NotificationTheme.BACKGROUND);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0x333333)),
                new EmptyBorder(0, 0, 16, 0)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(label("Type"), gbc);
        gbc.gridx = 1;
        form.add(typeCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(label("Channel"), gbc);
        gbc.gridx = 1;
        form.add(channelCombo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(label("Recipient"), gbc);
        gbc.gridx = 1;
        form.add(recipientField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(label("Message"), gbc);
        gbc.gridx = 1;
        form.add(new JScrollPane(messageArea), gbc);
        return form;
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(NotificationTheme.FONT_BODY);
        l.setForeground(NotificationTheme.TEXT_SECONDARY);
        return l;
    }

    private JScrollPane buildCardsPanel() {
        JScrollPane scroll = new JScrollPane(cardsContainer);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(NotificationTheme.BACKGROUND);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel buildButtonPanel() {
        JButton sendBtn = new JButton("Send notification");
        sendBtn.setFont(NotificationTheme.FONT_BUTTON);
        sendBtn.setBackground(NotificationTheme.ACCENT_INFO);
        sendBtn.setForeground(Color.BLACK);
        sendBtn.setFocusPainted(false);
        sendBtn.setBorderPainted(false);
        sendBtn.setContentAreaFilled(true);
        sendBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendBtn.addActionListener(e -> sendNotification());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(NotificationTheme.BACKGROUND);
        btnPanel.add(sendBtn);
        return btnPanel;
    }

    private void addWelcomeCard() {
        NotificationCardPanel card = new NotificationCardPanel(
                NotificationCardPanel.State.INFO,
                "Notification dashboard",
                "Send a notification using the form above. Urgent (red), Informational (blue).",
                "Select type, channel, recipient and message, then click Send notification.");
        card.setPreferredSize(new Dimension(520, 120));
        card.setMaximumSize(new Dimension(520, 140));
        addCard(card);
    }

    private void addCard(NotificationCardPanel card) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
        wrap.setBackground(NotificationTheme.BACKGROUND);
        wrap.add(card);
        cardsContainer.add(wrap);
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }

    private void sendNotification() {
        String recipient = recipientField.getText().trim();
        String message = messageArea.getText().trim();
        if (recipient.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a recipient.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (message.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a message.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String typeName = (String) typeCombo.getSelectedItem();
        String channelName = (String) channelCombo.getSelectedItem();
        NotificationChannel channel = getChannel(channelName);
        Notification notification = createNotification(typeName, channel);

        notification.send(recipient, message);

        NotificationCardPanel.State state = "Urgent".equals(typeName)
                ? NotificationCardPanel.State.ERROR
                : NotificationCardPanel.State.INFO;
        String title = typeName + " — " + channelName;
        String description = message.length() > 80 ? message.substring(0, 77) + "..." : message;
        String detail = "Recipient: " + recipient + "\nChannel: " + channelName + "\n\n" + message;

        NotificationCardPanel card = new NotificationCardPanel(state, title, description, detail);
        card.setPreferredSize(new Dimension(520, 140));
        card.setMaximumSize(new Dimension(520, 160));
        addCard(card);
    }

    private NotificationChannel getChannel(String name) {
        if ("Email".equals(name)) return emailChannel;
        if ("SMS".equals(name)) return smsChannel;
        return pushChannel;
    }

    private Notification createNotification(String typeName, NotificationChannel channel) {
        if ("Urgent".equals(typeName)) return new UrgentNotification(channel);
        return new InformationalNotification(channel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        EventQueue.invokeLater(() -> {
            NotificationUI ui = new NotificationUI();
            ui.setVisible(true);
        });
    }
}
