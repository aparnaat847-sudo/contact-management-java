package com.contacts.service;

import com.contacts.model.Contact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Business logic: ArrayList keeps display order, HashMap gives O(1) lookup by id. */
public class ContactService {
    private final ContactRepository repository;
    private final List<Contact> contacts = new ArrayList<>();
    private final Map<String, Contact> byId = new HashMap<>();

    public ContactService(ContactRepository repository) throws StorageException {
        this.repository = repository;
        try {
            for (Contact c : repository.load()) {
                contacts.add(c);
                byId.put(c.getId(), c);
            }
        } catch (IOException e) {
            throw new StorageException("Could not read contacts file: " + e.getMessage(), e);
        }
    }

    public List<Contact> getAll() {
        return new ArrayList<>(contacts);
    }

    public Contact add(String name, String phone, String email) throws ValidationException, StorageException {
        name = name.trim(); phone = phone.trim(); email = email.trim();
        ContactValidator.validate(name, phone, email);
        ensurePhoneUnique(phone, null);
        Contact c = new Contact(UUID.randomUUID().toString(), name, phone, email);
        contacts.add(c);
        byId.put(c.getId(), c);
        try {
            persist();
        } catch (StorageException e) {
            contacts.remove(c);
            byId.remove(c.getId());
            throw e;
        }
        return c;
    }

    public void update(String id, String name, String phone, String email)
            throws ValidationException, StorageException {
        Contact c = byId.get(id);
        if (c == null) throw new ValidationException("Select a contact to edit first.");
        name = name.trim(); phone = phone.trim(); email = email.trim();
        ContactValidator.validate(name, phone, email);
        ensurePhoneUnique(phone, id);
        String oldName = c.getName(), oldPhone = c.getPhone(), oldEmail = c.getEmail();
        c.setName(name); c.setPhone(phone); c.setEmail(email);
        try {
            persist();
        } catch (StorageException e) {
            c.setName(oldName); c.setPhone(oldPhone); c.setEmail(oldEmail);
            throw e;
        }
    }

    public void delete(String id) throws ValidationException, StorageException {
        Contact c = byId.get(id);
        if (c == null) throw new ValidationException("Select a contact to delete first.");
        int index = contacts.indexOf(c);
        contacts.remove(c);
        byId.remove(id);
        try {
            persist();
        } catch (StorageException e) {
            contacts.add(index, c);
            byId.put(id, c);
            throw e;
        }
    }

    /** Case-insensitive search by name or phone. */
    public List<Contact> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.trim().toLowerCase(Locale.ROOT);
        List<Contact> result = new ArrayList<>();
        for (Contact c : contacts) {
            if (c.getName().toLowerCase(Locale.ROOT).contains(q) || c.getPhone().contains(q)) {
                result.add(c);
            }
        }
        return result;
    }

    private void ensurePhoneUnique(String phone, String ignoreId) throws ValidationException {
        for (Contact c : contacts) {
            if (c.getPhone().equals(phone) && !c.getId().equals(ignoreId)) {
                throw new ValidationException("A contact with this phone number already exists.");
            }
        }
    }

    private void persist() throws StorageException {
        try {
            repository.save(contacts);
        } catch (IOException e) {
            throw new StorageException("Could not save contacts: " + e.getMessage(), e);
        }
    }
}
