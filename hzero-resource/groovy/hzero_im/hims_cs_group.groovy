package script.db

databaseChangeLog(logicalFilePath: 'script/db/hims_cs_group.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-10-25-hims_cs_group") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hims_cs_group_s', startValue:"1")
        }
        createTable(tableName: "hims_cs_group", remarks: "客服群组信息表") {
            column(name: "cs_group_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键 ")  {constraints(primaryKey: true)}
            column(name: "group_key", type: "varchar(" + 60 * weight + ")",  remarks: "群组Key")  {constraints(nullable:"false")}
            column(name: "group_name", type: "varchar(" + 60 * weight + ")",  remarks: "群组名称")  {constraints(nullable:"false")}
            column(name: "description", type: "varchar(" + 480 * weight + ")",   defaultValue:"0",   remarks: "群组描述")
            column(name: "face_url", type: "varchar(" + 480 * weight + ")",  remarks: "群头像")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用 ")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁 ")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间  ")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "创建人 ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新时间  ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "最后更新人")  {constraints(nullable:"false")}  

        }
        createIndex(tableName: "hims_cs_group", indexName: "hims_cs_group_n1") {
            column(name: "group_name")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"group_key",tableName:"hims_cs_group",constraintName: "hims_cs_group_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hims_cs_group") {
        dropDefaultValue(tableName: 'hims_cs_group', columnName: 'description')
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-01-hims_cs_group") {
        addDefaultValue(tableName: "hims_cs_group", columnName: "description", defaultValue: "null")
    }
}
