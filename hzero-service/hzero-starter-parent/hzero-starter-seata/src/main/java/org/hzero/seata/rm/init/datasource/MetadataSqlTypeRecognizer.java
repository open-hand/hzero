package org.hzero.seata.rm.init.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @author XCXCXCXCX
 * @date 2020/4/17 5:23 下午
 */
public class MetadataSqlTypeRecognizer implements SqlTypeRecognizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataSqlTypeRecognizer.class);

    @Override
    public String recognizeSqlType(DataSource dataSource) {
        try {
            DatabaseMetaData metadata = dataSource.getConnection().getMetaData();
            return metadata.getDatabaseProductName();
        } catch (SQLException e) {
            LOGGER.error("recognize sql type failed", e);
        }
        return "unknown";
    }
}
