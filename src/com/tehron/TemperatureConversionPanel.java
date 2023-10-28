package com.tehron;

import javax.swing.*;
import java.awt.*;

public class TemperatureConversionPanel extends JPanel {
    private final JTextField inputField;
    private final JLabel resultLabel;
    private final JComboBox<String> fromUnitComboBox;
    private final JComboBox<String> toUnitComboBox;

    public TemperatureConversionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel titleLabel = new JLabel("Конвертация температуры");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        inputField = new JTextField();
        JButton convertButton = new JButton("Конвертировать");
        convertButton.addActionListener(e -> convertTemperature());

        resultLabel = new JLabel("Результат: - ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String[] temperatureUnits = {"Градус Цельсия (°C)", "Градус Фаренгейта (°F)", "Кельвин (K)"};
        fromUnitComboBox = new JComboBox<>(temperatureUnits);
        toUnitComboBox = new JComboBox<>(temperatureUnits);

        JPanel p1 = new JPanel();
        p1.setLayout(new BoxLayout(p1, BoxLayout.LINE_AXIS));
        p1.add(fromUnitComboBox);
        p1.add(new JLabel(" в "));
        p1.add(toUnitComboBox);
        add(p1);

        add(new JSeparator(JSeparator.HORIZONTAL));

        JPanel p2 = new JPanel();
        p2.setLayout(new BoxLayout(p2, BoxLayout.LINE_AXIS));
        p2.add(titleLabel);
        p2.add(inputField);
        add(p2);

        add(new JSeparator(JSeparator.HORIZONTAL));

        JPanel p3 = new JPanel(new BorderLayout());
        p3.add(convertButton, BorderLayout.CENTER);
        add(p3);

        JPanel p4 = new JPanel(new BorderLayout());
        p4.add(resultLabel);
        add(p4);
    }

    private void convertTemperature() {
        try {
            double inputValue = Double.parseDouble(inputField.getText());
            double result = convertBetweenUnits(inputValue, fromUnitComboBox.getSelectedIndex(), toUnitComboBox.getSelectedIndex());
            resultLabel.setText(inputValue + " " + fromUnitComboBox.getSelectedItem() + " = " + result + " " + toUnitComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            resultLabel.setText("Пожалуйста, введите числовое значение.");
        }
    }

    private double convertBetweenUnits(double value, int fromUnitIndex, int toUnitIndex) {
        double result = 0;

        if (fromUnitIndex == 0 && toUnitIndex == 1) {
            result = (value * 9/5) + 32; // Celsius to Fahrenheit
        } else if (fromUnitIndex == 0 && toUnitIndex == 2) {
            result = value + 273.15; // Celsius to Kelvin
        } else if (fromUnitIndex == 1 && toUnitIndex == 0) {
            result = (value - 32) * 5/9; // Fahrenheit to Celsius
        } else if (fromUnitIndex == 1 && toUnitIndex == 2) {
            result = (value - 32) * 5/9 + 273.15; // Fahrenheit to Kelvin
        } else if (fromUnitIndex == 2 && toUnitIndex == 0) {
            result = value - 273.15; // Kelvin to Celsius
        } else if (fromUnitIndex == 2 && toUnitIndex == 1) {
            result = (value - 273.15) * 9/5 + 32; // Kelvin to Fahrenheit
        }

        return result;
    }
}

