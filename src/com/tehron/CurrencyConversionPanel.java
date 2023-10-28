package com.tehron;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

public class CurrencyConversionPanel extends JPanel {
    private final JTextField fromCurrencyField;
    private final JTextField toCurrencyField;
    private final JTextField amountField;
    private final JLabel resultLabel;

    public CurrencyConversionPanel() {
        setLayout(new GridLayout(5, 2));

        JLabel titleLabel = new JLabel("Конвертация валюты");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        fromCurrencyField = new JTextField();
        fromCurrencyField.addKeyListener(new CurrencyFieldListener());

        toCurrencyField = new JTextField();
        toCurrencyField.addKeyListener(new CurrencyFieldListener());

        amountField = new JTextField();
        amountField.addKeyListener(new CurrencyFieldListener());

        resultLabel = new JLabel("Результат будет здесь");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        add(new JLabel("Из валюты"));
        add(new JLabel("В валюту"));
        add(fromCurrencyField);
        add(toCurrencyField);
        add(new JLabel("Количество"));
        add(new JLabel(""));
        add(amountField);
        add(new JLabel(""));
        add(new JLabel("Результат"));
        add(resultLabel);
    }

    private class CurrencyFieldListener implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            updateConversionResult();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            /*if(((JTextField)e.getComponent()).getText().equals(""))
                ((JTextField)e.getComponent()).setText("0");*/

            updateConversionResult();
        }

        @Override
        public void keyReleased(KeyEvent e) {
            updateConversionResult();
        }
    }

    private void updateConversionResult() {
        try {
            double fromValue = Double.parseDouble(fromCurrencyField.getText());
            double toValue = Double.parseDouble(toCurrencyField.getText());
            double amount = Double.parseDouble(amountField.getText());

            double result = (amount / fromValue) * toValue;
            resultLabel.setText(new DecimalFormat("0.000").format(result));
        } catch (NumberFormatException ex) {
            resultLabel.setText("Введите корректные значения.");
        }
    }

}
