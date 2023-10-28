package com.tehron;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.Objects;

public class SystemPanel {
    private final JPanel systemPanel;
    private final JButton[] buttonsArray;

    private final JTextField fromSystemTextField;
    private final JComboBox<String> fromSystemComboBox;

    private final JTextField toSystemTextField;
    private final JComboBox<String> toSystemComboBox;

    public SystemPanel() {
        String[] systems = new String[15];
        for (int i = 2; i <= 16; i++)
            systems[i - 2] = Integer.valueOf(i).toString();

        buttonsArray = new JButton[16];

        systemPanel = new JPanel(new BorderLayout());

        fromSystemTextField = new JTextField();
        fromSystemComboBox = new JComboBox<>(systems);
        fromSystemComboBox.addActionListener(new FromComboBoxListener());

        toSystemTextField = new JTextField();
        toSystemComboBox = new JComboBox<>(systems);

        createSystemPanel();
    }

    public JPanel getSystemPanel() {
        return systemPanel;
    }

    private void createSystemPanel() {
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.LINE_AXIS));

        fromSystemTextField.setPreferredSize(new Dimension(
                fromSystemTextField.getPreferredSize().width,
                fromSystemTextField.getPreferredSize().height + 20
        ));
        fromSystemTextField.setHorizontalAlignment(JTextField.RIGHT);
        fromSystemTextField.setEditable(false);
        fromSystemTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toSystemTextField.setText(convertFromDec(convertToDec()));
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toSystemTextField.setText(convertFromDec(convertToDec()));
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        fromSystemTextField.setText("0");
        fromSystemComboBox.addActionListener(e -> fromSystemTextField.setText(convertFromDec(convertToDec())));

        toSystemTextField.setPreferredSize(new Dimension(
                toSystemTextField.getPreferredSize().width,
                toSystemTextField.getPreferredSize().height + 20
        ));
        toSystemTextField.setHorizontalAlignment(JTextField.RIGHT);
        toSystemTextField.setEditable(false);
        toSystemComboBox.addActionListener(e -> toSystemTextField.setText(convertFromDec(convertToDec())));

        fieldsPanel.add(fromSystemTextField);
        fieldsPanel.add(fromSystemComboBox);
        fieldsPanel.add(toSystemTextField);
        fieldsPanel.add(toSystemComboBox);

        systemPanel.add(fieldsPanel, BorderLayout.NORTH);

        createNumbersPanel();
    }

    private void createNumbersPanel() {
        JPanel numbersPanel = new JPanel(new GridLayout(7, 3));

        JButton button = new JButton();
        button.setEnabled(false);
        numbersPanel.add(button);

        JButton clear = new JButton("C");
        clear.addActionListener(e -> fromSystemTextField.setText("0"));
        numbersPanel.add(clear);

        JButton x = new JButton("X");
        x.addActionListener(e -> fromSystemTextField.setText(fromSystemTextField.getText().substring(0, fromSystemTextField.getText().length() - 1)));
        numbersPanel.add(x);

        int i = 0;
        for (; i < 10; i++) {
            buttonsArray[i] = new JButton(String.valueOf(i));
            buttonsArray[i].addActionListener(new NumberButtonListener());
            numbersPanel.add(buttonsArray[i]);
        }

        for (char j = 'A'; j <= 'F'; j++, i++) {
            buttonsArray[i] = new JButton(String.valueOf(j));
            buttonsArray[i].addActionListener(new NumberButtonListener());
            numbersPanel.add(buttonsArray[i]);
        }

        fromSystemComboBox.setSelectedIndex(0);
        systemPanel.add(numbersPanel);
    }

    // Определение слушателей для кнопок
    private class NumberButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String digit = ((JButton) e.getSource()).getText();
            if (digit.equals("Error") || (!digit.equals(".") && fromSystemTextField.getText().equals("0")))
                fromSystemTextField.setText(digit);
            else
                fromSystemTextField.setText(fromSystemTextField.getText() + digit);
        }
    }

    private class FromComboBoxListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int until = Integer.parseInt((String) Objects.requireNonNull(fromSystemComboBox.getSelectedItem()));

            for (int i = 0; i < 16; i++)
                buttonsArray[i].setEnabled(i < until);
        }
    }

    private String convertToDec() {
        BigInteger fromSystem = new BigInteger(String.valueOf(fromSystemComboBox.getSelectedIndex() + 2));
        String fromNumber = fromSystemTextField.getText();
        BigInteger result = new BigInteger("0");
        BigInteger curPow = new BigInteger("1");

        for (int i = fromNumber.length() - 1; i >= 0; --i) {
            int curDigit = fromNumber.charAt(i);
            if (fromNumber.charAt(i) < 48 || fromNumber.charAt(i) > 57)
                curDigit = (fromNumber.charAt(i) - 'A') + 10;
            else
                curDigit -= 48;

            result = result.add(new BigInteger(String.valueOf(curDigit)).multiply(curPow));
            curPow = curPow.multiply(fromSystem);
        }

        return result.toString();
    }

    private String convertFromDec(String dec) {
        BigInteger toSystem = new BigInteger((String) Objects.requireNonNull(toSystemComboBox.getSelectedItem()));
        BigInteger fromNumber = new BigInteger(dec);
        StringBuilder result = new StringBuilder();

        if (fromNumber.compareTo(BigInteger.ZERO) == 0)
            result.append("0");
        else
            while (fromNumber.compareTo(BigInteger.ZERO) != 0) {
                BigInteger remainder = fromNumber.remainder(toSystem);
                switch (remainder.intValue()) {
                    case 10 -> result.append("A");
                    case 11 -> result.append("B");
                    case 12 -> result.append("C");
                    case 13 -> result.append("D");
                    case 14 -> result.append("E");
                    case 15 -> result.append("F");
                    default -> result.append(remainder);
                }

                fromNumber = fromNumber.divide(toSystem);
            }

        return result.reverse().toString();
    }
}
