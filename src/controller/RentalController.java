package controller;

import exceptions.RentalException;
import models.Car;
import models.RentalTransaction;
import models.User;
import utils.ServiceManager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RentalController {
    //Kiralama statüsüne göre kiralamaları getirme.
    public static List<RentalTransaction> getMyRentalsByStatus(User user, RentalTransaction.TransactionStatus transactionStatus) throws RentalException {
        List<RentalTransaction> rentals = ServiceManager.getRentalService().getRentalsByUser(user);
        List<RentalTransaction> filteredRentals = new ArrayList<>(List.of());
        for (RentalTransaction rental : rentals) {
            if (rental.getStatus().equals(transactionStatus)) {
                filteredRentals.add(rental);
            }
        }
        return filteredRentals;

    }

    //Beklemedeki kiralama getirme.
    public static List<RentalTransaction> getPendingRentals() throws RentalException {
        List<RentalTransaction> rentals = ServiceManager.getRentalService().listRentals();
        List<RentalTransaction> filteredRentals = new ArrayList<>(List.of());
        for (RentalTransaction rental : rentals) {
            if (rental.getStatus().equals(RentalTransaction.TransactionStatus.PENDING)) {
                filteredRentals.add(rental);
            }
        }
        return filteredRentals;

    }

    //Kiralama uzatma işlemlerini kontrol eden metot.
    public static RentalTransaction extendRentalTransaction(UUID transactionId, LocalDate endDate) throws RentalException {
        return ServiceManager.getRentalService().extendRental(transactionId, endDate);
    }

    //Kiralama işlemlerinin statüsünü kontrol eden metot.
    public static void evalRental(UUID transactionId, boolean isAccept) throws RentalException {
        Car car = ServiceManager.getRentalService().updateRentalStatus(transactionId, isAccept);
        ServiceManager.getCarService().updateCarStatus(car, isAccept ? Car.CarStatus.RENTED : Car.CarStatus.AVAILABLE);
    }

    //Kiralama tamamlama işlemlerini kontrol eden metot.
    public static Car finishedRentalTransaction(UUID transactionId) throws RentalException {
        UUID carId = ServiceManager.getRentalService().completeRental(transactionId);
        Car car = ServiceManager.getCarService().searchCarById(carId);
        ServiceManager.getCarService().updateCarStatus(car, Car.CarStatus.AVAILABLE);
        return car;

    }

    //Kiralama başlatma işlemlerini kontrol eden metot.
    public static String startRental(UUID carId, User user, LocalDate startedDate, LocalDate endedDate) throws RentalException {
        Car car = ServiceManager.getCarService().searchCarById(carId);
        RentalTransaction rental = ServiceManager.getRentalService().startRental(car, user, startedDate, endedDate);
        ServiceManager.getCarService().updateCarStatus(car, Car.CarStatus.PENDING);
        return rental.getRentedCar().getPlateNumber();

    }
}
