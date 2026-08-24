import React, { PureComponent } from 'react';
import { Table, Tag } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, valueMapMeaning, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel, tableScrollWidth } from 'utils/utils';
import intl from 'utils/intl';

/**
 * 表单管理数据列表
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
export default class TableList extends PureComponent {
  /**
   * 编辑
   *
   * @param {*} record
   * @memberof ListTable
   */
  @Bind()
  editData(record) {
    const { onEdit } = this.props;
    onEdit(record);
  }

  @Bind()
  viewData(record) {
    const { onView } = this.props;
    onView(record);
  }

  render() {
    const {
      dataSourceData = {},
      match,
      loading,
      dataSourceTypeList,
      pagination,
      onChange,
      tenantId,
    } = this.props;
    const columns = [
      {
        title: intl.get('entity.tenant.tag').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hpfm.dataSource.model.dataSource.datasourceCode').d('数据源编码'),
        dataIndex: 'datasourceCode',
        width: 150,
      },
      {
        title: intl.get('hpfm.dataSource.model.dataSource.description').d('数据源名称'),
        dataIndex: 'description',
        width: 250,
      },
      {
        title: intl.get('hpfm.dataSource.model.dataSource.dsPurposeCodeMeaning').d('数据源用途'),
        dataIndex: 'dsPurposeCodeMeaning',
        width: 280,
        render: (val, record) => {
          const valList = val.split(',') || [];
          const list = record.dsPurposeCode.split(',') || [];
          return valList.map((item, index) => {
            let mean = '';
            if (item) {
              switch (list[index]) {
                case 'DI':
                  mean = <Tag color="green">{item}</Tag>;
                  break;
                case 'DT':
                  mean = <Tag color="orange">{item}</Tag>;
                  break;
                default:
                  mean = <Tag color="red">{item}</Tag>;
                  break;
              }
            }
            return mean;
          });
        },
      },
      {
        title: intl.get('hpfm.dataSource.model.dataSource.class').d('数据源分类'),
        dataIndex: 'dsClassMeaning',
        width: 250,
      },
      {
        title: intl.get('hpfm.dataSource.model.dataSource.dbType').d('数据库类型'),
        dataIndex: 'dbType',
        width: 100,
        render: (val) => val && valueMapMeaning(dataSourceTypeList, val),
      },
      {
        title: intl.get('hzero.common.source').d('来源'),
        dataIndex: 'source',
        width: 100,
        render: (_, record) => {
          return tenantId === record.tenantId ? (
            <Tag color="green">{intl.get('hzero.common.custom').d('自定义')}</Tag>
          ) : (
            <Tag color="orange">{intl.get('hzero.common.predefined').d('预定义')}</Tag>
          );
        },
      },
      {
        title: intl.get('hzero.common.remark').d('备注'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 90,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        dataIndex: 'operator',
        fixed: 'right',
        width: 80,
        render: (val, record) => {
          const operators = [];
          if (tenantId === record.tenantId || !isTenantRoleLevel()) {
            operators.push({
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '数据源-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.editData(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          } else {
            operators.push({
              key: 'view',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.view`,
                      type: 'button',
                      meaning: '数据源-查看',
                    },
                  ]}
                  onClick={() => {
                    this.viewData(record);
                  }}
                >
                  {intl.get('hzero.common.button.view').d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.view').d('查看'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ].filter((col) => {
      return isTenantRoleLevel() ? col.dataIndex !== 'tenantName' : col.dataIndex !== 'source';
    });
    return (
      <Table
        bordered
        rowKey="datasourceId"
        loading={loading}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        dataSource={dataSourceData.content}
        pagination={pagination}
        onChange={(page) => onChange(page)}
      />
    );
  }
}
