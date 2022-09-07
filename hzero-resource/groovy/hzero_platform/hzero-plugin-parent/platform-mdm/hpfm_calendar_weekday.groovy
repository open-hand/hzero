package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_calendar_weekday.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_calendar_weekday") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_calendar_weekday_s', startValue:"1")
        }
        createTable(tableName: "hpfm_calendar_weekday", remarks: "日历工作日表") {
            column(name: "weekday_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "calendar_id", type: "bigint",  remarks: "平台级日历表ID")  {constraints(nullable:"false")}  
            column(name: "weekday_code", type: "varchar(" + 30 * weight + ")",  remarks: "周几,HMDM.WEEKDAY")  {constraints(nullable:"false")}  
            column(name: "weekday_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否为工作日,1/是;0/否")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"calendar_id,weekday_code",tableName:"hpfm_calendar_weekday",constraintName: "hpfm_calendar_weekday_u1")
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_calendar_weekday"){
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-04-03-hpfm_calendar_weekday_1"){
        dropUniqueConstraint(tableName: "hpfm_calendar_weekday",constraintName: "hpfm_calendar_weekday_u1")
        addUniqueConstraint(columnNames:"calendar_id,weekday_code,tenant_id",tableName:"hpfm_calendar_weekday",constraintName: "hpfm_calendar_weekday_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_calendar_weekday") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_weekday') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}