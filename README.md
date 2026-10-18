# Contact Management App (JavaFX)

Add, view, search (name or phone), edit and delete contacts in a JavaFX window,
with regex validation and CSV persistence.

## Structure
```
src/main/java/com/contacts/
  MainApp.java                   JavaFX entry point (opens the window)
  model/Contact.java             Model
  view/ContactView.java          View (UI only)
  controller/ContactController   Controller (event handling)
  service/ContactService         Logic (ArrayList + HashMap)
  service/ContactValidator       Regex validation (10-digit phone, email)
  service/ContactRepository      CSV file I/O (temp file + atomic replace)
data/contacts.csv                Sample data
```

## Requirements
- JDK 17 or 21
- Maven 3.8+ (JavaFX is downloaded automatically)

## Build and run
```
mvn clean javafx:run
```
Compile only: `mvn clean compile`

### Without Maven (manual javac)
Download the JavaFX SDK, then from the project root:
```
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -d out $(find src -name "*.java")
java  --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp out com.contacts.MainApp
```
Run from the project root so `data/contacts.csv` is found.

## Validation rules
- Name: not empty
- Phone: exactly 10 digits
- Email: standard `name@domain.tld` syntax
- Phone numbers must be unique

## Data persistence
Every add, edit and delete writes `data/contacts.csv` immediately. The file is
written to a `.tmp` file first and then swapped in, so a crash cannot leave a
half-written file. If saving fails, the in-memory change is rolled back and an
error dialog is shown.

## Screenshots
Run the app and save your screenshots into `screenshots/`
(main window, validation error, search result), then link them here.
