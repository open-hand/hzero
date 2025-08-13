import React, { Component } from 'react';
import { Form, Input, Modal, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

import styles from './style/index.less';

const FormItem = Form.Item;
const formsLayouts = {
  labelCol: { span: 5 },
  wrapperCol: { span: 19 },
};

@Form.create({ fieldNameProp: null })
export default class GroupModal extends Component {
  @Bind()
  create() {
    const { form, handleCreate = () => {} } = this.props;
    form.validateFields((err, values = {}) => {
      if (!err) {
        handleCreate(values);
      }
    });
  }

  @Bind()
  save() {
    const { groupInfo = {}, form, handleSave = () => {} } = this.props;
    const { unitGroupId, menuCode } = groupInfo;
    form.validateFields((err, values = {}) => {
      if (!err) {
        handleSave({
          ...values,
          menuCode,
          unitGroupId,
        });
      }
    });
  }

  render() {
    const {
      groupInfo = {},
      visible,
      menuName,
      createGroupLoading,
      modifyGroupLoading,
      handleClose = () => {},
      form: { getFieldDecorator = () => {} },
    } = this.props;
    const { groupCode, groupName } = groupInfo;
    const isCreate = isEmpty(groupInfo);
    return (
      <Modal
        title={
          isCreate
            ? intl
                .get('hpfm.individuationUnit.view.message.title.createUnitGroup')
                .d('新建个性化单元组')
            : intl
                .get('hpfm.individuationUnit.view.message.title.editUnitGroup')
                .d('编辑个性化单元组')
        }
        width={500}
        visible={visible}
        onCancel={handleClose}
        footer={
          <div style={{ paddingRight: 8 }}>
            <Button onClick={handleClose} disabled={createGroupLoading || modifyGroupLoading}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </Button>
            <Button
              type="primary"
              onClick={isCreate ? this.create : this.save}
              loading={createGroupLoading || modifyGroupLoading}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </Button>
          </div>
        }
        destroyOnClose
      >
        <Form className={styles['group-model-form']}>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.menuName')
              .d('所属功能')}
            {...formsLayouts}
          >
            {getFieldDecorator('menuCode', {
              initialValue: menuName,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.groupCode')
              .d('单元组编码')}
            {...formsLayouts}
          >
            {getFieldDecorator('groupCode', {
              initialValue: groupCode,
              rules: [
                {
                  required: isCreate,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.groupCode')
                        .d('单元组编码'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.groupCode')
                        .d('单元组编码')}不能为空`
                    ),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 50,
                  message: intl.get('hzero.common.validation.max', {
                    max: 50,
                  }),
                },
              ],
            })(<Input disabled={!isCreate} typeCase="upper" />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.groupName')
              .d('单元组名称')}
            {...formsLayouts}
          >
            {getFieldDecorator('groupName', {
              initialValue: groupName,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.groupName')
                        .d('单元组名称'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.groupName')
                        .d('单元组名称')}不能为空`
                    ),
                },
                {
                  max: 50,
                  message: intl.get('hzero.common.validation.max', {
                    max: 50,
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
