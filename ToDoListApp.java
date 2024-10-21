import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ToDoListApp {
    private JFrame frame;
    private JTextField taskField;
    private JTable taskTable;
    private TaskTableModel tableModel;
    private JButton removeButton;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ToDoListApp::new);
    }

    public ToDoListApp() {
        frame = new JFrame("To-Do List App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.setLayout(new BorderLayout());

        // Set the sky blue background color with 50% opacity
        frame.getContentPane().setBackground(new Color(135, 206, 235, 128)); // Sky blue color with 50% opacity

        // Task input field
        taskField = new JTextField();
        frame.add(taskField, BorderLayout.NORTH);

        // Create table model and task table
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);

        // Set a preferred width for the checkbox column (narrower)
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(30);

        JScrollPane scrollPane = new JScrollPane(taskTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false); // Set button panel transparent so background shows through
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Add task button
        JButton addButton = new JButton("Add Task");
        buttonPanel.add(addButton);

        // Remove task button (initially hidden)
        removeButton = new JButton("Remove Selected Tasks");
        removeButton.setVisible(false);
        buttonPanel.add(removeButton);

        // Add task button action listener
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String task = taskField.getText().trim();
                if (!task.isEmpty()) {
                    tableModel.addTask(task);
                    taskField.setText("");
                }
            }
        });

        // Remove selected tasks action listener
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.removeSelectedTasks();
                removeButton.setVisible(false);
            }
        });

        // Add a listener to show/hide the remove button based on checkbox selection
        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (tableModel.hasCheckedTasks()) {
                removeButton.setVisible(true);
            } else {
                removeButton.setVisible(false);
            }
        });

        frame.setVisible(true);
    }

    // Custom Table Model class for handling tasks with checkboxes
    class TaskTableModel extends AbstractTableModel {
        private final ArrayList<Task> tasks = new ArrayList<>();
        private final String[] columnNames = {"", "Task"}; // Empty string for checkbox header

        // Inner class to represent a task with a checkbox
        class Task {
            boolean isSelected;
            String taskName;

            Task(String taskName) {
                this.taskName = taskName;
                this.isSelected = false;
            }
        }

        public void addTask(String task) {
            tasks.add(new Task(task));
            fireTableDataChanged();
        }

        public void removeSelectedTasks() {
            tasks.removeIf(task -> task.isSelected);
            fireTableDataChanged();
        }

        public boolean hasCheckedTasks() {
            for (Task task : tasks) {
                if (task.isSelected) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int getRowCount() {
            return tasks.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == 0) {
                return Boolean.class;
            } else {
                return String.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Task task = tasks.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return task.isSelected;
                case 1:
                    return task.taskName;
                default:
                    return null;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                tasks.get(rowIndex).isSelected = (Boolean) aValue;
                fireTableCellUpdated(rowIndex, columnIndex);
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 0; // Only checkbox column is editable
        }
    }
}