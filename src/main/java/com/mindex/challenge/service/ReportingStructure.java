package com.mindex.challenge.service;

import java.util.List;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import org.springframework.beans.factory.annotation.Autowired;

public class ReportingStructure {
    private Employee employee;
    private int numberOfReports;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ReportingStructure(String employeeId){
        this.employee = employeeRepository.findByEmployeeId(employeeId);
        this.numberOfReports = getNumberOfReports(employeeId);
    }

    /*
         //TODO JAVA DOCS AND FORMAT THEM
     */
    private int getNumberOfReports(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId);//LOOKUP EMPLOYEE BY ID
        List<Employee> directReports = employee.getDirectReports();
        int returnValue = 0;
        if (directReports != null && !directReports.isEmpty()) {
            // Assuming no circular dependencies
            for (Employee e : directReports) {
                returnValue += getNumberOfReports(e.getEmployeeId());
            }
        }
        return returnValue;
    }
}
