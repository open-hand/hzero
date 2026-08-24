package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_acl_dashboard.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_acl_dashboard") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_acl_dashboard_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_acl_dashboard", remarks: "安全组工作台配置") {
            column(name: "sec_grp_acl_dashboard_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "card_id", type: "int",  remarks: "卡片ID，hpfm.hpfm_dashboard_card.id")  {constraints(nullable:"false")}  
            column(name: "x", type: "tinyint",  remarks: "x轴")  {constraints(nullable:"false")}  
            column(name: "y", type: "tinyint",  remarks: "y轴")  {constraints(nullable:"false")}  
            column(name: "default_display_flag", type: "tinyint",  remarks: "初始化标识")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_id,tenant_id,card_id",tableName:"hiam_sec_grp_acl_dashboard",constraintName: "hiam_sec_grp_acl_dashboard_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-11-25-hiam_sec_grp_acl_dashboard") {
        dropUniqueConstraint(tableName: 'hiam_sec_grp_acl_dashboard', constraintName: 'hiam_sec_grp_acl_dashboard_u1')
        modifyDataType(tableName: "hiam_sec_grp_acl_dashboard", columnName: 'card_id', newDataType: "bigint")
        addUniqueConstraint(columnNames:"sec_grp_id,tenant_id,card_id",tableName:"hiam_sec_grp_acl_dashboard",constraintName: "hiam_sec_grp_acl_dashboard_u1")
    }
}