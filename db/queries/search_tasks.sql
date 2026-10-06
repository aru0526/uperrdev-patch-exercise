-- H2-compatible task search query
-- Mirrors TaskRepository.searchTasks (the Spring Data repository layer)
--
-- Parameters:
--   :term   — lower-cased search term wrapped in wildcards, e.g. '%api%'
--   :status — status filter or NULL for all statuses
--
-- NOTE: the OR is parenthesised on purpose. AND binds tighter than OR, so the
-- unparenthesised form applied `archived` and `status` to only one side of the OR.
-- Paging (LIMIT/OFFSET) is applied by Spring Data via Pageable.

SELECT *
FROM tasks
WHERE archived = FALSE
  AND (LOWER(title) LIKE :term OR LOWER(description) LIKE :term)
  AND (:status IS NULL OR status = :status)
ORDER BY created_at DESC, id DESC;
