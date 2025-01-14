package repositories;

import models.Car;
import models.CommercialVehicle;
import models.PassengerCar;
import models.SUV;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class CarRepository implements CarRepositoryInt {
    private List<Car> cars;
    private static final String CAR_DATA_FILE = "cars.txt";

    //Constructor
    public CarRepository() {
        this.cars = new ArrayList<>();
        loadCarsFromFile();
    }

    //Araç ekleme
    public void addCar(Car car) {
        cars.add(car);
        saveCarsToFile();
    }

    //Araçları listeleme
    public List<Car> getAllCars() {
        return cars;
    }

    //Plaka numarasına göre araç silme
    public void deleteCar(String plateNumber) {
        cars.removeIf(car -> car.getPlateNumber().equals(plateNumber));
        saveCarsToFile();
    }

    //Araba statüsüne göre güncelleme
    public void updateCarStatus(Car car, Car.CarStatus status) {
        car.setStatus(status);
        saveCarsToFile();
    }

    //Araba güncelleme
    public void updateCars(Car newCar, Car oldCar) {


        if (oldCar != null) {
            oldCar.setStatus(newCar.getStatus());
            oldCar.setBrand(newCar.getBrand());
            oldCar.setModel(newCar.getModel());
            oldCar.setGear(newCar.getGear());
            oldCar.setFuelType(newCar.getFuelType());
            oldCar.setYear(newCar.getYear());
            oldCar.setDailyRentPrice(newCar.getDailyRentPrice());
            oldCar.setPlateNumber(newCar.getPlateNumber());
            oldCar.setEngineCapacity(newCar.getEngineCapacity());
            oldCar.setCategory(newCar.getCategory());
            oldCar.setLastMaintenanceDate(newCar.getLastMaintenanceDate());
            oldCar.setTotalMileage(newCar.getTotalMileage());
            
            if (oldCar instanceof CommercialVehicle) {
                if (newCar instanceof CommercialVehicle) {
                    ((CommercialVehicle) oldCar).setHasCoolingSystem(((CommercialVehicle) newCar).isHasCoolingSystem());
                    ((CommercialVehicle) oldCar).setLoadCapacity(((CommercialVehicle) newCar).getLoadCapacity());
                    ((CommercialVehicle) oldCar).setFuelConsumptionRate(((CommercialVehicle) newCar).getFuelConsumptionRate());

                }
            } else if (oldCar instanceof PassengerCar) {
                if (newCar instanceof PassengerCar) {
                    ((PassengerCar) oldCar).setPassengerCapacity(((PassengerCar) newCar).getPassengerCapacity());
                    ((PassengerCar) oldCar).setHasAirConditioning(((PassengerCar) newCar).isHasAirConditioning());
                    ((PassengerCar) oldCar).setComfortLevel(((PassengerCar) newCar).getComfortLevel());
                }
            } else if (oldCar instanceof SUV) {
                if (newCar instanceof SUV) {
                    ((SUV) oldCar).setGroundClearance(((SUV) newCar).getGroundClearance());
                    ((SUV) oldCar).setTerrainMode(((SUV) newCar).getTerrainMode());
                    ((SUV) oldCar).setHasAllWheelDrive(((SUV) newCar).isHasAllWheelDrive());
                }

            }
            saveCarsToFile();
        }
    }

    //Plaka numarasına göre araç bulma
    public Car findCarByPlateNumber(String plateNumber) {
        return cars.stream()
                .filter(car -> car.getPlateNumber().equals(plateNumber))
                .findFirst()
                .orElse(null);
    }

    // Plaka numarasına göre araç bulma
    public Car findCarById(UUID carId) {
        return cars.stream()
                .filter(car -> car.getCarId().toString().equals(carId.toString()))
                .findFirst()
                .orElse(null);
    }

    //Araç isimine göre araç aratma,bulma.
    public List<Car> findCarByText(String query) {
        List<Car> filteredCars = new ArrayList<>(List.of());
        for (Car car : cars) {
            String carName = car.getBrand().toLowerCase() + " " + car.getModel().toLowerCase();
            if (carName.contains(query.toLowerCase())) {
                filteredCars.add(car);
            }
        }
        return filteredCars;
    }


    //Dosyadan araçları yükleme
    private void loadCarsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CAR_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Detaylı dosya formatı:
                // PASSENGER,carId,status,totalMileage,lastMaintenanceDate,plateNumber,brand,model,gear,fuelType,year,dailyRentPrice,engineCapacity,category,passengerCapacity,hasAirConditioning,fuelType
                // COMMERCIAL,carId,status,totalMileage,lastMaintenanceDate,plateNumber,brand,model,gear,fuelType,year,dailyRentPrice,engineCapacity,category,loadCapacity,hasCoolingSystem
                String[] data = line.split(";");
                Car car;
                if (data[0].equals("PASSENGER")) {
                    car = new PassengerCar(
                            UUID.fromString(data[1]),  // carId
                            Car.CarStatus.valueOf(data[2]),  // status
                            Integer.parseInt(data[3]),  // totalMileage
                            LocalDate.parse(data[4]),
                            data[5],   // plateNumber
                            data[6],   // brand
                            data[7],// model
                            Car.CarGear.valueOf(data[8]),//gear
                            Car.FuelType.valueOf(data[9]),//fuelType
                            Integer.parseInt(data[10]),   // year
                            Double.parseDouble(data[11].replace(",", ".")), // dailyRentPrice
                            Integer.parseInt(data[12]),  // engineCapacity
                            Integer.parseInt(data[13]),  // passengerCapacity
                            Boolean.parseBoolean(data[14]), // hasCoolingSystem
                            PassengerCar.CarComfort.valueOf(data[15]) //comfort level
                    );

                } else if (data[0].equals("COMMERCIAL")) {
                    car = new CommercialVehicle(
                            UUID.fromString(data[1]),  // carId
                            Car.CarStatus.valueOf(data[2]),  // status
                            Integer.parseInt(data[3]),  // totalMileage
                            LocalDate.parse(data[4]),
                            data[5],   // plateNumber
                            data[6],   // brand
                            data[7],// model
                            Car.CarGear.valueOf(data[8]),//gear
                            Car.FuelType.valueOf(data[9]),//fuelType
                            Integer.parseInt(data[10]),   // year
                            Double.parseDouble(data[11].replace(",", ".")), // dailyRentPrice
                            Integer.parseInt(data[12]),  // engineCapacity
                            Double.parseDouble(data[13].replace(",", ".")),  // loadCapacity
                            Boolean.parseBoolean(data[14]),// hasCoolingSystem,
                            Double.parseDouble(data[15].replace(",", ".")) // fuelConsumptionRate
                    );
                } else if (data[0].equals("SUV")) {
                    car = new SUV(
                            UUID.fromString(data[1]),  // carId
                            Car.CarStatus.valueOf(data[2]),  // status
                            Integer.parseInt(data[3]),  // totalMileage
                            LocalDate.parse(data[4]),
                            data[5],   // plateNumber
                            data[6],   // brand
                            data[7],   // model
                            Car.CarGear.valueOf(data[8]),   //gear
                            Car.FuelType.valueOf(data[9]),   //fuelType
                            Integer.parseInt(data[10]),   // year
                            Double.parseDouble(data[11].replace(",", ".")), // dailyRentPrice
                            Integer.parseInt(data[12]),  // engineCapacity
                            Boolean.parseBoolean(data[13]), // hasAllWheelDrive,
                            Integer.parseInt(data[14]),  // ground clearance
                            SUV.TerrainMode.valueOf(data[15]) // terrain mode
                    );
                } else {
                    continue; // Desteklenmeyen araç tipi
                }
                cars.add(car);
            }
        } catch (IOException e) {
            System.err.println("Dosyadan araçlar yüklenirken hata oluştu: " + e.getMessage());
        }
    }

    //Araçları dosyaya kaydetme
    private void saveCarsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CAR_DATA_FILE))) {
            for (Car car : cars) {
                if (car instanceof PassengerCar passengerCar) {
                    writer.write(String.format("PASSENGER;%s;%s;%d;%s;%s;%s;%s;%s;%s;%d;%.2f;%d;%d;%b;%s%n",
                            passengerCar.getCarId(),  // Car ID
                            passengerCar.getStatus(), // Car Status
                            passengerCar.getTotalMileage(), // Total Mileage
                            passengerCar.getLastMaintenanceDate(), // Last Maintenance Date
                            passengerCar.getPlateNumber(), // Plate Number
                            passengerCar.getBrand(), // Brand
                            passengerCar.getModel(),// Model
                            passengerCar.getGear(),//Gear
                            passengerCar.getFuelType(),//Fuel Type
                            passengerCar.getYear(), // Year
                            passengerCar.getDailyRentPrice(), // Daily Rent Price
                            passengerCar.getEngineCapacity(), // Engine Capacity
                            passengerCar.getPassengerCapacity(), // Passenger Capacity
                            passengerCar.isHasAirConditioning(), // Has Air Conditioning
                            passengerCar.getComfortLevel() // Comfort Level
                    ));

                } else if (car instanceof CommercialVehicle commercialVehicle) {
                    writer.write(String.format("COMMERCIAL;%s;%s;%d;%s;%s;%s;%s;%s;%s;%d;%.2f;%d;%.2f;%b;%.2f%n",
                            commercialVehicle.getCarId(), // Car ID
                            commercialVehicle.getStatus(), // Car Status
                            commercialVehicle.getTotalMileage(), // Total Mileage
                            commercialVehicle.getLastMaintenanceDate(), // Last Maintenance Date
                            commercialVehicle.getPlateNumber(), // Plate Number
                            commercialVehicle.getBrand(), // Brand
                            commercialVehicle.getModel(),// Model
                            commercialVehicle.getGear(),// Gear
                            commercialVehicle.getFuelType(),//Fuel Type
                            commercialVehicle.getYear(), // Year
                            commercialVehicle.getDailyRentPrice(), // Daily Rent Price
                            commercialVehicle.getEngineCapacity(), // Engine Capacity
                            commercialVehicle.getLoadCapacity(), // Load Capacity
                            commercialVehicle.isHasCoolingSystem(), // Has Cooling System
                            commercialVehicle.getFuelConsumptionRate() //Fuel Consumption Rate
                    ));
                } else if (car instanceof SUV suv) {
                    writer.write(String.format("SUV;%s;%s;%d;%s;%s;%s;%s;%s;%s;%d;%.2f;%d;%b;%d;%s%n",
                            suv.getCarId(),  // Car ID
                            suv.getStatus(), // Car Status
                            suv.getTotalMileage(), // Total Mileage
                            suv.getLastMaintenanceDate(), // Last Maintenance Date
                            suv.getPlateNumber(), // Plate Number
                            suv.getBrand(), // Brand
                            suv.getModel(),// Model
                            suv.getGear(),// Gear
                            suv.getFuelType(), //Fuel Type
                            suv.getYear(), // Year
                            suv.getDailyRentPrice(), // Daily Rent Price
                            suv.getEngineCapacity(), // Engine Capacity
                            suv.isHasAllWheelDrive(), // All-Wheel Drive
                            suv.getGroundClearance(), // Ground Clearance
                            suv.getTerrainMode() // Terrain Mode
                    ));
                }
            }
        } catch (IOException e) {
            System.err.println("Araçlar dosyaya kaydedilirken hata oluştu: " + e.getMessage());
        }
    }
}