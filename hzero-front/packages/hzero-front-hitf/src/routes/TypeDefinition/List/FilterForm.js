/**
 * FilterForm - 应用类型定义-列表页查询条件表单
 * @date: 2019-8-22
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import Lov from 'components/Lov';
import cacheComponent from 'components/CacheComponent';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hitf/application-type-definition/list' })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  state = {
    expandForm: false, // 是否展开
  };

  /**
   * 切换应用大类
   * @param {string} value - lov选中值
   */
  @Bind()
  handleChangeMajorCategory(value) {
    const {
      form: { setFieldsValue = () => {}, getFieldValue = () => {} },
    } = this.props;
    if (getFieldValue('majorCategory') !== value) {
      setFieldsValue({ minorCategory: undefined });
    }
  }

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          onSearch();
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

  /**
   * 多查询条件展示
   */
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e, getFieldValue = () => {} },
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.applicationCode')
                .d('应用代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('applicationCode', {
                rules: [
                  {
                    max: 80,
                    message: intl.get('hzero.common.validation.max', {
                      max: 80,
                    }),
                  },
                ],
              })(<Input typeCase="upper" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hitf.typeDefinition.model.typeDefinition.name').d('应用名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('applicationName', {
                rules: [
                  {
                    max: 255,
                    message: intl.get('hzero.common.validation.max', {
                      max: 255,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hitf.typeDefinition.model.typeDefinition.interfaceId').d('开放接口')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceId')(<Lov code="HITF.COMPOSE_ENTRY_INTERFACE" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button data-code="reset" onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button
                data-code="search"
                type="primary"
                htmlType="submit"
                onClick={this.handleSearch}
              >
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.majorCategory')
                .d('应用大类')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('majorCategory')(
                <Lov code="HITF.APP_MAJOR_CATEGORY" onChange={this.handleChangeMajorCategory} />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl
                .get('hitf.typeDefinition.model.typeDefinition.minorCategory')
                .d('应用小类')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('minorCategory')(
                <Lov
                  disabled={!getFieldValue('majorCategory')}
                  code="HITF.APP_MINOR_CATEGORY"
                  queryParams={{ parentValue: getFieldValue('majorCategory') }}
                />
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
