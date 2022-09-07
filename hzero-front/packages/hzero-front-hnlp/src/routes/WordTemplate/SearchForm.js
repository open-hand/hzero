/**
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-29
 * @copyright 2019-05-29 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

import Lov from 'components/Lov';

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class SearchForm extends Component {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
  }

  static propTypes = {
    onSearch: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
  };

  @Bind()
  handleChangeTenantId() {
    const { form } = this.props;
    form.resetFields('templateId');
  }

  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

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

  renderTemplateItem(formItemLayout = FORM_COL_4_LAYOUT) {
    const { languageMessage, form } = this.props;
    return (
      <Col {...formItemLayout}>
        <Form.Item
          {...SEARCH_FORM_ITEM_LAYOUT}
          label={languageMessage.model.templateWord.templateName}
        >
          {form.getFieldDecorator('templateName')(<Input trim />)}
        </Form.Item>
      </Col>
    );
  }

  renderActualWordItem(formItemLayout = FORM_COL_4_LAYOUT) {
    const { languageMessage, form } = this.props;
    return (
      <Col {...formItemLayout}>
        <Form.Item
          {...SEARCH_FORM_ITEM_LAYOUT}
          label={languageMessage.model.templateWord.actualWord}
        >
          {form.getFieldDecorator('actualWord')(<Input />)}
        </Form.Item>
      </Col>
    );
  }

  renderWordItem(formItemLayout = FORM_COL_4_LAYOUT) {
    const { languageMessage, form } = this.props;
    return (
      <Col {...formItemLayout}>
        <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.model.templateWord.word}>
          {form.getFieldDecorator('word')(<Input />)}
        </Form.Item>
      </Col>
    );
  }

  renderSiteSearchForm() {
    const { languageMessage, form, removeBatchLoading = false } = this.props;
    const { expandForm } = this.state;
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
              label={languageMessage.model.templateWord.tenant}
            >
              {form.getFieldDecorator('tenantId')(
                <Lov code="HPFM.TENANT" onChange={this.handleChangeTenantId} />
              )}
            </Form.Item>
          </Col>
          {this.renderTemplateItem(FORM_COL_4_LAYOUT)}
          {this.renderActualWordItem(FORM_COL_4_LAYOUT)}
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button htmlType="reset">{languageMessage.common.btn.reset}</Button>
              <Button type="primary" htmlType="submit" disabled={removeBatchLoading}>
                {languageMessage.common.btn.search}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={expandForm ? expandFormStyle : noExpandFormStyle}
          gutter={24}
          type="flex"
          align="bottom"
        >
          {this.renderWordItem(FORM_COL_4_LAYOUT)}
        </Row>
      </Form>
    );
  }

  renderTenantSearchForm() {
    const { languageMessage, removeBatchLoading = false } = this.props;
    return (
      <Form
        className={SEARCH_FORM_CLASSNAME}
        onSubmit={this.handleSearchFormSubmit}
        onReset={this.handleSearchFormReset}
      >
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          {this.renderTemplateItem()}
          {this.renderActualWordItem()}
          {this.renderWordItem()}
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

  render() {
    const { isTenantRoleLevel } = this.props;
    if (isTenantRoleLevel) {
      return this.renderTenantSearchForm();
    } else {
      return this.renderSiteSearchForm();
    }
  }
}
