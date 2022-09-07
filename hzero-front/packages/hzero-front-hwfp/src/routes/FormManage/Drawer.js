import React, { PureComponent } from 'react';
import { Modal, Form, Input, Select } from 'hzero-ui';
import { isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import Switch from 'components/Switch';

import intl from 'utils/intl';
import { CODE } from 'utils/regExp';

const FormItem = Form.Item;
const { Option } = Select;

const formLayout = {
  labelCol: { span: 6 },
  wrapperCol: { span: 16 },
};

/**
 * 新建或编辑模态框数据展示
 * @extends {PureComponent} - React.PureComponent
 * @reactProps {Function} onAdd - 添加确定的回调函数
 * @reactProps {Function} onEdit - 编辑确定的回调函数
 * @reactProps {Object} tableRecord - 表格中信息的一条记录
 * @reactProps {Boolean} isCreate - 是否为新建账户
 * @reactProps {String} anchor - 模态框弹出方向
 * @return React.element
 */
@Form.create({ fieldNameProp: null })
export default class Drawer extends PureComponent {
  @Bind()
  onOk() {
    const { form, onAdd, isCreate, tableRecord, onEdit } = this.props;
    const { formDefinitionId, objectVersionNumber } = tableRecord;
    form.validateFields((err, values) => {
      if (isEmpty(err)) {
        if (isCreate) {
          onAdd(values);
        } else {
          onEdit({ formDefinitionId, objectVersionNumber, ...values });
        }
      }
    });
  }

  /**
   * 校验编码唯一性
   *
   * @param {*} rule
   * @param {*} value
   * @param {*} callback
   * @memberof Drawer
   */
  @Bind()
  checkUniqueCode(rule, value, callback) {
    const {
      onCheck,
      form: { getFieldValue },
    } = this.props;
    const codeValue = getFieldValue('code');
    onCheck(rule, value, callback, codeValue);
  }

  render() {
    const { visible, onCancel, saving, anchor, tableRecord, isCreate, category } = this.props;
    const { getFieldDecorator } = this.props.form;
    return (
      <Modal
        destroyOnClose
        width={520}
        title={
          isCreate
            ? intl.get('hwfp.formManage.view.message.create').d('新建表单管理')
            : intl.get('hwfp.formManage.view.message.edit').d('编辑表单管理')
        }
        visible={visible}
        onCancel={onCancel}
        onOk={this.onOk}
        confirmLoading={saving}
        wrapClassName={`ant-modal-sidebar-${anchor}`}
        transitionName={`move-${anchor}`}
      >
        <Form>
          <FormItem
            label={intl.get('hwfp.formManage.model.formManage.code').d('表单编码')}
            {...formLayout}
          >
            {getFieldDecorator('code', {
              validateTrigger: 'onBlur',
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.formManage.model.formManage.code').d('表单编码'),
                  }),
                },
                {
                  pattern: CODE,
                  message: intl
                    .get('hzero.common.validation.code')
                    .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  validator: isCreate ? this.checkUniqueCode : '',
                },
              ],
              initialValue: tableRecord ? tableRecord.code : '',
            })(<Input trim inputChinese={false} disabled={!isCreate} />)}
          </FormItem>
          <FormItem
            label={intl.get('hwfp.common.model.process.class').d('流程分类')}
            {...formLayout}
          >
            {getFieldDecorator('category', {
              // rules: [
              //   {
              //     required: true,
              //     message: intl.get('hzero.common.validation.notNull', {
              //       name: intl
              //         .get('hwfp.formManage.model.formManage.category')
              //         .d('流程分类'),
              //     }),
              //   },
              // ],
              initialValue: tableRecord.category,
            })(
              <Select allowClear>
                {category &&
                  category.map(item => (
                    <Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Option>
                  ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            label={intl.get('hwfp.formManage.model.formManage.url').d('表单url')}
            {...formLayout}
          >
            {getFieldDecorator('url', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.formManage.model.formManage.url').d('表单url'),
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.url : '',
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hwfp.common.model.common.description').d('描述')}
            {...formLayout}
          >
            {getFieldDecorator('description', {
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hwfp.common.model.common.description').d('描述'),
                  }),
                },
              ],
              initialValue: tableRecord ? tableRecord.description : '',
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl.get('hwfp.formManage.model.formManage.invokeFlag').d('是否回调')}
            {...formLayout}
          >
            {getFieldDecorator('invokeFlag', {
              initialValue: isUndefined(tableRecord.invokeFlag) ? 1 : tableRecord.invokeFlag,
            })(<Switch />)}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
