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
		return this.getMap(sql, "avg_salary");
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

	public Map<String, Double> getMap(String sql, String field) {
		Map<String, Double> genderToSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				Double salary = resultSet.getDouble(field);
				genderToSalaryMap.put(gender, salary);
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

	public EmployeePayRollData addEmployee(String name, double salary, LocalDate start, String gender) {
		int employeeId = -1;
		EmployeePayRollData employeePayrollData = null;
		String sql = String.format(
				"insert into employee_payroll(name,gender,salary,start) values ('%s','%s','%s','%s')", name, gender,
				salary, Date.valueOf(start));
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayRollData(employeeId, name, salary);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayRollData addEmployeeToBothTables(String name, Double salary, LocalDate start, String gender,
			String company, String department) throws SQLException {
		int employeeID = -1;
		Connection connection = null;
		EmployeePayRollData employeePayrollData = null;
		int company_id = 0;
		// to get connection
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);

		} catch (Exception e) {
			e.printStackTrace();
			connection.rollback();
		}
		// to get company id/ if not available insert
		try (Statement statement = connection.createStatement()) {
			company_id = 0;
			String sqlQueryForCompany = String.format("select id from company where name = '%s';", company);
			ResultSet result = statement.executeQuery(sqlQueryForCompany);
			if (result.next()) {
				company_id = result.getInt("id");
			} else {
				String sqlForCompanyInsert = String.format("insert into company(name) values ('%s');", company);
				int rowAffected = statement.executeUpdate(sqlForCompanyInsert, statement.RETURN_GENERATED_KEYS);
				if (rowAffected == 1) {
					ResultSet resultSet = statement.getGeneratedKeys();
					if (resultSet.next()) {
						company_id = resultSet.getInt(1);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}
		// to insert into employee payroll
		String sql = String.format(
				"insert into employee_payroll(name, gender, salary, start,company_id) "
						+ "values ( '%s', '%s', '%s', '%s','%d');",
				name, gender, salary, Date.valueOf(start), company_id);
		try (Statement statement = connection.createStatement()) {
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next()) {
					employeeID = resultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}
		// to insert into payroll_details
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxable_pay = salary - deductions;
			double tax = salary * 0.1;
			double net_pay = salary - tax;
			String sqlQuery = String.format("insert into payroll_details"
					+ "(employee_id, basic_pay, deductions, taxable_pay, tax, net_pay) values"
					+ "(%s, %s, %s, %s, %s, %s)", employeeID, salary, deductions, taxable_pay, tax, net_pay);
			int rowsAffected = statement.executeUpdate(sqlQuery);
			if (rowsAffected == 1) {
				employeePayrollData = new EmployeePayRollData(employeeID, name, salary, start);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}
		// to insert department id/ if not available insert
		try (Statement statement = connection.createStatement()) {
			String sqlQuery1 = String.format("select* from department where name='%s';", department);
			ResultSet resultSet = statement.executeQuery(sqlQuery1);
			int dept_id = -1;
			String sqlForDept = null;
			if (resultSet.next()) {
				dept_id = resultSet.getInt("id");
			} else {
				sqlForDept = String.format("insert into department(name) values ('%s');", department);
				int rowAffected = statement.executeUpdate(sqlForDept, statement.RETURN_GENERATED_KEYS);
				if (rowAffected == 1) {
					resultSet = statement.getGeneratedKeys();
					if (resultSet.next()) {
						dept_id = resultSet.getInt(1);
					}
				}
			}
			String sqlQueryforDeptTable = String
					.format("insert into employee_dept(emp_id,dept_id) values ('%d' , '%d');", employeeID, dept_id);
			statement.execute(sqlQueryforDeptTable);
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null)
				connection.close();
		}
		return employeePayrollData;
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

	public List<EmployeePayRollData> removeEmployee(String name) {
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			String sql = String.format("update employee_payroll set is_active=false where name = '%s';", name);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String sqlQuery="select* from employee_payroll where is_active=true;";
		return this.getEmployeePayrollDataUsingDB(sqlQuery);
	}
}