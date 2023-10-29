package Journal;

import Journal.Model.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class StudentListPanel extends JPanel {
    private final Statement statement;

    private final ArrayList<Student> listOfStudents = new ArrayList<>();

    private final JTable studentsListTable;

    private int groupId = -1;

    public StudentListPanel(Statement statement) {
        this.statement = statement;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        studentsListTable = new JTable();
        studentsListTable.setPreferredScrollableViewportSize(new Dimension(400, 600));
        add(new JScrollPane(studentsListTable));
    }

    private void updateStudentsListTable() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0;
            }
        };

        model.setColumnCount(11);

        int i = 1;
        for (Student student: listOfStudents)
            model.addRow(student.asVector(i++));

        studentsListTable.setModel(model);


        TableColumnModel columnModel = studentsListTable.getColumnModel();
        String[] columnHeaderArr = new String[] {
                "№", "ФИО", "Дата рождения", "Почта", "Номер телефона", "1)ФИО родителей", "2)ФИО родителей",
                "1)Номер родителей", "2)Номер родителей", "Адрес", "Год поступления"
        };

        for (int columnIndex = 0; columnIndex < 11; columnIndex++) {
            TableColumn column = columnModel.getColumn(columnIndex);
            column.setHeaderValue(columnHeaderArr[columnIndex]);

            if(columnIndex == 1) {
                column.setPreferredWidth(220);
                column.setMinWidth(220);
                column.setWidth(220);
            } else if(columnIndex == 0 || columnIndex == 10) {
                column.setPreferredWidth(25);
                column.setMinWidth(25);
                column.setWidth(25);
            }
        }

        String[] columnNameInDb = new String[] {
                "id", "fio", "date_of_birth", "email",
                "phone_number", "parent1_name", "parent2_name",
                "parent1_phone", "parent2_phone", "address", "year_of_enrollment"
        };
        model.addTableModelListener(e -> {
            try {
                System.out.println(model.getValueAt(e.getFirstRow(), e.getColumn()));
                statement.execute("UPDATE student SET " + columnNameInDb[e.getColumn()] + " = '"
                        + model.getValueAt(e.getFirstRow(), e.getColumn())
                        + "' WHERE id_group = " + groupId + " AND id = "
                        + listOfStudents.get(e.getFirstRow()).getId() + ";");
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
    }


    public void change(int groupId) {
        listOfStudents.clear();
        this.groupId = groupId;
        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM student WHERE id_group = " + groupId + ";");

            while (resultSet.next())
                listOfStudents.add(
                        new Student(
                                resultSet.getInt("id"),
                                resultSet.getString("fio"),
                                resultSet.getInt("id_group"),
                                resultSet.getString("date_of_birth"),
                                resultSet.getString("email"),
                                resultSet.getString("phone_number"),
                                resultSet.getString("parent1_name"),
                                resultSet.getString("parent2_name"),
                                resultSet.getString("parent1_phone"),
                                resultSet.getString("parent2_phone"),
                                resultSet.getString("address"),
                                resultSet.getInt("year_of_enrollment")
                        )
                );

            updateStudentsListTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
