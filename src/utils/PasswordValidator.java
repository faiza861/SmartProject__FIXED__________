package utils;

public class PasswordValidator {
    public static boolean isValid(String p) {
        if (p == null || p.length() < 6) return false;
        boolean hasDigit = p.chars().anyMatch(Character::isDigit);
        return hasDigit;
    }
}
