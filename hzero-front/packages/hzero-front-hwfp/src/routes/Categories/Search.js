import React, { Component } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import cacheComponent from 'components/CacheComponent';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onStore - 存储表单值
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hwfl/setting/categories' })
export default class Search extends Component {
  // 提交查询表单
  @Bind()
  handleFormSearch() {
    const { form, onSearch } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onSearch(values);
      }
    });
  }

  // 重置表单
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  render() {
    const { form, isSiteFlag } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          {isSiteFlag && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('entity.tenant.tag').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hwfp.categories.model.categories.categoryCode').d('流程分类编码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('categoryCode')(<Input trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hwfp.categories.model.categories.description').d('流程分类描述')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('description')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleFormSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
