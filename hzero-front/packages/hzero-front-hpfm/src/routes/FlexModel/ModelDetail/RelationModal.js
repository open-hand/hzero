/* eslint-disable eqeqeq */
import React, { Component } from 'react';
import { Drawer, Card, Col, Row, Form, Select, Button, Icon, Input } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import isEmpty from 'lodash/isEmpty';
import { connect } from 'dva';

import notification from 'utils/notification';
import intl from 'utils/intl';
import Lov from 'components/Lov';
import { getSingleTenantValueCode } from '@/utils/constConfig';
import styles from '../style/index.less';

const FormItem = Form.Item;
const { Option } = Select;

@Form.create({ fieldNameProp: null })
@connect(({ loading = {} }) => ({
  createRelationLoading: loading.effects['flexModel/createRelation'],
}))
export default class RelationMoal extends Component {
  state = {
    models: [], // 所有模型
    masterModels: [], // 主模型名
    slaveModelFields: [], // 从模型字段
    currentMasterModelFieldId: '', // 当前选择的主模型字段id
    currentSlaveModelFieldId: '', // 当前选择的从模型字段id
  };

  @Bind()
  fetchModels() {
    this.props
      .dispatch({
        type: 'flexModel/queryList',
        params: {
          noPaging: true,
          sort: {
            order: 'asc',
            field: ['model_code'],
          },
        },
      })
      .then((res) => {
        if (res) {
          const { dataSource } = res || {};
          this.setState({
            models: dataSource || [],
          });
        }
      });
  }

  @Bind()
  handleSearchField(id) {
    const { modelInfo = {} } = this.props;
    const { modelId } = modelInfo;
    if (id && id !== modelId) {
      this.props
        .dispatch({
          type: 'flexModel/queryFieldsList',
          params: {
            modelId: id,
          },
        })
        .then((res) => {
          if (res) {
            const { dataSource = [] } = res || {};
            this.setState({ slaveModelFields: dataSource });
          }
        });
    } else {
      this.props.form.setFieldsValue({ slaveModelFieldCode: '' });
      this.setState({ slaveModelFields: [] });
    }
  }

  @Bind()
  handleSearchEntity(value, entity) {
    if (value) {
      const { models } = this.state;
      const filterModels = models.filter((item) =>
        item.modelName.toLowerCase().includes(value.toLowerCase())
      );
      if (entity === 'master') {
        this.setState({ masterModels: filterModels });
      }
    }
  }

  @Bind()
  handleChangeEntity(value, entity) {
    if (entity === 'master') {
      this.props.form.setFieldsValue({ masterModelFieldCode: '' });
    } else {
      this.props.form.setFieldsValue({ slaveModelFieldCode: '' });
    }
    if (value) {
      const { models } = this.state;
      const currentModel = models.find((item) => item.modelName === value) || {};
      this.props
        .dispatch({
          type: 'flexModel/queryFieldsList',
          params: {
            modelId: currentModel.modelId,
          },
        })
        .then((res) => {
          if (res) {
            const { dataSource } = res || {};
            if (entity === 'slave') {
              this.setState({ slaveModelFields: dataSource });
            }
          }
        });
    }
  }

  @Bind()
  checkEntityValid(rule, value, callback) {
    const { modelInfo = {} } = this.props;
    const masterModelId = modelInfo.modelId;
    if (masterModelId !== undefined && value !== undefined && masterModelId == value) {
      callback(
        intl.get('hpfm.flexModelDetail.view.message.modelCannotSame').d('主模型和从模型不能相同')
      );
    } else {
      callback();
    }
  }

  @Bind()
  clearState() {
    this.setState({
      masterModels: [], // 主模型名
      slaveModelFields: [], // 从模型字段
      currentMasterModelFieldId: {}, // 当前选择的主模型字段id
      currentSlaveModelFieldId: {}, // 当前选择的从模型字段id
    });
  }

  @Bind()
  closeModal() {
    this.clearState();
    this.props.handleClose();
  }

