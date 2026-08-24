package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_label_print.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-18-hrpt_label_print") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_label_print_s', startValue:"1")
        }
        createTable(tableName: "hrpt_label_print", remarks: "标签打印") {
            column(name: "label_print_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "label_template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板编码，hrpt_label_template.template_code")  {constraints(nullable:"false")}  
            column(name: "paper_size", type: "varchar(" + 30 * weight + ")",  remarks: "纸张尺寸，值集：HRPT.LABEL_PAPER")   
            column(name: "paper_width", type: "int",  remarks: "纸张宽度")   
            column(name: "paper_high", type: "int",  remarks: "纸张高度")   
            column(name: "print_direction", type: "tinyint",   defaultValue:"0",   remarks: "0:横向 1:纵向")  {constraints(nullable:"false")}  
            column(name: "margin_top", type: "int",  remarks: "上边距")   
            column(name: "margin_bottom", type: "int",  remarks: "下边距")   
            column(name: "margin_left", type: "int",  remarks: "左边距")   
            column(name: "margin_right", type: "int",  remarks: "右边距")   
            column(name: "wide_qty", type: "int",  remarks: "宽数量")   
            column(name: "high_qty", type: "int",  remarks: "高数量")   
            column(name: "high_space", type: "int",  remarks: "高间距")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"label_template_code,tenant_id",tableName:"hrpt_label_print",constraintName: "hrpt_label_print_u1")
    }
}