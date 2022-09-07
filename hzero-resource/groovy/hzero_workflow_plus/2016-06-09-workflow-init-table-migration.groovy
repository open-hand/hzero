package script.db

databaseChangeLog(logicalFilePath: "2016-06-09-init-table-migration.groovy") {

    changeSet(author: "jessen", id: "20160926-activiti.create.engine-1") {
        if (helper.isSqlServer()) {
            sqlFile(path: "/data/mssql/tables/activiti.create.engine.sql", relativeToChangelogFile: "true")
        } else if (helper.isOracle()) {
            sqlFile(path: "/data/oracle/tables/activiti.create.engine.sql", relativeToChangelogFile: "true")
        } else if (helper.isPostgresql()) {
            sqlFile(path: "/data/postgre/tables/activiti.create.engine.sql", relativeToChangelogFile: "true")
        } else {
            sqlFile(path: "/data/mysql/tables/activiti.create.engine.sql", relativeToChangelogFile: "true")
        }
    }

    changeSet(author: "jessen", id: "20160926-activiti.create.history-1") {
        if (helper.isSqlServer()) {
            sqlFile(path: "/data/mssql/tables/activiti.create.history.sql", relativeToChangelogFile: "true")
        } else if (helper.isOracle()) {
            sqlFile(path: "/data/oracle/tables/activiti.create.history.sql", relativeToChangelogFile: "true")
        } else if (helper.isPostgresql()) {
            sqlFile(path: "/data/postgre/tables/activiti.create.history.sql", relativeToChangelogFile: "true")
        } else {
            sqlFile(path: "/data/mysql/tables/activiti.create.history.sql", relativeToChangelogFile: "true")
        }
    }

    changeSet(author: "jessen", id: "20160926-activiti.create.identity-1") {
        if (helper.isSqlServer()) {
            sqlFile(path: "/data/mssql/tables/activiti.create.identity.sql", relativeToChangelogFile: "true")
        } else if (helper.isOracle()) {
            sqlFile(path: "/data/oracle/tables/activiti.create.identity.sql", relativeToChangelogFile: "true")
        } else if (helper.isPostgresql()) {
            sqlFile(path: "/data/postgre/tables/activiti.create.identity.sql", relativeToChangelogFile: "true")
        } else {
            sqlFile(path: "/data/mysql/tables/activiti.create.identity.sql", relativeToChangelogFile: "true")
        }
    }

}
