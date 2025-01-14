package services;

import models.User;

import repositories.UserRepository;
import utils.Validator;

import java.util.List;
import java.util.UUID;

public class UserService implements UserServiceInt {
    private final UserRepository userRepository;

    //Constructorlar.
    public UserService() {
        this.userRepository = new UserRepository();
    }

    //Tüm kullanıcıları listeleme.Repository katmanını kullanır.
    public List<User> listAllUsers() {
        return userRepository.getUsers();
    }

    //Email e göre kullanıcı getirme.Repository katmanını kullanır.
    public User getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    //Kullanıcı kaydetme.Repository katmanını kullanır.
    @Override
    public User registerUser(String username, String email, String password, User.UserRole role) {

        if (username.isEmpty()) {
            throw new IllegalArgumentException("The username field cannot be empty");
        }
        if (username.length() < 4) {
            throw new IllegalArgumentException(" The username must be at least 3 characters long.");
        }
        if (!Validator.validateEmail(email)) {
            throw new IllegalArgumentException("The email is not in the appropriate format.");
        }
        if (!Validator.validatePassword(password)) {
            throw new IllegalArgumentException("Your password does not meet the requirements. You must enter a password that is at least 8 characters long and contains at least 4 letters.");
        }

        if (userRepository.findUserByEmail(email) == null) {
            if (!(userRepository.findUserByPassword(password))) {
                return userRepository.saveUser(username, email, password, role);
            } else {
                throw new IllegalArgumentException("This password is already in use. Try another password");
            }
        } else {
            throw new IllegalArgumentException("This email is already in use. Try another email");
        }


    }

    //Giriş işlemleri.Repository katmanını kullanır.
    @Override
    public User login(String email, String password) {
        User user = userRepository.findUserByEmail(email);
        if (user != null) {
            if (user.verifyPassword(password)) {
                return user;
            }

        }
        return null;
    }

    //User Id ye göre kullanıcı getirme.Repository katmanını kullanır.
    @Override
    public User getUserById(UUID userId) {
        return userRepository.findUserById(userId);
    }

    //Kullanıcı güncelleme.Repository katmanını kullanır.
    @Override
    public User updateUser(UUID userId, String username, String password, User.UserRole role) {


        if (username.isEmpty()) {
            throw new IllegalArgumentException("The username field cannot be empty");
        }
        if (username.length() < 4) {
            throw new IllegalArgumentException(" The username must be at least 3 characters long.");
        }
        if (!Validator.validatePassword(password)) {
            throw new IllegalArgumentException("Your password does not meet the requirements. You must enter a password that is at least 8 characters long and contains at least 4 letters.");
        }

        if (!(userRepository.findUserByPassword(password))) {
            User user = userRepository.findUserById(userId);
            userRepository.updateUser(user, username, password, role);
            return user;
        } else {
            throw new IllegalArgumentException("This password is already in use. Try another password");
        }


    }

    //Kullanıcı silme.Repository katmanını kullanır.
    @Override
    public void deleteUser(UUID userId) {
        userRepository.deleteUser(userId);
    }

}
