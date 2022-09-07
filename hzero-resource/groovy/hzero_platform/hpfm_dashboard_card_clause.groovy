package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_card_clause.groovy') {
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-03-08-hpfm_dashboard_card_clause") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_card_clause_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_card_clause", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "card_id", type: "bigint",  remarks: "卡片ID") {constraints(nullable:" false")}
            column(name: "clause_id", type: "bigint",  remarks: "条目ID") {constraints(nullable:" false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")
                    {constraints(nullable:" false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")
                    {constraints(nullable:" false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")
                    {constraints(nullable:" false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")
                    {constraints(nullable:" false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")
                    {constraints(nullable:" false")}

        }
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-03-12-hpfm_dashboard_card_clause"){
        addUniqueConstraint(columnNames:"clause_id,card_id",tableName:"hpfm_dashboard_card_clause",constraintName: "hpfm_dashboard_card_clause_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-05-30-hpfm_dashboard_card_clause"){
        addColumn(tableName: "hpfm_dashboard_card_clause"){
            column(name: "order_seq", type: "int", remarks: "排序")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_dashboard_card_clause") {
        addColumn(tableName: 'hpfm_dashboard_card_clause') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}