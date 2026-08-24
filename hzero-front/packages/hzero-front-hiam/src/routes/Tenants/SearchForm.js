import React from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class SearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
          onSearch(values);
        }
      });
    }
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.tenant.code').d('租户编码')}
            >
              {form.getFieldDecorator('tenantNum')(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('entity.tenant.name').d('租户名称')}
            >
              {form.getFieldDecorator('tenantName')(<Input />)}
            </Form.Item>
          </Col>

          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
