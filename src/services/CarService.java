package services;


import models.Car;
import repositories.CarRepository;

import java.util.List;
import java.util.UUID;

public class CarService implements CarServiceInt {
    private final CarRepository carRepository;

    //Constructorlar.
    public CarService() {
        this.carRepository = new CarRepository();
    }

    //Tüm araçları listele.Repository katmanını kullanır.
    public List<Car> listAllCars() {
        return carRepository.getAllCars();
    }

    //Araç güncelleme.Repository katmanını kullanır.
    public void updateCar(Car newCar, Car oldCar) {
        if (!(newCar.isEligibleForRent())) {
            throw new IllegalArgumentException("Not suitable for renting a car. \n" +
                    "SUV: \n" +
                    "It must be more than 2008 model and the ground clearance must be more than 200 \n " +
                    "Commercial Vehicle: \n" +
                    "Must be more than 2005 model \n" +
                    "Passenger Car: \n" +
                    "Must be more than 2010 model"
            );
        }
        carRepository.updateCars(newCar, oldCar);
    }

    //Araç statüsünü güncelleştirme.Repository katmanını kullanır.
    public void updateCarStatus(Car car, Car.CarStatus status) {
        carRepository.updateCarStatus(car, status);
    }

    //Araç silme.Repository katmanını kullanır.
    public void deleteCar(String plateNumber) {
        carRepository.deleteCar(plateNumber);
    }

    //Araç arama.Repository katmanını kullanır.
    public List<Car> searchCars(String query) {
        return carRepository.findCarByText(query);
    }


    //Yeni araç ekle.Repository katmanını kullanır.
    public void registerCar(Car car) {
        if (carRepository.findCarByPlateNumber(car.getPlateNumber()) != null) {
            throw new IllegalArgumentException("This license plate number is already registered!");
        }
        if (!(car.isEligibleForRent())) {
            throw new IllegalArgumentException("Not suitable for renting a car. \n" +
                    "SUV: \n" +
                    "It must be more than 2008 model and the ground clearance must be more than 200 \n" +
                    "Commercial Vehicle: \n" +
                    "Must be more than 2005 model \n" +
                    "Passenger Car: \n" +
                    "Must be more than 2010 model"
            );
        }
        carRepository.addCar(car);
    }

    //Plaka numarasına göre araç ara.Repository katmanını kullanır.
    public Car searchCarByPlate(String plateNumber) {
        return carRepository.findCarByPlateNumber(plateNumber);
    }

    //Araç ID sine göre araç ara.Repository katmanını kullanır.
    public Car searchCarById(UUID carId) {
        return carRepository.findCarById(carId);
    }
}
