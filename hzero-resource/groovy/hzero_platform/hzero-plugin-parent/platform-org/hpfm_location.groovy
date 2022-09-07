package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_location.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_location") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_location_s', startValue:"1")
        }
        createTable(tableName: "hpfm_location", remarks: "货位") {
            column(name: "location_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "location_code", type: "varchar(" + 30 * weight + ")",  remarks: "库位编码")  {constraints(nullable:"false")}  
            column(name: "location_name", type: "varchar(" + 120 * weight + ")",  remarks: "库位名称")  {constraints(nullable:"false")}  
            column(name: "inventory_id", type: "bigint",  remarks: "库房ID,hpfm_inventory.inventory_id")  {constraints(nullable:"false")}  
            column(name: "organization_id", type: "bigint",  remarks: "库存组织ID,hpfm_inv_organization.organization_id")  {constraints(nullable:"false")}  
            column(name: "ou_id", type: "bigint",  remarks: "业务实体ID,hpfm_operation_unit.ou_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "source_code", type: "varchar(" + 30 * weight + ")",  remarks: "数据来源 值集：HPFM.DATA_SOURCE")  {constraints(nullable:"false")}  
            column(name: "external_system_code", type: "varchar(" + 30 * weight + ")",  remarks: "外部来源系统代码")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_location", indexName: "hpfm_location_n1") {
            column(name: "location_code")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hpfm_location", indexName: "hpfm_location_n2") {
            column(name: "inventory_id")
            column(name: "organization_id")
        }

    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_location") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_location') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}