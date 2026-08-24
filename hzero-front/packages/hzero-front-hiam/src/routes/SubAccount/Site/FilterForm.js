/**
 * FilterForm.js
 * @date 2018-12-15
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import cacheComponent from 'components/CacheComponent';
import Checkbox from 'components/Checkbox';
import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';
import { getDateFormat } from 'utils/utils';

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hiam/sub-account-site' })
export default class FilterForm extends React.Component {
  static propTypes = {
    onRef: PropTypes.func.isRequired,
    onSearch: PropTypes.func.isRequired,
  };

  state = {
    advanceForm: false,
  };

  componentDidMount() {
    const { onRef } = this.props;
    onRef(this);
  }

  render() {
    return this.renderAdvanceForm();
  }

  renderAdvanceForm() {
    const { getFieldDecorator, getFieldValue } = this.props.form;
    const { defaultTenant, advanceForm } = this.state;
    const { userType } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.loginName').d('账号')}
            >
              {getFieldDecorator('loginName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.userType').d('用户类型')}
            >
              {getFieldDecorator('userType', {
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
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.phone').d('手机号码')}
            >
              {getFieldDecorator('phone')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button
                style={{ display: advanceForm ? 'inline-block' : 'none' }}
                onClick={this.toggleForm}
              >
                {intl.get('hzero.common.button.collected').d('收起查询')}
              </Button>
              <Button
                style={{ display: advanceForm ? 'none' : 'inline-block' }}
                onClick={this.toggleForm}
              >
                {intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row style={{ display: advanceForm ? '' : 'none' }} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.email').d('邮箱')}
            >
              {getFieldDecorator('email')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.realName').d('名称')}
            >
              {getFieldDecorator('realName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.tenant').d('所属租户')}
            >
              {getFieldDecorator('organizationId')(<Lov code="HPFM.TENANT" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.user.defaultTenant').d('默认租户')}
            >
              {getFieldDecorator('defaultTenant', {
                initialValue: defaultTenant === undefined ? 0 : 1,
              })(<Checkbox />)}
            </Form.Item>
          </Col>
        </Row>
        <Row style={{ display: advanceForm ? 'block' : 'none' }} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.from').d('有效日期从')}
            >
              {getFieldDecorator('startDateActive')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateFormat()}
                  disabledDate={(currentDate) =>
                    getFieldValue('endDateActive') &&
                    moment(getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.date.active.to').d('有效日期至')}
            >
              {getFieldDecorator('endDateActive')(
                <DatePicker
                  style={{ width: '100%' }}
                  placeholder=""
                  format={getDateFormat()}
                  disabledDate={(currentDate) =>
                    getFieldValue('startDateActive') &&
                    moment(getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                  }
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  @Bind()
  toggleForm() {
    const { advanceForm = false } = this.state;
    this.setState({
      advanceForm: !advanceForm,
    });
  }

  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSearch() {
    const { onSearch } = this.props;
    onSearch();
  }
}
