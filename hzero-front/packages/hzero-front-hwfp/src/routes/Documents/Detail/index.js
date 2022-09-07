/*
 * index.js - 流程单据详情
 * @date: 2019-04-29
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Col, Form, Input, Popconfirm, Row, Spin, Tag } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { Bind } from 'lodash-decorators';
import { isUndefined } from 'lodash';
import classnames from 'classnames';

import Switch from 'components/Switch';
import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import TLEditor from 'components/TLEditor';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_EDIT_FORM_CLASSNAME,
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ITEM_LAYOUT_COL_3,
  FORM_COL_3_LAYOUT,
  ROW_READ_ONLY_CLASSNAME,
  ROW_READ_WRITE_CLASSNAME,
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';

import styles from './index.less';
import VariableList from './VariableList';
import VariableDrawer from './VariableDrawer';
import FormDrawer from './FormDrawer';
import CategoriesModal from './CategoriesModal';
import FormList from './FormList';
import EmailList from './EmailList';
import EmailDrawer from './EmailDrawer';

/**
 * 流程单据详情
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} documents - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
@connect(({ documents, loading }) => ({
  documents,
  isSiteFlag: !isTenantRoleLevel(),
  loading: loading.effects['documents/fetchDetailHeader'],
  saving: loading.effects['documents/createDocuments'] || loading.effects['documents/updateHeader'],
  fetchingVariableList: loading.effects['documents/fetchVariableList'],
  fetchingCategories: loading.effects['documents/handleSearchCategories'],
  savingVariable:
    loading.effects['documents/handleSaveVariables'] ||
    loading.effects['documents/handleUpdateVariables'],
  savingForm:
    loading.effects['documents/handleSaveForm'] || loading.effects['documents/handleUpdateForm'],
  savingEmail:
    loading.effects['documents/handleSaveEmail'] || loading.effects['documents/handleUpdateEmail'],
  fetchingFormList: loading.effects['documents/fetchFormList'],
  fetchingEmailList: loading.effects['documents/fetchEmailList'],
}))
@formatterCollections({ code: ['hwfp.documents', 'hwfp.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      categoryVisible: false,
      formDrawerVisible: false,
      variableDrawerVisible: false,
      emailDrawerVisible: false,
      predefined: false, // 预定义标志
      variableDataSource: [],
      variableRecord: {},
      formRecord: {}, // 表单当前编辑行
      emailRecord: {}, // 邮件表单当前编辑行
      headerInfo: {},
      processCategoryList: [], // 流程分类
      formDataSource: [], // 表单列表
      emailDataSource: [], // 邮件列表
      id: props.match.params.id,
      tenantId: getCurrentOrganizationId(),
    };
  }

  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    this.fetchHeader();
    this.fetchVariableList();
    this.fetchFormList();
    this.fetchEmailList();
    this.fetchEnumMap();
  }

  /**
   * 查询头
   */
  @Bind()
  fetchHeader() {
    const { tenantId } = this.state;
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'documents/fetchDetailHeader',
        payload: {
          documentId: id,
        },
      }).then(res => {
        if (res) {
          this.setState({
            headerInfo: res,
            processCategoryList: res.processCategoryList || [],
            predefined: tenantId !== res.tenantId && isTenantRoleLevel(),
          });
        }
      });
    }
  }

  /**
   * 查询变量列表
   */
  @Bind()
  fetchVariableList() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'documents/fetchVariableList',
        payload: {
          sourceId: id,
          sourceType: 'DOCUMENT',
        },
      }).then(res => {
        if (res) {
          this.setState({
            variableDataSource: res || [],
          });
        }
      });
    }
  }

  /**
   * 查询表单列表
   */
  @Bind()
  fetchFormList() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'documents/fetchFormList',
        payload: {
          sourceId: id,
        },
      }).then(res => {
        if (res) {
          this.setState({
            formDataSource: res || [],
          });
        }
      });
    }
  }

  /**
   * 查询邮件列表
   */
  @Bind()
  fetchEmailList() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'documents/fetchEmailList',
        payload: {
          documentId: id,
        },
      }).then(res => {
        if (res) {
          this.setState({
            emailDataSource: res.content || [],
          });
        }
      });
    }
  }

  /**
   * 查询值集
   */
  @Bind()
  fetchEnumMap() {
    const { dispatch } = this.props;
    dispatch({
      type: 'documents/fetchEnumMap',
    });
  }

  /**
   * 添加变量
   */
  @Bind()
  handleAddVariable() {
    this.setState({ variableDrawerVisible: true });
  }

  @Bind()
  handleAddForm() {
    this.setState({ formDrawerVisible: true });
  }

  @Bind()
  handleAddEmail() {
    this.setState({ emailDrawerVisible: true });
  }

  /**
   * 保存流程单据头
   */
  @Bind()
  handleSave() {
    const { dispatch, form, match = {} } = this.props;
    const { headerInfo, processCategoryList } = this.state;
    const {
      params: { id },
    } = match;
    form.validateFields((err, values) => {
      if (!err) {
        if (isUndefined(id)) {
          dispatch({
            type: 'documents/createDocuments',
            payload: {
              ...values,
              categoryIdSet: processCategoryList.map(n => n.categoryId),
            },
          }).then(res => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hwfp/setting/documents/detail/${res.documentId}`,
                })
              );
            }
            if (!headerInfo.documentId) {
              this.setState({ id: res.documentId });
            }
          });
        } else {
          const { variableList, ...otherHeaderInfo } = headerInfo;
          dispatch({
            type: 'documents/updateHeader',
            payload: {
              documentId: id,
              processDocument: {
                ...otherHeaderInfo,
                ...values,
                categoryIdSet: processCategoryList.map(n => n.categoryId),
              },
            },
          }).then(res => {
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
   * 编辑变量
   * @param {object} variableRecord - 变量对象
   */
  @Bind()
  handleEditVariable(variableRecord) {
    this.setState({ variableDrawerVisible: true, variableRecord });
  }

  /**
   * 编辑表单
   * @param {object} formRecord - 变量对象
   */
  @Bind()
  handleEditForm(formRecord) {
    this.setState({ formDrawerVisible: true, formRecord });
  }

  /**
   * 编辑邮件
   * @param {object} emailRecord - 变量对象
   */
  @Bind()
  handleEditEmail(emailRecord) {
    this.setState({ emailDrawerVisible: true, emailRecord });
  }

  /**
   * 删除变量
   * @param {obejct} variableRecord - 变量对象
   */
  @Bind()
  handleDeleteVariable(variableRecord) {
    const { dispatch } = this.props;
    const { variableId } = variableRecord;
    dispatch({
      type: 'documents/deleteVariable',
      payload: {
        variableId,
        processVariable: variableRecord,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchVariableList();
      }
    });
  }

  /**
   * 删除表单
   * @param {obejct} record - 表单对象
   */
  @Bind()
  handleDeleteForm(record) {
    const { dispatch } = this.props;
    const { formId } = record;
    dispatch({
      type: 'documents/deleteForm',
      payload: {
        formId,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchFormList();
      }
    });
  }

  /**
   * 删除邮件
   * @param {obejct} emailRecord - 邮件对象
   */
  @Bind()
  handleDeleteEmail(emailRecord) {
    const { dispatch } = this.props;
    // const { templateId } = emailRecord;
    dispatch({
      type: 'documents/deleteEmail',
      payload: {
        processVariable: emailRecord,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchEmailList();
      }
    });
  }

  /**
   * 保存流程变量
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveVariables(values) {
    const { dispatch } = this.props;
    const { id } = this.state;
    const { variableId } = values;
    if (!variableId) {
      dispatch({
        type: 'documents/handleSaveVariables',
        payload: {
          ...values,
          sourceId: id,
          sourceType: 'DOCUMENT',
        },
      }).then(res => {
        if (res) {
          this.fetchVariableList();
          notification.success();
          this.handleCancelOption();
        }
      });
    } else {
      dispatch({
        type: 'documents/handleUpdateVariables',
        payload: {
          variableId,
          processVariable: values,
        },
      }).then(res => {
        if (res) {
          this.fetchVariableList();
          notification.success();
          this.handleCancelOption();
        }
      });
    }
  }

  /**
   * 保存流程表单
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveForm(values) {
    const { dispatch } = this.props;
    const { id } = this.state;
    const { formId } = values;
    if (!formId) {
      dispatch({
        type: 'documents/handleSaveForm',
        payload: {
          ...values,
          documentId: id,
        },
      }).then(res => {
        if (res) {
          this.fetchFormList();
          notification.success();
          this.handleCancelFormDrawer();
        }
      });
    } else {
      dispatch({
        type: 'documents/handleUpdateForm',
        payload: {
          formId,
          processVariable: values,
        },
      }).then(res => {
        if (res) {
          this.fetchFormList();
          notification.success();
          this.handleCancelFormDrawer();
        }
      });
    }
  }

  /**
   * 保存邮件表单
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveEmail(values) {
    const { dispatch } = this.props;
    const { id } = this.state;
    const { templateId } = values;
    if (!templateId) {
      dispatch({
        type: 'documents/handleSaveEmail',
        payload: {
          ...values,
          documentId: id,
        },
      }).then(res => {
        if (res) {
          this.fetchEmailList();
          notification.success();
          this.handleCancelEmailDrawer();
        }
      });
    } else {
      dispatch({
        type: 'documents/handleUpdateEmail',
        payload: {
          templateId,
          processVariable: values,
        },
      }).then(res => {
        if (res) {
          this.fetchEmailList();
          notification.success();
          this.handleCancelEmailDrawer();
        }
      });
    }
  }

  /**
   * 变量滑窗取消操作
   */
  @Bind()
  handleCancelOption() {
    this.setState({
      variableDrawerVisible: false,
      variableRecord: {},
    });
  }

  /**
   * 变量滑窗取消操作
   */
  @Bind()
  handleCancelFormDrawer() {
    this.setState({
      formDrawerVisible: false,
      formRecord: {},
    });
  }

  /**
   * 变量滑窗取消操作
   */
  @Bind()
  handleCancelEmailDrawer() {
    this.setState({
      emailDrawerVisible: false,
      emailRecord: {},
    });
  }

  /**
   * handleModalVisible - 改变弹窗显隐
   * @param {*} field
   * @param {*} value
   */
  @Bind()
  handleModalVisible(field, value) {
    this.setState({ [field]: value });
  }

  /**
   *
   * 查询流程分类列表
   * @param {*} fields
   */
  @Bind()
  handleSearchCategories(fields) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'documents/handleSearchCategories',
      payload: fields,
    });
  }

  /**
   * 添加流程分类
   * @param {Array} [selectedRows=[]]
   */
  @Bind()
  handleAddCategories(selectedRows = []) {
    const { processCategoryList } = this.state;
    this.setState({
      processCategoryList: [...processCategoryList, ...selectedRows],
    });
  }

  /**
   * 关闭流程分类tag
   * @param {String} categoryId
   */
  @Bind()
  handleCloseTag(categoryId) {
    const { processCategoryList } = this.state;
    this.setState({
      processCategoryList: processCategoryList.filter(n => n.categoryId !== categoryId),
    });
  }

  /**
   * 阻止tag默认删除事件
   * @param {*} e
   */
  @Bind()
  preventDefault(e) {
    e.preventDefault();
  }

  render() {
    const {
      form,
      isSiteFlag,
      dispatch,
      loading,
      fetchingVariableList,
      saving,
      savingVariable,
      savingForm,
      savingEmail,
      fetchingCategories,
      fetchingFormList,
      fetchingEmailList,
      documents: { line = [], dataType = [], operator = [], enumMap = {} },
    } = this.props;
    const {
      id,
      predefined,
      categoryVisible,
      tenantId,
      variableDataSource,
      variableRecord = {},
      formRecord = {},
      emailRecord = {},
      formDataSource = [],
      emailDataSource = [],
      variableDrawerVisible = false,
      formDrawerVisible = false,
      emailDrawerVisible = false,
      headerInfo = {},
      processCategoryList = [],
    } = this.state;
    const headerTitle = intl.get('hwfp.common.view.message.title.document').d('流程单据');
    const { getFieldDecorator } = form;
    const variableListProps = {
      predefined,
      dataSource: variableDataSource,
      loading: fetchingVariableList,
      onEdit: this.handleEditVariable,
      onDelete: this.handleDeleteVariable,
    };
    const formListProps = {
      predefined,
      dataSource: formDataSource,
      loading: fetchingFormList,
      onEdit: this.handleEditForm,
      onDelete: this.handleDeleteForm,
    };
    const emailListProps = {
      predefined,
      dataSource: emailDataSource,
      loading: fetchingEmailList,
      onEdit: this.handleEditEmail,
      onDelete: this.handleDeleteEmail,
    };
    const variableDrawerProps = {
      dispatch,
      enumMap,
      tenantId,
      dataType,
      operator,
      loading: savingVariable,
      anchor: 'right',
      visible: variableDrawerVisible,
      itemData: variableRecord,
      ruleList: line,
      onHandleOk: this.handleSaveVariables,
      onCancel: this.handleCancelOption,
      title: intl.get('hwfp.common.view.message.title.variableMaintain').d('变量维护'),
    };
    const formDrawerProps = {
      dispatch,
      enumMap,
      tenantId,
      dataType,
      operator,
      loading: savingForm,
      anchor: 'right',
      visible: formDrawerVisible,
      itemData: formRecord,
      ruleList: line,
      onHandleOk: this.handleSaveForm,
      onCancel: this.handleCancelFormDrawer,
      title: intl.get('hwfp.common.view.message.title.formMaintain').d('表单维护'),
    };
    const emailDrawerProps = {
      dispatch,
      enumMap,
      tenantId,
      dataType,
      operator,
      loading: savingEmail,
      anchor: 'right',
      visible: emailDrawerVisible,
      itemData: emailRecord,
      ruleList: line,
      onHandleOk: this.handleSaveEmail,
      onCancel: this.handleCancelEmailDrawer,
      title: intl.get('hwfp.common.view.message.title.emailMaintain').d('邮件维护'),
    };
    const {
      enabledFlag,
      documentCode,
      description,
      tenantName,
      _token,
      tenantId: headerTenantId,
    } = headerInfo;
    return (
      <>
        <Header title={headerTitle} backPath="/hwfp/setting/documents/list">
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSave}
            disabled={predefined || saving}
            loading={saving}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Spin spinning={loading || false}>
            <Form
              className={classnames(
                styles['detail-form'],
                DETAIL_EDIT_FORM_CLASSNAME,
                EDIT_FORM_CLASSNAME
              )}
            >
              {isSiteFlag && (
                <Row className={id ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl.get('entity.tenant.tag').d('租户')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('tenantId', {
                        initialValue: headerTenantId,
                        rules: [
                          {
                            required: !id,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('entity.tenant.tag').d('租户'),
                            }),
                          },
                        ],
                      })(
                        id ? <>{tenantName}</> : <Lov textValue={tenantName} code="HPFM.TENANT" />
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              )}
              <Row
                className={classnames({
                  [ROW_WRITE_ONLY_CLASSNAME]: !id && !predefined,
                  [ROW_READ_WRITE_CLASSNAME]: id ? !predefined : predefined,
                  [ROW_READ_ONLY_CLASSNAME]: id && predefined,
                })}
              >
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hwfp.common.model.common.documentCode').d('流程单据编码')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('documentCode', {
                      initialValue: documentCode,
                      rules: [
                        {
                          required: !id,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hwfp.common.model.common.documentCode')
                              .d('流程单据编码'),
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
                    })(
                      id ? (
                        <>{documentCode}</>
                      ) : (
                        <Input trim inputChinese={false} disabled={id} typeCase="upper" />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row className={predefined ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl
                      .get('hwfp.common.model.common.documentDescription')
                      .d('流程单据描述')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('description', {
                      rules: [
                        {
                          required: !predefined,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hwfp.common.model.common.documentDescription')
                              .d('流程单据描述'),
                          }),
                        },
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                      ],
                      initialValue: description,
                    })(
                      predefined ? (
                        <>{description}</>
                      ) : (
                        <TLEditor
                          label={intl
                            .get('hwfp.common.model.common.documentDescription')
                            .d('流程单据描述')}
                          field="description"
                          inputSize={{ zh: 240, en: 240 }}
                          token={_token}
                          disabled={predefined}
                        />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row className={ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hzero.common.status.enable').d('启用')}
                  >
                    {getFieldDecorator('enabledFlag', {
                      initialValue: enabledFlag === 0 ? 0 : 1,
                    })(<Switch disabled={predefined} />)}
                  </Form.Item>
                </Col>
              </Row>
              <Row className={ROW_READ_ONLY_CLASSNAME}>
                <Form.Item
                  {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                  className={styles['add-document-form']}
                  label={intl.get(`hwfp.common.view.message.title.category`).d('流程分类')}
                >
                  {!predefined && (
                    <Button
                      disabled={predefined}
                      className={styles['button-margin-bottom']}
                      onClick={() => this.handleModalVisible('categoryVisible', true)}
                    >
                      {intl.get(`hwfp.common.view.button.addCategory`).d('新增流程分类')}
                    </Button>
                  )}
                  <div className={styles['form-item-control-wrapper']}>
                    {processCategoryList.map(
                      item =>
                        item.categoryId &&
                        (!predefined ? (
                          <Popconfirm
                            placement="topRight"
                            title={intl
                              .get('hzero.common.message.confirm.delete')
                              .d('是否删除此条记录？')}
                            onConfirm={() => this.handleCloseTag(item.categoryId)}
                          >
                            <Tag color="blue" closable onClose={this.preventDefault}>
                              {`${item.description}(${item.categoryCode})`}
                            </Tag>
                          </Popconfirm>
                        ) : (
                          <Tag color="blue" closable={!predefined} onClose={this.preventDefault}>
                            {`${item.description}(${item.categoryCode})`}
                          </Tag>
                        ))
                    )}
                  </div>
                </Form.Item>
              </Row>
              {id && (
                <Row className={ROW_WRITE_ONLY_CLASSNAME}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                    className={styles['add-document-form']}
                    label={intl.get(`hwfp.common.view.message.title.variable`).d('流程变量')}
                  >
                    {!predefined && (
                      <Button
                        className={styles['button-margin-bottom']}
                        onClick={this.handleAddVariable}
                        disabled={!id || predefined}
                      >
                        {intl.get('hwfp.common.view.button.addVariable').d('添加流程变量')}
                      </Button>
                    )}
                    <div className={classnames(styles['form-item-control-wrapper'])}>
                      <VariableList {...variableListProps} />
                    </div>
                  </Form.Item>
                </Row>
              )}
              {id && (
                <Row className={classnames(ROW_WRITE_ONLY_CLASSNAME, styles['list-margin-top'])}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                    className={styles['add-document-form']}
                    label={intl.get('hwfp.common.view.message.title.form').d('流程表单')}
                  >
                    {!predefined && (
                      <Button
                        className={styles['button-margin-bottom']}
                        onClick={this.handleAddForm}
                        disabled={!id || predefined}
                      >
                        {intl.get('hwfp.common.view.button.addForm').d('新增流程表单')}
                      </Button>
                    )}
                    <div className={classnames(styles['form-item-control-wrapper'])}>
                      <FormList {...formListProps} />
                    </div>
                  </Form.Item>
                </Row>
              )}
              {id && (
                <Row className={classnames(ROW_WRITE_ONLY_CLASSNAME, styles['list-margin-top'])}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT_COL_3}
                    className={styles['add-document-form']}
                    label={intl.get('hwfp.common.view.message.title.email').d('邮件审批表单')}
                  >
                    {!predefined && (
                      <Button
                        className={styles['button-margin-bottom']}
                        onClick={this.handleAddEmail}
                        disabled={!id || predefined}
                      >
                        {intl.get('hwfp.common.view.button.addEmail').d('新增邮件审批表单')}
                      </Button>
                    )}
                    <div className={classnames(styles['form-item-control-wrapper'])}>
                      <EmailList {...emailListProps} />
                    </div>
                  </Form.Item>
                </Row>
              )}
            </Form>
          </Spin>
          <VariableDrawer {...variableDrawerProps} />
          <FormDrawer {...formDrawerProps} />
          <EmailDrawer {...emailDrawerProps} />
          <CategoriesModal
            headerInfo={headerInfo}
            dataSource={processCategoryList}
            loading={fetchingCategories}
            visible={categoryVisible}
            onHandleAddCategories={this.handleAddCategories}
            onCloseCategoryModal={() => this.handleModalVisible('categoryVisible', false)}
            onHandleSearchCategories={this.handleSearchCategories}
          />
        </Content>
      </>
    );
  }
}
