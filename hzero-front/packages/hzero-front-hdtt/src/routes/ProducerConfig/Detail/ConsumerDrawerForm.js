/**
 * ConsumerDrawer - 数据消费生产消费配置详情页-消费配置-配置表单
 * @date: 2019/4/16
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { Form, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';
import Switch from 'components/Switch';
import Lov from 'components/Lov';
import intl from 'utils/intl';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
const promptCode = 'hdtt.producerConfig.model.producerConfig';

/**
 * 列信息-数据修改滑窗(抽屉)
 * @extends {Component} - React.Component
 * @reactProps {Object} consumerDbConfig - 当前编辑行数据
 * @reactProps {Function} onChangeDb - 修改DB-LOV
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class ConsumerDrawerForm extends Component {
  constructor(props) {
    super(props);
    this.getFormValue = this.getFormValue.bind(this);
  }

  /**
   * state初始化
   */
  state = {
    consumerDs: undefined,
    consumerDsId: undefined,
  };

  /**
   * 收集表单值
   */
  @Bind()
  getFormValue() {
    const { consumerDbConfig, form } = this.props;
    const {
      consumerDs = consumerDbConfig.consumerDs,
      consumerDsId = consumerDbConfig.consumerDsId,
    } = this.state;
    let formValues = {};
    form.validateFields((err, values) => {
      if (!err) {
        formValues = { ...values, consumerDs, consumerDsId };
      }
    });
    return formValues;
  }

  /**
   * 修改消费服务
   */
  @Bind()
  onChangeConsumerService(val) {
    const {
      form: { setFieldsValue },
    } = this.props;
    setFieldsValue({ consumerDb: undefined });
    this.props.onChangeService(val);
  }

  /**
   * 修改消费DB
   * @param {string} val - 修改的值
   * @param {{initDsCode: string, initDsId: number}} record - 数据库相关值
   */
  @Bind()
  onChangeConsumerDB(val, { initDsCode, initDsId, initDbCode }) {
    this.setState(
      {
        consumerDs: initDsCode,
        consumerDsId: initDsId,
      },
      () => {
        this.props.onChangeDb(initDbCode, initDsId);
      }
    );
  }

  render() {
    const { consumerDbConfig = {}, form, dataSource = [] } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const isEdit = !isEmpty(consumerDbConfig);
    return (
      <Form>
        <Row type="flex">
          <Col span={12}>
            <Form.Item
              label={intl.get(`${promptCode}.consServiceName`).d('消费服务')}
              {...formLayout}
            >
              {getFieldDecorator('consumerService', {
                initialValue: consumerDbConfig.consumerService,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.consServiceName`).d('消费服务'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HDTT.SERVICE"
                  textValue={consumerDbConfig.consumerService}
                  onChange={this.onChangeConsumerService}
                  disabled={isEdit || (!isEdit && dataSource.length)}
                />
              )}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item label={intl.get(`${promptCode}.consDB`).d('消费DB')} {...formLayout}>
              {getFieldDecorator('consumerDb', {
                initialValue: consumerDbConfig.consumerDb,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.consDB`).d('消费DB'),
                    }),
                  },
                ],
              })(
                <Lov
                  textField="consumerDb"
                  disabled={
                    !getFieldValue('consumerService') || isEdit || (!isEdit && dataSource.length)
                  }
                  code="HDTT.SERVICE_DATABASE"
                  queryParams={{ serviceName: getFieldValue('consumerService') }}
                  onChange={this.onChangeConsumerDB}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row type="flex">
          <Col span={12}>
            <Form.Item
              label={intl.get(`${promptCode}.consTableName`).d('消费表名')}
              {...formLayout}
            >
              {getFieldDecorator('consumerTable', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get(`${promptCode}.consTableName`).d('消费表名'),
                    }),
                  },
                ],
                initialValue: consumerDbConfig.consumerTable,
              })(<Input typeCase="lower" disabled={isEdit} />)}
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              label={intl.get(`${promptCode}.consumerOffset`).d('初始偏移')}
              {...formLayout}
            >
              {getFieldDecorator('consumerOffset', {
                initialValue: consumerDbConfig.consumerOffset,
              })(<Input style={{ width: '100%' }} disabled />)}
            </Form.Item>
          </Col>
        </Row>
        <Row type="flex">
          <Col span={12}>
            <Form.Item label={intl.get(`hzero.common.status.enable`).d('启用')} {...formLayout}>
              {getFieldDecorator('enabledFlag', {
                initialValue: isEdit ? consumerDbConfig.enabledFlag : 1,
              })(<Switch />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }
}
