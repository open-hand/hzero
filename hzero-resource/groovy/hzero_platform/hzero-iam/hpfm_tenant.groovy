package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tenant.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_tenant") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_tenant_s', startValue:"1")
        }
        createTable(tableName: "hpfm_tenant", remarks: "租户信息") {
            column(name: "tenant_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_num", type: "varchar(" + 30 * weight + ")",  remarks: "租户编码")  {constraints(nullable:"false")}  
            column(name: "tenant_name", type: "varchar(" + 120 * weight + ")",  remarks: "租户名")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
        createIndex(tableName: "hpfm_tenant", indexName: "hpfm_tenant_n1") {
            column(name: "tenant_num")
        }
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-04-29-hpfm_tenant") {
        addColumn(tableName: "hpfm_tenant") {
            column(name: "limit_user_qty", type: "int", remarks: "租户下的有效用户数，null表示不限制")
        }
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-04-20-hpfm_tenant") {
        addUniqueConstraint(columnNames: "tenant_num", tableName: "hpfm_tenant", constraintName: "hpfm_tenant_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_tenant") {
        addColumn(tableName: "hpfm_tenant") {
            column(name: "ext_info", type: "longtext", remarks: "")
        }
    }

}