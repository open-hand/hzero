package script.db

databaseChangeLog(logicalFilePath: 'asgard_saga_task_instance.groovy') {
    changeSet(id: '2018-07-04-create-table-asgard_saga_task_instance', author: 'jcalaz@163.com') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_SAGA_TASK_INSTANCE_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_SAGA_TASK_INSTANCE") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_SAGA_TASK_INSTANCE')
            }
            column(name: 'SAGA_INSTANCE_ID', type: 'BIGINT', remarks: '关联的instance id') {
                constraints(nullable: false)
            }
            column(name: 'SAGA_CODE', type: 'VARCHAR(128)', remarks: 'saga标识') {
                constraints(nullable: false)
            }
            column(name: 'TASK_CODE', type: 'VARCHAR(64)', remarks: '任务标识') {
                constraints(nullable: false)
            }
            column(name: 'TIMEOUT_POLICY', type: 'VARCHAR(64)', remarks: '超时策略') {
                constraints(nullable: false)
            }
            column(name: 'TIMEOUT_SECONDS', type: 'INT', defaultValue: "300", remarks: '超时时间(s)') {
                constraints(nullable: false)
            }
            column(name: 'REF_TYPE', type: 'VARCHAR(128)', remarks: '关联类型')
            column(name: 'REF_ID', type: 'TEXT', remarks: '关联id')
            column(name: 'INSTANCE_LOCK', type: 'VARCHAR(64)', remarks: '消费该消息的实例锁')

            column(name: 'RETRIED_COUNT', type: 'INT', defaultValue: "0", remarks: '重试次数') {
                constraints(nullable: false)
            }

            column(name: 'CONCURRENT_LIMIT_NUM', type: 'INT', remarks: '最大并发数，当并发策略不为NONE时生效', defaultValue: "1") {
                constraints(nullable: false)
            }

            column(name: 'CONCURRENT_LIMIT_POLICY', type: 'VARCHAR(32)', remarks: '并发策略。NONE,TYPE,TYPE_AND_ID', defaultValue: 'NONE'){
                constraints(nullable: false)
            }

            column(name: 'EXCEPTION_MESSAGE', type: 'TEXT', remarks: '消息消费失败的异常信息')

            column(name: 'STATUS', type: 'VARCHAR(32)', defaultValue: "RUNNING", remarks: 'saga执行状态。QUEUE,RUNNING,ROLLBACK,FAILED,COMPLETED') {
                constraints(nullable: false)
            }
            column(name: 'INPUT_DATA_ID', type: 'BIGINT', remarks: '输入参数的json data id')
            column(name: 'OUTPUT_DATA_ID', type: 'BIGINT', remarks: '输出参数的json data id')
            column(name: 'SEQ', type: 'INT', remarks: 'saga中任务次序') {
                constraints(nullable: false)
            }
            column(name: 'MAX_RETRY_COUNT', type: 'INT', defaultValue: "0", remarks: '最大重试次数') {
                constraints(nullable: false)
            }
            column(name: "PLANNED_START_TIME", type: "DATETIME(3)", remarks: '计划开始执行时间')
            column(name: "ACTUAL_START_TIME", type: "DATETIME(3)", remarks: '实际开始执行时间')
            column(name: "ACTUAL_END_TIME", type: "DATETIME(3)", remarks: '实际执行结束时间')
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-12-19-drop-and-add-column', author: 'jcalaz@163.com') {
        if (helper.isSqlServer()) {
            dropDefaultValue(tableName: 'ASGARD_SAGA_TASK_INSTANCE', columnName: 'TIMEOUT_SECONDS')
        }
        dropColumn(tableName: 'ASGARD_SAGA_TASK_INSTANCE', columnName: 'TIMEOUT_POLICY')
        dropColumn(tableName: 'ASGARD_SAGA_TASK_INSTANCE', columnName: 'TIMEOUT_SECONDS')
        dropColumn(tableName: 'ASGARD_SAGA_TASK_INSTANCE', columnName: 'REF_TYPE')
        dropColumn(tableName: 'ASGARD_SAGA_TASK_INSTANCE', columnName: 'REF_ID')
        addColumn(tableName: 'ASGARD_SAGA_TASK_INSTANCE') {
            column(name: 'SERVICE', type: 'VARCHAR(64)', remarks: '该消息所属的服务') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(id: '2018-01-09-create-index-INDEX_ASGARD_INSTANCE_ID_SEQ', author: 'flyleft') {
        createIndex(tableName: 'ASGARD_SAGA_TASK_INSTANCE', indexName: 'INDEX_ASGARD_INSTANCE_ID_SEQ', unique: false) {
            column(name: 'SAGA_INSTANCE_ID')
            column(name: 'SEQ')
        }
    }

}