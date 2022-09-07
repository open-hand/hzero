import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input, Select } from 'hzero-ui';
import { isUndefined, isEmpty } from 'lodash';
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
export default class VariableDrawer extends Component {
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
   * 条件编码唯一性校验
   * @param {!object} documents - 规则
   * @param {!string} value - 表单值
   * @param {!Function} callback
   */
  @Bind()
  checkUnique(documents, value, callback) {
    const { ruleList, itemData } = this.props;
    if (isUndefined(itemData.code)) {
      // 非编辑时，校验规则编码是否重复
      const target = ruleList.find(item => item.code === +value);
      if (target) {
        callback(
          intl.get('hwfp.common.view.validation.code.exist').d('编码已存在，请输入其他编码')
        );
      }
      callback();
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
      enumMap = {},
    } = this.props;
    const { variableTypes = [] } = enumMap;
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
            label={intl.get('hwfp.common.model.common.variableName').d('字段名称')}
            {...formLayout}
          >
            {getFieldDecorator('variableName', {
              initialValue: itemData.variableName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.variableName').d('字段名称'),
                  }),
                },
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.common.variableType').d('字段类型')}
            {...formLayout}
          >
            {getFieldDecorator('variableType', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.variableType').d('字段类型'),
                  }),
                },
              ],
              initialValue: itemData.variableType,
            })(
              <Select>
                {variableTypes.map(item => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.common.model.common.variableDescription').d('字段描述')}
            {...formLayout}
          >
            {getFieldDecorator('description', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.variableDescription').d('字段描述'),
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
            {...formLayout}
            label={intl.get('hwfp.common.model.common.requiredFlag').d('是否必输')}
          >
            {getFieldDecorator('requiredFlag', {
              initialValue: itemData.requiredFlag === 0 ? 0 : 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
