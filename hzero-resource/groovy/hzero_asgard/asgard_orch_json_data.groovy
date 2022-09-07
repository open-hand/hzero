package script.db

databaseChangeLog(logicalFilePath: 'asgard_orch_json_data.groovy') {
    changeSet(id: '2018-07-04-create-table-asgard_orch_json_data', author: 'jcalaz@163.com') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_ORCH_JSON_DATA_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_ORCH_JSON_DATA") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_ORCH_JSON_DATA')
            }
            column(name: 'DATA', type: 'MEDIUMTEXT', remarks: '存储数据') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(id: '2018-12-21-add-column-sha2', author: 'flyleft') {
        addColumn(tableName: 'ASGARD_ORCH_JSON_DATA') {
            column(name: 'SHA2', type: 'CHAR(64)', remarks: '存储数据的sha256值') {
                constraints(nullable: true)
            }
        }
        addUniqueConstraint(tableName: 'ASGARD_ORCH_JSON_DATA', columnNames: 'SHA2', constraintName: 'UK_ASGARD_ORCH_JSON_DATA_SHA')
    }
}