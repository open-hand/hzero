import React, { PureComponent } from 'react';
import { Button, Form, Input, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';

import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

const modelPrompt = 'hiam.menuConfig.model.menuConfig';
const commonPrompt = 'hzero.common';

@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  onClick() {
    const {
      handleQueryList = (e) => e,
      form: { getFieldsValue = (e) => e },
    } = this.props;
    const data = getFieldsValue() || {};
    handleQueryList({
      ...data,
    });
  }

  onReset() {
    const {
      form: { resetFields = (e) => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
    } = this.props;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom">
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`${modelPrompt}.permissionCode`).d('权限编码')}
            >
              {getFieldDecorator('code')(<Input trim typeCase="lower" inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`${modelPrompt}.permissionName`).d('权限名称')}
            >
              {getFieldDecorator('name')(<Input />)}
            </FormItem>
          </Col>
          <Col span={8}>
            <FormItem>
              <Button onClick={this.onReset.bind(this)} style={{ marginRight: 8 }}>
                {intl.get(`${commonPrompt}.button.reset`).d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick.bind(this)}>
                {intl.get(`${commonPrompt}.button.search`).d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
