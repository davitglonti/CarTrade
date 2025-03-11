CREATE TABLE cars.user_car (
    user_id BIGINT NOT NULL,
    car_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, car_id),
    FOREIGN KEY (user_id) REFERENCES cars.app_user(ID),
    FOREIGN KEY (car_id) REFERENCES cars.car(ID)
);