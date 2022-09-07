package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_sqlid.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-06-10-hiam_doc_type_sqlid") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_sqlid_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type_sqlid", remarks: "单据类型定义SQLID") {
            column(name: "doc_type_sqlid_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "doc_type_id", type: "bigint",  remarks: "单据类型ID，hiam_doc_type.doc_type_id")  {constraints(nullable:"false")}  
            column(name: "sqlid", type: "varchar(" + 240 * weight + ")",  remarks: "来源数据实体，Mapper ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"doc_type_id,sqlid",tableName:"hiam_doc_type_sqlid",constraintName: "hiam_doc_type_sqlid_u1")
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2020-07-31-hiam_doc_type_sqlid") {
        addColumn(tableName: 'hiam_doc_type_sqlid') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}
