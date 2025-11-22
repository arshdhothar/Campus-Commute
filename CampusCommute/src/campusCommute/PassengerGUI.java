package campusCommute;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PassengerGUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JPanel searchPanel;
    private JPanel historyPanel;

    private JTextField searchField;
    private JList<String> searchResultsList;
    private DefaultListModel<String> searchListModel;
    private List<Ride> currentSearchRides = new ArrayList<>();

    private JTable historyTable;
    private DefaultTableModel historyModel;

    private Passenger passenger;

    public PassengerGUI() {
        String name = (CurrentUser.loggedInUser != null) ? CurrentUser.getName() : "Guest";
        this.passenger = new Passenger(name);

        setTitle("Passenger Portal - " + name);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        createSearchTab();
        tabbedPane.addTab("Search Rides", searchPanel);

        createHistoryTab();
        tabbedPane.addTab("My History", historyPanel);

        if (CurrentUser.loggedInUser != null) {
            JPanel profileWrapper = new JPanel(new BorderLayout());
            JButton openProfileBtn = new JButton("Open My Full Profile");
            openProfileBtn.addActionListener(e -> new ViewProfileGUI(CurrentUser.loggedInUser).setVisible(true));
            
            JPanel center = new JPanel(new GridBagLayout());
            center.add(openProfileBtn);
            profileWrapper.add(center);
            tabbedPane.addTab("My Profile", profileWrapper);
        }

        add(tabbedPane);
    }

    public PassengerGUI(List<Ride> rides) {
        this();
    }

    private void createSearchTab() {
        searchPanel = new JPanel(new BorderLayout(10, 10));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(e -> performSearch());
        topBar.add(new JLabel("Destination/Start: "), BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(searchBtn, BorderLayout.EAST);
        searchPanel.add(topBar, BorderLayout.NORTH);

        searchListModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchListModel);
        searchPanel.add(new JScrollPane(searchResultsList), BorderLayout.CENTER);

        JPanel bottomBar = new JPanel(new BorderLayout());
        JButton requestBtn = new JButton("Request Selected Ride");
        requestBtn.addActionListener(e -> requestSelectedRide());
        bottomBar.add(requestBtn, BorderLayout.WEST);

        if (CurrentUser.loggedInUser != null) {
            EmergencyButtonPanel emergencyPanel = new EmergencyButtonPanel(
                    CurrentUser.getName(), 
                    CurrentUser.loggedInUser.getPhone(), 
                    "NO_RIDE_SELECTED"
            );
            
            searchResultsList.addListSelectionListener(e -> {
                int idx = searchResultsList.getSelectedIndex();
                if (idx >= 0 && idx < currentSearchRides.size()) {
                    Ride r = currentSearchRides.get(idx);
                    emergencyPanel.setRideId(String.valueOf(r.getId()));
                }
            });
            
            bottomBar.add(emergencyPanel, BorderLayout.EAST);
        }

        searchPanel.add(bottomBar, BorderLayout.SOUTH);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            searchListModel.clear();
            currentSearchRides.clear();
            return;
        }

        List<Ride> results = passenger.searchRides(query);
        currentSearchRides = results;
        searchListModel.clear();
        for (Ride r : results) {
            searchListModel.addElement(r.toString());
        }
        
        if (results.isEmpty()) {
            searchListModel.addElement("No rides found matching '" + query + "'");
        }
    }

    private void requestSelectedRide() {
        int idx = searchResultsList.getSelectedIndex();
        if (idx < 0) {
            JOptionPane.showMessageDialog(this, "Please select a ride first.");
            return;
        }
        Ride r = currentSearchRides.get(idx);
        boolean success = passenger.sendRideRequest(r);
        if (success) {
            JOptionPane.showMessageDialog(this, "Request sent successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to send request. It may already exist.");
        }
    }

    private void createHistoryTab() {
        historyPanel = new JPanel(new BorderLayout(10, 10));
        historyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columns = {"Date", "Start", "Destination", "Price", "DriverID", "Action"};
        
        historyModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        historyTable = new JTable(historyModel);
        
        historyTable.getColumnModel().getColumn(4).setMinWidth(0);
        historyTable.getColumnModel().getColumn(4).setMaxWidth(0);
        historyTable.getColumnModel().getColumn(4).setWidth(0);

        historyTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JCheckBox()));

        historyPanel.add(new JScrollPane(historyTable), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh History");
        refreshBtn.addActionListener(e -> loadPassengerHistory());
        historyPanel.add(refreshBtn, BorderLayout.SOUTH);
        
        loadPassengerHistory();
    }

    private void loadPassengerHistory() {
        if (CurrentUser.loggedInUser == null) return;

        Ride[] history = RideService.getInstance().getPassengerHistory(CurrentUser.getId());
        historyModel.setRowCount(0);

        if (history != null) {
            for (Ride r : history) {
                String driverIdStr = (r.getDriverId() != null) ? String.valueOf(r.getDriverId()) : "0";
                String actionText = r.getStatus();

                // CHANGED LOGIC: Allow rating if Approved OR Completed
                boolean isRateable = "APPROVED".equalsIgnoreCase(r.getStatus()) || 
                                     "COMPLETED".equalsIgnoreCase(r.getStatus());

                if (isRateable && r.getDriverId() != null) {
                    actionText = "Rate Driver";
                }

                historyModel.addRow(new Object[]{
                        r.getDate(),
                        r.getStartLocation(),
                        r.getDestination(),
                        "$" + r.getPrice(),
                        driverIdStr,
                        actionText
                });
            }
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) {
                setText("");
                setEnabled(false);
            } else {
                setText(value.toString());
                setEnabled("Rate Driver".equals(value.toString()));
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && "Rate Driver".equals(label)) {
                String driverIdStr = (String) historyTable.getValueAt(currentRow, 4);
                Long driverId = Long.parseLong(driverIdStr);

                SwingUtilities.invokeLater(() -> showRatingDialog(driverId));
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        private void showRatingDialog(Long driverId) {
            String input = JOptionPane.showInputDialog(button, 
                "Rate this driver (1.0 - 5.0):", "Rate Ride", JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.isEmpty()) {
                try {
                    double rating = Double.parseDouble(input);
                    if (rating < 1.0 || rating > 5.0) {
                        JOptionPane.showMessageDialog(button, "Please enter a value between 1.0 and 5.0");
                    } else {
                        boolean ok = RideService.getInstance().rateDriver(driverId, rating);
                        if (ok) {
                            JOptionPane.showMessageDialog(button, "Rating submitted! Thank you.");
                        } else {
                            JOptionPane.showMessageDialog(button, "Failed to submit. You may have already rated this ride.");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(button, "Invalid number format.");
                }
            }
        }
    }
}