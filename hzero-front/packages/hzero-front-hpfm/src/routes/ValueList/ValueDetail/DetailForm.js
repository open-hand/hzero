import React from 'react';
import { Button, DatePicker, Form, Input, InputNumber, Modal } from 'hzero-ui';
import moment from 'moment';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';

import { getCurrentOrganizationId, getDateFormat } from 'utils/utils';
import intl from 'utils/intl';

@Form.create({ fieldNameProp: null })
export default class DetailForm extends React.Component {
  state = {
    tenantId: getCurrentOrganizationId(),
  };

  @Bind()
  handleOk() {
    const { form, onOk = (e) => e } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const { form, editValue, title, modalVisible, saveLoading, onCancel } = this.props;
    const { tenantId } = this.state;
    // 当前租户是否和数据中的租户对应
    const isCurrentTenant =
      editValue.tenantId !== undefined ? tenantId !== editValue.tenantId : false;
    const { getFieldDecorator, getFieldValue } = form;
    const {
      value,
      meaning,
      description,
      tag,
      orderSeq,
      parentLovCode,
      parentTenantId,
      parentValue,
      parentMeaning,
      startDateActive,
      endDateActive,
      _token,
      enabledFlag = 1,
    } = editValue;

    const fromLayOut = {
      labelCol: { span: 5 },
      wrapperCol: { span: 12 },
    };

    const dateFormat = getDateFormat();

    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        width={520}
        visible={modalVisible}
        confirmLoading={saveLoading}
        onCancel={onCancel}
        footer={
          isCurrentTenant
            ? null
            : [
                <Button key="cancel" onClick={onCancel}>
                  {intl.get('hzero.common.button.cancel').d('取消')}
                </Button>,
                <Button key="on" type="primary" loading={saveLoading} onClick={this.handleOk}>
                  {intl.get('hzero.common.button.ok').d('确定')}
                </Button>,
              ]
        }
      >
        <Form>
          <Form.Item {...fromLayOut} label={intl.get('hpfm.valueList.model.line.value').d('值')}>
            {getFieldDecorator('value', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.line.value').d('值'),
                  }),
                },
              ],
              initialValue: value,
            })(<Input disabled={!!value} dbc2sbc={false} />)}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.valueList.model.line.meaning').d('含义')}
          >
            {getFieldDecorator('meaning', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.valueList.model.line.meaning').d('含义'),
                  }),
                },
                {
                  max: 240,
                  message: intl.get('hzero.common.validation.max', {
                    max: 240,
                  }),
                },
              ],
              initialValue: meaning,
            })(
              <TLEditor
                disabled={isCurrentTenant}
                label={intl.get('hpfm.valueList.model.line.meaning').d('含义')}
                field="meaning"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.common.model.common.orderSeq').d('排序号')}
          >
            {getFieldDecorator('orderSeq', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.common.model.common.orderSeq').d('排序号'),
                  }),
                },
              ],
              initialValue: orderSeq,
            })(<InputNumber style={{ width: '100%' }} disabled={isCurrentTenant} />)}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.valueList.model.line.parentValue').d('父级值集值')}
          >
            {getFieldDecorator('parentValue', {
              initialValue: parentValue,
            })(
              <Lov
                disabled={!!value || !parentLovCode}
                textValue={parentMeaning}
                code="HPFM.LOV.LOV_VALUE"
                queryParams={{
                  lovCode: 'HPFM.LOV.LOV_VALUE',
                  parentLovCode,
                  parentTenantId,
                  enabledFlag: 1,
                }}
              />
            )}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.valueList.model.line.startDateActive').d('有效期起')}
          >
            {getFieldDecorator('startDateActive', {
              initialValue: startDateActive ? moment(startDateActive) : null,
            })(
              <DatePicker
                disabled={isCurrentTenant}
                style={{ width: '100%' }}
                placeholder=""
                format={dateFormat}
                disabledDate={(currentDate) =>
                  getFieldValue('endDateActive') &&
                  moment(getFieldValue('endDateActive')).isBefore(currentDate, 'day')
                }
              />
            )}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.valueList.model.line.endDateActive').d('有效期止')}
          >
            {getFieldDecorator('endDateActive', {
              initialValue: endDateActive ? moment(endDateActive) : null,
            })(
              <DatePicker
                disabled={isCurrentTenant}
                style={{ width: '100%' }}
                placeholder=""
                format={dateFormat}
                disabledDate={(currentDate) =>
                  getFieldValue('startDateActive') &&
                  moment(getFieldValue('startDateActive')).isAfter(currentDate, 'day')
                }
              />
            )}
          </Form.Item>
          <Form.Item {...fromLayOut} label={intl.get('hzero.common.status.enable').d('启用')}>
            {getFieldDecorator('enabledFlag', {
              initialValue: enabledFlag,
            })(<Switch disabled={isCurrentTenant} />)}
          </Form.Item>
          <Form.Item
            {...fromLayOut}
            label={intl.get('hpfm.valueList.model.line.description').d('描述')}
          >
            {getFieldDecorator('description', {
              initialValue: description,
            })(
              <TLEditor
                disabled={isCurrentTenant}
                label={intl.get('hpfm.valueList.model.line.description').d('描述')}
                field="description"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item {...fromLayOut} label={intl.get('hpfm.valueList.model.line.tag').d('标记')}>
            {getFieldDecorator('tag', {
              initialValue: tag,
            })(<Input disabled={isCurrentTenant} />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
