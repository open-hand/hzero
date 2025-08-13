package io.seata.rm.datasource.undo.sqlserver;

import io.seata.common.loader.LoadLevel;
import io.seata.rm.datasource.undo.AbstractUndoExecutor;
import io.seata.rm.datasource.undo.SQLUndoLog;
import io.seata.rm.datasource.undo.UndoExecutorHolder;
import io.seata.sqlparser.util.JdbcConstants;

/**
 * @author XCXCXCXCX
 * @version 1.3.0
 * @date 2020/3/9 2:39 下午
 */
@LoadLevel(name = JdbcConstants.SQLSERVER)
public class SqlserverUndoExecutorHolder implements UndoExecutorHolder {

    @Override
    public AbstractUndoExecutor getInsertExecutor(SQLUndoLog sqlUndoLog) {
        return new SqlserverUndoInsertExecutor(sqlUndoLog);
    }

    @Override
    public AbstractUndoExecutor getUpdateExecutor(SQLUndoLog sqlUndoLog) {
        return new SqlserverUndoUpdateExecutor(sqlUndoLog);
    }

    @Override
    public AbstractUndoExecutor getDeleteExecutor(SQLUndoLog sqlUndoLog) {
        return new SqlserverUndoDeleteExecutor(sqlUndoLog);
    }
}
