import React, { PureComponent } from 'react';
import { Table, Tag, Popconfirm } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import { tableScrollWidth, getCurrentOrganizationId } from 'utils/utils';
import { enableRender, valueMapMeaning, operatorRender } from 'utils/renderer';
import { Bind } from 'lodash-decorators';

/**
 * 消息模板数据展示列表
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
   * render
   * @returns React.element
   */

  @Bind()
  handleUpdate(record) {
    const { onUpdate } = this.props;
    onUpdate(record);
  }

  @Bind()
  handleCopy(record) {
    const { onCopy } = this.props;
    onCopy(record);
  }

  @Bind()
  handleDel(record) {
    const { onDelete } = this.props;
    onDelete(record);
  }

  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      language,
      tenantRoleLevel,
      path,
    } = this.props;
    const columns = [
      !tenantRoleLevel && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 200,
      },
      {
        title: intl.get('hmsg.messageTemplate.model.template.templateCode').d('消息模板代码'),
        dataIndex: 'templateCode',
        width: 270,
      },
      {
        title: intl.get('hmsg.messageTemplate.model.template.templateName').d('消息模板名称'),
        dataIndex: 'templateName',
      },
      {
        title: intl.get('entity.lang.tag').d('语言'),
        dataIndex: 'lang',
        width: 150,
        render: (val) => val && valueMapMeaning(language, val),
      },
      tenantRoleLevel &&
        !VERSION_IS_OP && {
          title: intl.get('hmsg.common.view.source').d('来源'),
          width: 120,
          dataIndex: 'tenantId',
          render: (_, record) => {
            const tenantId = getCurrentOrganizationId();
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
        dataIndex: 'operator',
        width: 150,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          const tenantId = getCurrentOrganizationId();
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
                      meaning: '消息模板-编辑',
                    },
                  ]}
                  onClick={() => this.handleUpdate(record)}
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
                <a onClick={() => this.handleCopy(record)}>
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
                  title={intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？')}
                  onConfirm={() => this.handleDel(record)}
                  style={{ textAlign: 'center' }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
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
          loading={loading}
          rowKey="templateId"
          dataSource={dataSource}
          pagination={pagination}
          columns={columns}
          scroll={{ x: tableScrollWidth(columns) }}
          onChange={(page) => onChange(page)}
        />
      </>
    );
  }
}
