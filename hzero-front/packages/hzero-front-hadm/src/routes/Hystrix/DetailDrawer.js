/**
 * DetailDrawer - 新增熔断细则Form侧滑表单
 * @date: 2018-9-15
 * @author: LZH <zhaohui.liu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, hand
 */
import React, { PureComponent } from 'react';
import { Form, Input, Select, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import PropTypes from 'prop-types';
import { map } from 'lodash';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const { Option } = Select;
const { TextArea } = Input;

/**
 * 熔断保护新增
 * @extends {PureComponent} -React.PureComponent
 * @reactProps {Function} onHandleSelect //lov设置名称
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class DetailDrawer extends PureComponent {
  /**
   * 组件属性定义
   */
  static propTypes = {
    anchor: PropTypes.oneOf(['left', 'right', 'top', 'bottom']),
    title: PropTypes.string,
    visible: PropTypes.bool,
    onOk: PropTypes.func,
    onCancel: PropTypes.func,
  };

  /**
   * 组件属性默认值设置
   */
  static defaultProps = {
    anchor: 'right',
    title: '',
    visible: false,
    onOk: (e) => e,
    onCancel: (e) => e,
  };

  /**
   * 选择参数代码，顺带带出参数描述
   */
  @Bind()
  setRemark(rowKey) {
    const { propertyNameList, form } = this.props;
    let propertyRemark = '';
    propertyNameList.forEach((n) => {
      if (n.value === rowKey) {
        propertyRemark = n.meaning;
      }
    });
    form.setFieldsValue({ propertyRemark });
  }

  /**
   * 保存
   */
  @Bind()
  saveBtn() {
    const { form, onOk } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        onOk({ ...values });
      });
    }
  }

  render() {
    const {
      form,
      data,
      propertyNameList,
      title,
      anchor,
      visible,
      onCancel,
      addLoading,
    } = this.props;
    return (
      <Modal
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        confirmLoading={addLoading}
        visible={visible}
        onOk={this.saveBtn}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        onCancel={onCancel}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
        destroyOnClose
      >
        <Form>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hadm.hystrix.model.hystrix.propertyName`).d('参数代码')}
          >
            {form.getFieldDecorator('propertyName', {
              initialValue: data.propertyName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`hadm.hystrix.model.hystrix.propertyName`).d('参数代码'),
                  }),
                },
              ],
            })(
              <Select onChange={this.setRemark} showSearch>
                {map(propertyNameList, (e) => (
                  <Option value={e.value} key={e.value}>
                    {e.value}
                  </Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hadm.hystrix.model.hystrix.propertyRemark`).d('参数描述')}
          >
            {form.getFieldDecorator('propertyRemark', {
              initialValue: data.propertyRemark,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hadm.hystrix.model.hystrix.propertyValue`).d('参数值')}
          >
            {form.getFieldDecorator('propertyValue', {
              initialValue: data.propertyValue,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`hadm.hystrix.model.hystrix.propertyValue`).d('参数值'),
                  }),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hzero.common.status.enable').d('启用')}
          >
            {form.getFieldDecorator('enabledFlag', {
              initialValue: data.enabledFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hzero.common.view.description`).d('描述')}
          >
            {form.getFieldDecorator('remark', {
              initialValue: data.remark,
            })(<TextArea autosize={{ minRows: 3, maxRows: 6 }} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
