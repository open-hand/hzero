/**
 * FilterForm 服务器上传配置查询表单
 * @date: 2019-7-4
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
    this.state = {
      hidden: true,
    };
  }

  /**
   *更多查询按钮
   */
  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
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

  render() {
    const {
      form: { getFieldDecorator },
      isSiteFlag,
    } = this.props;
    const { hidden } = this.state;
    const enabledList = [
      { value: 1, meaning: intl.get('hzero.common.status.enable').d('启用') },
      { value: 0, meaning: intl.get('hzero.common.status.disable').d('禁用') },
    ];
    return (
      <Form className="more-fields-search-form">
        <Row>
          <Col span={24}>
            <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
              {isSiteFlag && (
                <Col {...FORM_COL_4_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.model.tenantName').d('租户')}
                  >
                    {getFieldDecorator('tenantId')(<Lov allowClear code="HPFM.TENANT" />)}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hfile.serverUpload.model.serverUpload.configCode').d('配置编码')}
                >
                  {getFieldDecorator('configCode')(
                    <Input trim typeCase="upper" inputChinese={false} />
                  )}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.status').d('状态')}
                >
                  {getFieldDecorator('enabledFlag')(
                    <Select allowClear>
                      {enabledList &&
                        enabledList.map(item => (
                          <Option value={item.value} key={item.value}>
                            {item.meaning}
                          </Option>
                        ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              {!isSiteFlag && (
                <Col {...FORM_COL_4_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hfile.serverUpload.model.serverUpload.description').d('描述')}
                  >
                    {getFieldDecorator('description')(<Input />)}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                <Form.Item>
                  {isSiteFlag && (
                    <Button onClick={this.handleToggle}>
                      {hidden
                        ? intl.get('hzero.common.button.viewMore').d('更多查询')
                        : intl.get('hzero.common.button.collected').d('收起查询')}
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
          </Col>
          {isSiteFlag && (
            <Col span={24}>
              <Row
                {...SEARCH_FORM_ROW_LAYOUT}
                style={hidden ? { display: 'none' } : { display: '' }}
                type="flex"
                gutter={24}
                align="bottom"
              >
                <Col {...FORM_COL_4_LAYOUT}>
                  <Form.Item
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hfile.serverUpload.model.serverUpload.description').d('描述')}
                  >
                    {getFieldDecorator('description')(<Input />)}
                  </Form.Item>
                </Col>
              </Row>
            </Col>
          )}
        </Row>
      </Form>
    );
  }
}
