/*
 * Search - 接口监控查询
 * @date: 2018/09/17 15:40:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { map } from 'lodash';

import cacheComponent from 'components/CacheComponent';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getDateTimeFormat, isTenantRoleLevel } from 'utils/utils';
import {
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const { Option } = Select;

const expandFormStyle = {
  display: '',
};

const noExpandFormStyle = {
  display: 'none',
};

const tenantRoleLevel = isTenantRoleLevel();

@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hitf/interface-logs/list' })
export default class Search extends React.Component {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
    };
  }

  @Bind()
  handleSearch() {
    const { onFilterChange, form } = this.props;
    if (onFilterChange) {
      form.validateFields((err, values) => {
        if (!err) {
          onFilterChange(values);
        }
      });
    }
  }

  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  /**
   * 多查询条件展示
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
      form: { getFieldDecorator, getFieldValue },
    } = this.props;
    const { expandForm = true } = this.state;
    const enabledFlagArr = [
      { value: 'fail', meaning: intl.get('hitf.interfaceLogs.view.message.failed').d('失败') },
      { value: 'success', meaning: intl.get('hitf.interfaceLogs.view.message.success').d('成功') },
    ];
    const dateTimeFormat = getDateTimeFormat();
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeStart')
                .d('平台接口请求时间从')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('requestTimeStart', {
                initialValue: moment().subtract(1, 'months'),
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeStart')
                        .d('平台接口请求时间从'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  format={dateTimeFormat}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    getFieldValue('requestTimeEnd') &&
                    moment(getFieldValue('requestTimeEnd')).isBefore(currentDate, 'time')
                  }
                  style={{ width: '100%' }}
                  showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeEnd')
                .d('平台接口请求时间至')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('requestTimeEnd', {
                initialValue: moment(),
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hitf.interfaceLogs.model.interfaceLogs.requestTimeEnd')
                        .d('平台接口请求时间至'),
                    }),
                  },
                ],
              })(
                <DatePicker
                  format={dateTimeFormat}
                  placeholder=""
                  disabledDate={(currentDate) =>
                    getFieldValue('requestTimeStart') &&
                    moment(getFieldValue('requestTimeStart')).isAfter(currentDate, 'time')
                  }
                  style={{ width: '100%' }}
                  showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.serverCode').d('服务代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serverCode')(
                <Input typeCase="upper" trim inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
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
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.serverName').d('服务名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('serverName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.interfaceCode').d('接口代码')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceCode')(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.interfaceName').d('接口名称')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('interfaceName')(<Input />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.internal.respStatus')
                .d('平台接口响应状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('responseStatus')(
                <Select allowClear>
                  {map(enabledFlagArr, (e) => (
                    <Option value={e.value} key={e.value}>
                      {e.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl
                .get('hitf.interfaceLogs.model.interfaceLogs.invokeType')
                .d('接口调用类型')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('invokeType')(<Lov code="HITF.INVOKE_TYPE" />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.clientId').d('客户端ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('clientId')(<Input inputChinese={false} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          {!tenantRoleLevel && (
            <Col {...FORM_COL_4_LAYOUT}>
              <Form.Item
                {...SEARCH_FORM_ITEM_LAYOUT}
                label={intl.get('hitf.interfaceLogs.model.interfaceLogs.tenant').d('所属租户')}
              >
                {getFieldDecorator('tenantId')(<Lov code="HPFM.TENANT" />)}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hitf.interfaceLogs.model.interfaceLogs.invokeKey').d('请求ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('invokeKey')(<Input inputChinese={false} />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
