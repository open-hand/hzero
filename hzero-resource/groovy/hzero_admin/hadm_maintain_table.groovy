package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_maintain_table.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-14-hadm_maintain_table") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_maintain_table_s', startValue:"1")
        }
        createTable(tableName: "hadm_maintain_table", remarks: "在线运维表") {
            column(name: "maintain_table_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "maintain_id", type: "bigint",  remarks: "在线运维ID，hadm.maintain_id")  {constraints(nullable:"false")}
            column(name: "service_code", type: "varchar(" + 120 * weight + ")",  remarks: "服务名")  {constraints(nullable:"false")}
            column(name: "table_name", type: "varchar(" + 60 * weight + ")",  remarks: "表名")  {constraints(nullable:"false")}
            column(name: "maintain_mode", type: "varchar(" + 30 * weight + ")",  remarks: "表运维模式")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"maintain_id,service_code,table_name",tableName:"hadm_maintain_table",constraintName: "hadm_maintain_table_u1")
    }
}