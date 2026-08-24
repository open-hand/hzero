/**
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-08-14
 * @copyright 2019 © HAND
 */

import React from 'react';
import { Form, Input, Button, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  FORM_COL_3_LAYOUT,
  FORM_FIELD_CLASSNAME,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class SearchForm extends React.Component {
  @Bind()
  handleSearchBtnClick() {
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleResetBtnClick() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
      disabled,
    } = this.props;
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.roleManagement.model.roleManagement.loginName').d('账户')}
            >
              {getFieldDecorator('userLoginName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.roleManagement.model.roleManagement.userLoginName').d('用户名')}
            >
              {getFieldDecorator('userRealName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                type="primary"
                htmlType="submit"
                onClick={this.handleSearchBtnClick}
                disabled={disabled}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.roleManagement.model.roleManagement.tenant').d('所属租户')}
            >
              {getFieldDecorator('tenantName')(<Input className={FORM_FIELD_CLASSNAME} />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
