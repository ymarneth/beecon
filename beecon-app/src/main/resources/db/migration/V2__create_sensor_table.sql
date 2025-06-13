CREATE TABLE hives (
                         id UUID PRIMARY KEY,
                         user_id TEXT NOT NULL,
                         name TEXT NOT NULL,
                         created_at TIMESTAMPTZ NOT NULL
);
