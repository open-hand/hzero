package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_currency.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_currency") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_currency_s', startValue:"1")
        }
        createTable(tableName: "hpfm_currency", remarks: "币种信息") {
            column(name: "currency_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "currency_code", type: "varchar(" + 30 * weight + ")",  remarks: "币种代码")  {constraints(nullable:"false")}  
            column(name: "currency_name", type: "varchar(" + 120 * weight + ")",  remarks: "币种名称")  {constraints(nullable:"false")}  
            column(name: "country_id", type: "bigint",  remarks: "国家/地区代码,hmdm_country.country_id")  {constraints(nullable:"false")}  
            column(name: "financial_precision", type: "int",  remarks: "财务精度")  {constraints(nullable:"false")}  
            column(name: "default_precision", type: "int",  remarks: "精度")  {constraints(nullable:"false")}  
            column(name: "currency_symbol", type: "varchar(" + 30 * weight + ")",  remarks: "货币符号")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标志")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"currency_code",tableName:"hpfm_currency",constraintName: "hpfm_currency_u1")
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_currency"){
        addColumn(tableName: 'hpfm_currency') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_currency_1"){
        dropUniqueConstraint(tableName: "hpfm_currency",constraintName: "hpfm_currency_u1")
        addUniqueConstraint(columnNames:"currency_code,tenant_id",tableName:"hpfm_currency",constraintName: "hpfm_currency_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_currency") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_currency') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}