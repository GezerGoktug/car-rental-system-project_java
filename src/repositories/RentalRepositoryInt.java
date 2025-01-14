package repositories;


import models.RentalTransaction;
import models.Car;
import models.User;
import exceptions.RentalException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RentalRepositoryInt {
    void saveRentalTransaction(RentalTransaction rentalTransaction) throws RentalException;

    Optional<RentalTransaction> findRentalByTransactionId(UUID transactionId) throws RentalException;

    List<RentalTransaction> findRentalsByCar(Car car) throws RentalException;

    List<RentalTransaction> findRentalsByUser(User user) throws RentalException;

    void updateRental(RentalTransaction rentalTransaction, boolean isConfirm) throws RentalException;
}
