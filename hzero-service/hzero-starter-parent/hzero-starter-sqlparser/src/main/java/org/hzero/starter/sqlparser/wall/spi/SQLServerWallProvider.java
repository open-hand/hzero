/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hzero.starter.sqlparser.wall.spi;

import org.hzero.starter.sqlparser.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import org.hzero.starter.sqlparser.sql.dialect.sqlserver.visitor.MSSQLServerExportParameterVisitor;
import org.hzero.starter.sqlparser.sql.parser.SQLParserFeature;
import org.hzero.starter.sqlparser.sql.parser.SQLStatementParser;
import org.hzero.starter.sqlparser.sql.visitor.ExportParameterVisitor;
import org.hzero.starter.sqlparser.util.JdbcConstants;
import org.hzero.starter.sqlparser.wall.WallConfig;
import org.hzero.starter.sqlparser.wall.WallProvider;
import org.hzero.starter.sqlparser.wall.WallVisitor;

/**
 * SQLServerProvider
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-17
 */
public class SQLServerWallProvider extends WallProvider {

    public final static String DEFAULT_CONFIG_DIR = "META-INF/druid/wall/sqlserver";

    public SQLServerWallProvider(){
        this(new WallConfig(DEFAULT_CONFIG_DIR));
    }

    public SQLServerWallProvider(WallConfig config){
        super(config, JdbcConstants.SQL_SERVER);
    }

    @Override
    public SQLStatementParser createParser(String sql) {
        return new SQLServerStatementParser(sql, SQLParserFeature.EnableSQLBinaryOpExprGroup);
    }

    @Override
    public WallVisitor createWallVisitor() {
        return new SQLServerWallVisitor(this);
    }

    @Override
    public ExportParameterVisitor createExportParameterVisitor() {
        return new MSSQLServerExportParameterVisitor();
    }
}
