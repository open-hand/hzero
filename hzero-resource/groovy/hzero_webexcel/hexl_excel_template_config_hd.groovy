// demo.groovy
package script.db

databaseChangeLog(logicalFilePath: 'hexl_excel_template_config_hd.groovy') {
    changeSet(id: '2019-09-19-webexcel-hexl_excel_tplt_config_hd', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName:'hexl_excel_tplt_config_hd_s', startValue:"1")
        }
        createTable(tableName:"hexl_excel_tplt_config_hd"){
            column(name:"config_hd_id",type:"bigint",autoIncrement:true,remarks:"主键"){
                constraints(primaryKey: true)
            }
            column(name:"template_id",type:"bigint",remarks:"模板ID"){
                constraints(nullable:"false")
            }
            column(name:"table_name",type:"varchar("+ 32 * weight +")",remarks:"表名")
            column(name:"table_type",type:"varchar("+ 120 * weight +")",remarks:"表类型")
            column(name:"multi_line_from",type:"int")
            column(name:"multi_line_to",type:"int")
            column(name:"service_code",type:"varchar("+ 255 * weight +")",remarks:"服务代码")
            column(name:"callback_url",type:"varchar("+ 480 * weight +")",remarks:"回调url")
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
    changeSet(id: '2019-12-05-webexcel-hexl_excel_tplt_config_hd', author: 'tianyang.huang@hand-china.com'){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hexl_excel_tplt_config_hd') {
            column(name: "sheet_name", type: "varchar(" + 30 * weight + ")", remarks: "sheet名称")
        }
        addColumn(tableName: 'hexl_excel_tplt_config_hd') {
            column(name: "sheet_code", type: "varchar(" + 30 * weight + ")", remarks: "sheet代码")
        }
        addUniqueConstraint(columnNames:"sheet_code,table_type,template_id",tableName:"hexl_excel_tplt_config_hd",constraintName: "HEXL_EXCEL_TPLT_CONFIG_HD_U1")

    }
}