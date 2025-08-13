import React, { PureComponent } from 'react';
import { Table, Tag, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { tableScrollWidth, isTenantRoleLevel } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

/**
 * 短信配置数据展示列表
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
  // constructor(props) {
  //   super(props);
  //   this.state = {
  //     isCopy: false,
  //   };
  // }

  // 编辑
  @Bind()
  handleEdit(record) {
    const { onGetRecord } = this.props;
    onGetRecord(record);
  }

  @Bind()
  handleCopy(record) {
    const { onCopy } = this.props;
    onCopy(record);
  }

  @Bind()
  handleDelete(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  render() {
    const {
      smsData,
      loading,
      pagination,
      onChange,
      tenantRoleLevel,
      path,
      handleShowFilter,
    } = this.props;
    const columns = [
      !tenantRoleLevel && {
        title: intl.get('hmsg.smsConfig.model.smsConfig.tenant').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hmsg.common.view.accountCode').d('账户代码'),
        dataIndex: 'serverCode',
        width: 150,
      },
      {
        title: intl.get('hmsg.common.view.accountName').d('账户名称'),
        dataIndex: 'serverName',
        width: 150,
      },
      {
        title: intl.get('hmsg.smsConfig.model.smsConfig.serverTypeCode').d('服务类型'),
        dataIndex: 'serverTypeMeaning',
        width: 150,
      },
      {
        title: intl.get('hmsg.smsConfig.model.smsConfig.signName').d('短信签名'),
        dataIndex: 'signName',
      },
      tenantRoleLevel &&
        !VERSION_IS_OP && {
          title: intl.get('hmsg.common.view.source').d('来源'),
          width: 120,
          dataIndex: 'tenantId',
          render: (_, record) => {
            const { tenantId } = this.props;
            return tenantId.toString() === record.tenantId.toString() ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            );
          },
        },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 100,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        key: 'error',
        render: (val, record) => {
          const operators = [];
          const { tenantId } = this.props;
          if (tenantId.toString() === record.tenantId.toString() || !tenantRoleLevel) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '短信配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleEdit(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (
            tenantId.toString() !== record.tenantId.toString() &&
            tenantRoleLevel &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'copy',
              ele: (
                <a
                  onClick={() => {
                    this.handleCopy(record);
                  }}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          }
          if (
            tenantId.toString() === record.tenantId.toString() &&
            tenantRoleLevel &&
            !VERSION_IS_OP
          ) {
            operators.push({
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl
                    .get(`hmsg.smsConfig.view.message.title.confirmDelete`)
                    .d('确定删除该数据吗？')}
                  onConfirm={() => this.handleDelete(record)}
                  style={{ textAlign: 'center' }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            });
          }
          if (tenantId.toString() === record.tenantId.toString() || !isTenantRoleLevel()) {
            operators.push({
              key: 'filter',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.filter`,
                      type: 'button',
                      meaning: '手机账户-设置黑白名单',
                    },
                  ]}
                  onClick={() => handleShowFilter(record)}
                >
                  {intl.get('hmsg.smsConfig.view.title.filter').d('设置黑白名单')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hmsg.smsConfig.view.title.filter').d('设置黑白名单'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <>
        <Table
          bordered
          rowKey="serverId"
          dataSource={smsData.content || []}
          loading={loading}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={(page) => onChange(page)}
        />
      </>
    );
  }
}
