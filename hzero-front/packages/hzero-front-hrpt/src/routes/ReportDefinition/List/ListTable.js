import React, { PureComponent } from 'react';
import { Table, Popconfirm, Icon, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import { Button as ButtonPermission } from 'components/Permission';
import { tableScrollWidth } from 'utils/utils';
import { VERSION_IS_OP } from 'utils/config';

/**
 * 跳转条件数据列表
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

  copyOption(record) {
    const { onCopy } = this.props;
    onCopy(record);
  }

  /**
   * 分配权限
   * @param {object} record - 数据对象
   */
  assignPermission(record) {
    this.props.onAssign(record);
  }

  /**
   * 删除
   * @param {object} record - 数据对象
   */
  deleteOption(record) {
    this.props.onDelete(record);
  }

  /**
   * 菜单路由
   * @param {object} record
   */
  @Bind()
  handleMenuRoute(record) {
    this.props.onMenuRoute(record);
  }

  /**
   * uReport报表编辑器
   * @param {object} record
   */
  @Bind()
  handleUReportEditor(record) {
    this.props.onUReportEditor(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      loading,
      dataSource,
      pagination,
      onChange,
      tenantRoleLevel,
      tenantId,
      path,
    } = this.props;
    const columns = [
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
        width: 70,
      },
      !tenantRoleLevel && {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hrpt.common.report.reportCode').d('报表代码'),
        dataIndex: 'reportCode',
        width: 150,
      },
      {
        title: intl.get('hrpt.common.report.reportName').d('报表名称'),
        dataIndex: 'reportName',
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.reportType').d('报表类型'),
        dataIndex: 'reportTypeMeaning',
        width: 150,
        render: (text, record) => {
          const code = record.reportTypeCode;
          if (code === 'T' || code === 'ST') {
            return (
              <span>
                <Icon type="table" style={{ color: '#2B975C', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          }
          if (code === 'C') {
            return (
              <span>
                <Icon type="pie-chart" style={{ color: '#AB82FF', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          }
          if (code === 'D') {
            return (
              <span>
                <Icon type="profile" style={{ color: '#E95D3B', marginRight: 5, fontSize: 16 }} />
                {text}
              </span>
            );
          }
          return (
            <span>
              <Icon type="table" style={{ color: '#E95D3B', marginRight: 5, fontSize: 16 }} />
              {text}
            </span>
          );
        },
      },
      {
        title: intl.get('hrpt.reportDefinition.model.reportDefinition.datasetId').d('数据集'),
        dataIndex: 'datasetName',
        width: 150,
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
      },
      tenantRoleLevel &&
        !VERSION_IS_OP && {
          title: intl.get('hrpt.reportDefinition.model.reportDefinition.source').d('来源'),
          dataIndex: 'custom-flag',
          width: 120,
          render: (value, record) =>
            tenantId === record.tenantId ? (
              <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
            ) : (
              <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
            ),
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
        width: 200,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          if (tenantRoleLevel && !VERSION_IS_OP && String(tenantId) !== String(record.tenantId)) {
            operators.push({
              key: 'copy',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.copy`,
                      type: 'button',
                      meaning: '报表定义-复制',
                    },
                  ]}
                  onClick={() => {
                    this.copyOption(record);
                  }}
                >
                  {intl.get('hzero.common.button.copy').d('复制')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.copy').d('复制'),
            });
          } else {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: '报表定义-编辑',
                      },
                    ]}
                    onClick={() => {
                      this.editOption(record);
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'assign',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.assign`,
                        type: 'button',
                        meaning: '报表定义-分配权限',
                      },
                    ]}
                    onClick={() => {
                      this.assignPermission(record);
                    }}
                  >
                    {intl.get('hrpt.common.view.assignPermission').d('分配权限')}
                  </ButtonPermission>
                ),
                len: 4,
                title: intl.get('hrpt.common.view.assignPermission').d('分配权限'),
              },
              {
                key: 'route',
                ele: (
                  <a onClick={() => this.handleMenuRoute(record)}>
                    {intl.get('hrpt.reportDefinition.modal.reportDefinition.route').d('菜单路由')}
                  </a>
                ),
                len: 4,
                title: intl.get('hrpt.reportDefinition.modal.reportDefinition.route').d('菜单路由'),
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
                          code: `${path}.button.delete`,
                          type: 'button',
                          meaning: '报表定义-删除',
                        },
                      ]}
                    >
                      {intl.get('hzero.common.button.delete').d('删除')}
                    </ButtonPermission>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          }
          if (!tenantRoleLevel && record.reportTypeCode === 'U') {
            operators.push({
              key: 'designer',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.designer`,
                      type: 'button',
                      meaning: '报表定义-设计器',
                    },
                  ]}
                  onClick={() => {
                    this.handleUReportEditor(record);
                  }}
                >
                  {intl.get('hrpt.reportDefinition.view.reportDefinition.designer').d('设计器')}
                </ButtonPermission>
              ),
              len: 3,
              title: intl.get('hrpt.reportDefinition.view.reportDefinition.designer').d('设计器'),
            });
          }
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    return (
      <Table
        bordered
        rowKey="reportId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSource}
        pagination={pagination}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
