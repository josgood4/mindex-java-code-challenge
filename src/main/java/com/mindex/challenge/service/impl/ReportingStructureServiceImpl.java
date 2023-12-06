package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Getting ReportingStructure for employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);
        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        ReportingStructure reportingStructure = new ReportingStructure(employee, getNumberOfReports(id));

        return reportingStructure;
    }

    private int getNumberOfReports(String employeeId) {
        // Look up employee by Id since that may be all that's saved in directReport list.
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        List<Employee> directReports = employee.getDirectReports();
        int returnValue = 0;
        if (directReports != null && !directReports.isEmpty()) {
            // Assuming no circular directReports, or else this would infinitely recurse.
            for (Employee e : directReports) {
                // The returnValue will be equal to the sum of the following:
                //   -> each directReport immediately under the current employee
                //   -> getNumberOfReports value for all employees under the current employee
                returnValue++;
                returnValue += getNumberOfReports(e.getEmployeeId());
            }
        }
        return returnValue;
    }
}
