import React, { PureComponent } from 'react';
import { Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Button as ButtonPermission } from 'components/Permission';

import { enableRender, operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';

/**
 * 规则引擎数据展示列表
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
   * @param {object} record - 规则引擎对象
   */
  @Bind()
  editData(record) {
    this.props.onEdit(record);
  }

  /**
   * 删除
   * @param {object} record - 规则引擎对象
   */
  @Bind()
  deleteData(record) {
    this.props.onDelete(record);
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { loading, ruleEngineData = {}, pagination, onChange, match } = this.props;
    const columns = [
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.tenantId').d('租户'),
        dataIndex: 'tenantName',
        width: 150,
      },
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.serverName').d('服务名称'),
        dataIndex: 'serverName',
        width: 150,
      },
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.scriptCode').d('脚本编码'),
        dataIndex: 'scriptCode',
        width: 150,
      },
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.category').d('脚本分类'),
        dataIndex: 'categoryMeaning',
        width: 150,
      },
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.scriptDescription').d('描述'),
        dataIndex: 'scriptDescription',
      },
      {
        title: intl.get('hpfm.ruleEngine.model.ruleEngine.scriptTypeCode').d('类型'),
        dataIndex: 'scriptTypeMeaning',
        width: 150,
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
        width: 120,
        render: (val, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${match.path}.button.edit`,
                      type: 'button',
                      meaning: '规则引擎-编辑',
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
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.deleteData(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${match.path}.button.delete`,
                        type: 'button',
                        meaning: '规则引擎-删除',
                      },
                    ]}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(col => {
      return isTenantRoleLevel() ? col.dataIndex !== 'tenantName' : true;
    });
    return (
      <Table
        bordered
        loading={loading}
        rowKey="ruleScriptId"
        columns={columns}
        dataSource={ruleEngineData.content}
        pagination={pagination}
        onChange={page => onChange(page)}
      />
    );
  }
}
