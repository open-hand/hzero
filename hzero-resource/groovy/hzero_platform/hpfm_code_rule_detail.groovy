package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_code_rule_detail.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_code_rule_detail") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_code_rule_detail_s', startValue:"1")
        }
        createTable(tableName: "hpfm_code_rule_detail", remarks: "编码规则明细") {
            column(name: "rule_detail_id", type: "bigint", autoIncrement: true ,   remarks: "编码规则详情id ")  {constraints(primaryKey: true)} 
            column(name: "rule_dist_id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "int",  remarks: "序号，决定编码规则生成的顺序")  {constraints(nullable:"false")}  
            column(name: "field_type", type: "varchar(" + 30 * weight + ")",  remarks: "段类型")  {constraints(nullable:"false")}  
            column(name: "field_value", type: "varchar(" + 240 * weight + ")",  remarks: "段值")   
            column(name: "date_mask", type: "varchar(" + 30 * weight + ")",  remarks: "日期格式")   
            column(name: "seq_length", type: "bigint",  remarks: "位数")   
            column(name: "start_value", type: "bigint",  remarks: "开始值")   
            column(name: "current_value", type: "bigint",  remarks: "当前值")   
            column(name: "reset_frequency", type: "varchar(" + 30 * weight + ")",  remarks: "重置频率")   
            column(name: "reset_date", type: "datetime",  remarks: "上次重置日期")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "创建人  ")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "创建日期 ")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "最后更新人  ")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "最后更新日期 ")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_code_rule_detail", indexName: "hpfm_code_rule_detail_n1") {
            column(name: "rule_dist_id")
        }

        addUniqueConstraint(columnNames:"rule_dist_id,order_seq",tableName:"hpfm_code_rule_detail",constraintName: "hpfm_code_rule_detail_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-03-19-hpfm_code_rule_detail") {
        addColumn(tableName: "hpfm_code_rule_detail") {
            column(name: "encrypted_flag", type: "tinyint", remarks: "加密标记", defaultValue: 0) {
                constraints(nullable: "false")
            }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_code_rule_detail") {
        addColumn(tableName: 'hpfm_code_rule_detail') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}