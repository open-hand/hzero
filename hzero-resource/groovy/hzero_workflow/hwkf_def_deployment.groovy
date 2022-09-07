package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwkf_def_deployment.groovy') {
    changeSet(author: "weisen.yang@hand-china.com", id: "2021-01-27-hwkf_def_deployment") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hwkf_def_deployment_s', startValue: "1")
        }
        createTable(tableName: "hwkf_def_deployment", remarks: "工作流流程部署表") {
            column(name: "DEPLOYMENT_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "FLOW_KEY", type: "varchar(" + 30 * weight + ")", remarks: "流程定义key") { constraints(nullable: "false") }
            column(name: "VERSION", type: "int", remarks: "流程定义版本") { constraints(nullable: "false") }
            column(name: "DEPLOY_DATE", type: "datetime", remarks: "部署日期") { constraints(nullable: "false") }
            column(name: "FLOW_JSON", type: "longtext", remarks: "流程定义Json") { constraints(nullable: "false") }
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "CREATION_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "CREATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATED_BY", type: "bigint", remarks: "")
            column(name: "LAST_UPDATE_DATE", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "MODEL_ID", type: "bigint", remarks: "流程图modelId") { constraints(nullable: "false") }
            column(name: "UPGRADE_FROM", type: "bigint", remarks: "自动升级源deploymentId")
        }

        createIndex(tableName: "hwkf_def_deployment", indexName: "hwkf_def_deployment_n1") {
            column(name: "UPGRADE_FROM")
        }

        addUniqueConstraint(columnNames: "FLOW_KEY,VERSION,TENANT_ID", tableName: "hwkf_def_deployment", constraintName: "hwkf_def_deployment_u1")
    }
}