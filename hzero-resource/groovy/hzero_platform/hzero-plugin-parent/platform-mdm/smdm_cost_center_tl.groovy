package script.db

databaseChangeLog(logicalFilePath: 'script/db/smdm_cost_center_tl.groovy') {

    changeSet(author: "liangliang.jiang@hand-china.com", id: "2019-11-07-smdm_cost_center_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }

        createTable(tableName: "smdm_cost_center_tl") {
            column(name: "cost_id", type: "bigint", remarks: "表ID，主键")
            column(name: "lang", type: "varchar(" + 50 * weight + "))", remarks: "语言")
            column(name: "costName", type: "varchar(" + 50 * weight + "))", remarks: "描述")

        }
    }
    changeSet(author: "liangliang.jiang@hand-china.com", id: "2019-11-11-smdm_cost_center_tl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        renameColumn(tableName: "smdm_cost_center_tl", newColumnName: "cost_name", oldColumnName: "costName", columnDataType: "varchar(" + 50 * weight + "))")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-08-18-smdm_cost_center_tl") {
        addColumn(tableName: 'smdm_cost_center_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}

