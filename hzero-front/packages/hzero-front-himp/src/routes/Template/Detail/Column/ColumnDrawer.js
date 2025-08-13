/**
 * Editor - 编辑数据表单
 * @since 2019-1-28
 * @author jiacheng.wang <jiacheng.wang@hand-china.com>
 * @version 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Input, Col, Row, InputNumber, Select, Modal, Spin, Tooltip, Icon } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import styles from '../index.less';

const FormItem = Form.Item;
const { Option } = Select;
const formLayout = {
  labelCol: { span: 8 },
  wrapperCol: { span: 16 },
};

@Form.create({ fieldNameProp: null })
export default class ColumnDrawer extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const {
      form: { getFieldDecorator = (e) => e },
      initData = {},
      columnTypeCode = [],
      title,
      modalVisible,
      loading,
      onCancel,
      initLoading = false,
      templateType,
    } = this.props;
    const dataSource =
      templateType === 'C'
        ? columnTypeCode
        : columnTypeCode.filter((item) => item.tag !== 'client');
    const {
      minValue,
      maxValue,
      validateSet,
      columnName,
      columnType,
      length,
      columnIndex,
      columnCode,
      formatMask,
      regularExpression,
      sampleData,
      description,
      changeDataFlag = 1,
      nullableFlag = 1,
      validateFlag = 1,
      enabledFlag = 1,
      _token,
    } = initData;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={820}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={initLoading}>
          <Form className={styles['template-column-input']}>
            <Row>
              <Col span={8}>
                <FormItem
                  label={
                    <span>
                      {intl.get(`himp.template.model.template.columnIndex`).d('列号')}
                      &nbsp;
                      <Tooltip
                        title={intl
                          .get('himp.template.view.message.title.columnIndex')
                          .d('列号从0开始递增')}
                      >
                        <Icon type="question-circle-o" />
                      </Tooltip>
                    </span>
                  }
                  {...formLayout}
                >
                  {getFieldDecorator('columnIndex', {
                    initialValue: columnIndex,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`himp.template.model.template.columnIndex`).d('列号'),
                        }),
                      },
                    ],
                  })(<InputNumber min={0} style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.columnCode`).d('列编码')}
                  {...formLayout}
                >
                  {getFieldDecorator('columnCode', {
                    initialValue: columnCode,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`himp.template.model.template.columnCode`).d('列编码'),
                        }),
                      },
                      {
                        pattern: /^[a-zA-Z0-9][a-zA-Z0-9-_./:]*$/,
                        message: intl
                          .get('himp.template.model.template.columnCodePattern')
                          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”、“:”'),
                      },
                    ],
                  })(<Input trim inputChinese={false} />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.columnName`).d('列名')}
                  {...formLayout}
                >
                  {getFieldDecorator('columnName', {
                    initialValue: columnName,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`himp.template.model.template.columnName`).d('列名'),
                        }),
                      },
                    ],
                  })(
                    <TLEditor
                      label={intl.get('himp.template.model.template.columnName').d('列名')}
                      field="columnName"
                      token={_token}
                    />
                  )}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.columnType`).d('列类型')}
                  {...formLayout}
                >
                  {getFieldDecorator('columnType', {
                    initialValue: columnType,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get(`himp.template.model.template.columnType`).d('列类型'),
                        }),
                      },
                    ],
                  })(
                    <Select>
                      {dataSource.map((n) => (
                        <Option key={n.value} value={n.value}>
                          {n.meaning}
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.formatMask`).d('格式掩码')}
                  {...formLayout}
                >
                  {getFieldDecorator('formatMask', {
                    initialValue: formatMask,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.length`).d('长度')}
                  {...formLayout}
                >
                  {getFieldDecorator('length', {
                    initialValue: length,
                  })(<InputNumber min={0} style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.minValue`).d('最小值')}
                  {...formLayout}
                >
                  {getFieldDecorator('minValue', {
                    initialValue: minValue,
                  })(<InputNumber min={0} style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.maxValue`).d('最大值')}
                  {...formLayout}
                >
                  {getFieldDecorator('maxValue', {
                    initialValue: maxValue,
                  })(<InputNumber min={0} style={{ width: '100%' }} />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.validateSet`).d('验证值集')}
                  {...formLayout}
                >
                  {getFieldDecorator('validateSet', {
                    initialValue: validateSet,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.regularExpression`).d('正则式')}
                  {...formLayout}
                >
                  {getFieldDecorator('regularExpression', {
                    initialValue: regularExpression,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.sampleData`).d('示例数据')}
                  {...formLayout}
                >
                  {getFieldDecorator('sampleData', {
                    initialValue: sampleData,
                  })(<Input />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.description`).d('描述')}
                  {...formLayout}
                >
                  {getFieldDecorator('description', {
                    initialValue: description,
                  })(<Input />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <FormItem label={intl.get(`hzero.common.status.enable`).d('启用')} {...formLayout}>
                  {getFieldDecorator('enabledFlag', {
                    initialValue: enabledFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.nullable`).d('允许为空')}
                  {...formLayout}
                >
                  {getFieldDecorator('nullableFlag', {
                    initialValue: nullableFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.validate`).d('数据验证')}
                  {...formLayout}
                >
                  {getFieldDecorator('validateFlag', {
                    initialValue: validateFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
            </Row>
            <Row>
              <Col span={8}>
                <FormItem
                  label={intl.get(`himp.template.model.template.changeDataFlag`).d('值集转换')}
                  {...formLayout}
                >
                  {getFieldDecorator('changeDataFlag', {
                    initialValue: changeDataFlag,
                  })(<Switch />)}
                </FormItem>
              </Col>
            </Row>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
