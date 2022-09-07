/**
 * 接口字段维护 /hiam/api-field
 * DataTable
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Popconfirm, Table } from 'hzero-ui';

import { Button as ButtonPermission } from 'components/Permission';

import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';
import intl from 'hzero-front/lib/utils/intl';

export default class DataTable extends Component {
  static propTypes = {
    // dataSource: PropTypes.array.isRequired,
    // pagination: PropTypes.object.isRequired,
    // loading: PropTypes.bool.isRequired,
    onChange: PropTypes.func.isRequired,
    // 字段维护按钮点击
    onRecordDelete: PropTypes.func.isRequired,
  };

  getColumns() {
    return [
      {
        title: intl.get('hiam.apiField.model.field.fieldName').d('字段名称'),
        dataIndex: 'fieldName',
        width: 160,
      },
      {
        title: intl.get('hiam.apiField.model.field.fieldType').d('字段类型'),
        dataIndex: 'fieldTypeMeaning',
        width: 160,
      },
      {
        title: intl.get('hiam.apiField.model.field.orderSeq').d('排序号'),
        width: 160,
        dataIndex: 'orderSeq',
      },
      {
        title: intl.get('hiam.apiField.model.field.fieldDescription').d('字段描述'),
        dataIndex: 'fieldDescription',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 110,
        fixed: 'right',
        render: (_, record) => {
          const { onRecordDelete, onRecordEdit, path } = this.props;
          const actions = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '接口字段维护详情-编辑',
                    },
                  ]}
                  onClick={() => {
                    onRecordEdit(record);
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
                    onRecordDelete(record);
                  }}
                >
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '接口字段维护详情-删除',
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
          return operatorRender(actions);
        },
      },
    ];
  }

  render() {
    const { dataSource, pagination, removeLoading = false, loading = false, onChange } = this.props;
    const columns = this.getColumns();
    return (
      <Table
        bordered
        rowKey="id"
        dataSource={dataSource}
        pagination={pagination}
        columns={columns}
        scroll={{ x: tableScrollWidth(columns) }}
        onChange={onChange}
        loading={loading || removeLoading}
      />
    );
  }
}
