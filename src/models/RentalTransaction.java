package models;

import exceptions.RentalException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class RentalTransaction {
    private UUID transactionId;
    private Car rentedCar;
    private User renter;
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalCost;
    private TransactionStatus status;


    public double getTotalCost() {
        return totalCost;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Car getRentedCar() {
        return rentedCar;
    }

    public User getRenter() {
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    // İşlem durumu
    public enum TransactionStatus {
        PENDING, ACTIVE, COMPLETED, REJECTED
    }

    private void applyTransaction(Car car, User renter, LocalDate startDate, LocalDate endDate) {
        this.rentedCar = car;
        this.renter = renter;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public RentalTransaction(UUID uuid, TransactionStatus status, Car car, User renter, LocalDate startDate, LocalDate endDate) {
        this.transactionId = uuid;
        this.status = status;
        applyTransaction(car, renter, startDate, endDate);
        calculateTotalCost();
    }

    public RentalTransaction(Car car, User renter, LocalDate startDate, LocalDate endDate) {
        this.transactionId = UUID.randomUUID();
        this.status = TransactionStatus.PENDING;
        applyTransaction(car, renter, startDate, endDate);
        calculateTotalCost();
    }

    // Toplam maliyeti hesaplama
    private void calculateTotalCost() {
        long rentalDays = ChronoUnit.DAYS.between(startDate, endDate);
        Car.FuelType fuelType = rentedCar.getFuelType();
        this.totalCost = rentedCar.calculateRentCost((int) rentalDays, fuelType);
    }

    // Kiralama süresini uzatma
    public void extendRental(LocalDate newEndDate) {
        this.endDate = newEndDate;
        calculateTotalCost();
    }

    // İşlem durumunu güncelleme
    public void updateStatus(TransactionStatus newStatus) {
        this.status = newStatus;

        // Araç durumunu güncelleme
        if (newStatus == TransactionStatus.PENDING) {
            rentedCar.setStatus(Car.CarStatus.PENDING);
        } else if (newStatus.equals(TransactionStatus.COMPLETED) || newStatus.equals(TransactionStatus.REJECTED)) {
            rentedCar.setStatus(Car.CarStatus.AVAILABLE);
        }
    }
}

