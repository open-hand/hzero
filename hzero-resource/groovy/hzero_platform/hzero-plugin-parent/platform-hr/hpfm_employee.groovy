package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_employee.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_employee") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_employee_s', startValue:"1")
        }
        createTable(tableName: "hpfm_employee", remarks: "员工表") {
            column(name: "employee_id", type: "bigint", autoIncrement: true ,   remarks: "员工ID")  {constraints(primaryKey: true)} 
            column(name: "employee_num", type: "varchar(" + 30 * weight + ")",  remarks: "员工编码")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 60 * weight + ")",  remarks: "员工姓名")  {constraints(nullable:"false")}  
            column(name: "name_en", type: "varchar(" + 60 * weight + ")",  remarks: "员工英文名")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "email", type: "varchar(" + 60 * weight + ")",  remarks: "电子邮件")   
            column(name: "mobile", type: "varchar(" + 60 * weight + ")",  remarks: "移动电话")   
            column(name: "gender", type: "tinyint",  remarks: "性别, 值集:HPFM.GENDER")  {constraints(nullable:"false")}
            column(name: "cid", type: "varchar(" + 60 * weight + ")",  remarks: "身份编码")   
            column(name: "quick_index", type: "varchar(" + 30 * weight + ")",  remarks: "快速检索")   
            column(name: "phoneticize", type: "varchar(" + 60 * weight + ")",  remarks: "拼音")   
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "员工状态，值集HPFM.EMPLOYEE_STATUS")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用状态")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁 ")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建时间 ")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新时间")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最后更新人")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_employee", indexName: "hpfm_employee_n1") {
            column(name: "name")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"employee_id,employee_num",tableName:"hpfm_employee",constraintName: "hpfm_employee_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_employee") {
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_employee') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }

    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-18-hpfm_employee") {
        modifyDataType(tableName: "hpfm_employee", columnName: 'phoneticize', newDataType: "varchar(" + 240 * weight + ")")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-24-hpfm_employee") {
        modifyDataType(tableName: "hpfm_employee", columnName: 'quick_index', newDataType: "varchar(" + 240 * weight + ")")
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-26-hpfm_employee") {
        dropUniqueConstraint(tableName: "hpfm_employee",constraintName: "hpfm_employee_u1")
        addUniqueConstraint(columnNames:"employee_num,tenant_id",tableName:"hpfm_employee",constraintName: "hpfm_employee_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-11-11-hpfm_employee") {
        addColumn(tableName: 'hpfm_employee') {
            column(name: "entry_date", type: "datetime", remarks: "员工入职时间")
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-10-16-hpfm_employee") {
        //mysql不支持setColumnRemarks命令
        if (!helper.isMysql()){
            setColumnRemarks (tableName: "hpfm_employee", columnName: "gender", remarks: "性别, 值集:HPFM.GENDER")
        }
        dropNotNullConstraint (tableName: "hpfm_employee", columnName: "gender", columnDataType: "tinyint")
    }
}