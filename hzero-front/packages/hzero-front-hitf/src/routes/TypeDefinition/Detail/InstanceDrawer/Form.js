/**
 * InstanceForm - 实例配置表单
 * @date: 2019/8/26
 * @author: hulingfangzi <lingfangzi.hu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, Row, Col } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { FORM_COL_2_LAYOUT, MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

import MappingClassModal from '@/components/MappingClassModal';

const FormItem = Form.Item;

/**
 * 实例配置表单
 * @extends {Component} - React.Component
 * @reactProps {Object} instanceDetail - 实例详情
 * @reactProps {Function} onRef - 绑定元素
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class InstanceForm extends PureComponent {
  state = {
    currentCode: '',
    isShowModal: false,
  };

  /**
   * 获取映射类内容
   */
  @Bind()
  getCurrentCode() {
    const { currentCode } = this.state;
    const { mappingClass } = this.props.instanceDetail;
    return currentCode || mappingClass;
  }

  /**
   * 显示映射类弹窗
   */
  @Bind()
  handleOpenMappingClassModal() {
    const { instanceDetail = {}, onFetchMappingClass = () => {} } = this.props;
    const { applicationInstId, mappingClass } = instanceDetail;
    const { currentCode } = this.state;
    const params = applicationInstId ? { applicationInstId } : {};
    let code = '';
    if (currentCode) {
      code = currentCode;
    } else if (mappingClass) {
      code = mappingClass;
    } else {
      onFetchMappingClass(params).then(res => {
        if (res) {
          this.setState({
            currentCode: res.template,
            isShowModal: true,
          });
        }
      });
    }
    this.setState({
      currentCode: code,
      isShowModal: true,
    });
  }

  /**
   * 关闭映射类弹窗
   */
  @Bind()
  handleCloseMappingClassModal(value) {
    this.setState({
      isShowModal: false,
      currentCode: value,
    });
  }

  /**
   * 测试映射类
   * @param {string} value - 映射类代码
   */
  @Bind()
  handleTestMappingClass(value, cb = e => e) {
    const {
      onTestMappingClass = () => {},
      instanceDetail = {},
      fetchMappingClassLoading,
      testMappingClassLoading,
    } = this.props;
    const { applicationInstId = null } = instanceDetail;
    this.setState({
      currentCode: value,
    });
    if (fetchMappingClassLoading || testMappingClassLoading) return;
    onTestMappingClass(applicationInstId, value).then(res => {
      if (res) {
        notification.success();
        cb(res);
      }
    });
  }

  render() {
    const { currentCode, isShowModal } = this.state;
    const {
      form: { getFieldDecorator, setFieldsValue },
      instanceDetail = {},
      isCreate,
      composePolicy,
      fetchMappingClassLoading,
      testMappingClassLoading,
    } = this.props;
    const {
      interfaceName,
      interfaceCode,
      weight,
      remark,
      enabledFlag,
      instInterfaceId,
      orderSeq,
    } = instanceDetail;
    return (
      <>
        <Form>
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl
                  .get('hitf.typeDefinition.model.typeDefinition.instance.code')
                  .d('实例接口代码')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('instInterfaceId', {
                  initialValue: instInterfaceId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hitf.typeDefinition.model.typeDefinition.instance.code')
                          .d('实例接口代码'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HITF.COMPOSE_INST_INTERFACE"
                    disabled={!isCreate}
                    textValue={interfaceCode}
                    textField="interfaceCode"
                    onChange={(val, record) => {
                      setFieldsValue({ interfaceName: record.interfaceName });
                    }}
                  />
                )}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl
                  .get('hitf.typeDefinition.model.typeDefinition.instance.name')
                  .d('实例接口名称')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('interfaceName', {
                  initialValue: interfaceName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hitf.typeDefinition.model.typeDefinition.instance.name')
                          .d('实例接口名称'),
                      }),
                    },
                  ],
                })(<Input disabled={!isCreate} />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              {composePolicy === 'WEIGHT' ? (
                <FormItem
                  label={intl
                    .get('hitf.typeDefinition.model.typeDefinition.instance.weight')
                    .d('权重')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('weight', {
                    initialValue: weight,
                    rules: [
                      {
                        pattern: /^[1-9]\d*$/,
                        message: intl
                          .get('hitf.typeDefinition.model.typeDefinition.number.warning')
                          .d('请输入正整数'),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
              ) : (
                <FormItem
                  label={intl
                    .get('hitf.typeDefinition.model.typeDefinition.instance.orderSeq')
                    .d('优先级')}
                  {...MODAL_FORM_ITEM_LAYOUT}
                >
                  {getFieldDecorator('orderSeq', {
                    initialValue: orderSeq,
                    rules: [
                      {
                        pattern: /^[1-9]\d*$/,
                        message: intl
                          .get('hitf.typeDefinition.model.typeDefinition.number.warning')
                          .d('请输入正整数'),
                      },
                    ],
                  })(<Input />)}
                </FormItem>
              )}
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hitf.typeDefinition.model.typeDefinition.remark').d('说明')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('remark', {
                  initialValue: remark,
                })(<Input />)}
              </FormItem>
            </Col>
          </Row>
          <Row>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl.get('hzero.common.status').d('状态')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {getFieldDecorator('enabledFlag', {
                  initialValue: isCreate ? 1 : enabledFlag,
                })(<Switch />)}
              </FormItem>
            </Col>
            <Col {...FORM_COL_2_LAYOUT}>
              <FormItem
                label={intl
                  .get('hitf.typeDefinition.model.typeDefinition.instance.class')
                  .d('映射类')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                <a onClick={this.handleOpenMappingClassModal}>
                  {intl
                    .get('hitf.typeDefinition.view.message.title.detail.mapping.class')
                    .d('查看映射类详情')}
                </a>
              </FormItem>
            </Col>
          </Row>
        </Form>
        <MappingClassModal
          data={currentCode}
          loading={fetchMappingClassLoading}
          testLoading={testMappingClassLoading}
          visible={isShowModal}
          onCancel={this.handleCloseMappingClassModal}
          onTest={this.handleTestMappingClass}
        />
      </>
    );
  }
}
