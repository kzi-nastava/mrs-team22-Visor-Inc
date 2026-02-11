UPDATE driver_state_changes
SET performed_at = DATEADD('HOUR', -9, CURRENT_TIMESTAMP)
WHERE driver_id = 1;
