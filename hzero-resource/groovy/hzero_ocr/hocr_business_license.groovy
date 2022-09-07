package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_business_license.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_business_license") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_business_license_s', startValue:"1")
        }
        createTable(tableName: "hocr_business_license", remarks: "营业执照识别记录表") {
            column(name: "business_license_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "name", type: "varchar(" + 120 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}  
            column(name: "type", type: "varchar(" + 30 * weight + ")",  remarks: "类型")  {constraints(nullable:"false")}  
            column(name: "address", type: "varchar(" + 240 * weight + ")",  remarks: "住所")  {constraints(nullable:"false")}  
            column(name: "legal_person", type: "varchar(" + 30 * weight + ")",  remarks: "法定代表人")  {constraints(nullable:"false")}  
            column(name: "registered_capital", type: "varchar(" + 30 * weight + ")",  remarks: "注册资本")  {constraints(nullable:"false")}  
            column(name: "found_date", type: "date",  remarks: "成立日期")  {constraints(nullable:"false")}  
            column(name: "valid_period", type: "varchar(" + 30 * weight + ")",  remarks: "营业期限")  {constraints(nullable:"false")}  
            column(name: "registration_number", type: "varchar(" + 120 * weight + ")",  remarks: "证件编号")  {constraints(nullable:"false")}  
            column(name: "uscc", type: "varchar(" + 120 * weight + ")",  remarks: "社会信用代码")  {constraints(nullable:"false")}  
            column(name: "business_scope", type: "varchar(" + 480 * weight + ")",   defaultValue:"",   remarks: "经营范围")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}