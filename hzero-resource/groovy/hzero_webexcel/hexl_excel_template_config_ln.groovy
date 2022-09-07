// demo.groovy
package script.db

databaseChangeLog(logicalFilePath: 'hexl_excel_template_config_ln.groovy') {
    changeSet(id: '2019-09-19-webexcel-hexl_excel_template_config_ln', author: 'yang.yang07@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName:'hexl_excel_tplt_config_ln_s', startValue:"1")
        }
        createTable(tableName:"hexl_excel_tplt_config_ln"){
            column(name:"config_ln_id",type:"bigint",autoIncrement:true,remarks:"主键"){
                constraints(primaryKey: true)
            }
            column(name:"config_hd_id",type:"bigint",remarks:"模板头ID"){
                constraints(nullable:"false")
            }
            column(name:"excel_code",type:"varchar("+ 30 * weight +")",remarks:"字段值"){
                constraints(nullable:"false")
            }
            column(name:"column_name",type:"varchar("+ 255 * weight +")",remarks:"字段名"){
                constraints(nullable:"false")
            }
            column(name:"column_type",type:"varchar("+ 30 * weight +")",remarks:"字段类型"){
                constraints(nullable:"false")
            }
            column(name:"precisions",type:"int",remarks:"精度")
            column(name:"hide_column_flag",type:"varchar("+ 1 * weight +")",remarks:"是否隐藏列,Y为隐藏，N为不隐藏")
            column(name:"hide_row_flag",type:"varchar("+ 1 * weight +")",remarks:"是否隐藏行，Y为隐藏，N为不隐藏")
            column(name:"prompt",type:"varchar("+ 255 * weight +")",remarks:"字段描述")
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
    changeSet(id: '2019-12-05-webexcel-hexl_excel_template_config_ln', author: 'tianyang.huang@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: "hexl_excel_tplt_config_ln", columnName: "excel_code", columnDataType: "varchar(" + 30 * weight + ")")
    }
}