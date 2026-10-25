package com.dms.common;

import java.time.LocalDateTime;

/**
 * Demonstrates OOP Abstraction.
 * Interface to track audit trail info across core domain entities.
 */
public interface Auditable {

    LocalDateTime getCreatedAt();

    void setCreatedAt(LocalDateTime createdAt);

    LocalDateTime getUpdatedAt();

    void setUpdatedAt(LocalDateTime updatedAt);
}
