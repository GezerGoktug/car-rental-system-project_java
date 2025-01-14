package models;

import java.time.LocalDate;
import java.util.UUID;

public class CommercialVehicle extends Car {
    private double loadCapacity; // Kg cinsinden
    private boolean hasCoolingSystem;
    private double fuelConsumptionRate;

    public double getLoadCapacity() {
        return loadCapacity;
    }

    public double getFuelConsumptionRate() {
        return fuelConsumptionRate;
    }

    public void setFuelConsumptionRate(double fuelConsumptionRate) {
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public boolean isHasCoolingSystem() {
        return hasCoolingSystem;
    }

    public void setLoadCapacity(double loadCapacity) {
        this.loadCapacity = loadCapacity;
    }

    public void setHasCoolingSystem(boolean hasCoolingSystem) {
        this.hasCoolingSystem = hasCoolingSystem;
    }

    public CommercialVehicle(String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
                             double dailyRentPrice, int engineCapacity,
                             double loadCapacity, boolean hasCoolingSystem, double fuelConsumptionRate) {
        super(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice,
                engineCapacity, CarCategory.COMMERCIAL);
        this.loadCapacity = loadCapacity;
        this.hasCoolingSystem = hasCoolingSystem;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    public CommercialVehicle(UUID carId, CarStatus status, int totalMileage, LocalDate lastMaintenanceDate, String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
                             double dailyRentPrice, int engineCapacity,
                             double loadCapacity, boolean hasCoolingSystem, double fuelConsumptionRate) {
        super(carId, status, totalMileage, lastMaintenanceDate, plateNumber, brand, model, gear, fuelType, year, dailyRentPrice,
                engineCapacity, CarCategory.COMMERCIAL);
        this.loadCapacity = loadCapacity;
        this.hasCoolingSystem = hasCoolingSystem;
        this.fuelConsumptionRate = fuelConsumptionRate;
    }

    //Toplam kiralama bedelini hesaplama.
    @Override
    public double calculateRentCost(int days, FuelType fuelType) {
        double baseCost = getDailyRentPrice() * days;

        // Yük kapasitesi ve soğutma sistemine göre fiyat ayarlaması
        if (loadCapacity > 5000) {
            baseCost *= 1.2; // Yük kapasitesi fazla ise zam
        }

        if (hasCoolingSystem) {
            baseCost *= 1.15; // Soğutma sistemi varsa zam
        }
        switch (fuelType) {
            case ELECTRIC:
                baseCost *= 0.9; // İndirim
                break;
            case HYBRID:
                baseCost *= 1.05; // Küçük zam
                break;
            default:
                break;
        }


        return baseCost;
    }

    @Override
    public void performMaintenance() {
        System.out.println("Ticari araç bakımı yapılıyor: " + getBrand() + " " + getModel());
        // Bakım işlemleri detaylandırılabilir
    }

    @Override
    public boolean isEligibleForRent() {
        return getYear() > 2005;
    }
}
