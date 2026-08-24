import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import classnames from 'classnames';

import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

// 使用 FormItem 组件
const FormItem = Form.Item;
// 使用 Option 组件
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    const { onRef } = props;
    if (onRef) {
      onRef(this);
    }
    this.state = {
      expandForm: false,
    };
  }

  /**
   * handleSearch - 查询
   * @param {function} [onSearch] - 查询方法
   */
  @Bind()
  handleSearch() {
    const { onSearch } = this.props;
    if (onSearch) {
      onSearch();
    }
  }

  /**
   * handleReset - 重置表单
   * @param {*} form - 表单对象
   */
  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * toggleForm - 查询条件展开/收起
   * @memberof Search
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
      typeList = [],
    } = this.props;
    return (
      <Form className={classnames('more-fields-form')}>
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.financialCode.model.financialCode.financialCode').d('代码')}
            >
              {getFieldDecorator('code')(<Input trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.financialCode.model.financialCode.financialName').d('名称')}
            >
              {getFieldDecorator('name')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.financialCode.model.financialCode.financialType').d('类型')}
            >
              {getFieldDecorator('type')(
                <Select mode="multiple">
                  {typeList.map((n) => (
                    <Option key={n.value} value={n.value}>
                      {n.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button htmlType="submit" type="primary" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
