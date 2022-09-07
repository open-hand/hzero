/**
 * DataAudit - 数据变更审计配置
 * @date: 2019-7-9
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { connect } from 'dva';
import { Button } from 'hzero-ui';
import querystring from 'querystring';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty, isNumber, isNull } from 'lodash';
import { routerRedux } from 'dva/router';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import {
  filterNullValueObject,
  getEditTableData,
  isTenantRoleLevel,
  getCurrentOrganizationId,
  getCurrentTenant,
} from 'utils/utils';
import intl from 'utils/intl';
import notification from 'utils/notification';
import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';

const isTenant = isTenantRoleLevel();
const currentOrganizationId = getCurrentOrganizationId();

/**
 * 数据变更审计配置
 * @extends {Component} - Component
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} dataAuditConfig - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ dataAuditConfig, loading }) => ({
  dataAuditConfig,
  loading:
    loading.effects['dataAuditConfig/fetchConfigList'] ||
    loading.effects['dataAuditConfig/updateConfigList'],
  updateLoading: loading.effects['dataAuditConfig/updateConfigList'],
  fetchAboutConfigListLoading: loading.effects['dataAuditConfig/fetchAboutConfigList'],
}))
@formatterCollections({ code: ['hmnt.dataAuditConfig'] })
export default class DataAuditConfig extends Component {
  form;

  state = {
    isHandling: false,
    modalVisible: false,
    currentRecord: {}, // 当前编辑的数据
  };

  /**
   * 初始查询列表数据及值集
   */
  componentDidMount() {
    const {
      dataAuditConfig: { configPage = {} },
      location: { state: { _back } = {} },
    } = this.props;
    const page = isUndefined(_back) ? {} : configPage;
    this.handleSearch(page);
  }

  /**
   * 查询
   * @param {Object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const {
      dispatch,
      dataAuditConfig: { currentTenant },
    } = this.props;
    let fieldValues = {};
    if (!isUndefined(this.form)) {
      const values = this.form.getFieldsValue();
      fieldValues = filterNullValueObject(values);
      if (!isTenant) {
        const { tenantName, tenantId } = getCurrentTenant();
        if (fieldValues.tenantId === tenantId) {
          fieldValues.tenantName = tenantName;
        }
        fieldValues.tenantName = fieldValues.tenantName || currentTenant.tenantName;
        const currentTenantId = isNumber(values.tenantId) ? values.tenantId : tenantId;
        this.handleSetCurrentTenant(currentTenantId, fieldValues.tenantName);
      }
    }
    dispatch({
      type: 'dataAuditConfig/fetchConfigList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...fieldValues,
      },
    });
  }

  /**
   * 设置当前租户
   * @param {number} currentTenantId - 选中的租户id
   * @param {string} currentTenantName - 选中的租户名称
   */
  @Bind()
  handleSetCurrentTenant(currentTenantId, currentTenantName) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataAuditConfig/updateState',
      payload: {
        currentTenant: {
          tenantName: currentTenantName,
          tenantId: currentTenantId,
        },
      },
    });
  }

  /**
   * 审计
   * @param {number} auditDataConfigId - 实体ID
   */
  @Bind()
  goDetail(auditDataConfigId) {
    const {
      dispatch,
      dataAuditConfig: {
        currentTenant: { tenantId },
      },
    } = this.props;
    const params = { id: auditDataConfigId };
    if (!isTenant) {
      params.tenantId = isNull(tenantId) ? getCurrentTenant().tenantId : tenantId;
    }
    dispatch(
      routerRedux.push({
        pathname: `/hmnt/data-audit-config/detail`,
        search: querystring.stringify(params),
      })
    );
  }

  /**
   * 编辑配置列表
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEdit(record) {
    this.handleModalVisible(true);
    const { dispatch } = this.props;
    this.handleModalVisible(true);
    dispatch({
      type: 'dataAuditConfig/updateState',
      payload: {
        dataAuditDetail: record,
      },
    });
    this.fetchAboutConfigList(record);
    this.setState({
      currentRecord: record,
    });
    // const {
    //   dispatch,
    //   dataAuditConfig: { configList },
    // } = this.props;
    // const newList = configList.map(item =>
    //   item.entityTableId === record.entityTableId
    //     ? { ...item, _status: flag ? 'update' : '' }
    //     : item
    // );
    // dispatch({
    //   type: 'dataAuditConfig/updateState',
    //   payload: {
    //     configList: [...newList],
    //   },
    // });
  }

  /**
   * 启停用审计
   * @param {Object} record - 表格行数据
   */
  @Bind()
  handleEnable(record) {
    const {
      dispatch,
      dataAuditConfig: {
        configPage = {},
        currentTenant: { tenantId },
      },
    } = this.props;
    const payload = {
      ...record,
      auditFlag: record.auditFlag === 0 ? 1 : 0,
    };
    if (!isTenant) {
      payload.tenantId = tenantId;
    }
    this.setState({ isHandling: true }, () => {
      dispatch({
        type: 'dataAuditConfig/enableConfigList',
        payload,
      }).then((res) => {
        this.setState({ isHandling: false });
        if (res) {
          notification.success();
          this.handleSearch(configPage);
        }
      });
    });
  }

  /**
   * 保存配置列表
   */
  @Bind()
  handleSaveConfigList() {
    const {
      dispatch,
      dataAuditConfig: { configList },
      updateLoading,
      loading,
    } = this.props;
    if (loading || updateLoading) return;
    const nextConfigList = getEditTableData(configList, ['_status']);
    if (Array.isArray(nextConfigList) && nextConfigList.length !== 0) {
      dispatch({
        type: 'dataAuditConfig/updateConfigList',
        payload: nextConfigList,
      }).then((res) => {
        if (res) {
          notification.success();
          this.handleSearch();
        }
      });
    }
  }

  /**
   * 设置Form
   * @param {object} ref - FilterForm组件引用
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  /**
   * 控制modal显示与隐藏
   * @param {boolean}} flag 是否显示modal
   */
  handleModalVisible(flag) {
    this.setState({ modalVisible: !!flag });
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
  handleAddConfigAudit(value) {
    const { currentRecord } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'dataAuditConfig/createAboutConfigAudit',
      payload: {
        tenantId: isTenant ? currentOrganizationId : currentRecord.tenantId,
        auditOpConfigId: value.auditOpConfigId,
        auditDataConfigId: currentRecord.auditDataConfigId,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAboutConfigList(currentRecord);
      }
    });
  }

  /**
   * 删除关联数据审计
   */
  @Bind()
  handleDeleteConfigAudit(record) {
    const { currentRecord } = this.state;
    const { dispatch } = this.props;
    dispatch({
      type: 'dataAuditConfig/deleteAboutConfigAudit',
      payload: record,
    }).then((res) => {
      if (res) {
        notification.success();
        this.fetchAboutConfigList(currentRecord);
      }
    });
  }

  @Bind()
  fetchAboutConfigList(record) {
    const { dispatch } = this.props;
    dispatch({
      type: 'dataAuditConfig/fetchAboutConfigList',
      payload: {
        auditDataConfigId: record.auditDataConfigId,
        tenantId: isTenant ? currentOrganizationId : record.tenantId,
      },
    });
  }

  // 模态框 保存
  @Bind()
  handleSave(fieldsValue) {
    const {
      dispatch,
      dataAuditConfig: { configPage = {}, dataAuditDetail = {} },
    } = this.props;
    dispatch({
      type: 'dataAuditConfig/updateDataAuditConfig',
      payload: { ...dataAuditDetail, ...fieldsValue },
    }).then((res) => {
      if (res) {
        notification.success();
        this.hideModal();
        this.handleSearch(configPage);
      }
    });
  }

  render() {
    const {
      dataAuditConfig: {
        configList = [],
        configPage = {},
        currentTenant = {},
        dataAuditDetail = {},
        aboutConfigAuditList = [],
      },
      loading,
      updateLoading = false,
      fetchAboutConfigListLoading = false,
    } = this.props;
    const { isHandling, modalVisible } = this.state;
    const filterProps = {
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
      currentTenant,
    };
    const listProps = {
      configList,
      configPage,
      loading,
      isHandling,
      onChange: this.handleSearch,
      onAudit: this.goDetail,
      onEdit: this.handleEdit,
      onEnable: this.handleEnable,
    };
    return (
      <>
        <Header title={intl.get('hmnt.dataAuditConfig.view.message.title').d('数据审计配置')}>
          <Button
            icon="save"
            type="primary"
            onClick={this.handleSaveConfigList}
            loading={updateLoading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <Drawer
            title={intl.get('hmnt.dataAuditConfig.view.message.edit').d('编辑数据审计配置')}
            loading={fetchAboutConfigListLoading}
            modalVisible={modalVisible}
            initData={dataAuditDetail}
            onCancel={this.hideModal}
            onOk={this.handleSave}
            isTenantRoleLevel={isTenant}
            onDeleteConfigAudit={this.handleDeleteConfigAudit}
            onAddConfigAudit={this.handleAddConfigAudit}
            aboutConfigAuditList={aboutConfigAuditList}
          />
        </Content>
      </>
    );
  }
}
