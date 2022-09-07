package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_purchase_agent_user.groovy') {
    changeSet(author: "zhiying.dong@hand-china.com", id: "2019-03-11-hpfm_purchase_agent_user") {
    if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_purchase_agent_user_s', startValue:"1")
        }
        createTable(tableName: "hpfm_purchase_agent_user", remarks: "采购员用户") {
        column(name: "agent_user_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
        column(name: "purchase_agent_id", type: "bigint",  remarks: "公司hpfm_purchase_agent.purchase_agent_id")  {constraints(nullable:"false")}  
        column(name: "user_id", type: "bigint",  remarks: "指定用户,iam_user.user_id")  {constraints(nullable:"false")}  
        column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
        column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
        column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
        column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

    }

   addUniqueConstraint(columnNames:"purchase_agent_id,user_id",tableName:"hpfm_purchase_agent_user",constraintName: "hpfm_purchase_agent_user_u1")   
  }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_purchase_agent_user") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_purchase_agent_user") {
        addColumn(tableName: 'hpfm_purchase_agent_user') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}