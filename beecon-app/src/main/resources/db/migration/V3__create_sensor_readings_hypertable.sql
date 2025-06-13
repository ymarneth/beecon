CREATE TABLE sensor_readings (
                                 id UUID NOT NULL,
                                 hive_id UUID NOT NULL,
                                 created_at TIMESTAMPTZ NOT NULL,
                                 temperature DOUBLE PRECISION,
                                 humidity DOUBLE PRECISION,
                                 co2Emission DOUBLE PRECISION,
                                 precipitation DOUBLE PRECISION,
                                 PRIMARY KEY (id, created_at)
);

SELECT create_hypertable('sensor_readings', 'created_at', if_not_exists => TRUE);
