/*
 * Zuul限流配置表单
 * @date: 2018/08/07 14:57:58
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

/**
 * Zuul限流配置表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch  搜索
 * @reactProps {Function} handleFormReset  重置表单
 * @reactProps {Function} toggleForm  展开查询条件
 * @reactProps {Function} renderAdvancedForm 渲染所有查询条件
 * @reactProps {Function} renderSimpleForm 渲染缩略查询条件
 * @return React.element
 */
// const messagePrompt = 'spfm.invitationList.view.message';
const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hadm/rate-limit' })
export default class ListFilter extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
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

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  /**
   * 多查询条件展示
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
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
  renderForm() {
    const { expandForm } = this.state;
    const {
      form: { getFieldDecorator },
      limitTypes = [],
      refreshStatus = [],
    } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitKey`).d('代码')}
            >
              {getFieldDecorator('rateLimitKey')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.rateLimitType`).d('限流方式')}
            >
              {getFieldDecorator('rateLimitType')(
                <Select>
                  {limitTypes.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.explain').d('说明')}
            >
              {getFieldDecorator('remark')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
        <Row
          type="flex"
          align="bottom"
          gutter={24}
          {...SEARCH_FORM_ROW_LAYOUT}
          style={{ display: expandForm ? '' : 'none' }}
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.serviceName`).d('网关服务')}
            >
              {getFieldDecorator('serviceId')(<Lov code="HADM.SERVICE" textField="serviceName" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.zuulRateLimit.model.zuulRateLimit.refreshStatus`).d('刷新状态')}
            >
              {getFieldDecorator('refreshStatus')(
                <Select style={{ width: '100%' }} allowClear>
                  {refreshStatus.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  render() {
    return <div className="table-list-search">{this.renderForm()}</div>;
  }
}
