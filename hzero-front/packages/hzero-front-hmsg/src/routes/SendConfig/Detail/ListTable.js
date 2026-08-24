import React, { PureComponent } from 'react';
import { Popconfirm, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { operatorRender, valueMapMeaning, enableRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import { Bind } from 'lodash-decorators';

/**
 * 发送配置数据展示列表
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
  /**
   * 编辑
   * @param {object} record - 数据对象
   */
  editOption(record) {
    this.props.onEdit(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  deleteOption(record) {
    this.props.onDelete(record);
  }

  /**
   * 查看模板内容
   * @param {object} record
   */
  @Bind()
  viewTemplate(record) {
    const { onOpen = (e) => e } = this.props;
    onOpen(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, dataSource = [], messageType, path } = this.props;
    const columns = [
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.templateCode').d('模板代码'),
        dataIndex: 'templateCode',
        width: 220,
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.templateName').d('模板名称'),
        dataIndex: 'templateName',
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.typeCode').d('消息类型'),
        dataIndex: 'typeCode',
        width: 120,
        render: (val) => val && valueMapMeaning(messageType, val),
      },
      {
        title: intl.get('hmsg.common.view.accountCode').d('账户代码'),
        dataIndex: 'serverCode',
        width: 200,
      },
      {
        title: intl.get('hmsg.common.view.accountName').d('账户名称'),
        dataIndex: 'serverName',
        width: 120,
      },
      {
        title: intl.get('hmsg.sendConfig.model.sendConfig.tryTimes').d('重试次数'),
        dataIndex: 'tryTimes',
        width: 120,
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        render: (val) => enableRender(val),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'option',
        width: 180,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.editTemplate`,
                      type: 'button',
                      meaning: '消息发送配置-编辑模板',
                    },
                  ]}
                  onClick={() => this.editOption(record)}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'views',
              ele: (
                <a onClick={() => this.viewTemplate(record)}>
                  {intl.get('hmsg.sendConfig.view.message.viewTemplate').d('查看模板')}
                </a>
              ),
              len: 4,
              title: intl.get('hmsg.sendConfig.view.message.viewTemplate').d('查看模板'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  placement="topRight"
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => this.deleteOption(record)}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.deleteTemplate`,
                        type: 'button',
                        meaning: '消息发送配置-删除模板',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Table
          bordered
          rowKey="tempServerLineId"
          loading={loading}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          dataSource={dataSource}
          pagination={false}
        />
      </>
    );
  }
}
