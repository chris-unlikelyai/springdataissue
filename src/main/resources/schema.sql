CREATE TABLE IF NOT EXISTS parent_entity
(
    id                 UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS child_entity
(
    id                 UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY,
    parent_entity      UUID NOT NULL REFERENCES parent_entity (id) ON DELETE CASCADE,
    parent_entity_key  INT  NOT NULL
);
