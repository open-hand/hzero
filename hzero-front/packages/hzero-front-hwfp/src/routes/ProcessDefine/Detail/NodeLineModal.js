import React, { Component } from 'react';
import { Drawer, Form, InputNumber, Input, Switch, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';
import isNil from 'lodash/isNil';
import isNumber from 'lodash/isNumber';

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
  checkProportion(rule, value, callback) {
    if (value) {
      if (
        value.indexOf('.') === -1 &&
        parseInt(value, 10) === Number(value) &&
        parseInt(value, 10) > 0 &&
        parseInt(value, 10) <= 100
      ) {
        callback();
      } else {
        callback(
          intl.get('hwfp.processDefine.validation.positiveNumberReg').d('请输入1到100之间的正整数')
        );
      }
    } else {
      callback();
    }
  }

  @Bind()
  save() {
    const { editData = {}, form, handleCreate = () => {} } = this.props;
    const { approveChainLineId, objectVersionNumber } = editData;
    const isCreate = isEmpty(editData);
    form.validateFields((err, value) => {
      if (!err) {
        let { approveMethodValue } = value;
        if (approveMethodValue) {
          approveMethodValue /= 100;
        }
        let params = {
          ...value,
          approveMethodValue,
        };
        if (!isCreate) {
          params = {
            ...params,
            approveChainLineId,
            objectVersionNumber,
          };
        }
        params = filterNullValueObject(params);
        handleCreate(params);
      }
    });
  }

  @Bind()
  handleChangeApproveMethod() {
    this.props.form.setFieldsValue({ approveMethodValue: null });
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
      approveOrder,
      name,
      serviceName,
      approveMethod,
      approveMethodValue,
      enabledFlag,
    } = editData;
    const newApproveMethodValue =
      !isNil(approveMethodValue) && isNumber(approveMethodValue)
        ? approveMethodValue * 100
        : approveMethodValue;
    const isCreate = isEmpty(editData);
    return (
      <Drawer
        visible={visible}
        title={
          isCreate
            ? intl.get('hwfp.processDefine.view.message.title.createApproveChain').d('新建审批链')
            : intl.get('hwfp.processDefine.view.message.title.editApproveChain').d('编辑审批链')
        }
        closable
        onClose={handleClose}
        destroyOnClose
        width={400}
      >
        <Form layout="vertical" className={styles['node-line-modal-form']}>
          <Form.Item
            label={intl.get('hwfp.processDefine.model.processDefine.approveOrder').d('审批顺序')}
          >
            {getFieldDecorator('approveOrder', {
              initialValue: !isCreate ? approveOrder : null,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.processDefine.model.processDefine.approveOrder')
                        .d('审批顺序'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.processDefine.model.processDefine.approveOrder')
                        .d('审批顺序')}不能为空`
                    ),
                },
                {
                  pattern: /^[0-9]*$/,
                  message: intl.get('hwfp.processDefine.validation.numberReg').d('请输入正整数'),
                },
                {
                  validator: this.checkApproveOrder,
                },
              ],
            })(<InputNumber min={0} />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.processDefine.model.processDefine.name').d('审批链名称')}
          >
            {getFieldDecorator('name', {
              initialValue: !isCreate ? name : '',
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl.get('hwfp.processDefine.model.processDefine.name').d('审批链名称'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.processDefine.model.processDefine.name')
                        .d('审批链名称')}不能为空`
                    ),
                },
              ],
            })(<Input />)}
          </Form.Item>
          <Form.Item
            label={intl.get('hwfp.processDefine.model.processDefine.approveMethod').d('审批方式')}
          >
            {getFieldDecorator('approveMethod', {
              initialValue: !isCreate ? approveMethod : '',
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.processDefine.model.processDefine.approveMethod')
                        .d('审批方式'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.processDefine.model.processDefine.approveMethod')
                        .d('审批方式')}不能为空`
                    ),
                },
              ],
            })(
              <Lov
                code="HWFP.APPROVE_STRATEGY"
                textValue={serviceName}
                queryParams={{
                  serviceType: 'APPROVAL_STRATEGY',
                  tenantId: getCurrentOrganizationId(),
                }}
                lovOptions={{
                  displayField: 'description',
                  valueField: 'serviceId',
                }}
                onChange={this.handleChangeApproveMethod}
              />
            )}
          </Form.Item>
          {getFieldValue('approveMethod') === 8 && (
            <Form.Item
              style={{ width: '50%' }}
              label={intl.get('hwfp.processDefine.model.processDefine.proportion').d('比例')}
            >
              {getFieldDecorator('approveMethodValue', {
                initialValue:
                  !isCreate && !isNil(newApproveMethodValue) ? String(newApproveMethodValue) : '',
                rules: [
                  {
                    required: true,
                    message: intl
                      .get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processDefine.model.processDefine.proportion')
                          .d('比例'),
                      })
                      .d(
                        `${intl
                          .get('hwfp.processDefine.model.processDefine.proportion')
                          .d('比例')}不能为空`
                      ),
                  },
                  {
                    validator: this.checkProportion,
                  },
                ],
              })(<Input addonAfter="%" />)}
            </Form.Item>
          )}
        </Form>
        <Form
          layout="inline"
          className={styles['node-line-modal-inline-form']}
          style={{ paddingBottom: 50 }}
        >
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
