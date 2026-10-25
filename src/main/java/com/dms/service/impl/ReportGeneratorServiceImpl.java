package com.dms.service.impl;

import com.dms.model.DisasterReport;
import com.dms.model.EmergencyRequest;
import com.dms.model.Resource;
import com.dms.model.Shelter;
import com.dms.repository.DisasterReportRepository;
import com.dms.repository.EmergencyRequestRepository;
import com.dms.repository.ResourceRepository;
import com.dms.repository.ShelterRepository;
import com.dms.service.ReportGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ReportGeneratorServiceImpl implements ReportGeneratorService {

    @Autowired
    private DisasterReportRepository disasterReportRepository;

    @Autowired
    private EmergencyRequestRepository emergencyRequestRepository;

    @Autowired
    private ShelterRepository shelterRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Override
    public ByteArrayInputStream exportDisastersCsv() {
        List<DisasterReport> reports = disasterReportRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("ID,Title,Type,Severity,Status,Location,City,Latitude,Longitude,Casualties,Affected,ReportedAt");
            for (DisasterReport r : reports) {
                writer.printf("%d,\"%s\",%s,%s,%s,\"%s\",\"%s\",%f,%f,%d,%d,%s%n",
                        r.getId(),
                        escapeSpecialChars(r.getTitle()),
                        r.getDisasterType(),
                        r.getSeverity(),
                        r.getStatus(),
                        escapeSpecialChars(r.getLocationName()),
                        escapeSpecialChars(r.getCity() != null ? r.getCity() : ""),
                        r.getLatitude(),
                        r.getLongitude(),
                        r.getCasualtiesCount() != null ? r.getCasualtiesCount() : 0,
                        r.getAffectedCount() != null ? r.getAffectedCount() : 0,
                        r.getReportedAt()
                );
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportEmergencyRequestsCsv() {
        List<EmergencyRequest> requests = emergencyRequestRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("ID,Type,Priority,Status,PeopleCount,Location,City,ContactPhone,RequestedAt");
            for (EmergencyRequest r : requests) {
                writer.printf("%d,%s,%s,%s,%d,\"%s\",\"%s\",\"%s\",%s%n",
                        r.getId(),
                        r.getRequestType(),
                        r.getPriority(),
                        r.getStatus(),
                        r.getNumberOfPeople(),
                        escapeSpecialChars(r.getLocationName()),
                        escapeSpecialChars(r.getCity() != null ? r.getCity() : ""),
                        escapeSpecialChars(r.getContactPhone() != null ? r.getContactPhone() : ""),
                        r.getRequestedAt()
                );
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportSheltersCsv() {
        List<Shelter> shelters = shelterRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("ID,Name,Address,City,TotalCapacity,Occupancy,AvailableBeds,Status,ContactPerson,ContactPhone");
            for (Shelter s : shelters) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",%d,%d,%d,%s,\"%s\",\"%s\"%n",
                        s.getId(),
                        escapeSpecialChars(s.getName()),
                        escapeSpecialChars(s.getAddress()),
                        escapeSpecialChars(s.getCity() != null ? s.getCity() : ""),
                        s.getTotalCapacity(),
                        s.getCurrentOccupancy(),
                        s.getRemainingCapacity(),
                        s.getStatus(),
                        escapeSpecialChars(s.getContactPerson() != null ? s.getContactPerson() : ""),
                        escapeSpecialChars(s.getContactPhone() != null ? s.getContactPhone() : "")
                );
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    public ByteArrayInputStream exportResourcesCsv() {
        List<Resource> resources = resourceRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter writer = new PrintWriter(out, true, StandardCharsets.UTF_8)) {
            writer.println("ID,Name,Type,Quantity,Unit,StorageLocation,WarehouseCode,MinThreshold,Status");
            for (Resource r : resources) {
                writer.printf("%d,\"%s\",%s,%d,%s,\"%s\",\"%s\",%d,%s%n",
                        r.getId(),
                        escapeSpecialChars(r.getName()),
                        r.getType(),
                        r.getQuantity(),
                        r.getUnit(),
                        escapeSpecialChars(r.getStorageLocation()),
                        escapeSpecialChars(r.getWarehouseCode() != null ? r.getWarehouseCode() : ""),
                        r.getMinThreshold(),
                        r.getStatus()
                );
            }
        }
        return new ByteArrayInputStream(out.toByteArray());
    }

    private String escapeSpecialChars(String text) {
        if (text == null) return "";
        return text.replace("\"", "\"\"");
    }
}
