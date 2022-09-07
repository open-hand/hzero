import React, { Component } from 'react';
import { Drawer, Form, Input, Button } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import { CODE_UPPER } from 'utils/regExp';

import styles from './style/index.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class UnitModal extends Component {
  @Bind()
  copy() {
    const { form, groupCode, handleCopyUnit = () => {} } = this.props;
    form.validateFields((err, values = {}) => {
      if (!err) {
        const { unitCode = '' } = values;
        const params = {
          ...values,
          unitCode: groupCode.concat('.').concat(unitCode),
        };
        handleCopyUnit(params);
      }
    });
  }

  render() {
    const {
      visible,
      groupCode = '',
      loading,
      handleClose = () => {},
      form: { getFieldDecorator = () => {} },
    } = this.props;

    return (
      <Drawer
        width={400}
        title={intl.get('hpfm.individuationUnit.view.message.title.copyUnit').d('复制个性化单元')}
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
              .get('hpfm.individuationUnit.model.individuationUnit.copyUnitCode')
              .d('复制的单元编码')}
          >
            {getFieldDecorator('copyUnitCode', {
              rules: [
                {
                  required: true,
                  message: intl
                    .get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hpfm.individuationUnit.model.individuationUnit.copyUnitCode')
                        .d('复制的单元编码'),
                    })
                    .d(
                      `${intl
                        .get('hpfm.individuationUnit.model.individuationUnit.copyUnitCode')
                        .d('复制的单元编码')}不能为空`
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
            })(<Input typeCase="upper" />)}
          </FormItem>
        </Form>
        <div className={styles['model-bottom-button']}>
          <Button style={{ marginRight: 8 }} disabled={loading} onClick={handleClose}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button type="primary" loading={loading} onClick={this.copy}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
