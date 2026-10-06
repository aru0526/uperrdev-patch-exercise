# NOTES

## Summary of changes
Highest value first (detailed explanations are in `handwritten/`):
- **SQL precedence bug** (`TaskRepository`, `db/queries`, Oracle package): `AND`/`OR` were unparenthesised, so the `archived` and `status` filters only applied to one side of the `OR`. Archived tasks leaked into results and the status filter did nothing without a search term.
- **Removed `Thread.sleep`** in the controller (up to ~1s per request for short or empty queries).
- **Frontend race condition**: older, slower responses could overwrite newer ones. Fixed with `AbortController`.
- **Error/loading state** never reset, so one failed request left the UI stuck. Fixed.
- **Paging**: moved to the database (`Pageable` + count query), clamped `page`/`pageSize`, added a stable `id` tie-breaker, invalid `status` now returns 400 instead of 500.
- **UX**: 300 ms search debounce, reset to page 1 when filters change, aria-labels on controls.

## What I chose not to change
- `%` / `_` wildcard escaping in search input (low impact, extra risk).
- Hardcoded CORS origin, H2 console and `show-sql` (fine for a local exercise).
- `status` stored as a `String` rather than an enum (works; larger refactor).

## Biggest remaining risk
There are no automated tests, so regressions like the precedence bug would go unnoticed. `LIKE '%term%'` on `LOWER(...)` also cannot use an index and will full-scan as data grows. The Oracle package duplicates the query logic (count + cursor), so the copies can drift.

## Tools / AI used
Used Claude to review the code, draft the fixes and reproduce the SQL bug against the seed data. I reviewed each change myself and ran the app locally.
Assumptions: archived tasks should never be listed; search matches title OR description; max page size of 100 is my choice.
