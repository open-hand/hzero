import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input, Row, Col } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import Switch from 'components/Switch';

const { TextArea } = Input;
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
/**
 * Form.Item 组件label、wrapper长度比例划分
 * templateContent 模板内容的长度比例
 */
const temcLayout = {
  labelCol: { span: 3 },
  wrapperCol: { span: 20 },
};
/**
 * 跳转条件-数据修改滑窗(抽屉)
 * @extends {Component} - React.Component
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onHandleOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class EmailDrawer extends Component {
  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onHandleOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onHandleOk: e => e,
    onCancel: e => e,
  };

  /**
   * 确定操作
   */
  @Bind()
  handleOk() {
    const { form, onHandleOk, itemData } = this.props;
    if (onHandleOk) {
      const { interfaceCode, ...rest } = itemData;
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          const dataSource = {
            ...rest,
            ...values,
          };
          onHandleOk(dataSource);
        }
      });
    }
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      anchor,
      visible,
      title,
      itemData,
      onCancel,
      form: { getFieldDecorator },
      loading = false,
    } = this.props;
    return (
      <Modal
        okButtonProps={{ loading }}
        title={title}
        width={1000}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.handleOk}
        onCancel={onCancel}
        destroyOnClose
      >
        <Form>
          <Row type="flex">
            <Col span={12}>
              <Form.Item
                label={intl.get('hwfp.common.model.common.templateCode').d('模板编码')}
                {...formLayout}
              >
                {getFieldDecorator('templateCode', {
                  initialValue: itemData.templateCode,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hwfp.common.model.common.templateCode').d('模板编码'),
                      }),
                    },
                    {
                      max: 30,
                      message: intl.get('hzero.common.validation.max', {
                        max: 30,
                      }),
                    },
                  ],
                })(
                  <Input inputChinese={false} disabled={itemData.templateCode} typeCase="upper" />
                )}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hwfp.common.model.common.templateName').d('模板名称')}
                {...formLayout}
              >
                {getFieldDecorator('templateName', {
                  initialValue: itemData.templateName,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hwfp.common.model.common.templateName').d('模板名称'),
                      }),
                    },
                    {
                      max: 40,
                      message: intl.get('hzero.common.validation.max', {
                        max: 40,
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hwfp.common.model.common.interfaceCode').d('数据来源')}
                {...formLayout}
              >
                {getFieldDecorator('interfaceId', {
                  initialValue: itemData.interfaceId,
                })(<Lov code="HWFP.INTERFACE_DEFINE" textValue={itemData.interfaceCode} />)}
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                label={intl.get('hwfp.common.model.common.templateRemark').d('模板描述')}
                {...formLayout}
              >
                {getFieldDecorator('templateRemark', {
                  initialValue: itemData.templateRemark,
                  rules: [
                    {
                      max: 40,
                      message: intl.get('hzero.common.validation.max', {
                        max: 40,
                      }),
                    },
                  ],
                })(<Input />)}
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={24}>
              <Form.Item
                label={intl.get('hwfp.common.model.common.templateContent').d('模板内容')}
                {...temcLayout}
              >
                {getFieldDecorator('templateContent', {
                  initialValue: itemData.templateContent,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hwfp.common.model.common.templateContent').d('模板内容'),
                      }),
                    },
                  ],
                })(
                  <TextArea
                    rows={20}
                    placeholder={intl
                      .get('hwfp.common.view.message.placeholder.pleaseInput')
                      .d(
                        '请输入完整的HTML代码,并且将<#assign json=text?eval />嵌入到<html>后面......'
                      )}
                  />
                )}
              </Form.Item>
            </Col>
          </Row>
          <Row>
            <Col span={12}>
              <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
                {getFieldDecorator('enabledFlag', {
                  initialValue: itemData.enabledFlag === 0 ? 0 : 1,
                })(<Switch />)}
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    );
  }
}
