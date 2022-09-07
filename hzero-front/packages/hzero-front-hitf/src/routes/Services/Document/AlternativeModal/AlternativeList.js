/**
 * AlternativeList - 备选值-数据列表
 * @date: 2019/5/28
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Checkbox, Form, Input, Popconfirm } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import EditTable from 'components/EditTable';

/**
 * 备选值列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Array} dataSource - 列表数据源
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Function} onEditLine - 编辑行
 * @reactProps {Function} onCleanLine - 清除行
 * @reactProps {Function} onDelete - 删除行
 * @return React.element
 */
export default class AlternativeList extends PureComponent {
  /**
   * 更改默认值
   * @param {object} e - EVENT
   * @param {object} record - 当前行数据
   */
  @Bind()
  changeDefaultValue(e, record) {
    const { onChangeDefaultFlag } = this.props;
    onChangeDefaultFlag(e.target.checked, record);
  }

  render() {
    const { dataSource, loading, onEditLine, onCleanLine, onDelete } = this.props;
    const columns = [
      {
        title: intl.get('hitf.services.model.services.requiredFlag').d('是否必填'),
        dataIndex: 'defaultFlag',
        width: 70,
        align: 'center',
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Checkbox checked={val === 1} onChange={(e) => this.changeDefaultValue(e, record)} />
          ) : (
            <Checkbox disabled checked={val === 1} />
          ),
      },
      {
        title: intl.get('hitf.services.model.services.param.alternative.value').d('参数潜在值'),
        dataIndex: 'paramValue',
        width: 150,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('paramValue', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.services.model.services.param.alternative.value')
                        .d('参数潜在值'),
                    }),
                  },
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
                initialValue: val,
              })(<Input />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hitf.services.model.services.remark').d('说明'),
        dataIndex: 'remark',
        width: 150,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('remark', {
                initialValue: val,
              })(<Input />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 90,
        render: (val, record) => {
          const operators = [];
          if (record._status === 'create') {
            operators.push({
              key: 'clean',
              ele: (
                <a onClick={() => onCleanLine(record)}>
                  {intl.get('hzero.common.button.clean').d('清除')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.clean').d('清除'),
            });
          } else if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => onEditLine(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.cancel').d('取消'),
            });
          } else {
            operators.push(
              {
                key: 'edit',
                ele: (
                  <a onClick={() => onEditLine(record, true)}>
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </a>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
              {
                key: 'delete',
                ele: (
                  <Popconfirm
                    title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                    placement="topRight"
                    onConfirm={() => onDelete(record)}
                  >
                    <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                  </Popconfirm>
                ),
                len: 2,
                title: intl.get('hzero.common.button.delete').d('删除'),
              }
            );
          }
          return operatorRender(operators, record);
        },
      },
    ];

    return (
      <EditTable
        bordered
        rowKey="paramValueId"
        loading={loading}
        columns={columns}
        dataSource={dataSource}
        pagination={false}
      />
    );
  }
}
