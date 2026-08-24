package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_data_hierarchy.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-09-04-hpfm_data_hierarchy") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_data_hierarchy_s', startValue:"1")
        }
        createTable(tableName: "hpfm_data_hierarchy", remarks: "数据层级配置") {
            column(name: "data_hierarchy_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "data_hierarchy_code", type: "varchar(" + 30 * weight + ")",  remarks: "数据层级编码")  {constraints(nullable:"false")}  
            column(name: "data_hierarchy_name", type: "varchar(" + 60 * weight + ")",   defaultValue:" ",   remarks: "数据层级名称")  {constraints(nullable:"false")}  
            column(name: "parent_id", type: "bigint",  remarks: "上级数据层级ID，hpfm_data_hierarchy.data_hierarchy_id")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "value_source_id", type: "bigint",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "level_path", type: "varchar(" + 480 * weight + ")",  remarks: "层级路径")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "排序号")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"data_hierarchy_code,tenant_id",tableName:"hpfm_data_hierarchy",constraintName: "hpfm_data_hierarchy_u1")
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-06-10-hpfm_data_hierarchy") {
        addColumn(tableName: "hpfm_data_hierarchy") {
            column(name: "display_style", type: "varchar(30)", defaultValue: "MODAL", remarks: "显示样式，默认模态框") {
                constraints(nullable: "false")
            }
        }

    }
}