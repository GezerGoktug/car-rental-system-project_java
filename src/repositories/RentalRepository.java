package repositories;

import models.RentalTransaction;
import models.Car;
import models.User;
import exceptions.RentalException;
import services.CarService;
import services.UserService;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RentalRepository implements RentalRepositoryInt {
    private List<RentalTransaction> rentalTransactions = new ArrayList<>();
    private static final String RENTAL_DATA_FILE = "rentals.txt";

    //Constructorlar.
    public RentalRepository(CarService carService, UserService userService) {
        loadRentalsFromFile(carService, userService);
    }

    //Getter metotlar.
    public List<RentalTransaction> getRentalTransactions() {
        return rentalTransactions;
    }

    //Kiralama işlemini dosyaya kaydetme.
    @Override
    public void saveRentalTransaction(RentalTransaction rentalTransaction) throws RentalException {
        if (rentalTransaction == null) {
            throw new RentalException("Invalid rental transaction", RentalException.ErrorCode.INVALID_RENTAL_PERIOD);
        }
        rentalTransactions.add(rentalTransaction);
        saveRentalsToFile(); //Yeni kiralama işlemini dosyaya kaydet
    }

    //Kiralama ID sine göre kiralamayı bulma.
    @Override
    public Optional<RentalTransaction> findRentalByTransactionId(UUID transactionId) {
        return rentalTransactions.stream()
                .filter(rental -> rental.getTransactionId().equals(transactionId))
                .findFirst();
    }

    //Kiralamayı silme.
    public void deleteRental(UUID transactionId) {
        rentalTransactions = rentalTransactions.stream()
                .filter(rental -> !(rental.getTransactionId().equals(transactionId)))
                .collect(Collectors.toList());
        saveRentalsToFile();
    }

    //Kiralamadaki araçları bulma
    @Override
    public List<RentalTransaction> findRentalsByCar(Car car) {
        List<RentalTransaction> rentals = new ArrayList<>();
        for (RentalTransaction rental : rentalTransactions) {
            if (rental.getRentedCar().equals(car)) {
                rentals.add(rental);
            }
        }
        return rentals;
    }

    //Kullanıcıya göre kiralamayı bulma.
    @Override
    public List<RentalTransaction> findRentalsByUser(User user) {

        return rentalTransactions.stream()
                .filter(rental -> rental.getRenter().getUserId().equals(user.getUserId()))
                .collect(Collectors.toList());
    }

    //Kiralama süresini uzatmak.
    public void extendRental(RentalTransaction rentalTransaction, LocalDate endDate) throws RentalException {
        rentalTransaction.extendRental(endDate);
        saveRentalsToFile();
    }

    //Kiralamayı güncelleme
    public void updateRental(RentalTransaction rentalTransaction, boolean isConfirm) {
        if (isConfirm) {
            rentalTransaction.updateStatus(RentalTransaction.TransactionStatus.COMPLETED);
        } else {
            rentalTransaction.updateStatus(RentalTransaction.TransactionStatus.REJECTED);
        }

        saveRentalsToFile();
    }

    //Dosyadan kiralama işlemlerini yükleme.
    private void loadRentalsFromFile(CarService carService, UserService userService) {
        try (BufferedReader reader = new BufferedReader(new FileReader(RENTAL_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Örnek dosya formatı:
                // <transactionId>,<carId>,<userId>,<startDate>,<endDate>,<price>,<status>
                String[] data = line.split(";");
                UUID transactionId = UUID.fromString(data[0]);
                UUID carId = UUID.fromString(data[1]);
                UUID userId = UUID.fromString(data[2]);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(data[3], formatter); // startDate'yi LocalDate olarak al
                LocalDate endDate = LocalDate.parse(data[4], formatter);
                RentalTransaction.TransactionStatus status = RentalTransaction.TransactionStatus.valueOf(data[5]);


                Car car = carService.searchCarById(carId);
                User user = userService.getUserById(userId);

                RentalTransaction rentalTransaction = new RentalTransaction(transactionId, status, car, user, startDate, endDate);
                rentalTransactions.add(rentalTransaction);
            }
        } catch (IOException e) {
            System.err.println("Dosyadan kiralama işlemleri yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    //Kiralama işlemlerini dosyaya kaydetme.
    private void saveRentalsToFile() {

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RENTAL_DATA_FILE))) {
            for (RentalTransaction rentalTransaction : rentalTransactions) {
                writer.write(String.format("%s;%s;%s;%s;%s;%s%n",
                        rentalTransaction.getTransactionId(),
                        rentalTransaction.getRentedCar().getCarId(),
                        rentalTransaction.getRenter().getUserId(),
                        rentalTransaction.getStartDate().format(dateFormatter),
                        rentalTransaction.getEndDate().format(dateFormatter),
                        rentalTransaction.getStatus()
                ));
            }
        } catch (IOException e) {
            System.err.println("Kiralama işlemleri dosyaya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
}
