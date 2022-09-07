import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input } from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Switch from 'components/Switch';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
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
export default class FormDrawer extends Component {
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
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          onHandleOk({
            ...itemData,
            ...values,
          });
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
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.handleOk}
        onCancel={onCancel}
        destroyOnClose
      >
        <Form>
          <Form.Item
            label={intl.get('hwfp.common.model.common.formCode').d('表单编码')}
            {...formLayout}
          >
            {getFieldDecorator('formCode', {
              initialValue: itemData.formCode,
              rules: [
                {
                  required: !itemData.formId,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.formCode').d('表单编码'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input inputChinese={false} disabled={itemData.formId} typeCase="upper" />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.common.formDescription').d('表单描述')}
            {...formLayout}
          >
            {getFieldDecorator('description', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.formDescription').d('表单描述'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
              initialValue: itemData.description,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.common.pcFormUrl').d('PC端表单URL')}
            {...formLayout}
          >
            {getFieldDecorator('formUrl', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.pcFormUrl').d('PC端表单URL'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
              initialValue: itemData.formUrl,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.common.mobileFormUrl').d('移动端表单URL')}
            {...formLayout}
          >
            {getFieldDecorator('mobileFormUrl', {
              initialValue: itemData.mobileFormUrl,
            })(<Input />)}
          </Form.Item>
          <Form.Item {...formLayout} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: itemData.enabledFlag === 0 ? 0 : 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}