package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_user_dashboard.groovy') {
    changeSet(author: 'fan@choerodon.io', id: '2018-07-23-iam-user-dashboard') {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'IAM_USER_DASHBOARD_S', startValue:"1")
        }
        createTable(tableName: "IAM_USER_DASHBOARD") {
            column(name: 'ID', type: 'BIGINT', autoIncrement: true, remarks: '表ID，主键，供其他表做外键，unsigned bigint、单表时自增、步长为 1') {
                constraints(primaryKey: true, primaryKeyName: 'PK_IAM_USER_DASHBOARD')
            }
            column(name: 'USER_ID', type: 'BIGINT', remarks: 'user id') {
                constraints(nullable: true)
            }
            column(name: 'DASHBOARD_ID', type: 'BIGINT', remarks: 'dashboard id') {
                constraints(nullable: true)
            }
            column(name: 'IS_VISIBLE', type: 'TINYINT', defaultValue: "1", remarks: '是否可见') {
                constraints(nullable: true)
            }
            column(name: 'SORT', type: 'VARCHAR(128)', remarks: '顺序') {
                constraints(nullable: true)
            }
            column(name: 'LEVEL', type: 'VARCHAR(64)', remarks: '层级：site / organization / project') {
                constraints(nullable: true)
            }
            column(name: 'SOURCE_ID', type: 'BIGINT', remarks: '对应项目/组织 id')

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1") {
                constraints(nullable: true)
            }
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "0") {
                constraints(nullable: true)
            }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: 'superleader8@gmail.com', id: '2018-08-28-rename') {
        renameColumn(columnDataType: 'VARCHAR(64)', newColumnName: "FD_LEVEL", oldColumnName: "LEVEL", remarks: '层级：site / organization / project', tableName: 'IAM_USER_DASHBOARD')
    }

    changeSet(author: 'hzero@hand-china.com', id: '2019-04-25-add-column-positionDTO') {
        addColumn(tableName: 'IAM_USER_DASHBOARD') {
            column(name: 'POSITION', type: "VARCHAR(128)", remarks: '仪表盘位置')
        }
    }

    changeSet(id: '2019-08-12-iam_user_dashboard', author: 'jiangzhou.bo@hand-china.com') {
        dropTable(tableName: 'iam_user_dashboard')
        if (helper.dbType().isSupportSequence()) {
            dropSequence(sequenceName: 'IAM_USER_DASHBOARD_S')
        }
    }
}