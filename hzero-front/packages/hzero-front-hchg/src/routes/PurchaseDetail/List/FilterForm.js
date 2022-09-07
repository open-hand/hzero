/**
 * FilterForm - 购买详单-列表页查询条件表单
 * @date: 2019-8-30
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';

import cacheComponent from 'components/CacheComponent';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hchg/purchase-detail' })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields(err => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator = e => e },
      statusList,
    } = this.props;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.purchaseDetail.model.purchaseDetail.tradeNo').d('交易流水')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('tradeNo', {
                rules: [
                  {
                    max: 600,
                    message: intl.get('hzero.common.validation.max', {
                      max: 600,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hchg.purchaseDetail.model.purchaseDetail.chargeService')
                .d('服务代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeService', {
                rules: [
                  {
                    max: 128,
                    message: intl.get('hzero.common.validation.max', {
                      max: 128,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.status').d('状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('orderStatus')(
                <Select allowClear>
                  {statusList.map(item => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button data-code="reset" onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                data-code="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearch}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
