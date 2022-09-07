package script.db

databaseChangeLog(logicalFilePath: 'script/db/hims_knowledge_category.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-02-hims_knowledge_category") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hims_knowledge_category_s', startValue:"1")
        }
        createTable(tableName: "hims_knowledge_category", remarks: "知识库分类表") {
            column(name: "category_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键 ")  {constraints(primaryKey: true)}
            column(name: "category_code", type: "varchar(" + 30 * weight + ")",  remarks: "知识类别编码")  {constraints(nullable:"false")}
            column(name: "category_name", type: "varchar(" + 120 * weight + ")",  remarks: "知识类别名称")  {constraints(nullable:"false")}
            column(name: "type", type: "varchar(" + 30 * weight + ")", remarks: "知识节点类型，包含dir和qa两种"){constraints(nullable:"false")}
            column(name: "parent_id", type: "bigint",  remarks: "父级知识类别Id")
            column(name: "level_path", type: "varchar(" + 480 * weight + ")", remarks: "层级路径")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户Id ")  {constraints(nullable:"false")}
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁 ")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间  ")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "创建人 ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新时间  ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "最后更新人")  {constraints(nullable:"false")}  

        }
        addUniqueConstraint(columnNames:"category_code, tenant_id",tableName:"hims_knowledge_category",constraintName: "hims_knowledge_category_u1")
    }
}