package com.mindex.challenge.data;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;

public class ReportingStructure {
    private Employee employee;

    private int numberOfReports;

    public Employee getEmployee() {
        return employee;
    }

    public int getNumberOfReports() {
        return numberOfReports;
    }

    public ReportingStructure(Employee employee, int numberOfReports){
        this.employee = employee;
        this.numberOfReports = numberOfReports;
    }
}