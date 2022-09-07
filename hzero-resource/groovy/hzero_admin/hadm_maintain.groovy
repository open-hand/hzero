package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_maintain.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-14-hadm_maintain") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_maintain_s', startValue:"1")
        }
        createTable(tableName: "hadm_maintain", remarks: "在线运维") {
            column(name: "maintain_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "maintain_version", type: "varchar(" + 60 * weight + ")",  remarks: "在线运维版本")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")  {constraints(nullable:"true")}
            column(name: "state", type: "varchar(" + 30 * weight + ")",  remarks: "运维状态")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"maintain_version",tableName:"hadm_maintain",constraintName: "hadm_maintain_u1")
    }
}