import com.tehron.ConvertPanel;
import com.tehron.NumberPlane;
import com.tehron.SystemPanel;

import javax.swing.*;

public class CalculatorByTehron extends JDialog {
    private final JFrame frame;
    private final JTabbedPane tabbedPane;

    public CalculatorByTehron() {
        frame = new JFrame("Техронский калькулятор😎");
        frameSettings();

        tabbedPane = new JTabbedPane();

        createNumberPanel();
        createSystemPanel();
        createConvertPanel();

        frame.add(tabbedPane);
        frame.setVisible(true);
    }


    private void createNumberPanel() {
        tabbedPane.add(new NumberPlane().getNumberPanel());
        tabbedPane.setTitleAt(0, "Работа с числами");
    }

    private void createSystemPanel() {
        tabbedPane.add(new SystemPanel().getSystemPanel());
        tabbedPane.setTitleAt(1, "Система счисления");
    }

    private void createConvertPanel() {
        tabbedPane.add(new ConvertPanel());
        tabbedPane.setTitleAt(2, "Конвертация");
    }

    public void frameSettings() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 500);
        frame.setLocation(400, 100);
    }

    public static void main(String[] args) {
        new CalculatorByTehron();
    }
}