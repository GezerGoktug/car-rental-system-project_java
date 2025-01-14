package utils;

public class Validator {
    //Email format kontrolü yapar.
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return email.matches(emailRegex);
    }

    //Password format kontrolü yapar.
    public static boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        // At least 4 letters
        long letterCount = password.chars()
                .filter(Character::isLetter)
                .count();
        return letterCount >= 4;


    }
}
