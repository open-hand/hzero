package script.db

databaseChangeLog(logicalFilePath: 'asgard_quartz_method.groovy') {
    changeSet(id: '2018-09-05-create-table-asgard_quartz_method', author: 'flyleft') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'ASGARD_QUARTZ_METHOD_S', startValue:"1")
        }
        createTable(tableName: "ASGARD_QUARTZ_METHOD") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_QUARTZ_METHOD')
            }
            column(name: 'METHOD', type: 'VARCHAR(128)', remarks: '方法名') {
                constraints(nullable: false)
            }
            column(name: 'SERVICE', type: 'VARCHAR(64)', remarks: '方法所在服务') {
                constraints(nullable: false)
            }
            column(name: 'MAX_RETRY_COUNT', type: 'INT', defaultValue: "0", remarks: '最大重试次数') {
                constraints(nullable: false)
            }
            column(name: 'PARAMS', type: 'TEXT', remarks: '方法参数列表的json形式') {
                constraints(nullable: false)
            }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'ASGARD_QUARTZ_METHOD', columnNames: 'METHOD', constraintName: 'UK_ASGARD_QUARTZ_METHOD_METHOD')
    }

    changeSet(id: '2018-09-11-add-code-and-description', author: 'longhe1996@icloud.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_METHOD') {
            column(name: "CODE", type: "VARCHAR(64)", remarks: '方法标志') {
                constraints(nullable: false)
            }
        }
        addColumn(tableName: 'ASGARD_QUARTZ_METHOD') {
            column(name: "DESCRIPTION", type: "VARCHAR(255)", remarks: '方法描述')
        }
    }

    changeSet(id: '2018-10-30-add-column-level', author: 'youquandeng1@gmail.com') {
        addColumn(tableName: 'ASGARD_QUARTZ_METHOD') {
            column(name: "FD_LEVEL", type: "VARCHAR(32)", defaultValue: "site", remarks: '层级', afterColumn: 'PARAMS')
        }
    }
}