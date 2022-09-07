import React from 'react';
import { Button, Col, Form, Input, Row, Select } from 'hzero-ui';
import { Bind, Throttle } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import {
  DEBOUNCE_TIME,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
} from 'utils/constants';

const FormItem = Form.Item;

const expandFormStyle = {
  display: '',
};
const noExpandFormStyle = {
  display: 'none',
};

@Form.create({ fieldNameProp: null })
export default class FilterForm extends React.PureComponent {
  constructor(props) {
    super(props);
    // 调用父组件 props onRef 方法
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
   * 查询操作
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    form.validateFields((err) => {
      if (isEmpty(err)) {
        onSearch(form);
      }
    });
  }

  /**
   * 重置操作
   */
  @Bind()
  handleReset() {
    const { form } = this.props;
    form.resetFields();
  }

  render() {
    const { form, sourceFlagList = [], jobStatusList = [] } = this.props;
    const { getFieldDecorator } = form;
    const { expandForm } = this.state;
    return (
      <Form className="more-fields-search-form">
        <Row type="flex" gutter={24} align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              label={intl.get('hsdr.jobInfo.model.jobInfo.id').d('任务ID')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('taskId', {
                rules: [
                  {
                    pattern: /^[0-9]*$/,
                    message: intl.get('hsdr.jobInfo.validation.digital').d('只能输入数字'),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <Form.Item
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hsdr.jobInfo.model.jobInfo.jobCode').d('任务编码')}
            >
              {getFieldDecorator('jobCode')(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hsdr.jobInfo.model.jobInfo.sourceFlag').d('来源标识')}
            >
              {getFieldDecorator('sourceFlag')(
                <Select allowClear>
                  {sourceFlagList.map((item) => (
                    <Select.Option label={item.meaning} value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className="search-btn-more">
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
              </Button>
              <Button onClick={this.handleReset}>
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
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hsdr.jobInfo.model.jobInfo.jobStatus').d('状态')}
            >
              {getFieldDecorator('jobStatus')(
                <Select allowClear>
                  {jobStatusList.map((item) => (
                    <Select.Option label={item.meaning} value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem label="JobHandler" {...SEARCH_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('jobHandler')(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hsdr.jobInfo.model.jobInfo.executorName').d('执行器')}
            >
              {getFieldDecorator('executorName')(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={expandForm ? expandFormStyle : noExpandFormStyle}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hsdr.jobInfo.model.jobInfo.jobDesc').d('任务描述')}
            >
              {getFieldDecorator('description')(<Input />)}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
