package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_market_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-21-hadm_market_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hadm_market_config_s', startValue: "1")
        }
        createTable(tableName: "hadm_market_config", remarks: "应用市场配置") {
            column(name: "market_id", type: "bigint", autoIncrement: "true", remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: "true")
            }
            column(name: "client_id", type: "varchar(64)", remarks: "客户端唯一uuid")  {
                constraints(nullable: "false")
            }
            column(name: "company_name", type: "varchar(" + 60 * weight + ")",  remarks: "公司名称")
            column(name: "join_flag", type: "tinyint",  remarks: "是否同意加入改善计划")
            column(name: "icon_flag", type: "tinyint",  remarks: "是否展示市场图标")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
        }
    }
}
