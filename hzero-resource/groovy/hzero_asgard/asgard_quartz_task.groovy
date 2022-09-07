package script.db

databaseChangeLog(logicalFilePath: 'asgard_quartz_task.groovy') {
    changeSet(id: '2018-09-05-create-table-asgard_quartz_task', author: 'flyleft') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'ASGARD_QUARTZ_TASK_S', startValue: "1")
        }
        createTable(tableName: "ASGARD_QUARTZ_TASK") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_QUARTZ_TASK')
            }
            column(name: 'NAME', type: 'VARCHAR(64)', remarks: '任务名') {
                constraints(nullable: false, unique: true, uniqueConstraintName: 'UK_ASGARD_QUARTZ_TASK_NAME')
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '描述')
            column(name: "START_TIME", type: "DATETIME", remarks: '开始时间')
            column(name: "END_TIME", type: "DATETIME", remarks: '结束时间')
            column(name: 'TRIGGER_TYPE', type: 'VARCHAR(16)', remarks: '触发器类型。simple_trigger和cron_trigger') {
                constraints(nullable: false)
            }
            column(name: "SIMPLE_REPEAT_COUNT", type: "BIGINT", remarks: 'simple-trigger重复次数')
            column(name: "SIMPLE_REPEAT_INTERVAL", type: "BIGINT", remarks: 'simple-trigger执行间隔')
            column(name: "CRON_EXPRESSION", type: "VARCHAR(120)", remarks: 'cron-trigger表达式')

            column(name: 'EXECUTE_PARAMS', type: 'TEXT', remarks: '任务执行参数') {
                constraints(nullable: false)
            }
            column(name: 'EXECUTE_METHOD', type: 'VARCHAR(128)', remarks: '任务执行方法') {
                constraints(nullable: false)
            }
            column(name: 'status', type: 'VARCHAR(16)', defaultValue: "ENABLE", remarks: '任务状态。ENABLE,DISABLE,FINISHED') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-09-12-add-unit', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: "SIMPLE_REPEAT_INTERVAL_UNIT", type: "VARCHAR(32)", remarks: 'simple-trigger执行时间间隔的单位。SECOND,MINUTES,HOUR,WEEK,MONTH')
        }
    }

    changeSet(id: '2018-10-30-add-column-level', author: 'youquandeng1@gmail.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: "FD_LEVEL", type: "VARCHAR(32)", defaultValue: "site", remarks: '层级', afterColumn: 'EXECUTE_METHOD')
        }
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: 'SOURCE_ID', type: 'BIGINT', defaultValue: "0", remarks: '创建该记录的源id，可以是projectId,也可以是organizarionId等', afterColumn: 'FD_LEVEL')
        }
    }

    changeSet(id: '2018-11-30-resize-column-name', author: 'longhe1996@icloud.com') {
        dropUniqueConstraint(tableName: 'ASGARD_QUARTZ_TASK', constraintName: "UK_ASGARD_QUARTZ_TASK_NAME")
        dropColumn(tableName: 'ASGARD_QUARTZ_TASK', columnName:'NAME')
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: 'NAME', type: 'VARCHAR(255)', remarks: '任务名')
        }
    }

    changeSet(id: '2018-12-05-add-column-strategy', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: "EXECUTE_STRATEGY", type: "VARCHAR(32)", defaultValue: "STOP",
                    remarks: '任务的执行策略，默认为STOP：到了第二次执行时间，上一次执行尚未完成，则置上一次执行为失败状态，并停止任务',
                    afterColumn: 'EXECUTE_METHOD')
        }
    }

    changeSet(id: '2018-01-03-add-column-user_details', author: 'flyleft') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK') {
            column(name: 'user_details', type: 'TEXT', remarks: '创建定时任务的userDetails信息')
        }
    }

}