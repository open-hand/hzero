/**
 * reportDefinition - 报表平台/报表定义
 * @date: 2018-11-19
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Card, Col, Form, Input, InputNumber, Modal, Row, Select, Tabs } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { filter, isEmpty, isFinite, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { filterNullValueObject, getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { deleteBeforeMenuTabRemove, onBeforeMenuTabRemove } from 'utils/menuTab';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  DETAIL_EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import MetaColumnsTable from './MetaColumnsTable';
import MetaColumnsDrawer from './MetaColumnsDrawer';
import TemplateTable from './TemplateTable';
import TemplateDrawer from './TemplateDrawer';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const { Option } = Select;

/**
 * 报表定义-行数据管理组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} reportDefinition - 数据源
 * @reactProps {!Object} fetchApproveHeaderLoading - 数据加载是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ reportDefinition, loading }) => ({
  reportDefinition,
  tenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
  fetchDefinitionDetailLoading: loading.effects['reportDefinition/fetchReportDefinitionDetail'],
  getMetaColumnsLoading: loading.effects['reportDefinition/getMetaMetaColumns'],
  getTemplateListLoading: loading.effects['reportDefinition/fetchInitTemplate'],
  fetchTemplateDetailLoading: loading.effects['reportDefinition/fetchTemplateDetail'],
  createTemplateLoading: loading.effects['reportDefinition/createTemplate'],
  saving:
    loading.effects['reportDefinition/createReportDefinition'] ||
    loading.effects['reportDefinition/updateReportDefinition'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hrpt.reportDefinition', 'entity.tenant', 'entity.lang', 'entity.template', 'hrpt.common'],
})
export default class Detail extends Component {
  /**
   * state初始化
   */
  state = {
    metaColumnsItem: {}, // 列信息表格中的一条数据
    metaColumnsDrawerVisible: false, // 列信息模态框
    templateDrawerVisible: false, // 模板模态框
    metaColumnsSelectedRowKeys: [], // 列信息选中行
    templateSelectedRowKeys: [], // 模板选中行
    templateListSelectedRowKeys: [], // 模板列表选中行
    isCreateMetaColumn: false, // 是否新建列信息
    isChangeContent: false, // 是否改变内容
    isColumnFlag: true, // 是否显示报表列
    exportTypeCode: {}, // 导出类型
    exportTypeList: [], // 导出类型列表
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
    onBeforeMenuTabRemove(
      '/hrpt/report-definition',
      () =>
        new Promise((resolve, reject) => {
          const { isChangeContent } = this.state;
          if (isChangeContent) {
            Modal.confirm({
              title: intl
                .get('hzero.common.message.confirm.giveUpTip')
                .d('你有修改未保存，是否确认离开？'),
              onOk: () => {
                resolve();
              },
              onCancel: () => {
                reject();
              },
            });
          } else {
            resolve();
          }
        })
    );
  }

  componentWillUnmount() {
    deleteBeforeMenuTabRemove('/hrpt/report-definition');
    this.props.dispatch({
      type: 'reportDefinition/updateState',
      payload: {
        header: {},
        template: [],
      },
    });
  }

  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'reportDefinition/fetchReportDefinitionDetail',
        payload: {
          reportId: id,
        },
      }).then((res) => {
        // 如果是模板报表
        if (res) {
          if (res.reportTypeCode === 'D') {
            dispatch({
              type: 'reportDefinition/fetchTemplateDetail',
              payload: { reportId: match.params.id },
            });
          }
          this.setState({ isColumnFlag: res.reportTypeCode !== 'U' });
          this.setExportTypeList(res.reportTypeCode, res.templateTypeCode);
        }
      });
    } else {
      dispatch({
        type: 'reportDefinition/updateState',
        payload: {
          header: {},
          template: [],
        },
      });
    }
    const lovCodes = {
      reportTypeCode: 'HRPT.REPORT_TYPE', // 报表类型
      layout: 'HRPT.LAYOUT_TYPE', // 布局位置
      type: 'HRPT.COLUMN_TYPE', // 布局类型
      sortType: 'HRPT.COLUMN_SORT_TYPE', // 排序类型
      templateTypeCode: 'HRPT.TEMPLATE_TYPE', // 模板类型
    };
    // 初始化 值集
    dispatch({
      type: 'reportDefinition/batchCode',
      payload: {
        lovCodes,
      },
    });
    dispatch({ type: 'reportDefinition/fetchExportType' }).then((res) => {
      if (res) {
        this.setState({ exportTypeCode: res });
      }
    });
    this.setState({ isChangeContent: false });
  }

  /**
   * 初始化列信息
   */
  @Bind()
  getMetaMetaColumns() {
    const {
      form,
      dispatch,
      match,
      reportDefinition: { header = {} },
    } = this.props;
    const { id } = match.params;
    form.validateFields(['datasetId', 'tenantId', 'reportTypeCode'], (err, values) => {
      if (isEmpty(err)) {
        dispatch({
          type: 'reportDefinition/getMetaMetaColumns',
          payload: { ...values, reportId: id, header },
        }).then((res) => {
          if (res) {
            this.handleUpdateState();
          }
        });
      }
    });
  }

  /**
   * 保存
   */
  @Bind()
  handleSave() {
    const {
      dispatch,
      form,
      match,
      reportDefinition: { header = {} },
    } = this.props;
    const { metaColumns = [] } = header;
    form.validateFields((err, values) => {
      const { layout = undefined, statColumnLayout = undefined, ...otherValues } = values;
      const options = { layout, statColumnLayout };
      if (!err) {
        if (isUndefined(match.params.id)) {
          dispatch({
            type: 'reportDefinition/createReportDefinition', // 新增逻辑
            payload: {
              metaColumns: JSON.stringify(metaColumns),
              options: JSON.stringify(options),
              ...otherValues,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hrpt/report-definition/detail/${res.reportId}`,
                })
              );
            }
          });
        } else {
          dispatch({
            type: 'reportDefinition/updateReportDefinition', // 更新逻辑
            payload: {
              ...header,
              metaColumns: JSON.stringify(metaColumns),
              options: JSON.stringify(options),
              ...otherValues,
            },
          }).then((res) => {
            if (res) {
              notification.success();
              this.handleSearch();
            }
          });
        }
      }
    });
  }

  /**
   * 列信息-新建打开滑窗
   */
  @Bind()
  handleAddMetaColumns() {
    this.getInitMetaColumn();
  }

  /**
   * 列信息-新建打开模态框初始化滑窗数据
   */
  @Bind()
  getInitMetaColumn() {
    const {
      dispatch,
      reportDefinition: { header },
    } = this.props;
    const { metaColumns = [] } = header;
    const ordinalList = metaColumns && metaColumns.map((item) => item.ordinal);
    const ordinalMax = Math.max(...ordinalList);
    dispatch({
      type: 'reportDefinition/getInitMetaColumn',
    }).then((res) => {
      if (res) {
        const anotherRes = { ...res, ordinal: isFinite(ordinalMax) ? ordinalMax + 1 : 0 };
        this.setState({
          metaColumnsItem: { ...anotherRes },
          metaColumnsDrawerVisible: true,
          isCreateMetaColumn: true,
        });
      }
    });
  }

  /**
   * 列信息-关闭滑窗
   */
  @Bind()
  handleCancelMetaColumns() {
    this.setState({
      metaColumnsDrawerVisible: false,
      metaColumnsItem: {},
      isCreateMetaColumn: false,
    });
  }

  /**
   * 列信息-新增滑窗保存操作
   */
  @Bind()
  handleSaveMetaColumnsContent(values) {
    const {
      dispatch,
      reportDefinition: { header = {} },
    } = this.props;
    const { metaColumns = [], ...otherValues } = header;
    dispatch({
      type: 'reportDefinition/updateState',
      payload: {
        header: { metaColumns: [...metaColumns, values], ...otherValues },
      },
    });
    this.setState({ metaColumnsDrawerVisible: false, metaColumnsItem: {} });
    this.handleUpdateState();
  }

  /**
   * 列信息-编辑打开滑窗
   */
  @Bind()
  handleEditMetaColumnsContent(record) {
    this.setState({
      metaColumnsDrawerVisible: true,
      metaColumnsItem: { ...record },
      isCreateMetaColumn: false,
    });
  }

  /**
   * 列信息-编辑保存滑窗
   */
  @Bind()
  handleEditMetaColumnsOk(values) {
    const {
      dispatch,
      reportDefinition: { header = {} },
    } = this.props;
    const { metaColumns = [], ...otherValues } = header;
    const newList = metaColumns.map((item) => {
      if (item.ordinal === values.ordinal) {
        return values;
      }
      return item;
    });
    dispatch({
      type: 'reportDefinition/updateState',
      payload: { header: { metaColumns: newList, ...otherValues } },
    });
    this.setState({ metaColumnsDrawerVisible: false, metaColumnsItem: {} });
    this.handleUpdateState();
  }

  /**
   * 列信息-获取删除选中行
   */
  @Bind()
  handleMetaColumnsRowSelectChange(selectedRowKeys) {
    this.setState({ metaColumnsSelectedRowKeys: selectedRowKeys });
  }

  /**
   * 列信息-批量删除
   */
  @Bind()
  handleDeleteMetaColumns() {
    const {
      dispatch,
      reportDefinition: { header = {} },
    } = this.props;
    const { metaColumns = [], ...otherValues } = header;
    const { metaColumnsSelectedRowKeys } = this.state;
    const newParameters = filter(
      metaColumns,
      (item) => metaColumnsSelectedRowKeys.indexOf(item.ordinal) < 0
    );
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        for (let i = 0; i < newParameters.length; i++) {
          newParameters[i].ordinal = i + 1;
        }
        dispatch({
          type: 'reportDefinition/updateState',
          payload: {
            header: { metaColumns: [...newParameters], ...otherValues },
          },
        });
        this.setState({ metaColumnsSelectedRowKeys: [] });
        this.handleUpdateState();
      },
    });
  }

  /**
   * 列信息-序号唯一性校验
   */
  @Bind()
  checkUniqueMetaColumns(rule, value, callback) {
    const {
      reportDefinition: { header },
    } = this.props;
    const { metaColumns = [] } = header;
    const metaColumnsId = metaColumns.map((item) => item.ordinal);
    if (!isEmpty(metaColumnsId)) {
      if (metaColumnsId.some((item) => item === +value)) {
        callback(
          intl
            .get('hrpt.reportDefinition.view.reportDefinition.validateOrd')
            .d('序号已存在，请输入其他序号')
        );
      } else {
        callback();
      }
    } else {
      callback();
    }
  }

  /**
   * 模板-新建弹框打开
   */
  @Bind()
  handleInitTemplate() {
    this.setState({ templateDrawerVisible: true });
    this.fetchInitTemplate();
  }

  /**
   * 模板-初始化数据
   */
  @Bind()
  fetchInitTemplate(fields = {}) {
    const {
      dispatch,
      reportDefinition: { header = {} },
      form,
    } = this.props;
    const fieldValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    form.validateFields(['templateTypeCode'], (err, values) => {
      if (isEmpty(err)) {
        dispatch({
          type: 'reportDefinition/fetchInitTemplate',
          payload: {
            page: isEmpty(fields) ? {} : fields,
            tenantId: isTenantRoleLevel() ? header.tenantId : '',
            reportId: header.reportId,
            templateTypeCode: values.templateTypeCode,
            ...fieldValues,
          },
        });
      }
    });
  }

  /**
   * 模板-选择模板列表数据
   */
  @Bind()
  templateListRowSelectChange(selectedRowKeys) {
    this.setState({ templateListSelectedRowKeys: selectedRowKeys });
  }

  /**
   * 模板-弹窗取消操作
   */
  @Bind()
  handleCancelTemplate() {
    this.setState({
      templateDrawerVisible: false,
    });
  }

  /**
   * 模板-新增
   * @param {object} values - 保存数据
   */
  @Bind()
  handleAddTemplate() {
    const {
      dispatch,
      match,
      reportDefinition: { templateList = [], header = {} },
    } = this.props;
    const { templateListSelectedRowKeys } = this.state;
    const parameters = filter(
      templateList,
      (item) => templateListSelectedRowKeys.indexOf(item.templateId) >= 0
    );
    const newParams = parameters.map((item) => ({
      templateId: item.templateId,
      tenantId: item.tenantId,
      reportId: header.reportId,
    }));
    dispatch({
      type: 'reportDefinition/createTemplate',
      payload: { newParams },
    }).then((res) => {
      if (res) {
        this.setState({ templateDrawerVisible: false });
        dispatch({
          type: 'reportDefinition/fetchTemplateDetail',
          payload: { reportId: match.params.id },
        });
      }
    });
  }

  /**
   * 模板-单选删除
   */
  @Bind()
  handleDeleteTemplate() {
    const {
      dispatch,
      match,
      reportDefinition: { template = [] },
    } = this.props;
    const { templateSelectedRowKeys } = this.state;
    let reportTemplate = {};
    const parameters = filter(
      template,
      (item) => templateSelectedRowKeys.indexOf(item.reportTemplateId) >= 0
    );
    parameters.forEach((item) => {
      reportTemplate = { ...reportTemplate, ...item };
    });
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        dispatch({
          type: 'reportDefinition/deleteTemplate',
          payload: reportTemplate,
        }).then((res) => {
          if (res) {
            dispatch({
              type: 'reportDefinition/fetchTemplateDetail',
              payload: { reportId: match.params.id },
            });
            this.setState({ templateSelectedRowKeys: [] });
          }
        });
      },
    });
  }

  /**
   * 模板-获取删除选中行
   *
   * @param {*} selectedRowKeys
   * @memberof EditForm
   */
  @Bind()
  handleTemplateRowSelectChange(selectedRowKeys) {
    this.setState({ templateSelectedRowKeys: selectedRowKeys });
  }

  form;

  /**
   * 设置Form
   * @param {object} ref - TemplateDrawer组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 改变默认模板
   */
  @Bind()
  changeDefaultTemplate(record) {
    const { dispatch, match } = this.props;
    dispatch({
      type: 'reportDefinition/changeDefaultTemplate',
      payload: { newParams: { ...record, defaultFlag: 1 } },
    }).then((res) => {
      if (res) {
        dispatch({
          type: 'reportDefinition/fetchTemplateDetail',
          payload: { reportId: match.params.id },
        });
      }
    });
  }

  /**
   * 改变表单时
   */
  @Bind()
  handleUpdateState() {
    const { isChangeContent } = this.state;
    if (isChangeContent === false) {
      this.setState({ isChangeContent: true });
    }
  }

  /**
   * 改变报表类型
   */
  @Bind()
  changeReportTypeCode(value) {
    const { setFieldsValue, getFieldValue } = this.props.form;
    const templateTypeCode = getFieldValue('templateTypeCode');
    setFieldsValue({ datasetId: undefined });
    this.setExportTypeList(value, templateTypeCode);
    this.setState({
      isColumnFlag: value !== 'U',
    });
  }

  @Bind()
  setExportTypeList(reportTypeCode, templateTypeCode) {
    let list = [];
    const { exportTypeCode = {} } = this.state;
    if (reportTypeCode) {
      if (reportTypeCode === 'D' && templateTypeCode) {
        const data = exportTypeCode[reportTypeCode] || {};
        list = data[templateTypeCode] || [];
      } else if (reportTypeCode !== 'D') {
        list = exportTypeCode[reportTypeCode] || [];
      }
    }
    this.setState({ exportTypeList: list });
  }

  // 改变模板类型
  @Bind()
  changeTemplateTypeCode(value) {
    const { exportTypeCode = {} } = this.state;
    const { getFieldValue } = this.props.form;
    const reportTypeCode = getFieldValue('reportTypeCode');
    if (reportTypeCode === 'D') {
      const data = exportTypeCode[reportTypeCode] || {};
      const list = data[value] || [];
      this.setState({
        exportTypeList: value ? list : [],
      });
    }
    this.handleUpdateState();
  }

  /**
   * 改变租户
   */
  @Bind()
  changeTenantId() {
    const { setFieldsValue } = this.props.form;
    setFieldsValue({ datasetId: undefined });
    this.handleUpdateState();
  }

  renderHeaderForm() {
    const {
      form,
      form: { getFieldDecorator, getFieldValue },
      reportDefinition: {
        header = {},
        template = [],
        code: { reportTypeCode = [], layout = [], templateTypeCode = [] },
      },
      match,
      tenantId,
      tenantRoleLevel,
    } = this.props;
    const { options = {}, metaColumns = [] } = header;
    const reportTypeCodeList = tenantRoleLevel
      ? reportTypeCode.filter((item) => item.tag !== 'SITE') // 租户级值集不显示tag为"SITE"的值
      : reportTypeCode;
    const isTemplateTab = form.getFieldValue('reportTypeCode') === 'D';
    const { exportTypeList = [] } = this.state;
    return (
      <Form className={DETAIL_EDIT_FORM_CLASSNAME}>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          {!tenantRoleLevel && (
            <Col {...FORM_COL_3_LAYOUT}>
              <Form.Item label={intl.get('entity.tenant.tag').d('租户')} {...EDIT_FORM_ITEM_LAYOUT}>
                {getFieldDecorator('tenantId', {
                  initialValue: header.tenantId,
                  rules: [
                    {
                      required: true,
                      message: intl.get('hzero.common.validation.notNull', {
                        name: intl.get('entity.tenant.tag').d('租户'),
                      }),
                    },
                  ], // 校验规则
                })(
                  <Lov
                    code="HPFM.TENANT"
                    textValue={header.tenantName}
                    disabled={!isUndefined(header.tenantId)}
                    onChange={this.changeTenantId}
                  />
                )}
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hrpt.common.report.reportCode').d('报表代码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('reportCode', {
                initialValue: header.reportCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hrpt.common.report.reportCode').d('报表代码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ],
              })(
                <Input
                  trim
                  typeCase="upper"
                  inputChinese={false}
                  disabled={!isUndefined(header.reportCode)}
                  onChange={this.handleUpdateState}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hrpt.common.report.reportName').d('报表名称')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('reportName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('hrpt.common.report.reportName').d('报表名称'),
                    }),
                  },
                ],
                initialValue: header.reportName,
              })(
                <TLEditor
                  label={intl.get('hrpt.common.report.reportName').d('报表名称')}
                  field="reportName"
                  token={header ? header._token : null}
                  onChange={this.handleUpdateState}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.reportType')
                .d('报表类型')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('reportTypeCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.reportDefinition.model.reportDefinition.reportType')
                        .d('报表类型'),
                    }),
                  },
                ],
                initialValue: header.reportTypeCode,
              })(
                <Select
                  allowClear
                  disabled={metaColumns.length !== 0}
                  onChange={this.changeReportTypeCode}
                >
                  {reportTypeCodeList &&
                    reportTypeCodeList.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hrpt.reportDefinition.model.reportDefinition.datasetId').d('数据集')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('datasetId', {
                initialValue: header.datasetId,
                rules: [
                  {
                    required: getFieldValue('reportTypeCode') !== 'U', // U:UReport类型
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.reportDefinition.model.reportDefinition.datasetId')
                        .d('数据集'),
                    }),
                  },
                ],
              })(
                <Lov
                  code="HRPT.DATASET"
                  disabled={
                    !isUndefined(header.datasetId) ||
                    metaColumns.length !== 0 ||
                    getFieldValue('reportTypeCode') === 'U' ||
                    (isUndefined(getFieldValue('tenantId')) && !tenantRoleLevel) ||
                    isUndefined(getFieldValue('reportTypeCode'))
                  }
                  textValue={header.datasetName}
                  queryParams={
                    getFieldValue('reportTypeCode') === 'T' ||
                    getFieldValue('reportTypeCode') === 'ST'
                      ? {
                          sqlType: 'S',
                          enabledFlag: 1,
                          tenantId: tenantRoleLevel ? tenantId : getFieldValue('tenantId'),
                        }
                      : {
                          enabledFlag: 1,
                          tenantId: tenantRoleLevel ? tenantId : getFieldValue('tenantId'),
                        }
                  }
                  onChange={this.handleUpdateState}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.tempType')
                .d('模板类型')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('templateTypeCode', {
                rules: [
                  {
                    required: getFieldValue('reportTypeCode') === 'D',
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.reportDefinition.model.reportDefinition.tempType')
                        .d('模板类型'),
                    }),
                  },
                ],
                initialValue: header.templateTypeCode,
              })(
                <Select
                  allowClear
                  disabled={
                    !isEmpty(template) ||
                    (!isUndefined(match.params.id) &&
                      getFieldValue('reportTypeCode') !== 'D' &&
                      (!isTemplateTab || (isTemplateTab && template.length !== 0))) ||
                    getFieldValue('reportTypeCode') === 'ST' ||
                    getFieldValue('reportTypeCode') === 'C' ||
                    getFieldValue('reportTypeCode') === 'T' ||
                    getFieldValue('reportTypeCode') === 'U' // st: 简单报表，c:图形报表，t:复杂报表,u: ureport报表
                  }
                  onChange={this.changeTemplateTypeCode}
                >
                  {templateTypeCode &&
                    templateTypeCode.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.layout')
                .d('布局列位置')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('layout', {
                initialValue: options.layout,
              })(
                <Select
                  allowClear
                  disabled={
                    getFieldValue('reportTypeCode') === 'ST' ||
                    getFieldValue('reportTypeCode') === 'C' ||
                    getFieldValue('reportTypeCode') === 'D' ||
                    getFieldValue('reportTypeCode') === 'U'
                  }
                  onChange={this.handleUpdateState}
                >
                  {layout &&
                    layout.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.colLayout')
                .d('统计列位置')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('statColumnLayout', {
                initialValue: options.statColumnLayout,
              })(
                <Select
                  disabled={
                    getFieldValue('reportTypeCode') === 'ST' ||
                    getFieldValue('reportTypeCode') === 'C' ||
                    getFieldValue('reportTypeCode') === 'D' ||
                    getFieldValue('reportTypeCode') === 'U'
                  }
                  allowClear
                  onChange={this.handleUpdateState}
                >
                  {layout &&
                    layout.map((item) => (
                      <Option key={item.value} value={item.value}>
                        {item.meaning}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item label={intl.get('hzero.common.remark').d('备注')} {...EDIT_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('remark', {
                initialValue: header.remark,
              })(<Input onChange={this.handleUpdateState} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hrpt.reportDefinition.model.reportDefinition.orderSeq').d('排序号')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('orderSeq', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.reportDefinition.model.reportDefinition.orderSeq')
                        .d('排序号'),
                    }),
                  },
                ],
                initialValue: header.orderSeq,
              })(
                <InputNumber min={0} style={{ width: '50%' }} onChange={this.handleUpdateState} />
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.limitRows')
                .d('异步阈值')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('limitRows', {
                rules: [
                  {
                    required:
                      getFieldValue('reportTypeCode') === 'T' ||
                      getFieldValue('reportTypeCode') === 'ST' ||
                      getFieldValue('reportTypeCode') === 'TS1',
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl
                        .get('hrpt.reportDefinition.model.reportDefinition.limitRows')
                        .d('异步阈值'),
                    }),
                  },
                ],
                initialValue: header.limitRows,
              })(
                <InputNumber
                  disabled={
                    !getFieldValue('reportTypeCode') === 'ST' ||
                    !getFieldValue('reportTypeCode') === 'TS1' ||
                    getFieldValue('reportTypeCode') === 'C' ||
                    getFieldValue('reportTypeCode') === 'D' ||
                    getFieldValue('reportTypeCode') === 'U'
                  }
                  min={0}
                  style={{ width: '50%' }}
                  onChange={this.handleUpdateState}
                />
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.exportType')
                .d('导出类型')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('exportTypeList', {
                initialValue: header.exportTypeList || [],
              })(
                <Select
                  mode="multiple"
                  disabled={
                    !getFieldValue('reportTypeCode') ||
                    getFieldValue('reportTypeCode') === 'C' ||
                    (getFieldValue('reportTypeCode') === 'D' && !getFieldValue('templateTypeCode'))
                  }
                  allowClear
                >
                  {exportTypeList &&
                    exportTypeList.map((item) => (
                      <Option key={item} value={item}>
                        {item}
                      </Option>
                    ))}
                </Select>
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl
                .get('hrpt.reportDefinition.model.reportDefinition.pageFlag')
                .d('分页标识')}
              labelCol={{ span: 12 }}
              wrapperCol={{ span: 12 }}
            >
              {getFieldDecorator('pageFlag', {
                initialValue: isUndefined(match.params.id) ? 1 : header.pageFlag,
              })(<Switch onChange={this.handleUpdateState} />)}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hzero.common.status.asyncFlag').d('异步标识')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('asyncFlag', {
                initialValue: header.asyncFlag || 0,
              })(<Switch disabled={getFieldValue('reportTypeCode') === 'U'} />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: isUndefined(match.params.id) ? 1 : header.enabledFlag,
              })(<Switch onChange={this.handleUpdateState} />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      dispatch,
      saving,
      match,
      form,
      tenantRoleLevel,
      fetchDefinitionDetailLoading,
      getMetaColumnsLoading,
      getTemplateListLoading,
      fetchTemplateDetailLoading,
      createTemplateLoading,
      reportDefinition: {
        header = {},
        template = [],
        templatePagination = {},
        code: { type = [], sortType = [], templateTypeCode = [] },
        templateList = [],
        templateListPagination = {},
      },
    } = this.props;
    const {
      metaColumnsItem = {},
      templateDrawerVisible = false,
      metaColumnsDrawerVisible = false,
      metaColumnsSelectedRowKeys = [],
      templateSelectedRowKeys = [],
      templateListSelectedRowKeys = [],
      isCreateMetaColumn,
      isChangeContent,
      isColumnFlag,
    } = this.state;
    const isTemplateTab = form.getFieldValue('reportTypeCode') === 'D';
    const metaColumnsRowSelection = {
      selectedRowKeys: metaColumnsSelectedRowKeys,
      onChange: this.handleMetaColumnsRowSelectChange,
    };
    const templateRowSelection = {
      templateSelectedRowKeys,
      type: 'radio',
      onChange: this.handleTemplateRowSelectChange,
    };
    const templateListRowSelection = {
      templateListSelectedRowKeys,
      onChange: this.templateListRowSelectChange,
    };
    const spinning = isUndefined(match.params.id) ? false : fetchDefinitionDetailLoading;
    const headerTitle = isUndefined(match.params.id)
      ? intl.get('hrpt.reportDefinition.view.message.title.add').d('报表定义 - 添加')
      : intl.get('hrpt.reportDefinition.view.message.title.edit').d('报表定义 - 编辑');
    const metaColumnsTitle = metaColumnsItem.ordinal
      ? intl.get('hrpt.reportDefinition.view.message.drawer.edit').d('编辑列')
      : intl.get('hrpt.reportDefinition.view.message.drawer.add').d('添加列');
    const metaColumnsProps = {
      header,
      dispatch,
      type,
      sortType,
      metaColumnsRowSelection,
      loading: getMetaColumnsLoading,
      dataSource: header.metaColumns,
      onEdit: this.handleEditMetaColumnsContent,
    };
    const metaColumnsDrawerProps = {
      metaColumnsTitle,
      type,
      sortType,
      isCreateMetaColumn,
      anchor: 'right',
      visible: metaColumnsDrawerVisible,
      itemData: metaColumnsItem,
      onOk: this.handleSaveMetaColumnsContent,
      onCancel: this.handleCancelMetaColumns,
      onEditOk: this.handleEditMetaColumnsOk,
      onCheckUnique: this.checkUniqueMetaColumns,
    };
    const templateDrawerProps = {
      tenantRoleLevel,
      templateTypeCode,
      templateListPagination,
      templateListRowSelection,
      loading: getTemplateListLoading,
      confirmLoading: createTemplateLoading,
      dataSource: templateList,
      visible: templateDrawerVisible,
      onSearch: this.fetchInitTemplate,
      onRef: this.handleBindRef,
      onChange: this.fetchInitTemplate,
      onOk: this.handleAddTemplate,
      onCancel: this.handleCancelTemplate,
    };
    const templateProps = {
      tenantRoleLevel,
      templateRowSelection,
      templateTypeCode,
      loading: fetchTemplateDetailLoading,
      pagination: templatePagination,
      dataSource: template,
      onChangeDefaultTemplate: this.changeDefaultTemplate,
    };
    return (
      <>
        <Header
          title={headerTitle}
          isChange={isChangeContent}
          backPath="/hrpt/report-definition/list"
        >
          <Button icon="save" type="primary" onClick={this.handleSave} loading={saving}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
          <Button
            icon="hdd"
            onClick={this.getMetaMetaColumns}
            disabled={form.getFieldValue('reportTypeCode') === 'D'}
          >
            {intl.get('hrpt.reportDefinition.view.button.initColumn').d('初始化列')}
          </Button>
        </Header>
        <Content>
          <Card
            key="report-definition-header"
            bordered={false}
            title={<h3>{intl.get('hrpt.reportDefinition.view.message.reportTable').d('报表')}</h3>}
            className={DETAIL_CARD_CLASSNAME}
            loading={spinning}
          >
            {this.renderHeaderForm()}
          </Card>
          {isColumnFlag && (
            <Card
              key="report-definition-line"
              bordered={false}
              title={
                <h3>{intl.get('hrpt.reportDefinition.view.message.reportColumn').d('报表列')}</h3>
              }
              className={DETAIL_CARD_TABLE_CLASSNAME}
              loading={spinning}
            >
              <div className="table-list-operator">
                <Button
                  type="primary"
                  onClick={isTemplateTab ? this.handleInitTemplate : this.handleAddMetaColumns}
                  disabled={isTemplateTab && match.params.id === undefined}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
                <Button
                  onClick={isTemplateTab ? this.handleDeleteTemplate : this.handleDeleteMetaColumns}
                  disabled={
                    isTemplateTab
                      ? templateSelectedRowKeys.length === 0
                      : metaColumnsSelectedRowKeys.length === 0
                  }
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Button>
              </div>
              {form.getFieldValue('reportTypeCode') !== 'D' ? (
                <Tabs defaultActiveKey="initColumn" animated={false}>
                  <Tabs.TabPane
                    tab={intl.get('hrpt.reportDefinition.view.tab.initColumn').d('列信息')}
                    key="initColumn"
                  >
                    <MetaColumnsTable {...metaColumnsProps} />
                  </Tabs.TabPane>
                </Tabs>
              ) : (
                <Tabs defaultActiveKey="template" animated={false}>
                  <Tabs.TabPane
                    forceRender
                    tab={intl.get('hrpt.reportDefinition.view.tab.template').d('模板分配')}
                    key="template"
                  >
                    <TemplateTable {...templateProps} />
                  </Tabs.TabPane>
                </Tabs>
              )}
            </Card>
          )}
          <MetaColumnsDrawer {...metaColumnsDrawerProps} />
          <TemplateDrawer {...templateDrawerProps} />
        </Content>
      </>
    );
  }
}
