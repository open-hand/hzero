package org.hzero.platform.domain.service.datasource.relationaldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.hzero.core.util.EncryptionUtils;
import org.hzero.platform.domain.entity.Datasource;
import org.hzero.platform.domain.service.datasource.AbstractConnection;
import org.hzero.platform.infra.annocations.DatasourceType;
import org.hzero.platform.infra.constant.Constants;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * 关系型数据库连接
 *
 * @author xiaoyu.zhao@hand-china.com
 */
@DatasourceType({Constants.Datasource.DB_MYSQL, Constants.Datasource.DB_TIDB, Constants.Datasource.DB_ORACLE,
        Constants.Datasource.DB_MSSQL})
@Component
public class RelationalDBConnection extends AbstractConnection<RelationalDB> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelationalDBConnection.class);

    @Override
    public void testConnection(Datasource datasource) {
        RelationalDB result = this.convertEntity(datasource, new RelationalDB());
        Connection con = null;
        try {
            Class.forName(result.getDriverClass());
            con = DriverManager.getConnection(result.getDatasourceUrl(), result.getUsername(),
                    result.getPasswordEncrypted());
            con.close();
        } catch (Exception e) {
            LOGGER.error("RelationalDB connection failed! Exception is :{}", e);
            throw new CommonException(HpfmMsgCodeConstants.ERROR_CONNECTION);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    LOGGER.info("RelationalDB close connection info, exception is :{}", e);
                }
            }
        }

    }

    @Override
    protected RelationalDB convertEntity(Datasource datasource, RelationalDB entity) {
        // 关系型数据库类型，校验该类型数据库下的必输参数
        this.validRelationalDBParam(datasource);
        BeanUtils.copyProperties(datasource, entity);
        return entity;
    }

    /**
     * 关系型数据库必输参数校验
     *
     * @param datasource 数据源
     */
    private void validRelationalDBParam(Datasource datasource) {
        // 校验连接池类型，url地址，驱动类，用户，密码必输
        String datasourceUrl = datasource.getDatasourceUrl();
        String driverClass = datasource.getDriverClass();
        String username = datasource.getUsername();
        String passwordEncrypted = datasource.getPasswordEncrypted();

        // 只要有一个参数为空或为""就报错，若密码不为null的话则将其加密后返回
        if (StringUtils.isEmpty(datasourceUrl) || StringUtils.isEmpty(driverClass) || StringUtils.isEmpty(username)
                        || StringUtils.isEmpty(passwordEncrypted)) {
            if (passwordEncrypted != null) {
                passwordEncrypted = EncryptionUtils.MD5.encrypt(passwordEncrypted);
            }
            throw new CommonException(HpfmMsgCodeConstants.ERROR_RELATIONAL_PARAM_ILLEGAL, datasourceUrl, driverClass,
                            username, passwordEncrypted);
        }
    }
}
