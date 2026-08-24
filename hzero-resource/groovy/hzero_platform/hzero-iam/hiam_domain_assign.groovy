package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_domain_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiam_domain_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_domain_assign_s', startValue:"1")
        }
        createTable(tableName: "hiam_domain_assign", remarks: "单点二级域名分配") {
            column(name: "domain_assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "domain_id", type: "bigint",  remarks: "域名ID，hiam_domain.domain_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint", defaultValue:"0",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "company_id", type: "bigint",   defaultValue:"0",   remarks: "客户公司ID，HPFM_COMPANY.COMPANY_ID")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"domain_id,tenant_id,company_id",tableName:"hiam_domain_assign",constraintName: "hiam_domain_assign_u1")
    }
}
