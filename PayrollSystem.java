import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class PayrollSystem {
    private static final double taxRate = 0.12; //0.12% tax rate;

    private static final double[] taxBrackets = {0, 10000, 20000, 30000, 40000, 50000};
    private static final double[] taxRates = {0.0, 0.1, 0.15, 0.2, 0.25, 0.3};

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MM-yyyy",Locale.ENGLISH);
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);

    private static final List<Employee> employees = new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args){
        boolean run = true;

        try{
            while(run){
                menu();
                int choice;
                try{
                    choice = Integer.parseInt(sc.nextLine().trim());
                }catch(NumberFormatException e){
                    System.out.println("Please Enter a Number : ");
                    continue;
                }
                switch(choice){
                    case 1:
                        addEmployee();
                        break;
                    case 2:
                        displayEmployees();
                        break;
                    case 3:
                        generateSlip();
                        break;
                    case 4:
                        generatePayroll();
                        break;
                    case 5:
                        run = false;
                        System.out.println("Exiting Program...");
                        break;   
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;            
                }
            }
                }catch(NoSuchElementException e){
                    System.out.println("Input Stream closed. Exiting program...");
            }
            sc.close();
        }

    public static void menu(){
        System.out.println("Payroll System Menu:");
        System.out.println("1. Add Employee");
        System.out.println("2. Display Employees");
        System.out.println("3. Generate Salary Slip");
        System.out.println("4. Generate Payroll");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static double calculateGrossSalary(Employee employee){
        return round2 (employee.getBasicSalary()+employee.getAllowance());
}

    private static double calculateTax(double grossSalary){
        double annualIncome = grossSalary * 12;
        double annualTax = 0.0;

        for (int i = 0; i < taxBrackets.length; i++) {
            double lowerLimit = taxBrackets[i];
            double upperLimit = (i + 1 < taxBrackets.length) ? taxBrackets[i + 1] : Double.MAX_VALUE;

            if (annualIncome <= lowerLimit) {
                break;
            }

            double taxableAmount = Math.min(annualIncome, upperLimit) - lowerLimit;
            annualTax += taxableAmount * taxRates[i];
        }
        return round2(annualTax / 12);
    }

    private static double calculateProvidentFund(Employee employee){
        return round2(employee.getBasicSalary() * taxRate);
    }

    private static double calculateTotalDeduction(double tax, double providentFund){
        return round2(tax + providentFund);
    }

    private static double calculateNetSalary(double grossSalary, double totalDeduction){
        return round2(grossSalary - totalDeduction);
    }
    private static double round2(double value){
        return Math.round(value *100)/100;
    }

    private static void addEmployee(){
        int id = readInt("Enter Employee ID: ");
        if(id <= 0){
            System.out.println("ID must be greater than 0");
            return;
        }
        if(findById(id) != null){
            System.out.println("Employee with "+id+" already exists");
            return;
        }

        String name = readName("Employee Name: ");
        double basicSalary = readSalary("Basic Salary: ");
        double allowance = readAllowance("Allowance: ");

        try{
            Employee emp = new Employee(id, name, basicSalary, allowance);
            employees.add(emp);
            System.out.println("Employee Added Successfully!");
        }catch(IllegalArgumentException e){
            System.out.println("ERROR : "+e.getMessage());
        }
    }

    private static void displayEmployees(){
        if(employees.isEmpty()){
            System.out.println("No employees found.");
            return;
        }
        System.out.println();
        System.out.println(String.format("| %-5s | %-25s | %13s | %12s | %13s|", "ID", "Name", "Basic Salary", "Allowances", "Gross Salary"));
        System.out.println(separator(5,25,13,12,13));

        for(Employee e : employees){
            System.out.println(String.format("| %-5d | %-25s | %13.2f | %12.2f | %13.2f|", e.getId(),e.getName(), e.getBasicSalary(),e.getAllowance(), calculateGrossSalary(e)));         
        }
    System.out.println("Total Employees: "+employees.size());
    }

    private static void generateSlip(){
        int id = readInt("Enter Employee ID: ");
        Employee employee = findById(id);

        if(employee == null){
            System.out.println("Emplyee with id : "+id+" not found!");
            return;
        }
        System.out.println();
        printSalarySlip(employee);
    }

    private static void printSalarySlip(Employee employee){
        double gross = calculateGrossSalary(employee);
        double tax = calculateTax(gross);
        double providentFund = calculateProvidentFund(employee);
        double totalDeduction = calculateTotalDeduction(tax, providentFund);
        double net = calculateNetSalary(gross, totalDeduction);

        System.out.print(Line('='));
        System.out.println("Salary Slip . "+YearMonth.now().format(MONTH_FORMAT));
        System.out.print(Line('='));
        System.out.println(String.format("%-14s: %s","Employee ID", employee.getId()));
        System.out.println(String.format("%-14s: %s","Employee Name", employee.getName()));
        System.out.print(Line('-'));

        System.out.println("EARNINGS");
        System.out.println(String.format(" %-14s: %13.2f", "Basic Salary", employee.getBasicSalary()));
        System.out.println(String.format(" %-14s: %13.2f", "Allowance", employee.getAllowance()));
        System.out.print(Line('-'));
        System.out.println("DEDUCTIONS");
        System.out.println(String.format(" %-14s: %13.2f", "Tax", tax));
        System.out.println(String.format(" %-14s: %13.2f", "Provident Fund", providentFund));
        System.out.println(String.format("%-14s: %13.2f","Deduction", totalDeduction));
        System.out.print(Line('-'));
        System.out.println(String.format(" %-14s: %13.2f", "Net Salary", net));
        System.out.print(Line('='));
        System.out.println("Slip Got Generated : "+ LocalDateTime.now().format(dateFormatter)+" "+LocalDateTime.now().format(timeFormatter));        
    }

    private static void generatePayroll(){
        if(employees.isEmpty()){
            System.out.println("No Employee Found");
            return;
        }

        System.out.println("PayRoll---"+ YearMonth.now().format(MONTH_FORMAT));
        System.out.println(String.format("| %-5s | %-25s | %13s | %12s | %13s|", "ID", "Name", "Basic Salary", "Allowances", "Gross Salary"));
        System.out.println(separator(5,25,13,12,13));

        double totalGross = 0;
        double totalDeduction = 0;
        double totalNet = 0;

        for(Employee employee : employees){
            double gross = calculateGrossSalary(employee);
            double tax = calculateTax(gross);
            double providentFund = calculateProvidentFund(employee);
            double net = calculateNetSalary(gross, calculateTotalDeduction(tax, providentFund));

            System.out.println(String.format("| %-5s | %-25s | %13.2f | %12.2f | %13.2f|", employee.getId(), employee.getName(), employee.getBasicSalary(), employee.getAllowance(), gross));
            totalGross += gross;
            totalDeduction += calculateTotalDeduction(tax, providentFund);
            totalNet += net;
        }
        System.out.println(separator(5,25,13,12,13));
        System.out.println(String.format("| %-5s | %-25s | %13.2f | %12.2f | %13.2f|", "", "Total", totalGross, totalDeduction, totalNet));
        System.out.println("Employees Paid : "+employees.size());
    }

    private static Employee findById(int id){
        for(Employee employee: employees){
            if(employee.getId() == id){
                return employee;
            }
        }
        return null;
    }

    private static String Line(char c){
        char[] chars = new char[55];
        Arrays.fill(chars, c);
        return new String(chars);
    }

    private static String separator(int... widths){
        StringBuilder sb = new StringBuilder("|");
        for(int width : widths){
            for(int i = 0; i < width+2 ; i++){
                sb.append('-');
            }
            sb.append('|');
        }
        return sb.toString();
    }

    private static int readInt(String prompt){
        while(true){
            System.out.print(prompt);
            try{
                return Integer.parseInt(sc.nextLine().trim());
            }catch(NumberFormatException e){
                System.out.println("Enter a valid. Integer...");
            }
        }
    }

    private static String readName(String prompt){
        while(true){
            System.out.print(prompt);
            String name = sc.nextLine().trim();
            if(name.isEmpty()){
                System.out.println("Name must not be empty");
            }else if(name.length() > Employee.MAX_LEN){
                System.out.println("Name must be less than "+Employee.MAX_LEN+1);
            }else{
                return name;
            }
        }
    }

    private static double readSalary(String prompt){
        return readAmount(prompt);
    }

    private static double readAllowance(String prompt){
        return readAmount(prompt);
    }

    private static double readAmount(String prompt){
        while(true){
            System.out.print(prompt);

            try{
                double value = Double.parseDouble(sc.nextLine().trim());
                
                if(Double.isNaN(value) || Double.isInfinite(value)){
                    System.out.println("Invalid Input. Give a valid input");
                }else if(value < 0){
                    System.out.println("Value must be 0 or great");
                }else{
                    return value;
                }
            }catch(NumberFormatException e){
                System.out.println("Invalid Input. Please Enter a valid input");
            }
        }
    }
}
