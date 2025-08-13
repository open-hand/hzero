package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_receiver_type_line.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-06-12-hmsg_receiver_type_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_receiver_type_line_s', startValue:"1")
        }
        createTable(tableName: "hmsg_receiver_type_line", remarks: "接收者类型行表") {
            column(name: "receiver_type_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "receiver_type_id", type: "bigint", remarks: "接收组类型ID") { constraints(nullable: "false") }
            column(name: "receive_target_id", type: "bigint", remarks: "接收目标ID") { constraints(nullable: "false") }
            column(name: "receive_target_tenant_id", type: "bigint", remarks: "接收目标所属租户ID") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"receiver_type_id,receive_target_id,receive_target_tenant_id",tableName:"hmsg_receiver_type_line",constraintName: "hmsg_receiver_type_line_u1")
    }
}