import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';
import Switch from 'components/Switch';
import Lov from 'components/Lov';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

const FormItem = Form.Item;
const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();

@Form.create({ fieldNameProp: null })
export default class CreateDrawer extends React.Component {
  /**
   * 保存
   */
  @Bind()
  handleOk() {
    const { form, onOk = e => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        let handleFieldsValue = fieldsValue;
        if (isTenant) {
          handleFieldsValue = { ...fieldsValue, tenantId: organizationId };
        }
        onOk(handleFieldsValue);
      }
    });
  }

  render() {
    const { form, modalVisible, loading, onCancel } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={intl.get('hpfm.dataGroup.view.message.create').d('创建数据组')}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOk}
      >
        <Form>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.dataGroup.model.dataGroup.code').d('代码')}
          >
            {getFieldDecorator('groupCode', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dataGroup.model.dataGroup.code').d('代码'),
                  }),
                },
                {
                  max: 60,
                  message: intl.get('hzero.common.validation.max', {
                    max: 60,
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
              validateFirst: true,
            })(<Input />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称')}
          >
            {getFieldDecorator('groupName', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.dataGroup.model.dataGroup.name').d('名称'),
                  }),
                },
                {
                  max: 360,
                  message: intl.get('hzero.common.validation.max', {
                    max: 360,
                  }),
                },
              ],
              validateFirst: true,
            })(<Input />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.dataGroup.model.dataGroup.remark').d('说明')}
          >
            {getFieldDecorator('remark')(<Input />)}
          </FormItem>
          {!isTenant && (
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hpfm.permission.model.permission.tenant').d('租户')}
            >
              {getFieldDecorator('tenantId', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.permission.model.permission.tenant').d('租户'),
                    }),
                  },
                ],
              })(<Lov code="HPFM.TENANT" textField="tenantName" />)}
            </FormItem>
          )}
          <Form.Item
            label={intl.get('hpfm.dataGroup.model.dataGroup.isEnabled').d('是否启用')}
            {...MODAL_FORM_ITEM_LAYOUT}
          >
            {getFieldDecorator('enabledFlag', {
              initialValue: 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
