package services;

import models.RentalTransaction;
import models.Car;
import models.User;
import exceptions.RentalException;
import repositories.RentalRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class RentalService implements RentalServiceInt {
    private final RentalRepository rentalRepository;

    //Constructorlar.
    public RentalService(CarService carService, UserService userService) {
        this.rentalRepository = new RentalRepository(carService, userService);
    }

    //Tüm kiralamaları sıralama.Repository katmanını kullanır.
    public List<RentalTransaction> listRentals() {
        return rentalRepository.getRentalTransactions();
    }

    //Kiralama başlatma işlemleri yapar.Repository katmanını kullanır.
    @Override
    public RentalTransaction startRental(Car car, User user, LocalDate startDate, LocalDate endDate) throws RentalException {
        if (car.getStatus() != Car.CarStatus.AVAILABLE) {
            throw new RentalException("Car is not available for rent", RentalException.ErrorCode.CAR_NOT_AVAILABLE);
        }
        if (LocalDate.now().isAfter(startDate)) {
            throw new RentalException("Value cannot be entered before today's date.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (startDate.isAfter(endDate)) {
            throw new RentalException("End date cannot be before start date", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), startDate) > 30) {
            throw new RentalException("Appointments for car rental can be made a maximum of 30 days in advance.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (ChronoUnit.DAYS.between(startDate, endDate) > 21) {
            throw new RentalException("A car can be rented for a maximum of 20 days.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        RentalTransaction rentalTransaction = new RentalTransaction(car, user, startDate, endDate);
        rentalRepository.saveRentalTransaction(rentalTransaction);
        return rentalTransaction;
    }

    //Kiralama süresini uzatma.Repository katmanını kullanır.
    @Override
    public RentalTransaction extendRental(UUID transactionId, LocalDate newEndDate) throws RentalException {
        RentalTransaction rentalTransaction = rentalRepository.findRentalByTransactionId(transactionId)
                .orElseThrow(() -> new RentalException("Rental transaction not found", RentalException.ErrorCode.INVALID_RENTAL_PERIOD));

        if (LocalDate.now().isAfter(newEndDate)) {
            throw new RentalException("Value cannot be entered before today's date.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (rentalTransaction.getStartDate().isAfter(newEndDate)) {
            throw new RentalException("End date cannot be before start date", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (ChronoUnit.DAYS.between(LocalDate.now(), rentalTransaction.getStartDate()) > 30) {
            throw new RentalException("Appointments for car rental can be made a maximum of 30 days in advance.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (ChronoUnit.DAYS.between(rentalTransaction.getStartDate(), newEndDate) > 21) {
            throw new RentalException("A car can be rented for a maximum of 20 days.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        if (rentalTransaction.getEndDate().isAfter(newEndDate)) {
            throw new RentalException("New End date cannot be before end date.", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }

        rentalRepository.extendRental(rentalTransaction, newEndDate);
        return rentalTransaction;
    }

    //Kiralama statüsünü değiştirme.Repository katmanını kullanır.
    @Override
    public Car updateRentalStatus(UUID transactionId, boolean isAccept) throws RentalException {
        RentalTransaction rentalTransaction = rentalRepository.findRentalByTransactionId(transactionId)
                .orElseThrow(() -> new RentalException("Rental transaction not found", RentalException.ErrorCode.INVALID_RENTAL_PERIOD));
        rentalRepository.updateRental(rentalTransaction, isAccept);
        return rentalTransaction.getRentedCar();
    }

    //Kiralama işlemlerini tamamlama.Repository katmanını kullanır.
    public UUID completeRental(UUID transactionId) throws RentalException {
        RentalTransaction rentalTransaction = rentalRepository.findRentalByTransactionId(transactionId)
                .orElseThrow(() -> new RentalException("Rental transaction not found", RentalException.ErrorCode.INVALID_RENTAL_PERIOD));

        rentalRepository.deleteRental(transactionId);
        return rentalTransaction.getRentedCar().getCarId();

    }

    //Kullanıcıya göre araç kiralama güncelleme.Repository katmanını kullanır.
    public void updateRentalsByUser(User user, User newUser) {
        List<RentalTransaction> rentals = rentalRepository.findRentalsByUser(user);
        for (RentalTransaction rental : rentals) {
            rental.setRenter(newUser);
        }
    }

    //Kullanıcıya göre kiralamaları getirme.Repository katmanını kullanır.
    @Override
    public List<RentalTransaction> getRentalsByUser(User user) throws RentalException {
        return rentalRepository.findRentalsByUser(user);
    }
}
