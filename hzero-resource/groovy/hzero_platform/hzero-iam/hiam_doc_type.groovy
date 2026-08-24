package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_doc_type.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_doc_type") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_doc_type_s', startValue:"1")
        }
        createTable(tableName: "hiam_doc_type", remarks: "单据类型定义") {
            column(name: "doc_type_id", type: "bigint", autoIncrement: true ,   remarks: "表ID")  {constraints(primaryKey: true)} 
            column(name: "doc_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "单据类型编码")  {constraints(nullable:"false")}  
            column(name: "doc_type_name", type: "varchar(" + 240 * weight + ")",  remarks: "单据类型名称")   
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "source_service_name", type: "varchar(" + 80 * weight + ")",  remarks: "来源微服务（也即数据实体所在微服务）")  {constraints(nullable:"false")}  
            column(name: "source_data_entity", type: "varchar(" + 80 * weight + ")",  remarks: "来源数据实体，Mapper ID")   
            column(name: "level_code", type: "varchar(" + 30 * weight + ")",  remarks: "层级，HIAM.DOC_TYPE_LEVEL_CODE,GLOBAL/TENANT")  {constraints(nullable:"false")}  
            column(name: "auth_scope_code", type: "varchar(" + 30 * weight + ")",  remarks: "权限限制范围，HIAM.AUTHORITY_SCOPE_CODE")   
            column(name: "order_seq", type: "int",  remarks: "排序号")   
            column(name: "rule_id", type: "bigint",  remarks: "数据屏蔽规则ID，创建数据屏蔽规则后，反写，HPFM_PERMISSION_RULE.RULE_ID")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"0",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"doc_type_code,tenant_id",tableName:"hiam_doc_type",constraintName: "hiam_doc_type_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-23-hiam_doc_type") {
        dropColumn(tableName: 'hiam_doc_type', columnName: 'rule_id')
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-07-24-hiam_doc_type") {
        addColumn(tableName: 'hiam_doc_type') {
            if(helper.dbType().isSupportSequence()){
				column(name: 'auth_control_type', type: 'varchar(30)', defaultValue: "", remarks: '权限控制类型，HIAM.DOC.AUTH_TYPE') { constraints(nullable: false) }
			} else {
				column(name: 'auth_control_type', type: 'varchar(30)', remarks: '权限控制类型，HIAM.DOC.AUTH_TYPE') { constraints(nullable: false) }
			}
        }
    }
}