package com.mindex.challenge.service;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    // TODO docs
    // TODO interface?
    public ReportingStructure read(String id) {
        LOG.debug("Getting ReportingStructure for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        ReportingStructure reportingStructure = new ReportingStructure(employee, getNumberOfReports(id));

        return reportingStructure;
    }

    /*
         //TODO JAVA DOCS AND FORMAT THEM
     */
    private int getNumberOfReports(String employeeId) {
        Employee employee = employeeRepository.findByEmployeeId(employeeId);//LOOKUP EMPLOYEE BY ID
        List<Employee> directReports = employee.getDirectReports();
        int returnValue = 0;
        if (directReports != null && !directReports.isEmpty()) {
            // Assuming no circular directReports
            for (Employee e : directReports) {
                returnValue++;
                returnValue += getNumberOfReports(e.getEmployeeId());
            }
        }
        return returnValue;
    }
}
