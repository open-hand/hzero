/*
 * index.js - 流程分类详情
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
  ROW_WRITE_ONLY_CLASSNAME,
} from 'utils/constants';

import List from './List';
import Drawer from './Drawer';
import DocumentsModal from './DocumentsModal';
import styles from './index.less';

const documentModalRowKey = 'documentId';
/**
 * 流程分类详情
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} categories - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */

@Form.create({ fieldNameProp: null })
@connect(({ categories, loading }) => ({
  categories,
  isSiteFlag: !isTenantRoleLevel(),
  loading: loading.effects['categories/fetchDetailHeader'],
  saving:
    loading.effects['categories/createCategories'] || loading.effects['categories/updateHeader'],
  fetchingList: loading.effects['categories/fetchDetailList'],
  fetchingCategories: loading.effects['categories/handleSearchDocuments'],
  savingVariable:
    loading.effects['categories/handleSaveVariables'] ||
    loading.effects['categories/handleUpdateVariables'],
}))
@formatterCollections({ code: ['hwfp.categories', 'hwfp.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    this.state = {
      documentsVisible: false,
      predefined: false, // 预定义标志
      dataSource: [],
      record: {},
      headerInfo: {},
      documentList: [], // 流程分类
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
    this.fetchList();
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
        type: 'categories/fetchDetailHeader',
        payload: {
          categoryId: id,
        },
      }).then(res => {
        if (res) {
          this.setState({
            headerInfo: res,
            documentList: res.processDocumentList || [],
            predefined: tenantId !== res.tenantId && isTenantRoleLevel(),
          });
        }
      });
    }
  }

  /**
   * 查询列表
   */
  @Bind()
  fetchList() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'categories/fetchDetailList',
        payload: {
          sourceId: id,
          sourceType: 'CATEGORY',
        },
      }).then(res => {
        if (res) {
          this.setState({
            dataSource: res || [],
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
      type: 'categories/fetchEnumMap',
    });
  }

  /**
   * 添加变量
   */
  @Bind()
  handleAddLine() {
    this.setState({ drawerVisible: true, record: {} });
  }

  /**
   * 保存流程分类头
   */
  @Bind()
  handleSave() {
    const { dispatch, form, match = {} } = this.props;
    const { headerInfo, documentList } = this.state;
    const {
      params: { id },
    } = match;
    form.validateFields((err, values) => {
      if (!err) {
        if (isUndefined(id)) {
          dispatch({
            type: 'categories/createCategories',
            payload: {
              ...values,
              documentIdSet: documentList.map(n => n[documentModalRowKey]),
            },
          }).then(res => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hwfp/setting/categories/detail/${res.categoryId}`,
                })
              );
              if (!headerInfo.categoryId) {
                this.setState({ id: res.categoryId });
              }
            }
          });
        } else {
          const { variableList, processDocumentList, ...otherHeaderInfo } = headerInfo;
          dispatch({
            type: 'categories/updateHeader',
            payload: {
              categoryId: id,
              processDocument: {
                ...otherHeaderInfo,
                ...values,
                documentIdSet: documentList.map(n => n[documentModalRowKey]),
              },
            },
          }).then(res => {
            if (res) {
              notification.success();
              this.fetchHeader();
            }
          });
        }
      }
    });
  }

  /**
   * 编辑变量
   * @param {object} record - 变量对象
   */
  @Bind()
  handleEditVariable(record) {
    this.setState({ drawerVisible: true, record });
  }

  /**
   * 删除变量
   * @param {object} record - 变量对象
   */
  @Bind()
  handleDeleteVariable(record) {
    const { dispatch } = this.props;
    const { variableId } = record;
    dispatch({
      type: 'categories/deleteVariable',
      payload: {
        variableId,
        processVariable: record,
      },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchList();
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
        type: 'categories/handleSaveVariables',
        payload: {
          ...values,
          sourceId: id,
          sourceType: 'CATEGORY',
        },
      }).then(res => {
        if (res) {
          this.fetchList();
          notification.success();
          this.setState({ drawerVisible: false, record: {} });
        }
      });
    } else {
      dispatch({
        type: 'categories/handleUpdateVariables',
        payload: {
          variableId,
          processVariable: values,
        },
      }).then(res => {
        if (res) {
          this.fetchList();
          notification.success();
          this.setState({ drawerVisible: false, record: {} });
        }
      });
    }
  }

  /**
   * 滑窗取消操作
   */
  @Bind()
  handleCancelOption() {
    this.setState({
      drawerVisible: false,
      record: {},
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
   * 查询流程单据列表
   * @param {*} fields
   */
  @Bind()
  handleSearchDocuments(fields) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'categories/handleSearchDocuments',
      payload: fields,
    });
  }

  /**
   * 添加流程单据
   * @param {Array} [selectedRows=[]]
   */
  @Bind()
  handleAddCategories(selectedRows = []) {
    const { documentList } = this.state;
    this.setState({
      documentList: [...documentList, ...selectedRows],
    });
  }

  /**
   * 关闭流程单据tag
   * @param {String} documentId
   */
  @Bind()
  handleCloseTag(documentId) {
    const { documentList } = this.state;
    this.setState({
      documentList: documentList.filter(n => n.documentId !== documentId),
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
      dispatch,
      loading,
      fetchingList,
      saving,
      savingVariable,
      fetchingCategories,
      isSiteFlag,
      categories: { line = [], dataType = [], operator = [], enumMap = {} },
    } = this.props;
    const {
      id,
      predefined,
      documentsVisible,
      tenantId,
      dataSource,
      record = {},
      drawerVisible = false,
      headerInfo = {},
      documentList = [],
    } = this.state;
    const headerTitle = intl.get('hwfp.categories.view.message.title').d('流程分类');
    const { getFieldDecorator } = form;
    const listProps = {
      predefined,
      dataSource,
      loading: fetchingList,
      onEdit: this.handleEditVariable,
      onDelete: this.handleDeleteVariable,
    };
    const drawerProps = {
      dispatch,
      enumMap,
      tenantId,
      dataType,
      operator,
      anchor: 'right',
      visible: drawerVisible,
      itemData: record,
      ruleList: line,
      loading: savingVariable,
      onHandleOk: this.handleSaveVariables,
      onCancel: this.handleCancelOption,
      title: intl.get('hwfp.common.model.rule.maintain').d('变量维护'),
    };
    const {
      categoryCode,
      description,
      enabledFlag,
      tenantName,
      _token,
      tenantId: headerTenantId,
    } = headerInfo;
    return (
      <>
        <Header title={headerTitle} backPath="/hwfp/setting/categories/list">
          <Button
            disabled={predefined || saving}
            icon="save"
            type="primary"
            onClick={this.handleSave}
            loading={saving}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Spin
            spinning={loading || fetchingList || false}
            wrapperClassName={DETAIL_EDIT_FORM_CLASSNAME}
          >
            <Form className={classnames(EDIT_FORM_CLASSNAME, styles['detail-form'])}>
              <Row className={id ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}>
                {isSiteFlag && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('entity.tenant.tag').d('租户')}
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
                )}
              </Row>
              <Row className={id ? ROW_READ_ONLY_CLASSNAME : ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hwfp.categories.model.categories.categoryCode')
                      .d('流程分类编码')}
                  >
                    {getFieldDecorator('categoryCode', {
                      initialValue: categoryCode,
                      // validateFirst: true,
                      // validateTrigger: 'onBlur',
                      rules: [
                        {
                          required: !id,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hwfp.common.model.common.code').d('编码'),
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
                        // {
                        //   validator: isUndefined(match.params.id) ? this.checkUnique : '',
                        // },
                      ],
                    })(
                      id ? (
                        <>{categoryCode}</>
                      ) : (
                        <Input trim inputChinese={false} typeCase="upper" />
                      )
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row className={ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hwfp.categories.model.categories.description')
                      .d('流程分类描述')}
                  >
                    {getFieldDecorator('description', {
                      rules: [
                        {
                          max: 240,
                          message: intl.get('hzero.common.validation.max', {
                            max: 240,
                          }),
                        },
                        {
                          required: !predefined,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl.get('hwfp.common.model.common.description').d('描述'),
                          }),
                        },
                      ],
                      initialValue: description,
                    })(
                      <TLEditor
                        label={intl.get('hwfp.common.model.common.description').d('描述')}
                        field="description"
                        inputSize={{ zh: 240, en: 240 }}
                        token={_token}
                        disabled={predefined}
                      />
                    )}
                  </Form.Item>
                </Col>
              </Row>
              <Row className={ROW_WRITE_ONLY_CLASSNAME}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get(`hzero.common.status.enable`).d('启用')}
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
                  label={intl.get(`hwfp.common.view.message.title.document`).d('流程单据')}
                >
                  {!predefined && (
                    <Button
                      disabled={predefined}
                      onClick={() => this.handleModalVisible('documentsVisible', true)}
                      className={styles['button-margin-bottom']}
                    >
                      {intl.get(`hwfp.common.view.button.addDocument`).d('新增流程单据')}
                    </Button>
                  )}
                  <div className={styles['form-item-control-wrapper']}>
                    {documentList.map(
                      item =>
                        item[documentModalRowKey] &&
                        (!predefined ? (
                          <Popconfirm
                            placement="topRight"
                            title={intl
                              .get('hzero.common.message.confirm.delete')
                              .d('是否删除此条记录？')}
                            onConfirm={() => this.handleCloseTag(item[documentModalRowKey])}
                          >
                            <Tag color="blue" closable={!predefined} onClose={this.preventDefault}>
                              {`${item.description}(${item.documentCode})`}
                            </Tag>
                          </Popconfirm>
                        ) : (
                          <Tag color="blue" closable={!predefined} onClose={this.preventDefault}>
                            {`${item.description}(${item.documentCode})`}
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
                        onClick={this.handleAddLine}
                        disabled={!id || predefined}
                      >
                        {intl.get('hwfp.common.view.button.addVariable').d('添加流程变量')}
                      </Button>
                    )}
                    <div className={styles['form-item-control-wrapper']}>
                      <List {...listProps} />
                    </div>
                  </Form.Item>
                </Row>
              )}
            </Form>
          </Spin>
          <Drawer {...drawerProps} />
          <DocumentsModal
            dataSource={documentList}
            loading={fetchingCategories}
            visible={documentsVisible}
            onHandleAddCategories={this.handleAddCategories}
            onCloseCategoryModal={() => this.handleModalVisible('documentsVisible', false)}
            onHandleSearchDocuments={this.handleSearchDocuments}
          />
        </Content>
      </>
    );
  }
}
