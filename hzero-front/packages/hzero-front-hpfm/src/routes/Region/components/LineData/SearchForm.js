import React from 'react';
import PropTypes from 'prop-types';
import { Form, Input, Button, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class SearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

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
        <Row type="flex" align="bottom" gutter={24}>
          <Col span={6}>
            <Form.Item
              label={intl.get('hpfm.region.model.region.condition').d('区域代码/区域名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('condition')(<Input />)}
            </Form.Item>
          </Col>
          <Col span={6}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT}>
              <Button style={{ marginRight: 8 }} onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
