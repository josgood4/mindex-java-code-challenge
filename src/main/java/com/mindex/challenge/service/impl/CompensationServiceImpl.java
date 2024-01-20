package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        setEmployeeDataFromRepository(compensation);
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Getting Compensation with employeeId [{}]. ", employeeId);

        Compensation compensation = compensationRepository.findCompensationByEmployeeEmployeeId(employeeId);

        if (compensation == null) {
            throw new RuntimeException("No compensations found for employee id: " + employeeId);
        }
        // If there are updates to employee data, they will not be persisted in
        // the compensation object here - only reported back from the endpoint
        setEmployeeDataFromRepository(compensation);

        return compensation;
    }

    private void setEmployeeDataFromRepository(Compensation compensation) {
        if (compensation.getEmployee() == null || compensation.getEmployee().getEmployeeId() == null) {
            throw new RuntimeException("Employee or employee id not provided for Compensation.");
        }

        // If there are differences between employee data in the repository
        // and the employee data already present in the compensation,
        // the repository data is assumed to be the most up-to-date.
        String employeeId = compensation.getEmployee().getEmployeeId();
        Employee employee = employeeRepository.findByEmployeeId(employeeId);
        if (employee == null) {
            throw new RuntimeException("Employee not found in the repository: " + employeeId);
        }
        compensation.setEmployee(employee);
    }
}
