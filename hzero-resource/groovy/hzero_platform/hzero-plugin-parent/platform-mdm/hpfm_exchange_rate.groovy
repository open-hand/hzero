package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_exchange_rate.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_exchange_rate") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_exchange_rate_s', startValue:"1")
        }
        createTable(tableName: "hpfm_exchange_rate", remarks: "汇率定义") {
            column(name: "exchange_rate_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "rate_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "汇率类型代码,hmdm_exchange_rate_type.type_code")  {constraints(nullable:"false")}  
            column(name: "from_currency_code", type: "varchar(" + 30 * weight + ")",  remarks: "被兑换货币CODE,hmdm_currency.currency_code")  {constraints(nullable:"false")}  
            column(name: "to_currency_code", type: "varchar(" + 30 * weight + ")",  remarks: "兑换货币CODE,hmdm_currency.currency_code")  {constraints(nullable:"false")}  
            column(name: "rate", type: "decimal(16,8)",  remarks: "税率，以1为基本单位")  {constraints(nullable:"false")}  
            column(name: "rate_date", type: "date",  remarks: "汇率日期")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rate_type_code,from_currency_code,to_currency_code,rate_date",tableName:"hpfm_exchange_rate",constraintName: "hpfm_exchange_rate_u1")
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_exchange_rate"){
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_exchange_rate_1"){
        dropUniqueConstraint(tableName: "hpfm_exchange_rate",constraintName: "hpfm_exchange_rate_u1")
        addUniqueConstraint(columnNames:"rate_type_code,from_currency_code,to_currency_code,rate_date,tenant_id",tableName:"hpfm_exchange_rate",constraintName: "hpfm_exchange_rate_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_exchange_rate") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_exchange_rate') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}