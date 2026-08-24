import React, { PureComponent } from 'react';
import { Form, Input, Button, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';

import {
  FORM_COL_4_LAYOUT,
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  onClick() {
    const {
      handleFetchData = e => e,
      form: { getFieldsValue = e => e },
    } = this.props;
    const data = getFieldsValue() || {};
    handleFetchData({
      ...data,
    });
  }

  onReset() {
    const {
      form: { resetFields = e => e },
    } = this.props;
    resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = e => e },
      disabled,
    } = this.props;
    return (
      <Form>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hiam.roleManagement.model.roleManagement.userLoginName`).d('用户名')}
            >
              {getFieldDecorator('userRealName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get(`hzero.common.button.reset`).d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                onClick={this.onClick.bind(this)}
                disabled={disabled}
              >
                {intl.get(`hzero.common.button.search`).d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
