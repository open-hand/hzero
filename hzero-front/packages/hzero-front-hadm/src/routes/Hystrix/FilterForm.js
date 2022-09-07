/*
 * FilterForm - 熔断设置查询表单
 * @date: 2018/09/11 10:44:00
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import cacheComponent from 'components/CacheComponent';
import intl from 'utils/intl';
import { Bind, Throttle } from 'lodash-decorators';
import { map } from 'lodash';
import { DEBOUNCE_TIME, FORM_COL_4_LAYOUT, SEARCH_FORM_ITEM_LAYOUT } from 'utils/constants';

const { Option } = Select;

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

/**
 * 熔断保护设置查询表单
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} handleSearch //搜索
 * @reactProps {Function} handleFormReset //重置表单
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
@cacheComponent({ cacheKey: '/hadm/hystrix/list' })
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

  /**
   * 多查询条件展示
   */
  @Throttle(DEBOUNCE_TIME)
  @Bind()
  toggleForm() {
    const { expandForm } = this.state;
    this.setState({
      expandForm: !expandForm,
    });
  }

  /**
   *  表单查询
   */
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

  /**
   *  表单重置
   */
  @Bind()
  handleFormReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const {
      form: { getFieldDecorator },
      confTypeCodeList,
      refreshStatus = [],
    } = this.props;
    const { expandForm = true } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.hystrix.model.hystrix.confTypeCode`).d(`代码`)}
            >
              {getFieldDecorator('confTypeCode')(
                <Input trim typeCase="upper" inputChinese={false} />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.hystrix.model.hystrix.confKey`).d(`类型`)}
            >
              {getFieldDecorator('confKey')(
                <Select style={{ width: '100%' }} allowClear>
                  {map(confTypeCodeList, e => (
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
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.hystrix.model.hystrix.remark`).d(`描述`)}
            >
              {getFieldDecorator('remark')(<Input />)}
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
                {intl.get(`hzero.common.button.reset`).d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get(`hzero.common.button.search`).d('查询')}
              </Button>
            </Form.Item>
          </Col>
        </Row>
        <Row
          type="flex"
          gutter={24}
          align="bottom"
          style={expandForm ? expandFormStyle : noExpandFormStyle}
        >
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.hystrix.model.hystrix.serviceName`).d(`服务`)}
            >
              {getFieldDecorator('serviceName')(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get(`hadm.hystrix.model.hystrix.refreshStatus`).d(`刷新状态`)}
            >
              {getFieldDecorator('refreshStatus')(
                <Select allowClear>
                  {map(refreshStatus, e => (
                    <Option value={e.value} key={e.value}>
                      {e.meaning}
                    </Option>
                  ))}
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
