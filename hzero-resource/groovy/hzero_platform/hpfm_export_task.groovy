package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_export_task.groovy') {
    
    changeSet(author:"hzero@hand-china.com", id: "2019-08-05-hpfm_export_task"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_export_task_S', startValue:"10001")
        }
        createTable(tableName:"hpfm_export_task"){
            column(name:"task_id",type:"bigint",autoIncrement:"true",startWith:"10001",remarks:"表ID，主键，供其他表做外键"){
                constraints(nullable:"false",primaryKey: "true",primaryKeyName: "hpfm_export_task_PK")
            }
            column(name:"task_code",type:"varchar(" + 60*weight + ")",remarks:"uuid，对应一个导出任务"){
                constraints(nullable:"false")
            }
            column(name:"task_name",type:"varchar(" + 60*weight + ")",remarks:"任务名称，与导出文件名相同"){
                constraints(nullable:"false")
            }
            column(name:"download_url",type:"varchar(" + 240*weight + ")",remarks:"下载地址")
            column(name:"service_name",type:"varchar(" + 30*weight + ")",remarks:"服务名，由哪个服务发起的导出任务"){
                constraints(nullable:"false")
            }
            column(name:"host_name",type:"varchar(" + 120*weight + ")",remarks:"实例地址，由哪个实例发起的导出任务"){
                constraints(nullable:"false")
            }
            column(name:"tenant_id",type:"bigint",remarks:"租户ID"){
               constraints(nullable:false)
            }
            column(name:"user_id",type:"bigint",remarks:"用户ID")
            column(name:"error_info",type:"longtext",remarks:"任务异常信息")
            column(name:"state",type:"varchar(" + 30*weight + ")",defaultValue:"doing",remarks:"任务状态（done,doing,canceled）"){
                constraints(nullable:"false")
            }
            column(name:"end_date_time",type:"datetime",remarks:"任务结束时间")
            column(name:"object_version_number",type:"bigint",defaultValue:"1",remarks:"行版本号，用来处理锁"){
                constraints(nullable:"false")
            }
            column(name:"CREATED_BY",type:"bigint", defaultValue : "-1")
            column(name:"CREATION_DATE",type:"datetime", defaultValueComputed : "CURRENT_TIMESTAMP")
            column(name:"LAST_UPDATED_BY",type:"bigint", defaultValue : "-1")
            column(name:"LAST_UPDATE_DATE",type:"datetime", defaultValueComputed : "CURRENT_TIMESTAMP")
            column(name:"LAST_UPDATE_LOGIN",type:"bigint", defaultValue : "-1")
        }
        addUniqueConstraint(columnNames:"task_code",tableName:"hpfm_export_task",constraintName: "hpfm_export_task_U1")
    }


}