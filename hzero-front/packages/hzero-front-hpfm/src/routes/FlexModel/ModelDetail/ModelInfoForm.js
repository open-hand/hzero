import React, { Component } from 'react';
import { Form, Row, Col, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import notification from 'utils/notification';
import { FORM_COL_3_LAYOUT } from 'utils/constants';
import intl from 'utils/intl';

import styles from '../style/index.less';

const FormItem = Form.Item;
const formsLayouts = { labelCol: { span: 5 }, wrapperCol: { span: 13 } };
const UEDDisplayFormItem = props => {
  const { label, value } = props;
  return (
    <FormItem label={label} {...formsLayouts}>
      {value}
    </FormItem>
  );
};

@Form.create({ fieldNameProp: null })
export default class ModelInfoForm extends Component {
  @Bind()
  handleSaveModel(value = '') {
    const {
      modelInfo = {},
      form = {},
      onSaveModalName = () => {},
      fetchModel = () => {},
    } = this.props;
    const { modelName, objectVersionNumber, modelId, supportMultiLang } = modelInfo;
    if (value !== modelName) {
      form.validateFields((err, values) => {
        if (!err) {
          const params = {
            ...values,
            modelId,
            supportMultiLang,
            objectVersionNumber,
          };
          onSaveModalName(params).then(res => {
            if (res) {
              notification.success();
              fetchModel({ modelId });
            }
          });
        }
      });
    }
  }

  render() {
    const { modelInfo = {}, form = {} } = this.props;
    const { modelCode, modelName, modelTable, serviceName } = modelInfo;
    const { getFieldDecorator } = form;
    return (
      <Form
        className={`writable-row-custom ${styles['model-writable-row-custom']}`}
        style={{ padding: '0 16px 8px', align: 'left' }}
      >
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.modelCode').d('模型编码')}
              value={modelCode}
            />
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <FormItem
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.modelName').d('模型名称')}
              {...formsLayouts}
              className={styles['modelInfo-form-modelName-label']}
            >
              {getFieldDecorator('modelName', {
                initialValue: modelName,
                rules: [
                  {
                    required: true,
                    message: intl
                      .get('hzero.common.validation.notNull', {
                        name: intl
                          .get('hpfm.flexModelDetail.model.flexModelDetail.modelName')
                          .d('模型名称'),
                      })
                      .d(
                        `${intl
                          .get('hpfm.flexModelDetail.model.flexModelDetail.modelName')
                          .d('模型名称')}不能为空`
                      ),
                  },
                  {
                    max: 30,
                    message: intl.get('hzero.common.validation.max', {
                      max: 30,
                    }),
                  },
                ],
              })(<Input onBlur={e => this.handleSaveModel(e.target.value)} />)}
            </FormItem>
          </Col>
        </Row>
        <Row gutter={48}>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.modelTable').d('模型表')}
              value={modelTable}
            />
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <UEDDisplayFormItem
              label={intl
                .get('hpfm.flexModelDetail.model.flexModelDetail.serviceName')
                .d('服务名称')}
              value={serviceName}
            />
          </Col>
        </Row>
      </Form>
    );
  }
}
