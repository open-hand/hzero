package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_id_card.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_id_card") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_id_card_s', startValue:"1")
        }
        createTable(tableName: "hocr_id_card", remarks: "身份证识别记录表") {
            column(name: "id_card_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "name", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "姓名")  {constraints(nullable:"false")}  
            column(name: "sex", type: "varchar(" + 10 * weight + ")",   defaultValue:"",   remarks: "性别")  {constraints(nullable:"false")}  
            column(name: "birth", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "出生日期")  {constraints(nullable:"false")}  
            column(name: "ethnicity", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "民族")  {constraints(nullable:"false")}  
            column(name: "address", type: "varchar(" + 120 * weight + ")",   defaultValue:"",   remarks: "地址")  {constraints(nullable:"false")}  
            column(name: "number", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "身份证号")  {constraints(nullable:"false")}  
            column(name: "issue", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "签发机关")  {constraints(nullable:"false")}  
            column(name: "valid_from", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "签发日期")  {constraints(nullable:"false")}  
            column(name: "valid_to", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "失效日期")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}