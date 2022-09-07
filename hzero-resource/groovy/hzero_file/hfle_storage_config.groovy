package script.db

databaseChangeLog(logicalFilePath: 'script/db/hfle_storage_config.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hfle_storage_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hfle_storage_config_s', startValue:"1")
        }
        createTable(tableName: "hfle_storage_config", remarks: "") {
            column(name: "storage_config_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "storage_type", type: "tinyint",  remarks: "类型 1:阿里 2:华为 3:Minio 4:腾讯 5:七牛")  {constraints(nullable:"false")}
            column(name: "domain", type: "varchar(" + 120 * weight + ")",  remarks: "绑定的域名")
            column(name: "end_point", type: "varchar(" + 120 * weight + ")",  remarks: "EndPoint")
            column(name: "access_key_id", type: "varchar(" + 120 * weight + ")",  remarks: "AccessKeyId")
            column(name: "access_key_secret", type: "varchar(" + 120 * weight + ")",  remarks: "AccessKeySecret")
            column(name: "app_id", type: "bigint",  remarks: "腾讯云 AppId")
            column(name: "region", type: "varchar(" + 120 * weight + ")",  remarks: "腾讯云 所在区域")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "默认标识")  {constraints(nullable:"false")}
            column(name: "access_control", type: "varchar(" + 120 * weight + ")",  remarks: "bucket权限控制")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"storage_type,tenant_id",tableName:"hfle_storage_config",constraintName: "hfle_storage_config_u1")
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-17-hfle_storage_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: "hfle_storage_config"){
            column(name: "bucket_prefix", type: "varchar(" + 60 * weight + ")", remarks: "Bucket前缀")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-05-24-hfle_storage_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropUniqueConstraint(tableName: 'hfle_storage_config', constraintName: 'hfle_storage_config_u1')
        addColumn(tableName: 'hfle_storage_config') {
                column(name: "storage_code", type: "varchar(" + 30 * weight + ")", remarks: "存储编码")
        }
        addUniqueConstraint(tableName: 'hfle_storage_config', columnNames: 'tenant_id,storage_type,storage_code', constraintName: 'hfle_storage_config_u1')
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-05-29-hfle_storage_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hfle_storage_config') {
            column(name: "prefix_strategy", type: "varchar(" + 30 * weight + ")", remarks: "文件名前缀策略")
        }
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-06-26-hfle_storage_config") {
        dropUniqueConstraint(tableName: 'hfle_storage_config', constraintName: 'hfle_storage_config_u1')
        addUniqueConstraint(tableName: 'hfle_storage_config', columnNames: 'tenant_id,storage_code', constraintName: 'hfle_storage_config_u1')
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-10-30-hfle_storage_config") {
        addColumn(tableName: 'hfle_storage_config') {
            column(name: "create_bucket_flag", type: "tinyint",   defaultValue:"1",   remarks: "是否自动创建Bucket")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hfle_storage_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hfle_storage_config", columnName: 'access_key_secret', newDataType: "varchar(" + 480 * weight + ")")
    }
}