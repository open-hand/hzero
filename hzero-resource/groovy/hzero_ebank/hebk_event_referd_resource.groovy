package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_event_referd_resource.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_event_referd_resource") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_event_referd_resource_s', startValue: "10001")
        }
        createTable(tableName: "hebk_event_referd_resource", remarks: "事件资源") {
            column(name: "resource_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_event_referd_resource_pk")
            }
            column(name: "event_id", type: "bigint", remarks: "事件ID，hebk_event.event_id") {
                constraints(nullable: "false")
            }
            column(name: "resource", type: "varchar(" + 120 * weight + ")", remarks: "资源") {
                constraints(nullable: "false")
            }
        }
        createIndex(tableName: "hebk_event_referd_resource", indexName: "hebk_event_referd_resource_n1") {
            column(name: "event_id")
        }
        createIndex(tableName: "hebk_event_referd_resource", indexName: "hebk_event_referd_resource_n2") {
            column(name: "resource")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-12_hebk_event_referd_resource") {
        addColumn(tableName: 'hebk_event_referd_resource') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-19_hebk_event_referd_resource") {
        dropIndex(tableName: 'hebk_event_referd_resource', indexName: 'hebk_event_referd_resource_n2')
        if(helper.isOracle()) {
            sqlFile(path: './resource_change_oracle.sql', relativeToChangelogFile: true, stripComments: true)
        } else if (helper.isSqlServer()){
            sqlFile(path: './resource_change_mssql.sql', relativeToChangelogFile: true, stripComments: true)
        } else if (helper.isPostgresql()){
            sqlFile(path: './resource_change_postgres.sql', relativeToChangelogFile: true, stripComments: true)
        } else {
            sqlFile(path: './resource_change_mysql.sql', relativeToChangelogFile: true, stripComments: true)
        }
        createIndex(tableName: "hebk_event_referd_resource", indexName: "hebk_event_referd_resource_n2") {
            column(name: "event_resource")
        }
    }
}
