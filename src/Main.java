import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

interface Treatable {
    void receiveTreatment();
}

interface IClinic {
    void addAppointment(Patient p, Doctor d, LocalDateTime date);
    void displayPatientAppointments(Patient p);
    void displayDoctorCalendar(Doctor d);
    void addPrescription(Patient p, Prescription pre);
    void displayPatientPrescriptions(Patient p);
    void findPatientsByMedicineAndDoctor(String medicineName, Doctor doc);
}

class Treatment {
    protected String description;

    public Treatment(String description) {
        this.description = description;
    }

    public String getDescription() { return description; }

    @Override
    public String toString() { return description; }
}

class Medicine extends Treatment {
    private double dosage;

    public Medicine(String name, double dosage) {
        super(name);
        this.dosage = dosage;
    }

    @Override
    public String toString() { return description + " (" + dosage + "mg)"; }
}

class Appointment {
    private LocalDateTime date;
    private Patient patient;
    private Doctor doctor;

    public Appointment(LocalDateTime date, Patient patient, Doctor doctor) {
        this.date = date;
        this.patient = patient;
        this.doctor = doctor;
    }

    public LocalDateTime getDate() { return date; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }

    @Override
    public String toString() {
        return "Date: " + date + " | Doctor: " + doctor.getName();
    }
}

class Prescription {
    private Doctor doctor;
    private Patient patient;
    private List<Treatment> treatments;

    public Prescription(Doctor doctor, Patient patient) {
        this.doctor = doctor;
        this.patient = patient;
        this.treatments = new ArrayList<>();
    }

    public void addTreatment(Treatment t) { treatments.add(t); }
    public List<Treatment> getTreatments() { return treatments; }
    public Doctor getDoctor() { return doctor; }

    @Override
    public String toString() {
        return "Prescription by Dr. " + doctor.getName() + ": " + treatments;
    }
}

abstract class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }

    public void introduce() {
        System.out.println("Hi, I am " + name + " and I am " + age + " years old.");
    }
}

class Patient extends Person implements Treatable {
    private String condition;
    private List<Prescription> prescriptions;
    private List<Appointment> appointments;

    public Patient(String name, int age, String condition) {
        super(name, age);
        this.condition = condition;
        this.prescriptions = new ArrayList<>();
        this.appointments = new ArrayList<>();
    }

    public void addPrescription(Prescription p) { prescriptions.add(p); }
    public void addAppointment(Appointment a) { appointments.add(a); }
    public List<Prescription> getPrescriptions() { return prescriptions; }
    public List<Appointment> getAppointments() { return appointments; }

    @Override
    public void receiveTreatment() {
        System.out.println("Patient " + name + " is receiving treatment for: " + condition);
    }
}

abstract class Staff extends Person {
    protected String role;

    public Staff(String name, int age, String role) {
        super(name, age);
        this.role = role;
    }

    public abstract void performDuties();

    public boolean scheduleAppointment(Patient p, Doctor d, LocalDateTime date) {
        if (d.isAvailable(date)) {
            Appointment app = new Appointment(date, p, d);
            d.addAppointment(app);
            p.addAppointment(app);
            return true;
        }
        return false;
    }
}

class Doctor extends Staff {
    private List<Appointment> appointments;

    public Doctor(String name, int age) {
        super(name, age, "Doctor");
        this.appointments = new ArrayList<>();
    }

    public boolean isAvailable(LocalDateTime newDate) {
        for (Appointment a : appointments) {
            if (a.getDate().isEqual(newDate)) {
                return false;
            }
        }
        return true;
    }

    public void addAppointment(Appointment a) { appointments.add(a); }
    public List<Appointment> getAppointments() { return appointments; }

    public Prescription createPrescription(Patient p) {
        Prescription pres = new Prescription(this, p);
        p.addPrescription(pres);
        return pres;
    }

    @Override
    public void performDuties() {
        System.out.println("Dr. " + name + " is seeing patients.");
    }

    public void prescribeMedicine(String medicine) {
        System.out.println("Dr. " + name + " verbally prescribes: " + medicine);
    }
}

class Nurse extends Staff {
    public Nurse(String name, int age) { super(name, age, "Nurse"); }

    @Override
    public void performDuties() { System.out.println(role + " " + name + " is assisting doctors."); }

