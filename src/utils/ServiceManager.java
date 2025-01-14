package utils;

import services.CarService;
import services.RentalService;
import services.UserService;

public class ServiceManager {
    private static UserService userService;
    private static CarService carService;
    private static RentalService rentalService;

    //Constructorlar.
    public ServiceManager() {
        userService = new UserService();
        carService = new CarService();
        rentalService = new RentalService(carService, userService);
    }

    //Getter metodlar.
    public static CarService getCarService() {
        return carService;
    }

    public static RentalService getRentalService() {
        return rentalService;
    }

    public static UserService getUserService() {
        return userService;
    }
}
