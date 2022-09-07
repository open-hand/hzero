/**
 * 流程定义 - 复制
 * @date: 2019-5-29
 * @author: jiacheng.wang <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React from 'react';
import { Form, Modal, Select, Spin, Input, InputNumber, Switch } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import { isNil, omit } from 'lodash';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import Lov from 'components/Lov';

import styles from './style/index.less';

@Form.create({ fieldNameProp: null })
export default class CopyValue extends React.Component {
  componentDidMount() {
    const { currentCopyRecord = {}, onFetchDocuments = (e) => e } = this.props;
    onFetchDocuments(currentCopyRecord.category);
  }

  @Bind()
  handleCategoryChange(value) {
    const {
      onFetchDocuments,
      form: { setFieldsValue },
    } = this.props;
    if (value) onFetchDocuments(value);
    setFieldsValue({ documentId: '', newKey: '', newName: '' });
  }

  @Bind()
  handleOk() {
    const { form, onOk = (e) => e, currentCopyRecord } = this.props;
    form.validateFields((error, values) => {
      if (!error) {
        const { overtime } = values;
        let params = values;
        if (!overtime) {
          params = omit(params, ['overtime', 'overtimeUnit']);
        }
        onOk({ oldKey: currentCopyRecord.key, ...params });
      }
    });
  }

  render() {
    const {
      form,
      onCancel,
      visible,
      dataLoading = false,
      loading = false,
      documents = [],
      currentCopyRecord = {},
      tenantId,
    } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    return (
      <Modal
        title={intl.get('hwfp.processDefine.view.title.copyValue').d('流程复制')}
        visible={visible}
        width={500}
        confirmLoading={loading}
        onOk={this.handleOk}
        onCancel={onCancel}
      >
        <Spin spinning={dataLoading}>
          <Form>
            <Form.Item
              label={intl.get('hwfp.common.model.process.code').d('流程编码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('newKey', {
                initialValue: currentCopyRecord.key,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hwfp.common.model.process.code').d('流程编码'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                  {
                    pattern: /^[A-Z][A-Z0-9-_./]*$/,
                    message: intl
                      .get('hzero.common.validation.codeUpperBegin')
                      .d('全大写及数字，必须以字母开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(<Input trim typeCase="upper" inputChinese={false} />)}
            </Form.Item>

            <Form.Item
              label={intl.get('hwfp.common.model.process.name').d('流程名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('newName', {
                initialValue: currentCopyRecord.name,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hwfp.common.model.process.name').d('流程名称'),
                    }),
                  },
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                ],
              })(<Input />)}
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.common.model.process.class').d('流程分类')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('categoryId', {
                initialValue: currentCopyRecord.category,
              })(
                <Lov
                  textValue={currentCopyRecord.categoryDescription}
                  code="HWFP.PROCESS_CATEGORY"
                  queryParams={{ tenantId }}
                  onChange={this.handleCategoryChange}
                />
              )}
            </Form.Item>
            <Form.Item
              label={intl.get('hwfp.common.model.documents.class').d('流程单据')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('documentId', {
                initialValue: currentCopyRecord.documentId,
                rules: [
                  {
                    required: !isNil(form.getFieldValue('categoryId')),
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hwfp.common.model.documents.class').d('流程单据'),
                    }),
                  },
                ],
              })(
                <Select allowClear={false} disabled={isNil(form.getFieldValue('categoryId'))}>
                  {documents.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            <Form.Item
              className={styles['inline-form-item']}
              label={intl.get('hwfp.processDefine.model.processDefine.timeout').d('超时时间')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              <span>
                {getFieldDecorator('overtime', {
                  initialValue: currentCopyRecord.overtime,
                  rules: [
                    {
                      required: getFieldValue('overtimeEnabled') === 1,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hwfp.processDefine.model.processDefine.timeout')
                          .d('超时时间'),
                      }),
                    },
                  ],
                })(<InputNumber min={0} />)}
              </span>
              <span>
                {getFieldDecorator('overtimeUnit', {
                  initialValue: currentCopyRecord.overtimeUnit || 'hour',
                })(
                  <Select allowClear style={{ width: '30%' }}>
                    <Select.Option value="hour">
                      {intl.get('hzero.common.date.unit.hours').d('小时')}
                    </Select.Option>
                    <Select.Option value="day">
                      {intl.get('hzero.common.date.unit.day').d('小时')}
                    </Select.Option>
                  </Select>
                )}
              </span>
            </Form.Item>
            <Form.Item
              label={intl
                .get('hwfp.processDefine.model.processDefine.overtimeEnabled')
                .d('超时设置启用')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('overtimeEnabled', {
                initialValue: currentCopyRecord.overtimeEnabled || 0,
              })(<Switch checkedValue={1} unCheckedValue={0} />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
