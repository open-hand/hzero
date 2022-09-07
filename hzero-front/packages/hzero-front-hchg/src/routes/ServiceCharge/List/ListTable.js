/**
 * ListTable - 服务计费配置-列表页
 * @date: 2019-8-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Table, Icon } from 'hzero-ui';
import { tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Boolean} isTenant - 是否为租户级
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Array} pagination - Table分页
 * @reactProps {Boolean} handleLoading - 发布中/取消中加载标志
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Function} onRedirectToEdit - 跳转详情页
 * @return React.element
 */

export default class ListTable extends Component {
  state = {
    currentChargeGroupId: null, // 当前处理行ID
  };

  /**
   * 处理错误
   * @param {object} record - 表格行数据
   */
  @Bind()
  handlePublish(record, flag) {
    const { onPublish = () => {}, onCancel = () => {}, handleLoading } = this.props;
    if (handleLoading) return;
    this.setState(
      {
        currentChargeGroupId: record.chargeGroupId,
      },
      () => {
        if (flag) {
          onPublish(record);
        } else {
          onCancel(record);
        }
      }
    );
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      isTenant,
      onChange,
      handleLoading,
      onRedirectToEdit = () => {},
    } = this.props;
    const { currentChargeGroupId } = this.state;
    const columns = [
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.groupCode').d('计费组代码'),
        dataIndex: 'groupCode',
        width: 200,
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称'),
        dataIndex: 'groupName',
        width: 200,
      },
      !isTenant && {
        title: intl.get('hzero.common.model.common.tenantId').d('租户'),
        width: 200,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.chargeService').d('服务代码'),
        dataIndex: 'chargeService',
        width: 300,
      },
      {
        title: intl.get('hchg.purchaseDetail.model.serviceCharge.serviceName').d('服务说明'),
        dataIndex: 'chargeServiceMeaning',
        width: 200,
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.billCycle').d('账单周期'),
        dataIndex: 'billCycleMeaning',
        width: 150,
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.remark').d('说明'),
        dataIndex: 'remark',
        width: 200,
      },
      {
        title: intl.get('hchg.serviceCharge.model.serviceCharge.status').d('状态'),
        dataIndex: 'statusMeaning',
        width: 150,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 160,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => (
          <span className="action-link">
            <a onClick={() => onRedirectToEdit(record.chargeGroupId)}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
            {/* eslint-disable-next-line no-nested-ternary */}
            {handleLoading && currentChargeGroupId === record.chargeGroupId ? (
              <a>
                <Icon type="loading" />
              </a>
            ) : record.status === 'PUBLISHED' ? (
              <a onClick={() => this.handlePublish(record, false)}>
                {intl.get('hzero.common.button.cancel').d('取消')}
              </a>
            ) : (
              <a onClick={() => this.handlePublish(record, true)}>
                {intl.get('hzero.common.button.release').d('发布')}
              </a>
            )}
          </span>
        ),
      },
    ].filter(Boolean);

    return (
      <Table
        bordered
        loading={loading}
        rowKey="chargeGroupId"
        columns={columns}
        dataSource={dataSource}
        pagination={pagination}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
