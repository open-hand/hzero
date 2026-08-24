package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_doc_type_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_assign_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type_assign", remarks: "单据类型分配") {
            column(name: "assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "doc_type_id", type: "bigint",  remarks: "单据类型ID，HIAM_DOC_TYPE.DOC_TYPE_ID")  {constraints(nullable:"false")}  
            column(name: "assign_value_id", type: "bigint",  remarks: "分配值ID，当前level_code=TENANT，则此处为租户ID，HPFM_TENANT.TENANT_ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"doc_type_id,assign_value_id",tableName:"hiam_doc_type_assign",constraintName: "hiam_doc_type_assign_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hiam_doc_type_assign") {
        addColumn(tableName: 'hiam_doc_type_assign') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}