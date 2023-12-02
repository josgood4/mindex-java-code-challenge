package com.mindex.challenge.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureService.class);

    // TODO docs
    // TODO interface?
    public ReportingStructure read(String id) {
        LOG.debug("Getting ReportingStructure for employee with id [{}]", id);

        ReportingStructure reportingStructure = new ReportingStructure(id);

        return reportingStructure;
    }

}
