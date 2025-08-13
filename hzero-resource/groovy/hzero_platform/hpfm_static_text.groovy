package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_static_text.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_static_text") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_static_text_s', startValue:"1")
        }
        createTable(tableName: "hpfm_static_text", remarks: "平台静态信息") {
            column(name: "text_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "text_code", type: "varchar(" + 30 * weight + ")",  remarks: "编码")  {constraints(nullable:"false")}  
            column(name: "title", type: "varchar(" + 120 * weight + ")",  remarks: "标题")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "parent_id", type: "bigint",  remarks: "父级ID")  {constraints(nullable:"false")}  
            column(name: "start_date", type: "datetime",  remarks: "有效期从")  {constraints(nullable:"false")}  
            column(name: "end_date", type: "datetime",  remarks: "有效期至")   
            column(name: "parent_ids", type: "varchar(" + 240 * weight + ")",   defaultValue:"0",   remarks: "父级ID集合")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "company_id", type: "bigint",   defaultValue:"0",   remarks: "hpfm_company.company_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-03-28-hpfm_static_text") {
        dropNotNullConstraint(tableName: "hpfm_static_text", columnName: "company_id", columnDataType: "bigint")
        addUniqueConstraint(tableName: "hpfm_static_text", columnNames: "text_code, tenant_id", constraintName: "hpfm_static_text_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-05-16-hpfm_static_text") {
        dropUniqueConstraint(tableName: "hpfm_static_text", constraintName: "hpfm_static_text_u1")
        createIndex(tableName: "hpfm_static_text", indexName: "hpfm_static_text_n1") {
            column(name: "text_code")
            column(name: "parent_id")
            column(name: "tenant_id")
        }
    }
}