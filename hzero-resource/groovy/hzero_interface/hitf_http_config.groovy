package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_http_config.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-11-04-hitf_http_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_http_config_s', startValue:"1")
        }
        createTable(tableName: "hitf_http_config", remarks: "HTTP参数配置") {
            column(name: "http_config_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "source_code", type: "varchar(" + 30 * weight + ")",  remarks: "来源代码，SERVICE/INTERFACE")  {constraints(nullable:"false")}
            column(name: "source_id", type: "varchar(" + 128 * weight + ")",  remarks: "来源ID")  {constraints(nullable:"false")}
            column(name: "property_code", type: "varchar(" + 128 * weight + ")",  remarks: "属性代码，代码：HITF.HTTP_CONFIG_PROPERTY")  {constraints(nullable:"false")}
            column(name: "property_value", type: "varchar(" + 128 * weight + ")",  remarks: "属性值")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"source_code,source_id,property_code",tableName:"hitf_http_config",constraintName: "hitf_http_config_u1")
    }

    // 修复租户越权问题
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-06-11-hitf_http_config") {
        // 添加租户ID
        addColumn(tableName: 'hitf_http_config') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
        }

        // 删除重建唯一性索引，添加租户ID
        dropUniqueConstraint(uniqueColumns:"source_code,source_id,property_code",tableName:"hitf_http_config",constraintName: "hitf_http_config_u1")
        addUniqueConstraint(columnNames:"source_code,source_id,property_code,tenant_id",tableName:"hitf_http_config",constraintName: "hitf_http_config_u1")
    }
}
