/**
 * ListTable - 数据变更审计配置详情页-列表
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, Icon, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { yesOrNoRender, operatorRender, enableRender } from 'utils/renderer';
import intl from 'utils/intl';
import { tableScrollWidth } from 'utils/utils';
import EditTable from 'components/EditTable';

const { Option } = Select;
const promptCode = 'hmnt.dataAuditConfig.model.dataAuditConfig';

/**
 * 跳转条件数据列表
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onEdit - 编辑
 * @reactProps {Function} onEnable - 启停用
 * @reactProps {Boolean} loading - 数据加载完成标记
 * @reactProps {Array} detailList - 数据源
 * @return React.element
 */

export default class DetailTable extends PureComponent {
  state = {
    currentAuditDataConfigLineId: null,
  };

  /**
   * 启停用
   * @param {object} record - 表格行数组
   */
  @Bind()
  handleEnable(record) {
    this.setState(
      {
        currentAuditDataConfigLineId: record.auditDataConfigLineId,
      },
      () => {
        this.props.onEnable(record);
      }
    );
  }

  render() {
    const { detailList, loading, onEdit, isHandling, displayTypes } = this.props;
    const { currentAuditDataConfigLineId } = this.state;
    const columns = [
      {
        title: intl.get(`${promptCode}.fieldName`).d('字段名'),
        dataIndex: 'fieldName',
        width: 200,
      },
      {
        title: intl.get(`${promptCode}.columnName`).d('列名'),
        dataIndex: 'columnName',
        width: 200,
      },
      {
        title: intl.get(`${promptCode}.pkIdFlag`).d('是否主键'),
        dataIndex: 'pkIdFlag',
        width: 90,
        render: yesOrNoRender,
      },
      {
        title: intl.get(`${promptCode}.uniqueFlag`).d('是否唯一'),
        dataIndex: 'uniqueFlag',
        width: 90,
        render: yesOrNoRender,
      },
      {
        title: intl.get(`${promptCode}.transientFlag`).d('是否非数据库字段'),
        dataIndex: 'transientFlag',
        width: 150,
        render: yesOrNoRender,
      },
      {
        title: intl.get(`${promptCode}.dataSecurityFlag`).d('是否敏感字段'),
        dataIndex: 'dataSecurityFlag',
        width: 150,
        render: yesOrNoRender,
      },
      {
        title: intl.get(`${promptCode}.multiLanguageFlag`).d('是否多语言字段'),
        dataIndex: 'multiLanguageFlag',
        width: 150,
        render: yesOrNoRender,
      },
      {
        title: intl.get(`hzero.common.status`).d('状态'),
        dataIndex: 'auditFlag',
        width: 150,
        render: enableRender,
      },
      {
        title: intl.get(`${promptCode}.displayName`).d('展示名称'),
        dataIndex: 'displayName',
        width: 200,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('displayName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.displayName`).d('展示名称'),
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
        title: intl.get(`${promptCode}.displayType`).d('展示类型'),
        dataIndex: 'displayTypeMeaning',
        width: 200,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('displayType', {
                initialValue: record.displayType,
              })(
                <Select style={{ width: '100%' }} allowClear>
                  {displayTypes &&
                    displayTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get(`${promptCode}.displayMask`).d('格式掩码'),
        dataIndex: 'displayMask',
        width: 200,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('displayMask', {
                initialValue: val,
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        key: 'edit',
        fixed: 'right',
        render: (_, record) => {
          const operators = [];
          if (record._status !== 'update') {
            operators.push({
              key: 'edit',
              ele: (
                <a onClick={() => onEdit(record, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          if (record._status === 'update') {
            operators.push({
              key: 'cancel',
              ele: (
                <a onClick={() => onEdit(record, false)}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </a>
              ),
            });
          }
          operators.push({
            key: 'enableOrDisable',
            ele:
              currentAuditDataConfigLineId === record.auditDataConfigLineId && isHandling ? (
                <a>
                  <Icon type="loading" />
                </a>
              ) : (
                <a onClick={() => this.handleEnable(record)}>
                  {record.auditFlag === 1
                    ? intl.get('hzero.common.button.disable').d('禁用')
                    : intl.get('hzero.common.button.enable').d('启用')}
                </a>
              ),
            len: 2,
            title:
              record.auditFlag === 1
                ? intl.get('hzero.common.button.disable').d('禁用')
                : intl.get('hzero.common.button.enable').d('启用'),
          });
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ];

    return (
      <EditTable
        bordered
        loading={loading}
        rowKey="auditDataConfigLineId"
        columns={columns}
        dataSource={detailList}
        scroll={{ x: tableScrollWidth(columns) }}
      />
    );
  }
}
