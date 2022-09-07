/**
 * PersonalLoginRecord - 个人（登录记录）
 * @date: 2019-01-10
 * @author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @version: 0.0.1
 * @copyright: Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { getCurrentOrganizationId } from 'utils/utils';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Table } from 'hzero-ui';
import { isEmpty } from 'lodash';

import formatterCollections from 'utils/intl/formatterCollections';

const prefix = 'hpfm.login.audit.model';

@connect(({ personalLoginRecord, loading }) => ({
  personalLoginRecord,
  loading: loading.effects['personalLoginRecord/fetchRecords'],
  tenantId: getCurrentOrganizationId(),
}))
@formatterCollections({ code: 'hpfm.login' })
export default class PersonalLoginRecord extends PureComponent {
  componentDidMount() {
    const { dispatch, tenantId } = this.props;
    const type = 'personalLoginRecord/fetchRecords';
    dispatch({ type, payload: { tenantId } });
  }

  /**
   * 处理表格翻页
   * @param {*} page
   * @memberof PersonalLoginRecord
   */
  @Bind()
  onChange(page) {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'personalLoginRecord/fetchRecords',
      payload: {
        tenantId,
        page: isEmpty(page) ? {} : page,
      },
    });
  }

  render() {
    const {
      loading,
      personalLoginRecord: { dataSource = [], pagination = {} },
    } = this.props;
    const columns = [
      {
        title: intl.get(`${prefix}.login.auditTypeMeaning`).d('审计类型'),
        width: 150,
        dataIndex: 'auditTypeMeaning',
      },
      {
        title: intl.get(`${prefix}.login.time`).d('登录时间'),
        width: 200,
        dataIndex: 'loginDate',
      },
      {
        title: intl.get(`${prefix}.login.address`).d('登录地址'),
        width: 200,
        dataIndex: 'loginIp',
      },
      {
        title: intl.get(`${prefix}.login.device`).d('登录设备'),
        dataIndex: 'loginDevice',
      },
    ];
    return (
      <>
        <Header
          title={intl.get('hpfm.userInfo.view.login.log').d('登录日志')}
          backPath="/hiam/user/info"
        />
        <Content>
          <Table
            bordered
            rowKey="order"
            loading={loading}
            columns={columns}
            dataSource={dataSource.map((item, index) => ({ ...item, order: index + 1 }))}
            pagination={pagination}
            onChange={page => this.onChange(page)}
          />
        </Content>
      </>
    );
  }
}
