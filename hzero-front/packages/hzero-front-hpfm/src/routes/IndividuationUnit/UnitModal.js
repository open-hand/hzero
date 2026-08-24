import React, { Component } from 'react';
import { Drawer, Form, Input, Button, Select, Switch, Row } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import Lov from 'components/Lov';
import { CODE_UPPER } from 'utils/regExp';
import { getSingleTenantValueCode } from '@/utils/constConfig';
import styles from './style/index.less';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
export default class UnitModal extends Component {
  @Bind()
  create() {
    const { form, groupCode, handleCreate = () => {} } = this.props;
    form.validateFields((err, values = {}) => {
      if (!err) {
        const { sqlIds = [], unitCode = '' } = values;
        const params = {
          ...values,
          unitCode: groupCode.concat('.').concat(unitCode),
          sqlIds: sqlIds.join(','),
        };
        handleCreate(params);
      }
    });
  }

  render() {
    const {
      visible,
      groupName,
      groupCode = '',
      createListLoading,
      unitTypeOptions = [],
      handleClose = () => {},
      form: { getFieldDecorator = () => {}, getFieldValue },
    } = this.props;

    return (
      <Drawer
        width={400}
        title={intl.get('hpfm.individuationUnit.view.message.title.createUnit').d('新建个性化单元')}
        visible={visible}
        closable
        destroyOnClose
        onClose={handleClose}
      >
        <Form layout="vertical" className={styles['unit-editor-form']}>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.unitCode')
              .d('单元编码')}
          >
            {getFieldDecorator('unitCode', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitCode')
                        .d('单元编码'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitCode')
                        .d('单元编码')}不能为空`
                    ),
                },
                {
                  pattern: CODE_UPPER,
                  message: intl
                    .get('hzero.common.validation.codeUpper')
                    .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                },
                {
                  max: 50,
                  message: intl.get('hzero.common.validation.max', {
                    max: 50,
                  }),
                },
              ],
            })(<Input typeCase="upper" addonBefore={(groupCode || '').concat('.')} />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.unitName')
              .d('单元名称')}
          >
            {getFieldDecorator('unitName', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitName')
                        .d('单元名称'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitName')
                        .d('单元名称')}不能为空`
                    ),
                },
                {
                  max: 50,
                  message: intl.get('hzero.common.validation.max', {
                    max: 50,
                  }),
                },
              ],
            })(<Input />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.unitType')
              .d('单元类型')}
          >
            {getFieldDecorator('unitType', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitType')
                        .d('单元类型'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.unitType')
                        .d('单元类型')}不能为空`
                    ),
                },
              ],
            })(
              <Select>
                {unitTypeOptions.map((item) => (
                  <Option value={item.value}>{item.meaning}</Option>
                ))}
              </Select>
            )}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.relateGroup')
              .d('所属单元组')}
          >
            {getFieldDecorator('unitGroupId', {
              initialValue: groupName,
            })(<Input disabled />)}
          </FormItem>
          <FormItem
            label={intl
              .get('hpfm.individuationUnit.model.individuationUnit.modelName')
              .d('关联主模型')}
            style={{ display: getFieldValue('unitType') === 'TABPANE' ? 'none' : 'block' }}
          >
            {getFieldDecorator('modelId', {
              initialValue: getFieldValue('unitType') === 'TABPANE' ? -1 : undefined,
              rules: [
                {
                  required: getFieldValue('unitType') !== 'TABPANE',
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                        .d('关联主模型'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                        .d('关联主模型')}不能为空`
                    ),
                },
              ],
            })(<Lov code={getSingleTenantValueCode('HPFM.CUST.MODEL_VIEW')} />)}
          </FormItem>
          <FormItem
            label={intl.get('hpfm.individuationUnit.model.individuationUnit.sqlIds').d('SQL IDs')}
            style={{ display: getFieldValue('unitType') === 'TABPANE' ? 'none' : 'block' }}
          >
            {getFieldDecorator(
              'sqlIds',
              {}
            )(<Select mode="tags" dropdownClassName={styles['sqlIds-select-options']} />)}
          </FormItem>
        </Form>
        <Form
          layout="inline"
          className={`${styles['unit-editor-form']} ${styles['inline-form']}`}
          style={{ marginBottom: 50 }}
        >
          <Row>
            <FormItem
              label={intl.get('hpfm.individuationUnit.model.individuationUnit.readOnly').d('只读')}
              labelCol={{ span: 4 }}
              wrapperCol={{ span: 20 }}
            >
              {getFieldDecorator('readOnly', {
                initialValue: 0,
              })(<Switch checkedValue={1} unCheckedValue={0} />)}
            </FormItem>
          </Row>
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button style={{ marginRight: 8 }} disabled={createListLoading} onClick={handleClose}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button type="primary" loading={createListLoading} onClick={this.create}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
