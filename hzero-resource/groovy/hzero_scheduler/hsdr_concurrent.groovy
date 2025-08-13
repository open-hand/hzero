package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_concurrent.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_concurrent") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_concurrent_s', startValue:"1")
        }
        createTable(tableName: "hsdr_concurrent", remarks: "并发程序") {
            column(name: "concurrent_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "executable_id", type: "bigint",  remarks: "可执行ID")  {constraints(nullable:"false")}  
            column(name: "conc_code", type: "varchar(" + 30 * weight + ")",  remarks: "并发程序代码")  {constraints(nullable:"false")}  
            column(name: "conc_name", type: "varchar(" + 120 * weight + ")",  remarks: "并发程序名称")  {constraints(nullable:"false")}  
            column(name: "conc_description", type: "varchar(" + 240 * weight + ")",  remarks: "并发程序描述")   
            column(name: "alarm_email", type: "varchar(" + 240 * weight + ")",  remarks: "报警邮件")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",  remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"conc_code,tenant_id",tableName:"hsdr_concurrent",constraintName: "hsdr_concurrent_u1")
    }

    changeSet(id: '2019-03-14-hsdr_concurrent', author: 'zhiying.dong@hand-china.com') {
        addDefaultValue(tableName: 'hsdr_concurrent', columnName: 'object_version_number', defaultValue: '1')
    }
}