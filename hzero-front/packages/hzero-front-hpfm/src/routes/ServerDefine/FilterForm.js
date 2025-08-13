/**
 * FilterForm - 服务器定义-查询条件表单
 * @date: 2019-7-1
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { filterNullValueObject } from 'utils/utils';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

const { Option } = Select;
/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = { expandForm: true };
  }

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields((err, values) => {
        if (!err) {
          const params = filterNullValueObject(values);
          onSearch(params);
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

  @Bind()
  handleToggle() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const {
      form: { getFieldDecorator },
      typeList,
      isSiteFlag,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              {isSiteFlag && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('entity.tenant.tag').d('租户')}
                  >
                    {getFieldDecorator('tenantId', {})(<Lov code="HPFM.TENANT" />)}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.serverName')
                    .d('服务器名称')}
                >
                  {getFieldDecorator('serverName')(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl
                    .get('hpfm.serverDefine.model.serverDefine.protocolCode')
                    .d('服务器协议')}
                >
                  {getFieldDecorator('protocolCode')(
                    <Select allowClear>
                      {typeList.map(item => (
                        <Option label={item.meaning} value={item.value} key={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              {!isSiteFlag && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hpfm.serverDefine.model.serverDefine.serverDescription')
                      .d('服务器说明')}
                  >
                    {getFieldDecorator('serverDescription')(<Input />)}
                  </Form.Item>
                </Col>
              )}
            </Row>
            {isSiteFlag && (
              <Row {...FORM_COL_3_LAYOUT} style={{ display: expandForm ? 'none' : '' }}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hpfm.serverDefine.model.serverDefine.serverDescription')
                      .d('服务器说明')}
                  >
                    {getFieldDecorator('serverDescription')(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
            )}
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              {isSiteFlag && (
                <Button onClick={this.handleToggle}>
                  {!expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get('hzero.common.button.viewMore').d('更多查询')}
                </Button>
              )}
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" onClick={this.handleSearch} htmlType="submit">
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
