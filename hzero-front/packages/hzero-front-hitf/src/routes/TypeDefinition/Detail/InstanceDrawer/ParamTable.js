/**
 * ParamTable - 参数映射表格
 * @date: 2019/8/27
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input } from 'hzero-ui';
import EditTable from 'components/EditTable';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { tableScrollWidth } from 'utils/utils';

/**
 * 参数映射表格
 * @extends {Component} - React.Component
 * @reactProps {array} dataSource - 参数映射数据源
 * @reactProps {array} instInterfaceId - 实例接口id
 * @reactProps {Function} onEditLine - 编辑行
 * @return React.element
 */
export default class ParamTable extends Component {
  render() {
    const { dataSource = [], instInterfaceId, onEditLine, isReq, interfaceId } = this.props;
    const columns = [
      {
        title: isReq
          ? intl.get('hitf.typeDefinition.model.typeDefinition.sourceParamName').d('实例参数')
          : intl.get('hitf.typeDefinition.model.typeDefinition.combineParamName').d('组合参数'),
        dataIndex: 'sourceParamName',
        width: 150,
      },
      {
        title: isReq
          ? intl.get('hitf.typeDefinition.model.typeDefinition.srcParamType').d('实例参数类型')
          : intl.get('hitf.typeDefinition.model.typeDefinition.combineParamType').d('组合参数类型'),
        dataIndex: 'sourceParamNameType',
        width: 150,
      },
      {
        title: isReq
          ? intl.get('hitf.typeDefinition.model.typeDefinition.srcParamRemark').d('实例参数说明')
          : intl
              .get('hitf.typeDefinition.model.typeDefinition.combineParamRemark')
              .d('组合参数说明'),
        dataIndex: 'sourceParamRemark',
        width: 150,
      },
      {
        title: intl.get('hitf.typeDefinition.model.typeDefinition.targetParamName').d('映射参数'),
        dataIndex: 'targetParamName',
        width: 150,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('targetParamName', {
                // rules: [
                //   {
                //     required: true,
                //     message: intl.get('hzero.common.validation.notNull', {
                //       name: intl.get('hitf.typeDefinition.model.typeDefinition.targetParamName').d('映射参数'),
                //     }),
                //   },
                // ],
                initialValue: record.targetParamId,
              })(
                <Lov
                  code="HITF.PARAMETER_MAPPING"
                  textValue={record.targetParamName}
                  queryParams={{
                    paramType: record.sourceParamNameType,
                    actionType: record.actionType,
                    instInterfaceId: record.actionType === 'REQ' ? interfaceId : instInterfaceId,
                  }}
                  onChange={(_, item) => {
                    record.$form.setFieldsValue({ targetParamNameType: item.paramType });
                  }}
                />
              )}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl
          .get('hitf.typeDefinition.model.typeDefinition.targetParamNameType')
          .d('映射参数类型'),
        dataIndex: 'targetParamNameType',
        width: 150,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('targetParamNameType', {
                initialValue: val,
              })(<Input disabled />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl
          .get('hitf.typeDefinition.model.typeDefinition.targetParamRemark')
          .d('映射参数说明'),
        dataIndex: 'targetParamRemark',
        width: 150,
        render: (val, record) =>
          record._status === 'update' ? (
            <Form.Item>
              {record.$form.getFieldDecorator('targetParamRemark', {
                initialValue: val,
              })(<Input disabled />)}
            </Form.Item>
          ) : (
            val
          ),
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 90,
        fixed: 'right',
        render: (val, record) => {
          const operators = [];
          if (record._status === 'update') {
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
            operators.push({
              key: 'edit',
              ele: (
                <a onClick={() => onEditLine(record, true)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            });
          }
          return operatorRender(operators, record);
        },
      },
    ];
    return (
      <>
        <EditTable
          rowKey="applicationInstMapId"
          bordered
          pagination={false}
          columns={columns}
          dataSource={dataSource}
          scroll={{ x: tableScrollWidth(columns) }}
        />
      </>
    );
  }
}
