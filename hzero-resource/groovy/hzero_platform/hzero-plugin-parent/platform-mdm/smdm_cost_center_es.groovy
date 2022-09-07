package script.db
databaseChangeLog(logicalFilePath: 'script/db/smdm_cost_center_es.groovy') {

changeSet(author:"liangliang.jiang@hand-china.com", id: "2019-11-09-smdm_cost_center_es"){

    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    if(helper.dbType().isSupportSequence()){
        createSequence(sequenceName: 'smdm_cost_center_es_s', startValue:"1")
    }


    createTable(tableName:"smdm_cost_center_es"){
 column(name:"cost_es_id",type:"bigint",autoIncrement:"true",startWith:"1",remarks:"表ID，主键"){
   constraints(primaryKey: "true",primaryKeyName: "cost_es_id_pk")
}
     column(name:"external_system_code",type:"varchar(" + 50 * weight + "))",remarks:"描述")
     column(name:"cost_id",type:"BIGINT",remarks:"成本中心ID")
             {
                 constraints(nullable:"false")
             }
     column(name:"es_cost_id",type:"bigint",remarks:"外部成本中心ID")
     column(name:"es_cost_code",type:"varchar(" + 30 * weight + "))",remarks:"外部成本中心code")
     column(name:"data_version",type:'bigint',remarks:"数据版本")

 column(name:"OBJECT_VERSION_NUMBER",type:"BIGINT",defaultValue: "1",remarks:"行版本号，用来处理锁")
 {
   constraints(nullable:"false")
}
 column(name:"CREATED_BY",type:"BIGINT", defaultValue: "-1")
 {
   constraints(nullable:"false")
}
 column(name:"CREATION_DATE",type:"DATETIME", defaultValueComputed : "CURRENT_TIMESTAMP")
 {
   constraints(nullable:"false")
}
 column(name:"LAST_UPDATED_BY",type:"BIGINT", defaultValue: "-1")
 {
   constraints(nullable:"false")
}
 column(name:"LAST_UPDATE_DATE",type:"DATETIME", defaultValueComputed : "CURRENT_TIMESTAMP")
 {
   constraints(nullable:"false")
}
}
  }
}

