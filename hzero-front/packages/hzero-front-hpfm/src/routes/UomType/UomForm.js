/*
 * UomForm - 计量单位类型定义新增弹窗
 * @date: 2018/08/07 14:42:49
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty, isFunction } from 'lodash';

import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';
import Lov from 'components/Lov';

import intl from 'utils/intl';
import { getCurrentOrganizationId } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const promptCode = 'hpfm.uomType';

/**
 * 计量单位新增
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onHandleSelect // lov设置名称
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class UomForm extends PureComponent {
  constructor(props) {
    super(props);
    if (isFunction(props.onRef)) props.onRef(this);
  }

  /**
   * 选择Lov带出对应的名称
   * @param {String} rowKeys
   * @param {Object} record
   */
  @Bind()
  onHandleSelect(rowKeys, record) {
    const {
      form: { setFieldsValue, getFieldDecorator },
    } = this.props;
    getFieldDecorator([`baseUomName1`]);
    setFieldsValue({
      baseUomName1: record.uomName,
      baseUomCode: record.uomCode,
    });
  }

  // 保存
  @Bind()
  saveBtn() {
    const { form, onHandleAdd } = this.props;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        onHandleAdd(values);
      }
    });
  }

  render() {
    const { form, data, title, anchor, visible, onCancel, confirmLoading } = this.props;
    const { _token } = data;
    return (
      <Modal
        destroyOnClose
        title={title}
        width={520}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
        visible={visible}
        onOk={this.saveBtn}
        onCancel={onCancel}
        confirmLoading={confirmLoading}
        okText={intl.get('hzero.common.button.ok').d('确定')}
        cancelText={intl.get('hzero.common.button.cancel').d('取消')}
      >
        <Form>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`${promptCode}.model.uomType.uomTypeCode`).d('单位类型代码')}
          >
            {form.getFieldDecorator('uomTypeCode', {
              initialValue: data.uomTypeCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`${promptCode}.model.uomType.uomTypeCode`).d('单位类型代码'),
                  }),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
              ],
            })(
              <Input
                trim
                typeCase="upper"
                inputChinese={false}
                disabled={!!(data.uomTypeCode && data.uomTypeId)}
              />
            )}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`${promptCode}.model.uomType.uomTypeName`).d('单位类型名称')}
          >
            {form.getFieldDecorator('uomTypeName', {
              initialValue: data.uomTypeName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get(`${promptCode}.model.uomType.uomTypeName`).d('单位类型名称'),
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get(`${promptCode}.model.uomType.uomTypeName`).d('单位类型名称')}
                field="uomTypeName"
                token={_token}
              />
            )}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`${promptCode}.model.uomType.baseUomName`).d('基本单位名称')}
          >
            {form.getFieldDecorator('baseUomName', {
              initialValue: data.baseUomName,
            })(
              <Lov
                textValue={data.baseUomName}
                code="HPFM.UOM"
                onChange={(rowKeys, record) => this.onHandleSelect(rowKeys, record)}
                queryParams={{ organizationId: getCurrentOrganizationId(), enabledFlag: 1 }}
              />
            )}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`${promptCode}.model.uomType.baseUomCode`).d('基本单位代码')}
          >
            {form.getFieldDecorator('baseUomCode', {
              initialValue: data.baseUomCode,
            })(<Input disabled />)}
          </Form.Item>
          <Form.Item
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get(`hzero.common.status.enable`).d('启用')}
          >
            {form.getFieldDecorator('enabledFlag', {
              initialValue: data.enabledFlag === 0 ? 0 : 1,
            })(<Switch />)}
          </Form.Item>
        </Form>
      </Modal>
    );
  }
}
