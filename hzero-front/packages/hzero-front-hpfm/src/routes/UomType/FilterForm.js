/*
 * FilterForm - 计量单位类型定义查询表单
 * @date: 2018/08/07 14:30:36
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isFunction } from 'lodash';

import intl from 'utils/intl';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const promptCode = 'hpfm.uomType';
/**
 * 计量单位查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch // 搜索
 * @reactProps {Function} handleFormReset // 重置表单
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  componentDidMount() {
    const { onRef } = this.props;
    if (isFunction(onRef)) {
      onRef(this);
    }
  }

  /**
   * 查询列表
   */
  @Bind()
  handleSearch() {
    const { onFilterChange, form } = this.props;
    if (onFilterChange) {
      form.validateFields(err => {
        if (!err) {
          onFilterChange();
        }
      });
    }
  }

  /**
   * 重置表单数据
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator },
    } = this.props;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.uomType.uomTypeCode`).d('单位类型代码')}
            >
              {getFieldDecorator('uomTypeCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`${promptCode}.model.uomType.uomTypeName`).d('单位类型名称')}
            >
              {getFieldDecorator('uomTypeName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button style={{ marginRight: 8 }} onClick={this.handleFormReset}>
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