    public void checkVitals(Patient patient) {
        System.out.println("Nurse " + name + " checking vitals of " + patient.getName());
    }
}

class Receptionist extends Staff {
    public Receptionist(String name, int age) { super(name, age, "Receptionist"); }

    @Override
    public void performDuties() { System.out.println(role + " " + name + " is scheduling appointments."); }
}

class ClinicSystem implements IClinic {
    private List<Patient> registeredPatients = new ArrayList<>();

    public void registerPatient(Patient p) { registeredPatients.add(p); }

    @Override
    public void addAppointment(Patient p, Doctor d, LocalDateTime date) {
        if (d.isAvailable(date)) {
            Appointment app = new Appointment(date, p, d);
            d.addAppointment(app);
            p.addAppointment(app);
            System.out.println("[OK] Appointment set for " + p.getName() + " with Dr. " + d.getName() + " at " + date);
        } else {
            System.out.println("[ERROR] Dr. " + d.getName() + " is busy at " + date);
        }
    }

    @Override
    public void displayPatientAppointments(Patient p) {
        System.out.println("\n--- Appointments for " + p.getName() + " ---");
        for (Appointment a : p.getAppointments()) {
            System.out.println(a);
        }
    }

    @Override
    public void displayDoctorCalendar(Doctor d) {
        System.out.println("\n--- Calendar for Dr. " + d.getName() + " ---");
        for (Appointment a : d.getAppointments()) {
            System.out.println(a.getDate() + " with Patient: " + a.getPatient().getName());
        }
    }

    @Override
    public void addPrescription(Patient p, Prescription pre) {
        if(!p.getPrescriptions().contains(pre)) {
            p.addPrescription(pre);
        }
    }

    @Override
    public void displayPatientPrescriptions(Patient p) {
        System.out.println("\n--- Prescriptions for " + p.getName() + " ---");
        for (Prescription pre : p.getPrescriptions()) {
            System.out.println(pre);
        }
    }

    @Override
    public void findPatientsByMedicineAndDoctor(String medicineName, Doctor doc) {
        System.out.println("\n--- Searching: Patients with " + medicineName + " from Dr. " + doc.getName() + " ---");
        boolean found = false;
        for (Patient p : registeredPatients) {
            for (Prescription pre : p.getPrescriptions()) {
                if (pre.getDoctor() == doc) {
                    for (Treatment t : pre.getTreatments()) {
                        if (t instanceof Medicine && t.getDescription().equalsIgnoreCase(medicineName)) {
                            System.out.println("Found: " + p.getName());
                            found = true;
                        }
                    }
                }
            }
        }
        if (!found) System.out.println("No patients found.");
    }
}

public class ClinicTest {
    public static void main(String[] args) {
        System.out.println("=== CLINIC SYSTEM DEMO ===");

        ClinicSystem clinic = new ClinicSystem();

        Doctor docZosia = new Doctor("Zosia", 50);
        Nurse nurseHania = new Nurse("Hania", 30);
        Patient patientMaja = new Patient("Maja", 40, "Flu");
        Patient patientJula = new Patient("Jula", 22, "Broken Arm");

        clinic.registerPatient(patientMaja);
        clinic.registerPatient(patientJula);

        LocalDateTime now = LocalDateTime.now().withNano(0).withSecond(0);

        System.out.println("\n>>> Scheduling Appointments:");
        clinic.addAppointment(patientMaja, docZosia, now);
        clinic.addAppointment(patientJula, docZosia, now);
        clinic.addAppointment(patientJula, docZosia, now.plusHours(1));

        clinic.displayDoctorCalendar(docZosia);

        System.out.println("\n>>> Creating Prescriptions:");

        Prescription p1 = docZosia.createPrescription(patientMaja);
        p1.addTreatment(new Medicine("Ibuprofen", 400));
        p1.addTreatment(new Treatment("Bed rest"));

        Prescription p2 = docZosia.createPrescription(patientJula);
        p2.addTreatment(new Medicine("Ibuprofen", 400));
        p2.addTreatment(new Medicine("Calcium", 500));

        clinic.displayPatientPrescriptions(patientMaja);

        clinic.findPatientsByMedicineAndDoctor("Ibuprofen", docZosia);

        System.out.println("\n>>> Legacy Methods Check:");
        docZosia.introduce();
        patientMaja.receiveTreatment();
        nurseHania.checkVitals(patientMaja);
    }
}