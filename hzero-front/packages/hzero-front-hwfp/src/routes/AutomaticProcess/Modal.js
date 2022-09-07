import React, { Component } from 'react';
import { Drawer, Form, InputNumber, Input, Switch, Button, Select, DatePicker } from 'hzero-ui';
import moment from 'moment';
import { Bind } from 'lodash-decorators';
import isNil from 'lodash/isNil';

import Lov from 'components/Lov';
import intl from 'utils/intl';
import { filterNullValueObject, getCurrentOrganizationId } from 'utils/utils';
import { DEFAULT_DATE_FORMAT } from 'utils/constants';

import styles from './style/index.less';

const { TextArea } = Input;

@Form.create({ fieldNameProp: null })
export default class EditModal extends Component {
  @Bind()
  save() {
    const { form, handleCreate = () => {} } = this.props;

    form.validateFields((err, value) => {
      if (!err) {
        // eslint-disable-next-line
        let { processStartDate, processEndDate, processCondition } = value;
        let params = value;
        if (processCondition === 'FIXED_PERIOD') {
          if (processStartDate) {
            processStartDate = processStartDate.format(DEFAULT_DATE_FORMAT);
          } else {
            processStartDate = moment().format(DEFAULT_DATE_FORMAT);
          }
          if (processEndDate) {
            processEndDate = processEndDate.format(DEFAULT_DATE_FORMAT);
          }
          params = {
            ...params,
            processStartDate: processStartDate.concat(' 00:00:00'),
            processEndDate: processEndDate.concat(' 23:59:59'),
          };
        }
        params = filterNullValueObject(params);
        handleCreate(params);
      }
    });
  }

