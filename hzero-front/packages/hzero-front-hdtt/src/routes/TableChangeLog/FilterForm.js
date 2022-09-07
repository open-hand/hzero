/**
 * FilterForm - 表结构变更-列表页查询条件表单
 * @date: 2019-7-18
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Bind } from 'lodash-decorators';
import { Form, Button, Input, Row, Col, Select } from 'hzero-ui';
import Lov from 'components/Lov';
import intl from 'utils/intl';
import {
  SEARCH_FORM_ITEM_LAYOUT,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_COL_CLASSNAME,
} from 'utils/constants';

const { Option } = Select;
const FormItem = Form.Item;
const prompt = 'hdtt.producerConfig.model.producerConfig';

/**
 * 查询表单
 * @extends {Component} - React.Component
 * @reactProps {Function} onSearch - 查询
 * @reactProps {Function} onRef - 绑定组件
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

  /**
   * 提交查询表单
   */
  @Bind()
  handleSearch() {
    const { form, onSearch } = this.props;
    if (onSearch) {
      form.validateFields(err => {
        if (!err) {
          onSearch();
        }
      });
    }
  }

  /**
   * 重置表单
   */
  @Bind()
  handleFormReset() {
    this.props.form.resetFields();
  }

  /**
   * 表单展开收起
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
      form: { getFieldDecorator = e => e },
      statusTypes,
    } = this.props;
    const { expandForm } = this.state;
    return (
      <Form layout="inline" className="more-fields-search-form">
        <Row {...SEARCH_FORM_ROW_LAYOUT} type="flex" align="bottom">
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${prompt}.producerService`).d('生产服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceService')(
                <Lov code="HDTT.SERVICE" textField="sourceService" />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`hdtt.tableChangeLog.model.tableChangeLog.sourceDs`).d('生产数据源')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceDs', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${prompt}.sourceDb`).d('生产DB')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceDb', {
                rules: [
                  {
                    max: 150,
                    message: intl.get('hzero.common.validation.max', {
                      max: 150,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <Form.Item>
              <Button onClick={this.toggleForm}>
                {expandForm
                  ? intl.get('hzero.common.button.collected').d('收起查询')
                  : intl.get('hzero.common.button.viewMore').d('更多查询')}
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
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${prompt}.tableName`).d('生产表名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('sourceTable', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input typeCase="lower" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${prompt}.consServiceName`).d('消费服务')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('targetService')(
                <Lov code="HDTT.SERVICE" textField="targetService" />
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`hdtt.tableChangeLog.model.tableChangeLog.targetDs`).d('消费数据源')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('targetDs', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
        </Row>
        <Row {...SEARCH_FORM_ROW_LAYOUT} style={{ display: expandForm ? '' : 'none' }}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem label={intl.get(`${prompt}.consDB`).d('消费DB')} {...SEARCH_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('targetDb', {
                rules: [
                  {
                    max: 150,
                    message: intl.get('hzero.common.validation.max', {
                      max: 150,
                    }),
                  },
                ],
              })(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get(`${prompt}.consTableName`).d('消费表名')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('targetTable', {
                rules: [
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input typeCase="lower" />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              label={intl.get('hzero.common.status').d('状态')}
              {...SEARCH_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('processStatus')(
                <Select allowClear>
                  {statusTypes.length &&
                    statusTypes.map(({ value, meaning }) => (
                      <Option key={value} value={value}>
                        {meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }
}
