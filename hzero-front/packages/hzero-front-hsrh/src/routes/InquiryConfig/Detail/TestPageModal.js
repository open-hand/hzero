import React, { Component } from 'react';
import { Modal, Form, Input } from 'hzero-ui';
import { CodeArea } from 'choerodon-ui/pro';
import JSONFormatter from 'choerodon-ui/pro/lib/code-area/formatters/JSONFormatter';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';

const formlayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
};

@Form.create({ fieldNameProp: null })
export default class UpdateModal extends Component {
  @Bind()
  handleSubmit() {
    const { form } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        const { respondParam, ...res } = fieldsValue;
        this.props.handleExecute(res);
      }
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      result,
      fieldArray,
    } = this.props;
    return (
      <Modal
        title={intl.get(`hsrh.inquiryConfig.model.inquiryConfig.testPage`).d('测试页面')}
        visible={this.props.visible}
        onOk={this.handleSubmit}
        onCancel={this.props.handleCancel}
        okText={intl.get(`hzero.common.button.trigger`).d('执行')}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        destroyOnClose
        bodyStyle={{ overflow: 'hidden' }}
      >
        <Form>
          {fieldArray.map(item => (
            <Form.Item
              {...formlayout}
              label={intl.get(`hsrh.inquiryConfig.model.inquiryConfig.${item}`).d(`${item}`)}
            >
              {getFieldDecorator(`${item}`, {})(<Input />)}
            </Form.Item>
          ))}
          <Form.Item
            {...formlayout}
            label={intl.get('hsrh.inquiryConfig.model.inquiryConfig.result').d('返回结果')}
          >
            {getFieldDecorator('respondParam', {
              initialValue: result,
            })(<CodeArea formatter={JSONFormatter} style={{ height: 450 }} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
