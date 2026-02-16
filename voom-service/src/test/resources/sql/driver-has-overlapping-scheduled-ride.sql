INSERT INTO ride_route (
    ride_route_id,
    total_distance_km
)
VALUES (
    100,
    100.0
);

INSERT INTO route_point (
    route_point_id,
    ride_route_id,
    order_index,
    latitude,
    longitude,
    address,
    point_type
)
VALUES
(
    100,
    100,
    0,
    45.0,
    19.0,
    'Start',
    'PICKUP'
),
(
    101,
    100,
    1,
    45.1,
    19.1,
    'End',
    'DROPOFF'
);

INSERT INTO ride_request (
    ride_request_id,
    creator_id,
    ride_route_id,
    status,
    schedule_type,
    scheduled_time,
    vehicle_type_id,
    baby_transport,
    pet_transport,
    calculated_price
)
VALUES (
    100,
    1,
    100,
    'ACCEPTED',
    'LATER',
    TIMESTAMP '2026-01-01 10:00:00',
    1,
    FALSE,
    FALSE,
    1000
);

INSERT INTO ride (
    ride_id,
    ride_request_id,
    driver_id,
    status,
    started_at,
    finished_at,
    reminder_sent   
)
VALUES (
    100,
    100,
    1,
    0,
    TIMESTAMP '2026-01-01 10:00:00',
    TIMESTAMP '2026-01-01 12:00:00',
    FALSE
);

