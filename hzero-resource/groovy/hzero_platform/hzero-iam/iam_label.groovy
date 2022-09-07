package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_label.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_label") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'iam_label_s', startValue: "1")
        }
        createTable(tableName: "iam_label", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "name", type: "varchar(" + 64 * weight + ")", remarks: "名称") { constraints(nullable: "false") }
            column(name: "type", type: "varchar(" + 32 * weight + ")", remarks: "类型") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "")
            column(name: "created_by", type: "bigint", defaultValue: "0", remarks: "")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "")
            column(name: "last_updated_by", type: "bigint", defaultValue: "0", remarks: "")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "")

        }

        addUniqueConstraint(columnNames: "name,type", tableName: "iam_label", constraintName: "iam_label_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2019-04-25-iam-label-add-column') {
        addColumn(tableName: 'IAM_LABEL') {
            column(name: 'FD_LEVEL', type: "varchar(" + 32 * weight + ")", remarks: '层级') {
                constraints(nullable: false)
            }
        }
        addColumn(tableName: 'IAM_LABEL') {
            column(name: 'DESCRIPTION', type: "varchar(" + 128 * weight + ")", remarks: '描述')
        }
    }
    changeSet(author: 'hzero@hand-china.com', id: '2020-02-26-iam-label-add-column') {
        addColumn(tableName: 'IAM_LABEL') {
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'IAM_LABEL') {
            column(name: "tag", type: "varchar(" + 240 * weight + ")", remarks: "标记")
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-04-30-iam_label') {
        addColumn(tableName: 'IAM_LABEL') {
            column(name: "inherit_flag", type: "tinyint", defaultValue: "0", remarks: "是否可继承") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'IAM_LABEL') {
            column(name: "preset_flag", type: "tinyint", defaultValue: "0", remarks: "是否内置标签，内置标签页面不可更新") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'IAM_LABEL') {
            column(name: "visible_flag", type: "tinyint", defaultValue: "1", remarks: "是否页面可见") { constraints(nullable: "false") }
        }
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-05-21-iam-label') {
        addDefaultValue(tableName: 'IAM_LABEL', columnName: 'FD_LEVEL', columnDataType: "varchar(" + 32 * weight + ")",
                defaultValue: 'SITE')
    }
}
