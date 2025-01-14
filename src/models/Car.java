package models;

import java.time.LocalDate;
import java.util.UUID;

public abstract class Car {
    // Benzersiz araç kimliği
    private UUID carId;

    // Araç temel bilgileri
    private String plateNumber;
    private String brand;
    private String model;
    private CarGear gear;
    private FuelType fuelType;
    private int year;
    private int engineCapacity;


    // Araç durumu bilgileri
    private CarStatus status;
    private double dailyRentPrice;

    // Bakım ve performans bilgileri
    private int totalMileage;
    private LocalDate lastMaintenanceDate;

    // Araç kategorisi
    private CarCategory category;

    // Yakıt tipi enum'u
    public enum FuelType {
        GASOLINE, DIESEL, ELECTRIC, HYBRID, LPG
    }

    // Araç durumu enum'u
    public enum CarStatus {
        AVAILABLE, PENDING, RENTED, UNDER_MAINTENANCE, DAMAGED
    }

    public enum CarGear {
        AUTOMATIC, SEMIAUTOMATIC, MANUEL
    }

    // Araç kategorisi enum'u
    public enum CarCategory {
        PASSENGER, COMMERCIAL, SUV
    }

    public int getEngineCapacity() {
        return engineCapacity;
    }

    public int getTotalMileage() {
        return totalMileage;
    }

    public LocalDate getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    //Araba özelliklerini onaylama.
    private void applyCarProperties(String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
                                    double dailyRentPrice, int engineCapacity, CarCategory category) {
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.gear = gear;
        this.fuelType = fuelType;
        this.year = year;
        this.category = category;
        this.dailyRentPrice = dailyRentPrice;
        this.engineCapacity = engineCapacity;
    }


    public Car(String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
               double dailyRentPrice, int engineCapacity, CarCategory category) {
        this.carId = UUID.randomUUID(); // Benzersiz ID oluşturma
        this.status = CarStatus.AVAILABLE;
        this.totalMileage = 0;
        this.lastMaintenanceDate = LocalDate.now();
        applyCarProperties(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity, category);
    }

    public Car(UUID carId, CarStatus status, int totalMileage, LocalDate lastMaintenanceDate, String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
               double dailyRentPrice, int engineCapacity, CarCategory category) {
        this.carId = carId;
        this.status = status;
        this.totalMileage = totalMileage;
        this.lastMaintenanceDate = lastMaintenanceDate;
        applyCarProperties(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice, engineCapacity, category);
    }


    //Abstract metodlar

    public abstract double calculateRentCost(int days, Car.FuelType fuelType);

    public abstract void performMaintenance();

    public abstract boolean isEligibleForRent();

    // Gelişmiş getter ve setter metodları
    public UUID getCarId() {
        return carId;
    }

    public CarStatus getStatus() {
        return status;
    }

    public void setStatus(CarStatus status) {
        this.status = status;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setGear(CarGear gear) {
        this.gear = gear;
    }

    public void setDailyRentPrice(double dailyRentPrice) {
        this.dailyRentPrice = dailyRentPrice;
    }

    public void setEngineCapacity(int engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCategory(CarCategory category) {
        this.category = category;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public void setLastMaintenanceDate(LocalDate lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public void setTotalMileage(int totalMileage) {
        this.totalMileage = totalMileage;
    }

    // Diğer getter ve setter metodları
    public String getPlateNumber() {
        return plateNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public CarGear getGear() {
        return gear;
    }

    public CarCategory getCategory() {
        return category;
    }

    public int getYear() {
        return year;
    }

    public double getDailyRentPrice() {
        return dailyRentPrice;
    }

    // Kilometre güncelleme metodu
    public void updateMileage(int additionalMileage) {
        if (additionalMileage > 0) {
            this.totalMileage += additionalMileage;

            // Bakım kontrolü
            if (totalMileage % 10000 == 0) {
                performMaintenance();
            }
        }
    }
}