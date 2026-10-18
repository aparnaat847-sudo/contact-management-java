package com.contacts.service;

import java.util.regex.Pattern;

/** Regex based field validation. */
public final class ContactValidator {
    private static final Pattern PHONE = Pattern.compile("^\\d{10}$");
    private static final Pattern EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    private ContactValidator() {}

    public static void validate(String name, String phone, String email) throws ValidationException {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name must not be empty.");
        }
        if (phone == null || !PHONE.matcher(phone).matches()) {
            throw new ValidationException("Phone must be exactly 10 digits (numbers only).");
        }
        if (email == null || !EMAIL.matcher(email).matches()) {
            throw new ValidationException("Email address is not valid (example: name@domain.com).");
        }
    }
}
