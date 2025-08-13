import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind, Throttle } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

/**
 * 查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onStoreFormValues - 存储表单值
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
export default class QueryForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  // 提交查询表单
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    form.validateFields((err) => {
      if (isEmpty(err)) {
        onSearch();
      }
    });
  }

  // 重置表单
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  render() {
    const { serverTypeList, tenantRoleLevel, form } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col>
            <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={12} align="bottom">
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.smsConfig.model.smsConfig.serverTypeCode').d('服务类型')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('serverTypeCode')(
                    <Select allowClear>
                      {serverTypeList &&
                        serverTypeList.map((item) => (
                          <Option value={item.value} key={item.value}>
                            {item.meaning}
                          </Option>
                        ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.common.view.accountCode').d('账户代码')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('serverCode')(
                    <Input typeCase="upper" trim inputChinese={false} />
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT}>
                <FormItem
                  label={intl.get('hmsg.common.view.accountName').d('账户名称')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {form.getFieldDecorator('serverName')(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
                <Form.Item>
                  {!tenantRoleLevel && (
                    <Button onClick={this.toggleForm}>
                      {expandForm
                        ? intl.get('hzero.common.button.collected').d('收起查询')
                        : intl.get('hzero.common.button.viewMore').d('更多查询')}
                    </Button>
                  )}
                  <Button onClick={this.handleFormReset}>
                    {intl.get('hzero.common.button.reset').d('重置')}
                  </Button>
                  <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                </Form.Item>
              </Col>
            </Row>
            {!tenantRoleLevel && (
              <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
                <Col {...FORM_COL_4_LAYOUT}>
                  <FormItem
                    label={intl.get('hmsg.smsConfig.model.smsConfig.tenant').d('租户')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {form.getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
                  </FormItem>
                </Col>
              </Row>
            )}
          </Col>
        </Row>
      </Form>
    );
  }
}
