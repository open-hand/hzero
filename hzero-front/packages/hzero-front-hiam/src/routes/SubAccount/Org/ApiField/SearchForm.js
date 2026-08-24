/**
 * 接口字段权限维护 /hiam/sub-account-org/api/:usedId
 * SearchForm
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-10
 * @copyright 2019-07-10 © HAND
 */
import React from 'react';
import PropTypes from 'prop-types';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';

import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import { isTenantRoleLevel } from 'utils/utils';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const expandFormStyle = {};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hiam/sub-account-org/api/search-form' })
export default class SearchForm extends React.Component {
  static propTypes = {
    onSearch: PropTypes.func.isRequired,
  };

  constructor(props) {
    super(props);
    this.state = {
      expandForm: false,
    };
  }

  @Bind()
  handleResetBtnClick(e) {
    e.preventDefault();
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSearchBtnClick(e) {
    e.preventDefault();
    const { onSearch } = this.props;
    onSearch();
  }

  @Bind()
  handleToggleForm() {
    const { expandForm = false } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const { form, requestMethod = [] } = this.props;
    const { expandForm = false } = this.state;
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.api.serviceName').d('服务名')}
            >
              {form.getFieldDecorator('serviceName')(
                <Lov
                  allowClear
                  code={
                    VERSION_IS_OP || isTenantRoleLevel()
                      ? 'HADM.ROUTE.SERVICE_CODE.ORG'
                      : 'HADM.ROUTE.SERVICE_CODE'
                  }
                  textField="serviceName"
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.api.method').d('请求方式')}
            >
              {form.getFieldDecorator('method')(
                <Select allowClear>
                  {requestMethod.map(item => (
                    <Select.Option key={item.value} value={item.value}>
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
              label={intl.get('hiam.subAccount.model.api.path').d('请求路径')}
            >
              {form.getFieldDecorator('path')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.handleToggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleResetBtnClick}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearchBtnClick}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.subAccount.model.api.description').d('请求描述')}
            >
              {form.getFieldDecorator('description')(<Input />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
