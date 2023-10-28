package com.tehron;
import javax.swing.*;
import java.awt.*;

public class DistanceConversionPanel extends JPanel {
    private final JTextField inputField;
    private final JLabel resultLabel;
    private final JComboBox<String> fromUnitComboBox;
    private final JComboBox<String> toUnitComboBox;

    public DistanceConversionPanel() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JLabel titleLabel = new JLabel("Конвертация расстояния");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        inputField = new JTextField();
        JButton convertButton = new JButton("Конвертировать");
        convertButton.addActionListener(e -> convertDistance());

        resultLabel = new JLabel("Результат: - ");
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);

        String[] distanceUnits = {"километры (км)", "метры (м)", "сантиметры (см)", "мили (миля)", "ярды (ярд)"};
        fromUnitComboBox = new JComboBox<>(distanceUnits);
        toUnitComboBox = new JComboBox<>(distanceUnits);

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

    private void convertDistance() {
        try {
            double inputValue = Double.parseDouble(inputField.getText());
            double result = convertBetweenUnits(inputValue, fromUnitComboBox.getSelectedIndex(), toUnitComboBox.getSelectedIndex());
            resultLabel.setText(inputValue + " " + fromUnitComboBox.getSelectedItem() + " = " + result + " " + toUnitComboBox.getSelectedItem());
        } catch (NumberFormatException ex) {
            resultLabel.setText("Пожалуйста, введите числовое значение.");
        }
    }

    private double convertBetweenUnits(double value, int fromUnitIndex, int toUnitIndex) {
        double[] conversionFactors = {1, 1000, 100000, 0.621371, 1094};
        return value * conversionFactors[toUnitIndex] / conversionFactors[fromUnitIndex];
    }
}