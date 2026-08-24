package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_dashboard_clause.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_dashboard_clause") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_dashboard_clause_s', startValue:"1")
        }
        createTable(tableName: "hpfm_dashboard_clause", remarks: "工作台条目配置") {
            column(name: "clause_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "card_id", type: "bigint",  remarks: "卡片ID")  {constraints(nullable:"false")}  
            column(name: "clause_code", type: "varchar(" + 30 * weight + ")",  remarks: "条目代码")  {constraints(nullable:"false")}  
            column(name: "clause_name", type: "varchar(" + 60 * weight + ")",  remarks: "条目名称")  {constraints(nullable:"false")}  
            column(name: "data_tenant_level", type: "varchar(" + 30 * weight + ")",  remarks: "数据租户级别HPFM.DATA_TENANT_LEVEL(SITE/平台级|TENANT/租户级)")  {constraints(nullable:"false")}  
            column(name: "menu_code", type: "varchar(" + 128 * weight + ")",  remarks: "跳转至功能")   
            column(name: "route", type: "varchar(" + 128 * weight + ")",  remarks: "路由")   
            column(name: "stats_expression", type: "varchar(" + 360 * weight + ")",  remarks: "数据匹配表达式")  {constraints(nullable:"false")}  
            column(name: "doc_remark_expression", type: "varchar(" + 360 * weight + ")",  remarks: "单据标题/备注计算表达式")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"card_id,clause_code",tableName:"hpfm_dashboard_clause",constraintName: "hpfm_dashboard_clause_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-03-06-hpfm_dashboard_clause"){
        dropUniqueConstraint(tableName: "hpfm_dashboard_clause",constraintName: "hpfm_dashboard_clause_u1")
        dropColumn(tableName: "hpfm_dashboard_clause", columnName: "card_id")
        addUniqueConstraint(columnNames:"clause_code",tableName:"hpfm_dashboard_clause",constraintName: "hpfm_dashboard_clause_u1")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-04-11-hpfm_dashboard_clause"){
        dropUniqueConstraint(tableName: "hpfm_dashboard_clause",constraintName: "hpfm_dashboard_clause_u1")
        addColumn(tableName: "hpfm_dashboard_clause"){
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"clause_code,tenant_id",tableName:"hpfm_dashboard_clause",constraintName: "hpfm_dashboard_clause_u1")
    }
}