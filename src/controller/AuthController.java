package controller;

import exceptions.RentalException;
import models.RentalTransaction;
import models.User;
import utils.ServiceManager;

import java.util.List;
import java.util.UUID;


public class AuthController {
    //Giriş işlemlerini kontrol eden metot.
    public static User login(String email, String password) throws Exception {
        User user = ServiceManager.getUserService().login(email, password);
        if (user != null) {
            return user;
        } else {
            throw new Exception("Invalid username or password!");
        }
    }

    //Kayıt olma işlemlerini kontrol eden metot.
    public static User register(String username, String email, String password, User.UserRole role) {
        try {
            User newUser = ServiceManager.getUserService().registerUser(username, email, password, role);
            return newUser;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //Kullanıcı işlemlerini kontrol eden metot.(CustomerDashboard)
    public static User updateUserInfos(User user, String newUsername, String newPassword) {
        try {
            User updatedUser = ServiceManager.getUserService().updateUser(user.getUserId(), newUsername, newPassword, user.getRole());
            ServiceManager.getRentalService().updateRentalsByUser(user, updatedUser);
            return updatedUser;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //Kullanıcı işlemlerini kontrol eden metot.(AdminDashboard)
    public static User updateUserInfosByUserId(UUID userId, String newUsername, String newPassword, User.UserRole role) {
        try {
            User existingUser = ServiceManager.getUserService().getUserById(userId);
            User updatedUser = ServiceManager.getUserService().updateUser(existingUser.getUserId(), newUsername, newPassword, role);
            ServiceManager.getRentalService().updateRentalsByUser(existingUser, updatedUser);
            return updatedUser;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //Kullanıcıları getiren metot.
    public static List<User> getAllUsers() {
        return ServiceManager.getUserService().listAllUsers();
    }

    //Kullanıcı silme işlemlerini kontrol eden metot.
    public static void deleteUser(UUID userId) throws RentalException {
        User userToDelete = ServiceManager.getUserService().getUserById(userId);

        if (userToDelete.getRole() == User.UserRole.ADMIN) {
            throw new IllegalArgumentException("Could not be delete because account have a role to admin ");
        }

        ServiceManager.getUserService().deleteUser(userToDelete.getUserId());
        List<RentalTransaction> rentals = ServiceManager.getRentalService().getRentalsByUser(userToDelete);
        for (RentalTransaction rental : rentals) {
            ServiceManager.getRentalService().completeRental(rental.getTransactionId());
        }
    }

}
