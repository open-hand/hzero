/* eslint-disable react/state-in-constructor */
/* eslint-disable react/jsx-props-no-spreading */
import React, { Component } from 'react';
import {
  Button,
  Form,
  Input,
  Row,
  Col,
  Table,
  Icon,
  Spin,
  Select,
  Tooltip,
  Menu,
  Dropdown,
  Switch,
  Popconfirm,
} from 'hzero-ui';
import { isEmpty } from 'lodash';
import { Bind } from 'lodash-decorators';
import { connect } from 'dva';

import { queryMapIdpValue } from 'services/api';
import intl from 'utils/intl';
import { yesOrNoRender } from 'utils/renderer';
import notification from 'utils/notification';
import {
  colOptions,
  getFieldNameAlias,
  getFieldConfigAlias,
  getAddFieldAlias,
  getDefaultActiveAlias,
} from '@/utils/constConfig.js';
import Modal from './Modal';
import styles from '../style/index.less';

const rowKey = 'id';
const FormItem = Form.Item;
const { Option } = Select;
const formsLayouts = {
  labelCol: { span: 6 },
  wrapperCol: { span: 18 },
};
const formsLayoutsLong = {
  labelCol: { span: 3 },
  wrapperCol: { span: 21 },
};

function getIntlMapping(key) {
  switch (key) {
    case 'R':
      return intl.get('hpfm.individuationUnit.view.message.rightFixed').d('右固定');
    case 'L':
      return intl.get('hpfm.individuationUnit.view.message.leftFixed').d('左固定');
    case 'FORM':
      return intl.get('hpfm.individuationUnit.view.message.form').d('表单');
    case 'GRID':
      return intl.get('hpfm.individuationUnit.view.message.grid').d('表格');
    case 'QUERYFORM':
      return intl.get('hpfm.individuationUnit.view.message.filter').d('表单-查询');
    case 'FILTER':
      return intl.get('hpfm.individuationUnit.view.message.filter').d('查询');
    case 'TABPANE':
      return intl.get('hpfm.individuationUnit.view.message.tabPane').d('标签');
    case 'WIDGET':
      return intl.get('hpfm.individuationUnit.view.message.widget').d('编辑组件');
    case 'TEXT':
      return intl.get('hpfm.individuationUnit.view.message.text').d('文本显示');
    default:
  }
}
const UEDDisplayFormItem = (props) => {
  const { label, value } = props;
  return (
    <FormItem label={label} {...formsLayouts}>
      {value}
    </FormItem>
  );
};

@Form.create({ fieldNameProp: null })
@connect(({ loading = {} }) => ({
  saveLoading: loading.effects['individuationUnit/modifyUnit'],
  fetchLoading: loading.effects['individuationUnit/fetchUnitDetail'],
  deleteFieldLoading: loading.effects['individuationUnit/deleteField'],
}))
export default class IndividuationUnitDetail extends Component {
  state = {
    unitInfo: {},
    fields: [],
    modelVisible: false,
    modalData: {},
    relationModals: [],
    gridFixedOptions: [],
    renderOptions: [],
    groupUnits: [],
    widgetType: [],
    widgetTypeObj: {},
    unitList: [],
    fieldList: {},
  };

  componentDidMount() {
    const { unitId, detailGroupId, dispatch } = this.props;
    this.fetchUnitDetail({ unitId });
    this.fetchLovData();
    dispatch({
      type: 'individuationUnit/queryGroupUnits',
      params: { unitGroupId: detailGroupId },
    }).then((res) => {
      if (res) {
        this.setState({ groupUnits: res.length > 0 ? res : [] });
      }
    });
    this.queryRelatedUnits(unitId);
  }

  @Bind()
  queryRelatedUnits(id) {
    const { dispatch } = this.props;
    dispatch({
      type: 'individuationUnit/queryRelatedUnits',
      payload: { unitId: id },
    }).then((res) => {
      if (!isEmpty(res)) {
        const unitList = res || [];
        const fieldList = {};
        unitList.forEach((i) => {
          fieldList[i.unitId] = i.unitFields || [];
        });
        this.setState({ unitList, fieldList });
      } else {
        this.setState({ unitList: [], fieldList: {} });
      }
    });
  }

  @Bind()
  fetchUnitDetail(params = {}) {
    const { unitId, dispatch } = this.props;
    dispatch({
      type: 'individuationUnit/fetchUnitDetail',
      params,
    }).then((res) => {
      if (res) {
        const { unit = {}, fields = [] } = res || {};
        const { modelId } = unit;
        this.setState({
          fields,
          unitInfo: unit,
        });
        this.fetchRelationModal({ modelId, unitId });
      }
    });
  }

