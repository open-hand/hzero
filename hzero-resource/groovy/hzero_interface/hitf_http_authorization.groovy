package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_http_authorization.groovy') {
    changeSet(author:"mingke.yan@hand-china.com", id: "2020-07-31_hitf_http_authorization"){
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        }
        else if (helper.isOracle()){
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_http_authorization_S', startValue:"10001")
        }
        createTable(tableName:"hitf_http_authorization", remarks: "接口认证信息"){
            column(name:"auth_id",type:"bigint",autoIncrement:"true",startWith:"10001",remarks:"表ID，主键"){
                constraints(nullable:"false",primaryKey: "true",primaryKeyName: "hitf_http_authorization_PK")
            }
            column(name:"auth_type",type:"varchar("+ 30 * weight +")",defaultValue:"NONE",remarks:"认证模式，代码：HITF.AUTH_TYPE"){
                constraints(nullable:"false")
            }
            column(name:"auth_json",type:"clob",remarks:"认证信息")
            column(name:"tenant_id",type:"bigint",defaultValue:"0",remarks:"租户ID"){
                constraints(nullable:"false")
            }
            column(name:"object_version_number",type:"bigint",defaultValue:"1",remarks:"行版本号，用来处理锁"){
                constraints(nullable:"false")
            }
            column(name:"created_by",type:"bigint", defaultValue : "-1")
            column(name:"creation_date",type:"datetime", defaultValueComputed : "CURRENT_TIMESTAMP")
            column(name:"last_updated_by",type:"bigint", defaultValue : "-1")
            column(name:"last_update_date",type:"datetime", defaultValueComputed : "CURRENT_TIMESTAMP")
        }
    }
}
