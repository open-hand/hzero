/**
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-05-24
 * @copyright 2019-05-24 © HAND
 */

import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const expandFormRowStyle = {
  display: '',
};

const noExpandFormRowStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class SearchForm extends Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
    languageMessage: PropTypes.object.isRequired,
  };

  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
  }

  @Bind()
  handleToggleForm(e) {
    e.preventDefault();
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

  render() {
    const { languageMessage, form, modelAccuracy = [], isTenantRoleLevel } = this.props;
    const { expandForm = false } = this.state;
    return (
      <Form
        className={SEARCH_FORM_CLASSNAME}
        onSubmit={this.handleSearchFormSubmit}
        onReset={this.handleSearchFormReset}
      >
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          {!isTenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={languageMessage.model.template.tenantName}
              >
                {form.getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={languageMessage.model.template.templateCode}
            >
              {form.getFieldDecorator('templateCode')(
                <Input trim inputChinese={false} typeCase="upper" />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={languageMessage.model.template.templateName}
            >
              {form.getFieldDecorator('templateName')(<Input />)}
            </Form.Item>
          </Col>
          {isTenantRoleLevel ? (
            <>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.common._status}>
                  {form.getFieldDecorator('enabledFlag')(
                    <Select allowClear>
                      <Select.Option key={1} value={1}>
                        {languageMessage.common.status.enable}
                      </Select.Option>
                      <Select.Option key={0} value={0}>
                        {languageMessage.common.status.disable}
                      </Select.Option>
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
                <Form.Item>
                  <Button onClick={this.handleToggleForm}>
                    {expandForm
                      ? languageMessage.common.btn.collected
                      : languageMessage.common.btn.viewMore}
                  </Button>
                  <Button htmlType="reset">{languageMessage.common.btn.reset}</Button>
                  <Button type="primary" htmlType="submit">
                    {languageMessage.common.btn.search}
                  </Button>
                </Form.Item>
              </Col>
            </>
          ) : (
            <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button onClick={this.handleToggleForm}>
                  {expandForm
                    ? languageMessage.common.btn.collected
                    : languageMessage.common.btn.viewMore}
                </Button>
                <Button htmlType="reset">{languageMessage.common.btn.reset}</Button>
                <Button type="primary" htmlType="submit">
                  {languageMessage.common.btn.search}
                </Button>
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={expandForm ? expandFormRowStyle : noExpandFormRowStyle}
          type="flex"
          gutter={24}
          align="bottom"
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.model.template.maxGram}>
              {form.getFieldDecorator('maxGram')(
                <Select allowClear>
                  {modelAccuracy.map(item => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          {!isTenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item {...SEARCH_FORM_ITEM_LAYOUT} label={languageMessage.common._status}>
                {form.getFieldDecorator('enabledFlag')(
                  <Select allowClear>
                    <Select.Option key={1} value={1}>
                      {languageMessage.common.status.enable}
                    </Select.Option>
                    <Select.Option key={0} value={0}>
                      {languageMessage.common.status.disable}
                    </Select.Option>
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
