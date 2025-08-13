package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_executor.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_executor") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_executor_s', startValue:"1")
        }
        createTable(tableName: "hsdr_executor", remarks: "执行器") {
            column(name: "executor_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "executor_code", type: "varchar(" + 30 * weight + ")",  remarks: "执行器编码")  {constraints(nullable:"false")}  
            column(name: "executor_name", type: "varchar(" + 120 * weight + ")",  remarks: "执行器名称")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",   defaultValue:"0",   remarks: "排序")  {constraints(nullable:"false")}  
            column(name: "executor_type", type: "tinyint",   defaultValue:"0",   remarks: "执行器地址类型：0=自动注册、1=手动录入")  {constraints(nullable:"false")}  
            column(name: "address_list", type: "varchar(" + 240 * weight + ")",  remarks: "执行器地址列表，多地址逗号分隔")   
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "执行器状态")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "scope", type: "tinyint",   defaultValue:"0",   remarks: "权限范围：0=租户级、1=平台级")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"0",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"executor_code",tableName:"hsdr_executor",constraintName: "hsdr_executor_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-02-25-hsdr_executor") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if (helper.isSqlServer()) {
            dropDefaultValue(tableName: 'hsdr_executor', columnName: 'scope')
            dropColumn(tableName: 'hsdr_executor', columnName: 'scope')
        } else {
            dropColumn(tableName: 'hsdr_executor', columnName: 'scope')
        }
        addColumn(tableName: 'hsdr_executor') {
            column(name: "server_name", type: "varchar(" + 120 * weight + ")",  remarks: "服务名")
        }
    }
}
