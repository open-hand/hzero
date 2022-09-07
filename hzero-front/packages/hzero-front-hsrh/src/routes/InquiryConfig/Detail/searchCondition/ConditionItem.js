import React, { Component } from 'react';
import { Form, Row, Col, Select, Input } from 'hzero-ui';
// import { DEFAULT_DATETIME_FORMAT } from 'utils/constants';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';

const formlayout = {
  labelCol: { span: 2 },
  wrapperCol: { span: 22 },
};

export default class ConditionItem extends Component {
  state = {
    logicSymbolList: [
      {
        value: 'term',
        meaning: '=',
      },
      {
        value: 'gt',
        meaning: '>',
      },
      {
        value: 'lt',
        meaning: '<',
      },
    ],
  };

  @Bind()
  indexOnchange(val) {
    const { form, item, indexList } = this.props;
    const index = indexList.filter((res) => res.fieldName === val);
    const dataTypeList = ['LONG', 'INTEGER', 'DOUBLE', 'FLOAT', 'DATE'];
    if (index && index[0]) {
      // 切换清空参数
      form.setFieldsValue({ [`${item.id}-logicSymbol`]: '' });
      form.setFieldsValue({ [`${item.id}-param`]: '' });
      if (dataTypeList.every((res) => res !== index[0].fieldType)) {
        this.setState({
          logicSymbolList: [
            {
              value: 'term',
              meaning: '=',
            },
          ],
        });
      } else {
        this.setState({
          logicSymbolList: [
            {
              value: 'term',
              meaning: '=',
            },
            {
              value: 'gt',
              meaning: '>',
            },
            {
              value: 'lt',
              meaning: '<',
            },
          ],
        });
      }
    }
  }

  render() {
    const {
      form: { getFieldDecorator },
      item = {},
      indexList,
    } = this.props;
    const { logicSymbolList } = this.state;
    return (
      <React.Fragment>
        <Form>
          <Row>
            <Col span={8}>
              <Form.Item {...formlayout}>
                {getFieldDecorator(`${item.id}-indexCode`, {
                  initialValue: item && item.indexCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hsrh.inquiryConfig.model.inquiryConfig.indexCode')
                          .d('索引'),
                      }),
                    },
                  ],
                })(
                  <Select style={{ width: '100%' }} onChange={this.indexOnchange}>
                    {(indexList || []).map((items) => (
                      <Select.Option value={items.fieldName} key={items.fieldName}>
                        {items.fieldName}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item {...formlayout}>
                {getFieldDecorator(`${item.id}-logicSymbol`, {
                  initialValue: item && item.logicSymbol,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hsrh.inquiryConfig.model.inquiryConfig.logicSymbol')
                          .d('逻辑符'),
                      }),
                    },
                  ],
                })(
                  <Select style={{ width: '100%' }} onChange={this.handleDefineOnChange}>
                    {logicSymbolList.map((items) => (
                      <Select.Option key={items.value}>{items.meaning}</Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item {...formlayout}>
                {getFieldDecorator(`${item.id}-param`, {
                  initialValue: item && item.param,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hsrh.inquiryConfig.model.inquiryConfig.param').d('参数值'),
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </React.Fragment>
    );
  }
}
