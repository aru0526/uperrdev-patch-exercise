package com.internal.tasktracker;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Search non-archived tasks by term (title OR description) and optional status.
    // The OR is wrapped in parentheses: AND binds tighter than OR in SQL, so without
    // them the archived/status filters only applied to one side of the OR.
    // Paging is done in the database (LIMIT/OFFSET) rather than in memory; `id DESC`
    // is a tie-breaker so page boundaries stay stable when created_at values are equal.
    @Query(value = "SELECT * FROM tasks WHERE archived = FALSE "
                 + "AND (LOWER(title) LIKE :term OR LOWER(description) LIKE :term) "
                 + "AND (:status IS NULL OR status = :status) "
                 + "ORDER BY created_at DESC, id DESC",
           countQuery = "SELECT COUNT(*) FROM tasks WHERE archived = FALSE "
                      + "AND (LOWER(title) LIKE :term OR LOWER(description) LIKE :term) "
                      + "AND (:status IS NULL OR status = :status)",
           nativeQuery = true)
    Page<Task> searchTasks(@Param("term") String term,
                           @Param("status") String status,
                           Pageable pageable);
}
