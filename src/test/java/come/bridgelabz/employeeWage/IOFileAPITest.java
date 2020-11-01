package come.bridgelabz.employeeWage;

import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.*;

import come.bridgelabz.employeeWage.EmployeePayrollService.IOService;

public class IOFileAPITest {

	private static String HOME = System.getProperty("user.home");
	private static String play = "tempPlay";

	@Test
	public void givenPathWhenCheckedConfirm() {
		Path homePath = Paths.get(HOME);
		Assert.assertTrue(Files.exists(homePath));

		Path playPath = Paths.get(HOME + "/" + play);
		// if(Files.exists(playPath))
		Assert.assertTrue(Files.notExists(playPath));
	}

	@Test
	public void givenEmployeePayrollInDB_shouldMatchEmployeeCount() {
		EmployeePayrollService employeeService = new EmployeePayrollService();
		List<EmployeePayRollData> data = employeeService.readEmployeeData(IOService.DB_IO);
		Assert.assertEquals(4, data.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_shouldMatch() {
		EmployeePayrollService employeeService = new EmployeePayrollService();
		List<EmployeePayRollData> employeePayrollData = employeeService.readEmployeeData(IOService.DB_IO);
		employeeService.updateEmployeeSalary("Praneetha", 4000000);
		boolean result = employeeService.checkEmployeeInSyncWithDB("Praneetha");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchCount() {
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		LocalDate start = LocalDate.of(2018, 01, 01);
		LocalDate end = LocalDate.now();
		List<EmployeePayRollData> employeePayrollData = employeeService.readEmployeePayrollForDateRange(IOService.DB_IO,
				start, end);
		Assert.assertEquals(3, employeePayrollData.size());

	}
	
	@Test
	public void givenPayroll_WhenAvgSalaryRetrievedByGender_shouldMatch()
	{
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeeService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("f").equals(3500000.0));
	}
	
	@Test
	public void givenPayroll_WhenSumRetrievedByGender_shouldMatch()
	{
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeeService.readSumSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("m").equals(7000000.0));
	}
	
	@Test
	public void givenPayroll_WhenMaxRetrievedByGender_shouldMatch()
	{
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeeService.readMaxSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("m").equals(4000000.0));
	}
	
	@Test
	public void givenPayroll_WhenMinRetrievedByGender_shouldMatch()
	{
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeeService.readMinSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("m").equals(3000000.0));
	}
	
	@Test
	public void givenPayroll_WhenCountRetrievedByGender_shouldMatch()
	{
		EmployeePayrollService employeeService = new EmployeePayrollService();
		employeeService.readEmployeeData(IOService.DB_IO);
		Map<String,Double> averageSalaryByGender = employeeService.readCountByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("m").equals(2.0));
	}
}