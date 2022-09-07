/**
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-28
 * @copyright 2019-05-28 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

import Lov from 'components/Lov';

@Form.create({ fieldNameProp: null })
export default class SearchForm extends Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
  };

  @Bind()
  handleSearchFormSubmit(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleSearchFormReset(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { languageMessage, form, removeBatchLoading = false } = this.props;
    return (
      <Form
        className={SEARCH_FORM_CLASSNAME}
        onSubmit={this.handleSearchFormSubmit}
        onReset={this.handleSearchFormReset}
      >
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={languageMessage.model.basicData.dataType}
            >
              {form.getFieldDecorator('dataType')(<Lov code="HNLP.BASIC_DATA_TYPE" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.model.basicData.value}>
              {form.getFieldDecorator('value')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.model.basicData.empty}>
              {form.getFieldDecorator('empty')(
                <Select allowClear>
                  <Select.Option key={0} value={0}>
                    {languageMessage.common.status.yes}
                  </Select.Option>
                  <Select.Option key={1} value={1}>
                    {languageMessage.common.status.no}
                  </Select.Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button htmlType="reset">{languageMessage.common.btn.reset}</Button>
              <Button type="primary" htmlType="submit" disabled={removeBatchLoading}>
                {languageMessage.common.btn.search}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
