package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_group.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_group") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_group_s', startValue:"1")
        }
        createTable(tableName: "hpfm_group", remarks: "集团信息") {
            column(name: "group_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "group_num", type: "varchar(" + 30 * weight + ")",  remarks: "集团编码，hpfm_hr_unit.unit_code")  {constraints(nullable:"false")}  
            column(name: "group_name", type: "varchar(" + 150 * weight + ")",  remarks: "集团名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "unit_id", type: "bigint",  remarks: "组织id")   
            column(name: "source_key", type: "varchar(" + 60 * weight + ")",  remarks: "源数据key")   
            column(name: "source_code", type: "varchar(" + 30 * weight + ")",  remarks: "来源,值集：HPFM.DATA_SOURCE")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "core_flag", type: "tinyint",   defaultValue:"0",   remarks: "")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"source_key,source_code",tableName:"hpfm_group",constraintName: "hpfm_group_u2")
        addUniqueConstraint(columnNames:"tenant_id",tableName:"hpfm_group",constraintName: "hpfm_group_u3")
        addUniqueConstraint(columnNames:"group_num",tableName:"hpfm_group",constraintName: "hpfm_group_u4")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-10-hpfm_group") {
        dropUniqueConstraint(tableName: "hpfm_group", constraintName: "hpfm_group_u2")
    }
}