package controller;


import models.Car;
import utils.ServiceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CarController {
    //Mevcut statüsündeki araçları getiriyor.
    public static List<Car> getAvailableCars() {
        List<Car> cars = ServiceManager.getCarService().listAllCars();
        List<Car> availableCars = new ArrayList<>(List.of());
        for (Car car : cars) {
            if (car.getStatus().equals(Car.CarStatus.AVAILABLE)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    //Tüm araçları getiriyor.
    public static List<Car> getAllCars() {
        List<Car> cars = ServiceManager.getCarService().listAllCars();
        return cars;
    }

    //Araç ID sine göre seçilen aracı getirme.
    public static Car getSelectedCarById(UUID carId) {
        Car car = ServiceManager.getCarService().searchCarById(carId);
        return car;
    }

    //Araç arama işlemlerini kontrol eden metot.
    public static List<Car> getSearchedCars(String query) {
        List<Car> cars = ServiceManager.getCarService().searchCars(query);
        return cars;
    }

    //Araç ekleme işlemlerini kontrol eden metot.
    public static void addNewCar(Car newCar) {

        ServiceManager.getCarService().registerCar(newCar);
    }

    //Araç güncelleme işlemlerin kontrol eden metot.
    public static void updateCar(Car newCar, String plateNumber) {
        Car existingCar = ServiceManager.getCarService().searchCarByPlate(plateNumber);
        ServiceManager.getCarService().updateCar(newCar, existingCar);
    }

    //Araç silme işlemlerini kontrol eden metot.
    public static void deleteCar(String plateNumber) {
        ServiceManager.getCarService().deleteCar(plateNumber);
    }


}
