package com.dms.service;

import java.io.ByteArrayInputStream;

public interface ReportGeneratorService {
    ByteArrayInputStream exportDisastersCsv();
    ByteArrayInputStream exportEmergencyRequestsCsv();
    ByteArrayInputStream exportSheltersCsv();
    ByteArrayInputStream exportResourcesCsv();
}
