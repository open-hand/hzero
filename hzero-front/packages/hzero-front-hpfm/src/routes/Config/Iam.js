/**
 * System - 系统配置
 * @date: 2019-11-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';

const FormItem = Form.Item;
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};

@Form.create({ fieldNameProp: null })
export default class System extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  render() {
    const {
      form: { getFieldDecorator },
      config: { data = [] },
      isSite,
    } = this.props;

    const password = this.findConfigField('HIAM.IF_SEND_MODIFY_PASSWORD', data);
    const sendFlag = this.findConfigField('HIAM.IF_SEND_CREATE_USER', data);
    const url = this.findConfigField('HIAM.INDEX_URL', data);
    return (
      <Form>
        {isSite && (
          <Row>
            <Col span={16}>
              <FormItem
                label={intl.get('hpfm.config.model.config.url').d('发送信息首页地址链接')}
                {...formLayout}
              >
                {getFieldDecorator('url', {
                  initialValue: url,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.config.model.config.url').d('发送信息首页地址链接'),
                      }),
                    },
                    // {
                    //   max: 80,
                    //   message: intl.get('hzero.common.validation.max', { max: 80 }),
                    // },
                  ],
                })(<Input />)}
              </FormItem>
            </Col>
          </Row>
        )}
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.password').d('修改密码是否发送消息')}
              {...formLayout}
            >
              {getFieldDecorator('password', {
                initialValue: password === '1' ? 1 : 0,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={16}>
            <FormItem
              label={intl.get('hpfm.config.model.config.sendFlag').d('创建用户是否发送消息')}
              {...formLayout}
            >
              {getFieldDecorator('sendFlag', {
                initialValue: sendFlag === '1' ? 1 : 0,
              })(<Switch />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * 从配置列表查找配置项
   * @param {Number|String} field 查询配置字段的 ID 或 Code
   * @param {Array} data 获取到的原配置数组
   */
  @Bind()
  findConfigField(field, data) {
    if (data.length > 0) {
      const dataFilter = data.find(item => {
        return item.configCode === field;
      });
      return dataFilter ? dataFilter.configValue : null;
    }
  }
}
