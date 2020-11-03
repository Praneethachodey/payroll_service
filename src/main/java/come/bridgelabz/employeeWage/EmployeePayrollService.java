package come.bridgelabz.employeeWage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmployeePayrollService {
	public enum IOService {
		CONSOLE_IO, FILE_IO, OTHER_IO, DB_IO
	};

	public static List<EmployeePayRollData> employeePayrollList;
	EmployeePayrollDBService dbService = null;

	public EmployeePayrollService(List<EmployeePayRollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;

	}

	public EmployeePayrollService() {
		dbService = new EmployeePayrollDBService();
	}

	public static void main(String[] args) {
		employeePayrollList = new ArrayList<>();
		EmployeePayrollService employee = new EmployeePayrollService();
		Scanner ConsoleReader = new Scanner(System.in);
		employee.readEmployeeData(ConsoleReader);
		employee.writeEmployeeData();
	}

	public void readEmployeeData(Scanner employee) {
		System.out.println("enter employee id");
		int id = employee.nextInt();
		System.out.println("enter employee name");
		String name = employee.next();
		System.out.println("enter employee salary");
		double salary = employee.nextDouble();
		employeePayrollList.add(new EmployeePayRollData(id, name, salary));
	}

	public void writeEmployeeData() {
		System.out.println("\n writing employee payroll data in console : " + employeePayrollList);
	}

	public List<EmployeePayRollData> readEmployeeData(IOService type) {
		if (type.equals(IOService.DB_IO)) {
			this.employeePayrollList = dbService.readData();
			return employeePayrollList;

		}
		return null;
	}

	public List<EmployeePayRollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate start,
			LocalDate end) {
		if (ioService.equals(IOService.DB_IO))
			return dbService.getEmployeePayrollForDateRange(start, end);
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) {
		return dbService.getAverageSalaryByGender();
	}

	public Map<String, Double> readSumSalaryByGender(IOService dbIo) {
		return dbService.getSumSalaryByGender();
	}

	public Map<String, Double> readMaxSalaryByGender(IOService dbIo) {
		return dbService.getMaxByGender();

	}

	public Map<String, Double> readMinSalaryByGender(IOService dbIo) {
		return dbService.getMinByGender();
	}

	public Map<String, Double> readCountByGender(IOService dbIo) {
		return dbService.getCountByGender();
	}

	public void updateEmployeeSalary(String name, double salary) {
		int result = dbService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayRollData employeePayRollData = this.getEmployeePayrollData(name);
		if (employeePayRollData != null)
			employeePayRollData.salary = salary;

	}

	public void addEmployeeToPayroll(String name, double salary, LocalDate start, String gender) {
		employeePayrollList.add(dbService.addEmployee(name, salary, start, gender));
	}

	public void addEmployeeToPayrollBothTables(String name, double salary, LocalDate start, String gender,String company,String dept) throws SQLException {
		employeePayrollList.add(dbService.addEmployeeToBothTables(name, salary, start, gender,company,dept));
	}

	private EmployeePayRollData getEmployeePayrollData(String name) {
		return employeePayrollList.stream()
				.filter(employeePayRollDataItem -> employeePayRollDataItem.name.equalsIgnoreCase(name)).findFirst()
				.orElse(null);
	}

	public boolean checkEmployeeInSyncWithDB(String name) {
		List<EmployeePayRollData> employeePayrollDataList = dbService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public boolean removeEmployee(String name) {
		EmployeePayRollData employeedata = employeePayrollList.stream().filter(employee -> employee.name.equalsIgnoreCase(name)).findFirst().orElse(null);
		employeePayrollList.remove(employeedata);
		return dbService.removeEmployee(name).size()==employeePayrollList.size();		
	}
}
