/**
 * 卡片编辑模态框
 * todo 卡片编码 只做必输校验, 准确性由 使用者保证
 * todo 数值的长度限制 由组件决定 没有表单校验
 * @date 2019-01-21
 * @author WY yang.wang06@hand-china.com
 * @copyright © HAND 2019
 */
import React from 'react';
import PropTypes from 'prop-types';
import { Bind } from 'lodash-decorators';
import { Modal, Form, Input, InputNumber, Select, Spin } from 'hzero-ui';

import Switch from 'components/Switch';
import TLEditor from 'components/TLEditor';

import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import { CODE } from 'utils/regExp';

/**
 * 卡片编辑模态框
 * @ReactProps {boolean} [isEdit=false] - 编辑状态
 * @ReactProps {object} [editRecord] - 编辑数据, 当是编辑状态时 必输
 * @ReactProps {object[]} catalogType - 分类的值集
 * @ReactProps {object[]} fdLevel - 层级
 * @ReactProps {object} modalProps - 传递给Modal的属性
 * @ReactProps {Function} onOk - 点击确认按钮且表单校验通过时的回调
 * @ReactProps {Function} onCancel - 取消编辑
 */
@Form.create({ fieldNameProp: null })
export default class CardEditModal extends React.Component {
  static propTypes = {
    onOk: PropTypes.func.isRequired,
    onCancel: PropTypes.func.isRequired,
    catalogType: PropTypes.array.isRequired,
    fdLevel: PropTypes.array.isRequired,
  };

  render() {
    const {
      isEdit = false,
      editRecord = {},
      form,
      catalogType,
      fdLevel,
      modalProps,
      confirmLoading,
      detailLoading = false,
    } = this.props;
    const modalTitle = isEdit
      ? intl.get('hpfm.card.view.title.cardEdit').d('卡片编辑')
      : intl.get('hpfm.card.view.title.cardCreate').d('卡片新建');
    // const isEditAndDisabled = isEdit && editRecord.enabledFlag === 0;
    return (
      <Modal
        title={modalTitle}
        width="520px"
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        confirmLoading={confirmLoading}
        {...modalProps}
        onOk={this.handleModalOkBtnClick}
        onCancel={this.handleModalCancelBtnClick}
        afterClose={this.handleAfterModalClose}
      >
        <Spin spinning={detailLoading}>
          <Form>
            <Form.Item
              key="code"
              label={intl.get('hpfm.card.model.card.code').d('卡片代码')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('code', {
                initialValue: isEdit ? editRecord.code : undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.code').d('卡片代码'),
                    }),
                  },
                  {
                    pattern: CODE,
                    message: intl
                      .get('hzero.common.validation.code')
                      .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                  {
                    max: 250,
                    message: intl.get('hzero.common.validation.max', {
                      max: 250,
                    }),
                  },
                ],
              })(<Input trim inputChinese={false} disabled={isEdit} />)}
            </Form.Item>
            <Form.Item
              key="cardParams"
              label={intl.get('hpfm.card.model.card.cardParams').d('卡片参数')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('cardParams', {
                initialValue: isEdit ? editRecord.cardParams : undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.cardParams').d('卡片参数'),
                    }),
                  },
                  {
                    pattern: /^[a-zA-Z][a-zA-Z0-9-_./=&]*$/,
                    message: intl
                      .get('hpfm.card.view.message.code')
                      .d('大小写及数字，必须以字母开头，可包含“-”、“_”、“.”、“/”、“=”、“&”'),
                  },
                  {
                    max: 240,
                    message: intl.get('hzero.common.validation.max', {
                      max: 240,
                    }),
                  },
                ],
              })(<Input trim inputChinese={false} />)}
            </Form.Item>
            <Form.Item
              key="name"
              label={intl.get('hpfm.card.model.card.name').d('卡片名称')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('name', {
                initialValue: isEdit ? editRecord.name : undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.name').d('卡片名称'),
                    }),
                  },
                  {
                    max: 32,
                    message: intl.get('hzero.common.validation.max', {
                      max: 32,
                    }),
                  },
                ],
              })(
                <TLEditor
                  label={intl.get('hpfm.card.model.card.name').d('卡片名称')}
                  field="name"
                  token={isEdit ? editRecord._token : undefined}
                />
              )}
            </Form.Item>
            <Form.Item
              key="description"
              label={intl.get('hpfm.card.model.card.description').d('卡片描述')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('description', {
                initialValue: isEdit ? editRecord.description : undefined,
              })(
                <TLEditor
                  label={intl.get('hpfm.card.model.card.description').d('卡片描述')}
                  field="description"
                  token={isEdit ? editRecord._token : undefined}
                />
              )}
            </Form.Item>
            <Form.Item
              key="catalogType"
              label={intl.get('hpfm.card.model.card.catalogType').d('卡片类型')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('catalogType', {
                initialValue: isEdit ? editRecord.catalogType : undefined,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.catalogType').d('卡片类型'),
                    }),
                  },
                ],
              })(
                <Select>
                  {catalogType.map((item) => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form.Item>
            {isTenantRoleLevel() || (
              <Form.Item
                key="fdLevel"
                label={intl.get('hpfm.card.model.card.fdLevel').d('层级')}
                {...MODAL_FORM_ITEM_LAYOUT}
              >
                {form.getFieldDecorator('level', {
                  initialValue: isEdit ? editRecord.level : undefined,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('hpfm.card.model.card.fdLevel').d('层级'),
                      }),
                    },
                  ],
                })(
                  <Select>
                    {fdLevel.map((item) => (
                      <Select.Option value={item.value} key={item.value}>
                        {item.meaning}
                      </Select.Option>
                    ))}
                  </Select>
                )}
              </Form.Item>
            )}
            <Form.Item
              key="w"
              label={intl.get('hpfm.card.model.card.w').d('宽度')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('w', {
                initialValue: isEdit ? editRecord.w : 1,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.w').d('宽度'),
                    }),
                  },
                ],
              })(<InputNumber min={1} precision={0} step={1} />)}
            </Form.Item>
            <Form.Item
              key="h"
              label={intl.get('hpfm.card.model.card.h').d('高度')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('h', {
                initialValue: isEdit ? editRecord.h : 1,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hpfm.card.model.card.h').d('高度'),
                    }),
                  },
                ],
              })(<InputNumber min={1} precision={0} step={1} />)}
            </Form.Item>
            <Form.Item
              key="enabledFlag"
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...MODAL_FORM_ITEM_LAYOUT}
            >
              {form.getFieldDecorator('enabledFlag', {
                initialValue: isEdit ? editRecord.enabledFlag : 1,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hzero.common.status.enable').d('启用'),
                    }),
                  },
                ],
              })(<Switch />)}
            </Form.Item>
          </Form>
        </Spin>
      </Modal>
    );
  }

  /**
   * 模态框确认按钮点击
   * 校验表单 校验成功 回调 onOk
   */
  @Bind()
  handleModalOkBtnClick() {
    const { form, onOk } = this.props;
    form.validateFields((err, formFields) => {
      if (!err) {
        onOk(formFields);
      }
    });
  }

  /**
   * 模态框取消按钮点击
   * 回调 onCancel
   */
  @Bind()
  handleModalCancelBtnClick() {
    const { onCancel } = this.props;
    onCancel();
  }

  /**
   * 模态框关闭后 重置表单内容
   */
  @Bind()
  handleAfterModalClose() {
    const { form } = this.props;
    form.resetFields();
  }
}
