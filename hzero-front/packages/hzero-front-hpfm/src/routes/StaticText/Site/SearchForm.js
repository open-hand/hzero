/**
 * SearchForm.js
 * @date 2018-12-25
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/static-text/list/search' })
export default class SearchForm extends React.Component {
  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  render() {
    const { form } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              key="title"
              label={intl.get('hpfm.staticText.model.staticText.title').d('标题')}
            >
              {form.getFieldDecorator('title')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              key="textCode"
              label={intl.get('hpfm.staticText.model.staticText.code').d('编码')}
            >
              {form.getFieldDecorator('textCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              key="tenantId"
              label={intl.get('entity.tenant.tag').d('租户')}
            >
              {form.getFieldDecorator('tenantId')(
                <Lov code="HPFM.TENANT" textField="tenantName" />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item key="btns">
              <Button key="reset" onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                key="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearchBtnClick}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

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
}
