package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_receiver_detail.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-06-30-hmsg_receiver_detail") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_receiver_detail_s', startValue:"1")
        }
        createTable(tableName: "hmsg_receiver_detail", remarks: "接收者明细") {
            column(name: "receiver_detail_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "receiver_type_id", type: "bigint",  remarks: "接收者ID，hmsg_receiver_type.receiver_type_id")  {constraints(nullable:"false")}
            column(name: "account_type_code", type: "varchar(" + 30 * weight + ")",  remarks: "账户类型，值集：HMSG.RECEIVER.ACCOUNT_TYPE")  {constraints(nullable:"false")}  
            column(name: "account_num", type: "varchar(" + 120 * weight + ")",  remarks: "账户")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 120 * weight + ")",  remarks: "描述")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"receiver_type_id,account_type_code,account_num",tableName:"hmsg_receiver_detail",constraintName: "hmsg_receiver_detail_u1")
    }
}