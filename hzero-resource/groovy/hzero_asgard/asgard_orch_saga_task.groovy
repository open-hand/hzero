package script.db

databaseChangeLog(logicalFilePath: 'asgard_orch_saga_task.groovy') {
    changeSet(id: '2018-07-04-create-table-asgard_orch_saga_task', author: 'jcalaz@163.com') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_ORCH_SAGA_TASK_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_ORCH_SAGA_TASK") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_ORCH_SAGA_TASK')
            }
            column(name: 'CODE', type: 'VARCHAR(64)', remarks: '任务标识')
            column(name: 'TIMEOUT_POLICY', type: 'VARCHAR(64)', remarks: '超时策略')
            column(name: 'TIMEOUT_SECONDS', type: 'INT', remarks: '超时时间(s)')
            column(name: 'CONCURRENT_LIMIT_NUM', type: 'INT', remarks: '最大并发数', defaultValue: "1") {
                constraints(nullable: false)
            }
            column(name: 'CONCURRENT_LIMIT_POLICY', type: 'VARCHAR(32)', remarks: '并发策略。NONE,TYPE,TYPE_AND_ID', defaultValue: 'NONE'){
                constraints(nullable: false)
            }
            column(name: 'SAGA_CODE', type: 'VARCHAR(64)', remarks: 'saga标识') {
                constraints(nullable: false)
            }
            column(name: 'OUTPUT_SCHEMA', type: 'text', remarks: 'task输出的json schema')
            column(name: 'IS_ENABLED', type: 'TINYINT', defaultValue: "1", remarks: '是否启用') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE', type: 'VARCHAR(64)', remarks: '创建该task的服务') {
                constraints(nullable: false)
            }
            column(name: 'SEQ', type: 'INT', remarks: 'saga中任务次序') {
                constraints(nullable: false)
            }
            column(name: 'DESCRIPTION', type: 'VARCHAR(255)', remarks: '描述')

            column(name: 'MAX_RETRY_COUNT', type: 'INT', defaultValue: "0", remarks: '最大重试次数') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'ASGARD_ORCH_SAGA_TASK', columnNames: 'SAGA_CODE,CODE', constraintName: "saga_task_code_unique")
    }

    changeSet(id: '2018-08-03-add-outputSchemaSource', author: 'jcalaz@163.com') {
        addColumn(tableName: 'ASGARD_ORCH_SAGA_TASK') {
            column(name: "OUTPUT_SCHEMA_SOURCE", type: "VARCHAR(32)", defaultValue: "NONE", remarks: 'outputSchema定义来源，取值: OUTPUT_SCHEMA,OUTPUT_SCHEMA_CLASS,METHOD_RETURN_TYPE,NONE') {
                constraints(nullable: false)
            }
        }
    }

    changeSet(id: '2018-09-04-rename-unique-constraints', author: 'superleader8@gmail.com') {
        dropUniqueConstraint(constraintName: 'saga_task_code_unique', tableName: 'ASGARD_ORCH_SAGA_TASK')
        addUniqueConstraint(tableName: 'ASGARD_ORCH_SAGA_TASK', columnNames: 'SAGA_CODE,CODE', constraintName: "UK_ASGARD_ORCH_SAGA_TASK_U1")
    }

    changeSet(id: '2019-01-02-drop-column', author: 'jcalaz@163.com') {
        dropColumn(tableName: 'ASGARD_ORCH_SAGA_TASK', columnName: 'TIMEOUT_POLICY')
        dropColumn(tableName: 'ASGARD_ORCH_SAGA_TASK', columnName: 'TIMEOUT_SECONDS')
    }

    changeSet(id: '2019-01-21create-index', author: 'longhe1996@icloud.com') {
        createIndex(tableName: 'ASGARD_ORCH_SAGA_TASK', indexName: 'NK_ASGARD_ORCH_SAGA_TASK_N1', unique: false) {
            column(name: 'CODE')
        }
    }

}