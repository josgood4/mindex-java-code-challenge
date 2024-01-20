package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {
    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String employeeId) {
        LOG.debug("Getting ReportingStructure for employee with id [{}]", employeeId);

        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + employeeId);
        }

        int numReports = calculateNumberOfReports(employee);

        return new ReportingStructure(employee, numReports);
    }

    private int calculateNumberOfReports(Employee employee) {
        List<Employee> directReports = employee.getDirectReports();
        int returnValue = 0;
        // The numberOfReports will be equal to the sum of the following:
        //   -> each directReport immediately under the current employee
        //   -> numberOfReports value for all employees under the current employee
        // Loop also builds out nested Employee structure to save in the ReportingStructure
        if (directReports != null && !directReports.isEmpty()) {
            returnValue = directReports.size();
            List<Employee> upToDateSubs = new ArrayList<Employee>();
            for (Employee subEmployee : directReports) {
                Employee fullSubEmployee =
                        employeeRepository.findByEmployeeId(subEmployee.getEmployeeId());
                returnValue += calculateNumberOfReports(fullSubEmployee);
                // Add fullSubEmployee after recursing so that its subs will be updated
                upToDateSubs.add(fullSubEmployee);
            }
            employee.setDirectReports(upToDateSubs);
        }
        return returnValue;
    }
}
