import React, { PureComponent, Fragment } from 'react';
import { Form, Button, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import intl from 'utils/intl';

import { getCurrentOrganizationId } from 'utils/utils';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_CLASSNAME,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
/**
 * 成本中心
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} onSearch - 表单查询
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      tenantId: getCurrentOrganizationId(),
    };
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
          // 如果验证成功,则执行onSearch
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
    this.props.form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const { tenantId } = this.state;
    const { getFieldDecorator } = this.props.form;
    return (
      <Fragment>
        <Form className={SEARCH_FORM_CLASSNAME}>
          <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.costCenter.model.costCenter.code').d('成本中心编码')}
              >
                {getFieldDecorator('costCode', {})(<Input typeCase="upper" inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.costCenter.model.costCenter.desc').d('成本中心描述')}
              >
                {getFieldDecorator('costName', {})(<Input />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hpfm.costCenter.model.costCenter.ouName').d('业务实体')}
              >
                {getFieldDecorator(
                  'ouId',
                  {}
                )(<Lov code="HPFM.USER_AUTH.OU" queryParams={{ tenantId }} />)}
              </Form.Item>
            </Col>
            <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
              <Form.Item>
                <Button onClick={this.handleFormReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                  {intl.get('hzero.common.button.search').d('查询')}
                </Button>
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Fragment>
    );
  }
}
