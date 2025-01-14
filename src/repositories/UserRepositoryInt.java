package repositories;


import models.User;

import java.util.UUID;

public interface UserRepositoryInt {
    User saveUser(String username, String email, String password, User.UserRole role);

    User findUserById(UUID userId);

    User findUserByEmail(String email);

    void updateUser(User user, String username, String password, User.UserRole role);

    void deleteUser(UUID userId);
}
