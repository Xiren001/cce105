package project3;

import javax.swing.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.Window.Type;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Task {
    String description;
    boolean isCompleted;
    int urgency;

    Task(String description, int urgency) {
        this.description = description;
        this.isCompleted = false;
        this.urgency = urgency;
    }
}

public class final1 extends JFrame {
    private DefaultListModel<Task> toDoListModel;
    private JList<Task> toDoList;
    private JTextField taskDescriptionField;
    private JComboBox<String> urgencyComboBox;
    private NotificationFrame notificationFrame;
    private Task currentTask;

    public final1() {
    	setResizable(false);
        setBackground(new Color(51, 74, 130));
        setTitle("TASK LIST");
        setSize(753, 725);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set Look and Feel to Nimbus
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Customize UI properties
        UIManager.put("OptionPane.background", new Color(255, 255, 200));
        UIManager.put("OptionPane.messageForeground", Color.BLACK);

        toDoListModel = new DefaultListModel<>();

        String[] urgencyOptions = {"GREEN", "ORANGE", "RED"};
        getContentPane().setLayout(null);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(0, 608, 737, 79);
        getContentPane().add(buttonPanel);
        buttonPanel.setLayout(null);

        taskDescriptionField = new JTextField(20);
        taskDescriptionField.setBounds(137, 0, 264, 55);
        buttonPanel.add(taskDescriptionField);

        JButton addButton = new JButton("Add Task");
        addButton.setBounds(409, 12, 99, 31);
        buttonPanel.add(addButton);
        urgencyComboBox = new JComboBox<>(urgencyOptions);
        urgencyComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Low-priority", "Moderate", "High-priority"}));
        urgencyComboBox.setBounds(12, 12, 113, 30);
        buttonPanel.add(urgencyComboBox);

        JButton removeAllCompletedButton = new JButton("r");
        removeAllCompletedButton.setIcon(new ImageIcon(final1.class.getResource("/project3/trash-can-icon-21 (2).png")));
        removeAllCompletedButton.setBounds(678, 11, 47, 44);
        buttonPanel.add(removeAllCompletedButton);
        removeAllCompletedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAllCompletedTasks();
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String description = taskDescriptionField.getText();
                int urgency = urgencyComboBox.getSelectedIndex();
                if (!description.isEmpty()) {
                    addTask(description, urgency);
                    taskDescriptionField.setText(""); // Clear the input field after adding a task
                    urgencyComboBox.setSelectedIndex(0); // Reset the urgency combo box
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setBounds(0, 0, 737, 608);
        getContentPane().add(panel);
        panel.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 11, 717, 586);
        panel.add(scrollPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        toDoList = new JList<>(toDoListModel);
        scrollPane.setViewportView(toDoList);
        toDoList.setBackground(new Color(107, 124, 148));

        // Set font size for the cell renderer
        toDoList.setCellRenderer(new CheckboxListCellRenderer(toDoList.getFont().deriveFont(24.0f)));
        toDoList.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font size here

        // Add checkbox for each task
        toDoList.addMouseListener(new CheckboxMouseListener());

        // Notification frame
        notificationFrame = new NotificationFrame();
    }

    public void addTask(String description, int urgency) {
        Task newTask = new Task(description, urgency);

        // Determine the insertion index based on urgency
        int insertionIndex = 0;
        for (int i = 0; i < toDoListModel.getSize(); i++) {
            Task task = toDoListModel.getElementAt(i);
            if (urgency < task.urgency) {
                insertionIndex = i + 1;
            }
        }

        // Insert the new task at the determined index
        toDoListModel.add(insertionIndex, newTask);
    }

    private void removeAllCompletedTasks() {
        boolean removedTask = false;

        for (int i = toDoListModel.getSize() - 1; i >= 0; i--) {
            Task task = toDoListModel.getElementAt(i);
            if (task.isCompleted) {
                toDoListModel.removeElementAt(i);
                removedTask = true;
            }
        }

        if (removedTask) {
            notificationFrame.showNotification("Removed completed task(s)");
        }
    }

    private class NotificationFrame extends JFrame {
        private JLabel notificationLabel;

        public NotificationFrame() {
            setTitle("Notification");
            setSize(400, 100);
            setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            // Customize the appearance of the notification frame
            setUndecorated(true);
            setBackground(new Color(255, 255, 200));
            setLayout(new BorderLayout());

            notificationLabel = new JLabel();
            notificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(notificationLabel, BorderLayout.CENTER);
        }

        public void showNotification(String message) {
            notificationLabel.setText(message);
            setLocationRelativeTo(final1.this);
            setVisible(true);

            // Schedule a task to hide the notification after a short delay
            Timer timer = new Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private class CheckboxListCellRenderer extends JPanel implements ListCellRenderer<Task> {
        private JCheckBox checkBox;
        private JLabel textLabel;
        private JButton editButton;

        public CheckboxListCellRenderer(Font font) {
            setLayout(new BorderLayout());

            checkBox = new JCheckBox();
            add(checkBox, BorderLayout.WEST);

            textLabel = new JLabel();
            textLabel.setFont(font);
            add(textLabel, BorderLayout.CENTER);

            editButton = new JButton("Edit");
            add(editButton, BorderLayout.EAST);

            // Add an ActionListener for the edit button
            editButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Store the task being edited
                    currentTask = toDoListModel.getElementAt(toDoList.getSelectedIndex());
                    // Call a method to handle task editing
                    editTask(currentTask);
                }
            });

            JSeparator separator = new JSeparator(JSeparator.HORIZONTAL);
            add(separator, BorderLayout.SOUTH);

            // Set the panel to be transparent
            setOpaque(false);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            checkBox.setSelected(task.isCompleted);
            textLabel.setText(task.description);

            // Customize the color based on urgency
            switch (task.urgency) {
                case 0:
                    textLabel.setForeground(Color.GREEN);
                    break;
                case 1:
                    textLabel.setForeground(Color.ORANGE);
                    break;
                case 2:
                    textLabel.setForeground(Color.RED);
                    break;
            }

            return this;
        }
    }

