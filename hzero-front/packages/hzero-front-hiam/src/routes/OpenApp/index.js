/**
 * OpenApp - 三方应用管理
 * @date: 2018-10-8
 * @author: wangjiacheng <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Input, Table, Icon, Popconfirm, Row, Col, Select } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import {
  SEARCH_COL_CLASSNAME,
  SEARCH_FORM_ROW_LAYOUT,
  FORM_COL_4_LAYOUT,
  SEARCH_FORM_ITEM_LAYOUT,
} from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import notification from 'utils/notification';
import { tableScrollWidth } from 'utils/utils';

import Drawer from './Drawer';

const FormItem = Form.Item;
@Form.create({ fieldNameProp: null })
/**
 * 三方应用管理
 * @extends {Component} - PureComponent
 * @reactProps {Object} openApp - 数据源
 * @reactProps {Object} loading - 数据加载状态
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ loading, openApp }) => ({
  openApp,
  init: loading.effects['openApp/fetchOpenAppList'],
  fetchDetailLoading: loading.effects['openApp/fetchOpenAppDetail'],
  deleteLoading: loading.effects['openApp/deleteOpenApp'],
  saveDetailLoading: loading.effects['openApp/updateOpenApp'],
  enableLoading: loading.effects['openApp/enabledOpenApp'],
  disableLoading: loading.effects['openApp/disabledOpenApp'],
}))
@formatterCollections({ code: ['hiam.openApp'] })
export default class OpenApp extends React.PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false, // 模态框可见度
      actionOpenAppId: '',
      fileList: [], // 文件上传列表
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'openApp/init',
    });
    this.fetchOpenAppList();
  }

  /**
   * 获取三方应用管理列表信息
   * @param {*} params
   */
  fetchOpenAppList(params = {}) {
    const {
      dispatch,
      form,
      openApp: { pagination = {} },
    } = this.props;
    dispatch({
      type: 'openApp/fetchOpenAppList',
      payload: { page: pagination, ...form.getFieldsValue(), ...params },
    });
  }

  /**
   * 打开模态框，详情数据恢复初始状态
   */
  @Bind()
  handleCreate() {
    const { dispatch } = this.props;
    dispatch({
      type: 'openApp/updateState',
      payload: { openAppDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * @function handleModalVisible - 控制实例modal显示与隐藏
   * @param {boolean} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag, fileList: [] });
  }

  /**
   * @function handleModalSave - 保存
   * @param {object} data - 数据
   */
  @Bind()
  handleModalSave(data) {
    const {
      dispatch,
      openApp: { openAppDetail },
    } = this.props;
    const type = openAppDetail.openAppId ? 'openApp/updateOpenApp' : 'openApp/createOpenApp';
    dispatch({
      type,
      payload: { ...openAppDetail, ...data },
    }).then(res => {
      if (res) {
        notification.success();
        this.handleModalVisible(false);
        this.fetchOpenAppList();
      }
    });
  }

  /**
   * @function handleEnable - 启用或禁用
   * @param {boolean} flag - 启用标识
   * @param {object} record - 行数据
   */
  handleEnable(flag, record) {
    const { dispatch } = this.props;
    const { openAppId } = record;
    this.setState({ actionOpenAppId: openAppId });
    const type = flag ? 'openApp/enabledOpenApp' : 'openApp/disabledOpenApp';
    dispatch({ type, payload: record }).then(res => {
      if (res) {
        notification.success();
        this.fetchOpenAppList();
      }
    });
  }

  /**
   * @function deleteOpenApp - 更新
   * @param {string} record - 行数据
   */
  @Bind()
  handleUpdateOpenApp(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'openApp/fetchOpenAppDetail',
      payload: record,
    }).then(res => {
      this.setState({
        fileList: [
          {
            uid: '-1',
            name: res.appName,
            status: 'done',
            url: res.appImage,
          },
        ],
      });
    });
    this.handleModalVisible(true);
  }

  /**
   * @function deleteOpenApp - 删除
   * @param {string} record - 行数据
   */
  deleteOpenApp(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'openApp/deleteOpenApp',
      payload: record,
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchOpenAppList();
      }
    });
  }

  /**
   * @function handlePagination - 分页操作
   * @param {Object} pagination - 分页参数
   */
  @Bind()
  handlePagination(pagination) {
    this.fetchOpenAppList({
      page: pagination,
    });
  }

  /**
   * @function handleSearch - 搜索表单
   */
  @Bind()
  handleSearch() {
    this.fetchOpenAppList({ page: {} });
  }

  /**
   * @function handleResetSearch - 重置查询表单
   */
  @Bind()
  handleResetSearch() {
    this.props.form.resetFields();
  }

  /**
   * @function renderForm - 渲染搜索表单
   */
  renderFilterForm() {
    const {
      form,
      openApp: { channelTypes },
    } = this.props;
    const { getFieldDecorator } = form;
    return (
      <Form>
        <Row gutter={24} type="flex" align="bottom" {...SEARCH_FORM_ROW_LAYOUT}>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.openApp.model.openApp.appCode').d('应用编码')}
            >
              {getFieldDecorator('appCode', {})(<Input trim inputChinese={false} />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.openApp.model.openApp.appName').d('应用名称')}
            >
              {getFieldDecorator('appName', {})(<Input />)}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT}>
            <FormItem
              {...SEARCH_FORM_ITEM_LAYOUT}
              label={intl.get('hiam.openApp.model.openApp.channel').d('登录渠道')}
            >
              {getFieldDecorator('channel', {})(
                <Select allowClear>
                  {channelTypes.map(item => (
                    <Select.Option value={item.value} key={item.value}>
                      {item.meaning}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </FormItem>
          </Col>
          <Col {...FORM_COL_4_LAYOUT} className={SEARCH_COL_CLASSNAME}>
            <FormItem>
              <Button onClick={this.handleResetSearch}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button type="primary" htmlType="submit" onClick={this.handleSearch}>
                {intl.get('hzero.common.button.search').d('查询')}
              </Button>
            </FormItem>
          </Col>
        </Row>
      </Form>
    );
  }

  /**
   * @function render - react render函数
   */
  render() {
    const {
      init,
      fetchDetailLoading,
      saveDetailLoading,
      deleteLoading,
      enableLoading,
      disableLoading,
      match: { path },
      openApp: {
        openAppList = [],
        pagination = {},
        openAppDetail = {},
        channelTypes = [],
        codeList = [],
      },
    } = this.props;
    const { modalVisible, actionOpenAppId, fileList } = this.state;
    const { openAppId } = openAppDetail;
    const drawerProps = {
      fetchDetailLoading,
      saveDetailLoading,
      modalVisible,
      openAppDetail,
      fileList,
      codeList,
      channelTypes,
      detailTitle: openAppId
        ? `${intl.get('hzero.common.button.edit').d('编辑')}${openAppDetail.appName}`
        : `${intl.get('hzero.common.button.create').d('新建')}`,
      onCancel: this.handleModalVisible.bind(this, false),
      onOk: this.handleModalSave,
    };
    const columns = [
      {
        title: intl.get('hiam.openApp.model.openApp.orderSeq').d('序号'),
        dataIndex: 'orderSeq',
        width: 70,
      },
      {
        title: intl.get('hiam.openApp.model.openApp.appleImage').d('应用图片'),
        dataIndex: 'appImage',
        width: 90,
        render: (text, record) => (
          <img
            alt={record.appName}
            src={text}
            width="24"
            height="24"
            style={{ borderRadius: '50%' }}
          />
        ),
      },
      {
        title: intl.get('hiam.openApp.model.openApp.appCode').d('应用编码'),
        width: 150,
        dataIndex: 'appCodeMeaning',
      },
      {
        title: intl.get('hiam.openApp.model.openApp.appName').d('应用名称'),
        dataIndex: 'appName',
      },
      {
        title: intl.get('hiam.openApp.model.openApp.channel').d('登录渠道'),
        width: 90,
        dataIndex: 'channelMeaning',
      },
      {
        title: 'APP ID',
        dataIndex: 'appId',
      },
      {
        title: intl.get('hiam.openApp.model.openApp.subAppId').d('子应用ID'),
        dataIndex: 'subAppId',
      },
      {
        title: intl.get('hzero.common.status').d('状态'),
        dataIndex: 'enabledFlag',
        width: 90,
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        key: 'operator',
        width: 150,
        fixed: 'right',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <a onClick={() => this.handleUpdateOpenApp(record)}>
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'xxx',
              ele:
                record.enabledFlag === 1 ? (
                  <a onClick={() => this.handleEnable(false, record)}>
                    {disableLoading && record.nodeRuleId === actionOpenAppId ? (
                      <Icon type="loading" />
                    ) : (
                      intl.get('hzero.common.button.disable').d('禁用')
                    )}
                  </a>
                ) : (
                  <a onClick={() => this.handleEnable(true, record)}>
                    {enableLoading && record.nodeRuleId === actionOpenAppId ? (
                      <Icon type="loading" />
                    ) : (
                      intl.get('hzero.common.button.enable').d('启用')
                    )}
                  </a>
                ),
              title:
                record.enabledFlag === 1
                  ? intl.get('hzero.common.button.disable').d('禁用')
                  : intl.get('hzero.common.button.enable').d('启用'),
              len: 2,
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  arrowPointAtCenter={false}
                  placement="left"
                  overlayStyle={{ zIndex: 100 }}
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.deleteOpenApp(record);
                  }}
                >
                  {deleteLoading && record.nodeRuleId === actionOpenAppId ? (
                    <Icon type="loading" />
                  ) : (
                    intl.get('hzero.common.button.delete').d('删除')
                  )}
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    return (
      <>
        <Header title={intl.get('hiam.openApp.view.message.title').d('三方应用管理')}>
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '三方应用管理-新建',
              },
            ]}
            icon="plus"
            type="primary"
            onClick={this.handleCreate}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
        </Header>
        <Content>
          <div className="table-list-search">{this.renderFilterForm()}</div>
          <Table
            bordered
            rowKey="openAppId"
            loading={init}
            dataSource={openAppList}
            columns={columns}
            scroll={{ x: tableScrollWidth(columns) }}
            pagination={pagination}
            onChange={this.handlePagination}
          />
          <Drawer {...drawerProps} />
        </Content>
      </>
    );
  }
}
