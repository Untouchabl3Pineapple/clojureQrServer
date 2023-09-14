CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS logger;

CREATE TABLE IF NOT EXISTS logger
(
	log_id          UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
	log_date_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    	log_data        TEXT NOT NULL
);