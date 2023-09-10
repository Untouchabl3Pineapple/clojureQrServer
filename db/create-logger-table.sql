DROP TABLE IF EXISTS logger;

CREATE TABLE IF NOT EXISTS logger
(
	log_id UUID     PRIMARY KEY,
	log_date_time   TIMESTAMP WITH TIME ZONE NOT NULL,
    log_data        TEXT NOT NULL
);