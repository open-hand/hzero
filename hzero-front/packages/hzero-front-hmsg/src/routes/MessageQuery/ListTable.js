import React, { PureComponent } from 'react';
import { Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { createPagination } from 'utils/utils';
import intl from 'utils/intl';
import { statusRender, dateTimeRender, operatorRender } from 'utils/renderer';

/**
 * 消息查询数据展示列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onChange - 分页查询
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} dataSource - Table数据源
 * @reactProps {Object} pagination - 分页器
 * @reactProps {Number} pagination.current - 当前页码
 * @reactProps {Number} pagination.pageSize - 分页大小
 * @reactProps {Number} pagination.total - 数据总量
 * @return React.element
 */

export default class ListTable extends PureComponent {
  // 点击收件人查看模态框
  @Bind()
  showRecipientModal(record) {
    const { onOpenRecipientModal } = this.props;
    onOpenRecipientModal(record);
  }

  // 点击内容查看模态框
  @Bind()
  showContentModal(record) {
    const { onOpenContentModal } = this.props;
    onOpenContentModal(record);
  }

  // 点击错误查看模态框
  @Bind()
  showErrorModal(record) {
    const { onOpenErrorModal } = this.props;
    onOpenErrorModal(record);
  }

  @Bind()
  handleResend(record) {
    const { onResendMessage } = this.props;
    onResendMessage(record);
  }

  @Bind()
  handleStandardTableChange(pagination) {
    const { formValues, onQueryMessage } = this.props;
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      ...formValues,
    };
    onQueryMessage(params);
  }

  @Bind()
  handleSelectChange(selectedRowKeys, selectedRows) {
    const { onSelectChange = (e) => e } = this.props;
    onSelectChange(selectedRows, selectedRowKeys);
  }

  render() {
    const { messageData = {}, loading, tenantRoleLevel, path, resendLoading } = this.props;
    const rowSelection = {
      onChange: this.handleSelectChange,
    };
    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 120,
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.serverCode').d('账号代码'),
        dataIndex: 'serverCode',
        width: 120,
      },
      {
        title: intl.get('hmsg.common.view.type').d('类型'),
        dataIndex: 'messageTypeMeaning',
        width: 100,
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.subject').d('主题'),
        dataIndex: 'subject',
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.sendDate').d('发送时间'),
        dataIndex: 'sendDate',
        width: 150,
        render: dateTimeRender,
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'trxStatusCode',
        width: 100,
        render: (val, record) => <span>{statusRender(val, record.trxStatusMeaning)}</span>,
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.content').d('内容'),
        width: 80,
        key: 'content',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'view1',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.viewContent`,
                    type: 'button',
                    meaning: '消息查询-查看内容',
                  },
                ]}
                onClick={() => {
                  this.showContentModal(record);
                }}
              >
                {intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看'),
          });
          return operatorRender(operators);
        },
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.receiver').d('接收人'),
        width: 80,
        key: 'receiver',
        render: (text, record) => {
          const operators = [];
          operators.push({
            key: 'view2',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.viewRecipient`,
                    type: 'button',
                    meaning: '消息查询-查看接收者',
                  },
                ]}
                onClick={() => {
                  this.showRecipientModal(record);
                }}
              >
                {intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看'),
          });
          return operatorRender(operators);
        },
      },
      {
        title: intl.get('hmsg.messageQuery.model.messageQuery.error').d('错误'),
        width: 80,
        key: 'error',
        render: (text, record) => {
          const operators = [];
          if (record.trxStatusCode === 'F') {
            operators.push({
              key: 'view3',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.viewError`,
                      type: 'button',
                      meaning: '消息查询-查看错误',
                    },
                  ]}
                  onClick={() => {
                    this.showErrorModal(record);
                  }}
                >
                  {intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hmsg.messageQuery.view.messageQuery.view').d('查看'),
            });
            return operatorRender(operators);
          }
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        fixed: 'right',
        key: 'reset',
        render: (val, record) => {
          const operators = [];
          if (record.trxStatusCode !== 'P') {
            operators.push({
              key: 'resend',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.resend`,
                      type: 'button',
                      meaning: '消息查询-重试',
                    },
                  ]}
                  onClick={() => {
                    this.handleResend(record);
                  }}
                >
                  {intl.get('hmsg.messageQuery.view.button.resend').d('重试')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hmsg.messageQuery.view.button.resend').d('重试'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter((col) => (tenantRoleLevel ? col.dataIndex !== 'tenantName' : true));
    return (
      <Table
        bordered
        rowSelection={rowSelection}
        columns={columns}
        rowKey="transactionId"
        dataSource={messageData.content || []}
        pagination={createPagination(messageData)}
        loading={loading || resendLoading}
        onChange={this.handleStandardTableChange}
      />
    );
  }
}
