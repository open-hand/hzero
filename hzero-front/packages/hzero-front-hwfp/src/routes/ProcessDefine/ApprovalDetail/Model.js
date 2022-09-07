import React, { Component } from 'react';
import { Drawer, Form, Switch, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';
import isNil from 'lodash/isNil';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';

import styles from '../style/index.less';

@Form.create({ fieldNameProp: null })
export default class EditModal extends Component {
  @Bind()
  checkApproveOrder(rule, value, callback) {
    const { editData = {}, processNodeLines = [] } = this.props;
    const { approveOrder } = editData;
    const isCreate = isEmpty(editData);
    if (!isNil(value)) {
      let sameOrderLine = null;
      if (isCreate || (!isCreate && approveOrder !== value)) {
        sameOrderLine = processNodeLines.find((item) => item.approveOrder === value);
      }
      if (isNil(sameOrderLine)) {
        callback();
      } else {
        callback(
          intl.get('hwfp.processDetail.view.message.title.orderNotSame').d('已存在相同顺序的审批链')
        );
      }
    } else {
      callback();
    }
  }

  @Bind()
  save() {
    const { editData = {}, form, handleSave = () => {} } = this.props;
    const { approveDetailId, objectVersionNumber } = editData;
    const isCreate = isEmpty(editData);

    form.validateFields((err, value) => {
      if (!err) {
        let params = value;
        if (!isCreate) {
          params = {
            ...params,
            approveDetailId,
            objectVersionNumber,
          };
        }
        params = filterNullValueObject(params);
        handleSave(params);
      }
    });
  }

  @Bind()
  handleChangeApproveMethod() {
    this.props.form.setFieldsValue({ approveMethodValue: null });
  }

  @Bind()
  changeApproveStrategy() {
    this.props.form.setFieldsValue({ assignee: null });
  }

  render() {
    const {
      visible,
      editData = {},
      form: { getFieldDecorator = () => {}, getFieldValue = () => {} },
      handleClose = () => {},
      saveLoading,
    } = this.props;
    const {
      approveRule,
      approveRuleName,
      approver,
      assignee,
      enabledCondition,
      enabledConditionName,
      enabledFlag,
    } = editData;
    const isCreate = isEmpty(editData);

    return (
      <Drawer
        visible={visible}
        title={
          isCreate
            ? intl.get('hwfp.processDefine.view.message.title.createApproveRule').d('新建审批规则')
            : intl.get('hwfp.processDefine.view.message.title.editApproveRule').d('编辑审批规则')
        }
        closable
        onClose={handleClose}
        destroyOnClose
        width={400}
      >
        <Form layout="vertical" className={styles['node-line-modal-form']}>
          <Form.Item
            label={intl.get('hwfp.processDefine.model.processDefine.approveRule').d('审批规则')}
          >
            {getFieldDecorator('approveRule', {
              initialValue: !isCreate ? approveRule : null,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.processDefine.model.processDefine.approveRule')
                        .d('审批规则'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.processDefine.model.processDefine.approveRule')
                        .d('审批规则')}不能为空`
                    ),
                },
              ],
            })(
              <Lov
                code="HWFP.APPROVE_STRATEGY"
                textValue={approveRuleName}
                queryParams={{
                  serviceType: 'APPROVAL_CANDIDATE_RULE',
                  tenantId: getCurrentOrganizationId(),
                }}
                onChange={this.changeApproveStrategy}
                lovOptions={{
                  displayField: 'description',
                  valueField: 'serviceId',
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.processDefine.model.processDefine.approver').d('审批者')}
          >
            {getFieldDecorator('assignee', {
              initialValue: !isCreate ? assignee : '',
              rules: [
                {
                  required:
                    getFieldValue('approveRule') === 2 || getFieldValue('approveRule') === 3,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl.get('hwfp.processDefine.model.processDefine.approver').d('审批者'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.processDefine.model.processDefine.approver')
                        .d('审批者')}不能为空`
                    ),
                },
              ],
            })(
              <Lov
                disabled={getFieldValue('approveRule') !== 2 && getFieldValue('approveRule') !== 3}
                code={
                  getFieldValue('approveRule') === 2
                    ? 'HPFM.EMPLOYEE'
                    : 'HWFP.APPROVE_RULE_POSITION'
                }
                queryParams={{ tenantId: getCurrentOrganizationId() }}
                textValue={approver}
                lovOptions={{
                  displayField: getFieldValue('approveRule') === 2 ? 'name' : 'positionName',
                  valueField: getFieldValue('approveRule') === 2 ? 'employeeNum' : 'positionCode',
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl
              .get('hwfp.processDefine.model.processDefine.enabledCondition')
              .d('启用条件')}
          >
            {getFieldDecorator('enabledCondition', {
              initialValue: !isCreate ? enabledCondition : '',
            })(
              <Lov
                code="HWFP.APPROVE_STRATEGY"
                textValue={enabledConditionName}
                queryParams={{
                  serviceType: 'SEQUENCE_CONDITION',
                  tenantId: getCurrentOrganizationId(),
                }}
                lovOptions={{
                  displayField: 'description',
                  valueField: 'serviceId',
                }}
              />
            )}
          </Form.Item>
        </Form>
        <Form layout="inline" className={styles['node-line-modal-inline-form']}>
          <Form.Item label={intl.get('hwfp.processDefine.model.processDefine.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: !isCreate ? enabledFlag : 1,
            })(<Switch checkedValue={1} unCheckedValue={0} />)}
          </Form.Item>
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button onClick={handleClose} style={{ marginRight: 8 }} disabled={saveLoading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button type="primary" loading={saveLoading} onClick={this.save}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
