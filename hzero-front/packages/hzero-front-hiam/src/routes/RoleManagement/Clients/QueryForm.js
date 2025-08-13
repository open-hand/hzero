import React from 'react';
import PropTypes from 'prop-types';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import {
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_2_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class QueryForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  /**
   * 重置查询表单
   * @param {*} e
   */
  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 查询表单-查询
   * @param {*} e
   */
  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  render() {
    const { form } = this.props;
    return (
      <Form>
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hiam.roleManagement.model.roleManagement.clientName')
                .d('客户端名称')}
            >
              {form.getFieldDecorator('name')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_2_LAYOUT}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
              <Button onClick={this.handleResetBtnClick} style={{ marginRight: '8px' }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
