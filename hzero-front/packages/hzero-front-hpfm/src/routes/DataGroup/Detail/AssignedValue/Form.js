/**
 * AssignedForm - 分配值表单
 * @date: 2019/7/15
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form } from 'hzero-ui';
import intl from 'utils/intl';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 数据组管理-分配值表单
 * @extends {Component} - React.Component
 * @reactProps {Object} currentLine - 当前维度值行数据
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class AssignedForm extends PureComponent {
  render() {
    const { currentLine = {} } = this.props;
    const {
      dimensionCode,
      dimensionName,
      tenantName,
    } = currentLine;
    return (
      <Form>
        <FormItem
          label={intl.get('hpfm.dataGroup.model.dataGroup.dimensionCode').d('维度代码')}
          {...formLayout}
        >
          {dimensionCode}
        </FormItem>
        <FormItem
          label={intl.get('hpfm.dataGroup.model.dataGroup.dimensionName').d('维度名称')}
          {...formLayout}
        >
          {dimensionName}
        </FormItem>
        <FormItem
          label={intl.get('entity.tenant.tag').d('租户')}
          {...formLayout}
        >
          {tenantName}
        </FormItem>
      </Form>
    );
  }
}
