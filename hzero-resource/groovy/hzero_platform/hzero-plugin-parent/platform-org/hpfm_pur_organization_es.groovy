package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_pur_organization_es.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_pur_organization_es") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_pur_organization_es_s', startValue:"1")
        }
        createTable(tableName: "hpfm_pur_organization_es", remarks: "采购组织关联表") {
            column(name: "pur_org_es_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "external_system_code", type: "varchar(" + 30 * weight + ")",  remarks: "集团编码")  {constraints(nullable:"false")}  
            column(name: "purchase_org_id", type: "bigint",  remarks: "hzero平台采购组织id")  {constraints(nullable:"false")}  
            column(name: "es_purchase_org_id", type: "varchar(" + 50 * weight + ")",  remarks: "erp平台采购组织id")   
            column(name: "es_purchase_org_code", type: "varchar(" + 30 * weight + ")",  remarks: "erp平台采购组织代码")   
            column(name: "data_version", type: "bigint",  remarks: "erp平台数据版本")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"external_system_code,purchase_org_id",tableName:"hpfm_pur_organization_es",constraintName: "hpfm_pur_organization_es_u1")
        addUniqueConstraint(columnNames:"external_system_code,es_purchase_org_id,es_purchase_org_code",tableName:"hpfm_pur_organization_es",constraintName: "hpfm_pur_organization_es_u2")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_pur_organization_es") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_pur_organization_es") {
        addColumn(tableName: 'hpfm_pur_organization_es') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}