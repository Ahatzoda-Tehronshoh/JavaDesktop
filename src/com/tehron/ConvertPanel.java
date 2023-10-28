package com.tehron;

import javax.swing.*;
import java.awt.*;

public class ConvertPanel extends JPanel {
    public ConvertPanel() {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);

        JPanel distancePanel = new DistanceConversionPanel();
        tabbedPane.addTab("Расстояние", distancePanel);

        JPanel volumePanel = new VolumeConversionPanel();
        tabbedPane.addTab("Объем", volumePanel);

        JPanel weightPanel = new MassConversionPanel();
        tabbedPane.addTab("Масса", weightPanel);

        JPanel temperaturePanel = new TemperatureConversionPanel();
        tabbedPane.addTab("Температура", temperaturePanel);

        JPanel currencyPanel = new CurrencyConversionPanel();
        tabbedPane.addTab("Валюта", currencyPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