  @Bind()
  fetchRelationModal(params) {
    this.props
      .dispatch({
        type: 'individuationUnit/queryRelationModels',
        params,
      })
      .then((res) => {
        if (res) {
          this.setState({ relationModals: res || [] });
        }
      });
  }

  @Bind
  fetchLovData() {
    queryMapIdpValue({
      gridFixedOptions: 'HPFM.CUST.GIRD.FIXED',
      renderOptions: 'HPFM.CUST.RENDER_OPTIONS',
      condOptions: 'HPFM.CUST.UNIT_COND_OPTIONS',
      widgetType: 'HPFM.CUST.FIELD_COMPONENT',
    }).then((res) => {
      if (res) {
        const widgetTypeObj = {};
        (res.widgetType || []).forEach((i) => {
          widgetTypeObj[i.value] = i.meaning;
        });
        this.setState({
          gridFixedOptions: res.gridFixedOptions || [],
          renderOptions: res.renderOptions || [],
          condOptions: res.condOptions || [],
          widgetType: res.widgetType || [],
          widgetTypeObj,
        });
      }
    });
  }

  @Bind()
  save() {
    const { form, dispatch, unitId } = this.props;
    const { unitInfo = {} } = this.state;

    form.validateFields((err, values) => {
      if (!err) {
        const params = {
          ...values,
          sqlIds: values.sqlIds.join(','),
        };
        dispatch({
          type: 'individuationUnit/modifyUnit',
          params: {
            ...unitInfo,
            ...params,
          },
        }).then((res) => {
          if (res) {
            notification.success();
            this.queryRelatedUnits(unitId);
            this.setState({
              unitInfo: {
                ...unitInfo,
                ...params,
              },
            });
          }
        });
      }
    });
  }

  @Bind()
  handleEdit(fieldInfo = {}) {
    this.setState({ modelVisible: true, modalData: fieldInfo });
  }

  @Bind()
  handleDelete(fieldId = '') {
    this.props
      .dispatch({
        type: 'individuationUnit/deleteField',
        params: {
          unitFieldId: fieldId,
        },
      })
      .then((res) => {
        if (res) {
          notification.success();
          const { unitId } = this.props;
          this.fetchUnitDetail({ unitId });
        }
      });
  }

  @Bind()
  renderFieldName(fieldName, record) {
    const { fieldCodeAlias } = record;
    const menu = (
      <Menu>
        <Menu.Item key="edit" onClick={() => this.handleEdit(record)}>
          <Icon type="edit" />
          {intl.get('hzero.common.button.edit').d('编辑')}
        </Menu.Item>
        <Menu.Item key="delete">
          <Popconfirm
            title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
            onConfirm={() => this.handleDelete(record[rowKey])}
          >
            <Icon type="delete" style={{ marginRight: 8 }} />
            {intl.get('hzero.common.button.delete').d('刪除')}
          </Popconfirm>
        </Menu.Item>
      </Menu>
    );

    return (
      <div>
        <div className={styles['unit-operator']}>
          <Dropdown overlay={menu} trigger={['click']}>
            <a>.&nbsp;.&nbsp;.</a>
          </Dropdown>
        </div>
        <div style={{ fontWeight: 600, color: '#666' }}>{fieldName}</div>
        <div style={{ color: '#a5a5a5' }}>{fieldCodeAlias}</div>
      </div>
    );
  }

  @Bind()
  getColumns() {
    const {
      unitInfo: { unitType },
      widgetTypeObj,
    } = this.state;
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    const pureVirtual = unitType === 'TABPANE' || unitType === 'COLLAPSE';
    const commonColumns = [
      {
        title: getFieldNameAlias(unitType),
        dataIndex: 'fieldName',
        render: this.renderFieldName,
      },
      pureVirtual && {
        title: getDefaultActiveAlias(unitType),
        dataIndex: 'defaultActive',
        width: 100,
        render: yesOrNoRender,
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.fieldType').d('字段类型'),
        dataIndex: 'field.fieldCategoryMeaning',
        width: 90,
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.model').d('所属模型'),
        dataIndex: 'field.modelName',
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.bindField').d('字段绑定'),
        dataIndex: 'bindField',
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.widgetType').d('组件类型'),
        dataIndex: 'field.modelFieldWidget.fieldWidget',
        width: 100,
        render: (text) => widgetTypeObj[text] || text,
      },
      !pureVirtual && {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.renderType').d('渲染方式'),
        dataIndex: 'renderOptions',
        width: 100,
        render: (text) => getIntlMapping(text) || text,
      },
    ].filter(Boolean);
    if (isFormType) {
      return commonColumns.concat(this.getFormColumns());
    } else if (unitType === 'GRID') {
      return commonColumns.concat(this.getTableColumns());
    } else if (unitType === 'FILTER' || unitType === 'TABPANE') {
      return commonColumns.concat(this.getFilterFormColumns());
    }
    return commonColumns;
  }

