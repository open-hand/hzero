package script.db

databaseChangeLog(logicalFilePath: 'script/db/hims_cs_group_user_tag.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-02-hims_cs_group_user_tag") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hims_cs_group_user_tag_s', startValue:"1")
        }
        createTable(tableName: "hims_cs_group_user_tag", remarks: "群组客户知识标签") {
            column(name: "cs_group_user_tag_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键 ")  {constraints(primaryKey: true)}
            column(name: "cs_group_id", type: "bigint",  remarks: "群组客服关系Id，hims_cs_group.cs_group_id")  {constraints(nullable:"false")}
            column(name: "user_id", type: "varchar(" + 20 * weight + ")",  remarks: "用户Id，iam_user.id")  {constraints(nullable:"false")}
            column(name: "category_id", type: "bigint",  remarks: "知识分类Id,hims_knowledge_category.category_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁 ")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间  ")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "创建人 ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新时间  ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "最后更新人")  {constraints(nullable:"false")}  

        }
        addUniqueConstraint(columnNames:"cs_group_id,user_id,category_id",tableName:"hims_cs_group_user_tag",constraintName: "hims_cs_group_user_tag_u1")
    }
}