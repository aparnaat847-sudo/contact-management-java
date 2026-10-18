package com.contacts.controller;

import com.contacts.model.Contact;
import com.contacts.service.ContactService;
import com.contacts.service.StorageException;
import com.contacts.service.ValidationException;
import com.contacts.view.ContactView;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.List;
/** Connects the view to the service. */
public class ContactController {
    private final ContactView view;
    private final ContactService service;

    public ContactController(ContactView view, ContactService service) {
        this.view = view;
        this.service = service;
        wire();
        refresh();
    }

    private void wire() {
        view.getAddButton().setOnAction(e -> onAdd());
        view.getUpdateButton().setOnAction(e -> onUpdate());
        view.getDeleteButton().setOnAction(e -> onDelete());
        view.getClearButton().setOnAction(e -> clearForm());
        view.getSearchField().textProperty().addListener((obs, o, n) -> refresh());
        view.getTable().getSelectionModel().selectedItemProperty().addListener((obs, o, c) -> {
            if (c != null) {
                view.getNameField().setText(c.getName());
                view.getPhoneField().setText(c.getPhone());
                view.getEmailField().setText(c.getEmail());
            }
        });
    }

    private void onAdd() {
        try {
            service.add(view.getNameField().getText(), view.getPhoneField().getText(),
                    view.getEmailField().getText());
            clearForm();
            refresh();
            status("Contact added.");
        } catch (ValidationException ex) {
            showError("Invalid input", ex.getMessage());
        } catch (StorageException ex) {
            showError("Storage error", ex.getMessage());
        }
    }

    private void onUpdate() {
        Contact selected = view.getTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Select a contact in the table to edit.");
            return;
        }
        try {
            service.update(selected.getId(), view.getNameField().getText(),
                    view.getPhoneField().getText(), view.getEmailField().getText());
            refresh();
            status("Contact updated.");
        } catch (ValidationException ex) {
            showError("Invalid input", ex.getMessage());
        } catch (StorageException ex) {
            showError("Storage error", ex.getMessage());
        }
    }

    private void onDelete() {
        Contact selected = view.getTable().getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("No selection", "Select a contact in the table to delete.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete " + selected.getName() + "?");
        confirm.setHeaderText(null);
        if (confirm.showAndWait().filter(b -> b == ButtonType.OK).isEmpty()) {
            return;
        }
        try {
            service.delete(selected.getId());
            clearForm();
            refresh();
            status("Contact deleted.");
        } catch (ValidationException ex) {
            showError("Invalid", ex.getMessage());
        } catch (StorageException ex) {
            showError("Storage error", ex.getMessage());
        }
    }

    private void refresh() {
        List<Contact> items = service.search(view.getSearchField().getText());
        view.getTable().setItems(FXCollections.observableArrayList(items));
    }

    private void clearForm() {
        view.getTable().getSelectionModel().clearSelection();
        view.getNameField().clear();
        view.getPhoneField().clear();
        view.getEmailField().clear();
    }

    private void status(String msg) {
        view.getStatusLabel().setText(msg);
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
        status(title + ": " + msg);
    }
}
