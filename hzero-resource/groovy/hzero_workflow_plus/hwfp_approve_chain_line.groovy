package script.db

databaseChangeLog(logicalFilePath: 'script/db/hwfp_approve_chain_line.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-03-10-hwfp_approve_chain_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hwfp_approve_chain_line_s', startValue:"1")
        }
        createTable(tableName:"hwfp_approve_chain_line"){
            column(name:"approve_chain_line_id",type:"bigint",autoIncrement:"true",startWith:"10001",remarks:"主键"){
                constraints(nullable:false,primaryKey: true,primaryKeyName: "hwfp_approve_chain_line_PK")
            }
            column(name:"approve_chain_id",type:"bigint",remarks:"审批链头ID"){
                constraints(nullable:false)
            }
            column(name:"name",type:"varchar(" + 60 * weight + ")",remarks:"name"){
                constraints(nullable:false)
            }
            column(name:"approve_order",type:"int",remarks:"审批顺序"){
                constraints(nullable:false)
            }
            column(name:"enabled_flag",type:"Tinyint",defaultValue:"1",remarks:"禁用启用标示"){
                constraints(nullable:false)
            }
            column(name:"tenant_id",type:"bigint",remarks:"租户ID"){
                constraints(nullable:false)
            }
            column(name:"approve_method",type:"bigint",remarks:"审批方式")
            column(name:"approve_method_value",type:"decimal(10,2)",remarks:"审批方式变量")
            column(name:"object_version_number",type:"bigint",defaultValue:"1",remarks:"版本号"){
                constraints(nullable:false)
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: false) }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: false) }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: false) }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: false) }
        }
        addUniqueConstraint(columnNames:"approve_chain_id,approve_chain_line_id",tableName:"hwfp_approve_chain_line",constraintName: "hwfp_approve_chain_line_u1")
    }
}