package repositories;

import models.User;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserRepository implements UserRepositoryInt {
    private List<User> users = new ArrayList<>();
    private static final String USER_DATA_FILE = "users.txt";

    //Kullanıcıyı kaydetme.
    @Override
    public User saveUser(String username, String email, String password, User.UserRole role) {
        User newUser = new User(username, email, password, role);
        newUser.setHash(password);
        users.add(newUser);
        saveUsersToFile();
        return newUser;
    }

    //Kullanıcıları getirme.
    public List<User> getUsers() {
        return users.stream().filter(user -> !(user.getRole().equals(User.UserRole.ADMIN))).collect(Collectors.toList());
    }

    //Constructorlar.
    public UserRepository() {
        this.users = loadUsersFromFile();
    }

    //Kullanıcıyı userId ya göre bulma.
    @Override
    public User findUserById(UUID userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    //Kullanıcıyı password a göre bulma
    public boolean findUserByPassword(String password) {
        return users.stream()
                .anyMatch(user -> user.verifyPassword(password));
    }

    //Kullanıcıyı email e göre bulma.
    @Override
    public User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst().orElse(null);
    }

    //Kullanıcıyı güncelleme.
    @Override
    public void updateUser(User user, String username, String password, User.UserRole role) {

        if (user != null) {
            user.setUsername(username);
            user.setHashedPassword(password);
            user.setRole(role);
            user.setHash(password);
            saveUsersToFile();
        }
    }

    //Kullanıcıyı silme.
    @Override
    public void deleteUser(UUID userId) {
        users.removeIf(user -> user.getUserId().equals(userId) && user.getRole().equals(User.UserRole.CUSTOMER));
        saveUsersToFile();
    }


    //Kullanıcıları dosyaya kaydeden yardımcı metot.
    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (User user : users) {
                String userLine = user.getUserId() + ";" +
                        user.getUsername() + ";" +
                        user.getEmail() + ";" +
                        user.getHashedPassword() + ";" +
                        user.getRole() + ";" +
                        user.getRegistrationDate() + ";" +
                        user.isActive();
                writer.write(userLine);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Dosyadan kullanıcıları yükleyen yardımcı metot.
    private List<User> loadUsersFromFile() {
        List<User> usersList = new ArrayList<>();
        File file = new File(USER_DATA_FILE);

        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] userData = line.split(";");
                    UUID userId = UUID.fromString(userData[0]);
                    String username = userData[1];
                    String email = userData[2];
                    String hashedPassword = userData[3];
                    User.UserRole role = User.UserRole.valueOf(userData[4]);
                    boolean isActive = Boolean.parseBoolean(userData[6]);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate registrationDate = LocalDate.parse(userData[5], formatter);

                    User user = new User(userId, registrationDate, username, email, hashedPassword, role);
                    user.setUserId(userId);
                    user.setRegistrationDate(registrationDate);
                    user.setActive(isActive);

                    usersList.add(user);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return usersList;
    }
}
