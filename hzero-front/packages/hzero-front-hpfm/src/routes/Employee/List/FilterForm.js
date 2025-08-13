import React, { PureComponent } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import { SEARCH_FORM_ITEM_LAYOUT, FORM_COL_4_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

/**
 * 员工定义-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 表单查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hpfm/hr/staff' })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expand: false,
    };
  }

  /**
   * 表单查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
          // 如果验证成功,则执行onSearch
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
  toggleMore() {
    this.setState({ expand: !this.state.expand });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { form = {} /* , flexFieldsTriggers */, customizeFilterForm } = this.props;
    const { getFieldDecorator } = form;

    const { expand } = this.state;
    return customizeFilterForm(
      { code: 'HPFM.EMPLOYEE_DEFINITION.HEADER_FILTER', form, expand },
      <Form className="more-fields-search-form">
        <Row>
          <Col span={18}>
            <Row>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('entity.employee.code').d('员工编码')}
                >
                  {getFieldDecorator('employeeNum', {})(<Input trim inputChinese={false} />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('entity.employee.name').d('员工姓名')}
                >
                  {getFieldDecorator('name', {})(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <Form.Item
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('entity.employee.userId').d('用户')}
                >
                  {getFieldDecorator('userId')(<Lov code="HIAM.USER.ORG" />)}
                </Form.Item>
              </Col>
            </Row>
          </Col>
          <Col span={6} className="search-btn-more">
            <Form.Item>
              <Button data-code="reset" onClick={this.toggleMore}>
                {expand
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
      </Form>
    );
  }
}
