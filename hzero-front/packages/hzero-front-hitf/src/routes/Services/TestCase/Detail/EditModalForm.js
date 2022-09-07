/**
 * EditModalForm - 编辑测试用例-表单
 * @date: 2019/6/17
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

const { Option } = Select;
const { TextArea } = Input;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const formTextAreaLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 19 },
};

/**
 * 测编辑测试用例-表单
 * @extends {Component} - React.Component
 * @reactProps {object} testCaseDetail - 测试用例详情
 * @reactProps {array} usecaseTypes - 用例类型值集
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class EditModalForm extends Component {
  /**
   * 收集表单值
   */
  @Bind()
  getFormValue() {
    const { form } = this.props;
    let formValues = {};
    form.validateFields((err, values) => {
      if (!err) {
        formValues = { ...values };
      }
    });
    return formValues;
  }

  render() {
    const {
      form: { getFieldDecorator },
      testCaseDetail,
      usecaseTypes,
    } = this.props;
    return (
      <Form>
        <Row type="flex">
          <Col span={12}>
            <Form.Item
              label={intl.get('hitf.services.model.services.usecaseName').d('用例名称')}
              {...formLayout}
            >
              {getFieldDecorator('usecaseName', {
                initialValue: testCaseDetail.usecaseName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.usecaseName').d('用例名称'),
                    }),
                  },
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label={intl.get('hitf.services.model.services.usecaseType').d('用例类型')}
              {...formLayout}
            >
              {getFieldDecorator('usecaseType', {
                initialValue: testCaseDetail.usecaseType,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hitf.services.model.services.usecaseType').d('用例类型'),
                    }),
                  },
                ],
              })(
                <Select allowClear>
                  {usecaseTypes.length &&
                    usecaseTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row>
          <Col span={24}>
            <Form.Item
              label={intl.get('hitf.services.model.services.remark').d('说明')}
              {...formTextAreaLayout}
            >
              {getFieldDecorator('remark', {
                initialValue: testCaseDetail.remark,
              })(<TextArea style={{ height: 116 }} rows={4} />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
