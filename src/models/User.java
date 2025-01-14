package models;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;

import java.util.Base64;
import java.util.UUID;

public class User {
    private UUID userId;
    private String username;
    private String email;
    private String hashedPassword;
    private UserRole role;
    private LocalDate registrationDate;
    private boolean isActive;


    public boolean isActive() {
        return isActive;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setHash(String password) {
        String hashedPassword = hashPassword(password);
        setHashedPassword(hashedPassword);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    // Kullanıcı rolleri
    public enum UserRole {
        CUSTOMER, ADMIN
    }

    private void applyUserProperties(String username, String email, UserRole role) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.isActive = true;
    }

    public User(String username, String email, String password, UserRole role) {
        this.userId = UUID.randomUUID();
        this.hashedPassword = password;
        this.registrationDate = LocalDate.now();
        applyUserProperties(username, email, role);
    }

    public User(UUID uuid, LocalDate registrationDate, String username, String email, String password, UserRole role) {
        this.userId = uuid;
        this.hashedPassword = password;
        this.registrationDate = registrationDate;
        applyUserProperties(username, email, role);
    }


    //Şifre hashleme metodu.
    private String hashPassword(String password) {
        try {
            //SHA-256 ile şifreyi hashle.
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            //Hashi Base64 ile kodla ve döndür.
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algoritması bulunamadı", e);
        }
    }

    //Şifre doğrulama.
    public boolean verifyPassword(String inputPassword) {
        return this.hashedPassword.equals(hashPassword(inputPassword));
    }

    //Kullanıcı yetkilerini kontrol etme.
    public boolean hasAdminAccess() {
        return this.role == UserRole.ADMIN;
    }
}
