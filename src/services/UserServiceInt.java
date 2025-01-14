package services;

import models.User;
import java.util.UUID;

public interface UserServiceInt {
    User registerUser(String username, String email, String password, User.UserRole role);

    User login(String email, String password);

    User getUserById(UUID userId);

    User updateUser(UUID userId, String username, String password, User.UserRole role);

    void deleteUser(UUID userId);
}
