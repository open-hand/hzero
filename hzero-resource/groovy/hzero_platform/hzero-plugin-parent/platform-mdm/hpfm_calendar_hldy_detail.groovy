package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_calendar_hldy_detail.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_calendar_hldy_detail") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_calendar_hldy_detail_s', startValue:"1")
        }
        createTable(tableName: "hpfm_calendar_hldy_detail", remarks: "日历假期明细表") {
            column(name: "holiday_detail_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "calendar_id", type: "bigint",  remarks: "日历表ID")  {constraints(nullable:"false")}  
            column(name: "holiday_id", type: "bigint",  remarks: "日历假期ID")  {constraints(nullable:"false")}  
            column(name: "holiday_date", type: "date",  remarks: "日期")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"calendar_id,holiday_date",tableName:"hpfm_calendar_hldy_detail",constraintName: "hpfm_calendar_hldy_detail_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_calendar_hldy_detail") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_calendar_hldy_detail") {
        addColumn(tableName: 'hpfm_calendar_hldy_detail') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}