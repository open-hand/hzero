package script.db

databaseChangeLog(logicalFilePath: 'asgard_quartz.groovy') {
    changeSet(id: '2018-09-06-create-tables-asgard_quartz', author: 'jcalaz@163.com') {
        if(helper.isOracle()) {
//          plsql drop 分隔符是/，create statement的分隔符是; 所以分两个文件
            sqlFile(path: './quartz_oracle_drop.sql', relativeToChangelogFile: true, stripComments: true, endDelimiter: '/')
            sqlFile(path: './quartz_oracle_create.sql', relativeToChangelogFile: true, stripComments: true)
        } else if (helper.isSqlServer()){
            sqlFile(path: './quartz_mssql.sql', relativeToChangelogFile: true, stripComments: true)
        } else if (helper.isPostgresql()){
            sqlFile(path: './quartz_postgre.sql', relativeToChangelogFile: true, stripComments: true)
        } else {
            sqlFile(path: './quartz_mysql.sql', relativeToChangelogFile: true, stripComments: true)
        }
    }
}