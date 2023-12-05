package com.mindex.challenge;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {
	private String employeeUrl;
	private String reportingStructureUrl;
	private String compensationUrl;
	private String compensationIdUrl;

	@Autowired
	private ReportingStructureService reportingStructureService;

	@Autowired
	private CompensationService compensationService;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setup() {
		employeeUrl = "http://localhost:" + port + "/employee";
		reportingStructureUrl = "http://localhost:" + port + "/reportingStructure/{id}";
		compensationUrl = "http://localhost:" + port + "/compensation";
		compensationIdUrl = "http://localhost:" + port + "/compensation/{id}";
	}

	@Test
	public void testReportingStructure() {
		/*
		Create employees with the following structure:
		  F
		 /|\
		E C D
		  |\
		  A B
		 */
		Employee testEmployeeA = createEmployee("Bob", "Smith", "Software", "Developer");
		Employee testEmployeeB = createEmployee("James", "Johnson", "Software", "Developer");
		List<Employee> directReportsOfC = Arrays.asList(new Employee[]{testEmployeeA, testEmployeeB});
		Employee testEmployeeC = createEmployee("Jim", "Stewart", "Software", "Tech Lead", directReportsOfC);
		Employee testEmployeeD = createEmployee("Joe", "Mo", "HR", "HR Representative");
		Employee testEmployeeE = createEmployee("Carly", "Kennel", "Sales", "Sales Representative");
		List<Employee> directReportsOfF = Arrays.asList(new Employee[]{testEmployeeC, testEmployeeD, testEmployeeE});
		Employee testEmployeeF = createEmployee("Fred", "Baker", "Executive", "CEO", directReportsOfF);

		Employee[] employees = new Employee[] {testEmployeeA, testEmployeeB, testEmployeeC, testEmployeeD, testEmployeeE, testEmployeeF};

		for (Employee employee : employees) {
			assertNotNull(employee.getEmployeeId());
			ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employee.getEmployeeId()).getBody();
			//assert reportingStructure
		}


	}

	private Employee createEmployee(String firstName, String lastName, String department, String position) {
		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setDepartment(department);
		employee.setPosition(position);
		Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
		return createdEmployee;
	}

	private Employee createEmployee(String firstName, String lastName, String department, String position, List<Employee> directReports) {
		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setDepartment(department);
		employee.setPosition(position);
		employee.setDirectReports(directReports);
		Employee createdEmployee = restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
		return createdEmployee;
	}

}
