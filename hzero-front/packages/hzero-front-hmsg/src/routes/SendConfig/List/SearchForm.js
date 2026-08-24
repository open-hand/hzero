import React from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import cacheComponent from 'components/CacheComponent';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hmsg/send-config/search-form' })
export default class SearchForm extends React.Component {
  constructor(props) {
    super(props);
    const { onRef } = props;
    onRef(this);
  }

  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  @Bind()
  handleSearch() {
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form, tenantRoleLevel } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('entity.tenant.tag').d('租户')}
              >
                {form.getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmsg.sendConfig.model.sendConfig.messageCode').d('消息代码')}
            >
              {form.getFieldDecorator('messageCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hmsg.sendConfig.model.sendConfig.messageName').d('消息名称')}
            >
              {form.getFieldDecorator('messageName')(<Input />)}
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
