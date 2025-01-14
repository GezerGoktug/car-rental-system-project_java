package models;

import java.time.LocalDate;
import java.util.UUID;

public class PassengerCar extends Car {
    private int passengerCapacity;
    private boolean hasAirConditioning;
    private CarComfort comfortLevel;

    public enum CarComfort {
        LUXURY, STANDARD, ECONOMY
    }

    public CarComfort getComfortLevel() {
        return comfortLevel;
    }

    public void setComfortLevel(CarComfort comfortLevel) {
        this.comfortLevel = comfortLevel;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public void setHasAirConditioning(boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }


    public boolean isHasAirConditioning() {
        return hasAirConditioning;
    }


    public PassengerCar(String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
                        double dailyRentPrice, int engineCapacity,
                        int passengerCapacity, boolean hasAirConditioning, CarComfort comfortLevel) {
        super(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice,
                engineCapacity, CarCategory.PASSENGER);
        this.passengerCapacity = passengerCapacity;
        this.hasAirConditioning = hasAirConditioning;
        this.comfortLevel = comfortLevel;
    }

    public PassengerCar(UUID carId, CarStatus status, int totalMileage, LocalDate lastMaintenanceDate, String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
                        double dailyRentPrice, int engineCapacity,
                        int passengerCapacity, boolean hasAirConditioning, CarComfort comfortLevel) {
        super(carId, status, totalMileage, lastMaintenanceDate, plateNumber, brand, model, gear, fuelType, year, dailyRentPrice,
                engineCapacity, CarCategory.PASSENGER);
        this.passengerCapacity = passengerCapacity;
        this.hasAirConditioning = hasAirConditioning;
        this.comfortLevel = comfortLevel;
    }

    @Override
    public double calculateRentCost(int days, FuelType fuelType) {
        double baseCost = getDailyRentPrice() * days;

        // Hava kliması ve yakıt tipine göre fiyat ayarlaması
        if (hasAirConditioning) {
            baseCost *= 1.1;
        }

        switch (comfortLevel) {
            case LUXURY:
                baseCost *= 1.10; // İndirim
                break;
            case ECONOMY:
                baseCost *= 0.95; // Küçük zam
                break;
            case STANDARD:
                baseCost *= 1.00; // Küçük zam
                break;
            default:
                break;
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

    //Bakım durumunu belirten metot
    @Override
    public void performMaintenance() {
        System.out.println("Binek araç bakımı yapılıyor: " + getBrand() + " " + getModel());
        // Bakım işlemleri detaylandırılabilir
    }

    @Override
    public boolean isEligibleForRent() {
        return getYear() > 2010;
    }
}
