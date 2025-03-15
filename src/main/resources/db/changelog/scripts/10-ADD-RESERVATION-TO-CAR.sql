ALTER TABLE cars.car
    ADD COLUMN reserved_until TIMESTAMP,
    ADD COLUMN reserved_by BIGINT;

ALTER TABLE cars.car
    ADD CONSTRAINT fk_car_reserved_by
    FOREIGN KEY (reserved_by)
    REFERENCES cars.app_user(id);