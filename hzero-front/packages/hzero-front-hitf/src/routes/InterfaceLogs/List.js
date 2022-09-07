/*
 * List - 接口监控列表
 * @date: 2018/09/17 15:40:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Table, Spin } from 'hzero-ui';
import queryString from 'query-string';
// import { getDateFormat } from 'utils/utils';
import { dateTimeRender, TagRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

const tenantRoleLevel = isTenantRoleLevel();

export default class List extends PureComponent {
  render() {
    const {
      dataSource,
      history,
      pagination,
      loading,
      searchPaging,
      loadingMap,
      location: { search, pathname },
      onRetry,
    } = this.props;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const columns = [
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.tenant').d('所属租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.serverCode').d('服务代码'),
        dataIndex: 'serverCode',
        width: 180,
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.serverName').d('服务名称'),
        dataIndex: 'serverName',
        width: 180,
      },
      {
        title: intl
          .get('hitf.interfaceLogs.modal.interfaceLogs.interfaceServerVersion')
          .d('服务版本'),
        dataIndex: 'formatInterfaceServerVersion',
        width: 120,
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.interfaceCode').d('接口代码'),
        dataIndex: 'interfaceCode',
        width: 180,
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.interfaceName').d('接口名称'),
        dataIndex: 'interfaceName',
        width: 180,
      },
      {
        title: intl.get('hitf.interfaceLogs.modal.interfaceLogs.interfaceVersion').d('接口版本'),
        dataIndex: 'formatInterfaceVersion',
        width: 120,
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.clientId').d('客户端ID'),
        dataIndex: 'clientId',
        width: 150,
      },
      {
        title: intl
          .get('hitf.interfaceLogs.model.interfaceLogs.external.interfaceUrl')
          .d('第三方接口地址'),
        dataIndex: 'interfaceUrl',
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.invokeType').d('接口调用类型'),
        dataIndex: 'invokeTypeMeaning',
        width: 120,
        align: 'center',
      },
      {
        title: intl.get('hitf.interfaceLogs.model.interfaceLogs.invokeKey').d('请求ID'),
        dataIndex: 'invokeKey',
        width: 280,
      },
      {
        title: intl
          .get('hitf.interfaceLogs.model.interfaceLogs.internal.requestTime')
          .d('平台接口请求时间'),
        width: 160,
        dataIndex: 'requestTime',
        render: dateTimeRender,
      },
      {
        title: intl
          .get('hitf.interfaceLogs.model.interfaceLogs.internal.asyncFlag')
          .d('是否异步调用'),
        dataIndex: 'asyncFlag',
        width: 130,
        align: 'center',
        render: (val) => (val === 0 ? '否' : '是'),
      },
      {
        title: intl
          .get('hitf.interfaceLogs.model.interfaceLogs.internal.respStatus')
          .d('平台接口响应状态'),
        dataIndex: 'responseStatus',
        width: 130,
        align: 'center',
        render: (val) => {
          const statusLists = [
            {
              status: 'success',
              color: 'green',
              text: intl.get('hitf.interfaceLogs.model.interfaceLogs.success').d('成功'),
            },
            {
              status: 'fail',
              color: 'red',
              text: intl.get('hitf.interfaceLogs.model.interfaceLogs.failure').d('失败'),
            },
          ];
          return TagRender(val, statusLists);
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        render: (val, record) => {
          const operators = [
            {
              key: 'detail',
              ele: (
                <a
                  onClick={() => {
                    history.push(
                      pathname.indexOf('/private') === 0
                        ? `/private/hitf/interface-logs/detail/${record.interfaceLogId}?access_token=${accessToken}`
                        : `/hitf/interface-logs/detail/${record.interfaceLogId}`
                    );
                  }}
                >
                  {intl.get('hzero.common.button.detail').d('详情')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.detail').d('详情'),
            },
            {
              key: 'retry',
              ele: (
                <a onClick={() => onRetry(record)}>
                  <Spin spinning={loadingMap[record.interfaceLogId] || false} size="small">
                    {intl.get('hitf.interfaceLogs.view.button.retry').d('重试')}
                  </Spin>
                </a>
              ),
              len: 2,
              title: intl.get('hitf.interfaceLogs.view.button.retry').d('重试'),
            },
          ];
          return operatorRender(operators, record);
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <>
        <Table
          bordered
          rowKey="interfaceLogId"
          loading={loading}
          dataSource={dataSource}
          pagination={pagination}
          onChange={searchPaging}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </>
    );
  }
}
