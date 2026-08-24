import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { Modal, Form, DatePicker, Input } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import moment from 'moment';

// import Switch from 'components/Switch';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { VERSION_IS_OP } from 'utils/config';
import { getDateFormat } from 'utils/utils';

const dateFormat = getDateFormat();

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 14 },
};
/**
 * 请求权限定义-参数滑窗(抽屉)
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
    onOk: e => e,
    onCancel: e => e,
  };

  /**
   * 保存权限
   */
  @Bind()
  handleSave() {
    const { form, onSave, onEdit, itemData } = this.props;
    if (onSave) {
      form.validateFields((err, values) => {
        if (!err) {
          if (isEmpty(itemData)) {
            onSave({ ...values });
          } else {
            onEdit({
              ...itemData,
              ...values,
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
      createLoading,
      updateLoading,
      currentTenantId,
      tenantRoleLevel,
    } = this.props;
    const { getFieldDecorator, getFieldValue, setFieldsValue } = form;
    return (
      <Modal
        title={title}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        confirmLoading={createLoading || updateLoading}
        onOk={this.handleSave}
        onCancel={onCancel}
        destroyOnClose
      >
        <Form>
          {!tenantRoleLevel && (
            <Form.Item label={intl.get('entity.tenant.tag').d('租户')} {...formLayout}>
              {getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.tenant.tag').d('租户'),
                    }),
                  },
                ],
                initialValue: itemData.tenantId,
              })(
                <Lov
                  textValue={itemData.tenantName}
                  code="HPFM.TENANT"
                  onChange={() => {
                    setFieldsValue({ roleId: undefined });
                  }}
                  disabled={!isUndefined(itemData.permissionId)}
                />
              )}
            </Form.Item>
          )}
          <Form.Item
            label={intl.get('hrpt.reportDefinition.model.reportDefinition.roleId').d('角色')}
            {...formLayout}
          >
            {getFieldDecorator('roleId', {
              initialValue: itemData.roleId,
              rules: [
                {
                  required: VERSION_IS_OP,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hrpt.reportDefinition.model.reportDefinition.roleId').d('角色'),
                  }),
                },
              ],
            })(
              <Lov
                textValue={itemData.roleName}
                code="HIAM.TENANT_ROLE"
                disabled={
                  !isUndefined(itemData.permissionId)
                    ? true
                    : tenantRoleLevel
                    ? false
                    : isUndefined(getFieldValue('tenantId'))
                }
                queryParams={{
                  tenantId: tenantRoleLevel ? currentTenantId : getFieldValue('tenantId'),
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.reportDefinition.model.reportDefinition.startDate').d('有效期从')}
            {...formLayout}
          >
            {getFieldDecorator('startDate', {
              initialValue: itemData.startDate && moment(itemData.startDate, dateFormat),
            })(
              <DatePicker
                style={{ width: '100%' }}
                placeholder=""
                format={dateFormat}
                disabledDate={currentDate =>
                  getFieldValue('endDate') &&
                  moment(getFieldValue('endDate')).isBefore(currentDate, 'day')}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.reportDefinition.model.reportDefinition.endDate').d('有效期至')}
            {...formLayout}
          >
            {getFieldDecorator('endDate', {
              initialValue: itemData.endDate && moment(itemData.endDate, dateFormat),
            })(
              <DatePicker
                style={{ width: '100%' }}
                placeholder=""
                format={dateFormat}
                disabledDate={currentDate =>
                  getFieldValue('startDate') &&
                  moment(getFieldValue('startDate')).isAfter(currentDate, 'day')}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hrpt.reportDefinition.modal.reportDefinition.desc').d('权限说明')}
            {...formLayout}
          >
            {getFieldDecorator('remark', {
              initialValue: itemData.remark,
            })(<Input />)}
          </Form.Item>
          {/* <Form.Item label={intl.get('hzero.common.status').d('状态')} {...formLayout}>
            {getFieldDecorator('enabledFlag', {
              initialValue: itemData.enabledFlag === undefined ? 1 : itemData.enabledFlag,
            })(<Switch />)}
          </Form.Item> */}
        </Form>
      </Modal>
    );
  }
}
