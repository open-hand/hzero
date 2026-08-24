/**
 * templateManage - 报表平台/模板管理
 * @date: 2018-11-19
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Select, Spin } from 'hzero-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { filter, isEmpty, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';
import TLEditor from 'components/TLEditor';
import Switch from 'components/Switch';
import Lov from 'components/Lov';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { isTenantRoleLevel } from 'utils/utils';
import notification from 'utils/notification';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';
import { openTab } from 'utils/menuTab';

import TemplateLineTable from './TemplateLineTable';
import TemplateLineDrawer from './TemplateLineDrawer';

/**
 * Form.Item 组件label、wrapper长度比例划分
 */
const { Option } = Select;

/**
 * 模板管理-行数据管理组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} templateManage - 数据源
 * @reactProps {!Object} fetchApproveHeaderLoading - 数据加载是否完成
 * @reactProps {!Object} saving - 保存是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ templateManage, global, loading }) => ({
  templateManage,
  global,
  tenantRoleLevel: isTenantRoleLevel(),
  fetchTemplateHeaderDetailLoading: loading.effects['templateManage/fetchTemplateHeaderDetail'],
  fetchTemplateLineLoading: loading.effects['templateManage/fetchTemplateLine'],
  saving:
    loading.effects['templateManage/createTemplateLine'] ||
    loading.effects['templateManage/editTemplateLine'],
  savingHeader:
    loading.effects['templateManage/createTemplateManage'] ||
    loading.effects['templateManage/editTemplateManage'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hrpt.templateManage', 'entity.tenant', 'entity.lang', 'entity.template'],
})
export default class Detail extends Component {
  /**
   * state初始化
   */
  state = {
    templateLineDrawerVisible: false, // 列信息模态框
    templateLineSelectedRowKeys: [], // 列信息选中行
    isChangeTemplateType: false, // 是否改变模板类型
  };

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    if (!isUndefined(id)) {
      dispatch({
        type: 'templateManage/fetchTemplateHeaderDetail',
        payload: {
          templateId: id,
        },
      });
      this.fetchTemplateLine();
    } else {
      dispatch({
        type: 'templateManage/updateState',
        payload: {
          header: {},
          line: {},
        },
      });
    }
    const lovCodes = {
      templateTypeCode: 'HRPT.TEMPLATE_TYPE', // 模板类型
    };
    // 初始化 值集
    dispatch({
      type: 'templateManage/batchCode',
      payload: {
        lovCodes,
      },
    });
    this.setState({
      isChangeTemplateType: false,
    });
  }

  /**
   * 查询行
   * @param {object} fields - 查询参数
   */
  @Bind()
  fetchTemplateLine(fields = {}) {
    const { dispatch, match } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'templateManage/fetchTemplateLine',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        templateId: id,
      },
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
      templateManage: { header = {} },
    } = this.props;
    form.validateFields((err, values) => {
      if (!err) {
        if (isUndefined(match.params.id)) {
          dispatch({
            type: 'templateManage/createTemplateManage', // 新增逻辑
            payload: { ...values },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hrpt/template-manage/detail/${res.templateId}`,
                })
              );
            }
          });
        } else {
          dispatch({
            type: 'templateManage/editTemplateManage', // 更新逻辑
            payload: {
              ...header,
              ...values,
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
   * 模板明细行-新建打开滑窗
   */
  @Bind()
  handleAddTemplateLine() {
    this.setState({
      templateLineDrawerVisible: true,
    });
  }

  /**
   * 模板明细行-关闭滑窗
   */
  @Bind()
  handleCancelTemplateLine() {
    this.setState({
      templateLineDrawerVisible: false,
    });
    this.props.dispatch({
      type: 'templateManage/updateState',
      payload: { lineDetail: {}, fileList: [] },
    });
  }

  /**
   * 模板明细行-新增滑窗保存操作
   */
  @Bind()
  handleSaveTemplateLineContent(values) {
    const {
      dispatch,
      match,
      templateManage: { linePagination = {}, header = {} },
    } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'templateManage/createTemplateLine',
      payload: { ...values, templateId: id, tenantId: header.tenantId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleCancelTemplateLine();
        this.fetchTemplateLine(linePagination);
      }
    });
  }

  /**
   * 模板明细行-编辑打开滑窗
   */
  @Bind()
  handleEditTemplateLineContent(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'templateManage/fetchTemplateLineDetail',
      payload: { templateDtlId: record.templateDtlId },
    }).then((res) => {
      if (res) {
        this.setState({
          templateLineDrawerVisible: true,
        });
      }
    });
  }

  /**
   * 模板明细行-编辑保存滑窗
   */
  @Bind()
  handleEditTemplateLineOk(values) {
    const {
      dispatch,
      match,
      templateManage: { linePagination = {}, lineDetail = {}, header = {} },
    } = this.props;
    const { id } = match.params;
    dispatch({
      type: 'templateManage/editTemplateLine',
      payload: { ...lineDetail, ...values, templateId: id, tenantId: header.tenantId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleCancelTemplateLine();
        this.fetchTemplateLine(linePagination);
      }
    });
  }

  /**
   * 模板明细行-获取删除选中行
   */
  @Bind()
  handleMetaColumnsRowSelectChange(selectedRowKeys) {
    this.setState({ templateLineSelectedRowKeys: selectedRowKeys });
  }

  /**
   * 模板明细行-批量删除
   */
  @Bind()
  handleDeleteTemplateLine() {
    const {
      dispatch,
      templateManage: { line = {}, linePagination },
    } = this.props;
    const { content = [] } = line;
    const { templateLineSelectedRowKeys } = this.state;
    const newParameters = filter(
      content,
      (item) => templateLineSelectedRowKeys.indexOf(item.templateDtlId) >= 0
    );
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk: () => {
        dispatch({
          type: 'templateManage/deleteTemplateLine',
          payload: { newParameters },
        }).then((res) => {
          if (res) {
            notification.success();
            this.fetchTemplateLine(linePagination);
            this.setState({ templateLineSelectedRowKeys: [] });
          }
        });
      },
    });
  }

  /**
   * 改变模板类型
   */
  @Bind()
  changeTemplateTypeCode(val) {
    const {
      templateManage: { header = {} },
    } = this.props;
    if (header.templateTypeCode !== val) {
      this.setState({
        isChangeTemplateType: true,
      });
    } else {
      this.setState({
        isChangeTemplateType: false,
      });
    }
  }

  renderHeaderForm() {
    const {
      form: { getFieldDecorator },
      templateManage: {
        header = {},
        code: { templateTypeCode = [] },
        line = {},
      },
      match,
      tenantRoleLevel,
    } = this.props;
    const { content = [] } = line;
    return (
      <Form>
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
                  !isUndefined(header.tenantId) ? (
                    <div>{header.tenantName}</div>
                  ) : (
                    <Lov code="HPFM.TENANT" textValue={header.tenantName} />
                  )
                )}
              </Form.Item>
            </Col>
          )}
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('entity.template.code').d('模板代码')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('templateCode', {
                initialValue: header.templateCode,
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.template.code').d('模板代码'),
                    }),
                  },
                  {
                    pattern: CODE_UPPER,
                    message: intl
                      .get('hzero.common.validation.codeUpper')
                      .d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”'),
                  },
                ], // 校验规则
              })(
                !isUndefined(header.templateCode) ? (
                  <div>{header.templateCode}</div>
                ) : (
                  <Input trim typeCase="upper" inputChinese={false} />
                )
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('entity.template.name').d('模板名称')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('templateName', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.template.name').d('模板名称'),
                    }),
                  },
                ],
                initialValue: header.templateName,
              })(
                <TLEditor
                  label={intl.get('entity.template.name').d('模板名称')}
                  field="templateName"
                  token={header ? header._token : null}
                />
              )}
            </Form.Item>
          </Col>
        </Row>
        <Row {...EDIT_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('entity.template.type').d('模板类型')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('templateTypeCode', {
                rules: [
                  {
                    required: true,
                    message: intl.get('hzero.common.validation.notNull', {
                      name: intl.get('entity.template.type').d('模板类型'),
                    }),
                  },
                ],
                initialValue: header.templateTypeCode,
              })(
                content.length !== 0 ? (
                  <div>{header.templateTypeMeaning}</div>
                ) : (
                  <Select
                    allowClear
                    onChange={
                      !isUndefined(match.params.id)
                        ? (val) => this.changeTemplateTypeCode(val)
                        : undefined
                    }
                  >
                    {templateTypeCode &&
                      templateTypeCode.map((item) => (
                        <Option key={item.value} value={item.value}>
                          {item.meaning}
                        </Option>
                      ))}
                  </Select>
                )
              )}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item label={intl.get('hzero.common.remark').d('备注')} {...EDIT_FORM_ITEM_LAYOUT}>
              {getFieldDecorator('remark', {
                initialValue: header.remark,
              })(<Input />)}
            </Form.Item>
          </Col>
          <Col {...FORM_COL_3_LAYOUT}>
            <Form.Item
              label={intl.get('hzero.common.status.enable').d('启用')}
              {...EDIT_FORM_ITEM_LAYOUT}
            >
              {getFieldDecorator('enabledFlag', {
                initialValue: isUndefined(match.params.id) ? 1 : header.enabledFlag,
              })(<Switch />)}
            </Form.Item>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * word 文件编辑
   */
  @Bind()
  handleRowWordEdit(record) {
    const {
      templateManage: { detailWordEditor = {} },
      dispatch,
    } = this.props;
    dispatch({
      type: 'templateManage/updateState',
      payload: {
        detailWordEditor: {
          ...detailWordEditor,
          [record.templateDtlId]: record,
        },
      },
    });
    openTab({
      key: `/hrpt/template-manage/word-editor/${record.templateDtlId}`,
      title: 'hzero.common.title.wordEdit',
      closable: true,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      saving,
      match,
      fetchTemplateHeaderDetailLoading,
      fetchTemplateLineLoading,
      savingHeader,
      form,
      templateManage: {
        line = {},
        linePagination = {},
        lineDetail = {},
        fileList = [],
        header = {},
      },
      global: { supportLanguage = [] },
    } = this.props;
    const { content = [] } = line;
    const {
      templateLineDrawerVisible = false,
      templateLineSelectedRowKeys = [],
      isChangeTemplateType = false,
    } = this.state;
    const templateLineRowSelection = {
      templateLineSelectedRowKeys,
      onChange: this.handleMetaColumnsRowSelectChange,
    };
    const templateTypeCodeValue =
      content.length !== 0 ? header.templateTypeCode : form.getFieldValue('templateTypeCode');
    const spinning = isUndefined(match.params.id) ? false : fetchTemplateHeaderDetailLoading;
    const headerTitle = isUndefined(match.params.id)
      ? intl.get('hrpt.templateManage.view.message.title.add').d('模板管理 - 添加')
      : intl.get('hrpt.templateManage.view.message.title.edit').d('模板管理 - 编辑');
    const templateLineTitle = lineDetail.templateDtlId
      ? intl.get('hrpt.templateManage.view.message.templateLineDrawer.edit').d('编辑模板明细')
      : intl.get('hrpt.templateManage.view.message.templateLineDrawer.add').d('添加模板明细');
    const templateLineProps = {
      templateTypeCodeValue,
      templateLineRowSelection,
      supportLanguage,
      linePagination,
      loading: fetchTemplateLineLoading,
      dataSource: line.content,
      onEdit: this.handleEditTemplateLineContent,
      onChange: this.fetchTemplateLine,
      onRowWordEdit: this.handleRowWordEdit,
    };
    const templateLineDrawerProps = {
      templateTypeCodeValue,
      templateLineTitle,
      supportLanguage,
      fileList,
      saving,
      anchor: 'right',
      visible: templateLineDrawerVisible,
      itemData: lineDetail,
      onOk: this.handleSaveTemplateLineContent,
      onCancel: this.handleCancelTemplateLine,
      onEditOk: this.handleEditTemplateLineOk,
    };
    return (
      <>
        <Header title={headerTitle} backPath="/hrpt/template-manage/list">
          <Button icon="save" type="primary" onClick={this.handleSave} loading={savingHeader}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Spin spinning={spinning}>
            <Card
              bordered={false}
              key="template-manage-header"
              className={DETAIL_CARD_CLASSNAME}
              title={
                <h3>
                  {intl.get('hrpt.templateManage.view.message.templateHeader').d('模板头信息')}
                </h3>
              }
              loading={spinning}
            >
              {this.renderHeaderForm()}
            </Card>
            <Card
              bordered={false}
              key="template-manage-detail"
              className={DETAIL_CARD_TABLE_CLASSNAME}
              title={<h3>{intl.get('entity.template.detail').d('模板明细')}</h3>}
              loading={spinning}
            >
              <div className="table-list-operator">
                <Button
                  onClick={this.handleAddTemplateLine}
                  disabled={isUndefined(match.params.id) || isChangeTemplateType === true}
                  icon="plus"
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </Button>
                <Button
                  onClick={this.handleDeleteTemplateLine}
                  disabled={templateLineSelectedRowKeys.length === 0}
                  icon="delete"
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </Button>
              </div>
              <TemplateLineTable {...templateLineProps} />
            </Card>
            <TemplateLineDrawer {...templateLineDrawerProps} />
          </Spin>
        </Content>
      </>
    );
  }
}
