import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Input, Select, InputNumber } from 'hzero-ui';
import { isEmpty } from 'lodash';
import classNames from 'classnames';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import styles from './index.less';
// import Lov from '../../../../components/Lov';
/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};
/**
 * 请求定义-参数滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 操作对象
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
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
   * 确定操作
   */
  @Bind()
  saveBtn() {
    const { form, onOk, onEditOk, itemData, paramFormatList = [], editTypeList = [] } = this.props;
    const { concParamId } = itemData;
    if (onOk) {
      form.validateFields((err, values) => {
        if (isEmpty(err)) {
          const formatData = paramFormatList.find((item) => item.value === values.paramFormatCode);
          const editTypeData = editTypeList.find((item) => item.value === values.paramEditTypeCode);
          const { meaning: paramFormatMeaning } = formatData;
          const { meaning: paramEditTypeMeaning } = editTypeData;
          // 校验通过，进行保存操作
          if (isEmpty(itemData)) {
            onOk({ ...values, paramFormatMeaning, paramEditTypeMeaning });
          } else {
            onEditOk({
              ...values,
              concParamId,
              paramFormatMeaning,
              paramEditTypeMeaning,
            });
          }
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
      form,
      itemData,
      onCancel,
      paramFormatList,
      editTypeList,
    } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        title={title}
        width={550}
        className={classNames(styles['header-form'])}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        destroyOnClose
      >
        <Form>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.orderSeq').d('排序号')}
            {...formLayout}
          >
            {getFieldDecorator('orderSeq', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.concurrent.model.concurrent.orderSeq').d('排序号'),
                  }),
                },
              ],
              initialValue: itemData.orderSeq,
            })(<InputNumber step={1} style={{ width: '100%' }} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.paramCode').d('参数名称')}
            {...formLayout}
          >
            {getFieldDecorator('paramCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.concurrent.model.concurrent.paramCode').d('参数名称'),
                  }),
                },
              ],
              initialValue: itemData.paramCode,
            })(<Input disabled={!!itemData.concParamId && !isEmpty(itemData.concParamId)} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.paramName').d('参数描述')}
            {...formLayout}
          >
            {getFieldDecorator('paramName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hsdr.concurrent.model.concurrent.paramName').d('参数描述'),
                  }),
                },
              ],
              initialValue: itemData.paramName,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.paramFormatCode').d('参数格式')}
            {...formLayout}
          >
            {getFieldDecorator('paramFormatCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hsdr.concurrent.model.concurrent.paramFormatCode')
                      .d('参数格式'),
                  }),
                },
              ],
              initialValue: itemData.paramFormatCode,
            })(
              <Select>
                {paramFormatList.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.paramEditTypeCode').d('编辑类型')}
            {...formLayout}
          >
            {getFieldDecorator('paramEditTypeCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hsdr.concurrent.model.concurrent.paramEditTypeCode')
                      .d('编辑类型'),
                  }),
                },
              ],
              initialValue: itemData.paramEditTypeCode,
            })(
              <Select>
                {editTypeList.map((item) => (
                  <Select.Option key={item.value} value={item.value}>
                    {item.meaning}
                  </Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.businessModel').d('业务模型')}
            {...formLayout}
          >
            {getFieldDecorator('businessModel', {
              initialValue: itemData.businessModel,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.valueFiledFrom').d('字段值从')}
            {...formLayout}
          >
            {getFieldDecorator('valueFiledFrom', {
              initialValue: itemData.valueFiledFrom,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.valueFiledTo').d('字段值至')}
            {...formLayout}
          >
            {getFieldDecorator('valueFiledTo', {
              initialValue: itemData.valueFiledTo,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.defaultValue').d('默认值')}
            {...formLayout}
          >
            {getFieldDecorator('defaultValue', {
              initialValue: itemData.defaultValue,
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.notnullFlag').d('是否必须')}
            {...formLayout}
          >
            {getFieldDecorator('notnullFlag', {
              initialValue: itemData.notnullFlag === undefined ? 1 : itemData.notnullFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hsdr.concurrent.model.concurrent.showFlag').d('是否展示')}
            {...formLayout}
          >
            {getFieldDecorator('showFlag', {
              initialValue: itemData.showFlag === undefined ? 1 : itemData.showFlag,
            })(<Switch />)}
          </Form.Item>
          <Form.Item label={intl.get('hzero.common.status').d('状态')} {...formLayout}>
            {getFieldDecorator('enabledFlag', {
              initialValue: itemData.enabledFlag === undefined ? 1 : itemData.enabledFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
