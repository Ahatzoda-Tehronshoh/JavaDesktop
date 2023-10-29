package Journal;

import Journal.Model.Disciplina;
import Journal.Model.Journal;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class JournalPanel extends JPanel {
    private final JFrame parentFrame;

    private final ArrayList<Disciplina> listOfDisciplina = new ArrayList<>();
    private final HashMap<Integer, Integer> listOfStudents = new HashMap<>();
    private final HashMap<Integer, Integer> listOfStudentsRS = new HashMap<>();

    // contents columnIndex and id_journal in this column
    private final HashMap<Integer, Integer> listOfPairsColumn = new HashMap<>();

    private final JComboBox<String> subjectComboBox;
    private final Statement statement;
    private final JTable journalTable;
    private final JLabel infoLabel;

    private final JTextField themeField;
    private final JButton saveThemeButton;

    public JournalPanel(Statement statement, JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.statement = statement;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        subjectComboBox = new JComboBox<>();
        add(subjectComboBox);

        journalTable = new JTable();
        journalTable.setPreferredScrollableViewportSize(new Dimension(400, 600));
        journalTable.getTableHeader().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int clickedColumnIndex = journalTable.getTableHeader().columnAtPoint(e.getPoint());
                if (e.getClickCount() == 2 && clickedColumnIndex >= (2 + listOfPairsColumn.size()))
                    createAddingPairFrame().setVisible(true);
            }
        });
        JScrollPane scrollPane = new JScrollPane(journalTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        journalTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        add(scrollPane);

        infoLabel = new JLabel();
        add(infoLabel);

        JPanel themePanel = new JPanel();
        themePanel.setLayout(new BoxLayout(themePanel, BoxLayout.LINE_AXIS));

        themeField = new JTextField();
        themeField.setEditable(false);
        themePanel.add(themeField);

        saveThemeButton = new JButton();
        saveThemeButton.setText("Save");
        saveThemeButton.setEnabled(false);
        themePanel.add(saveThemeButton);

        add(themePanel);

        subjectComboBox.addActionListener(e -> showJournalTable());
    }

    private TableColumnModelListener modelListener;
    private int pairCounter = 0;

    private void showJournalTable() {
        infoLabel.setText("");
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 1 && column < (pairCounter + 2);
            }
        };
        ArrayList<Journal> listOfPairs = new ArrayList<>();

        if (subjectComboBox.getSelectedIndex() != -1) {
            Disciplina selectedDisp = listOfDisciplina.get(subjectComboBox.getSelectedIndex());
            model.setColumnCount(2 + (selectedDisp.getFullCount() / 2));

            try {
                statement.executeQuery("SELECT * FROM student WHERE id_group = " + selectedDisp.getIdGroup() + " ORDER BY fio;");
                ResultSet resultSet = statement.getResultSet();

                listOfStudents.clear();
                int i = 1;
                while (resultSet.next()) {
                    Object[] rowObject = new Object[2];
                    rowObject[0] = i;
                    rowObject[1] = resultSet.getString("fio");
                    listOfStudents.put(resultSet.getInt("id"), i);
                    listOfStudentsRS.put(i - 1, resultSet.getInt("id"));
                    model.addRow(rowObject);
                    i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                statement.executeQuery("SELECT * FROM journal where id_discipl = " + selectedDisp.getId() + " ORDER BY date_str;");
                ResultSet resultSet = statement.getResultSet();

                while (resultSet.next()) {
                    listOfPairs.add(
                            new Journal(
                                    resultSet.getInt("id"),
                                    resultSet.getInt("id_discipl"),
                                    resultSet.getInt("type_pair"),
                                    resultSet.getString("theme"),
                                    resultSet.getString("date_str")
                            )
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                statement.executeQuery("SELECT type_pair, COUNT(type_pair) as count FROM journal WHERE id_discipl = " +
                        selectedDisp.getId() + " GROUP BY type_pair;");
                ResultSet resultSet = statement.getResultSet();

                int[] count = new int[3];
                while (resultSet.next()) {
                    if (resultSet.getInt("type_pair") == 0)
                        count[0] = resultSet.getInt("count");
                    else if (resultSet.getInt("type_pair") == 1)
                        count[1] = resultSet.getInt("count");
                    else
                        count[2] = resultSet.getInt("count");
                }

                String formControl = "Зачёт";
                if(selectedDisp.getFormControl().equals("Э"))
                    formControl = "Экзамен";
                infoLabel.setText("<html><div style='text-align: center;'>Форма контроля: " + formControl + "<br>"
                        + "Количество часов: " + selectedDisp.getFullCount() + " - "
                        + "Осталось: " + (selectedDisp.getFullCount() - 2*Arrays.stream(count).sum()) + "<br>"
                        + "Количество ЛК: " + selectedDisp.getCountLK() + " - "
                        + "Осталось: " + (selectedDisp.getCountLK() - 2*count[0]) + "<br>"
                        + "Количество ПЗ: " + selectedDisp.getCountPZ() + " - "
                        + "Осталось: " + (selectedDisp.getCountPZ() - 2*count[1]) + "<br>"
                        + "Количество ЛБ: " + selectedDisp.getCountLB() + " - "
                        + "Осталось: " + (selectedDisp.getCountLB() - 2*count[2]) + ";</html>");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        journalTable.setModel(model);
        pairCounter = listOfPairs.size();
        model.fireTableStructureChanged();

        if (journalTable.getColumnCount() > 1) {
            TableColumnModel columnModel = journalTable.getColumnModel();

            for (int i = 0; i < model.getColumnCount(); i++)
                columnModel.getColumn(i).setHeaderValue("");

            TableColumn column = columnModel.getColumn(0);
            column.setHeaderValue("№");
            column.setPreferredWidth(25);
            column.setMinWidth(25);
            column.setWidth(25);

            column = columnModel.getColumn(1);
            column.setHeaderValue("ФИО");
            column.setPreferredWidth(220);
            column.setMinWidth(220);
            column.setWidth(120);

            int columnIndex = 2;
            for (Journal pair : listOfPairs) {
                column = columnModel.getColumn(columnIndex);

                column.setPreferredWidth(50);
                column.setMinWidth(50);
                column.setWidth(50);

                String typePair = "(ЛК)";
                if (pair.getTypePair() == 1)
                    typePair = "(ПЗ)";
                else if (pair.getTypePair() == 2)
                    typePair = "(ЛБ)";
                column.setHeaderValue(typePair + pair.getDateStr());

                column.setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                        setToolTipText(pair.getTheme());
                        return this;
                    }
                });

                journalContent(pair.getId(), columnIndex);
                listOfPairsColumn.put(columnIndex, pair.getId());
                columnIndex++;
            }
        }

        journalTable.setRowSelectionAllowed(false);
        journalTable.setCellSelectionEnabled(true);
        journalTable.getColumnModel().removeColumnModelListener(modelListener);
        modelListener = new TableColumnModelListener() {
            @Override
            public void columnAdded(TableColumnModelEvent e) {

            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {

            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {

            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {

            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    if (journalTable.getSelectedColumn() > 1 && journalTable.getSelectedRow() >= 0 && journalTable.getSelectedColumn() < (listOfPairs.size() + 2)) {
                        themeField.setEditable(true);
                        saveThemeButton.setEnabled(true);
                        themeField.setText(listOfPairs.get(journalTable.getSelectedColumn() - 2).getTheme());
                    } else {
                        themeField.setText("");
                        saveThemeButton.setEnabled(false);
                        themeField.setEditable(false);
                    }
                }
            }
        };
        journalTable.getColumnModel().addColumnModelListener(modelListener);

        saveThemeButton.addActionListener(e -> saveChangedTheme());

        model.addTableModelListener(e -> {
            String newValue = (String) model.getValueAt(e.getFirstRow(), e.getColumn());
            try {
                if (Integer.parseInt(newValue) < 0 && Integer.parseInt(newValue) > 100)
                    model.setValueAt("0", e.getFirstRow(), e.getColumn());
            } catch (NumberFormatException exception) {
                if (!newValue.equals("Н") && !newValue.equals(""))
                    model.setValueAt("0", e.getFirstRow(), e.getColumn());
            }
            newValue = (String) model.getValueAt(e.getFirstRow(), e.getColumn());

            if (listOfPairsColumn.get(e.getColumn()) != null && listOfStudentsRS.get(e.getFirstRow()) != null)
                try {
                    String query;
                    if (newValue.equals(""))
                        query = "DELETE FROM journal_content" + " WHERE id_journal = " + listOfPairsColumn.get(e.getColumn())
                                + " AND id_student = " + listOfStudentsRS.get(e.getFirstRow()) + ";";
                    else {
                        query = "IF NOT EXISTS (SELECT * FROM journal_content WHERE id_journal = " + listOfPairsColumn.get(e.getColumn()) +
                                " AND id_student = " + listOfStudentsRS.get(e.getFirstRow()) + " ) BEGIN insert into journal_content(id_journal, id_student, is_absent, mark) values("
                                + listOfPairsColumn.get(e.getColumn()) + ", " + listOfStudentsRS.get(e.getFirstRow()) + ", ";

                        if (newValue.equals("Н"))
                            query = query + "1, 0); END ELSE BEGIN UPDATE journal_content SET is_absent = 1 ";
                        else
                            query = query + "0, " + newValue + "); END ELSE BEGIN UPDATE journal_content SET mark = " + newValue;

                        query = query + " WHERE id_journal = " + listOfPairsColumn.get(e.getColumn())
                                + " AND id_student = " + listOfStudentsRS.get(e.getFirstRow()) + "; END";
                    }

                    statement.execute(query);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
        });
    }

    private void saveChangedTheme() {
        try {
            statement.execute(
                    "UPDATE journal SET theme = '"
                            + themeField.getText() + "' WHERE id = "
                            + listOfPairsColumn.get(journalTable.getSelectedColumn()) + ";"
            );

            showJournalTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void journalContent(int pairId, int columnIndex) {
        try {
            statement.executeQuery("SELECT * FROM journal_content WHERE id_journal = " + pairId + ";");
            ResultSet resultSet = statement.getResultSet();

            while (resultSet.next()) {
                if (resultSet.getInt("is_absent") == 0)
                    journalTable.setValueAt(
                            resultSet.getInt("mark"),
                            listOfStudents.get(resultSet.getInt("id_student")) - 1,
                            columnIndex
                    );
                else
                    journalTable.setValueAt(
                            "Н",
                            listOfStudents.get(resultSet.getInt("id_student")) - 1,
                            columnIndex
                    );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] getNotFinishedPairType() {
        if (subjectComboBox.getSelectedIndex() == -1)
            return new String[]{};

        ArrayList<String> result = new ArrayList<>();
        Disciplina currentDis = listOfDisciplina.get(subjectComboBox.getSelectedIndex());
        try {
            statement.executeQuery("SELECT type_pair, COUNT(type_pair) as count FROM journal WHERE id_discipl = " +
                    currentDis.getId() + " GROUP BY type_pair;");
            ResultSet resultSet = statement.getResultSet();

            int[] count = new int[3];
            while (resultSet.next()) {
                if (resultSet.getInt("type_pair") == 0)
                    count[0] = resultSet.getInt("count");
                else if (resultSet.getInt("type_pair") == 1)
                    count[1] = resultSet.getInt("count");
                else
                    count[2] = resultSet.getInt("count");
            }

            if (currentDis.getCountLK() - 2*count[0] > 0)
                result.add("ЛК");

            if (currentDis.getCountPZ() - 2*count[1] > 0)
                result.add("ПЗ");

            if (currentDis.getCountLB() - 2*count[2] > 0)
                result.add("ЛБ");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result.toArray(new String[0]);
    }

    private JDialog createAddingPairFrame() {
        JDialog dialog = new JDialog(parentFrame, "Добавьте новую пару:", true);
        dialog.setLayout(new GridLayout(4, 2));

        dialog.add(new JLabel("Тип занятие:"));
        JComboBox<String> pairType = new JComboBox<>(getNotFinishedPairType());
        dialog.add(pairType);

        dialog.add(new JLabel("Тема:"));
        JTextField theme = new JTextField();
        dialog.add(theme);

        dialog.add(new JLabel("Дата:"));
        JTextField date = new JTextField(10);
        date.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
        date.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            public void update() {
                String text = date.getText();
                if (text.length() == 2)
                    date.setText(text + "-");
                else if (text.length() == 5)
                    date.setText(text + "-");
            }
        });
        dialog.add(date);

        JLabel errorLabel = new JLabel();
        dialog.add(errorLabel);
        JButton addingButton = new JButton("Добавить");
        addingButton.setEnabled(pairType.getItemCount() > 0);
        addingButton.addActionListener(e -> {
            if (subjectComboBox.getSelectedIndex() == -1 || !isValidDate(date.getText())) {
                errorLabel.setText("<html>Некоторые поля заполнены неверно!</html>");
                return;
            }

            errorLabel.setText("");
            Disciplina currentDis = listOfDisciplina.get(subjectComboBox.getSelectedIndex());
            int pair = 0;
            switch (pairType.getItemAt(pairType.getSelectedIndex())) {
                case "ПЗ" -> pair = 1;
                case "ЛБ" -> pair = 2;
            }
            try {
                statement.execute("INSERT INTO journal(id_discipl, date_str, type_pair, theme) VALUES (" +
                        currentDis.getId() + ", '" +
                        date.getText() + "', " +
                        pair + ", '" +
                        theme.getText() + "');");
            } catch (Exception exception) {
                errorLabel.setText("<html>Что-то не так. Повторите пожалуйста! - " + exception.getMessage() + "</html>");
                exception.printStackTrace();
                return;
            }

            showJournalTable();
            dialog.setVisible(false);
        });
        dialog.add(addingButton);

        dialog.pack();
        dialog.setLocation(500, 300);
        return dialog;
    }

    private boolean isValidDate(String text) {
        return text.matches("^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})$");
    }

    public void change(int groupId) {
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM discipl WHERE id_group = " + groupId + ";");
            listOfDisciplina.clear();

            while (resultSet.next())
                listOfDisciplina.add(
                        new Disciplina(
                                resultSet.getInt("id"),
                                resultSet.getString("subject_name"),
                                resultSet.getInt("id_group"),
                                resultSet.getInt("countLK"),
                                resultSet.getInt("countPZ"),
                                resultSet.getInt("countLB"),
                                resultSet.getString("formControl")
                        )
                );

            subjectComboBox.removeAllItems();
            for (Disciplina dis : listOfDisciplina)
                subjectComboBox.addItem(dis.getSubjectName());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
