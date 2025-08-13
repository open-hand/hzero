package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_label_rel.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-30-hiam_label_rel") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_label_rel_s', startValue:"1")
        }
        createTable(tableName: "hiam_label_rel", remarks: "标签关系表") {
            column(name: "label_rel_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "data_type", type: "varchar(" + 32 * weight + ")",  remarks: "数据类型：iam_label.type")  {constraints(nullable:"false")}  
            column(name: "data_id", type: "bigint",  remarks: "数据ID")  {constraints(nullable:"false")}
            column(name: "label_id", type: "bigint",  remarks: "标签ID：iam_label.id")  {constraints(nullable:"false")}
            column(name: "assign_type", type: "varchar(" + 32 * weight + ")",   defaultValue:"M",   remarks: "分配类型：A-自动；M-手动")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"data_type,data_id,label_id",tableName:"hiam_label_rel",constraintName: "hiam_label_rel_u1")
    }
}