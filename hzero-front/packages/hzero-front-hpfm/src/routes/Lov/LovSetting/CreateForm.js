import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import lodash from 'lodash';

import Lov from 'components/Lov';

import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import { CODE_UPPER } from 'utils/regExp';
import { isTenantRoleLevel } from 'utils/utils';

/**
 * lov弹框编辑
 * @extends {Component} - React.Component
 * @reactProps {Object} form - 表单对象
 * @reactProps {Object} modalVisible - 控制modal显示/隐藏属性
 * @reactProps {Function} handleAdd - 数据保存
 * @reactProps {Function} showCreateModal - 控制modal显示隐藏方法
 * @return React.element
 */
const CreateForm = Form.create({ fieldNameProp: null })((props) => {
  const { form, modalVisible, handleAdd, showCreateModal, confirmLoading } = props;
  const okHandle = () => {
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      handleAdd({ ...fieldsValue, viewCode: lodash.trim(fieldsValue.viewCode) }, form);
    });
  };
  return (
    <Modal
      title={intl.get('hpfm.lov.view.message.title.modal.lovSetting').d('新建值集视图')}
      visible={modalVisible}
      onOk={okHandle}
      confirmLoading={confirmLoading}
      width={520}
      onCancel={() => showCreateModal(false, form)}
      wrapClassName="ant-modal-sidebar-right"
      transitionName="move-right"
    >
      <React.Fragment>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.viewCode').d('视图代码')}
        >
          {form.getFieldDecorator('viewCode', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.viewCode').d('视图代码'),
                }),
              },
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', {
                  max: 80,
                }),
              },
              {
                pattern: CODE_UPPER,
                message: intl
                  .get('hzero.common.validation.codeUpper')
                  .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
              },
            ],
          })(<Input trim typeCase="upper" inputChinese={false} />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.viewName').d('视图名称')}
        >
          {form.getFieldDecorator('viewName', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.viewName').d('视图名称'),
                }),
              },
              {
                max: 80,
                message: intl.get('hzero.common.validation.max', {
                  max: 80,
                }),
              },
            ],
          })(<Input />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.valueField').d('值字段名')}
        >
          {form.getFieldDecorator('valueField', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.valueField').d('值字段名'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(<Input inputChinese={false} />)}
        </Form.Item>
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.displayField').d('显示字段名')}
        >
          {form.getFieldDecorator('displayField', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.displayField').d('显示字段名'),
                }),
              },
              {
                max: 30,
                message: intl.get('hzero.common.validation.max', {
                  max: 30,
                }),
              },
            ],
          })(<Input inputChinese={false} />)}
        </Form.Item>
        {!isTenantRoleLevel() && (
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('entity.tenant.name').d('租户名称')}
          >
            {form.getFieldDecorator('tenantId')(
              <Lov
                code="HPFM.TENANT"
                textField="tenantName"
                onChange={() => {
                  form.resetFields('lovId');
                }}
              />
            )}
          </Form.Item>
        )}
        <Form.Item
          {...MODAL_FORM_ITEM_LAYOUT}
          label={intl.get('hpfm.lov.model.lov.lovId').d('值集')}
        >
          {form.getFieldDecorator('lovId', {
            rules: [
              {
                required: true,
                message: intl.get('hzero.common.validation.notNull', {
                  name: intl.get('hpfm.lov.model.lov.lovId').d('值集'),
                }),
              },
            ],
          })(
            <Lov
              code={isTenantRoleLevel() ? 'HPFM.LOV.LOV_DETAIL.ORG' : 'HPFM.LOV.LOV_DETAIL'}
              queryParams={{
                tenantId:
                  form.getFieldValue('tenantId') !== undefined
                    ? form.getFieldValue('tenantId')
                    : '',
                lovQueryFlag: 1,
              }}
            />
          )}
        </Form.Item>
      </React.Fragment>
    </Modal>
  );
});

export default CreateForm;
