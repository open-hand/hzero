package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_usecase_param.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com ", id: "2019-06-27-hitf_interface_usecase_param") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_usecase_param_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_usecase_param", remarks: "接口测试用例参数") {
            column(name: "interface_usecase_param_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "interface_usecase_id", type: "bigint",  remarks: "测试用例ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "param_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"GET",   remarks: "参数类型，代码HITF.PARAM_TYPE")   
            column(name: "param_value_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"STRING",   remarks: "参数值类型，代码HITF.PARAM_VALUE_TYPE")  {constraints(nullable:"false")}  
            column(name: "parameter_id", type: "bigint",  remarks: "参数ID")   
            column(name: "parameter_name", type: "varchar(" + 128 * weight + ")",  remarks: "参数名")  {constraints(nullable:"false")}  
            column(name: "parameter_value", type: "varchar(" + 255 * weight + ")",  remarks: "参数值")   
            column(name: "parameter_value_longtext", type: "longtext",  remarks: "LongText参数值")   
            column(name: "parameter_value_file", type: "varchar(" + 255 * weight + ")",  remarks: "文件类型参数值，例如，文件的UUID")   
            column(name: "parent_id", type: "bigint",   defaultValue:"0",   remarks: "父级ID")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
		if(!helper.isOracle()){
		    addUniqueConstraint(columnNames:"interface_usecase_id,tenant_id,parameter_name",tableName:"hitf_interface_usecase_param",constraintName: "hitf_interface_usecase_param_u1")
        }
    }

    changeSet(author: "jianbo.li@hand-china.com", id: "2019-06-28-hitf_interface_usecase_param") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(columnName:"parameter_name",columnDataType:"varchar(" + 128 * weight + ")",tableName: "hitf_interface_usecase_param")
    }
	
	changeSet(author: "jianbo.li@hand-china.com", id: "2019-07-26-hitf_interface_usecase_para") {
		if(!helper.isOracle()){
			dropUniqueConstraint(constraintName:"hitf_interface_usecase_param_u1",tableName:"hitf_interface_usecase_param")
		}
		renameTable(oldTableName:"hitf_interface_usecase_param",newTableName:"hitf_interface_usecase_para")
		addUniqueConstraint(columnNames:"interface_usecase_id,tenant_id,parameter_name",tableName:"hitf_interface_usecase_para",constraintName: "hitf_interface_usecase_para_u1")
    }
	
	changeSet(author: "jianbo.li@hand-china.com", id: "2019-11-04-hitf_interface_usecase_para") {
		if(helper.dbType().isSupportSequence()){
			dropSequence(sequenceName:"hitf_interface_usecase_param_s")
			createSequence(sequenceName: 'hitf_interface_usecase_para_s', startValue:"1")
		}
    }
	
}