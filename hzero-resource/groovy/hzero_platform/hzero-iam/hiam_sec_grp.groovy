package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp", remarks: "安全组") {
            column(name: "sec_grp_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_code", type: "varchar(" + 60 * weight + ")",  remarks: "安全组代码")  {constraints(nullable:"false")}  
            column(name: "sec_grp_name", type: "varchar(" + 255 * weight + ")",  remarks: "安全组名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "role_id", type: "bigint",  remarks: "所属角色ID")  {constraints(nullable:"false")}  
            column(name: "level", type: "varchar(" + 30 * weight + ")",  remarks: "层级，引用HIAM.SECURITY_GROUP_LEVEL")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "draft_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否草稿状态，1草稿，0非草稿，草稿状态不可分配至角色")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_code,level,tenant_id",tableName:"hiam_sec_grp",constraintName: "hiam_sec_grp_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hiam_sec_grp") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: "hiam_sec_grp") {
            column(name: "sec_grp_level", type: "varchar(" + 30 * weight + ")", remarks: "层级，引用HIAM.SECURITY_GROUP_LEVEL", defaultValue: "")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-1-hiam_sec_grp") {
        dropUniqueConstraint(tableName: "hiam_sec_grp", constraintName: "hiam_sec_grp_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-2-hiam_sec_grp") {
        addUniqueConstraint(columnNames:"sec_grp_code,sec_grp_level,tenant_id",tableName:"hiam_sec_grp",constraintName: "hiam_sec_grp_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-3-hiam_sec_grp") {
        dropColumn(tableName: "hiam_sec_grp", columnName:"level")
    }

}