  @Bind()
  renderDynamicConditionItem() {
    const { isBatch, editData = {}, form, processtimeOutOptions = [] } = this.props;
    const { getFieldValue = () => {}, getFieldDecorator = () => {} } = form;
    const { processStartDate, processEndDate, timeoutUnit, timeoutValue } = editData;
    const processCondition = getFieldValue('processCondition');
    // 固定时间
    if (processCondition === 'FIXED_PERIOD') {
      return (
        <>
          <Form.Item
            label={intl.get('hwfp.automaticProcess.model.automaticProcess.startDate').d('开始时间')}
          >
            {getFieldDecorator('processStartDate', {
              initialValue:
                !isBatch && !isNil(processStartDate)
                  ? moment(processStartDate, DEFAULT_DATE_FORMAT)
                  : undefined,
            })(
              <DatePicker
                format={DEFAULT_DATE_FORMAT}
                disabledDate={(currentDate) =>
                  (getFieldValue('processEndDate') &&
                    moment(getFieldValue('processEndDate')).isBefore(currentDate, 'day')) ||
                  (currentDate && moment(currentDate).subtract(-1, 'days') < moment().endOf('day'))
                }
              />
            )}
          </Form.Item>
          <Form.Item
            label={intl
              .get('hwfp.automaticProcess.model.automaticProcess.processEndDate')
              .d('结束时间')}
          >
            {getFieldDecorator('processEndDate', {
              initialValue:
                !isBatch && !isNil(processEndDate)
                  ? moment(processEndDate, DEFAULT_DATE_FORMAT)
                  : undefined,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.processEndDate')
                        .d('结束时间'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.processEndDate')
                        .d('结束时间')}不能为空`
                    ),
                },
              ],
            })(
              <DatePicker
                format={DEFAULT_DATE_FORMAT}
                disabledDate={(currentDate) =>
                  (getFieldValue('processStartDate') &&
                    moment(getFieldValue('processStartDate')).isAfter(currentDate, 'day')) ||
                  (currentDate && moment(currentDate).subtract(-1, 'days') < moment().endOf('day'))
                }
              />
            )}
          </Form.Item>
        </>
      );
    } else if (processCondition === 'TIME_OUT') {
      return (
        <Form.Item
          label={intl
            .get('hwfp.automaticProcess.model.automaticProcess.timeoutValue')
            .d('超时时间')}
          className={styles['input-number-form-item']}
        >
          <span>
            {getFieldDecorator('timeoutValue', {
              initialValue: !isBatch ? timeoutValue : '',
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.timeoutValue')
                        .d('超时时间'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.timeoutValue')
                        .d('超时时间')}不能为空`
                    ),
                },
              ],
            })(<InputNumber min={0} />)}
            {getFieldDecorator('timeoutUnit', {
              initialValue:
                !isBatch && timeoutUnit ? timeoutUnit : (processtimeOutOptions[0] || {}).value,
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.timeoutUnit')
                        .d('超时单位'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.timeoutUnit')
                        .d('超时单位')}不能为空`
                    ),
                },
              ],
            })(
              <Select style={{ width: '30%' }}>
                {processtimeOutOptions.map((item) => (
                  <Select.Option value={item.value}>{item.meaning}</Select.Option>
                ))}
              </Select>
            )}
          </span>
        </Form.Item>
      );
    } else {
      return null;
    }
  }

  @Bind()
  renderDynamicRuleItem() {
    const { isBatch, editData = {}, form } = this.props;
    const { getFieldValue = () => {}, getFieldDecorator = () => {} } = form;
    const { delegateCode, delegateName, processRemark } = editData;
    const processRule = getFieldValue('processRule');
    if (processRule === 'AutoDelegate') {
      return (
        <Form.Item
          label={intl.get('hwfp.automaticProcess.model.automaticProcess.delegater').d('转交人')}
        >
          {getFieldDecorator('delegateCode', {
            initialValue: !isBatch ? delegateCode : '',
            rules: [
              {
                required: true,
                message: intl
                  .get('hzero.common.validation.notNull', {
                    name: intl
                      .get('hwfp.automaticProcess.model.automaticProcess.delegater')
                      .d('转交人'),
                  })
                  .d(
                    `${intl
                      .get('hwfp.automaticProcess.model.automaticProcess.delegater')
                      .d('转交人')}不能为空`
                  ),
              },
            ],
          })(
            <Lov
              code="HPFM.EMPLOYEE"
              queryParams={{ tenantId: getCurrentOrganizationId() }}
              textValue={delegateName}
              lovOptions={{
                displayField: 'name',
                valueField: 'employeeNum',
              }}
            />
          )}
        </Form.Item>
      );
    } else if (processRule === 'AutoApprove') {
      return (
        <Form.Item
          label={intl
            .get('hwfp.automaticProcess.model.automaticProcess.processRemark')
            .d('处理意见')}
        >
          {getFieldDecorator('processRemark', {
            initialValue: !isBatch ? processRemark : '',
          })(<TextArea maxLength={100} style={{ height: '100px', resize: 'none' }} />)}
        </Form.Item>
      );
    } else {
      return null;
    }
  }

  render() {
    const {
      visible,
      isBatch,
      editData = {},
      form: { getFieldDecorator = () => {} },
      handleClose = () => {},
      loading,
      processConditionOptions = [],
      processRuleOptions = [],
    } = this.props;
    const { processName, processCondition, processRule, enabledFlag } = editData;
    return (
      <Drawer
        visible={visible}
        title={
          !isBatch
            ? intl.get('hwfp.automaticProcess.view.message.title.editRule').d('编辑处理规则')
            : intl
                .get('hwfp.automaticProcess.view.message.title.batchEditRule')
                .d('批量编辑处理规则')
        }
        closable
        onClose={handleClose}
        destroyOnClose
        width={400}
      >
        <Form layout="vertical" className={styles['rule-modal-form']}>
          {!isBatch && (
            <Form.Item
              label={intl
                .get('hwfp.automaticProcess.model.automaticProcess.processName')
                .d('流程名称')}
            >
              {getFieldDecorator('processName', {
                initialValue: !isBatch ? processName : '',
              })(<Input disabled />)}
            </Form.Item>
          )}
          <Form.Item
            label={intl.get('hwfp.automaticProcess.model.automaticProcess.condition').d('处理条件')}
          >
            {getFieldDecorator('processCondition', {
              initialValue: !isBatch ? processCondition : '',
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.condition')
                        .d('处理条件'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.condition')
                        .d('处理条件')}不能为空`
                    ),
                },
              ],
            })(
              <Select>
                {processConditionOptions.map((item) => (
                  <Select.Option value={item.value}>{item.meaning}</Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {this.renderDynamicConditionItem()}
          <Form.Item
            label={intl.get('hwfp.automaticProcess.model.automaticProcess.rule').d('处理规则')}
          >
            {getFieldDecorator('processRule', {
              initialValue: !isBatch ? processRule : '',
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hwfp.automaticProcess.model.automaticProcess.rule')
                        .d('处理规则'),
                    })
                    .d(
                      `${intl
                        .get('hwfp.automaticProcess.model.automaticProcess.rule')
                        .d('处理规则')}不能为空`
                    ),
                },
              ],
            })(
              <Select>
                {processRuleOptions.map((item) => (
                  <Select.Option value={item.value}>{item.meaning}</Select.Option>
                ))}
              </Select>
            )}
          </Form.Item>
          {this.renderDynamicRuleItem()}
        </Form>
        <Form
          layout="inline"
          className={styles['rule-modal-inline-form']}
          style={{ paddingBottom: 50 }}
        >
          <Form.Item label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: !isBatch ? enabledFlag || 0 : 1,
            })(<Switch checkedValue={1} unCheckedValue={0} />)}
          </Form.Item>
        </Form>
        <div className={styles['rule-model-bottom-button']}>
          <Button onClick={handleClose} style={{ marginRight: 8 }} disabled={loading}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button type="primary" loading={loading} onClick={this.save}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
