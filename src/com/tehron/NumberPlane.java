package com.tehron;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.MathContext;

public class NumberPlane {
    private final JPanel numberPanel;
    private final JTextField resultField;
    private final MathContext mathContext;
    private BigDecimal currentNumber;
    private String currentOperator;

    public NumberPlane() {
        numberPanel = new JPanel(new BorderLayout());
        resultField = new JTextField();
        mathContext = new MathContext(10);

        createNumberPanel();
    }

    public JPanel getNumberPanel() {
        return numberPanel;
    }

    private void createNumberPanel() {
        resultField.setText("0");
        resultField.setBackground(Color.white);
        resultField.setEditable(false);
        resultField.setHorizontalAlignment(JTextField.RIGHT);
        resultField.setFont(new Font(
                resultField.getFont().getFontName(),
                resultField.getFont().getStyle(),
                18
        ));
        resultField.setPreferredSize(new Dimension(
                resultField.getPreferredSize().width,
                resultField.getPreferredSize().height + 10
        ));

        JPanel textFieldNumberPanel = new JPanel(new BorderLayout());
        textFieldNumberPanel.add(resultField, BorderLayout.NORTH);
        JButton clearButton = new JButton("C");
        clearButton.addActionListener(new OperatorButtonListener("C"));
        textFieldNumberPanel.add(clearButton, BorderLayout.SOUTH);
        numberPanel.add(textFieldNumberPanel, BorderLayout.NORTH);

        JPanel numbersButtonPanel = new JPanel(new GridLayout(7, 3));

        // Создание кнопок с цифрами, операциями и функциями
        for (int i = 1; i <= 10; i++) {
            JButton button = new JButton(String.valueOf(i % 10));
            button.addActionListener(new NumberButtonListener());
            numbersButtonPanel.add(button);
        }

        JButton pointButton = new JButton(".");
        pointButton.addActionListener(new NumberButtonListener());
        numbersButtonPanel.add(pointButton);

        JButton addButton = new JButton("+");
        addButton.addActionListener(new OperatorButtonListener("+"));
        JButton subtractButton = new JButton("-");
        subtractButton.addActionListener(new OperatorButtonListener("-"));
        JButton multiplyButton = new JButton("*");
        multiplyButton.addActionListener(new OperatorButtonListener("*"));
        JButton divideButton = new JButton("/");
        divideButton.addActionListener(new OperatorButtonListener("/"));
        JButton percentButton = new JButton("%");
        percentButton.addActionListener(new OperatorButtonListener("%"));
        JButton sqrtButton = new JButton("√");
        sqrtButton.addActionListener(new OperatorButtonListener("sqrt"));
        JButton powerButton = new JButton("^");
        powerButton.addActionListener(new OperatorButtonListener("^"));

        JButton equalsButton = new JButton("=");
        equalsButton.setFont(new Font(
                equalsButton.getFont().getFontName(),
                Font.BOLD,
                24
        ));
        equalsButton.setForeground(Color.RED);
        equalsButton.addActionListener(new EqualsButtonListener());

        numbersButtonPanel.add(addButton);
        numbersButtonPanel.add(subtractButton);
        numbersButtonPanel.add(multiplyButton);
        numbersButtonPanel.add(divideButton);
        numbersButtonPanel.add(percentButton);
        numbersButtonPanel.add(sqrtButton);
        numbersButtonPanel.add(powerButton);
        numbersButtonPanel.add(equalsButton);

        currentNumber = null;
        currentOperator = null;

        numberPanel.add(numbersButtonPanel);
    }

    // Определение слушателей для кнопок
    private class NumberButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String digit = ((JButton) e.getSource()).getText();
            if(digit.equals("Error") || (!digit.equals(".") && resultField.getText().equals("0")))
                resultField.setText(digit);
            else
                resultField.setText(resultField.getText() + digit);
        }
    }

    private class OperatorButtonListener implements ActionListener {
        private final String operator;

        public OperatorButtonListener(String operator) {
            this.operator = operator;
        }

        public void actionPerformed(ActionEvent e) {
            if(operator.equals("C")) {
                resultField.setText("0");
                currentNumber = null;
                currentOperator = null;
            }
            else if(operator.equals("sqrt")) {
                currentOperator = operator;
                performCalculation();
            } else if (currentNumber == null) {
                currentNumber = new BigDecimal(resultField.getText());
                resultField.setText("0");
                currentOperator = operator;
            } else {
                performCalculation();
                currentOperator = operator;
            }
        }
    }

    private class EqualsButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            performCalculation();
        }
    }

    private void performCalculation() {
        if (currentOperator != null && currentOperator.equals("sqrt")) {
            BigDecimal number = new BigDecimal(resultField.getText());
            BigDecimal result = BigDecimal.valueOf(Math.sqrt(number.doubleValue()));

            if(result.subtract(new BigDecimal(result.toBigInteger())).compareTo(new BigDecimal("0.0")) == 0)
                resultField.setText(new BigDecimal(result.toBigInteger()).toPlainString());
            else
                resultField.setText(result.toPlainString());
        }

        if (currentNumber != null && currentOperator != null) {
            BigDecimal number = new BigDecimal(resultField.getText());
            BigDecimal result = BigDecimal.ZERO;
            switch (currentOperator) {
                case "+":
                    result = currentNumber.add(number, mathContext);
                    break;
                case "-":
                    result = currentNumber.subtract(number, mathContext);
                    break;
                case "*":
                    result = currentNumber.multiply(number, mathContext);
                    break;
                case "/":
                    if (number.compareTo(BigDecimal.ZERO) != 0)
                        result = currentNumber.divide(number, mathContext);
                    else {
                        resultField.setText("Error");
                        return;
                    }
                    break;
                case "%":
                    result = currentNumber.remainder(number, mathContext);
                    break;
                case "^":
                    result = currentNumber.pow(number.intValue());
                    break;
            }

            if(result.subtract(new BigDecimal(result.toBigInteger())).compareTo(new BigDecimal("0.0")) == 0)
                resultField.setText(new BigDecimal(result.toBigInteger()).toPlainString());
            else
                resultField.setText(result.toPlainString());

            currentNumber = null;
        }
    }
}
