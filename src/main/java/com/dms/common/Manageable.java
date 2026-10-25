package com.dms.common;

import java.util.List;
import java.util.Optional;

/**
 * Demonstrates OOP Abstraction.
 * Generic interface defining common lifecycle operations across disaster management entities.
 *
 * @param <T>  Entity or DTO type
 * @param <ID> Identifier type
 */
public interface Manageable<T, ID> {

    T create(T entity);

    T update(ID id, T entity);

    Optional<T> getById(ID id);

    List<T> getAll();

    void delete(ID id);
}
