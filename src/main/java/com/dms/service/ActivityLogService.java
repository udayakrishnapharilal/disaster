package com.dms.service;

import com.dms.model.ActivityLog;
import java.util.List;

public interface ActivityLogService {
    ActivityLog logActivity(String actorUsername, String actorRole, String action, String entityType, Long entityId, String details);
    List<ActivityLog> getRecentActivities(int limit);
}
