/*
 * Search - 服务注册查找
 * @date: 2018/10/29 14:03:38
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Form, Input, Button, Select, Row, Col } from 'hzero-ui';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import { Bind, Throttle } from 'lodash-decorators';
import {
  DEBOUNCE_TIME,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;
const { Option } = Select;

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class Search extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: (this.props.dataSource || {}).expandForm,
      tenantName: '',
    };
  }

  /**
   * 查询
   */
  onClick() {
    const { tenantName, expandForm } = this.state;
    const { fetchList = (e) => e, setCacheQueryParams = () => {}, form = {} } = this.props;
    const { getFieldsValue = () => {} } = form;
    fetchList();
    setCacheQueryParams({ ...getFieldsValue(), tenantName, expandForm });
  }

  /**
   * 重置表单
   */
  onReset() {
    const {
      form: { resetFields = (e) => e },
      setCacheQueryParams = () => {},
    } = this.props;
    resetFields();
    setCacheQueryParams({});
  }

  componentWillUnmount() {
    this.toggleForm.cancel();
  }

  /**
   * 展开 收起 表单
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  @Bind()
  onTenantIdChange(val, record) {
    this.setState({
      tenantName: record.tenantName,
    });
  }

  render() {
    const {
      form = {},
      serviceTypes = [],
      tenantRoleLevel,
      dataSource = {},
      serviceCategoryTypes = [],
      statusList = [],
    } = this.props;
    const { expandForm } = this.state;
    const { getFieldDecorator = (e) => e } = form;
    const enabledFlagList = [
      { value: 0, meaning: intl.get('hzero.common.status.disable').d('禁用') },
      { value: 1, meaning: intl.get('hzero.common.status.enable').d('启用') },
    ];
    const {
      serverName,
      enabledFlag,
      serverCode,
      tenantId,
      serviceType,
      tenantName,
      serviceCategory,
      namespace,
      status,
    } = dataSource;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.services.model.services.code').d('服务代码')}
                >
                  {getFieldDecorator('serverCode', {
                    initialValue: serverCode,
                  })(<Input typeCase="upper" inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.services.model.services.name').d('服务名称')}
                >
                  {getFieldDecorator('serverName', {
                    initialValue: serverName,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.services.model.services.type').d('服务类型')}
                >
                  {getFieldDecorator('serviceType', {
                    initialValue: serviceType,
                  })(
                    <Select allowClear>
                      {serviceTypes.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row
              {...SEARCH_FORM_ROW_LAYOUT}
              style={expandForm ? expandFormStyle : noExpandFormStyle}
            >
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.services.model.services.namespace').d('服务命名空间')}
                >
                  {getFieldDecorator('namespace', {
                    initialValue: namespace,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hitf.services.model.services.category').d('服务类别')}
                >
                  {getFieldDecorator('serviceCategory', {
                    initialValue: serviceCategory,
                  })(
                    <Select allowClear>
                      {serviceCategoryTypes.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hmsg.common.view.enabledFlag').d('启用标识')}
                >
                  {getFieldDecorator('enabledFlag', {
                    initialValue: enabledFlag,
                  })(
                    <Select allowClear>
                      {enabledFlagList.map((n) => (
                        <Option key={n.value} value={n.value}>
                          {n.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row
              {...SEARCH_FORM_ROW_LAYOUT}
              style={expandForm ? expandFormStyle : noExpandFormStyle}
            >
              {!tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <FormItem
                    {...SEARCH_FORM_ITEM_LAYOUT}
                    label={intl.get('hitf.services.model.services.tenant').d('所属租户')}
                  >
                    {getFieldDecorator('tenantId', {
                      initialValue: tenantId,
                    })(
                      <Lov
                        code="HPFM.TENANT"
                        textValue={tenantName}
                        onChange={this.onTenantIdChange}
                      />
                    )}
                  </FormItem>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <FormItem
                  {...SEARCH_FORM_ITEM_LAYOUT}
                  label={intl.get('hzero.common.status').d('状态')}
                >
                  {getFieldDecorator('status', {
                    initialValue: status,
                  })(
                    <Select allowClear>
                      {statusList.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
            </Row>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <FormItem>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.onReset.bind(this)}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.onClick.bind(this)}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
