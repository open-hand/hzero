package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_dcl_line.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_dcl_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_dcl_line_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_dcl_line", remarks: "安全组数据权限行") {
            column(name: "sec_grp_dcl_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_dcl_id", type: "bigint",  remarks: "安全组数据权限ID")  {constraints(nullable:"false")}  
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "data_id", type: "bigint",  remarks: "数据ID")  {constraints(nullable:"false")}  
            column(name: "data_code", type: "varchar(" + 80 * weight + ")",  remarks: "数据代码/编码")   
            column(name: "data_name", type: "varchar(" + 360 * weight + ")",  remarks: "数据名称")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_dcl_id,data_id",tableName:"hiam_sec_grp_dcl_line",constraintName: "hiam_sec_grp_dcl_line_u1")
    }
}