package repositories;

import models.Car;

import java.util.List;
import java.util.UUID;

public interface CarRepositoryInt {
    void addCar(Car car);

    List<Car> getAllCars();

    Car findCarByPlateNumber(String plateNumber);

    Car findCarById(UUID carId);
}
