/**
 * UserGroupManagement 用户组管理
 * @date: 2019-1-14
 * @author: guochaochao <chaochao.guo@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { Form, Input, Modal, Spin } from 'hzero-ui';
import { isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';

import GroupUsers from './GroupUsers';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class MessageForm extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const {
      path,
      form,
      initData,
      title,
      modalVisible,
      loading,
      onCancel,
      initLoading,
      // 用户组 用户
      getCurrentRestGroupUsers,
      assignUsersToGroup,
      delCurrentGroupUsers,
      getCurrentGroupUsers,
      getCurrentRestGroupUsersLoading,
      assignUsersToGroupLoading,
      delCurrentGroupUsersLoading,
      getCurrentGroupUsersLoading,
    } = this.props;
    const {
      userGroupId,
      groupCode,
      groupName,
      remark,
      enabledFlag,
      tenantName,
      tenantId,
      _token,
    } = initData;
    const { getFieldDecorator } = form;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
      >
        <Spin spinning={initLoading}>
          <Form>
            {!isTenantRoleLevel() && (
              <FormItem
                {...MODAL_FORM_ITEM_LAYOUT}
                label={intl.get('hiam.database.model.database.tenantName').d('租户名称')}
              >
                {getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hiam.database.model.database.tenantName').d('租户名称'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    style={{ width: '100%' }}
                    allowClear={false}
                    textValue={tenantName}
                    disabled={userGroupId !== undefined}
                    code="HPFM.TENANT"
                  />
                )}
              </FormItem>
            )}
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.userGroupManagement.model.userGroup.groupCode').d('用户组编码')}
            >
              {getFieldDecorator('groupCode', {
                initialValue: groupCode,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hiam.userGroupManagement.model.userGroup.groupCode')
                        .d('用户组编码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', { max: 30 }),
                  },
                ],
              })(
                <Input
                  trim
                  inputChinese={false}
                  typeCase="upper"
                  disabled={userGroupId !== undefined}
                />
              )}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.userGroupManagement.model.userGroup.groupName').d('用户组名称')}
            >
              {getFieldDecorator('groupName', {
                initialValue: groupName,
                rules: [
                  {
                    type: 'string',
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hiam.userGroupManagement.model.userGroup.groupName')
                        .d('用户组名称'),
                    }),
                  },
                  {
                    max: 50,
                    message: intl.get('hzero.common.validation.max', { max: 50 }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hiam.userGroupManagement.model.userGroup.groupName')
                    .d('用户组名称')}
                  field="groupName"
                  token={_token}
                  dbc2sbc={false}
                />
              )}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.userGroupManagement.model.userGroup.remark').d('备注说明')}
            >
              {getFieldDecorator('remark', {
                initialValue: remark,
              })(<Input dbc2sbc={false} />)}
            </FormItem>
            <FormItem
              {...MODAL_FORM_ITEM_LAYOUT}
              label={intl.get('hzero.common.status.enable').d('启用')}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag === undefined ? 1 : enabledFlag,
              })(<Switch />)}
            </FormItem>
          </Form>
          {!isUndefined(userGroupId) && (
            <GroupUsers
              path={path}
              tenantId={tenantId}
              userGroupId={userGroupId}
              getCurrentRestGroupUsers={getCurrentRestGroupUsers}
              assignUsersToGroup={assignUsersToGroup}
              delCurrentGroupUsers={delCurrentGroupUsers}
              getCurrentGroupUsers={getCurrentGroupUsers}
              getCurrentRestGroupUsersLoading={getCurrentRestGroupUsersLoading}
              assignUsersToGroupLoading={assignUsersToGroupLoading}
              delCurrentGroupUsersLoading={delCurrentGroupUsersLoading}
              getCurrentGroupUsersLoading={getCurrentGroupUsersLoading}
            />
          )}
        </Spin>
      </Modal>
    );
  }
}
