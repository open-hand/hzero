// demo.groovy
package script.db

databaseChangeLog(logicalFilePath: 'hexl_excel_template_config_bt.groovy') {
    changeSet(id: '2019-09-19-webexcel-hexl_excel_tplt_config_bt', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName:'hexl_excel_tplt_config_bt_s', startValue:"1")
        }
        createTable(tableName:"hexl_excel_tplt_config_bt"){
            column(name:"config_bt_id",type:"bigint",autoIncrement:true,remarks:"主键"){
                constraints(primaryKey: true)
            }
            column(name:"config_hd_id",type:"bigint",remarks:"模板头ID")
            column(name:"button_type",type:"varchar("+ 120 * weight +")",remarks:"按钮类型")
            column(name:"button_name",type:"varchar("+ 120 * weight +")",remarks:"按钮名称")
            column(name:"variable_excel_code",type:"varchar("+ 30 * weight +")")
            column(name:"target_excel_code",type:"varchar("+ 30 * weight +")")
            column(name:"target_excel_value",type:"decimal(20)")
            column(name:"guess_value",type:"decimal(20)",remarks:"估值")
            column(name:"enabled_flag",type:"tinyint",remarks:"是否启用")
            column(name:"tenant_id",type:"bigint",defaultValue:"0",remarks:"租户id"){
                constraints(nullable:"false")
            }
            column(name:"object_version_number",type:"bigint",defaultValue:"1",remarks:"行版本号，用来处理锁"){
                constraints(nullable:"false")
            }
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
    }
    changeSet(id: '2019-12-06-webexcel-hexl_excel_template_config_bt', author: 'tianyang.huang@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hexl_excel_tplt_config_bt') {
            column(name: "button_action", type: "varchar(" + 480 * weight + ")", remarks: "按钮动作")
        }
        addColumn(tableName: 'hexl_excel_tplt_config_bt') {
            column(name: "template_id", type: "varchar(" + 30 * weight + ")", remarks: "模板id")
        }
    }
}