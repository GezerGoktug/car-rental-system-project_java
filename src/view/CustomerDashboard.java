package view;

import controller.CarController;
import controller.RentalController;
import exceptions.RentalException;
import models.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public class CustomerDashboard extends JFrame {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private JTable carsTable;
    private JTable approvedOrdersTable;
    private JTable pendingOrdersTable;
    private JTable rejectedOrdersTable;


    public CustomerDashboard(User user) {
        this.currentUser = user;
        initializeFrame();
        initializeTabbedPane();
        setVisible(true);
    }

    //Formu başlatma
    private void initializeFrame() {
        setTitle("Car Rental System - " + currentUser.getUsername());
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addMenuBar();


    }

    //Menü bar ekleme.
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        //Profil menü oluşturma.
        JMenu profileMenu = new JMenu("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        JMenuItem updateProfileItem = new JMenuItem("Update Profile");
        logoutItem.addActionListener(e -> {
            //Login sayfasına geçiş.
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
            this.dispose();
        });
        updateProfileItem.addActionListener(e -> {
            //Login sayfasına geçiş.
            ProfilePanel profilePage = new ProfilePanel(currentUser);
            profilePage.setVisible(true);
            dispose();
        });
        profileMenu.add(logoutItem);
        profileMenu.add(updateProfileItem);

        menuBar.add(profileMenu);
        setJMenuBar(menuBar);
    }

    //Tabbed Pane başlatma.
    private void initializeTabbedPane() {
        tabbedPane = new JTabbedPane();

        //Araba panelini oluşturma.
        JPanel carsPanel = createCarsPanel();
        tabbedPane.addTab("Available Cars", carsPanel);

        JTabbedPane myOrdersTabbedPane = new JTabbedPane();

        JPanel approvedOrdersPanel = createApprovedOrdersPanel();
        JPanel rejectedOrdersPanel = createRejectedOrdersPanel();


        myOrdersTabbedPane.addTab("Approved Requests", approvedOrdersPanel);


        JPanel pendingOrdersPanel = createPendingOrdersPanel();
        myOrdersTabbedPane.addTab("Pending Requests", pendingOrdersPanel);
        myOrdersTabbedPane.addTab("Rejected Requests", rejectedOrdersPanel);
        tabbedPane.addTab("My Orders", myOrdersTabbedPane);

        add(tabbedPane);
    }

    //Onaylama paneli oluşturma.
    private JPanel createApprovedOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());


        String[] columnNames = {"Transaction ID", "Car Brand", "Car Model", "Plate Number", "Start Date", "End Date", "Status", "Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        approvedOrdersTable = new JTable(tableModel);
        approvedOrdersTable.setPreferredScrollableViewportSize(new Dimension(700, 400));
        approvedOrdersTable.setFillsViewportHeight(true);

        try {
            List<RentalTransaction> rentals = RentalController.getMyRentalsByStatus(currentUser, RentalTransaction.TransactionStatus.COMPLETED);
            for (RentalTransaction rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getPlateNumber(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus(),
                        rental.getTotalCost()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }


        JScrollPane scrollPane = new JScrollPane(approvedOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton returnCarButton = new JButton("Return Car");
        JButton extendRentalButton = new JButton("Extend Rental");

        returnCarButton.addActionListener(e -> openReturnCarModal());
        extendRentalButton.addActionListener(e -> openExtendRentalTransactionsModal());
        buttonPanel.add(returnCarButton);
        buttonPanel.add(extendRentalButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    //Bekleme paneli oluşturma.
    private JPanel createPendingOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Create table model for pending orders
        String[] columnNames = {"Transaction ID", "Car Brand", "Car Model", "Plate Number", "Start Date", "End Date", "Status", "Price"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        pendingOrdersTable = new JTable(tableModel);
        pendingOrdersTable.setPreferredScrollableViewportSize(new Dimension(700, 400));
        pendingOrdersTable.setFillsViewportHeight(true);

        try {
            List<RentalTransaction> rentals = RentalController.getMyRentalsByStatus(currentUser, RentalTransaction.TransactionStatus.PENDING);
            for (RentalTransaction rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getPlateNumber(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus(),
                        rental.getTotalCost()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }


        JScrollPane scrollPane = new JScrollPane(pendingOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelRentalButton = new JButton("Cancel Rental");


        cancelRentalButton.addActionListener(e -> openCancelRentalButtonModal());

        buttonPanel.add(cancelRentalButton);


        panel.add(buttonPanel, BorderLayout.SOUTH);


        return panel;
    }

    //Reddetme paneli oluşturma.
    private JPanel createRejectedOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Transaction ID", "Car Brand", "Car Model", "Plate Number", "Start Date", "End Date", "Status", "Price"};

        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        rejectedOrdersTable = new JTable(tableModel);
        rejectedOrdersTable.setPreferredScrollableViewportSize(new Dimension(700, 400));
        rejectedOrdersTable.setFillsViewportHeight(true);

        try {
            List<RentalTransaction> rentals = RentalController.getMyRentalsByStatus(currentUser, RentalTransaction.TransactionStatus.REJECTED);
            for (RentalTransaction rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getPlateNumber(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus(),
                        rental.getTotalCost()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(rejectedOrdersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    //Araba paneli oluşturma.
    private JPanel createCarsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"ID", "Brand", "Model", "Year", "Category", "Plate Number", "Daily Rent Price"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        carsTable = new JTable(tableModel);
        carsTable.setPreferredScrollableViewportSize(new Dimension(700, 400));
        carsTable.setFillsViewportHeight(true);


        List<Car> availableCars = CarController.getAvailableCars();
        for (Car car : availableCars) {
            tableModel.addRow(new Object[]{
                    car.getCarId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getCategory(),
                    car.getPlateNumber(),
                    car.getDailyRentPrice()
            });
        }

        JScrollPane scrollPane = new JScrollPane(carsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));


        JButton rentButton = new JButton("Rent Car");
        rentButton.setPreferredSize(new Dimension(100, 30));
        rentButton.addActionListener(e -> openRentCarModal());

        JButton detailsButton = new JButton("View Details");
        detailsButton.setPreferredSize(new Dimension(100, 30));
        detailsButton.addActionListener(e -> showCarDetails());


        buttonPanel.add(rentButton);
        buttonPanel.add(detailsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    //Araba detaylarını gösterme.
    private void showCarDetails() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to view details", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String carId = carsTable.getValueAt(selectedRow, 0).toString();

        Car car = CarController.getSelectedCarById(UUID.fromString(carId));

        JDialog detailsDialog = new JDialog(this, "Car Details", true);
        detailsDialog.setSize(400, 500);
        detailsDialog.setLocationRelativeTo(this);

        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        detailsPanel.add(new JLabel("Car ID:"));
        detailsPanel.add(new JLabel(carId));
        detailsPanel.add(new JLabel("Brand:"));
        detailsPanel.add(new JLabel(car.getBrand()));
        detailsPanel.add(new JLabel("Model:"));
        detailsPanel.add(new JLabel(car.getModel()));
        detailsPanel.add(new JLabel("Year:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getYear())));
        detailsPanel.add(new JLabel("Category:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getCategory())));
        detailsPanel.add(new JLabel("Engine Capacity:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getEngineCapacity())));
        detailsPanel.add(new JLabel("Plate Number:"));
        detailsPanel.add(new JLabel(car.getPlateNumber()));
        detailsPanel.add(new JLabel("Last Maintenance Date:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getLastMaintenanceDate())));
        detailsPanel.add(new JLabel("Total mileage:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getTotalMileage())));
        detailsPanel.add(new JLabel("Daily Rent Price:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getDailyRentPrice())));
        detailsPanel.add(new JLabel("Fuel Type:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getFuelType())));
        detailsPanel.add(new JLabel("Gear:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getGear())));

        if (car instanceof CommercialVehicle) {
            detailsPanel.add(new JLabel("Load Capacity:"));
            detailsPanel.add(new JLabel(String.valueOf(((CommercialVehicle) car).getLoadCapacity())));
            detailsPanel.add(new JLabel("Cooling System:"));
            detailsPanel.add(new JLabel(String.valueOf(((CommercialVehicle) car).isHasCoolingSystem())));
            detailsPanel.add(new JLabel("Fuel Consumption Rate:"));
            detailsPanel.add(new JLabel(String.valueOf(((CommercialVehicle) car).getFuelConsumptionRate())));
        } else if (car instanceof PassengerCar) {
            detailsPanel.add(new JLabel("Passenger Capacity:"));
            detailsPanel.add(new JLabel(String.valueOf(((PassengerCar) car).getPassengerCapacity())));
            detailsPanel.add(new JLabel("Air conditioning:"));
            detailsPanel.add(new JLabel(String.valueOf(((PassengerCar) car).isHasAirConditioning())));
            detailsPanel.add(new JLabel("Comfort Level:"));
            detailsPanel.add(new JLabel(String.valueOf(((PassengerCar) car).getComfortLevel())));
        } else if (car instanceof SUV) {
            detailsPanel.add(new JLabel("Terrain Mode:"));
            detailsPanel.add(new JLabel(String.valueOf(((SUV) car).getTerrainMode())));
            detailsPanel.add(new JLabel("Ground clearance:"));
            detailsPanel.add(new JLabel(String.valueOf(((SUV) car).getGroundClearance())));
            detailsPanel.add(new JLabel("All Wheel Drive:"));
            detailsPanel.add(new JLabel(String.valueOf(((SUV) car).isHasAllWheelDrive())));
            detailsPanel.add(new JLabel("Off-Road Ready:"));
            detailsPanel.add(new JLabel(String.valueOf(((SUV) car).isOffRoadReady())));
        }

        detailsDialog.add(detailsPanel);
        detailsDialog.setVisible(true);
    }

    //Araba tablosu yenileme.
    private void refreshCarTable() {
        DefaultTableModel tableModel = (DefaultTableModel) carsTable.getModel();
        tableModel.setRowCount(0);

        List<Car> availableCars = CarController.getAvailableCars();
        for (Car car : availableCars) {
            tableModel.addRow(new Object[]{
                    car.getCarId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getCategory(),
                    car.getPlateNumber(),
                    car.getDailyRentPrice()
            });
        }
    }

    //Kiralama tablosu yenileme.
    private void refreshRentalTable(RentalTransaction.TransactionStatus status) {
        JTable selectedTable;
        if (status.equals(RentalTransaction.TransactionStatus.PENDING)) {
            selectedTable = pendingOrdersTable;
        } else if (status.equals(RentalTransaction.TransactionStatus.COMPLETED)) {
            selectedTable = approvedOrdersTable;
        } else if (status.equals(RentalTransaction.TransactionStatus.REJECTED)) {
            selectedTable = rejectedOrdersTable;
        } else {
            return;
        }
        DefaultTableModel tableModel = (DefaultTableModel) selectedTable.getModel();
        tableModel.setRowCount(0);

        try {
            List<RentalTransaction> rentals = RentalController.getMyRentalsByStatus(currentUser, status);
            for (RentalTransaction rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getPlateNumber(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus(),
                        rental.getTotalCost()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //Kiralama tablosu iptal butonu açma.
    private void openCancelRentalButtonModal() {
        int selectedRow = pendingOrdersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental to cancel pending request", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog cancelRentalModal = new JDialog(this, "Return Car", true);
        cancelRentalModal.setSize(300, 300);
        cancelRentalModal.setLocationRelativeTo(this);

        JPanel modalPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        modalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String carName = pendingOrdersTable.getValueAt(selectedRow, 1).toString();
        String transactionId = pendingOrdersTable.getValueAt(selectedRow, 0).toString();
        String carPlateNumber = pendingOrdersTable.getValueAt(selectedRow, 3).toString();

        JLabel confirmationLabel = new JLabel("Do you want to cancel rental?");
        JLabel carLabel = new JLabel("Car: " + carName);
        JLabel transactionLabel = new JLabel("Car Plate Number: " + carPlateNumber);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {

            try {
                Car car = RentalController.finishedRentalTransaction(UUID.fromString(transactionId));
                refreshCarTable();
                refreshRentalTable(RentalTransaction.TransactionStatus.PENDING);
                JOptionPane.showMessageDialog(cancelRentalModal, car.getPlateNumber() + " canceled rental successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                cancelRentalModal.dispose();
            } catch (Exception err) {
                JOptionPane.showMessageDialog(null,
                        err.getMessage(),
                        "Rental Error",
                        JOptionPane.ERROR_MESSAGE);
            }


        });

        cancelButton.addActionListener(e -> cancelRentalModal.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        modalPanel.add(confirmationLabel);
        modalPanel.add(carLabel);
        modalPanel.add(transactionLabel);
        modalPanel.add(buttonPanel);

        cancelRentalModal.add(modalPanel);
        cancelRentalModal.setVisible(true);


    }

    //Araba kiralama dialogu açma.
    private void openRentCarModal() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to rent", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog rentModal = new JDialog(this, "Rent Car", true);
        rentModal.setSize(400, 200);
        rentModal.setLocationRelativeTo(this);

        JPanel modalPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        modalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        modalPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        JTextField startDateField = new JTextField();
        startDateField.setPreferredSize(new Dimension(100, 25));
        modalPanel.add(startDateField);

        modalPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        JTextField endDateField = new JTextField();
        endDateField.setPreferredSize(new Dimension(100, 25));
        modalPanel.add(endDateField);

        JButton confirmRentButton = new JButton("Confirm Rent");
        confirmRentButton.setPreferredSize(new Dimension(120, 30));
        confirmRentButton.addActionListener(e -> {
            try {
                LocalDate startDate = LocalDate.parse(startDateField.getText());
                LocalDate endDate = LocalDate.parse(endDateField.getText());

                String carId = carsTable.getValueAt(carsTable.getSelectedRow(), 0).toString();

                String plateNumber = RentalController.startRental(UUID.fromString(carId), currentUser, startDate, endDate);
                refreshCarTable();
                refreshRentalTable(RentalTransaction.TransactionStatus.PENDING);
                refreshRentalTable(RentalTransaction.TransactionStatus.COMPLETED);

                String message = String.format("Rent Request:\nCar: %s\nCar ID: %s\nStart Date: %s\nEnd Date: %s",
                        carId, plateNumber, startDate, endDate);
                JOptionPane.showMessageDialog(rentModal, message, "Rent Confirmation", JOptionPane.INFORMATION_MESSAGE);

                rentModal.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(rentModal, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RentalException err) {
                JOptionPane.showMessageDialog(null,
                        err.getMessage(),
                        "Rental Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        modalPanel.add(new JLabel());
        modalPanel.add(confirmRentButton);

        rentModal.add(modalPanel);
        rentModal.setVisible(true);
    }

    //Araç bırakma işlemleri.
    private void openReturnCarModal() {
        int selectedRow = approvedOrdersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to return", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog returnModal = new JDialog(this, "Return Car", true);
        returnModal.setSize(300, 300);
        returnModal.setLocationRelativeTo(this);

        JPanel modalPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        modalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String carName = approvedOrdersTable.getValueAt(selectedRow, 1).toString();
        String transactionId = approvedOrdersTable.getValueAt(selectedRow, 0).toString();
        String carPlateNumber = approvedOrdersTable.getValueAt(selectedRow, 3).toString();

        JLabel confirmationLabel = new JLabel("Do you want to return the following car?");
        JLabel carLabel = new JLabel("Car: " + carName);
        JLabel transactionLabel = new JLabel("Car Plate Number: " + carPlateNumber);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("Confirm Return");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {

            try {
                Car car = RentalController.finishedRentalTransaction(UUID.fromString(transactionId));
                refreshCarTable();
                refreshRentalTable(RentalTransaction.TransactionStatus.COMPLETED);
                JOptionPane.showMessageDialog(returnModal, car.getPlateNumber() + " returned successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                returnModal.dispose();
            } catch (Exception err) {
                JOptionPane.showMessageDialog(null,
                        err.getMessage(),
                        "Rental Error",
                        JOptionPane.ERROR_MESSAGE);
            }


        });

        cancelButton.addActionListener(e -> returnModal.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        modalPanel.add(confirmationLabel);
        modalPanel.add(carLabel);
        modalPanel.add(transactionLabel);
        modalPanel.add(buttonPanel);

        returnModal.add(modalPanel);
        returnModal.setVisible(true);
    }

    //Araç kiralama işlemlerini uzatma.
    private void openExtendRentalTransactionsModal() {
        int selectedRow = approvedOrdersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to extend rental", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        JDialog rentModal = new JDialog(this, "Extend Rental", true);
        rentModal.setSize(400, 200);
        rentModal.setLocationRelativeTo(this);

        JPanel modalPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        modalPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String currentStartDate = approvedOrdersTable.getValueAt(approvedOrdersTable.getSelectedRow(), 4).toString();
        String currentEndDate = approvedOrdersTable.getValueAt(approvedOrdersTable.getSelectedRow(), 5).toString();
        modalPanel.add(new JLabel("Current End Date: " + currentEndDate));

        String price = approvedOrdersTable.getValueAt(approvedOrdersTable.getSelectedRow(), 7).toString();
        modalPanel.add(new JLabel("Current Price: " + price));

        modalPanel.add(new JLabel("New End Date (YYYY-MM-DD):"));
        JTextField endDateField = new JTextField();
        endDateField.setPreferredSize(new Dimension(100, 25));
        modalPanel.add(endDateField);

        JButton confirmRentButton = new JButton("Extend Rent");
        confirmRentButton.setPreferredSize(new Dimension(120, 30));
        confirmRentButton.addActionListener(e -> {
            try {
                LocalDate endDate = LocalDate.parse(endDateField.getText());

                String transactionId = approvedOrdersTable.getValueAt(approvedOrdersTable.getSelectedRow(), 0).toString();

                RentalTransaction rental = RentalController.extendRentalTransaction(UUID.fromString(transactionId), endDate);
                refreshRentalTable(RentalTransaction.TransactionStatus.COMPLETED);

                String message = String.format("Rent Request:\nCar: %s\nCar ID: %s\nStart Date: %s\nEnd Date: %s",
                        transactionId, rental.getRentedCar().getPlateNumber(), currentStartDate, endDate);
                JOptionPane.showMessageDialog(rentModal, message, "Extend Rent Confirmation", JOptionPane.INFORMATION_MESSAGE);


                rentModal.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(rentModal, "Invalid date format. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (RentalException err) {
                JOptionPane.showMessageDialog(null,
                        err.getMessage(),
                        "Rental Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });


        modalPanel.add(new JLabel());
        modalPanel.add(confirmRentButton);

        rentModal.add(modalPanel);
        rentModal.setVisible(true);
    }


}