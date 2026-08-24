/**
 * DataHierarchies 数据层级配置
 * @date: 2019-8-14
 * @author: jinmingyang <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { Form, Modal, Input, InputNumber, Spin, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import Lov from 'components/Lov';
import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

@Form.create({ fieldNameProp: null })
export default class Drawer extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk, initData, organizationId } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk({ tenantId: organizationId, ...initData, ...fieldsValue });
      }
    });
  }

  @Bind()
  handleCancel() {
    const { onCancel } = this.props;
    onCancel();
  }

  render() {
    const {
      title,
      loading,
      initLoading,
      modalVisible,
      initData,
      organizationId,
      form,
      addFlag,
      editFlag,
      displayList = [],
    } = this.props;
    const {
      dataHierarchyCode,
      dataHierarchyName,
      parentId,
      parentName,
      valueSourceId,
      valueSourceName,
      orderSeq,
      enabledFlag,
      _token,
      displayStyle,
    } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={loading}
        onCancel={this.handleCancel}
        onOk={this.handleOk}
      >
        <Spin spinning={initLoading}>
          <Form>
            {/* {isSiteFlag && (
              <Form.Item
                label={intl.get('hpfm.dataHierarchies.model.dataHierarchies.tenantName').d('租户')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {form.getFieldDecorator('tenantId', {
                  initialValue: tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.dataHierarchies.model.dataHierarchies.tenantName')
                          .d('租户名称'),
                      }),
                    },
                  ],
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={tenantName}
                    allowClear
                  />
                )}
              </Form.Item>
            )} */}
            <Form.Item
              label={intl
                .get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyCode')
                .d('编码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('dataHierarchyCode', {
                initialValue: dataHierarchyCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyCode')
                        .d('编码'),
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
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(
                <Input
                  inputChinese={false}
                  typeCase="upper"
                  disabled={editFlag ? !addFlag : false}
                />
              )}
            </Form.Item>
            <Form.Item
              label={intl
                .get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyName')
                .d('名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('dataHierarchyName', {
                initialValue: dataHierarchyName,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyName')
                        .d('名称'),
                    }),
                  },
                  {
                    max: 60,
                    message: intl.get('hzero.common.validation.max', {
                      max: 60,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl
                    .get('hpfm.dataHierarchies.model.dataHierarchies.dataHierarchyName')
                    .d('名称')}
                  inputSize={{ zh: 60, en: 60 }}
                  field="dataHierarchyName"
                  token={_token}
                />
              )}
            </Form.Item>
            <Form.Item
              label={intl.get('hpfm.dataHierarchies.model.dataHierarchies.parentId').d('上级配置')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('parentId', {
                initialValue: parentId,
              })(
                <Lov
                  code="HPFM.DATA_HIERARCHY"
                  textValue={parentName}
                  queryParams={{ tenantId: organizationId }}
                  // textField='dataHierarchyName'
                  disabled={editFlag}
                  allowClear
                />
              )}
            </Form.Item>
            <Form.Item
              label={intl
                .get('hpfm.dataHierarchies.model.dataHierarchies.valueSourceId')
                .d('值来源')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('valueSourceId', {
                initialValue: valueSourceId,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataHierarchies.model.dataHierarchies.valueSourceId')
                        .d('值来源'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HPFM.LOV_VIEW"
                  textValue={valueSourceName}
                  // textField='viewCode'
                  queryParams={{ tenantId: organizationId }}
                  // disabled={editFlag}
                  allowClear
                />
              )}
            </Form.Item>
            <Form.Item
              label={intl
                .get('hpfm.dataHierarchies.model.dataHierarchies.displayStyle')
                .d('显示样式')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('displayStyle', {
                initialValue: displayStyle,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataHierarchies.model.dataHierarchies.displayStyle')
                        .d('显示样式'),
                    }),
                  },
                ],
              })(
                <Select allowClear disabled={!!parentName}>
                  {displayList.map((item) => (
                    <Select.Option key={item.value} value={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            <Form.Item
              label={intl.get('hpfm.dataHierarchies.model.dataHierarchies.orderSeq').d('排序')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('orderSeq', {
                initialValue: orderSeq,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.dataHierarchies.model.dataHierarchies.orderSeq')
                        .d('排序'),
                    }),
                  },
                ],
              })(<InputNumber min={0} precision={0} />)}
            </Form.Item>
            <Form.Item
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('enabledFlag', {
                initialValue: enabledFlag === undefined ? 1 : enabledFlag,
              })(<Switch disabled={!!parentName} />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }
}
