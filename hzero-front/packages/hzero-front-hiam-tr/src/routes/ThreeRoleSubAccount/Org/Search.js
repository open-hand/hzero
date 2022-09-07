/*
 * Search - 租户级子账户查询表单
 * @date: 2018/11/19 20:03:51
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Button, Input, Col, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import cacheComponent from 'components/CacheComponent';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

/**
 * 租户级子账户查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch  搜索
 * @reactProps {Function} handleFormReset  重置表单
 * @reactProps {Function} renderAdvancedForm 渲染所有查询条件
 * @reactProps {Function} renderSimpleForm 渲染缩略查询条件
 * @return React.element
 */
const FormItem = Form.Item;
// @formatterCollections({ code: 'spfm.invitationList' })
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hiam/tr-account/org/list' })
export default class FilterForm extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  // 查询条件展开/收起
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onFilterChange } = this.props;
    if (onFilterChange) {
      onFilterChange();
    }
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 渲染查询条件
   * @returns React.component
   */
  @Bind()
  renderSearchForm() {
    const { form, userType, customizeFilterForm } = this.props;
    const { expandForm } = this.state;
    return customizeFilterForm(
      { code: 'HIAM.SUB_ACCOUND.FILTER', form, expand: expandForm },
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col span={18}>
            <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.user.loginName').d('账号')}
                >
                  {form.getFieldDecorator('loginName')(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.user.userType').d('用户类型')}
                >
                  {form.getFieldDecorator('userType', {
                    initialValue: 'P',
                  })(
                    <Select
                      onChange={() => {
                        setTimeout(() => {
                          this.handleSearch();
                        }, 0);
                      }}
                    >
                      {userType.map((item) => (
                        <Select.Option value={item.value} key={item.value}>
                          {item.meaning}
                        </Select.Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
                >
                  {form.getFieldDecorator('phone')(<Input />)}
                </FormItem>
              </Col>
            </Row>
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
                >
                  {form.getFieldDecorator('email')(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
                >
                  {form.getFieldDecorator('realName')(<Input />)}
                </FormItem>
              </Col>
            </Row>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button
                style={{ display: expandForm ? 'none' : 'inline-block' }}
                onClick={this.toggleForm}
              >
                {intl.get(`hzero.common.button.viewMore`).d('更多查询')}
              </Button>
              <Button
                style={{ display: expandForm ? 'inline-block' : 'none' }}
                onClick={this.toggleForm}
              >
                {intl.get('hzero.common.button.collected').d('收起查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    return <div className="table-list-search">{this.renderSearchForm()}</div>;
  }
}
