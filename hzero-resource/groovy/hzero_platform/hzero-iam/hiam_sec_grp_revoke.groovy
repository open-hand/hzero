package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_revoke.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_revoke") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_revoke_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_revoke", remarks: "安全组权限回收") {
            column(name: "sec_grp_revoke_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "revoke_type", type: "varchar(" + 30 * weight + ")",  remarks: "回收类型，HIAM.SEC_GRP_REVOKE_TYPE，REVOKE-回收/SHIELD-屏蔽")  {constraints(nullable:"false")}  
            column(name: "authority_type", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型，HIAM.SEC_GRP_AUTHORITY_TYPE")  {constraints(nullable:"false")}  
            column(name: "authority_id", type: "bigint",  remarks: "安全组权限ID")  {constraints(nullable:"false")}  
            column(name: "shield_role_id", type: "bigint",  remarks: "屏蔽角色ID")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hiam_sec_grp_revoke", indexName: "hiam_sec_grp_revoke_n1") {
            column(name: "sec_grp_id")
            column(name: "revoke_type")
            column(name: "authority_type")
            column(name: "authority_id")
            column(name: "shield_role_id")
        }

    }
}