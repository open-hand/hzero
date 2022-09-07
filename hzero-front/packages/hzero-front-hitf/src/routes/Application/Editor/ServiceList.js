/**
 * Table - 菜单配置 - 列表页面表格
 * @date: 2018-7-4
 * @author: lijun <jun.li06@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Modal, Table } from 'hzero-ui';
import { isEmpty, isNumber, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { enableRender } from 'utils/renderer';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import notification from 'utils/notification';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';

export default class ServiceList extends PureComponent {
  defaultTableRowKey = 'interfaceServerId';

  @Bind()
  handleDeleteService() {
    const {
      selectedRowKeys,
      dataSource,
      onRowSelectionChange,
      deleteLines,
      fetchInformation,
      // onChangeState,
    } = this.props;
    if (selectedRowKeys.length > 0) {
      Modal.confirm({
        title: intl.get('hzero.common.message.confirm.title').d('提示'),
        content: intl
          .get('hitf.application.model.application.title.deleteContent')
          .d('未保存的数据将会丢失,确定删除吗?'),
        onOk() {
          const ids = [];
          const newDataSource = [];
          dataSource.forEach((item) => {
            if (
              !isUndefined(item.assignId) &&
              selectedRowKeys.indexOf(item.interfaceServerId) >= 0
            ) {
              ids.push(item.assignId);
            }
            if (selectedRowKeys.indexOf(item.interfaceServerId) < 0) {
              newDataSource.push(item);
            }
          });
          if (ids.length > 0) {
            deleteLines(ids).then((res) => {
              if (res) {
                onRowSelectionChange([], []);
                notification.success();
                fetchInformation();
                // onChangeState('interfaceListDataSource', newDataSource);
              }
            });
          } else {
            onRowSelectionChange([], []);
            notification.success();
            fetchInformation();
            // onChangeState('interfaceListDataSource', newDataSource);
          }
        },
      });
    }
  }

  render() {
    const {
      dataSource = [],
      pagination,
      selectedRowKeys = [],
      loading,
      onChange = (e) => e,
      onRowSelectionChange = (e) => e,
      formProps,
      formDataSource,
      applicationId,
      serviceListSelectedRows,
      addService = (e) => e,
    } = this.props;
    const { _token = '' } = formDataSource;
    const tableColumns = [
      {
        title: intl.get('hitf.application.model.application.serviceCode').d('服务代码'),
        dataIndex: 'serverCode',
      },
      {
        title: intl.get('hitf.application.model.application.serviceName').d('服务名称'),
        dataIndex: 'serverName',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: enableRender,
        width: 85,
      },
    ];
    const tableProps = {
      dataSource,
      pagination,
      loading,
      onChange,
      bordered: true,
      rowKey: this.defaultTableRowKey,
      columns: tableColumns,
      scroll: { x: tableScrollWidth(tableColumns) },
      rowSelection: {
        selectedRowKeys,
        onChange: onRowSelectionChange,
      },
    };
    return (
      <>
        <div className="action" style={{ textAlign: 'right' }}>
          <Lov
            isButton
            disabled={!_token}
            code="HITF.APPLICATION.SERVER_UNUSE"
            onChange={addService}
            queryParams={{
              organizationId: isTenantRoleLevel()
                ? formProps.dataSource.tenantId
                : formDataSource.tenantId,
              applicationId: isNumber(applicationId) ? applicationId : -1,
              // appTenantId: formDataSource.tenantId,
            }}
          >
            {intl.get('hitf.application.view.button.bind').d('绑定')}
          </Lov>
          <Button
            disabled={isEmpty(serviceListSelectedRows)}
            // onClick={addService}
            onClick={this.handleDeleteService}
            style={{ marginLeft: 8 }}
          >
            {intl.get('hitf.application.view.button.untie').d('解绑')}
          </Button>
        </div>
        <br />
        <Table {...tableProps} />
      </>
    );
  }
}
