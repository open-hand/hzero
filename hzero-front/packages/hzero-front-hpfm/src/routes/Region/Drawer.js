import React from 'react';
import { Form, Input, Modal } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import TLEditor from 'components/TLEditor';

import { CODE_UPPER } from 'utils/regExp';
import intl from 'utils/intl';
import { MODAL_FORM_ITEM_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;
@Form.create({ fieldNameProp: null })
export default class Drawer extends React.PureComponent {
  @Bind()
  handleOK() {
    const { form, onOk } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue);
      }
    });
  }

  render() {
    const { form, initData, title, loading, modalVisible, onCancel } = this.props;
    const { getFieldDecorator } = form;
    const { regionCode, regionName, quickIndex, _token } = initData;
    return (
      <Modal
        destroyOnClose
        title={title}
        visible={modalVisible}
        confirmLoading={loading}
        onCancel={onCancel}
        onOk={this.handleOK}
        width={520}
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
      >
        <Form>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.regionCode').d('区域代码')}
          >
            {getFieldDecorator('regionCode', {
              initialValue: regionCode,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.region.model.region.regionCode').d('区域代码'),
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
            })(<Input trim inputChinese={false} typeCase="upper" disabled={!!regionCode} />)}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.regionName').d('区域名称')}
          >
            {getFieldDecorator('regionName', {
              initialValue: regionName,
              rules: [
                {
                  required: true,
                  message: intl.get('hzero.common.validation.notNull', {
                    name: intl.get('hpfm.region.model.region.regionName').d('区域名称'),
                  }),
                },
                {
                  max: 120,
                  message: intl.get('hzero.common.validation.max', {
                    max: 120,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.region.model.region.regionName').d('区域名称')}
                field="regionName"
                token={_token}
              />
            )}
          </FormItem>
          <FormItem
            {...MODAL_FORM_ITEM_LAYOUT}
            label={intl.get('hpfm.region.model.region.quickIndex').d('快速索引')}
          >
            {getFieldDecorator('quickIndex', {
              initialValue: quickIndex,
              rules: [
                {
                  max: 30,
                  message: intl.get('hzero.common.validation.max', {
                    max: 30,
                  }),
                },
              ],
            })(
              <TLEditor
                label={intl.get('hpfm.region.model.region.quickIndex').d('快速索引')}
                field="quickIndex"
                token={_token}
              />
            )}
          </FormItem>
        </Form>
      </Modal>
    );
  }
}
