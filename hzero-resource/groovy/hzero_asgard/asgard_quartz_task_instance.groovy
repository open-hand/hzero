package script.db

databaseChangeLog(logicalFilePath: 'asgard_quartz_task_instance.groovy') {
    changeSet(id: '2018-09-05-create-table-asgard_quartz_task_instance', author: 'flyleft') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_QUARTZ_TASK_INSTANCE_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_QUARTZ_TASK_INSTANCE") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_QUARTZ_TASK_INSTANCE')
            }
            column(name: 'TASK_ID', type: 'BIGINT', remarks: '任务ID') {
                constraints(nullable: false)
            }
            column(name: "PLANNED_START_TIME", type: "DATETIME(3)", remarks: '计划开始执行时间') {
                constraints(nullable: false)
            }
            column(name: "ACTUAL_START_TIME", type: "DATETIME(3)", remarks: '实际开始执行时间')

            column(name: "ACTUAL_LAST_TIME", type: "DATETIME(3)", remarks: '上次执行时间')

            column(name: "PLANNED_NEXT_TIME", type: "DATETIME(3)", remarks: '实际执行结束时间')

            column(name: 'EXCEPTION_MESSAGE', type: 'TEXT', remarks: '消息消费失败的异常信息')

            column(name: 'RETRIED_COUNT', type: 'INT', defaultValue: "0", remarks: '重试次数') {
                constraints(nullable: false)
            }
            column(name: 'EXECUTE_PARAMS', type: 'TEXT', remarks: '任务执行参数') {
                constraints(nullable: false)
            }
            column(name: 'EXECUTE_METHOD', type: 'VARCHAR(128)', remarks: '任务执行方法') {
                constraints(nullable: false)
            }
            column(name: 'EXECUTE_RESULT', type: 'TEXT', remarks: '任务执行结果')

            column(name: 'INSTANCE_LOCK', type: 'VARCHAR(64)', remarks: '消费该消息的实例锁')

            column(name: 'STATUS', type: 'VARCHAR(32)', defaultValue: "RUNNING", remarks: '任务执行状态。RUNNING,FAILED,COMPLETED') {
                constraints(nullable: false)
            }
            column(name: 'max_retry_count', type: 'INT', remarks: '最大重试次数') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-09-13-add-task_name', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK_INSTANCE') {
            column(name: "TASK_NAME", type: "VARCHAR(64)", remarks: '任务名') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(id: '2018-10-30-add-column-level', author: 'youquandeng1@gmail.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_TASK_INSTANCE') {
            column(name: "FD_LEVEL", type: "VARCHAR(32)", defaultValue: "site", remarks: '层级', afterColumn: 'STATUS')
        }
        addColumn(tableName: 'ASGARD_QUARTZ_TASK_INSTANCE') {
            column(name: 'SOURCE_ID', type: 'BIGINT', defaultValue: "0", remarks: '创建该记录的源id，可以是projectId,也可以是organizarionId等', afterColumn: 'FD_LEVEL')
        }
    }

    changeSet(id: '2018-11-30-resize-column-task_name', author: 'longhe1996@icloud.com') {
        dropColumn(tableName: 'ASGARD_QUARTZ_TASK_INSTANCE', columnName:'TASK_NAME')
        addColumn(tableName: 'ASGARD_QUARTZ_TASK_INSTANCE') {
            column(name: 'TASK_NAME', type: 'VARCHAR(255)', remarks: '任务名')
        }
    }
}