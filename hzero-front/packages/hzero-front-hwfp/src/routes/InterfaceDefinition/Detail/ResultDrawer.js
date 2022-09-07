import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import EditTable from 'components/EditTable';

import intl from 'utils/intl';
import { getEditTableData } from 'utils/utils';

@Form.create({ fieldNameProp: null })
export default class AppSourceForm extends React.PureComponent {
  @Bind()
  handleResult() {
    const {
      onResult = e => e,
      currentRecord: { interfaceCode },
      parameterList,
    } = this.props;
    const params = getEditTableData(parameterList, ['parameterId']);
    if (parameterList.length === 0) {
      return onResult({ interfaceCode });
    }
    if (Array.isArray(params) && params.length > 0) {
      const args = {};
      params.forEach(item => {
        args[item.parameterName] = item.paramterValue;
      });
      onResult({ interfaceCode, args });
    }
  }

  render() {
    const { visible, loading, onCancel, resultData, parameterList } = this.props;
    const columns = [
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.parameterName').d('参数名称'),
        width: 150,
        dataIndex: 'parameterName',
        render: (val, record) => {
          if (record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('parameterName', {
                  initialValue: val,
                })(<Input disabled />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
      {
        title: intl.get('hwfp.interfaceDefinition.model.param.parameterValue').d('参数值'),
        dataIndex: 'paramterValue',
        render: (val, record) => {
          const { defaultValue = '' } = record;
          if (record._status === 'update' || record._status === 'create') {
            const { getFieldDecorator } = record.$form;
            return (
              <Form.Item>
                {getFieldDecorator('paramterValue', {
                  initialValue: defaultValue,
                })(<Input />)}
              </Form.Item>
            );
          } else {
            return val;
          }
        },
      },
    ];
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hwfp.interfaceDefinition.view.test').d('接口测试')}
        width="920px"
        visible={visible}
        confirmLoading={loading}
        okText={intl.get('hzero.common.button.test').d('测试')}
        onCancel={onCancel}
        onOk={this.handleResult}
      >
        <EditTable
          bordered
          rowKey="parameterId"
          columns={columns}
          dataSource={parameterList}
          pagination={false}
        />
        {!isEmpty(resultData) ? (
          <pre
            style={{
              margin: '10px 0',
              padding: 10,
              background: '#fcf6db',
              color: '#444',
              borderRadius: 2,
              overflow: 'scroll',
            }}
          >
            {JSON.stringify(resultData, null, 4)}
          </pre>
        ) : null}
      </Modal>
    );
  }
}
