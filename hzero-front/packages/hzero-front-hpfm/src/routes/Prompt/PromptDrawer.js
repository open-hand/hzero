import React from 'react';
import { Form, Input, Modal, Button, Col, Row, Spin } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import intl from 'utils/intl';

import { CODE } from 'utils/regExp';
import { FORM_COL_2_LAYOUT } from 'utils/constants';

const FormItem = Form.Item;

const formLayout = {
  labelCol: { span: 5 },
  wrapperCol: { span: 17 },
};

@Form.create({ fieldNameProp: null })
export default class PromptForm extends React.Component {
  @Bind()
  handleOk() {
    const { form, onOk, promptDetail = {} } = this.props;
    form.validateFields((err, fieldsValue) => {
      if (!err) {
        onOk(fieldsValue, promptDetail);
      }
    });
  }

  render() {
    const {
      form,
      initData,
      languageList,
      title,
      modalVisible,
      loading,
      updatePromptLoading,
      fetchPromptDetailLoading,
      onCancel,
      promptDetail = {},
      currentLanguage,
    } = this.props;
    const { getFieldDecorator } = form;
    const { promptId, promptKey, promptCode } = initData;
    const { promptConfigs = {} } = promptDetail;
    return (
      <Modal
        destroyOnClose
        wrapClassName="ant-modal-sidebar-right"
        transitionName="move-right"
        title={title}
        visible={modalVisible}
        confirmLoading={loading || updatePromptLoading}
        onCancel={onCancel}
        width="1000px"
        footer={[
          <Button key="cancel" onClick={onCancel}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>,
          <Button
            key="on"
            type="primary"
            loading={loading || updatePromptLoading}
            onClick={this.handleOk}
          >
            {intl.get('hzero.common.button.ok').d('确定')}
          </Button>,
        ]}
      >
        <Spin spinning={promptId !== undefined ? fetchPromptDetailLoading : false}>
          <Form>
            <Row>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hpfm.prompt.model.prompt.promptKey').d('模板代码')}
                >
                  {getFieldDecorator('promptKey', {
                    initialValue: promptKey,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.prompt.model.prompt.promptKey').d('模板代码'),
                        }),
                      },
                      {
                        pattern: CODE,
                        message: intl
                          .get('hzero.common.validation.code')
                          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                      {
                        max: 60,
                        message: intl.get('hzero.common.validation.max', {
                          max: 60,
                        }),
                      },
                    ],
                  })(<Input inputChinese={false} disabled={promptId !== undefined} />)}
                </FormItem>
              </Col>
              <Col {...FORM_COL_2_LAYOUT}>
                <FormItem
                  {...formLayout}
                  label={intl.get('hpfm.prompt.model.prompt.promptCode').d('代码')}
                >
                  {getFieldDecorator('promptCode', {
                    initialValue: promptCode,
                    rules: [
                      {
                        type: 'string',
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl.get('hpfm.prompt.model.prompt.promptCode').d('代码'),
                        }),
                      },
                      {
                        pattern: CODE,
                        message: intl
                          .get('hzero.common.validation.code')
                          .d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                      },
                      {
                        max: 240,
                        message: intl.get('hzero.common.validation.max', {
                          max: 240,
                        }),
                      },
                    ],
                  })(<Input trim inputChinese={false} disabled={promptId !== undefined} />)}
                </FormItem>
              </Col>
            </Row>
            {languageList.map((item) => {
              return (
                <Row>
                  <Col {...FORM_COL_2_LAYOUT}>
                    <FormItem
                      {...formLayout}
                      label={intl.get('hpfm.prompt.model.prompt.lang').d('语言')}
                    >
                      <Input value={item.description} disabled />
                    </FormItem>
                  </Col>
                  <Col {...FORM_COL_2_LAYOUT}>
                    <FormItem
                      {...formLayout}
                      label={intl.get('hpfm.prompt.model.prompt.description').d('描述')}
                    >
                      {getFieldDecorator(`promptConfigs[${item.code}]`, {
                        initialValue: promptId !== undefined ? promptConfigs[item.code] : null,
                        rules: [
                          {
                            required: item.code === currentLanguage,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('hpfm.prompt.model.prompt.description').d('描述'),
                            }),
                          },
                          {
                            max: 750,
                            message: intl.get('hzero.common.validation.max', {
                              max: 750,
                            }),
                          },
                        ],
                      })(<Input dbc2sbc={false} />)}
                    </FormItem>
                  </Col>
                </Row>
              );
            })}
          </Form>
        </Spin>
      </Modal>
    );
  }
}
