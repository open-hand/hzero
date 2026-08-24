/**
 * 二级域名模板分配--模板配置
 * @date: 2019-7-11
 * @author: XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Alert, Form, Icon, Modal, Spin, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

import TemplateConfigDrawer from './TemplateConfigDrawer';
import MessageDrawer from './MessageDrawer';

@connect(({ loading, ssoConfig }) => ({
  ssoConfig,
  getTemplateConfigListLoading: loading.effects['ssoConfig/getTemplateConfigList'],
  saving: loading.effects['ssoConfig/createTemplateConfigs'],
  getTemplateConfigsDetailLoading: loading.effects['ssoConfig/getTemplateConfigsDetail'],
  updateTemplateConfigsLoading: loading.effects['ssoConfig/updateTemplateConfigs'],
  messageDetailLoading: loading.effects['ssoConfig/fetchMessageDetail'],
  isTenant: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({
  code: ['hiam.ssoConfig'],
})
export default class TemplateConfig extends Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      fieldsValue: {},
      selectedRows: [],
      fileList: [], // 文件上传列表
      messageDrawerVisible: false,
    };
  }

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({ type: 'ssoConfig/initConfigType' });
    this.fetchTemplateConfigsList();
  }

  /**
   * 获取模板配置列表
   * @param {object} params
   * @memberof  templateConfigs
   */
  @Bind()
  fetchTemplateConfigsList(params = {}) {
    const {
      dispatch,
      match: {
        params: { templateAssignId, tenantId },
      },
      ssoConfig: { templateConfigPagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'ssoConfig/getTemplateConfigList',
      payload: {
        ...fieldsValue,
        page: templateConfigPagination,
        ...params,
        templateAssignId,
        tenantId,
      },
    });
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdateTemplateConfigs(record) {
    const { dispatch } = this.props;
    this.handleModalVisible(true);
    dispatch({
      type: 'ssoConfig/getTemplateConfigsDetail',
      payload: { configId: record.configId },
    }).then(res => {
      if (res) {
        this.setState({
          fileList: [
            {
              uid: '-1',
              name: res.fileName,
              status: 'done',
              url: res.configValue,
            },
          ],
        });
      }
    });
  }

  /**
   * 保存模板配置
   */
  @Bind()
  handleSaveTemplateConfigs(fieldsValue) {
    const {
      dispatch,
      match: {
        params: { templateAssignId, tenantId },
      },
      ssoConfig: { templateConfigsDetail = {} },
    } = this.props;
    if (fieldsValue.configTypeCode === 'TEXT') {
      this.getEditData().then(data => {
        if (data.text !== undefined) {
          dispatch({
            type: `ssoConfig/${
              templateConfigsDetail.configId !== undefined
                ? 'updateTemplateConfigs'
                : 'createTemplateConfigs'
            }`,
            payload: {
              ...templateConfigsDetail,
              ...fieldsValue,
              configValue:
                fieldsValue.configTypeCode === 'TEXT' ? data.text : fieldsValue.configValue,
              templateAssignId,
              tenantId,
            },
          }).then(res => {
            if (res) {
              notification.success();
              this.hideModal();
              this.fetchTemplateConfigsList();
            }
          });
        } else {
          notification.warning({
            message: intl.get('hzero.common.message.confirm.ckEditor').d('请输入富文本内容'),
          });
        }
      });
    } else {
      dispatch({
        type: `ssoConfig/${
          templateConfigsDetail.configId !== undefined
            ? 'updateTemplateConfigs'
            : 'createTemplateConfigs'
        }`,
        payload: {
          ...templateConfigsDetail,
          ...fieldsValue,
          configValue: fieldsValue.configValue,
          templateAssignId,
          tenantId,
        },
      }).then(res => {
        if (res) {
          notification.success();
          this.hideModal();
          this.fetchTemplateConfigsList();
        }
      });
    }
  }

  /**
   * 删除
   */
  @Bind()
  handleDeleteTemplateConfigs(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'ssoConfig/deleteTemplateConfigs',
      payload: { ...record, configId: record.configId },
    }).then(res => {
      if (res) {
        notification.success();
        this.fetchTemplateConfigsList();
      }
    });
  }

  /**
   * 分页
   */
  @Bind()
  handleTableChange(pagination) {
    this.fetchTemplateConfigsList({
      page: pagination,
    });
  }

  /**
   * 勾选回调
   * @param {*} _
   * @param {*} selectedRows
   */
  @Bind()
  handleSelectRows(_, selectedRows) {
    this.setState({
      selectedRows,
    });
  }

  // 批量删除模板配置
  @Bind()
  handleRemoveBtnClick(params = {}) {
    const {
      dispatch,
      match: {
        params: { templateAssignId, tenantId },
      },
    } = this.props;
    const { selectedRows, fieldsValue } = this.state;
    const onOk = () => {
      dispatch({
        type: 'ssoConfig/deleteTemplateConfigs',
        payload: selectedRows,
      }).then(res => {
        if (res) {
          notification.success();
          this.setState({ selectedRows: [] });
          dispatch({
            type: 'ssoConfig/getTemplateConfigList',
            payload: { ...fieldsValue, ...params, templateAssignId, tenantId },
          });
        }
      });
    };
    const onCancel = () => {
      this.setState({ selectedRows: [] });
    };
    Modal.confirm({
      title: intl.get('hzero.common.message.confirm.remove').d('确定删除选中数据？'),
      onOk,
      onCancel,
    });
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  @Bind()
  handleStaticTextEditorRef(staticTextEditorRef) {
    this.staticTextEditorRef = staticTextEditorRef;
  }

  @Bind()
  getEditData() {
    if (!this.staticTextEditorRef) {
      return Promise.reject();
    }
    return new Promise((resolve, reject) => {
      const { form } = this.props;
      form.validateFields((err, fieldsValue) => {
        if (err) {
          reject();
        } else {
          const { editor } = (this.staticTextEditorRef.staticTextEditor || {}).current;
          if (!editor || !editor.getData()) {
            return notification.warning({
              message: intl
                .get('hiam.staticText.view.message.alert.contentRequired')
                .d('请输入静态文本内容'),
            });
          }
          resolve({
            ...fieldsValue,
            text: editor.getData(),
          });
        }
      });
    });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    if (record.configId !== undefined) {
      dispatch({
        type: 'ssoConfig/getTemplateConfigsDetail',
        payload: { configId: record.configId },
      });
    }
    dispatch({
      type: 'ssoConfig/updateState',
      payload: { templateConfigsDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
    this.setState({
      fileList: [],
    });
  }

  @Bind()
  handleHideModal() {
    this.setState({
      messageDrawerVisible: false,
    });
  }

  @Bind()
  handleShowMessage() {
    const { dispatch } = this.props;
    this.setState({
      messageDrawerVisible: true,
    });
    dispatch({
      type: 'ssoConfig/fetchMessageDetail',
      payload: {
        textCode: 'HPFM.TEMPLATE_CONFIG',
      },
    });
  }

  render() {
    const {
      match: {
        path,
        params: { domainId, templateName, tenantId },
      },
      location: { search },
      getTemplateConfigListLoading,
      updateTemplateConfigsLoading,
      messageDetailLoading,
      saving,
      getTemplateConfigsDetailLoading = false,
      ssoConfig: {
        templateConfigList = [],
        templateConfigPagination = {},
        templateConfigsTypeList = [],
        templateConfigsDetail = {},
        messageDetail = {},
      },
    } = this.props;
    const {
      modalVisible,
      detailFirstLoadLoading,
      selectedRows,
      fileList,
      messageDrawerVisible,
    } = this.state;
    const { access_token: accessToken } = queryString.parse(search.substring(1));
    const rowSelection = {
      selectedRowKeys: selectedRows.map(n => n.configId),
      onChange: this.handleSelectRows,
    };
    const templateConfigsColumns = [
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.configCode').d('配置编码'),
        width: 100,
        dataIndex: 'configCode',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.configTypeCodeMeaning').d('配置类型'),
        dataIndex: 'configTypeCodeMeaning',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.remark').d('备注信息'),
        dataIndex: 'remark',
      },
      {
        title: intl.get('hiam.ssoConfig.model.ssoConfig.orderSeq').d('排序号'),
        dataIndex: 'orderSeq',
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '模板分配配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    this.handleUpdateTemplateConfigs(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
          ];
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
    const backPath = `/hiam/domain-config/template/${domainId}/${tenantId}`;
    return (
      <>
        <Header
          title={intl.get('hiam.message.view.message.title').d('模板分配配置')}
          backPath={
            path.indexOf('/private') === 0
              ? `/private${backPath}?access_token=${accessToken}`
              : `${backPath}`
          }
        >
          <Icon
            type="question-circle"
            onClick={this.handleShowMessage}
            style={{ marginRight: '20px', fontSize: '20px' }}
          />
        </Header>
        <Content>
          <Alert
            style={{ marginBottom: 16 }}
            message={`${intl
              .get('hiam.ssoConfig.model.sscConfig.templateName')
              .d('模板名称')}:${templateName || ''}`}
            type="info"
          />
          <div className="table-list-operator" style={{ textAlign: 'right' }}>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '模板分配配置-删除',
                },
              ]}
              key="remove"
              disabled={selectedRows.length === 0}
              onClick={this.handleRemoveBtnClick}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.add`,
                  type: 'button',
                  meaning: '模板分配配置-新增',
                },
              ]}
              type="primary"
              key="add"
              onClick={this.showModal}
            >
              {intl.get('hzero.common.button.add').d('新增')}
            </ButtonPermission>
          </div>
          <Spin spinning={getTemplateConfigListLoading}>
            <Table
              bordered
              rowKey="configId"
              dataSource={templateConfigList}
              columns={templateConfigsColumns}
              pagination={templateConfigPagination}
              onChange={this.handleTableChange}
              scroll={scroll}
              rowSelection={rowSelection}
            />
          </Spin>
          <TemplateConfigDrawer
            title={
              templateConfigsDetail.configId !== undefined
                ? intl.get('hiam.message.view.message.edit').d('编辑模板配置')
                : intl.get('hiam.message.view.message.create').d('新建模板配置')
            }
            initLoading={getTemplateConfigsDetailLoading}
            loading={saving || updateTemplateConfigsLoading}
            modalVisible={modalVisible}
            initData={templateConfigsDetail}
            templateConfigsTypeList={templateConfigsTypeList}
            onCancel={this.hideModal}
            onOk={this.handleSaveTemplateConfigs}
            detailFirstLoadLoading={detailFirstLoadLoading}
            fileList={fileList}
            onRef={this.handleStaticTextEditorRef}
          />
          <MessageDrawer
            messageVisible={messageDrawerVisible}
            onOk={this.handleHideModal}
            dataSource={messageDetail}
            messageDetailLoading={messageDetailLoading}
          />
        </Content>
      </>
    );
  }
}
