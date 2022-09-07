/**
 * 系统管理--模板维护
 * @date 2019-6-26
 * @author: XL <liang.xiong@hand-china.com>
 */
import React, { Component } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onStore - 存储表单值
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class QueryForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  // 提交查询表单
  @Bind()
  handleFormSearch() {
    const { form, onSearch, onStore } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onSearch();
        onStore(values);
      }
    });
  }

  // 重置表单
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  render() {
    const { dataTenantLevel = [], isTenantRoleLevel = false } = this.props;
    const { getFieldDecorator } = this.props.form;
    if (isTenantRoleLevel) {
      return (
        <Form>
          <Row type="flex" align="bottom" gutter={24}>
            <Col span={6}>
              <FormItem
                label={intl
                  .get('hpfm.portalTemplate.model.portalTemplate.templateName')
                  .d('模板名称')}
              >
                {getFieldDecorator('templateName')(<Input />)}
              </FormItem>
            </Col>
            <Col span={6}>
              <FormItem>
                <Button onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button
                  htmlType="submit"
                  type="primary"
                  style={{ marginLeft: 8 }}
                  onClick={this.handleFormSearch}
                >
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </FormItem>
            </Col>
          </Row>
        </Form>
      );
    }
    return (
      <Form className={SEARCH_FORM_CLASSNAME}>
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem {...SEARCH_FORM_ITEM_LAYOUT} label={intl.get('entity.tenant.tag').d('租户')}>
              {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.hpfmTemplate.model.portalTemplate.templateLevel').d('层级')}
            >
              {getFieldDecorator('templateLevelCode')(
                <Select allowClear>
                  {dataTenantLevel.map((item) => (
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
              label={intl
                .get('hpfm.portalTemplate.model.portalTemplate.templateName')
                .d('模板名称')}
            >
              {getFieldDecorator('templateName')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleFormSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
