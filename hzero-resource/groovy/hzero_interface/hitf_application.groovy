package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_application.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-09-05-hitf_application") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_application_s', startValue:"1")
        }
        createTable(tableName: "hitf_application", remarks: "应用产品配置") {
            column(name: "application_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "application_code", type: "varchar(" + 80 * weight + ")",  remarks: "应用产品代码")  {constraints(nullable:"false")}  
            column(name: "application_name", type: "varchar(" + 255 * weight + ")",  remarks: "应用产品名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "major_category", type: "varchar(" + 30 * weight + ")",  remarks: "大类，代码HITF.APP_MAJOR_CATEGORY")  {constraints(nullable:"false")}  
            column(name: "minor_category", type: "varchar(" + 30 * weight + ")",  remarks: "小类，代码HITF.APP_MINOR_CATEGORY")  {constraints(nullable:"false")}  
            column(name: "compose_policy", type: "varchar(" + 30 * weight + ")",   defaultValue:"ROUND_ROBIN",   remarks: "编排策略，代码：HITF.COMPOSE_POLICY")   
            column(name: "interface_id", type: "bigint",  remarks: "开放接口ID")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否启用。1启用，0未启用")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"application_code,tenant_id",tableName:"hitf_application",constraintName: "hitf_application_u1")
        addUniqueConstraint(columnNames:"interface_id",tableName:"hitf_application",constraintName: "hitf_application_u2")
    }
	
	changeSet(author: "jianbo.li@hand-china.com",id: "2019-11-05-hitf_application"){
		def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName:"hitf_application"){
			column(name: "fast_fail_flag",type: "tinyint",remarks: "快速失败",defaultValue:"1")
		}
    }
}