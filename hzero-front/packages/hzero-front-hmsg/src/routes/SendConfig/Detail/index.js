/**
 * sendConfig -  发送配置
 * @date: 2018-9-7
 * @author: CJ <juan.chen01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Card, Col, Form, Input, Row, Cascader } from 'hzero-ui';
import { DataSet } from 'choerodon-ui/pro';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { filter, isNumber, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuid from 'uuid/v4';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import Lov from 'components/Lov';
import Switch from 'components/Switch';
import { Button as ButtonPermission } from 'components/Permission';
import TLEditor from 'components/TLEditor';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { CODE_UPPER } from 'utils/regExp';
import {
  DETAIL_CARD_CLASSNAME,
  DETAIL_CARD_TABLE_CLASSNAME,
  EDIT_FORM_CLASSNAME,
  EDIT_FORM_ITEM_LAYOUT,
  EDIT_FORM_ROW_LAYOUT,
  FORM_COL_3_LAYOUT,
} from 'utils/constants';

import TemplateDrawer from './TemplateDrawer';
import ListTable from './ListTable';
import Drawer from './Drawer';

/**
 * 发送配置-行数据管理组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} sendConfig - 数据源
 * @reactProps {!boolean} fetchHeaderLoading - 数据加载是否完成
 * @reactProps {!boolean} createLoading - 新增是否完成
 * @reactProps {!boolean} updateLoading - 编辑是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ sendConfig, loading }) => ({
  sendConfig,
  tenantRoleLevel: isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  fetchHeaderLoading: loading.effects['sendConfig/fetchHeader'],
  createLoading: loading.effects['sendConfig/saveHeader'],
  updateLoading: loading.effects['sendConfig/updateHeader'],
  fetchTemplateLoading: loading.effects['sendConfig/fetchTemplateData'],
  fetchWebHookLoading: loading.effects['sendConfig/getWebhook'],
  deleteWebHookLoading: loading.effects['sendConfig/deleteWebhook'],
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hmsg.sendConfig', 'entity.tenant', 'hmsg.common'] })
export default class Detail extends Component {
  constructor(props) {
    super(props);
    const { location } = props;
    const { isCopy, tempServerId } = queryString.parse(location.search);
    this.state = {
      targetItem: {}, // 表格中的一条记录
      templateDrawerVisible: false, // 模板模态框状态
      categoryMeaning: '',
      subcategoryMeaning: '',
      clearSelect: (e) => e,
      isCopy,
      tempServerId,
    };

    this.ds = new DataSet({
      primaryKey: 'serverCode',
      autoCreate: true,
      fields: [
        {
          name: 'code',
          type: 'object',
          lovCode: 'HMSG.SERVER_WEBHOOK',
          multiple: true,
        },
      ],
      cacheSelection: true,
      selection: 'multiple',
      events: {
        update: this.handleDataSetChange,
      },
    });
  }

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    this.handleSearch();
  }

  @Bind()
  handleSearch() {
    const { dispatch, match } = this.props;
    const { isCopy, tempServerId } = this.state;
    const { id } = match.params;
    if (!isUndefined(id) || isCopy) {
      dispatch({
        type: isCopy ? 'sendConfig/fetchCopyHeader' : 'sendConfig/fetchHeader',
        payload: {
          tempServerId: isCopy ? tempServerId : id,
        },
      }).then((res) => {
        if (res && isCopy) {
          const { serverList = [], ...otherValues } = res;
          const arr = serverList.map((item) => {
            return { ...item, tempServerLineId: `create-${uuid()}` };
          });
          dispatch({
            type: 'sendConfig/updateState',
            payload: {
              copyDetail: { serverList: arr, ...otherValues },
            },
          });
        }
      });
    } else {
      dispatch({
        type: 'sendConfig/updateState',
        payload: {
          header: {},
        },
      });
    }
    dispatch({
      type: 'sendConfig/fetchType',
    });
    dispatch({
      type: 'sendConfig/queryMessageType',
    });
  }

  /**
   * 添加参数
   */
  @Bind()
  handleAddLine() {
    this.setState({ drawerVisible: true, targetItem: {} });
  }

  /**
   * 添加参数
   */
  @Bind()
  handleDataSetChange({ value, dataSet }) {
    this.handleAddWebHook(value);
    dataSet.reset();
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
      sendConfig: { header = {}, copyDetail = {} },
      tenantId,
      tenantRoleLevel,
    } = this.props;
    const { isCopy = false } = this.state;
    const { serverList = [], ...otherValues } = header;
    const { categoryMeaning, subcategoryMeaning } = this.state;
    form.validateFields((err, values) => {
      if (!err) {
        const anotherParameters = (isCopy ? copyDetail.serverList : serverList).map((item) => ({
          ...item,
          tempServerLineId:
            item.tempServerLineId && item.tempServerLineId.toString().startsWith('create-')
              ? ''
              : item.tempServerLineId,
        }));
        const { categoryCode, ...rest } = values;
        const params = {
          ...rest,
          categoryCode: categoryCode[0],
          subcategoryCode: categoryCode[1],
          tenantId: tenantRoleLevel ? tenantId : values.tenantId,
        };
        if (isUndefined(match.params.id) || isCopy) {
          dispatch({
            type: 'sendConfig/saveHeader', // 新增逻辑
            payload: {
              dto: {
                serverList: [...anotherParameters],
                ...params,
                categoryMeaning: categoryMeaning || header.categoryMeaning,
                subcategoryMeaning: subcategoryMeaning || header.subcategoryMeaning,
              },
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch(
                routerRedux.push({
                  pathname: `/hmsg/send-config/detail/${res.tempServerId}`,
                })
              );
            }
          });
        } else {
          dispatch({
            type: 'sendConfig/updateHeader', // 更新逻辑
            payload: {
              tempServerId: header.tempServerId,
              dto: {
                serverList: [...anotherParameters],
                ...otherValues,
                ...params,
                categoryMeaning: categoryMeaning || header.categoryMeaning,
                subcategoryMeaning: subcategoryMeaning || header.subcategoryMeaning,
              },
            },
          }).then((res) => {
            if (res) {
              notification.success();
              dispatch({
                type: 'sendConfig/updateState',
                payload: {
                  header: res,
                },
              });
            }
          });
        }
      }
    });
  }

  /**
   * 参数列表- 行编辑
   * @param {object} record - 规则对象
   */
  @Bind()
  handleEditContent(record) {
    this.setState({ drawerVisible: true, targetItem: { ...record } });
    this.handleFetchWebHook(record);
  }

  /**
   * 参数列表- 行编辑
   * @param {object} record - 规则对象
   */
  @Bind()
  handleFetchWebHook(record) {
    if (isNumber(record.objectVersionNumber) && record.typeCode === 'WEB_HOOK') {
      const {
        dispatch,
        tenantId,
        tenantRoleLevel,
        form: { getFieldValue },
      } = this.props;
      this.ds
        .getField('code')
        .setLovPara('tenantId', tenantRoleLevel ? tenantId : getFieldValue('tenantId'));
      this.ds.getField('code').setLovPara('tempServerLineId', record.tempServerLineId);
      dispatch({
        type: 'sendConfig/getWebhook',
        payload: { ...record },
      }).then((res) => {
        if (res) {
          this.state.clearSelect();
        }
      });
    }
  }

  @Bind()
  getRef(clearSelect) {
    this.setState({ clearSelect });
  }

  /**
   * 参数列表- 行删除
   * @param {obejct} record - 规则对象
   */
  @Bind()
  handleDeleteContent(record) {
    const {
      dispatch,
      sendConfig: { header = {}, copyDetail },
    } = this.props;
    const { isCopy } = this.state;
    const { serverList = [], ...otherValues } = header;
    const { serverList: copyServerList = [], ...otherCopyValues } = copyDetail;
    const recordTempServerLineId = record.tempServerLineId;
    if (!isCopy && !recordTempServerLineId.toString().startsWith('create-')) {
      dispatch({
        type: 'sendConfig/deleteLine',
        payload: { ...record },
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    } else if (isCopy) {
      // 删除的操作
      const newParameters = filter(
        copyServerList,
        (item) => recordTempServerLineId !== item.tempServerLineId
      );
      dispatch({
        type: 'sendConfig/updateState',
        payload: {
          copyDetail: { serverList: [...newParameters], ...otherCopyValues },
        },
      });
    } else {
      // 删除的操作
      const newParameters = filter(
        serverList,
        (item) => recordTempServerLineId !== item.tempServerLineId
      );
      dispatch({
        type: 'sendConfig/updateState',
        payload: {
          header: { serverList: [...newParameters], ...otherValues },
        },
      });
    }
  }

  /**
   * 新增滑窗保存操作
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveContent(values) {
    const {
      dispatch,
      sendConfig: { header = {}, copyDetail = {} },
    } = this.props;
    const { isCopy } = this.state;
    const { serverList = [], ...otherValues } = header;
    const { serverList: copyServerList = [], ...otherCopyValues } = copyDetail;
    const value = {
      tempServerLineId: `create-${uuid()}`,
      ...values,
    };
    dispatch({
      type: 'sendConfig/updateState',
      payload: {
        header: { serverList: [...serverList, value], ...otherValues },
      },
    });
    if (isCopy) {
      dispatch({
        type: 'sendConfig/updateState',
        payload: {
          copyDetail: { serverList: [...copyServerList, value], ...otherCopyValues },
        },
      });
    }
    this.setState({ drawerVisible: false, targetItem: {} });
  }

  // 编辑保存滑窗
  @Bind()
  handleEditOk(values) {
    const {
      dispatch,
      sendConfig: { header = {}, copyDetail = {} },
    } = this.props;
    const { isCopy } = this.state;
    const { serverList = [], ...otherValues } = header;
    const { serverList: copyServerList = [], ...otherCopyValues } = copyDetail;
    const newList = serverList.map((item) => {
      if (item.tempServerLineId === values.tempServerLineId) {
        return { ...item, ...values };
      }
      return item;
    });
    const newCopyList = copyServerList.map((i) => {
      if (i.tempServerLineId === values.tempServerLineId) {
        return { ...i, ...values };
      }
      return i;
    });
    dispatch({
      type: 'sendConfig/updateState',
      payload: { header: { serverList: newList, ...otherValues } },
    });
    if (isCopy) {
      dispatch({
        type: 'sendConfig/updateState',
        payload: { copyDetail: { serverList: newCopyList, ...otherCopyValues } },
      });
    }
    this.setState({ drawerVisible: false, targetItem: {} });
  }

  //
  @Bind()
  handleAddWebHook(payload) {
    const { dispatch } = this.props;
    const { targetItem } = this.state;
    dispatch({
      type: 'sendConfig/createWebhook',
      payload: {
        tempServerLineId: targetItem.tempServerLineId,
        payload: payload.map((item) => {
          return { ...item, tempServerId: targetItem.tempServerId };
        }),
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleFetchWebHook(targetItem);
      }
    });
  }

  @Bind()
  handleDeleteWebHook(payload) {
    const { dispatch } = this.props;
    const { targetItem } = this.state;
    dispatch({
      type: 'sendConfig/deleteWebhook',
      payload: {
        tempServerLineId: targetItem.tempServerLineId,
        payload: payload.map((item) => {
          return { ...item, tempServerId: targetItem.tempServerId };
        }),
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleFetchWebHook(targetItem);
      }
    });
  }

  /**
   * 滑窗取消操作
   */
  @Bind()
  handleCancelOption() {
    this.setState({
      drawerVisible: false,
      targetItem: {},
    });
  }

  /**
   * 改变租户，清空模板和服务
   */
  @Bind()
  changeTenant() {
    const { dispatch } = this.props;
    dispatch({
      type: 'sendConfig/updateState',
      payload: {
        header: { serverList: [] },
      },
    });
  }

  /**
   * 打开模板模态框
   * @param {object} record
   * @memberof Detail
   */
  @Bind()
  openTemplateDrawer(record) {
    const {
      dispatch,
      tenantRoleLevel,
      sendConfig: { header = {} },
    } = this.props;
    const params = !tenantRoleLevel
      ? { tenantId: header.tenantId, templateCode: record.templateCode }
      : { templateCode: record.templateCode };
    dispatch({
      type: 'sendConfig/fetchTemplateData',
      payload: params,
    });
    this.setState({
      templateDrawerVisible: true,
    });
  }

  /**
   * 关闭模板模态框
   */
  @Bind()
  closeTemplateDrawer() {
    this.setState({
      templateDrawerVisible: false,
    });
  }

  @Bind()
  handleCategoryCodeChange(_, selectedOptions) {
    this.setState({
      categoryMeaning: selectedOptions[0] && selectedOptions[0].meaning,
      subcategoryMeaning: selectedOptions[1] && selectedOptions[1].meaning,
    });
  }

  /**
   * render
   * @returns React.element
   */
  render() {
    const {
      form,
      fetchTemplateLoading,
      fetchHeaderLoading,
      fetchWebHookLoading,
      deleteWebHookLoading,
      createLoading,
      updateLoading,
      match,
      sendConfig: {
        header = {},
        messageType = [],
        copyDetail = {},
        templateData = {},
        categoryTree = [],
        webHook,
      },
      tenantRoleLevel,
      tenantId,
      match: { path },
    } = this.props;
    const { isCopy } = this.state;
    const { targetItem = {}, drawerVisible = false, templateDrawerVisible } = this.state;
    const headerTitle = isUndefined(match.params.id)
      ? intl.get('hmsg.sendConfig.view.message.title.add').d('消息发送配置 - 添加')
      : intl.get('hmsg.sendConfig.view.message.title.edit').d('消息发送配置 - 编辑');
    const title = targetItem.tempServerId
      ? intl.get('hmsg.sendConfig.view.message.drawer.editTemplate').d('编辑模版')
      : intl.get('hmsg.sendConfig.view.message.drawer.addTemplate').d('添加模版');
    const { getFieldDecorator, getFieldValue } = form;
    const listProps = {
      path,
      messageType,
      loading: fetchHeaderLoading,
      dataSource: isCopy ? copyDetail.serverList : header.serverList,
      onEdit: this.handleEditContent,
      onDelete: this.handleDeleteContent,
      onOpen: this.openTemplateDrawer,
    };
    const drawerProps = {
      title,
      path,
      messageType,
      webHook,
      tenantId: tenantRoleLevel ? tenantId : getFieldValue('tenantId'),
      ds: this.ds,
      anchor: 'right',
      visible: drawerVisible,
      itemData: targetItem,
      fetchLoading: fetchWebHookLoading,
      deleteLoading: deleteWebHookLoading,
      onOk: this.handleSaveContent,
      onCancel: this.handleCancelOption,
      onEditOk: this.handleEditOk,
      onAdd: this.handleAddWebHook,
      onDelete: this.handleDeleteWebHook,
      onFetch: this.handleFetchWebHook,
      onRef: this.getRef,
    };
    const templateDrawerProps = {
      title: intl.get('hmsg.sendConfig.view.message.viewTemplate').d('查看模板'),
      anchor: 'right',
      content: templateData,
      loading: fetchTemplateLoading,
      visible: templateDrawerVisible,
      onClose: this.closeTemplateDrawer,
    };
    return (
      <>
        <Header
          title={
            isCopy
              ? intl.get('hmsg.sendConfig.view.message.title.add').d('消息发送配置 - 添加')
              : headerTitle
          }
          backPath="/hmsg/send-config/list"
        >
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '消息发送配置-保存',
              },
            ]}
            type="primary"
            icon="save"
            onClick={this.handleSave}
            loading={createLoading || updateLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Header>
        <Content>
          <Card
            key="send-config-header"
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hmsg.sendConfig.view.message.title').d('发送配置')}</h3>}
          >
            <Form className={EDIT_FORM_CLASSNAME}>
              <Row {...EDIT_FORM_ROW_LAYOUT}>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hmsg.sendConfig.model.sendConfig.messageCode').d('消息代码')}
                    required={isUndefined(header.messageCode)}
                  >
                    {getFieldDecorator('messageCode', {
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.sendConfig.model.sendConfig.messageCode')
                              .d('消息代码'),
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
                      initialValue: isCopy ? copyDetail.messageCode : header.messageCode,
                    })(
                      isUndefined(header.messageCode) || isCopy ? (
                        <Input trim typeCase="upper" inputChinese={false} />
                      ) : (
                        <>{header.messageCode}</>
                      )
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    label={intl.get('hmsg.sendConfig.model.sendConfig.messageName').d('消息名称')}
                    {...EDIT_FORM_ITEM_LAYOUT}
                  >
                    {getFieldDecorator('messageName', {
                      initialValue: isCopy ? copyDetail.messageName : header.messageName,
                      rules: [
                        {
                          required: true,
                          message: intl.get('hzero.common.validation.notNull', {
                            name: intl
                              .get('hmsg.sendConfig.model.sendConfig.messageName')
                              .d('消息名称'),
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
                        label={intl
                          .get('hmsg.sendConfig.model.sendConfig.messageName')
                          .d('消息名称')}
                        field="messageName"
                        token={header ? header._token : null}
                      />
                    )}
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
                {!tenantRoleLevel && (
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      {...EDIT_FORM_ITEM_LAYOUT}
                      label={intl.get('entity.tenant.tag').d('租户')}
                      required={isUndefined(header.tenantId)}
                    >
                      {getFieldDecorator('tenantId', {
                        initialValue: header.tenantId,
                        rules: [
                          {
                            required: true,
                            message: intl.get('hzero.common.validation.notNull', {
                              name: intl.get('entity.tenant.tag').d('租户'),
                            }),
                          },
                        ],
                      })(
                        isUndefined(header.tenantId) ? (
                          <Lov
                            code="HPFM.TENANT"
                            textValue={header.tenantName}
                            onChange={this.changeTenant}
                          />
                        ) : (
                          <>{header.tenantName}</>
                        )
                      )}
                    </Form.Item>
                  </Col>
                )}
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl.get('hmsg.sendConfig.model.sendConfig.messageType').d('消息类型')}
                  >
                    {getFieldDecorator('categoryCode', {
                      initialValue: [header.categoryCode, header.subcategoryCode],
                    })(
                      <Cascader
                        fieldNames={{ label: 'meaning', value: 'value', children: 'children' }}
                        options={categoryTree}
                        expandTrigger="hover"
                        placeholder=""
                        onChange={this.handleCategoryCodeChange}
                      />
                    )}
                  </Form.Item>
                </Col>
                <Col {...FORM_COL_3_LAYOUT}>
                  <Form.Item
                    {...EDIT_FORM_ITEM_LAYOUT}
                    label={intl
                      .get('hmsg.sendConfig.model.sendConfig.receiveConfigFlag')
                      .d('是否配置接收')}
                  >
                    {getFieldDecorator('receiveConfigFlag', {
                      initialValue: isUndefined(match.params.id) ? 0 : header.receiveConfigFlag,
                    })(<Switch />)}
                  </Form.Item>
                </Col>
              </Row>
              {/* {isTenantRoleLevel() && (
                <Row {...EDIT_FORM_ROW_LAYOUT}>
                  <Col {...FORM_COL_3_LAYOUT}>
                    <Form.Item
                      label={intl
                        .get('hmsg.sendConfig.model.sendConfig.receiveCode')
                        .d('接收配置编码')}
                      {...EDIT_FORM_ITEM_LAYOUT}
                    >
                      {getFieldDecorator('receiveCode', {
                        initialValue: header.receiveCode,
                      })(
                        <Lov
                          code="HMSG.RECEIVE_CODE"
                          lovOptions={{ displayField: 'receiveCode', valueField: 'receiveCode' }}
                          textValue={header.receiveCode}
                        />
                      )}
                    </Form.Item>
                  </Col>
                </Row>
              )} */}
              {getFieldDecorator('tenantName', {
                initialValue: header.tenantName,
              })(<span />)}
              {getFieldDecorator('templateCode', {
                initialValue: header.templateCode,
              })(<span />)}
            </Form>
          </Card>
          <Card
            key="send-config-line"
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{intl.get('hmsg.sendConfig.view.title.template').d('模板')}</h3>}
          >
            <div className="table-list-operator">
              <ButtonPermission
                permissionList={[
                  {
                    code: `${path}.button.addTemplate`,
                    type: 'button',
                    meaning: '消息发送配置-添加模板',
                  },
                ]}
                type="primary"
                onClick={this.handleAddLine}
                icon="plus"
              >
                {intl.get('hmsg.sendConfig.view.button.addTemplate').d('添加模板')}
              </ButtonPermission>
            </div>
            <ListTable {...listProps} />
          </Card>
          {drawerVisible && <Drawer {...drawerProps} />}
          <TemplateDrawer {...templateDrawerProps} />
        </Content>
      </>
    );
  }
}
