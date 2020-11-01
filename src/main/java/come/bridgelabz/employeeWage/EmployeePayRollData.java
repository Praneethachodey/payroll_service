package come.bridgelabz.employeeWage;

import java.time.LocalDate;

public class EmployeePayRollData {
	int id;
	String name;
	double salary;
	LocalDate start;

	public EmployeePayRollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayRollData(int id, String name, double salary, LocalDate start) {
		this(id, name, salary);
		this.start = start;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		EmployeePayRollData other = (EmployeePayRollData) obj;
		return id==other.id && Double.compare(other.salary,salary)==0 && name.equals(other.name);
	}

	public String toString() {
		return "id: " + this.id + " name: " + this.name + " salary: " + this.salary;
	}
}
