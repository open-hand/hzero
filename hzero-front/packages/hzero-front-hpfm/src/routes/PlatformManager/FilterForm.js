import React, { PureComponent } from 'react';
import { Button, Col, DatePicker, Form, Input, Row, Select } from 'hzero-ui';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import { getDateTimeFormat } from 'utils/utils';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import { SEARCH_FORM_ROW_LAYOUT } from 'utils/constants';

const prefix = 'hpfm.login.audit.model';

@Form.create({ fieldNameProp: null })
export default class FilterForm extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      expandForm: false,
      timeFormat: getDateTimeFormat(),
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

  // 查询条件展开/收起
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({ expandForm: !expandForm });
  }

  render() {
    const { /* tenantId, */ form, typeList } = this.props;
    const { expandForm, timeFormat } = this.state;
    const formItemLayout = {
      labelCol: { span: 10 },
      wrapperCol: { span: 14 },
    };
    const { getFieldDecorator, getFieldValue, setFieldsValue, registerField } = form;
    return (
      <div className="table-list-search">
        <Form className="more-fields-search-form">
          <Row type="flex" align="bottom" gutter={24} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col span={6}>
              <Form.Item
                {...formItemLayout}
                label={intl.get(`${prefix}.auditTypeMeaning`).d('审计类型')}
              >
                {getFieldDecorator('auditType')(
                  <Select allowClear>
                    {typeList.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item {...formItemLayout} label={intl.get(`${prefix}.account`).d('账号')}>
                {getFieldDecorator('loginName', {})(<Input trim inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item {...formItemLayout} label={intl.get(`${prefix}.userName`).d('名称')}>
                {getFieldDecorator('userName', {})(<Input trim />)}
              </Form.Item>
            </Col>
            <Col span={6} className="search-btn-more">
              <Form.Item>
                <Button onClick={this.toggleForm}>
                  {expandForm
                    ? intl.get('hzero.common.button.collected').d('收起查询')
                    : intl.get(`hzero.common.button.viewMore`).d('更多查询')}
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
          <Row style={{ display: expandForm ? '' : 'none' }} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col span={6}>
              <Form.Item {...formItemLayout} label={intl.get(`${prefix}.phone`).d('手机号')}>
                {getFieldDecorator('phone', {})(<Input trim inputChinese={false} />)}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item {...formItemLayout} label={intl.get(`${prefix}.tenant`).d('所属租户')}>
                {getFieldDecorator(
                  'tenantName',
                  {}
                )(
                  <Lov
                    code="HPFM.TENANT"
                    onChange={(val, record) => {
                      registerField('tenantId');
                      setFieldsValue({ tenantId: record.tenantId });
                    }}
                    textValue={getFieldValue('tenantName')}
                  />
                )}
              </Form.Item>
            </Col>
            <Col span={6}>
              <Form.Item
                {...formItemLayout}
                label={intl.get(`${prefix}.login.time.after`).d('登录时间从')}
              >
                {getFieldDecorator('loginDateAfter')(
                  <DatePicker
                    showTime
                    placeholder=""
                    format={timeFormat}
                    disabledDate={(currentDate) =>
                      getFieldValue('loginDateBefore') &&
                      moment(getFieldValue('loginDateBefore')).isBefore(currentDate, 'second')
                    }
                  />
                )}
              </Form.Item>
            </Col>
          </Row>
          <Row style={{ display: expandForm ? '' : 'none' }} {...SEARCH_FORM_ROW_LAYOUT}>
            <Col span={6}>
              <Form.Item
                {...formItemLayout}
                label={intl.get(`${prefix}.login.time.before`).d('登录时间至')}
              >
                {getFieldDecorator('loginDateBefore')(
                  <DatePicker
                    showTime
                    placeholder=""
                    format={timeFormat}
                    disabledDate={(currentDate) =>
                      getFieldValue('loginDateAfter') &&
                      moment(getFieldValue('loginDateAfter')).isAfter(currentDate, 'second')
                    }
                  />
                )}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </div>
    );
  }
}
