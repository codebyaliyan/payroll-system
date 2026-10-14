public class Employee {

    public static final int MAX_LEN = 25;

    private final int id;
    private final String name;
    private final double basicSalary;
    private final double allowance;

    public Employee(int id, String name, double basicSalary, double allowance) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be greater than 0");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_LEN) {
            throw new IllegalArgumentException("Name must be <= " + MAX_LEN + " characters");
        }

        if (Double.isNaN(basicSalary) || Double.isInfinite(basicSalary) || basicSalary < 0) {
            throw new IllegalArgumentException("Basic salary must be >= 0");
        }

        if (Double.isNaN(allowance) || Double.isInfinite(allowance) || allowance < 0) {
            throw new IllegalArgumentException("Allowance must be >= 0");
        }

        this.id = id;
        this.name = trimmedName;
        this.basicSalary = basicSalary;
        this.allowance = allowance;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getBasicSalary() { return basicSalary; }
    public double getAllowance() { return allowance; }

    @Override
    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Basic: " + basicSalary + " | Allowance: " + allowance;
    }
}