  @Bind()
  createModal() {
    const { dispatch, form, relation = '', modelInfo = {}, fetchRelationList } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        const { slaveModelId } = values;
        const { modelId } = modelInfo;
        const { currentMasterModelFieldId, currentSlaveModelFieldId } = this.state;
        const isMaster = relation !== 'MANY_TO_ONE'; // 多对一 是 从，其他 是 主
        // 如果当前是从模型，当前是salve，将master和slave字段值交换
        const params = {
          masterModelId: isMaster ? modelId : slaveModelId,
          masterFieldId: isMaster ? currentMasterModelFieldId : currentSlaveModelFieldId,
          slaveModelId: isMaster ? slaveModelId : modelId,
          slaveFieldId: isMaster ? currentSlaveModelFieldId : currentMasterModelFieldId,
          relation: relation === 'MANY_TO_ONE' ? 'ONE_TO_MANY' : relation,
        };
        dispatch({
          type: 'flexModel/createRelation',
          params,
        }).then((res) => {
          if (res) {
            this.closeModal();
            notification.success();
            fetchRelationList({ modelId });
          }
        });
      }
    });
  }

  render() {
    const { masterModels, slaveModelFields } = this.state;
    const {
      visible,
      relation = '',
      data = {},
      modelInfo = {},
      fieldList = [],
      form,
      createRelationLoading,
      modelRelationOptions = [],
    } = this.props;
    const { getFieldDecorator } = form;
    const { masterModelName, masterFieldName, slaveModelName, slaveFieldName } = data;
    const { modelName } = modelInfo;
    const isCreate = isEmpty(data);
    const nowRelation = isCreate ? relation : data.relation;
    const nowRelationMean =
      (modelRelationOptions.find((item) => item.value === nowRelation) || {}).meaning || '';
    //  const isMaster = nowRelation !== 'MANY_TO_ONE'; //多对一 是 从，其他 是 主
    const title = isCreate
      ? intl
          .get('hpfm.flexModelDetail.view.message.title.createRelation')
          .d(`新建关联模型(${nowRelationMean})`)
      : intl
          .get('hpfm.flexModelDetail.view.message.title.viewModelRelation')
          .d(`查看关联模型(${nowRelationMean})`);
    return (
      <Drawer
        title={title}
        closable
        onClose={this.closeModal}
        visible={visible}
        destroyOnClose
        width={650}
      >
        <Row className={styles['relation-modal']} style={{ marginBottom: 50 }}>
          <Col span={11}>
            <Card title={intl.get('hpfm.flexModelDetail.view.message.title.current').d('当前')}>
              <Form layout="vertical" className={styles['relation-modal-form']}>
                <FormItem
                  label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.model').d('模型')}
                >
                  {getFieldDecorator('masterModelName', {
                    initialValue: isCreate ? modelName : masterModelName,
                    rules: [
                      {
                        validator: this.checkEntityValid,
                      },
                    ],
                  })(
                    <Select
                      showSearch
                      disabled
                      allowClear
                      defaultActiveFirstOption={false}
                      showArrow={false}
                      filterOption={false}
                      onSearch={(value) => this.handleSearchEntity(value, 'master')}
                      onChange={(value) => this.handleChangeEntity(value, 'master')}
                      notFoundContent={null}
                    >
                      {masterModels.map((item) => (
                        <Option key={item.modelName}>{item.modelName}</Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.field').d('字段')}
                >
                  {getFieldDecorator('masterModelFieldCode', {
                    initialValue: masterFieldName,
                    rules: [
                      {
                        required: isCreate,
                        message: intl
                          .get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.flexModelDetail.model.flexModelDetail.currentModelField')
                              .d('当前模型关联字段'),
                          })
                          .d(
                            `${intl
                              .get('hpfm.flexModelDetail.model.flexModelDetail.currentModelField')
                              .d('当前模型关联字段')}不能为空`
                          ),
                      },
                    ],
                  })(
                    <Select
                      className={styles['relation-modal-field-select']}
                      disabled={!isCreate}
                      showArrow={isCreate}
                    >
                      {fieldList.map((item) => (
                        <Option
                          value={item.fieldCode}
                          onClick={() => {
                            this.setState({ currentMasterModelFieldId: item.fieldId || '' });
                          }}
                        >
                          <div style={{ fontSize: '14px', color: isCreate ? '#000' : '#aaa' }}>
                            {' '}
                            {item.fieldName || item.fieldCode}{' '}
                          </div>
                          <div style={{ color: '#aaa' }}> {item.fieldCode} </div>
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Form>
            </Card>
          </Col>
          <Col span={2} className={styles['relation-modal-center']}>
            <Row>
              <Col
                span={1}
                className={
                  nowRelation === 'MANY_TO_ONE' ? styles['relation-modal-font-size-16'] : ''
                }
              >
                {nowRelation === 'MANY_TO_ONE' ? '*' : 1}
              </Col>
              <Col span={18}>
                <Icon type="minus" />
              </Col>
              <Col
                span={1}
                className={
                  nowRelation === 'ONE_TO_MANY' ? styles['relation-modal-font-size-16'] : ''
                }
              >
                {nowRelation === 'ONE_TO_MANY' ? '*' : 1}
              </Col>
            </Row>
          </Col>
          <Col span={11}>
            <Card title={intl.get('hpfm.flexModelDetail.view.message.title.relate').d('相关')}>
              <Form layout="vertical" className={styles['relation-modal-form']}>
                <FormItem
                  label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.model').d('模型')}
                >
                  {!isCreate ? (
                    <Input disabled defaultValue={slaveModelName} />
                  ) : (
                    getFieldDecorator('slaveModelId', {
                      rules: [
                        {
                          required: isCreate,
                          message: intl
                            .get('hzero.common.validation.notNull', {
                              name: intl
                                .get('hpfm.flexModelDetail.model.flexModelDetail.relatedModel')
                                .d('关联模型'),
                            })
                            .d(
                              `${intl
                                .get('hpfm.flexModelDetail.model.flexModelDetail.relatedModel')
                                .d('关联模型')}不能为空`
                            ),
                        },
                        {
                          validator: this.checkEntityValid,
                        },
                      ],
                    })(
                      <Lov
                        code={getSingleTenantValueCode('HPFM.CUST.MODEL_VIEW')}
                        onChange={this.handleSearchField}
                      />
                    )
                  )}
                </FormItem>
                <FormItem
                  label={intl.get('hpfm.flexModelDetail.model.flexModelDetail.field').d('字段')}
                >
                  {getFieldDecorator('slaveModelFieldCode', {
                    initialValue: slaveFieldName,
                    rules: [
                      {
                        required: isCreate,
                        message: intl
                          .get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hpfm.flexModelDetail.model.flexModelDetail.relateField')
                              .d('关联模型字段关联字段'),
                          })
                          .d(
                            `${intl
                              .get('hpfm.flexModelDetail.model.flexModelDetail.relateField')
                              .d('关联模型字段关联字段')}不能为空`
                          ),
                      },
                    ],
                  })(
                    <Select
                      className={styles['relation-modal-field-select']}
                      disabled={!isCreate}
                      showArrow={isCreate}
                    >
                      {slaveModelFields.map((item) => (
                        <Option
                          value={item.fieldCode}
                          onClick={() => {
                            this.setState({ currentSlaveModelFieldId: item.fieldId || '' });
                          }}
                        >
                          <div style={{ fontSize: '14px', color: '#000' }}>
                            {' '}
                            {item.fieldName || item.fieldCode}{' '}
                          </div>
                          <div style={{ color: '#aaa' }}> {item.fieldCode} </div>
                        </Option>
                      ))}
                    </Select>
                  )}
                </FormItem>
              </Form>
            </Card>
          </Col>
        </Row>
        <div className={styles['model-bottom-button']}>
          <Button
            onClick={this.closeModal}
            style={{ marginRight: 8 }}
            disabled={createRelationLoading}
          >
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
          <Button
            type="primary"
            loading={createRelationLoading}
            htmlType="submit"
            onClick={isCreate ? this.createModal : this.closeModal}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </div>
      </Drawer>
    );
  }
}
