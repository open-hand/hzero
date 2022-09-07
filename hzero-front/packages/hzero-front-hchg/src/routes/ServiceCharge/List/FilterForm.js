/**
 * FilterForm - 服务计费配置-查询条件表单
 * @date: 2019-8-28
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';
import cacheComponent from 'components/CacheComponent';

const FormItem = Form.Item;

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hchg/service-charge/list' })
export default class FilterForm extends Component {
  constructor(props) {
    super(props);
    props.onRef(this);
  }

  state = {
    expandForm: false, // 是否展开
  };

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields(err => {
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
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceCharge.model.serviceCharge.groupCode').d('计费组代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('groupCode', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input typeCase="upper" inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hchg.serviceCharge.model.serviceCharge.groupName').d('计费组名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('groupName', {
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
              label={intl.get('hchg.serviceCharge.model.serviceCharge.chargeService').d('服务代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('chargeService', {
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
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
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
      </Form>
    );
  }
}
