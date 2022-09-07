/**
 * FilterForm - 发票查验历史-查询条件表单
 * @date: 2019-8-26
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

function FilterForm(props) {
  const {
    form: { getFieldDecorator },
    isSiteFlag,
  } = props;
  /**
   * 提交查询表单
   */
  const handleSearch = () => {
    const { onSearch } = props;
    if (onSearch) {
      onSearch();
    }
  };

  /**
   * 重置表单
   */
  const handleFormReset = () => {
    props.form.resetFields();
  };

  return (
    <Form className="more-fields-search-form">
      <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" className="c7n-form-line-with-btn">
        {isSiteFlag && (
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hivc.inspectionHistory.model.inspectionHistory.tenantName')
                .d('租户')}
            >
              {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
            </Form.Item>
          </Col>
        )}
        <Col {...FORM_COL_4_LAYOUT}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl
              .get('hivc.inspectionHistory.model.inspectionHistory.invoiceCode')
              .d('发票代码')}
          >
            {getFieldDecorator('invoiceCode')(<Input trim />)}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_4_LAYOUT}>
          <Form.Item
            {...SEARCH_FORM_ITEM_LAYOUT}
            label={intl
              .get('hivc.inspectionHistory.model.inspectionHistory.invoiceNo')
              .d('发票号码')}
          >
            {getFieldDecorator('invoiceNo')(<Input trim />)}
          </Form.Item>
        </Col>
        <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
          <Form.Item>
            <Button onClick={handleFormReset}>
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button type="primary" onClick={handleSearch} htmlType="submit">
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );
}
export default Form.create({ fieldNameProp: null })(FilterForm);

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @return React.element
 */
