CREATE TABLE humansignals (
  heartbeat INTEGER NOT NULL ,
  oxygen_percentage REAL NOT NULL,
  stress_percentage REAL NOT NULL,
  decibels_voice INTEGER NOT NULL,
  external_noise TEXT NOT NULL,
  panic_button BOOLEAN NOT NULL,
  latitude REAL NOT NULL,
  longitude REAL NOT NULL,
  signal_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE alerts (
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);