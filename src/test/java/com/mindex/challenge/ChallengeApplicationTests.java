package com.mindex.challenge;

import com.mindex.challenge.data.Compensation;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTests {
	private String employeeUrl;
	private String reportingStructureUrl;
	private String compensationUrl;
	private String compensationIdUrl;

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
		C D E
	    |\
	    A B
		 */
		int[] numberOfReportsPerEmployee = {0,0,2,0,0,5};

		Employee testEmployeeA = createEmployee("Bob", "Smith", "Software", "Developer");
		Employee testEmployeeB = createEmployee("James", "Johnson", "Software", "Developer");
		List<Employee> directReportsOfC = Arrays.asList(new Employee[]{testEmployeeA, testEmployeeB});
		Employee testEmployeeC = createEmployee("Jim", "Stewart", "Software", "Tech Lead", directReportsOfC);
		Employee testEmployeeD = createEmployee("Joe", "Mo", "HR", "HR Representative");
		Employee testEmployeeE = createEmployee("Carly", "Kennel", "Sales", "Sales Representative");
		List<Employee> directReportsOfF = Arrays.asList(new Employee[]{testEmployeeC, testEmployeeD, testEmployeeE});
		Employee testEmployeeF = createEmployee("Fred", "Baker", "Executive", "CEO", directReportsOfF);

		Employee[] employees = new Employee[] {testEmployeeA, testEmployeeB, testEmployeeC, testEmployeeD, testEmployeeE, testEmployeeF};

		for (int i = 0; i < employees.length; i++) {
			assertNotNull(employees[i].getEmployeeId());
			ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, employees[i].getEmployeeId()).getBody();
			assertNotNull(reportingStructure);
            assertEquals(numberOfReportsPerEmployee[i], reportingStructure.getNumberOfReports());
			assertEmployeeEquivalence(employees[i], reportingStructure.getEmployee());
		}
	}

	@Test
	public void testCompensation() {
		Employee testEmployee = createEmployee("Bob", "Smith", "Software", "Developer");
		assertNotNull(testEmployee);

		Compensation testCompensation = new Compensation();
		testCompensation.setEmployee(testEmployee);
		testCompensation.setSalary(90000);
		testCompensation.setEffectiveDate(LocalDate.of(2023,12,1));

		Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
		assertNotNull(createdCompensation);
		assertCompensationEquivalence(testCompensation, createdCompensation);

		Compensation readCompensation = restTemplate.getForEntity(compensationIdUrl, Compensation.class, createdCompensation.getEmployee().getEmployeeId()).getBody();
		assertNotNull(readCompensation);
		assertCompensationEquivalence(createdCompensation, readCompensation);

	}

	private Employee createEmployee(String firstName, String lastName, String department, String position) {
		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setDepartment(department);
		employee.setPosition(position);
		return restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
	}

	private Employee createEmployee(String firstName, String lastName, String department, String position, List<Employee> directReports) {
		Employee employee = new Employee();
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setDepartment(department);
		employee.setPosition(position);
		employee.setDirectReports(directReports);
		return restTemplate.postForEntity(employeeUrl, employee, Employee.class).getBody();
	}

	private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
		assertEquals(expected.getFirstName(), actual.getFirstName());
		assertEquals(expected.getLastName(), actual.getLastName());
		assertEquals(expected.getDepartment(), actual.getDepartment());
		assertEquals(expected.getPosition(), actual.getPosition());
	}

	private static void assertCompensationEquivalence(Compensation expected, Compensation actual) {
		assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
		assertEquals(expected.getSalary(), actual.getSalary());
		assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
	}
}
