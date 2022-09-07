/**
 * FilterForm 查询表单
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @date 2019-06-17
 * @copyright 2018 © HAND
 */

import React from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

import { getDateTimeFormat } from 'utils/utils';
import intl from 'utils/intl';
import {
  DEFAULT_DATETIME_FORMAT,
  DEFAULT_TIME_FORMAT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const dateTimeFormat = getDateTimeFormat();
@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      hidden: true,
    };
  }

  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  @Bind()
  handleSearch() {
    const { form, search } = this.props;
    const fieldValues = form.getFieldsValue();
    fieldValues.refundDatetimeFrom =
      fieldValues.refundDatetimeFrom &&
      fieldValues.refundDatetimeFrom.format(DEFAULT_DATETIME_FORMAT);
    fieldValues.refundDatetimeTo =
      fieldValues.refundDatetimeTo && fieldValues.refundDatetimeTo.format(DEFAULT_DATETIME_FORMAT);
    search(fieldValues);
  }

  @Bind()
  handleToggle() {
    const { hidden } = this.state;
    this.setState({
      hidden: !hidden,
    });
  }

  render() {
    const { Option } = Select;
    const {
      form: { getFieldDecorator, getFieldValue },
      channelList,
      statusList,
    } = this.props;
    const { hidden } = this.state;
    return (
      <Form>
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpay.refundOrder.model.refundOrder.channelCode').d('支付渠道')}
            >
              {getFieldDecorator('channelCode')(
                <Select allowClear>
                  {channelList.map(item => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpay.refundOrder.model.refundOrder.merchantOrderNum')
                .d('商户订单号')}
            >
              {getFieldDecorator('merchantOrderNum', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status').d('状态')}
            >
              {getFieldDecorator('status')(
                <Select allowClear>
                  {statusList.map(item => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.handleToggle}>
                {hidden
                  ? intl.get('hzero.common.button.viewMore').d('更多查询')
                  : intl.get('hzero.common.button.collected').d('收起查询')}
              </Button>
              <Button onClick={this.handleReset}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" onClick={this.handleSearch} htmlType="submit">
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          {...SEARCH_FORM_ROW_LAYOUT}
          style={hidden ? { display: 'none' } : {}}
          type="flex"
          gutter={24}
          align="bottom"
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hpay.refundOrder.model.refundOrder.paymentOrderNum').d('支付订单号')}
            >
              {getFieldDecorator('paymentOrderNum', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpay.refundOrder.model.refundOrder.refundDatetimeFrom')
                .d('退款时间从')}
            >
              {getFieldDecorator('refundDatetimeFrom')(
                <DatePicker
                  style={{ width: '100%' }}
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  disabledDate={currentDate =>
                    getFieldValue('refundDatetimeTo') &&
                    moment(getFieldValue('refundDatetimeTo')).isBefore(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl
                .get('hpay.refundOrder.model.refundOrder.refundDatetimeTo')
                .d('退款时间至')}
            >
              {getFieldDecorator('refundDatetimeTo')(
                <DatePicker
                  style={{ width: '100%' }}
                  format={dateTimeFormat}
                  showTime={{ format: DEFAULT_TIME_FORMAT }}
                  placeholder=""
                  disabledDate={currentDate =>
                    getFieldValue('refundDatetimeFrom') &&
                    moment(getFieldValue('refundDatetimeFrom')).isAfter(currentDate, 'day')}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
