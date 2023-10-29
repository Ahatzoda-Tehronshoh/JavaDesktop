package Journal;

import Journal.Model.Group;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class ConnectingDB {
    private final static String DB_CONNECTOR_URL =
            "jdbc:sqlserver://DESKTOP-LU68DA2:1433;databaseName=journal_db;integratedSecurity=true;encrypt=false;";

    private final JFrame frame;
    private final Statement statement;
    private JTable groupTable;

    private final JournalPanel journalPanel;
    private final StudentListPanel studentListPanel;

    private final ArrayList<Group> listOfGroups = new ArrayList<>();

    public ConnectingDB() {
        Statement statement1;

        try {
            Connection connection = DriverManager.getConnection(DB_CONNECTOR_URL);
            statement1 = connection.createStatement();
        } catch (SQLException e) {
            statement1 = null;
            e.printStackTrace();
        }

        statement = statement1;

        frame = new JFrame("Class Journal");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1500, 800);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.LINE_AXIS));

        journalPanel = new JournalPanel(statement, frame);
        studentListPanel = new StudentListPanel(statement);

        groupsPanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setMinimumSize(new Dimension(1200, frame.getHeight()));

        tabbedPane.addTab("Список", new JScrollPane(studentListPanel));

        tabbedPane.addTab("Журнал", journalPanel);

        frame.getContentPane().add(tabbedPane);

        //frame.pack();
        frame.setVisible(true);
    }

    private void groupsPanel() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        try {
            String sql = "SELECT g.*, d.dir_name FROM group_table g, direction d WHERE d.id = g.id_dir ORDER BY semester, dir_name ASC;";
            ResultSet resultSet = statement.executeQuery(sql);

            model.addColumn("Группа");

            while (resultSet.next()) {
                Group group = new Group(
                        resultSet.getInt("id"),
                        resultSet.getInt("id_dir"),
                        resultSet.getInt("semester"),
                        resultSet.getString("year"),
                        resultSet.getString("dir_name")
                );

                listOfGroups.add(group);

                Object[] row = new Object[1];
                row[0] = group.getDirName() + " " + group.getSemester() + " - семестер " + group.getYear();
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        groupTable = new JTable(model);
        groupTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && groupTable.getSelectedRow() != -1) {
                int selectedGroupId = listOfGroups.get(groupTable.getSelectedRow()).getId();
                studentListPanel.change(selectedGroupId);
                journalPanel.change(selectedGroupId);
            }
        });
        groupTable.setPreferredScrollableViewportSize(new Dimension(400, 600));

        JScrollPane scrollPane = new JScrollPane(groupTable);
        scrollPane.setMaximumSize(new Dimension(300, frame.getHeight()));
        frame.add(scrollPane);
    }

    public static void main(String[] args) {
        new ConnectingDB();
    }
}