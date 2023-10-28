import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends JDialog {
    private final JFrame frame;
    private final JToggleButton boldToggleButton;
    private final JToggleButton italicToggleButton;
    private final JComboBox<String> fontSizeComboBox;
    private final JTextPane textArea;
    private final JLabel statusBar;
    private final JComboBox<String> fontComboBox;
    private final JButton fontColorButton;

    private final JComboBox<String> fioStyleComboBox;

    private Font currentFont;

    public TextEditor() {
        String[] fontList = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();

        String[] fontSizeList = new String[46];
        for (int i = 8; i < 100; i += 2)
            fontSizeList[i / 2 - 4] = "" + i;

        String[] fioStyleList = {
                "-",
                "Иванов И.И.",
                "Иванов Иван Иванович",
                "Иван Иванович ИВАНОВ",
                "ИВАНОВ И.И.",
                "ИВАНОВ ИВАН ИВАНОВИЧ"
        };

        frame = new JFrame("Тестовый редактор");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        currentFont = new Font(fontList[0], Font.PLAIN, Integer.parseInt(fontSizeList[2]));

        JMenuBar menuBar = new JMenuBar();
        textArea = new JTextPane();
        JMenu fileMenu = new JMenu("Меню");
        JMenuItem newFileMenuItem = new JMenuItem("Создать");
        JMenuItem openFileMenuItem = new JMenuItem("Открыть");
        JMenuItem saveFileMenuItem = new JMenuItem("Сохранить");

        JToolBar toolBar = new JToolBar();
        fontComboBox = new JComboBox<>(fontList);
        boldToggleButton = new JToggleButton("Ж");
        italicToggleButton = new JToggleButton("I");
        fontSizeComboBox = new JComboBox<>(fontSizeList);
        fontSizeComboBox.setSelectedIndex(3);
        fontColorButton = new JButton("   ");
        fontColorButton.setBackground(textArea.getForeground());
        fontColorButton.setForeground(textArea.getForeground());
        fioStyleComboBox = new JComboBox<>(fioStyleList);
        fioStyleComboBox.setSelectedIndex(0);

        applyFont();
        newFileMenuItem.addActionListener(e -> textArea.setText(""));

        openFileMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    textArea.setText(content.toString());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        saveFileMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.print(textArea.getText());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        fileMenu.add(newFileMenuItem);
        fileMenu.add(openFileMenuItem);
        fileMenu.add(saveFileMenuItem);
        menuBar.add(fileMenu);

        fontComboBox.addActionListener(e -> {
            currentFont = new Font(
                    (String) fontComboBox.getSelectedItem(),
                    currentFont.getStyle(),
                    currentFont.getSize()
            );
            applyFontToSelectedText(
                    currentFont,
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd()
            );
            applyFont();
        });
        fontSizeComboBox.addActionListener(e -> {
            if (fontSizeComboBox.getSelectedItem() != null) {
                currentFont = new Font(
                        (String) fontComboBox.getSelectedItem(),
                        currentFont.getStyle(),
                        Integer.parseInt((String) fontSizeComboBox.getSelectedItem())
                );
                applyFontToSelectedText(
                        currentFont,
                        textArea.getSelectionStart(),
                        textArea.getSelectionEnd()
                );
                applyFont();
            } else
                fontSizeComboBox.setSelectedIndex(2);
        });
        boldToggleButton.addActionListener(e -> {
            applyFontToSelectedText(
                    currentFont,
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd()
            );
            applyFont();
        });
        italicToggleButton.addActionListener(e -> {
            applyFontToSelectedText(
                    currentFont,
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd()
            );
            applyFont();
        });
        fontColorButton.addActionListener(e -> {
            Color choseColor = JColorChooser.showDialog(frame, "Choosing text color:", fontColorButton.getBackground());
            if (choseColor != null)
                fontColorButton.setBackground(choseColor);
            applyFontToSelectedText(
                    currentFont,
                    textArea.getSelectionStart(),
                    textArea.getSelectionEnd()
            );
            applyFont();
        });
        fioStyleComboBox.addActionListener(e -> changeFIO());

        toolBar.add(fontComboBox);
        toolBar.addSeparator(new Dimension(15, 2));
        toolBar.add(boldToggleButton);
        toolBar.addSeparator(new Dimension(15, 2));
        toolBar.add(italicToggleButton);
        toolBar.addSeparator(new Dimension(15, 2));
        toolBar.add(fontSizeComboBox);
        toolBar.addSeparator(new Dimension(15, 2));
        toolBar.add(fontColorButton);
        toolBar.addSeparator(new Dimension(15, 2));
        toolBar.add(fioStyleComboBox);

        textArea.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popUp = new JPopupMenu();

                    JMenuItem fieldColor = new JMenuItem("Цвет");
                    fieldColor.addActionListener(event ->
                            textArea.setBackground(
                                    JColorChooser.showDialog(
                                            frame,
                                            "Choosing color:",
                                            textArea.getBackground()
                                    )
                            )
                    );

                    popUp.add(fieldColor);
                    popUp.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        textArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> {
                    updateStatus();
                    applyFont();
                    changeFIO();
                });
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateStatus();
            }

            @Override
            public void changedUpdate(DocumentEvent e) { }
        });

        statusBar = new JLabel("Characters: 0, Digits: 0, Letters: 0, Special: 0, Word: 0, Sentences: 0");
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        frame.setJMenuBar(menuBar);
        frame.add(toolBar, BorderLayout.NORTH);
        frame.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void changeFIO() {
        if(fioStyleComboBox.getSelectedIndex() == 0)
            return;

        String regex = "([А-ЯЁ]+[а-яё]*[оеОЕ][вВ](?:[аоАО])?)\\s([А-ЯЁ]+[а-яё]*)\\s([А-ЯЁ]+[а-яё]*(?:[(вич)(ВИЧ)]))";
        Matcher matcher = Pattern.compile(regex)
                .matcher(textArea.getText());

        String resText = textArea.getText();
        while(matcher.find()) {
            String fio = matcher.group();
            String[] fioArr = fio.split(" ");
            switch (fioStyleComboBox.getSelectedIndex()) {
                case 1 -> resText = resText.replaceAll(fio, f(fioArr[0]) + " " + fioArr[1].charAt(0) + "." + fioArr[2].charAt(0) + ".");
                case 2 -> resText = resText.replaceAll(fio, f(fioArr[0]) + " " + f(fioArr[1]) + " " + f(fioArr[2]));
                case 3 -> resText = resText.replaceAll(fio, f(fioArr[1]) + " " + f(fioArr[2]) + " " + fioArr[0].toUpperCase());
                case 4 -> resText = resText.replaceAll(fio, fioArr[0].toUpperCase() + " " + fioArr[1].charAt(0) + "." + fioArr[2].charAt(0) + ".");
                case 5 -> resText = resText.replaceAll(fio, fio.toUpperCase());
            }
        }

        /**===========================================3==================================================**/
        regex = "([А-ЯЁ]+[а-яё]*)\\s([А-ЯЁ]+[а-яё]*(?:[(вич)(ВИЧ)])\\s([А-ЯЁ]+[а-яё]*[оеОЕ][вВ](?:[аоАО])?))";

        matcher = Pattern.compile(regex)
                .matcher(textArea.getText());

        while(matcher.find()) {
            String fio = matcher.group();
            String[] fioArr = fio.split(" ");
            switch (fioStyleComboBox.getSelectedIndex()) {
                case 1 -> resText = resText.replaceAll(fio, f(fioArr[2]) + " " + fioArr[0].charAt(0) + "." + fioArr[1].charAt(0) + ".");
                case 2 -> resText = resText.replaceAll(fio, f(fioArr[2]) + " " + f(fioArr[0]) + " " + f(fioArr[1]));
                case 3 -> resText = resText.replaceAll(fio, f(fioArr[0]) + " " + f(fioArr[1]) + " " + fioArr[2].toUpperCase());
                case 4 -> resText = resText.replaceAll(fio, fioArr[2].toUpperCase() + " " + fioArr[0].charAt(0) + "." + fioArr[1].charAt(0) + ".");
                case 5 -> resText = resText.replaceAll(fio, fioArr[2].toUpperCase() + " " + fioArr[0].toUpperCase() + " " + fioArr[1].toUpperCase());
            }
        }

        /**===========================================1&4==================================================**/
        if(fioStyleComboBox.getSelectedIndex() == 1 || fioStyleComboBox.getSelectedIndex() == 4) {
            regex = "([А-ЯЁ]+[а-яё]*[оеОЕ][вВ](?:[аоАО])?)\\s([А-ЯЁ]\\.)([А-ЯЁ]\\.)";

            matcher = Pattern.compile(regex)
                    .matcher(textArea.getText());

            while(matcher.find()) {
                String fio = matcher.group();
                String[] fioArr = fio.split(" ");
                switch (fioStyleComboBox.getSelectedIndex()) {
                    case 1 -> resText = resText.replaceAll(fio, f(fioArr[0]) + " " + fioArr[1].toUpperCase());
                    case 4 -> resText = resText.replaceAll(fio, fioArr[0].toUpperCase() + " " + fioArr[1].toUpperCase());
                }
            }
        }

        if(!textArea.getText().equals(resText))
            textArea.setText(resText);
    }

    private String f(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase();
    }

    private void applyFontToSelectedText(Font font, int start, int end) {
        if (start != end) {
            SimpleAttributeSet attributes = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attributes, font.getFamily());
            StyleConstants.setFontSize(attributes, font.getSize());
            StyleConstants.setBold(attributes, boldToggleButton.isSelected());
            StyleConstants.setItalic(attributes, italicToggleButton.isSelected());
            StyleConstants.setForeground(attributes, fontColorButton.getBackground());

            textArea
                    .getStyledDocument()
                    .setCharacterAttributes(
                            start,
                            end - start,
                            attributes,
                            false
                    );
        }
    }

    private void applyFont() {
        MutableAttributeSet attrs = new SimpleAttributeSet();
        StyleConstants.setFontFamily(attrs, currentFont.getFamily());
        StyleConstants.setFontSize(attrs, currentFont.getSize());
        StyleConstants.setBold(attrs, boldToggleButton.isSelected());
        StyleConstants.setItalic(attrs, italicToggleButton.isSelected());
        StyleConstants.setForeground(attrs, fontColorButton.getBackground());

        textArea.setCharacterAttributes(
                attrs,
                false
        );
    }

    private void updateStatus() {
        String text = textArea.getText();
        int charCount = text.length();
        int digitCount = text.replaceAll("[^0-9]", "").length();
        int letterCount = text.replaceAll("[^a-zA-Z]", "").length()
                + text.replaceAll("[^а-яА-Я]", "").length();
        int sentCount = text.split("[.!?]+").length;
        int wordCount = text.split("[\\s.,;!?]+").length;
        int specialCount = charCount - digitCount - letterCount;

        statusBar.setText("Characters: " + charCount + ", Digits: " + digitCount + ", Letters: " + letterCount
                + ", Special: " + specialCount + ", Word: " + wordCount + ", Sentences: " + sentCount);
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}
