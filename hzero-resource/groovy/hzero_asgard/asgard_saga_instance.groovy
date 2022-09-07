package script.db

databaseChangeLog(logicalFilePath: 'asgard_saga_instance.groovy') {
    changeSet(id: '2018-07-04-create-table-asgard_saga_instance', author: 'jcalaz@163.com') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_SAGA_INSTANCE_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_SAGA_INSTANCE") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_SAGA_INSTANCE')
            }
            column(name: 'SAGA_CODE', type: 'VARCHAR(64)', remarks: 'saga标识') {
                constraints(nullable: false)
            }
            column(name: 'STATUS', type: 'VARCHAR(32)', defaultValue: "RUNNING", remarks: 'saga执行状态。RUNNING,ROLLBACK,FAILED,NON_CONSUMER,COMPLETED') {
                constraints(nullable: false)
            }
            column(name: 'REF_TYPE', type: 'VARCHAR(128)', remarks: '关联类型')
            column(name: 'REF_ID', type: 'TEXT', remarks: '关联id')
            column(name: 'INPUT_DATA_ID', type: 'BIGINT', remarks: '输入参数的json data id')
            column(name: 'OUTPUT_DATA_ID', type: 'BIGINT', remarks: '输出参数的json data id')
            column(name: "START_TIME", type: "DATETIME(3)", remarks: 'saga开始执行的时间')
            column(name: "END_TIME", type: "DATETIME(3)", remarks: 'saga执行结束的时间')


            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-12-05-add-column-level', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'ASGARD_SAGA_INSTANCE') {
            column(name: "FD_LEVEL", type: "VARCHAR(32)", defaultValue: "site", remarks: '层级', afterColumn: 'REF_ID')
        }
        addColumn(tableName: 'ASGARD_SAGA_INSTANCE') {
            column(name: 'SOURCE_ID', type: 'BIGINT', defaultValue: "0", remarks: '创建该实例的源id，projectId/organizarionId', afterColumn: 'FD_LEVEL')
        }
    }

    changeSet(id: '2018-12-19-add-column', author: 'jcalaz@163.com') {
        addColumn(tableName: 'ASGARD_SAGA_INSTANCE') {
            column(name: 'uuid', type: 'CHAR(32)', remarks: 'uuid')
        }
		addColumn(tableName: 'ASGARD_SAGA_INSTANCE') {
            column(name: 'user_details', type: 'TEXT', remarks: '创建该saga的userDetails信息')
        }
        addUniqueConstraint(tableName: 'ASGARD_SAGA_INSTANCE', columnNames: 'uuid', constraintName: 'UK_ASGARD_SAGA_INSTANCE_UUID')
    }

    changeSet(id: '2018-01-04-add-column-createdOn', author: 'jcalaz@163.com') {
        addColumn(tableName: 'ASGARD_SAGA_INSTANCE') {
            column(name: 'created_on', type: 'VARCHAR(48)', remarks: '创建该saga的服务，用于回查')
        }
    }
}