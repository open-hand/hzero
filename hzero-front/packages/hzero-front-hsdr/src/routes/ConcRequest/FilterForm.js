import React, { PureComponent } from 'react';
import { Form, Button, Row, Col, Input, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

/**
 * 流程监控查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Object} form - 表单对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
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

  /**
   * 查询
   */
  @Bind()
  handleSearch() {
    const { onSearch, form } = this.props;
    if (onSearch) {
      form.validateFields((err) => {
        if (!err) {
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
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form: { getFieldDecorator },
      tenantRoleLevel,
      statusList = [],
      jobLogLdp = {},
    } = this.props;
    const { expandForm } = this.state;
    const { clientResultList = [] } = jobLogLdp;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.concRequest.model.concRequest.jobId').d('任务ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('taskId', {
                rules: [
                  {
                    pattern: /^[0-9]*$/,
                    message: intl.get('hsdr.concRequest.validation.digital').d('只能输入数字'),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.concRequest.model.concRequest.concCode').d('请求编码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('concCode')(<Input trim inputChinese={false} typeCase="upper" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.concRequest.model.concRequest.concName').d('请求名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('concName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleFormReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('entity.tenant.tag').d('租户')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.concRequest.model.concRequest.jobStatus').d('状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('jobStatus')(
                <Select allowClear>
                  {statusList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.concRequest.model.concRequest.cycleFlag').d('周期性')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('cycleFlag')(
                <Select allowClear>
                  <Select.Option value={1} key="1">
                    {intl.get('hsdr.concRequest.model.concRequest.flagY').d('是')}
                  </Select.Option>
                  <Select.Option value={0} key="0">
                    {intl.get('hsdr.concRequest.model.concRequest.flagN').d('否')}
                  </Select.Option>
                </Select>
              )}
            </Form.Item>
          </Col>
          {tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hsdr.jobLog.model.jobLog.clientResult').d('客户端执行结果')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('clientResult')(
                  <Select allowClear>
                    {clientResultList.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
        {!tenantRoleLevel && (
          <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                label={intl.get('hsdr.jobLog.model.jobLog.clientResult').d('客户端执行结果')}
                {...SEARCH_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('clientResult')(
                  <Select allowClear>
                    {clientResultList.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
          </Row>
        )}
      </Form>
    );
  }
}
