package services;

import models.RentalTransaction;
import models.Car;
import models.User;
import exceptions.RentalException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentalServiceInt {
    RentalTransaction startRental(Car car, User user, LocalDate startDate, LocalDate endDate) throws RentalException;

    RentalTransaction extendRental(UUID transactionId, LocalDate newEndDate) throws RentalException;

    Car updateRentalStatus(UUID transactionId, boolean isAccept) throws RentalException;
    

    List<RentalTransaction> getRentalsByUser(User user) throws RentalException;
}
