/**
 * FilterForm - Excel异步导出-查询条件表单
 * @date: 2019-8-7
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';

import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
} from 'utils/constants';

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends Component {
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

  render() {
    const {
      form: { getFieldDecorator },
      typeList = [],
    } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.excelAsyncExport.model.excelAsyncExport.taskCode')
                .d('任务编号')}
            >
              {getFieldDecorator('taskCode')(<Input trim typeCase="lower" inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpfm.excelAsyncExport.model.excelAsyncExport.taskName')
                .d('任务名称')}
            >
              {getFieldDecorator('taskName')(<Input trim typeCase="lower" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.excelAsyncExport.model.excelAsyncExport.state').d('任务状态')}
            >
              {getFieldDecorator('state')(
                <Select allowClear>
                  {typeList.map(item => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>

          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
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
