/**
 * ParamTable - 参数表格（除了BODY）
 * @date: 2019/6/27
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Button, Form, Input, Popconfirm, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import EditTable from 'components/EditTable';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';

const { Option } = Select;

/**
 * 编辑测试用例弹窗
 * @extends {Component} - Component
 * @reactProps {boolean} type - 参数类型
 * @reactProps {array} dataSource - 数据源
 * @reactProps {boolean} confirmLoading - 保存中标志
 * @reactProps {array} modelData - 数据模型（文档中参数的数据）
 * @reactProps {Function} onCreate - 新建参数
 * @reactProps {Function} onCleanLine - 清除参数
 * @reactProps {Function} onEditLine - 编辑参数
 * @reactProps {Function} onDelete - 删除参数
 * @return React.element
 */
export default class ParamTable extends Component {
  /**
   * 改变参数值
   * @param {stirng} value -  当前值
   * @param {object} option - 选项
   * @param {object} record -表格行数据
   */
  @Bind()
  handleChangeName(_, option, record) {
    let converter = '';
    if (option) {
      converter = option._owner ? option.key : '';
    }
    record.$form.setFieldsValue({
      parameterValue: '',
      converter,
    });
  }

  /**
   * 渲染表格列
   */
  @Bind()
  renderColumns() {
    const { dataSource, modelData = [], onDelete, onEditLine, onCleanLine } = this.props;
    const columns = [
      {
        title: intl.get('hitf.services.model.services.param').d('参数'),
        dataIndex: 'parameterName',
        width: 200,
        render: (val, record) =>
          ['update', 'create'].includes(record._status) ? (
            <Form.Item>
              {record.$form.getFieldDecorator('parameterName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.param').d('参数'),
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
              })(
                dataSource && dataSource.length && modelData.length ? (
                  <Select
                    mode="combobox"
                    showSearch
                    allowClear
                    style={{ width: '100%' }}
                    optionLabelProp="children"
                    onChange={(value, option) => this.handleChangeName(value, option, record)}
                  >
                    {modelData.map((item) => (
                      <Option key={item.paramId} value={`${item.paramId}_@_${item.paramName}`}>
                        {item.paramName}
                      </Option>
                    ))}
                  </Select>
                ) : (
                  <Input />
                )
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hitf.services.model.services.paramValue').d('参数值'),
        dataIndex: 'parameterValue',
        width: 200,
        render: (val, record) => {
          if (['update', 'create'].includes(record._status)) {
            let targetValuesArr = [];
            let targetParam;
            if (record.$form.getFieldValue('converter')) {
              targetParam = modelData.find(
                (item) => item.paramId === +record.$form.getFieldValue('converter')
              );
            } else if (record.$form.getFieldValue('converter') !== '') {
              targetParam = modelData.find((item) => item.paramId === record.parameterId);
            }
            targetValuesArr = targetParam ? targetParam.documentParamValueList : [];
            return (
              <>
                <Form.Item style={{ display: 'none' }}>
                  {record.$form.getFieldDecorator('converter', {
                    initialValue: record.parameterId,
                  })(<Input />)}
                </Form.Item>
                <Form.Item>
                  {record.$form.getFieldDecorator('parameterValue', {
                    initialValue: val,
                    rules: [
                      {
                        max: 255,
                        message: intl.get('hzero.common.validation.max', {
                          max: 255,
                        }),
                      },
                    ],
                  })(
                    targetValuesArr && targetValuesArr.length ? (
                      <Select allowClear style={{ width: '100%' }}>
                        {targetValuesArr.map((item) => (
                          <Option key={item.paramValueId} value={item.paramValue}>
                            {item.paramValue}
                          </Option>
                        ))}
                      </Select>
                    ) : (
                      <Input />
                    )
                  )}
                </Form.Item>
              </>
            );
          }
          return val;
        },
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'action',
        width: 90,
        render: (_, record) => {
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
    return columns;
  }

  render() {
    const { dataSource, onCreate, type } = this.props;
    return (
      <>
        <div className="table-list-search" style={{ textAlign: 'right' }}>
          <Button type="primary" onClick={() => onCreate(type)}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </div>
        <EditTable
          dataSource={dataSource}
          columns={this.renderColumns()}
          bordered
          pagination={false}
          style={{ marginBottom: '15px' }}
          rowKey="interfaceUsecaseParamId"
        />
      </>
    );
  }
}
