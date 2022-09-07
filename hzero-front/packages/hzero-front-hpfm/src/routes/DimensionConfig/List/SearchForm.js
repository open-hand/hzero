/**
 * DimensionConfig - 数据维度配置-查询条件表单
 * @date: 2019-7-16
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Button, Col, Form, Input, Row } from 'hzero-ui';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { FORM_COL_4_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/data-dimension-config/list' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    const { onRef } = props;
    if (onRef) {
      onRef(this);
    }
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
   * 重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { form } = this.props;
    const isSite = !isTenantRoleLevel();
    return (
      <Form>
        <Row type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.dimensionConfig.model.header.dimensionCode').d('维度代码')}
            >
              {form.getFieldDecorator('lovCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.dimensionConfig.model.header.dimensionName').d('维度名称')}
            >
              {form.getFieldDecorator('lovName')(<Input />)}
            </Form.Item>
          </Col>
          {isSite && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.valueList.model.header.tenantName').d('所属租户')}
              >
                {form.getFieldDecorator('tenantId')(
                  <Lov code="HPFM.TENANT" textField="tenantName" />
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item>
              <Button onClick={this.handleFormReset} style={{ marginRight: 8 }}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
