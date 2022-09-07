package script.db

databaseChangeLog(logicalFilePath: 'asgard_quartz_task_member.groovy') {
    changeSet(id: '2018-11-26-create-table-asgard_quartz_task_member', author: 'youquandeng1@gmail.com') {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'ASGARD_QUARTZ_TASK_MEMBER_S', startValue: "1")
        }
        createTable(tableName: "ASGARD_QUARTZ_TASK_MEMBER") {
            column(name: 'ID', type: 'BIGINT', remarks: 'ID', autoIncrement: true) {
                constraints(primaryKey: true, primaryKeyName: 'PK_ASGARD_QUARTZ_TASK_MEMBER')
            }
            column(name: 'TASK_ID', type: 'BIGINT', remarks: '任务ID') {
                constraints(nullable: false)
            }
            column(name: 'MEMBER_TYPE', type: 'VARCHAR(32)', defaultValue: "user", remarks: '成员类型(user/role)，默认为user') {
                constraints(nullable: false)
            }
            column(name: 'MEMBER_ID', type: 'BIGINT', remarks: '成员id，成员类型为user，则为userId') {
                constraints(nullable: false)
            }
            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1")
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(tableName: 'ASGARD_QUARTZ_TASK_MEMBER', columnNames: 'TASK_ID, MEMBER_TYPE, MEMBER_ID', constraintName: 'UK_QUARTZ_TASK_MEMBER_U1')
    }
}