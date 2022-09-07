/**
 * ProcessDefine - 流程设置/流程定义
 * @date: 2018-8-16
 * @author: WH <heng.wei@hand-china.com>
 * @version: 1.0.0
 * @copyright Copyright (c) 2018, Hand
 */

import React, { Component } from 'react';
import { Button } from 'hzero-ui';
import { connect } from 'dva';
import { Bind } from 'lodash-decorators';
import { isUndefined, isEmpty } from 'lodash';
import { routerRedux } from 'dva/router';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { getCurrentOrganizationId, filterNullValueObject, isTenantRoleLevel } from 'utils/utils';

import FilterForm from './FilterForm';
import ListTable from './ListTable';
import Drawer from './Drawer';
import ImportModal from './ImportModal';
import DeployRecord from './DeployRecord';
import CopyValue from './CopyValue';
import OverTimeSetting from './OverTimeSetting';
/**
 * 流程定义组件
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {!Object} processDefine - 数据源
 * @reactProps {!Object} loading - 数据加载是否完成
 * @reactProps {!Object} saving - 新建是否完成
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@formatterCollections({ code: ['hwfp.processDefine', 'hwfp.common', 'hzero.common'] })
@connect(({ processDefine, loading }) => ({
  processDefine,
  isSiteFlag: !isTenantRoleLevel(),
  tenantId: getCurrentOrganizationId(),
  loading: loading.effects['processDefine/fetchProcessList'],
  saving: loading.effects['processDefine/createProcess'],
  importLoading: loading.effects['processDefine/importProcess'],
  settingLoading: loading.effects['processDefine/saveOverTimeSetting'],
  releasing: loading.effects['processDefine/releaseProcess'],
  loadingRecord: loading.effects['processDefine/fetchDeployHistory'],
  copyLoading: loading.effects['processDefine/copyValue'],
  docLoading: loading.effects['processDefine/fetchDocuments'],
}))
export default class ProcessDefine extends Component {
  form;

  constructor(props) {
    super(props);
    this.drawerRef = React.createRef();
    this.state = {
      importVisible: false,
      deployRecord: {},
      deployModalVisible: false,
      settingVisible: false,
      copyVisible: false,
      currentCopyRecord: {},
      currentSettingRecord: {},
    };
  }

  /**
   * render()调用后获取数据
   */
  componentDidMount() {
    const {
      processDefine: { pagination = {} },
      location: { state: { _back } = {} },
    } = this.props;
    // 校验是否从详情页返回
    const page = isUndefined(_back) ? {} : pagination;
    this.handleSearch(page);
    this.fetchCategory();
  }

  /**
   * 传递表单对象
   * @param {object} ref - FilterForm对象
   */
  @Bind()
  handleBindRef(ref = {}) {
    this.form = (ref.props || {}).form;
  }

  // 获取流程分类
  @Bind()
  fetchCategory() {
    const { dispatch, tenantId } = this.props;
    dispatch({
      type: 'processDefine/fetchCategory',
      payload: { tenantId },
    });
  }

  /**
   * 查询
   * @param {object} fields - 查询参数
   */
  @Bind()
  handleSearch(fields = {}) {
    const { dispatch } = this.props;
    const filterValues = isUndefined(this.form)
      ? {}
      : filterNullValueObject(this.form.getFieldsValue());
    dispatch({
      type: 'processDefine/fetchProcessList',
      payload: {
        page: isEmpty(fields) ? {} : fields,
        ...filterValues,
      },
    });
  }

  /**
   * 数据新增
   */
  @Bind()
  handleAddContent() {
    this.setState({ drawerVisible: true });
  }

  /**
   * 流程删除
   * @param {obejct} record - 操作对象
   */
  @Bind()
  handleDeleteModel(record) {
    const {
      dispatch,
      tenantId,
      processDefine: { pagination },
    } = this.props;
    dispatch({
      type: 'processDefine/deleteProcess',
      payload: {
        tenantId,
        modelId: record.id,
        record,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 部署记录查看
   * @param {object} deployRecord - 流程对象
   */
  @Bind()
  handleDeployModel(deployRecord) {
    this.setState({
      deployRecord,
      deployModalVisible: true,
    });
  }

  /**
   * 流程发布
   * @param {object} record - 流程对象
   */
  @Bind()
  handleReleaseModel(record) {
    const {
      dispatch,
      tenantId,
      processDefine: { pagination },
    } = this.props;
    dispatch({
      type: 'processDefine/releaseProcess',
      payload: {
        tenantId,
        modelId: record.id,
      },
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 滑窗保存操作
   * @param {object} values - 保存数据
   */
  @Bind()
  handleSaveContent(values) {
    const {
      dispatch,
      tenantId,
      processDefine: { pagination },
    } = this.props;
    dispatch({
      type: 'processDefine/createProcess',
      payload: {
        tenantId,
        process: { tenantId, ...values },
      },
    }).then((res) => {
      if (res) {
        this.setState({ drawerVisible: false });
        notification.success();
        this.handleSearch(pagination);
      }
    });
  }

  /**
   * 滑窗取消操作
   */
  @Bind()
  handleCancelOption() {
    this.setState({ drawerVisible: false });
  }

  /**
   * render
   * @returns React.element
   */
  @Bind()
  handleCheckUnique(values) {
    const { dispatch, tenantId } = this.props;
    return dispatch({
      type: 'processDefine/checkUnique',
      payload: {
        tenantId,
        values,
      },
    });
  }

  @Bind()
  handleImport() {
    this.setState({ importVisible: true });
  }

  @Bind()
  handleImportCancel() {
    this.setState({ importVisible: false });
  }

  @Bind()
  handleImportOk(params, func = (e) => e) {
    const {
      dispatch,
      processDefine: { pagination },
    } = this.props;
    dispatch({
      type: 'processDefine/importProcess',
      payload: params,
    }).then((res) => {
      if (res) {
        notification.success();
        this.handleImportCancel();
        this.handleSearch(pagination);
        func();
      }
    });
  }

  /**
   * 流程分类改变时查询关联单据
   * @param {*} value
   */
  @Bind()
  handleFetchDocuments(value) {
    const { dispatch } = this.props;
    dispatch({
      type: 'processDefine/fetchDocuments',
      payload: value,
    });
  }

  /**
   * 取消发布记录弹窗
   */
  @Bind()
  handleCancelRecord() {
    this.setState({
      deployModalVisible: false,
      deployRecord: {},
    });
  }

  @Bind()
  handleFetchRecord(modelKey) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'processDefine/fetchDeployHistory',
      payload: { modelKey },
    });
  }

  @Bind()
  handleCopyProcess(record) {
    this.setState({ copyVisible: true, currentCopyRecord: record });
  }

  @Bind()
  handleOverTimeSetting(record) {
    this.setState({ settingVisible: true, currentSettingRecord: record });
  }

  @Bind()
  handleCancleSetting() {
    this.setState({ settingVisible: false, currentSettingRecord: {} });
  }

  @Bind()
  handleCancelCopy() {
    this.setState({ copyVisible: false, currentCopyRecord: {} });
  }

  @Bind()
  handleCopyValue(data) {
    const { dispatch } = this.props;
    dispatch({
      type: 'processDefine/copyValue',
      payload: data,
    }).then((res) => {
      if (res) {
        this.handleCancelCopy();
        notification.success();
        this.handleSearch();
      }
    });
  }

  @Bind()
  handleMaintainProcess(processId = '') {
    this.props.dispatch(
      routerRedux.push({
        pathname: `/hwfp/process-define/detail/${processId}`,
      })
    );
  }

  @Bind()
  handleSaveSetting(params) {
    const { dispatch } = this.props;
    dispatch({
      type: 'processDefine/saveOverTimeSetting',
      params,
    }).then((res) => {
      if (res) {
        this.handleCancleSetting();
        notification.success();
        this.handleSearch();
      }
    });
  }

  render() {
    const {
      loading,
      saving,
      docLoading,
      copyLoading,
      releasing,
      loadingRecord,
      isSiteFlag,
      tenantId,
      importLoading = false,
      settingLoading = false,
      processDefine: { category = [], documents = [], list = [], pagination = {} },
    } = this.props;
    const {
      importVisible = false,
      drawerVisible = false,
      settingVisible = false,
      deployModalVisible,
      deployRecord,
      copyVisible,
      currentCopyRecord,
      currentSettingRecord,
    } = this.state;
    const filterProps = {
      category,
      isSiteFlag,
      onSearch: this.handleSearch,
      onRef: this.handleBindRef,
    };
    const listProps = {
      isSiteFlag,
      category,
      pagination,
      releasing,
      dataSource: list,
      processing: {
        list: loading,
      },
      currentTenantId: tenantId,
      onEdit: this.handleEditModel,
      onDelete: this.handleDeleteModel,
      onDeploy: this.handleDeployModel,
      onRelease: this.handleReleaseModel,
      onChange: this.handleSearch,
      onSettingProcess: this.handleOverTimeSetting,
      onCopyProcess: this.handleCopyProcess,
      onMaintainProcess: this.handleMaintainProcess,
    };
    const drawerProps = {
      category,
      documents,
      saving,
      isSiteFlag,
      tenantId,
      ref: this.drawerRef,
      anchor: 'right',
      title: intl.get('hwfp.processDefine.view.option.create').d('新建流程'),
      visible: drawerVisible,
      onOk: this.handleSaveContent,
      onCancel: this.handleCancelOption,
      onCheck: this.handleCheckUnique,
      onFetchDocuments: this.handleFetchDocuments,
    };

    const importModalProps = {
      category,
      documents,
      isSiteFlag,
      tenantId,
      importVisible,
      importLoading,
      onOk: this.handleImportOk,
      onCancel: this.handleImportCancel,
      onFetchDocuments: this.handleFetchDocuments,
    };

    const deployRecordProps = {
      title: intl.get('hwfp.common.view.message.title.deployRecord').d('发布记录'),
      loading: loadingRecord,
      visible: deployModalVisible,
      record: deployRecord,
      onCancel: this.handleCancelRecord,
      onFetchRecord: this.handleFetchRecord,
    };

    const copyValueProps = {
      category,
      documents,
      isSiteFlag,
      tenantId,
      currentCopyRecord,
      visible: copyVisible,
      dataLoading: docLoading,
      loading: copyLoading,
      onOk: this.handleCopyValue,
      onCancel: this.handleCancelCopy,
      onFetchDocuments: this.handleFetchDocuments,
    };

    const settingProps = {
      visible: settingVisible,
      currentRecord: currentSettingRecord,
      dataLoading: settingLoading,
      onOk: this.handleSaveSetting,
      onCancel: this.handleCancleSetting,
    };

    return (
      <>
        <Header title={intl.get('hwfp.common.model.process.define').d('流程定义')}>
          <Button icon="plus" type="primary" onClick={this.handleAddContent}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
          <Button icon="to-top" onClick={this.handleImport}>
            {intl.get('hzero.common.button.import').d('导入')}
          </Button>
        </Header>
        <Content>
          <FilterForm {...filterProps} />
          <ListTable {...listProps} />
          <Drawer {...drawerProps} />
          <ImportModal {...importModalProps} />
          <DeployRecord {...deployRecordProps} />
          {copyVisible && <CopyValue {...copyValueProps} />}
          {settingVisible && <OverTimeSetting {...settingProps} />}
        </Content>
      </>
    );
  }
}
