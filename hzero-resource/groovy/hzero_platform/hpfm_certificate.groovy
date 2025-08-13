package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_certificate.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-09-29-hpfm_certificate") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_certificate_s', startValue:"1")
        }
        createTable(tableName: "hpfm_certificate", remarks: "CA证书配置") {
            column(name: "certificate_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "domain_name", type: "varchar(" + 128 * weight + ")",  remarks: "域名")  {constraints(nullable:"false")}  
            column(name: "issuer_domain_name", type: "varchar(" + 128 * weight + ")",  remarks: "颁发者")   
            column(name: "start_date", type: "date",  remarks: "有效期从")   
            column(name: "end_date", type: "date",  remarks: "有效期至")   
            column(name: "data", type: "blob",  remarks: "证书数据")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_certificate", indexName: "hpfm_certificate_n1") {
            column(name: "domain_name")
            column(name: "tenant_id")
        }

    }
}