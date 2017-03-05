IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE table_name = 'stored_event') DROP TABLE stored_event;

CREATE TABLE stored_event (
    stored_event_id bigint IDENTITY(1, 1) NOT NULL,
    event_type varchar(255) NOT NULL,
    event_body varchar(max) NOT NULL,
    occurred_on datetime NOT NULL,
    version int NOT NULL,
    PRIMARY KEY (stored_event_id)
);

IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE table_name = 'message_tracker') DROP TABLE message_tracker;

CREATE TABLE message_tracker (
    id bigint IDENTITY(1, 1) NOT NULL,
    last_published_message_id bigint NOT NULL,
    publication_channel varchar(255) NOT NULL,
    PRIMARY KEY (id, publication_channel)
);
