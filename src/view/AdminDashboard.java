package view;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import controller.AuthController;
import controller.CarController;
import controller.RentalController;
import exceptions.RentalException;
import models.*;

public class AdminDashboard extends JFrame {
    private JTabbedPane tabbedPane;

    //Paneller
    private JPanel carsPanel;
    private JPanel userPanel;
    private JPanel rentalRequestsPanel;

    //Tablolar
    private JTable carsTable;
    private JTable usersTable;
    private JTable rentalRequestsTable;

    //Butonlar
    private JButton searchCarButton;
    private JButton addCarButton;
    private JButton editCarButton;
    private JButton deleteCarButton;
    private JButton viewCarButton;

    private JButton addUserButton;
    private JButton editUserButton;
    private JButton deleteUserButton;

    private JButton approveRequestButton;
    private JButton rejectRequestButton;

    //Arama Texti
    private JTextField carSearchField;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        addMenuBar();
        initComponents();
        setupLayout();
    }

    //Menü bar ekleme.
    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu adminMenu = new JMenu("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");

        logoutItem.addActionListener(e -> {
            // Return to login page
            LoginPage loginPage = new LoginPage();
            loginPage.setVisible(true);
            this.dispose();
        });

        adminMenu.add(logoutItem);
        menuBar.add(adminMenu);
        setJMenuBar(menuBar);
    }

    //Bileşenleri başlatmak.
    private void initComponents() {
        //Tabbed pane başlatmak.
        tabbedPane = new JTabbedPane();

        //Arabalar paneli
        initCarsPanel();

        //Kullanıcıların paneli
        initUsersPanel();

        //Kiralama istek paneli
        initRentalRequestsPanel();
    }

    //Araba paneli başlatmak.
    private void initCarsPanel() {
        carsPanel = new JPanel(new BorderLayout());

        //Araba tablo modeli
        Vector<String> carColumnNames = new Vector<>();
        carColumnNames.add("ID");
        carColumnNames.add("Plate Number");
        carColumnNames.add("Brand");
        carColumnNames.add("Model");
        carColumnNames.add("Gear");
        carColumnNames.add("Fuel Type");
        carColumnNames.add("Year");
        carColumnNames.add("Category");
        carColumnNames.add("Daily Rent Price");

        DefaultTableModel carTableModel = new DefaultTableModel(carColumnNames, 0);
        carsTable = new JTable(carTableModel);

        List<Car> cars = CarController.getAllCars();
        for (Car car : cars) {
            carTableModel.addRow(new Object[]{
                    car.getCarId(),
                    car.getPlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getGear(),
                    car.getFuelType(),
                    car.getYear(),
                    car.getCategory(),
                    car.getDailyRentPrice()
            });
        }

        //Tabloya Scroll Pane ekleme
        JScrollPane carsScrollPane = new JScrollPane(carsTable);
        carsPanel.add(carsScrollPane, BorderLayout.CENTER);

        //Paneldeki butonlar
        JPanel carButtonPanel = new JPanel(new FlowLayout());
        searchCarButton = new JButton("Search");
        addCarButton = new JButton("Add Car");
        editCarButton = new JButton("Edit Car");
        deleteCarButton = new JButton("Delete Car");
        viewCarButton = new JButton("View Car");

        carSearchField = new JTextField(20);
        carSearchField.setToolTipText("Search Cars");

        carButtonPanel.add(carSearchField);
        carButtonPanel.add(searchCarButton);
        carButtonPanel.add(addCarButton);
        carButtonPanel.add(editCarButton);
        carButtonPanel.add(deleteCarButton);
        carButtonPanel.add(viewCarButton);

        carsPanel.add(carButtonPanel, BorderLayout.SOUTH);

        //Buton aktifliği.
        addCarButton.addActionListener(e -> showAddCarDialog());
        editCarButton.addActionListener(e -> showEditCarDialog());
        deleteCarButton.addActionListener(e -> showDeleteCarConfirmation());
        viewCarButton.addActionListener(e -> showCarDetails());
        searchCarButton.addActionListener(e -> searchCar());


        tabbedPane.addTab("Cars", carsPanel);
    }

    //Kullanıcı tablosunu yenileme.
    private void refreshUserTable() {
        DefaultTableModel usersTableModel = (DefaultTableModel) usersTable.getModel();
        usersTableModel.setRowCount(0);

        List<User> users = AuthController.getAllUsers();
        for (User user : users) {
            usersTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getRegistrationDate(),
            });
        }
    }

    //Kullanıcı panelini başlatmak.
    private void initUsersPanel() {
        userPanel = new JPanel(new BorderLayout());

        //Kullanıcı tablo modeli
        Vector<String> userColumnNames = new Vector<>();
        userColumnNames.add("User ID");
        userColumnNames.add("Username");
        userColumnNames.add("Email");
        userColumnNames.add("Role");
        userColumnNames.add("Registration Date");

        DefaultTableModel userTableModel = new DefaultTableModel(userColumnNames, 0);
        usersTable = new JTable(userTableModel);


        List<User> users = AuthController.getAllUsers();
        for (User user : users) {
            userTableModel.addRow(new Object[]{
                    user.getUserId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole(),
                    user.getRegistrationDate(),
            });
        }

        //Tabloya Scroll Pane eklemek.
        JScrollPane usersScrollPane = new JScrollPane(usersTable);
        userPanel.add(usersScrollPane, BorderLayout.CENTER);

        //Panele buton ekleme
        JPanel userButtonPanel = new JPanel(new FlowLayout());
        addUserButton = new JButton("Add User");
        editUserButton = new JButton("Edit User");
        deleteUserButton = new JButton("Delete User");


        userButtonPanel.add(addUserButton);
        userButtonPanel.add(editUserButton);
        userButtonPanel.add(deleteUserButton);

        userPanel.add(userButtonPanel, BorderLayout.SOUTH);

        //Butonların aktifliği.
        addUserButton.addActionListener(e -> showAddUserDialog());
        editUserButton.addActionListener(e -> showEditUserDialog());
        deleteUserButton.addActionListener(e -> showDeleteUserConfirmation());

        tabbedPane.addTab("Users", userPanel);
    }

    //Kiralama tablosunu yenileme.
    private void refreshRentalTable() {
        DefaultTableModel tableModel = (DefaultTableModel) rentalRequestsTable.getModel();
        tableModel.setRowCount(0);
        try {
            List<RentalTransaction> rentals = RentalController.getPendingRentals();
            for (RentalTransaction rental : rentals) {
                tableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getGear(),
                        rental.getRentedCar().getFuelType(),
                        rental.getRenter().getUsername(),
                        rental.getRenter().getEmail(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getTotalCost(),
                        rental.getStatus()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //Araba tablosunu yenilemek
    private void refreshCarTable() {
        DefaultTableModel tableModel = (DefaultTableModel) carsTable.getModel();
        tableModel.setRowCount(0);

        List<Car> cars = CarController.getAllCars();
        for (Car car : cars) {
            tableModel.addRow(new Object[]{
                    car.getCarId(),
                    car.getPlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getGear(),
                    car.getFuelType(),
                    car.getYear(),
                    car.getCategory(),
                    car.getDailyRentPrice()
            });
        }
    }

    //Kiralama istek panelini başlatma
    private void initRentalRequestsPanel() {
        rentalRequestsPanel = new JPanel(new BorderLayout());

        //Kiralama istek tablosu
        Vector<String> requestColumnNames = new Vector<>();
        requestColumnNames.add("Transaction ID");
        requestColumnNames.add("Car Brand");
        requestColumnNames.add("Car Model");
        requestColumnNames.add("Car Gear");
        requestColumnNames.add("Car Fuel Type");
        requestColumnNames.add("Renter Name");
        requestColumnNames.add("Renter Mail");
        requestColumnNames.add("Start Date");
        requestColumnNames.add("End Date");
        requestColumnNames.add("Total Cost");
        requestColumnNames.add("Status");

        DefaultTableModel requestTableModel = new DefaultTableModel(requestColumnNames, 0);
        rentalRequestsTable = new JTable(requestTableModel);

        try {
            List<RentalTransaction> rentals = RentalController.getPendingRentals();
            for (RentalTransaction rental : rentals) {
                requestTableModel.addRow(new Object[]{
                        rental.getTransactionId(),
                        rental.getRentedCar().getBrand(),
                        rental.getRentedCar().getModel(),
                        rental.getRentedCar().getGear(),
                        rental.getRentedCar().getFuelType(),
                        rental.getRenter().getUsername(),
                        rental.getRenter().getEmail(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getTotalCost(),
                        rental.getStatus()
                });
            }

        } catch (RentalException err) {
            JOptionPane.showMessageDialog(null,
                    err.getMessage(),
                    "Rental Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        //Tabloya Scroll Pane ekleme
        JScrollPane requestsScrollPane = new JScrollPane(rentalRequestsTable);
        rentalRequestsPanel.add(requestsScrollPane, BorderLayout.CENTER);

        //Paneldeki butonları tanımlama
        JPanel requestButtonPanel = new JPanel(new FlowLayout());
        approveRequestButton = new JButton("Approve Request");
        rejectRequestButton = new JButton("Reject Request");

        requestButtonPanel.add(approveRequestButton);
        requestButtonPanel.add(rejectRequestButton);

        rentalRequestsPanel.add(requestButtonPanel, BorderLayout.SOUTH);

        //Butonların aktifliği
        approveRequestButton.addActionListener(e -> approveRentalRequest());
        rejectRequestButton.addActionListener(e -> rejectRentalRequest());

        tabbedPane.addTab("Rental Requests", rentalRequestsPanel);
    }

    private void setupLayout() {
        add(tabbedPane);
    }

    //Araç ekleme işlemlerinin yapıldığı dialog üzerinden yapan metot.
    private void showAddCarDialog() {
        JDialog addCarDialog = new JDialog(this, "Add New Car", true);
        addCarDialog.setSize(400, 600);

        //Panel oluşturma.
        JPanel addCarPanel = new JPanel(new GridLayout(0, 2));

        //Genel değişkenleri oluşturma
        JTextField plateNumberField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField modelField = new JTextField();
        JComboBox<Car.CarGear> gearTypeComboBox = new JComboBox<>(Car.CarGear.values());
        JComboBox<Car.FuelType> fuelTypeComboBox = new JComboBox<>(Car.FuelType.values());
        JTextField yearField = new JTextField();
        JTextField dailyRentPriceField = new JTextField();
        JTextField engineCapacityField = new JTextField();

        //Araç tipi oluşturma.
        JComboBox<String> carTypeComboBox = new JComboBox<>(new String[]{"Passenger", "Commercial", "SUV"});
        carTypeComboBox.setSelectedItem("Passenger");


        //Hususi araçlar için özel özellikler.
        JTextField passengerCapacityField = new JTextField();
        JCheckBox airConditioningCheckBox = new JCheckBox();
        JComboBox<PassengerCar.CarComfort> comfortLevelComboBox = new JComboBox<>(PassengerCar.CarComfort.values());

        //Ticari araçlar için özel özellikler.
        JTextField loadCapacityField = new JTextField();
        JCheckBox coolingSystemCheckBox = new JCheckBox();
        JTextField fuelConsumptionRateField = new JTextField();

        //Suv araçlar için özel özellikler.
        JTextField groundClearanceField = new JTextField();
        JCheckBox hasAllWheelDriveCheckbox = new JCheckBox();
        JComboBox<SUV.TerrainMode> terrainModeComboBox = new JComboBox<>(SUV.TerrainMode.values());


        addCarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        //Panele değişkenleri ekleme.
        addCarPanel.add(new JLabel("Plate Number:"));
        addCarPanel.add(plateNumberField);
        addCarPanel.add(new JLabel("Brand:"));
        addCarPanel.add(brandField);
        addCarPanel.add(new JLabel("Model:"));
        addCarPanel.add(modelField);
        addCarPanel.add(new JLabel("Gear:"));
        addCarPanel.add(gearTypeComboBox);
        addCarPanel.add(new JLabel("Fuel Type:"));
        addCarPanel.add(fuelTypeComboBox);
        addCarPanel.add(new JLabel("Year:"));
        addCarPanel.add(yearField);
        addCarPanel.add(new JLabel("Daily Rent Price:"));
        addCarPanel.add(dailyRentPriceField);
        addCarPanel.add(new JLabel("Engine Capacity:"));
        addCarPanel.add(engineCapacityField);
        addCarPanel.add(new JLabel("Car Type:"));
        addCarPanel.add(carTypeComboBox);

        //Dinamik panel oluşturma
        JPanel dynamicPanel = new JPanel(new GridLayout(0, 1));
        dynamicPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        carTypeComboBox.addActionListener(e -> {
            dynamicPanel.removeAll();
            if (carTypeComboBox.getSelectedItem().equals("Passenger")) {
                dynamicPanel.add(new JLabel("Passenger Capacity:"));
                dynamicPanel.add(passengerCapacityField);
                dynamicPanel.add(new JLabel("Air Conditioning:"));
                dynamicPanel.add(airConditioningCheckBox);
                dynamicPanel.add(new JLabel("Comfort Level:"));
                dynamicPanel.add(comfortLevelComboBox);
            } else if (carTypeComboBox.getSelectedItem().equals("Commercial")) {
                JPanel commercialVehiclePanel = new JPanel();
                JPanel loadCapacityPanel = new JPanel();
                JPanel coolingPanel = new JPanel();
                JPanel fuelConsumptionRatePanel = new JPanel();
                commercialVehiclePanel.setLayout(new BorderLayout());
                coolingPanel.setLayout(new BorderLayout());
                loadCapacityPanel.setLayout(new BorderLayout());
                fuelConsumptionRatePanel.setLayout(new BorderLayout());
                dynamicPanel.add(commercialVehiclePanel);
                commercialVehiclePanel.add(loadCapacityPanel, BorderLayout.NORTH);
                commercialVehiclePanel.add(coolingPanel, BorderLayout.CENTER);
                commercialVehiclePanel.add(fuelConsumptionRatePanel, BorderLayout.SOUTH);
                loadCapacityPanel.add(new JLabel("Load Capacity:"), BorderLayout.WEST);
                loadCapacityPanel.add(loadCapacityField, BorderLayout.CENTER);
                coolingPanel.add(new JLabel("Cooling System:"), BorderLayout.WEST);
                coolingPanel.add(coolingSystemCheckBox, BorderLayout.CENTER);
                fuelConsumptionRatePanel.add(new JLabel("Fuel Consumption Rate"), BorderLayout.WEST);
                fuelConsumptionRatePanel.add(fuelConsumptionRateField, BorderLayout.CENTER);

            } else if (carTypeComboBox.getSelectedItem().equals("SUV")) {
                dynamicPanel.add(new JLabel("All Wheel Drive:"));
                dynamicPanel.add(hasAllWheelDriveCheckbox);
                dynamicPanel.add(new JLabel("Terrain Mode:"));
                dynamicPanel.add(terrainModeComboBox);
                dynamicPanel.add(new JLabel("Ground clearance"));
                dynamicPanel.add(groundClearanceField);
            }
            dynamicPanel.revalidate();
            dynamicPanel.repaint();
        });

        carTypeComboBox.getActionListeners()[0].actionPerformed(null);
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {

            String plateNumber = plateNumberField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            Car.CarGear gear = (Car.CarGear) gearTypeComboBox.getSelectedItem();
            Car.FuelType fuelType = (Car.FuelType) fuelTypeComboBox.getSelectedItem();
            int year = Integer.parseInt(yearField.getText());
            double dailyRentPrice = Double.parseDouble(dailyRentPriceField.getText());
            int engineCapacity = Integer.parseInt(engineCapacityField.getText());

            Car newCar;


            try {
                if (carTypeComboBox.getSelectedItem().equals("Passenger")) {
                    int passengerCapacity = Integer.parseInt(passengerCapacityField.getText());
                    Boolean hasAirConditioning = airConditioningCheckBox.isSelected();
                    PassengerCar.CarComfort comfortLevel = (PassengerCar.CarComfort) comfortLevelComboBox.getSelectedItem();
                    newCar = new PassengerCar(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity,
                            passengerCapacity, hasAirConditioning, comfortLevel);
                } else if (carTypeComboBox.getSelectedItem().equals("Commercial")) {
                    double loadCapacity = Double.parseDouble(loadCapacityField.getText());
                    Boolean hasCoolingSystem = coolingSystemCheckBox.isSelected();
                    double fuelConsumptionRate = Double.parseDouble(fuelConsumptionRateField.getText());
                    newCar = new CommercialVehicle(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity,
                            loadCapacity, hasCoolingSystem, fuelConsumptionRate);
                } else if (carTypeComboBox.getSelectedItem().equals("SUV")) {
                    int groundClearance = Integer.parseInt(groundClearanceField.getText());
                    Boolean hasAllWheelDrive = hasAllWheelDriveCheckbox.isSelected();
                    SUV.TerrainMode terrainMode = (SUV.TerrainMode) terrainModeComboBox.getSelectedItem();
                    newCar = new SUV(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity, hasAllWheelDrive, groundClearance, terrainMode);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Please select a vehicle type",
                            "Add Car Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                CarController.addNewCar(newCar);
                refreshCarTable();
                addCarDialog.dispose();

            } catch (Exception err) {
                JOptionPane.showMessageDialog(this,
                        err.getMessage(),
                        "Add Car Error",
                        JOptionPane.ERROR_MESSAGE);
            }


        });

        addCarDialog.setLayout(new BorderLayout());
        addCarDialog.add(addCarPanel, BorderLayout.NORTH);
        addCarDialog.add(dynamicPanel, BorderLayout.CENTER);
        addCarDialog.add(saveButton, BorderLayout.SOUTH);

        addCarDialog.setLocationRelativeTo(this);
        addCarDialog.setVisible(true);
    }

    //Araç düzenleme işlemlerinin yapıldığı dialog üzerinden yapan metot.
    private void showEditCarDialog() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String carId = carsTable.getValueAt(selectedRow, 0).toString();
        Car car = CarController.getSelectedCarById(UUID.fromString(carId));

        JDialog editCarDialog = new JDialog(this, "Edit Car", true);
        editCarDialog.setSize(400, 600);

        //Panel oluşturma
        JPanel editCarPanel = new JPanel(new GridLayout(0, 2));

        //Genel değişkenleri oluşturma.
        JTextField plateNumberField = new JTextField(car.getPlateNumber());
        JTextField brandField = new JTextField(car.getBrand());
        JTextField modelField = new JTextField(car.getModel());
        JComboBox<Car.CarGear> gearTypeComboBox = new JComboBox<>(Car.CarGear.values());
        JComboBox<Car.FuelType> fuelTypeComboBox = new JComboBox<>(Car.FuelType.values());
        JTextField yearField = new JTextField(String.valueOf(car.getYear()));
        JTextField dailyRentPriceField = new JTextField(String.valueOf(car.getDailyRentPrice()));
        JTextField engineCapacityField = new JTextField(String.valueOf(car.getEngineCapacity()));
        gearTypeComboBox.setSelectedItem(car.getGear());
        fuelTypeComboBox.setSelectedItem(car.getFuelType());

        //Araç tipi seçme.
        JComboBox<String> carTypeComboBox = new JComboBox<>(new String[]{"Passenger", "Commercial", "SUV"});
        carTypeComboBox.setSelectedItem(car instanceof PassengerCar ? "Passenger" : car instanceof CommercialVehicle ? "Commercial" : "SUV");
        carTypeComboBox.setEnabled(false);

        //Hususi araç için özel değişken oluşturma.
        JTextField passengerCapacityField = new JTextField();
        JCheckBox airConditioningCheckBox = new JCheckBox();
        JComboBox<PassengerCar.CarComfort> comfortLevelComboBox = new JComboBox<>(PassengerCar.CarComfort.values());

        //Ticari araç için özel değişken oluşturma.
        JTextField loadCapacityField = new JTextField();
        JCheckBox coolingSystemCheckBox = new JCheckBox();
        JTextField fuelConsumptionRateField = new JTextField();

        //SUV araç için özel değişken oluşturma.
        JTextField groundClearanceField = new JTextField();
        JCheckBox hasAllWheelDriveCheckbox = new JCheckBox();
        JComboBox<SUV.TerrainMode> terrainModeComboBox = new JComboBox<>(SUV.TerrainMode.values());

        editCarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Panele değişkenleri ekleme.
        editCarPanel.add(new JLabel("Plate Number:"));
        editCarPanel.add(plateNumberField);
        editCarPanel.add(new JLabel("Brand:"));
        editCarPanel.add(brandField);
        editCarPanel.add(new JLabel("Model:"));
        editCarPanel.add(modelField);
        editCarPanel.add(new JLabel("Gear:"));
        editCarPanel.add(gearTypeComboBox);
        editCarPanel.add(new JLabel("Fuel Type"));
        editCarPanel.add(fuelTypeComboBox);
        editCarPanel.add(new JLabel("Year:"));
        editCarPanel.add(yearField);
        editCarPanel.add(new JLabel("Daily Rent Price:"));
        editCarPanel.add(dailyRentPriceField);
        editCarPanel.add(new JLabel("Engine Capacity:"));
        editCarPanel.add(engineCapacityField);
        editCarPanel.add(new JLabel("Car Type:"));
        editCarPanel.add(carTypeComboBox);

        //Dinamik panel oluşturmaç
        JPanel dynamicPanel = new JPanel(new GridLayout(0, 1));
        dynamicPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        carTypeComboBox.addActionListener(e -> {
            dynamicPanel.removeAll();
            if (carTypeComboBox.getSelectedItem().equals("Passenger")) {
                if (car instanceof PassengerCar) {
                    PassengerCar passengerCar = (PassengerCar) car;
                    passengerCapacityField.setText(String.valueOf(passengerCar.getPassengerCapacity()));
                    airConditioningCheckBox.setSelected(passengerCar.isHasAirConditioning());
                    comfortLevelComboBox.setSelectedItem(passengerCar.getComfortLevel());
                }
                dynamicPanel.add(new JLabel("Passenger Capacity:"));
                dynamicPanel.add(passengerCapacityField);
                dynamicPanel.add(new JLabel("Air Conditioning:"));
                dynamicPanel.add(airConditioningCheckBox);
                dynamicPanel.add(new JLabel("Comfort Level"));
                dynamicPanel.add(comfortLevelComboBox);
            } else if (carTypeComboBox.getSelectedItem().equals("Commercial")) {
                if (car instanceof CommercialVehicle) {
                    CommercialVehicle commercialVehicle = (CommercialVehicle) car;
                    loadCapacityField.setText(String.valueOf(commercialVehicle.getLoadCapacity()));
                    coolingSystemCheckBox.setSelected(commercialVehicle.isHasCoolingSystem());
                    fuelConsumptionRateField.setText(String.valueOf(commercialVehicle.getFuelConsumptionRate()));
                }
                JPanel commercialVehiclePanel = new JPanel();
                JPanel loadCapacityPanel = new JPanel();
                JPanel coolingPanel = new JPanel();
                JPanel fuelConsumptionRatePanel = new JPanel();
                commercialVehiclePanel.setLayout(new BorderLayout());
                coolingPanel.setLayout(new BorderLayout());
                loadCapacityPanel.setLayout(new BorderLayout());
                fuelConsumptionRatePanel.setLayout(new BorderLayout());
                dynamicPanel.add(commercialVehiclePanel);
                commercialVehiclePanel.add(loadCapacityPanel, BorderLayout.NORTH);
                commercialVehiclePanel.add(coolingPanel, BorderLayout.CENTER);
                commercialVehiclePanel.add(fuelConsumptionRatePanel, BorderLayout.SOUTH);
                loadCapacityPanel.add(new JLabel("Load Capacity:"), BorderLayout.WEST);
                loadCapacityPanel.add(loadCapacityField, BorderLayout.CENTER);
                coolingPanel.add(new JLabel("Cooling System:"), BorderLayout.WEST);
                coolingPanel.add(coolingSystemCheckBox, BorderLayout.CENTER);
                fuelConsumptionRatePanel.add(new JLabel("Fuel Consumption Rate"), BorderLayout.WEST);
                fuelConsumptionRatePanel.add(fuelConsumptionRateField, BorderLayout.CENTER);
            } else if (carTypeComboBox.getSelectedItem().equals("SUV")) {
                if (car instanceof SUV) {
                    SUV suv = (SUV) car;
                    hasAllWheelDriveCheckbox.setSelected(suv.isHasAllWheelDrive());
                    terrainModeComboBox.setSelectedItem(suv.getTerrainMode());
                    groundClearanceField.setText(String.valueOf(suv.getGroundClearance()));
                }
                dynamicPanel.add(new JLabel("All Wheel Drive:"));
                dynamicPanel.add(hasAllWheelDriveCheckbox);
                dynamicPanel.add(new JLabel("Terrain Mode:"));
                dynamicPanel.add(terrainModeComboBox);
                dynamicPanel.add(new JLabel("Ground clearance"));
                dynamicPanel.add(groundClearanceField);
            }
            dynamicPanel.revalidate();
            dynamicPanel.repaint();
        });

        carTypeComboBox.getActionListeners()[0].actionPerformed(null);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {

            String plateNumber = plateNumberField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            Car.CarGear gear = (Car.CarGear) gearTypeComboBox.getSelectedItem();
            Car.FuelType fuelType = (Car.FuelType) fuelTypeComboBox.getSelectedItem();
            int year = Integer.parseInt(yearField.getText());
            double dailyRentPrice = Double.parseDouble(dailyRentPriceField.getText());
            int engineCapacity = Integer.parseInt(engineCapacityField.getText());

            Car newCar;
            if (car instanceof PassengerCar) {
                int passengerCapacity = Integer.parseInt(passengerCapacityField.getText());
                Boolean hasAirConditioning = airConditioningCheckBox.isSelected();
                PassengerCar.CarComfort comfortLevel = (PassengerCar.CarComfort) comfortLevelComboBox.getSelectedItem();
                newCar = new PassengerCar(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity,
                        passengerCapacity, hasAirConditioning, comfortLevel);
            } else if (car instanceof CommercialVehicle) {
                double loadCapacity = Double.parseDouble(loadCapacityField.getText());
                Boolean hasCoolingSystem = coolingSystemCheckBox.isSelected();
                double fuelConsumptionRate = Double.parseDouble(fuelConsumptionRateField.getText());
                newCar = new CommercialVehicle(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity,
                        loadCapacity, hasCoolingSystem, fuelConsumptionRate);
            } else if (car instanceof SUV) {
                int groundClearance = Integer.parseInt(groundClearanceField.getText());
                Boolean hasAllWheelDrive = hasAllWheelDriveCheckbox.isSelected();
                SUV.TerrainMode terrainMode = (SUV.TerrainMode) terrainModeComboBox.getSelectedItem();
                newCar = new SUV(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity, hasAllWheelDrive, groundClearance, terrainMode);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Please select a vehicle type",
                        "Add Car Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                CarController.updateCar(newCar, car.getPlateNumber());
                refreshCarTable();
                editCarDialog.dispose();
            } catch (Exception err) {
                JOptionPane.showMessageDialog(this,
                        err.getMessage(),
                        "Add Car Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        });

        editCarDialog.setLayout(new BorderLayout());
        editCarDialog.add(editCarPanel, BorderLayout.NORTH);
        editCarDialog.add(dynamicPanel, BorderLayout.CENTER);
        editCarDialog.add(saveButton, BorderLayout.SOUTH);

        editCarDialog.setLocationRelativeTo(this);
        editCarDialog.setVisible(true);
    }

    //Araç silme işlemi için mesaj.
    private void showDeleteCarConfirmation() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this car?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            String carId = carsTable.getValueAt(selectedRow, 0).toString();

            Car car = CarController.getSelectedCarById(UUID.fromString(carId));
            if (car.getStatus().equals(Car.CarStatus.AVAILABLE)) {
                CarController.deleteCar(car.getPlateNumber());
                refreshCarTable();
            } else {
                JOptionPane.showMessageDialog(this, "The selected vehicle is currently rented. You can delete the vehicle when available.", "Delete Car Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Araba detayları gösterme.
    private void showCarDetails() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to view details", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String carId = carsTable.getValueAt(selectedRow, 0).toString();

        Car car = CarController.getSelectedCarById(UUID.fromString(carId));

        //Dialog oluşturma.
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
        detailsPanel.add(new JLabel("Gear:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getGear())));
        detailsPanel.add(new JLabel("Fuel Type:"));
        detailsPanel.add(new JLabel(String.valueOf(car.getFuelType())));
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

    //Kullanıcı oluşturma için dialog oluşturma.
    private void showAddUserDialog() {
        JDialog addUserDialog = new JDialog(this, "Add New User", true);
        addUserDialog.setSize(400, 300);

        JPanel addUserPanel = new JPanel(new GridLayout(0, 2));

        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());

        addUserPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        addUserPanel.add(new JLabel("Username:"));
        addUserPanel.add(usernameField);
        addUserPanel.add(new JLabel("Email:"));
        addUserPanel.add(emailField);
        addUserPanel.add(new JLabel("Password:"));
        addUserPanel.add(passwordField);
        addUserPanel.add(new JLabel("Role:"));
        addUserPanel.add(roleComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String username = usernameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            User.UserRole role = (User.UserRole) roleComboBox.getSelectedItem();

            try {
                AuthController.register(username, email, password, role);
                refreshUserTable();
            } catch (IllegalArgumentException err) {
                JOptionPane.showMessageDialog(this, err.getMessage(), "Add User Error", JOptionPane.ERROR_MESSAGE);
            }
            addUserDialog.dispose();
        });

        addUserDialog.setLayout(new BorderLayout());
        addUserDialog.add(addUserPanel, BorderLayout.NORTH);
        addUserDialog.add(saveButton, BorderLayout.SOUTH);

        addUserDialog.setLocationRelativeTo(this);
        addUserDialog.setVisible(true);
    }

    //Kullanıcı düzenleme için dialog oluşturma.
    private void showEditUserDialog() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        String userId = usersTable.getValueAt(selectedRow, 0).toString();
        String username = usersTable.getValueAt(selectedRow, 1).toString();
        String role = usersTable.getValueAt(selectedRow, 3).toString();

        JDialog editUserDialog = new JDialog(this, "Edit User", true);
        editUserDialog.setSize(400, 300);

        JPanel editUserPanel = new JPanel(new GridLayout(0, 2));

        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField();
        JComboBox<User.UserRole> roleComboBox = new JComboBox<>(User.UserRole.values());
        roleComboBox.setSelectedItem(User.UserRole.valueOf(role));

        editUserPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        editUserPanel.add(new JLabel("Username:"));
        editUserPanel.add(usernameField);
        editUserPanel.add(new JLabel("New Password (optional):"));
        editUserPanel.add(passwordField);
        editUserPanel.add(new JLabel("Role:"));
        editUserPanel.add(roleComboBox);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            String newUsername = usernameField.getText();
            String password = new String(passwordField.getPassword());
            User.UserRole roleChoice = (User.UserRole) roleComboBox.getSelectedItem();

            try {
                User user = AuthController.updateUserInfosByUserId(UUID.fromString(userId), newUsername, password, roleChoice);
                refreshUserTable();
                JOptionPane.showMessageDialog(null,
                        "Update Account Successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);


            } catch (Exception err) {
                JOptionPane.showMessageDialog(null,
                        err.getMessage(),
                        "Update Account Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            editUserDialog.dispose();
        });

        editUserDialog.setLayout(new BorderLayout());
        editUserDialog.add(editUserPanel, BorderLayout.NORTH);
        editUserDialog.add(saveButton, BorderLayout.SOUTH);

        editUserDialog.setLocationRelativeTo(this);
        editUserDialog.setVisible(true);
    }

    //Kullanıcı silme işlemi için mesaj oluşturma.
    private void showDeleteUserConfirmation() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this user?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String userId = usersTable.getValueAt(selectedRow, 0).toString();
                AuthController.deleteUser(UUID.fromString(userId));
                refreshUserTable();
            } catch (IllegalArgumentException | RentalException err) {
                JOptionPane.showMessageDialog(this, err.getMessage(), "Delete Account Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Kiralama isteği kabul etme.
    private void approveRentalRequest() {
        int selectedRow = rentalRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental request", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String transactionId = rentalRequestsTable.getValueAt(selectedRow, 0).toString();
            RentalController.evalRental(UUID.fromString(transactionId), true);
            JOptionPane.showMessageDialog(this, " Sucsessfully approved rental request operations ", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshRentalTable();
        } catch (RentalException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Rental Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    //Kiralam isteği reddetme.
    private void rejectRentalRequest() {
        int selectedRow = rentalRequestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a rental request", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String transactionId = rentalRequestsTable.getValueAt(selectedRow, 0).toString();
            RentalController.evalRental(UUID.fromString(transactionId), false);
            JOptionPane.showMessageDialog(this, " Sucsessfully rejected rental request operations ", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshRentalTable();
        } catch (RentalException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Rental Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    //Araç arama
    private void searchCar() {
        String query = carSearchField.getText();

        DefaultTableModel tableModel = (DefaultTableModel) carsTable.getModel();
        tableModel.setRowCount(0);

        List<Car> cars = CarController.getSearchedCars(query);
        for (Car car : cars) {
            tableModel.addRow(new Object[]{
                    car.getCarId(),
                    car.getPlateNumber(),
                    car.getBrand(),
                    car.getModel(),
                    car.getGear(),
                    car.getFuelType(),
                    car.getYear(),
                    car.getCategory(),
                    car.getDailyRentPrice()
            });
        }

    }


}