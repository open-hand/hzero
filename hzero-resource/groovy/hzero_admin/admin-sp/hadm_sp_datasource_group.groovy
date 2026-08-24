package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_sp_datasource_group.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-25-hadm_sp_datasource_group") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_sp_datasource_group_s', startValue:"1")
        }
        createTable(tableName: "hadm_sp_datasource_group", remarks: "逻辑数据源组") {
            column(name: "datasource_group_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "datasource_group_name", type: "varchar(" + 30 * weight + ")",  remarks: "逻辑数据源组名")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述说明")  {constraints(nullable:"true")}
            column(name: "default_datasource_name", type: "varchar(" + 60 * weight + ")",  remarks: "默认数据源")  {constraints(nullable:"true")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"datasource_group_name",tableName:"hadm_sp_datasource_group",constraintName: "hadm_sp_datasource_group_u1")
    }
}