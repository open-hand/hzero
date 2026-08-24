import React from 'react';
import { Form, Modal, Popconfirm, Icon, Input, Tooltip } from 'hzero-ui';
import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import Lov from 'components/Lov';
import styles from './index.less';

const FormItem = Form.Item;

@Form.create({ fieldNameProp: null })
export default class RelatedModal extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      // loading: false,
      fieldLovMaps: [],
    };
  }

  componentDidMount() {
    const { fieldLovMaps } = this.props;
    this.setState({ fieldLovMaps: [...fieldLovMaps] });
  }

  @Bind()
  onOk() {
    const { record, form, onClose, updateLovMappings } = this.props;
    const { fieldLovMaps } = this.state;
    form.validateFields((err, values) => {
      if (err) return;
      const newFieldLovMaps = fieldLovMaps
        .map((_, index) => {
          if (!_) return false;
          return {
            ..._,
            sourceModelId: values[`sourceModelId#${index}`],
            modelName: values[`modelName#${index}`],
            sourceFieldAlias: values[`sourceFieldAlias#${index}`],
            sourceFieldCode: values[`sourceFieldCode#${index}`],
            sourceFieldId: values[`sourceFieldId#${index}`],
            sourceFieldName: values[`sourceFieldName#${index}`],
            targetFieldId: values[`targetFieldId#${index}`],
            targetFieldName: values[`targetFieldName#${index}`],
          };
        })
        .filter(Boolean);
      record.fieldLovMaps = newFieldLovMaps;
      updateLovMappings(newFieldLovMaps);
      onClose();
    });
  }

  setFieldInfo(record, i) {
    const { form } = this.props;
    const { fieldCode, fieldId, fieldName } = record;
    form.setFieldsValue({
      [`sourceFieldAlias#${i}`]: fieldCode,
      [`sourceFieldId#${i}`]: fieldId,
      [`sourceFieldName#${i}`]: fieldName,
    });
  }

  setModelName(record, i) {
    const { form } = this.props;
    const { modelName } = record;
    form.setFieldsValue({
      [`modelName#${i}`]: modelName,
    });
  }

  setTargetField(record, i) {
    const { form } = this.props;
    const { fieldName } = record;
    form.setFieldsValue({
      [`targetFieldName#${i}`]: fieldName,
    });
  }

  onDelete(index) {
    const { fieldLovMaps } = this.state;
    this.setState({ fieldLovMaps: fieldLovMaps.map((_, i) => (i === index ? undefined : _)) });
  }

  @Bind()
  addMap() {
    const { fieldLovMaps } = this.state;
    fieldLovMaps.push({});
    this.setState({ fieldLovMaps });
  }

  render() {
    const { fieldLovMaps } = this.state;
    const { visible, onClose, form, id } = this.props;

    return (
      <Modal
        destroyOnClose
        maskClosable
        width={780}
        visible={visible}
        onCancel={onClose}
        onOk={this.onOk}
        bodyStyle={{ padding: '12px' }}
        title={intl.get('hpfm.individual.view.message.title.relatedField').d('设置关联字段')}
      >
        {fieldLovMaps.map((i, index) => {
          if (!i) return null;
          return (
            <div className={styles['lov-map-wrap']}>
              <div className="container">
                <FormItem>
                  {form.getFieldDecorator(`sourceModelId#${index}`, {
                    initialValue: i.sourceModelId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.individual.model.config.modelCategory')
                            .d('所属模型'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HPFM.CUST.MODEL_VIEW.ORG"
                      onChange={(_, r) => this.setModelName(r, index)}
                      textValue={i.modelName}
                      placeHolder={intl
                        .get('hpfm.individual.model.config.modelCategory')
                        .d('所属模型')}
                    />
                  )}
                  {form.getFieldDecorator(`modelName#${index}`, {
                    initialValue: i.modelName,
                  })}
                </FormItem>
                <FormItem>
                  {form.getFieldDecorator(`sourceFieldId#${index}`, {
                    initialValue: i.sourceFieldId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.individual.model.config.sourceGridField')
                            .d('Lov表字段'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HPFM.CUST.MODEL_FIELD"
                      lovOptions={{ valueField: 'fieldId', displayField: 'fieldName' }}
                      queryParams={{ modelId: form.getFieldValue(`sourceModelId#${index}`) }}
                      textValue={i.sourceFieldName}
                      disabled={form.getFieldValue(`sourceModelId#${index}`) === undefined}
                      onChange={(_, r) => this.setFieldInfo(r, index)}
                      placeHolder={intl
                        .get('hpfm.individual.model.config.sourceGridField')
                        .d('Lov表字段')}
                    />
                  )}
                  {form.getFieldDecorator(`sourceFieldName#${index}`, {
                    initialValue: i.sourceFieldName,
                  })}
                  {form.getFieldDecorator(`sourceFieldCode#${index}`, {
                    initialValue: i.sourceFieldCode,
                  })}
                </FormItem>
                <FormItem>
                  {form.getFieldDecorator(`sourceFieldAlias#${index}`, {
                    initialValue: i.sourceFieldAlias,
                  })(
                    <Input
                      placeHolder={intl
                        .get('hpfm.individual.model.config.sourceFieldAlias')
                        .d('Lov表字段别名')}
                    />
                  )}
                </FormItem>
              </div>
              <Icon type="arrow-right" />
              <div className="container">
                <FormItem>
                  {form.getFieldDecorator(`targetFieldId#${index}`, {
                    initialValue: i.targetFieldId,
                    rules: [
                      {
                        required: true,
                        message: intl.get('hzero.common.validation.notNull', {
                          name: intl
                            .get('hpfm.individual.model.config.targetField')
                            .d('映射字段编码'),
                        }),
                      },
                    ],
                  })(
                    <Lov
                      code="HPFM.CUST.CONFIG_FIELD_VIEW"
                      queryParams={{ unitId: id }}
                      lovOptions={{ valueField: 'fieldId', displayField: 'fieldName' }}
                      onChange={(_, r) => this.setTargetField(r, index)}
                      textValue={i.targetFieldName}
                      placeHolder={intl
                        .get('hpfm.individual.model.config.targetField')
                        .d('映射字段编码')}
                    />
                  )}
                  {form.getFieldDecorator(`targetFieldName#${index}`, {
                    initialValue: i.targetFieldName,
                  })}
                </FormItem>
              </div>
              <Popconfirm
                title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录')}
                okText={intl.get('hzero.common.status.yes').d('是')}
                cancelText={intl.get('hzero.common.status.no').d('否')}
                onConfirm={() => this.onDelete(index)}
              >
                <Tooltip
                  placement="topRight"
                  title={intl.get('hzero.common.button.delete').d('删除')}
                >
                  <div className="delete">
                    <Icon type="delete" />
                  </div>
                </Tooltip>
              </Popconfirm>
            </div>
          );
        })}
        <Tooltip placement="right" title={intl.get('hzero.common.button.new').d('新建')}>
          <div className={styles['plus-container']} onClick={this.addMap}>
            <Icon type="plus-circle-o" />
          </div>
        </Tooltip>
      </Modal>
    );
  }
}