  @Bind()
  getFormColumns() {
    return [
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.row').d('行'),
        dataIndex: 'formRow',
        width: 60,
      },
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.col').d('列'),
        dataIndex: 'formCol',
        width: 60,
      },
    ];
  }

  @Bind()
  getTableColumns() {
    return [
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.position').d('位置'),
        dataIndex: 'gridSeq',
        width: 60,
      },
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.fixed').d('冻结'),
        dataIndex: 'gridFixed',
        width: 80,
        render: (text) => getIntlMapping(text) || text,
      },
    ];
  }

  @Bind()
  getFilterFormColumns() {
    return [
      {
        title: intl.get('hpfm.individuationUnit.model.individuationUnit.position').d('位置'),
        dataIndex: 'gridSeq',
        width: 60,
      },
    ];
  }

  @Bind()
  addNewField() {
    this.setState({ modelVisible: true, modalData: {} });
  }

  @Bind()
  closeModal() {
    this.setState({ modelVisible: false, modalData: {} });
  }

  render() {
    const {
      unitInfo = {},
      fields = [],
      relationModals = [],
      modelVisible,
      modalData,
      gridFixedOptions = [],
      renderOptions = [],
      condOptions = [],
      widgetType = [],
      groupUnits,
      unitList,
      fieldList,
    } = this.state;
    const {
      fetchLoading,
      saveLoading,
      deleteFieldLoading,
      form: { getFieldDecorator = () => {}, getFieldValue = () => {} },
      backToIndex = () => {},
      unitId,
    } = this.props;
    const {
      unitCode,
      unitName,
      unitType,
      menuName,
      modelName,
      sqlIds = '',
      // readOnly,
      enableFlag,
      formMaxCol,
      labelCol,
      wrapperCol,
      conRelatedUnits,
      unitGroupName,
    } = unitInfo;
    const isFormType = unitType === 'FORM' || unitType === 'QUERYFORM';
    return (
      <>
        <div className={styles.header}>
          <Tooltip title={intl.get('hzero.common.status.back').d('返回')} placement="bottom">
            <Icon type="arrow-left" className={styles['back-icon']} onClick={backToIndex} />
          </Tooltip>
          <span className={styles['header-title']}>
            {intl.get('hpfm.individuationUnit.view.message.title.unitDetail').d('个性化单元详情')}
          </span>
        </div>
        <div className={`${styles['unit-right-box']} ${styles.bordered}`}>
          <Spin spinning={fetchLoading || saveLoading || false}>
            <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px 0px' }}>
              <div className={styles['detail-container-title']}>
                {intl.get('hpfm.individuationUnit.view.message.title.unitConfig').d('单元配置')}
              </div>
              <Button type="primary" icon="save" loading={saveLoading} onClick={this.save}>
                {intl.get('hzero.common.button.save').d('保存')}
              </Button>
            </div>
            <Form layout="inline" className={styles['unit-detail-form']}>
              <Row gutter={48}>
                <Col span={12}>
                  <UEDDisplayFormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.unitCode')
                      .d('单元编码')}
                    value={unitCode}
                  />
                </Col>
                <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.unitName')
                      .d('单元名称')}
                    {...formsLayouts}
                  >
                    {getFieldDecorator('unitName', {
                      initialValue: unitName,
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
                          max: 30,
                          message: intl.get('hzero.common.validation.max', {
                            max: 30,
                          }),
                        },
                      ],
                    })(<Input />)}
                  </FormItem>
                </Col>
              </Row>
              <Row gutter={48}>
                <Col span={12}>
                  <UEDDisplayFormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.unitType')
                      .d('单元类型')}
                    value={getIntlMapping(unitType)}
                  />
                </Col>
                <Col span={12}>
                  <UEDDisplayFormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.menuName')
                      .d('所属功能')}
                    value={menuName}
                  />
                </Col>
                {unitType !== 'TABPANE' && (
                  <Col span={12}>
                    <UEDDisplayFormItem
                      label={intl
                        .get('hpfm.individuationUnit.model.individuationUnit.modelName')
                        .d('关联模型')}
                      value={modelName}
                    />
                  </Col>
                )}
                <Col span={12}>
                  <UEDDisplayFormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.relateGroup')
                      .d('所属单元组')}
                    value={unitGroupName}
                  />
                </Col>
              </Row>
              {isFormType && (
                <Row gutter={48}>
                  <Col span={12}>
                    <FormItem
                      label={intl
                        .get('hpfm.individuationUnit.model.individuationUnit.formMaxCol')
                        .d('表单列数')}
                      {...formsLayouts}
                    >
                      {getFieldDecorator('formMaxCol', {
                        initialValue: formMaxCol,
                      })(
                        <Select allowClear showSearch style={{ width: '120px' }}>
                          {colOptions.map((i) => (
                            <Option value={i}>{i}</Option>
                          ))}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                  <Col span={12}>
                    <FormItem
                      label={intl
                        .get('hpfm.individuationUnit.model.individuationUnit.labelWrapperCol')
                        .d('标签组件比例')}
                      {...formsLayouts}
                    >
                      {getFieldDecorator('labelCol', {
                        initialValue: labelCol,
                      })(
                        <Select
                          allowClear
                          showSearch
                          style={{ width: '120px', marginRight: '8px' }}
                          placeholder={intl
                            .get('hpfm.individuationUnit.model.individuationUnit.label')
                            .d('标签')}
                        >
                          {colOptions.map((i) => (
                            <Option value={i}>{i}</Option>
                          ))}
                        </Select>
                      )}
                      {getFieldDecorator('wrapperCol', {
                        initialValue: wrapperCol,
                      })(
                        <Select
                          allowClear
                          showSearch
                          style={{ width: '120px' }}
                          placeholder={intl
                            .get('hpfm.individuationUnit.model.individuationUnit.wrapper')
                            .d('组件')}
                        >
                          {colOptions.map((i) => (
                            <Option value={i}>{i}</Option>
                          ))}
                        </Select>
                      )}
                    </FormItem>
                  </Col>
                </Row>
              )}
              <Row gutter={48}>
                {/* <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.readOnly')
                      .d('只读')}
                    {...formsLayouts}
                  >
                    {getFieldDecorator('readOnly', {
                      initialValue: readOnly || 0,
                    })(<Switch checkedValue={1} unCheckedValue={0} />)}
                  </FormItem>
                </Col> */}
                <Col span={12}>
                  <FormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.enableFlag')
                      .d('启用')}
                    {...formsLayouts}
                  >
                    {getFieldDecorator('enableFlag', {
                      initialValue: enableFlag === undefined ? 1 : enableFlag,
                    })(<Switch checkedValue={1} unCheckedValue={0} />)}
                  </FormItem>
                </Col>
              </Row>
              <Row style={{ marginRight: '64px' }}>
                <Col span={24} style={{ display: unitType !== 'TABPANE' ? 'block' : 'none' }}>
                  <FormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.sqlIds')
                      .d('SQL IDs')}
                    {...formsLayoutsLong}
                  >
                    {getFieldDecorator('sqlIds', {
                      initialValue: sqlIds !== '' ? sqlIds.split(',') : [],
                    })(
                      <Select
                        mode="tags"
                        dropdownClassName={styles['sqlIds-select-options']}
                        style={{ width: '75%' }}
                      />
                    )}
                  </FormItem>
                </Col>
                <Col span={24}>
                  <FormItem
                    label={intl
                      .get('hpfm.individuationUnit.model.individuationUnit.conRelatedUnit')
                      .d('条件关联单元')}
                    {...formsLayoutsLong}
                  >
                    {getFieldDecorator('conRelatedUnits', {
                      initialValue: conRelatedUnits === undefined ? [] : conRelatedUnits,
                    })(
                      <Select mode="multiple" style={{ width: '75%' }} optionLabelProp="title">
                        {groupUnits
                          .filter((i) => i.id !== unitId)
                          .map((i) => (
                            <Option value={i.unitCode} title={i.unitName}>
                              <div style={{ fontWeight: 500, color: '#333' }}>{i.unitName}</div>
                              <div style={{ color: '#666' }}>{i.unitCode}</div>
                            </Option>
                          ))}
                      </Select>
                    )}
                  </FormItem>
                </Col>
              </Row>
            </Form>
          </Spin>
          <div>
            <div className={styles['detail-container']}>
              <div style={{ display: 'flex', justifyContent: 'space-between', margin: '10px 0px' }}>
                <div className={styles['detail-container-title']}>
                  {getFieldConfigAlias(unitType)}
                </div>
                <Button type="primary" icon="plus" onClick={this.addNewField}>
                  {getAddFieldAlias(unitType)}
                </Button>
              </div>
            </div>
            <Table
              bordered
              loading={deleteFieldLoading || fetchLoading}
              rowKey={rowKey}
              columns={this.getColumns()}
              dataSource={fields}
              pagination={false}
            />
          </div>
        </div>
        <Modal
          visible={modelVisible}
          data={modalData}
          fetchUnitDetail={this.fetchUnitDetail}
          gridFixedOptions={gridFixedOptions}
          renderOptions={renderOptions}
          condOptions={condOptions}
          widgetType={widgetType}
          unitInfo={unitInfo}
          unitList={unitList}
          fieldList={fieldList}
          readOnly={getFieldValue('readOnly') === 1}
          relationModals={relationModals}
          handleClose={this.closeModal}
        />
      </>
    );
  }
}
