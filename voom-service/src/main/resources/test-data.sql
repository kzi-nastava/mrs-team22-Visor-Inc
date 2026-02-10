-- PERMISSIONS
INSERT INTO permission (permission_id, permission_name) VALUES
(1, 'USER'),
(2, 'DRIVER'),
(3, 'ADMIN');

-- ROLES
INSERT INTO user_role (user_role_id, role_name) VALUES
(1, 'USER'),
(2, 'DRIVER'),
(3, 'ADMIN');

-- VEHICLE TYPES
INSERT INTO vehicle_type (vehicle_type_id, vehicle_type_name, vehicle_type_price) VALUES
(1, 'STANDARD', 150),
(2, 'LUXURY', 250),
(3, 'VAN', 200);

-- PERSON
INSERT INTO person (person_id, first_name, last_name, phone_number, address, birth_date)
VALUES
(1, 'Test', 'User', '0651111111', 'Test Address', '1980-01-01T00:00:00');

-- USER (password = Test123)
INSERT INTO users (user_id, person_id, email, password, user_status, user_role_id)
VALUES
(
    1,
    1,
    'user@test.com',
    '$2a$10$FBJwVWk7p8duui4F1u2ppuC37h6A8fEX.LYNv0DYD95Y8iLxERbh2',
    1,
    1
);

INSERT INTO person (person_id, first_name, last_name, phone_number, address, birth_date)
VALUES
(2, 'Driver', 'Test', '0652222222', 'Driver Address', '1985-01-01T00:00:00');

INSERT INTO users (user_id, person_id, email, password, user_status, user_role_id)
VALUES
(
    2,
    2,
    'driver@test.com',
    '$2a$10$FBJwVWk7p8duui4F1u2ppuC37h6A8fEX.LYNv0DYD95Y8iLxERbh2',
    1,
    2 
);

INSERT INTO drivers (driver_id, user_id, driver_status)
VALUES
(1, 2, 0);

INSERT INTO vehicles (
    vehicle_id,
    driver_id,
    vehicle_type_id,
    year1,
    model,
    license_plate,
    baby_seat,
    pet_friendly,
    number_of_seats
)
VALUES
(
    1,
    1,
    1,
    2020,
    'Toyota',
    'NS-123-AA',
    false,
    true,
    4
);


INSERT INTO driver_state_changes (state_change_id, driver_id, state, performed_at)
VALUES
(1, 1, 1, CURRENT_TIMESTAMP());
