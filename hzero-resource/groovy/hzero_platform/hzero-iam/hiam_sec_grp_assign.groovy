package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_assign.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_assign_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_assign", remarks: "安全组分配关系") {
            column(name: "user_sec_grp_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "assign_dimension", type: "varchar(" + 30 * weight + ")",  remarks: "分配维度：HIAM.SEC_GRP_ASSIGN_DIM[USER/ROLE]")  {constraints(nullable:"false")}  
            column(name: "dimension_value", type: "bigint",  remarks: "维度值[USER(用户ID),ROLE(角色ID)]")  {constraints(nullable:"false")}  
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID,hiam_sec_grp.sec_grp_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"assign_dimension,dimension_value,sec_grp_id,tenant_id",tableName:"hiam_sec_grp_assign",constraintName: "hiam_sec_grp_assign_u1")
    }
}