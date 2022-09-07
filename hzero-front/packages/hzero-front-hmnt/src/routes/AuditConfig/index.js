/**
 * audit-config 操作审计配置
 * @date: 2019-7-18
 * @author: xl <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import { connect } from 'dva';
import { Button, Form, Popconfirm, Table } from 'hzero-ui';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import notification from 'utils/notification';
import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';

import Drawer from './Drawer';
import FilterForm from './FilterForm';

@connect(({ loading, auditConfig }) => ({
  auditConfig,
  fetchAuditConfigLoading: loading.effects['auditConfig/fetchAuditConfigList'],
  saving: loading.effects['auditConfig/createAuditConfig'],
  getAuditConfigLoading: loading.effects['auditConfig/getAuditConfigDetail'],
  updateAuditConfigLoading: loading.effects['auditConfig/updateAuditConfig'],
  fetchAboutDataLoading: loading.effects['auditConfig/fetchAboutDataList'],
  tenantId: getCurrentOrganizationId(),
  isTenant: isTenantRoleLevel(),
}))
@Form.create({ fieldNameProp: null })
@formatterCollections({ code: ['hmnt.auditConfig'] })
export default class AuditConfig extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      modalVisible: false,
      fieldsValue: {},
      currentRecord: {},
    };
  }

  /**
   * 组件加载时获取操作审计配置列表
   */
  componentDidMount() {
    const { dispatch } = this.props;
    this.fetchAuditConfigList();
    dispatch({
      type: 'auditConfig/initAuditType',
    });
  }

  /**
   * 获取操作审计列表
   * @param {object} params
   */
  @Bind()
  fetchAuditConfigList(params = {}) {
    const {
      dispatch,
      auditConfig: { pagination = {} },
    } = this.props;
    const { fieldsValue } = this.state;
    dispatch({
      type: 'auditConfig/fetchAuditConfigList',
      payload: { ...fieldsValue, page: pagination, ...params },
    });
  }

  /**
   * 编辑打开模态框
   */
  @Bind()
  handleUpdateAuditConfig(record) {
    const { dispatch } = this.props;
    this.handleModalVisible(true);
    dispatch({
      type: 'auditConfig/updateState',
      payload: {
        auditConfigDetail: {},
      },
    });
    dispatch({
      type: 'auditConfig/getAuditConfigDetail',
      payload: { auditOpConfigId: record.auditOpConfigId },
    });
    this.fetchAboutDataList(record);
    this.setState({
      currentRecord: record,
    });
  }

  /**
   * 保存模态框
   */
  @Bind()
  handleSaveAuditConfig(fieldsValue) {
    const {
      dispatch,
      auditConfig: { auditConfigDetail = {} },
    } = this.props;
    dispatch({
      type: `auditConfig/${
        auditConfigDetail.auditOpConfigId !== undefined ? 'updateAuditConfig' : 'createAuditConfig'
      }`,
      payload: { ...auditConfigDetail, ...fieldsValue, permissionId: fieldsValue.permissionId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.fetchAuditConfigList();
      }
    });
  }

  /**
   * 删除
   */
  @Bind()
  handleDeleteAuditConfig(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'auditConfig/deleteAuditConfig',
      payload: { ...record, auditOpConfigId: record.auditOpConfigId },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAuditConfigList();
      }
    });
  }

  /**
   * 分页
   */
  @Bind()
  handleTableChange(pagination) {
    this.fetchAuditConfigList({
      page: pagination,
    });
  }

  /**
   * FilterForm查询
   */
  @Bind()
  handleSearch(form) {
    const fieldsValue = form.getFieldsValue();
    this.setState({ fieldsValue });
    this.fetchAuditConfigList({ ...fieldsValue, page: {} });
  }

  /**
   * 重置表单
   */
  @Bind()
  handleResetSearch(form) {
    this.setState({ fieldsValue: {} });
    form.resetFields();
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
  }

  /**
   * 打开模态框
   */
  @Bind()
  showModal(record = {}) {
    const { dispatch } = this.props;
    if (record.auditOpConfigId !== undefined) {
      dispatch({
        type: 'auditConfig/getAuditConfigDetail',
        payload: { auditOpConfigId: record.auditOpConfigId },
      });
    }
    dispatch({
      type: 'auditConfig/updateState',
      payload: { auditConfigDetail: {} },
    });
    this.handleModalVisible(true);
  }

  /**
   * 关闭模态框
   */
  @Bind()
  hideModal() {
    this.handleModalVisible(false);
  }

  /**
   * 新建关联数据审计
   */
  @Bind()
  handleAddDataAudit(value) {
    const { currentRecord } = this.state;
    const { dispatch, isTenant, tenantId } = this.props;
    dispatch({
      type: 'auditConfig/createAboutDataAudit',
      payload: {
        tenantId: isTenant ? tenantId : currentRecord.tenantId,
        auditOpConfigId: currentRecord.auditOpConfigId,
        auditDataConfigId: value.auditDataConfigId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAboutDataList(currentRecord);
      }
    });
  }

  /**
   * 删除关联数据审计
   */
  @Bind()
  handleDeleteDataAudit(record) {
    const { currentRecord } = this.state;
    const { dispatch } = this.props;

    dispatch({
      type: 'auditConfig/deleteAboutDataAudit',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAboutDataList(currentRecord);
      }
    });
  }

  @Bind()
  fetchAboutDataList(record) {
    const { dispatch, isTenant, tenantId } = this.props;
    dispatch({
      type: 'auditConfig/fetchAboutDataList',
      payload: {
        auditOpConfigId: record.auditOpConfigId,
        tenantId: isTenant ? tenantId : record.tenantId,
      },
    });
  }

  render() {
    const {
      updateAuditConfigLoading = false,
      fetchAuditConfigLoading = false,
      getAuditConfigLoading = false,
      fetchAboutDataLoading = false,
      saving = false,
      auditConfig: {
        auditConfigList = [],
        pagination = {},
        auditConfigDetail = {},
        auditTypeList = [],
        aboutDataAuditList = [],
      },
      isTenant,
      tenantId,
    } = this.props;
    const { modalVisible, detailFirstLoadLoading } = this.state;
    const auditConfigColumns = [
      !isTenant && {
        title: intl.get('hmnt.auditConfig.model.auditConfig.tenantName').d('租户'),
        width: 200,
        dataIndex: 'tenantName',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.auditTypeMeaning').d('审计类型'),
        width: 200,
        dataIndex: 'auditTypeMeaning',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.auditType').d('审计内容'),
        width: 200,
        dataIndex: 'auditType',
        render: (_, record) => {
          if (record.auditType === 'API') {
            return record.description;
          } else if (record.auditType === 'USER') {
            return record.username;
          } else if (record.auditType === 'ROLE') {
            return record.roleName;
          } else if (record.auditType === 'CLIENT') {
            return record.clientName;
          }
        },
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.businessKey').d('业务主键'),
        width: 200,
        dataIndex: 'businessKey',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.auditContent').d('操作内容'),
        dataIndex: 'auditContent',
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.auditArgsFlag').d('记录请求参数'),
        width: 120,
        dataIndex: 'auditArgsFlag',
        render: enableRender,
      },
      {
        title: intl.get('hmnt.auditConfig.model.auditConfig.auditResultFlag').d('记录响应参数'),
        width: 120,
        dataIndex: 'auditResultFlag',
        render: enableRender,
      },
      {
        title: intl.get(`hmnt.auditConfig.model.auditConfig.auditDataFlag`).d('记录操作数据'),
        width: 120,
        dataIndex: 'auditDataFlag',
        render: enableRender,
      },
      {
        title: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        fixed: 'right',
        dataIndex: 'edit',
        render: (text, record) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <a
                  onClick={() => {
                    this.handleUpdateAuditConfig(record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </a>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'delete',
              ele: (
                <Popconfirm
                  title={intl.get('hzero.common.message.confirm.delete').d('是否删除此条记录？')}
                  onConfirm={() => {
                    this.handleDeleteAuditConfig(record);
                  }}
                >
                  <a>{intl.get('hzero.common.button.delete').d('删除')}</a>
                </Popconfirm>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 3 });
        },
      },
    ].filter(Boolean);
    const scroll = {
      x: 549,
    };
    return (
      <>
        <Header title={intl.get('hmnt.auditConfig.view.message.title').d('操作审计配置')}>
          <Button icon="plus" type="primary" onClick={this.showModal}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <FilterForm
            search={this.handleSearch}
            reset={this.handleResetSearch}
            isTenantRoleLevel={isTenant}
            organizationId={tenantId}
          />
          <Table
            bordered
            rowKey="auditOpConfigId"
            loading={fetchAuditConfigLoading}
            dataSource={auditConfigList}
            columns={auditConfigColumns}
            pagination={pagination}
            onChange={this.handleTableChange}
            scroll={scroll}
          />
          <Drawer
            title={
              auditConfigDetail.auditOpConfigId !== undefined
                ? intl.get('hmnt.auditConfig.view.message.edit').d('编辑操作审计配置')
                : intl.get('hmnt.auditConfig.view.message.create').d('新建操作审计配置')
            }
            initLoading={getAuditConfigLoading}
            loading={
              auditConfigDetail.auditOpConfigId !== undefined ? updateAuditConfigLoading : saving
            }
            organizationId={tenantId}
            modalVisible={modalVisible}
            initData={auditConfigDetail}
            onCancel={this.hideModal}
            onOk={this.handleSaveAuditConfig}
            detailFirstLoadLoading={detailFirstLoadLoading}
            isTenantRoleLevel={isTenant}
            auditTypeList={auditTypeList}
            aboutDataAuditList={aboutDataAuditList}
            fetchAboutDataLoading={fetchAboutDataLoading}
            onAddDataAudit={this.handleAddDataAudit}
            onDeleteDataAudit={this.handleDeleteDataAudit}
          />
        </Content>
      </>
    );
  }
}
