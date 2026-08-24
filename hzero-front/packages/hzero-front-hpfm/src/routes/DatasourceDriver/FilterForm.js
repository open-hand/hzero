/**
 * dataSourceDriver-数据源驱动
 * @date: 2019-08-22
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */

import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Select, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import CacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

@Form.create({ fieldNameProp: null })
@CacheComponent({ cacheKey: '/hpfm/datasource-driver' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      hidden: true,
    };
  }

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  /**
   * 表单重置
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { dataSourceTypeList, isSiteFlag } = this.props;
    const { getFieldDecorator } = this.props.form;
    const { hidden } = this.state;
    return (
      <Fragment>
        <Form className="more-fields-search-form">
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            {isSiteFlag && (
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpfm.dataSourceDriver.model.dataSourceDriver.tenantName')
                    .d('租户名称')}
                >
                  {getFieldDecorator('tenantId')(<Lov allowClear code="HPFM.TENANT" />)}
                </Form.Item>
              </Col>
            )}
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hpfm.dataSourceDriver.model.dataSourceDriver.driverName')
                  .d('驱动名称')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('driverName')(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl
                  .get('hpfm.dataSourceDriver.model.dataSourceDriver.databaseType')
                  .d('驱动类型')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('databaseType')(
                  <Select allowClear>
                    {dataSourceTypeList.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            {!isSiteFlag && (
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hpfm.dataSourceDriver.model.dataSourceDriver.description')
                    .d('描述')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('description')(<Input />)}
                </Form.Item>
              </Col>
            )}
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item>
                {isSiteFlag && (
                  <Button onClick={this.handleToggle}>
                    {hidden
                      ? intl.get('hzero.common.button.viewMore').d('更多查询')
                      : intl.get('hzero.common.button.collected').d('收起查询')}
                  </Button>
                )}
                <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  style={{ marginLeft: 8 }}
                  htmlType="submit"
                  type="primary"
                  onClick={this.handleSearch}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
          {isSiteFlag && (
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={hidden ? { display: 'none' } : { display: '' }}>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  label={intl
                    .get('hpfm.dataSourceDriver.model.dataSourceDriver.description')
                    .d('描述')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('description')(<Input />)}
                </Form.Item>
              </Col>
            </Row>
          )}
        </Form>
      </Fragment>
    );
  }
}
