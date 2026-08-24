import React, { PureComponent } from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import moment from 'moment';

import Lov from 'components/Lov';

import { getDateTimeFormat } from 'utils/utils';
import {
  DEBOUNCE_TIME,
  DEFAULT_TIME_FORMAT,
  FORM_COL_3_4_LAYOUT,
  FORM_COL_3_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';
import intl from 'utils/intl';

const { Option } = Select;

const dateTimeFormat = getDateTimeFormat();
/**
 * 数据集查询表单
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
          // 如果验证成功,则执行search
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
      form: { getFieldDecorator, getFieldValue },
      requestStatusList = [],
      tenantRoleLevel,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT}>
              {!tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('entity.tenant.tag').d('租户')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator(
                      'tenantId',
                      {}
                    )(<Lov code="HPFM.TENANT" textField="tenantName" />)}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hrpt.common.report.reportName').d('报表名称')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('reportName', {})(<Input />)}
                </Form.Item>
              </Col>
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hrpt.common.view.requestStatus').d('运行状态')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator(
                    'requestStatus',
                    {}
                  )(
                    <Select allowClear>
                      {requestStatusList.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </Form.Item>
              </Col>
              {tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hrpt.common.view.startTime').d('开始时间')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('startDate')(
                      <DatePicker
                        format={dateTimeFormat}
                        showTime={{ format: DEFAULT_TIME_FORMAT }}
                        placeholder=""
                        disabledDate={(currentDate) =>
                          getFieldValue('endDate') &&
                          moment(getFieldValue('endDate')).isBefore(currentDate, 'day')
                        }
                      />
                    )}
                  </Form.Item>
                </Col>
              )}
            </Row>
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
        <Row>
          <Col {...FORM_COL_3_4_LAYOUT}>
            <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
              {!tenantRoleLevel && (
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hrpt.common.view.startTime').d('开始时间')}
                    {...SEARCH_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('startDate')(
                      <DatePicker
                        format={dateTimeFormat}
                        showTime={{ format: DEFAULT_TIME_FORMAT }}
                        placeholder=""
                        disabledDate={(currentDate) =>
                          getFieldValue('endDate') &&
                          moment(getFieldValue('endDate')).isBefore(currentDate, 'day')
                        }
                      />
                    )}
                  </Form.Item>
                </Col>
              )}
              <Col {...FORM_COL_3_LAYOUT}>
                <Form.Item
                  label={intl.get('hrpt.common.view.endTime').d('结束时间')}
                  {...SEARCH_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('endDate')(
                    <DatePicker
                      format={dateTimeFormat}
                      showTime={{ format: DEFAULT_TIME_FORMAT }}
                      placeholder=""
                      disabledDate={(currentDate) =>
                        getFieldValue('startDate') &&
                        moment(getFieldValue('startDate')).isAfter(currentDate, 'day')
                      }
                    />
                  )}
                </Form.Item>
              </Col>
            </Row>
          </Col>
        </Row>
      </Form>
    );
  }
}
