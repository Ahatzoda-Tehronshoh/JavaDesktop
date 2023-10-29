package Journal.Model;

import java.util.Vector;

public class Student {
    private final int id;
    private final String fio;
    private final int idGroup;
    private final String dateOfBirthday;
    private final String email;
    private final String phoneNumber;
    private final String parent1Name;
    private final String parent2Name;
    private final String parent1Phone;
    private final String parent2Phone;
    private final String address;
    private final int yearOfEnrollment;

    public Vector<String> asVector(int i) {
        Vector<String> vector = new Vector<>();
        vector.add((i) + "");
        vector.add(fio);
        vector.add(dateOfBirthday);
        vector.add(email);
        vector.add(phoneNumber);
        vector.add(parent1Name);
        vector.add(parent2Name);
        vector.add(parent1Phone);
        vector.add(parent2Phone);
        vector.add(address);
        vector.add(yearOfEnrollment + "");
        return vector;
    }

    public int getId() {
        return id;
    }

    public String getFio() {
        return fio;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getDateOfBirthday() {
        return dateOfBirthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getParent1Name() {
        return parent1Name;
    }

    public String getParent2Name() {
        return parent2Name;
    }

    public String getParent1Phone() {
        return parent1Phone;
    }

    public String getParent2Phone() {
        return parent2Phone;
    }

    public String getAddress() {
        return address;
    }

    public int getYearOfEnrollment() {
        return yearOfEnrollment;
    }

    public Student(int id, String fio, int idGroup, String dateOfBirthday, String email, String phoneNumber, String parent1Name, String parent2Name, String parent1Phone, String parent2Phone, String address, int yearOfEnrollment) {
        this.id = id;
        this.fio = fio;
        this.idGroup = idGroup;
        this.dateOfBirthday = dateOfBirthday;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.parent1Name = parent1Name;
        this.parent2Name = parent2Name;
        this.parent1Phone = parent1Phone;
        this.parent2Phone = parent2Phone;
        this.address = address;
        this.yearOfEnrollment = yearOfEnrollment;
    }
}
