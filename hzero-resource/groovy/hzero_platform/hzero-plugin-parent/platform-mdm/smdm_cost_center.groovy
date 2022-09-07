package script.db

databaseChangeLog(logicalFilePath: 'script/db/smdm_cost_center.groovy') {

    changeSet(author: "liangliang.jiang@hand-china.com", id: "2019-11-09-smdm_cost_center") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'smdm_cost_center_s', startValue: "1")
        }

        createTable(tableName: "smdm_cost_center") {

            column(name: "cost_id", type: "bigint", autoIncrement: "true", startWith: "1", remarks: "表ID，主键") {
                constraints(primaryKey: "true", primaryKeyName: "smdm_cost_id_PK")
            }
            column(name: "tenant_id", type: "BIGINT", remarks: "所属租户ID，hpfm_tenant.tenant_id")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "cost_code", type: "varchar(" + 30 * weight + "))", remarks: "成本中心")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "cost_name", type: "varchar(" + 12 * weight + "))", remarks: "成本名称") {
                constraints(nullable: "false")
            }
            column(name: "control_area", type: "varchar(" + 12 * weight + "))", remarks: "控制范围")
            column(name: "company_code", type: "varchar(" + 12 * weight + "))", remarks: "公司代码")
            column(name: "principal", type: "varchar(" + 60 * weight + "))", remarks: "负责人")
            column(name: "effective_date", type: "DATETIME", remarks: "开始生效日期")
            column(name: "effective_deadline", type: "DATETIME", remarks: "有效截至日期")
            column(name: "enabled_flag", type: "tinyint", remarks: "标识: 是否标记待删除科目")
            column(name: "illustration", type: "varchar(" + 3 * weight + "))", remarks: "简要说明")
            column(name: "source_code", type: "varchar(" + 30 * weight + "))", remarks: "数据来源")
            column(name: "external_system_code", type: "varchar(" + 30 * weight + "))", remarks: "外部系统代码")

            column(name: "query_cost_code", type: "varchar(" + 30 * weight + "))", remarks: "查询编码")
                    {
                        constraints(nullable: "false")
                    }

            column(name: "OBJECT_VERSION_NUMBER", type: "BIGINT", defaultValue: "1", remarks: "行版本号，用来处理锁")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "CREATED_BY", type: "BIGINT", defaultValue: "-1")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "CREATION_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "LAST_UPDATED_BY", type: "BIGINT", defaultValue: "-1")
                    {
                        constraints(nullable: "false")
                    }
            column(name: "LAST_UPDATE_DATE", type: "DATETIME", defaultValueComputed: "CURRENT_TIMESTAMP")
                    {
                        constraints(nullable: "false")
                    }

        }
        addUniqueConstraint(columnNames: "query_cost_code,external_system_code", tableName: "smdm_cost_center", constraintName: "smdm_cost_center_U1")
        createIndex(tableName: "smdm_cost_center", indexName: "smdm_cost_center_N1") {
            column(name: "cost_code")
        }
    }
    changeSet(author: "tingxiu.zhou@hand-china.com",id: "2019-12-05-add_column"){
        addColumn(tableName: "smdm_cost_center"){
            column(name: "company_id", type: "bigint", remarks: "公司ID")
        }
    }

    changeSet(author: "tingxiu.zhou@hand-china.com",id: "2019-12-05-add_column-ou_id"){
        addColumn(tableName: "smdm_cost_center"){
            column(name: "ou_id", type: "bigint", remarks: "业务实体ID")
        }
    }

    changeSet(author: "siqi.qiu@hand-china.com",id: "2019-12-10-drop_column-cost_name"){
        dropColumn(tableName: "smdm_cost_center",columnName:"cost_name" )
    }
    changeSet(author: "siqi.qiu@hand-china.com",id: "2019-12-05-add_column-cost_name"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'smdm_cost_center'){
            column(name: "cost_name", type: "varchar(" + 120 * weight + ")",  remarks: "成本名称")
        }
    }
    changeSet(author: "siqi.qiu@hand-china.com",id: "2019-12-05-drop_unique_cost_center"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropUniqueConstraint(tableName:"smdm_cost_center",constraintName: "smdm_cost_center_U1")
        addUniqueConstraint(columnNames: "tenant_id,query_cost_code,external_system_code", tableName: "smdm_cost_center", constraintName: "smdm_cost_center_U1")
    }
}

