package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_country.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_country") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_country_s', startValue:"1")
        }
        createTable(tableName: "hpfm_country", remarks: "国家定义") {
            column(name: "country_id", type: "bigint", autoIncrement: true ,   remarks: "表id，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "country_code", type: "varchar(" + 30 * weight + ")",  remarks: "国家编码")  {constraints(nullable:"false")}  
            column(name: "country_name", type: "varchar(" + 120 * weight + ")",  remarks: "国家名称")  {constraints(nullable:"false")}  
            column(name: "quick_index", type: "varchar(" + 30 * weight + ")",  remarks: "快速检索")   
            column(name: "abbreviation", type: "varchar(" + 30 * weight + ")",  remarks: "简称")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用标记")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "记录创建人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "记录创建日期")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "记录更新人")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "记录更新日期")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_country", indexName: "hpfm_country_n1") {
            column(name: "quick_index")
        }

        addUniqueConstraint(columnNames:"country_code",tableName:"hpfm_country",constraintName: "hpfm_country_u1")
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_country"){
        addColumn(tableName: 'hpfm_country') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_country_1"){
        dropUniqueConstraint(tableName: "hpfm_country",constraintName: "hpfm_country_u1")
        addUniqueConstraint(columnNames:"country_code,tenant_id",tableName:"hpfm_country",constraintName: "hpfm_country_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_country") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_country') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}