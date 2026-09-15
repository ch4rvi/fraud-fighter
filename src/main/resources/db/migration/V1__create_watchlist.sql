CREATE TABLE watchlist_table (

    id                       VARCHAR(50)       PRIMARY KEY,
    account_risk_level      VARCHAR(50),
    created_at              TIMESTAMP,
    modified_at             TIMESTAMP,
    owner                   VARCHAR(50),
    modified_by             VARCHAR(50),
    active                  BOOLEAN

);
