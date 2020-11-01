package come.bridgelabz.employeeWage;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.tools.javac.main.Main.Result;

public class EmployeePayrollDBService {
	private PreparedStatement employeePayrollDataStatement = null;
	private static EmployeePayrollDBService employeePayrollDBService;

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "root";
		Connection connection;
		System.out.println("connecting to database : " + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("connection is successfull " + connection);
		return connection;
	}

	public List<EmployeePayRollData> readData() {
		String sql = "SELECT * FROM employee_payroll;";
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public int updateEmployeeData(String name, double salary) {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary=%.2f where name ='%s';", salary, name);
		try {
			Connection connecton = this.getConnection();
			Statement statement = connecton.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public List<EmployeePayRollData> getEmployeePayrollData(String name) {
		List<EmployeePayRollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet result = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeeData(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private List<EmployeePayRollData> getEmployeeData(ResultSet resultSet) {
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayRollData(id, name, salary, startDate));

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	public List<EmployeePayRollData> getEmployeePayrollForDateRange(LocalDate start, LocalDate end) {

		String sql = String.format("select * from employee_payroll where start between '%s' and '%s';",
				Date.valueOf(start), Date.valueOf(end));
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public Map<String, Double> getAverageSalaryByGender() {
		String sql = "select gender, avg(salary) as avg_salary from employee_payroll group by gender;";
		return this.getMap(sql,"avg_salary");
	}

	public Map<String, Double> getSumSalaryByGender() {
		String sql = "select gender, sum(salary) as sum_salary from employee_payroll group by gender;";
		return this.getMap(sql, "sum_salary");
	}
		

	public Map<String, Double> getMaxByGender() {
		String sql = "select gender, max(salary) as max_salary from employee_payroll group by gender;";
		return this.getMap(sql, "max_salary");
	}
	
	public Map<String, Double> getMinByGender() {
		String sql = "select gender, min(salary) as min_salary from employee_payroll group by gender;";
		return this.getMap(sql, "min_salary");
	}
	public Map<String, Double> getCountByGender() {
		String sql = "select gender, count(salary) as count from employee_payroll group by gender;";
		return this.getMap(sql, "count");
	}

	
	public Map<String, Double> getMap(String sql,String field){
		Map<String, Double> genderToSalaryMap=new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				Double salary = resultSet.getDouble(field);
				genderToSalaryMap.put(gender,salary);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return genderToSalaryMap;
	
	}
	

	private List<EmployeePayRollData> getEmployeePayrollDataUsingDB(String sql) {
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeeData(resultSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollList;
	}

	private void prepareStatementForEmployeeData() {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT* FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

}