package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_tenant_card.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_tenant_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_tenant_card_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_tenant_card", remarks: "租户卡片分配") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",  remarks: "租户表hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "card_id", type: "bigint",  remarks: "卡片表hpfm_dashboard_card.id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"card_id,tenant_id",tableName:"hpfm_dashboard_tenant_card",constraintName: "hpfm_dashboard_tenant_card_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-05-08-hpfm_dashboard_tenant_card") {
        addDefaultValue(tableName: "hpfm_dashboard_tenant_card", columnName: "tenant_id", defaultValue: "0")
    }
}