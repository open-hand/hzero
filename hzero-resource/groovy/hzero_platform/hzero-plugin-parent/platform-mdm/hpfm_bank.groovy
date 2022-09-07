package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_bank.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_bank") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_bank_s', startValue:"1")
        }
        createTable(tableName: "hpfm_bank", remarks: "银行信息") {
            column(name: "bank_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "bank_code", type: "varchar(" + 30 * weight + ")",  remarks: "银行代码")  {constraints(nullable:"false")}  
            column(name: "bank_name", type: "varchar(" + 120 * weight + ")",  remarks: "银行名称")  {constraints(nullable:"false")}  
            column(name: "bank_short_name", type: "varchar(" + 60 * weight + ")",  remarks: "银行简称")   
            column(name: "bank_type_code", type: "varchar(" + 60 * weight + ")",  remarks: "银行类型,值集：HMDM.BANK_TYPE")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用标记")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"bank_code",tableName:"hpfm_bank",constraintName: "hpfm_bank_u1")
        addUniqueConstraint(columnNames:"bank_name",tableName:"hpfm_bank",constraintName: "hpfm_bank_u2")
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_bank"){
        addColumn(tableName: 'hpfm_bank') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_bank_1"){
        dropUniqueConstraint(tableName: "hpfm_bank",constraintName: "hpfm_bank_u1")
        dropUniqueConstraint(tableName: "hpfm_bank",constraintName: "hpfm_bank_u2")
        addUniqueConstraint(columnNames:"bank_code,tenant_id",tableName:"hpfm_bank",constraintName: "hpfm_bank_u1")
        addUniqueConstraint(columnNames:"bank_name,tenant_id",tableName:"hpfm_bank",constraintName: "hpfm_bank_u2")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_bank") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_bank') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}