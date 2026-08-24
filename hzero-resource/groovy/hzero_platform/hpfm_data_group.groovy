package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_data_group.groovy') {
    changeSet(author: "jianbo.li@hanf-china.com", id: "2019-07-25-hpfm_data_group") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_data_group_s', startValue:"1")
        }
        createTable(tableName: "hpfm_data_group", remarks: "数据组定义") {
            column(name: "group_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "group_code", type: "varchar(" + 60 * weight + ")",  remarks: "数据组代码")  {constraints(nullable:"false")}  
            column(name: "group_name", type: "varchar(" + 360 * weight + ")",  remarks: "数据组名称")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"group_code,tenant_id",tableName:"hpfm_data_group",constraintName: "hpfm_data_group_u1")
    }
}