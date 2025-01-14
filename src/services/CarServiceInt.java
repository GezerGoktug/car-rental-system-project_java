package services;

import models.Car;

import java.util.List;
import java.util.UUID;

public interface CarServiceInt {
    List<Car> listAllCars();

    void registerCar(Car car);

    Car searchCarByPlate(String plateNumber);

    Car searchCarById(UUID carId);
}