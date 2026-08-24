IF OBJECT_ID('undo_log') IS NULL
CREATE TABLE undo_log
(
    id            BIGINT         identity(1,1) NOT NULL,
    branch_id     BIGINT         NOT NULL,
    xid           VARCHAR(100)   NOT NULL,
    context       VARCHAR(128)   NOT NULL,
    rollback_info VARBINARY(MAX) NOT NULL,
    log_status    INT            NOT NULL,
    log_created   BIGINT         NOT NULL,
    log_modified  BIGINT         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
)