    private class CheckboxMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent event) {
            JList<Task> list = (JList<Task>) event.getSource();
            int index = list.locationToIndex(event.getPoint());
            currentTask = toDoListModel.getElementAt(index);

            if (event.getClickCount() == 2) {
                // Double-click to edit task description
                editTask(currentTask);
            } else if (event.getButton() == MouseEvent.BUTTON1) {
                // Left-click to toggle completion status
                currentTask.isCompleted = !currentTask.isCompleted;
                list.repaint();
            }
        }
    }

    private void editTask(Task task) {
        if (task != null) {
            // Create a custom dialog for editing tasks
        	JDialog editDialog = new JDialog();
            editDialog.setUndecorated(true);
            editDialog.setType(Type.POPUP);
            editDialog.setSize(450, 85);
            editDialog.getContentPane().setLayout(new BorderLayout());
            
            JPanel panel = new JPanel();
            panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
            editDialog.getContentPane().add(panel, BorderLayout.CENTER);

            // Create components for the dialog
            JTextField editField = new JTextField(task.description);
            JButton saveButton = new JButton("Save");
            editField.setBounds(10, 6, 354, 69);
            saveButton.setBounds(374, 6, 66, 69);
            panel.setLayout(null);
            panel.add(editField);
            panel.add(saveButton);
            

            // Add ActionListener for the save button
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Update the task description
                    task.description = editField.getText();
                    toDoList.repaint();
                    // Close the dialog
                    editDialog.dispose();
                }
            });

            // Set the dialog location relative to the main frame
            editDialog.setLocationRelativeTo(this);
            editDialog.setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new final1().setVisible(true);
            }
        });
    }
}
