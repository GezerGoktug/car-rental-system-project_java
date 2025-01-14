package models;

import java.time.LocalDate;
import java.util.UUID;

public class SUV extends Car implements OffRoadCapable {
    private boolean hasAllWheelDrive;
    private int groundClearance;
    private TerrainMode terrainMode;


    public enum TerrainMode {
        STANDARD, SAND, MUD, SNOW, ROCK
    }

    public SUV(String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
               double dailyRentPrice, int engineCapacity,
               boolean hasAllWheelDrive, int groundClearance,
               TerrainMode terrainMode) {
        super(plateNumber, brand, model, gear, fuelType, year, dailyRentPrice,
                engineCapacity, CarCategory.SUV);
        this.hasAllWheelDrive = hasAllWheelDrive;
        this.groundClearance = groundClearance;
        this.terrainMode = terrainMode;
    }


    public SUV(UUID carId, CarStatus status, int totalMileage, LocalDate lastMaintenanceDate,
               String plateNumber, String brand, String model, CarGear gear, FuelType fuelType, int year,
               double dailyRentPrice, int engineCapacity,
               boolean hasAllWheelDrive, int groundClearance,
               TerrainMode terrainMode) {
        super(carId, status, totalMileage, lastMaintenanceDate, plateNumber, brand, model, gear, fuelType, year,
                dailyRentPrice, engineCapacity, CarCategory.SUV);
        this.hasAllWheelDrive = hasAllWheelDrive;
        this.groundClearance = groundClearance;
        this.terrainMode = terrainMode;
    }

    //Getter and Setter metotlar
    public boolean isHasAllWheelDrive() {
        return hasAllWheelDrive;
    }

    public void setHasAllWheelDrive(boolean hasAllWheelDrive) {
        this.hasAllWheelDrive = hasAllWheelDrive;
    }

    public int getGroundClearance() {
        return groundClearance;
    }

    public void setGroundClearance(int groundClearance) {
        this.groundClearance = groundClearance;
    }

    public TerrainMode getTerrainMode() {
        return terrainMode;
    }

    public void setTerrainMode(TerrainMode terrainMode) {
        this.terrainMode = terrainMode;
    }

    //Toplam miktarı hesaplayan metotlar
    @Override
    public double calculateRentCost(int days, FuelType fuelType) {
        double baseCost = getDailyRentPrice() * days;

        if (hasAllWheelDrive) {
            baseCost *= 1.15; // 15% premium for all-wheel drive
        }


        switch (terrainMode) {
            case SAND:
            case ROCK:
                baseCost *= 1.1; //Para miktarında artış.
                break;
            default:
                break;
        }
        switch (fuelType) {
            case ELECTRIC:
                baseCost *= 0.9; //İndirim
                break;
            case HYBRID:
                baseCost *= 1.05; //Küçük zam
                break;
            default:
                break;
        }

        return baseCost;
    }

    //Bakım zamanı ile ilgili metod.
    @Override
    public void performMaintenance() {
        System.out.println("SUV bakımı yapılıyor: " + getBrand() + " " + getModel());
        //Özel SUV bakım prosedürleri eklenebilir
    }

    //Kiralama için gerekli olan şartlar.
    @Override
    public boolean isEligibleForRent() {
        return getYear() > 2008 && groundClearance >= 200;
    }

    //Off-Road durumu.
    @Override
    public boolean isOffRoadReady() {
        return hasAllWheelDrive && groundClearance >= 220
                && (terrainMode != TerrainMode.STANDARD);
    }

    //Off-Road parametresi.
    @Override
    public int getOffRoadMaintenanceFrequency() {
        return isOffRoadReady() ? 5000 : 10000;
    }
}