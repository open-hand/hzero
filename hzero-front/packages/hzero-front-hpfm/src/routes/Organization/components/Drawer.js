import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, Select, Input, InputNumber } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */

/**
 * 组织维护-数据修改滑窗(抽屉)
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {string} anchor - 抽屉滑动位置
 * @reactProps {string} title - 抽屉标题
 * @reactProps {boolean} visible - 抽屉是否可见
 * @reactProps {Function} onOk - 抽屉确定操作
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} itemData - 组织实体
 * @reactProps {Array} unitType - 公司类型值集
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
    anchor: 'left',
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
    const { form, onOk, itemData } = this.props;
    if (onOk) {
      form.validateFields((err, values) => {
        if (!err) {
          // 校验通过，进行保存操作
          onOk({ ...itemData, ...values });
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
      title,
      visible,
      form,
      loading,
      itemData,
      unitType,
      onCancel,
      tenantId,
      isCreate = false,
    } = this.props;
    const { getFieldDecorator } = form;
    const formLayout = {
      labelCol: { span: 6 },
      wrapperCol: { span: 12 },
    };
    return (
      <Modal
        destroyOnClose
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={loading}
        okButtonProps={{ disabled: !visible }}
        onOk={this.saveBtn}
        onCancel={onCancel}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item label={intl.get('entity.organization.code').d('组织编码')} {...formLayout}>
            {getFieldDecorator('unitCode', {
              initialValue: itemData.unitCode,
              rules: [
                isCreate && {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.organization.code').d('组织编码'),
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ].filter(Boolean),
            })(<Input trim typeCase="upper" inputChinese={false} disabled={!isCreate} />)}
          </Form.Item>
          <Form.Item label={intl.get('entity.organization.name').d('组织名称')} {...formLayout}>
            {getFieldDecorator('unitName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.organization.name').d('组织名称'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
              initialValue: itemData.unitName,
            })(
              <TLEditor
                label={intl.get('entity.organization.name').d('组织名称')}
                field="unitName"
                token={itemData._token}
              />
            )}
          </Form.Item>
          <Form.Item label={intl.get('entity.organization.type').d('组织类型')} {...formLayout}>
            {getFieldDecorator('unitTypeCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('entity.organization.type').d('组织类型'),
                  }),
                },
              ],
              initialValue: itemData.unitTypeCode,
            })(
              <Select>
                {unitType.map((item) => (
                  <Select.Option key={item.value}>{item.meaning}</Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          <Form.Item label={intl.get('entity.organization.company').d('关联企业')} {...formLayout}>
            {getFieldDecorator('companyId', {
              initialValue: itemData.companyId,
            })(
              <Lov
                code="HPFM.COMPANY"
                textValue={itemData.companyName}
                queryParams={{ tenantId, enabledFlag: 1 }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.organization.model.unit.parentUnit').d('上级组织')}
            {...formLayout}
          >
            {getFieldDecorator('parentUnitId', {
              initialValue: itemData.parentUnitId,
            })(
              <Lov
                code="HPFM.UNIT.COMPANY"
                textValue={itemData.parentUnitName}
                queryParams={{
                  tenantId: isCreate ? tenantId : itemData.tenantId,
                  // levelPath: itemData.levelPath,
                  unitId: itemData.unitId,
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.common.model.common.orderSeq').d('排序号')}
            {...formLayout}
          >
            {getFieldDecorator('orderSeq', {
              initialValue: isCreate ? 1 : itemData.orderSeq,
              rules: [
                isCreate && {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
                  }),
                },
              ].filter(Boolean),
            })(<InputNumber style={{ width: '100%' }} min={1} precision={0} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hpfm.organization.model.unit.supervisorFlag').d('主管组织')}
            {...formLayout}
          >
            {getFieldDecorator('supervisorFlag', {
              initialValue: isCreate ? 0 : itemData.supervisorFlag,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
