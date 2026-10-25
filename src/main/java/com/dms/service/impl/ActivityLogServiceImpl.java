package com.dms.service.impl;

import com.dms.model.ActivityLog;
import com.dms.repository.ActivityLogRepository;
import com.dms.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    @Override
    public ActivityLog logActivity(String actorUsername, String actorRole, String action,
                                   String entityType, Long entityId, String details) {
        ActivityLog log = new ActivityLog(
                actorUsername != null ? actorUsername : "System",
                actorRole != null ? actorRole : "SYSTEM",
                action,
                entityType,
                entityId,
                details
        );
        return activityLogRepository.save(log);
    }

    @Override
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogRepository.findRecentLogs(PageRequest.of(0, Math.max(1, limit)));
    }
}
