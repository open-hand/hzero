/*
 * index - 服务注册编辑页
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Card, Spin } from 'hzero-ui';
import {
  DataSet,
  Form,
  Lov,
  TextField,
  Select,
  Switch,
  Password,
  Tooltip,
  Modal,
  Button,
} from 'choerodon-ui/pro';
import { Input } from 'choerodon-ui';
import { connect } from 'dva';
import { routerRedux } from 'dva/router';
import { isEmpty, isNull, isUndefined } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';
import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import {
  createPagination,
  getCurrentOrganizationId,
  isTenantRoleLevel,
  getCurrentTenant,
  getEditTableData,
  encryptPwd,
  getResponse,
} from 'utils/utils';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import formatterCollections from 'utils/intl/formatterCollections';
import HttpConfigModal from '@/components/HttpConfigModal';
import { basicFormDS, historyDS } from '@/stores/Services/detailDS';
import { lineDS, authenticationDetailDS } from '@/stores/Services/authenticationModalDS';
import { SERVICE_CONSTANT } from '@/constants/constants';
import QuestionPopover from '@/components/QuestionPopover';
import InterfaceList from './List';
import getLang from '@/langs/serviceLang';
import {
  interfaceServerRelease,
  interfaceServerOffline,
  rollbackHistoryInterfaceServer,
  testOAuth2,
} from '@/services/servicesService';

/**
 * 服务注册
 * @extends {Component} - React.Component
 * @reactProps {Object} [location={}] - 当前路由信息
 * @reactProps {Object} [match={}] - react-router match路由信息
 * @reactProps {Object} [history={}]
 * @reactProps {Object} services - 数据源
 * @reactProps {Object} loading - 数据加载是否完成
 * @reactProps {Object} form - 表单对象
 * @reactProps {Function} [dispatch=function(e) {return e;}] - redux dispatch方法
 * @return React.element
 */
@connect(({ loading, services }) => ({
  fetchingList: loading.effects['services/queryList'],
  importLoading: loading.effects['services/importService'],
  deletingService: loading.effects['services/delete'],
  fetchingInterface: loading.effects['services/queryInterface'],
  editing: loading.effects['services/edit'],
  queryMonitorLoading: loading.effects['services/queryMonitor'],
  createMonitorLoading: loading.effects['services/createMonitor'],
  updateMonitorLoading: loading.effects['services/updateMonitor'],
  saveInterfacesLoading: loading.effects['services/saveInterfaces'],
  deleteLinesLoading: loading.effects['services/deleteLines'],
  fetchModalLoading: loading.effects['services/queryInternal'],
  saveBatchInterfacesLoading: loading.effects['services/saveBatchInterfaces'],
  fetchMappingClassLoading: loading.effects['services/queryMappingClass'],
  testMappingClassLoading: loading.effects['services/testMappingClass'],
  recognizeServiceParamLoading: loading.effects['services/recognizeServiceParam'],
  services,
  currentTenantId: getCurrentOrganizationId(),
  tenantRoleLevel: isTenantRoleLevel(),
}))
@formatterCollections({
  code: [
    'hitf.services',
    'hitf.document',
    'hitf.maintenanceConfig',
    'hitf.common',
    'hitf.dataMapping',
    'hitf.fieldMapping',
  ],
})
export default class Detail extends PureComponent {
  constructor(props) {
    super(props);
    this.state = {
      formDataSource: {}, // 表单数据
      interfaceListSelectedRows: [],
      listDataSource: [], // 接口数据列表
      listPagination: createPagination({ number: 0, size: 10, totalElements: 0 }), // 接口分页信息
      currentInterfaceType: 'EXTERNAL',
      currentServiceType: 'REST',
      namespace: '',
      isShowHttpModal: false,
      httpConfigList: [], // http弹窗列表数据
      showHttpsInfo: false,
      showSoapUserInfo: false,
      showCertificate: false,
      queryInterfaceDetailLoading: false,
      showNonDSField: true,
      isHistory: false,
      isPublished: false,
      isOffline: false,

      isHaveHistory: false,
      isNew: false,
      beingRelease: false,
      beingOffline: false,
      beingRollback: false,
      historyVersion: intl.get('hitf.services.view.message.history.version').d('历史版本'),
    };
    this.basicFormDS = new DataSet(basicFormDS());
    this.lineDS = new DataSet(lineDS());
    this.authenticationDetailDS = new DataSet(authenticationDetailDS());
    this.historyDS = new DataSet(historyDS());
  }

  componentDidMount() {
    const { dispatch, match = {} } = this.props;
    dispatch({
      type: 'services/queryIdpValue',
    });
    this.fetchStatisticsPeriodCode();
    this.fetchExceedThresholdActionCode();
    this.fetchStatisticsLevelCode();
    this.fetchHttpConfig();
    this.fetchPublicKey();
    if (match.params.id) {
      this.fetchDetail();
    } else {
      const record = this.basicFormDS.current;
      record.set('serviceType', 'REST');
      record.set('protocol', 'http://');
      record.set('serviceCategory', 'EXTERNAL');
      record.set('enabledFlag', 1);
    }
  }

  formCode = 'NONE';

  _modal;

  /**
   * 拉取认证模式数据
   */
  async fetchLine() {
    if (this.formCode) {
      this.lineDS.setQueryParameter('formCode', this.formCode);
      await this.lineDS.query();
    }
  }

  async setFormCode({ value }) {
    if (isUndefined(value) || isNull(value)) {
      return;
    }
    await (this.formCode = value);
    await this.fetchLine();
    this._modal.update({
      children: <>{this.modalForm()}</>,
    });
  }

  /**
   * 动态生成认证模式包含的表单数据
   * @param {lineForm}
   */
  dynamicAddFormItem({ lineForm, isNew }) {
    if (lineForm.length < 1) {
      return;
    }
    const { isHistory, isPublished } = this.state;
    const isDisabled = isHistory || isPublished;
    return lineForm.map((line) => {
      if (line.itemTypeCode === 'TEXT') {
        return (
          <TextField
            name={line.itemCode}
            disabled={isDisabled}
            label={
              line.itemDescription && (
                <QuestionPopover text={line.itemName} message={line.itemDescription} />
              )
            }
          />
        );
      } else if (line.itemTypeCode === 'LOV') {
        return (
          <Select
            name={line.itemCode}
            disabled={isDisabled}
            label={
              line.itemDescription && (
                <QuestionPopover text={line.itemName} message={line.itemDescription} />
              )
            }
          />
        );
      } else if (isNew) {
        return (
          <Password
            name={line.itemCode}
            disabled={isDisabled}
            label={
              line.itemDescription && (
                <QuestionPopover text={line.itemName} message={line.itemDescription} />
              )
            }
          />
        );
      } else {
        return (
          <Password
            placeholder={intl.get('hitf.services.view.placeholder.unchange').d('未更改')}
            disabled={isDisabled}
            name={line.itemCode}
            label={
              line.itemDescription && (
                <QuestionPopover text={line.itemName} message={line.itemDescription} />
              )
            }
          />
        );
      }
    });
  }

  /**
   * 认证配置模态框表单
   */
  modalForm() {
    const { interfaceServerId } = this.state.formDataSource;
    const isNew = isUndefined(interfaceServerId);
    const { isHistory, isPublished } = this.state;
    const isDisabled = isHistory || isPublished;
    const lineForm = this.lineDS.toData();
    lineForm.sort((a, b) => a.orderSeq - b.orderSeq);
    lineForm.map((line) => {
      if (line.itemTypeCode === 'LOV') {
        return this.authenticationDetailDS.addField(line.itemCode, {
          name: line.itemCode,
          type: 'string',
          label: line.itemName,
          disabled: isDisabled,
          lookupCode: line.valueSet,
          defaultValue: !isUndefined(line.defaultValue) ? line.defaultValue : '',
        });
      }
      return this.authenticationDetailDS.addField(line.itemCode, {
        name: line.itemCode,
        type: 'string',
        label: line.itemName,
        disabled: isDisabled,
        required: line.requiredFlag === 1,
        defaultValue: !isUndefined(line.defaultValue) ? line.defaultValue : '',
      });
    });
    const selectName = 'formCode';
    if (!isNew && !isUndefined(this.basicFormDS.current.data.httpAuthorization)) {
      const { httpAuthorization } = this.basicFormDS.toData()[0];
      let auth = {};
      const { authJson } = httpAuthorization;
      if (!isUndefined(authJson)) {
        auth = JSON.parse(authJson);
      }
      auth[selectName] = this.formCode;
      this.authenticationDetailDS.create(auth);
    } else {
      this.authenticationDetailDS.create({ formCode: this.formCode });
    }
    const size = this.authenticationDetailDS.toData().length - 1;
    return (
      <Form dataSet={this.authenticationDetailDS} columns={2} labelWidth={145}>
        <Select
          name="formCode"
          disabled={isDisabled}
          label={intl.get('hitf.services.view.placeholder.formCode').d('认证模式')}
          onChange={(value) => this.setFormCode({ value })}
          dataSet={this.authenticationDetailDS}
          defaultValue={this.authenticationDetailDS.toData()[size].formCode}
        />
        {this.dynamicAddFormItem({ lineForm, isNew })}
      </Form>
    );
  }

  notify() {
    setTimeout(() => {}, 800);
    return true;
  }

  handleOk({ isNew }) {
    const currentDetailDS = this.authenticationDetailDS.toData()[
      this.authenticationDetailDS.toData().length - 1
    ];
    const lineForm = this.lineDS.toData();
    lineForm.sort((a, b) => a.orderSeq - b.orderSeq);
    const newAuthenticationObj = {};
    let count = 0;
    lineForm
      .filter((line) => !isUndefined(currentDetailDS[line.itemCode]))
      .forEach((line) => {
        if (!isNew && line.itemTypeCode === 'PASSWORD' && isEmpty(currentDetailDS[line.itemCode])) {
          count++;
        } else if (line.requiredFlag === 0 && isEmpty(currentDetailDS[line.itemCode])) {
          count++;
        } else if (!isEmpty(currentDetailDS[line.itemCode])) {
          newAuthenticationObj[line.itemCode] = currentDetailDS[line.itemCode];
        }
      });
    const newAuthenticationJson = JSON.stringify(newAuthenticationObj);
    if (
      lineForm.length - count === Object.keys(newAuthenticationObj).length &&
      !isEmpty(currentDetailDS.formCode)
    ) {
      if (!isNew && !isUndefined(this.basicFormDS.current.data.httpAuthorization)) {
        this.basicFormDS.current.data.httpAuthorization.authType = currentDetailDS.formCode;
        this.basicFormDS.current.data.httpAuthorization.authJson = newAuthenticationJson;
      } else {
        this.basicFormDS.current.data.httpAuthorization = {
          authType: currentDetailDS.formCode,
          authJson: newAuthenticationJson,
        };
      }
      return new Promise((resolve, reject) => {
        setTimeout(this.notify() ? resolve : reject, 1000);
      });
    } else {
      notification.error({
        message: intl.get('hitf.services.view.message.validate').d('请先完善必输内容'),
      });
      return false;
    }
  }

  @Bind
  async handleInterfaceServerHistoryChange({ value }) {
    if (value === null) {
      return;
    }
    const { match = {}, currentTenantId } = this.props;
    this.basicFormDS.setQueryParameter('interfaceServerId', match.params.id);
    this.basicFormDS.setQueryParameter('version', value);
    this.basicFormDS.setQueryParameter('history', true);
    this.basicFormDS.setQueryParameter('organizationId', currentTenantId);
    const formatVersion = 'V'.concat(value, '.0');
    this.setState({
      historyVersion: formatVersion,
      isHistory: true,
      queryInterfaceDetailLoading: true,
    });
    await this.basicFormDS.query();
    this.setState({ queryInterfaceDetailLoading: false });
  }

  @Bind
  handleRelease() {
    const value = 'release';
    this.setState({ beingRelease: true });
    this.handleInterfaceServer({ value });
  }

  @Bind
  handleRollback() {
    this.setState({
      beingRollback: true,
      historyVersion: intl.get('hitf.services.view.message.history.version').d('历史版本'),
    });
    this.rollback(this.basicFormDS.toData()[0]);
  }

  @Bind
  handleNewest() {
    this.setState({ isHistory: false });
    this.setState({
      historyVersion: intl.get('hitf.services.view.message.history.version').d('历史版本'),
    });
    this.fetchDetail();
  }

  @Bind
  handleOffline() {
    this.setState({ beingOffline: true });
    this.offline(this.basicFormDS.toData()[0]);
  }

  /**
   * fetchStatisticsPeriodCode - 查询授权模式<HITF.GRANT_TYPE>code
   * @return {Array}
   */
  @Bind()
  fetchStatisticsPeriodCode() {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'services/queryCode', payload: { lovCode: 'HITF.STATISTICS_PERIOD' } });
  }

  /**
   * fetchStatisticsPeriodCode - 查询授权模式<HITF.GRANT_TYPE>code
   * @return {Array}
   */
  @Bind()
  fetchExceedThresholdActionCode() {
    const { dispatch = () => {} } = this.props;
    return dispatch({
      type: 'services/queryCode',
      payload: { lovCode: 'HITF.EXCEED_THRESHOLD_ACTION' },
    });
  }

  /**
   * fetchStatisticsLevelCode - 查询授权模式<HITF.GRANT_TYPE>code
   * @return {Array}
   */
  @Bind()
  fetchStatisticsLevelCode() {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'services/queryCode', payload: { lovCode: 'HITF.STATISTICS_LEVEL' } });
  }

  @Bind()
  fetchHttpConfig() {
    const { dispatch = () => {} } = this.props;
    return dispatch({
      type: 'services/queryCode',
      payload: { lovCode: 'HITF.HTTP_CONFIG_PROPERTY' },
    });
  }

  /**
   * 请求公钥
   */
  @Bind()
  fetchPublicKey() {
    const { dispatch = () => {} } = this.props;
    dispatch({
      type: 'services/getPublicKey',
    });
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  async fetchDetail(pageParams = {}) {
    let params = {};
    if (this.interfaceForm && this.interfaceForm.searchForm) {
      const {
        form: { getFieldsValue = (e) => e },
      } = this.interfaceForm.searchForm.props;
      params = getFieldsValue() || {};
    }
    const { match = {} } = this.props;
    const { page = 0, size = 10 } = pageParams;
    this.basicFormDS.queryParameter = params;
    this.basicFormDS.setQueryParameter('interfaceServerId', match.params.id);
    this.basicFormDS.setQueryParameter('page', page);
    this.basicFormDS.setQueryParameter('size', size);
    this.setState({ queryInterfaceDetailLoading: true });
    const res = await this.basicFormDS
      .query()
      .catch(() => this.setState({ queryInterfaceDetailLoading: false }));
    this.setState({ queryInterfaceDetailLoading: false });
    this.historyDS.setQueryParameter('interfaceServerId', match.params.id);
    const historyRes = await this.historyDS.query();
    if (!isEmpty(historyRes)) {
      this.setState({ isHaveHistory: true });
    }
    this.setState({ beingRelease: false, beingOffline: false, beingRollback: false });
    if (res) {
      const { pageInterfaces } = res;
      if (res.status === 'PUBLISHED') {
        this.setState({ isPublished: true, isNew: false, isOffline: false });
      } else if (res.status === 'OFFLINE') {
        this.setState({ isPublished: false, isNew: false, isOffline: true });
      } else {
        this.setState({ isPublished: false, isNew: true, isOffline: false });
      }
      this.setState({
        formDataSource: res,
        listDataSource: pageInterfaces.content || [],
        listPagination: createPagination(pageInterfaces),
        currentInterfaceType: res.serviceCategory || 'EXTERNAL',
        httpConfigList: res.httpConfigList,
        showCertificate: res.enabledCertificateFlag === 1,
        showSoapUserInfo:
          !isUndefined(res.soapWssPasswordType) && res.soapWssPasswordType !== 'NONE',
        showHttpsInfo: res.protocol === 'https://',
        showNonDSField: res.serviceCategory !== 'DS',
        currentServiceType: res.serviceType,
      });
    }
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  fetchMonitor(interfaceId) {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'services/queryMonitor', interfaceId });
  }

  /**
   * 查询内部接口
   * @param {Object} queryData
   */
  @Bind()
  fetchInternalInterface(queryData = {}) {
    const { dispatch = () => {}, match = {} } = this.props;
    return dispatch({
      type: 'services/queryInternal',
      payload: {
        ...queryData,
        editable: true,
        interfaceServerId: match.params.id,
      },
    });
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  createMonitor(interfaceId, data) {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'services/createMonitor', interfaceId, data });
  }

  /**
   * 查询接口详情
   * @param {Object} params
   */
  @Bind()
  updateMonitor(interfaceId, interfaceMonitorId, data) {
    const { dispatch = () => {} } = this.props;
    return dispatch({ type: 'services/updateMonitor', interfaceId, interfaceMonitorId, data });
  }

  @Bind()
  saveInterfaces(data, cb = () => {}) {
    const { dispatch, match = {} } = this.props;
    dispatch({ type: 'services/saveInterfaces', interfaceServerId: match.params.id, data }).then(
      (res) => {
        if (res) {
          notification.success();
          cb(res);
          this.fetchDetail();
        }
      }
    );
  }

  /**
   * 批量创建内部接口
   * @param {object} data - 选择的接口
   */
  @Bind()
  saveBatchInterfaces(data, cb = () => {}) {
    const { dispatch = () => {}, match = {} } = this.props;
    dispatch({
      type: 'services/saveBatchInterfaces',
      interfaceServerId: match.params.id,
      data,
    }).then((res) => {
      if (res) {
        notification.success();
        cb();
        this.fetchDetail();
      }
    });
  }

  /**
   * 创建服务
   * @param {Object} params
   * @param {Function} [cb=e => e]
   */
  @Bind()
  create(params) {
    const { dispatch } = this.props;
    return dispatch({ type: 'services/create', params }).then((res) => {
      if (res) {
        notification.success();
        // this.fetchDetail(res.interfaceServerId);
        dispatch(
          routerRedux.push({
            pathname: `/hitf/services/detail/${res.interfaceServerId}`,
          })
        );
      }
    });
  }

  /**
   * 修改服务
   * @param {Object} params
   * @param {Function} [cb= e => e]
   */
  @Bind()
  edit(params) {
    const { dispatch } = this.props;
    dispatch({ type: 'services/edit', params }).then((res) => {
      if (res) {
        notification.success();
        this.fetchDetail();
      }
    });
  }

  /**
   * 发布上线服务
   * @param {Object} params
   * @param {Function} [cb= e => e]
   */
  @Bind()
  release(params) {
    return new Promise((resolve, reject) => {
      interfaceServerRelease({
        ...params,
      }).then((res) => {
        this.setState({ isHistory: false, beingRelease: false });
        if (getResponse(res)) {
          this.fetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  /**
   * 下线服务
   * @param {Object} params
   * @param {Function} [cb= e => e]
   */
  @Bind()
  offline(params) {
    return new Promise((resolve, reject) => {
      interfaceServerOffline({
        ...params,
      }).then((res) => {
        this.setState({ isHistory: false, beingOffline: false });
        if (getResponse(res)) {
          this.fetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  /**
   * 版本回退
   * @param {Object} params
   * @param {Function} [cb= e => e]
   */
  @Bind()
  rollback(params) {
    return new Promise((resolve, reject) => {
      rollbackHistoryInterfaceServer({
        ...params,
      }).then((res) => {
        this.setState({ isHistory: false, beingRollback: false });
        if (getResponse(res)) {
          this.fetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  @Bind()
  cancel() {
    const { onCancel = (e) => e } = this.props;
    const { resetFields = (e) => e } = this.editorForm;
    resetFields();
    this.setState({
      formDataSource: {},
      interfaceListSelectedRows: [],
      listDataSource: [],
      listPagination: createPagination({ number: 0, size: 10, totalElements: 0 }),
    });
    onCancel();
  }

  // 处理HTTP配置
  @Bind()
  handleHttpConfigList() {
    const { httpConfigList } = this.state;
    const unChangedHttpConfigList = httpConfigList.filter((item) => !('_status' in item));
    let validateHttpConfigList = getEditTableData(httpConfigList);
    validateHttpConfigList = validateHttpConfigList.map((item) => {
      if (item._status === 'create') {
        const { _status, httpConfigId, ...others } = item;
        return others;
      } else {
        const { _status, ...others } = item;
        return others;
      }
    });
    return [...unChangedHttpConfigList, ...validateHttpConfigList];
  }

  /**
   * 注册
   */
  @Bind()
  async handleCreate() {
    const { currentTenantId, tenantRoleLevel } = this.props;
    const { formDataSource, listDataSource, namespace } = this.state;
    const { tenantNum } = getCurrentTenant();
    const validateHttpConfigList = this.handleHttpConfigList();
    const validate = await this.basicFormDS.validate();
    if (!validate) {
      notification.error({
        message: intl.get('hitf.services.view.message.validate').d('请先完善必输内容'),
      });
      return false;
    }
    const values = this.basicFormDS.current.toData();
    const tenantId = !tenantRoleLevel ? values.tenantId : currentTenantId;
    const { protocol, domainUrl, ...rest } = values;
    const nextValues = rest;
    if (values.serverDomain) {
      const lastDomainId = values.serverDomain.pop();
      nextValues.serverDomainId = lastDomainId;
    }
    if (values.serviceCategory === 'EXTERNAL' && domainUrl && protocol) {
      nextValues.domainUrl = `${protocol}${domainUrl}`;
    } else {
      nextValues.domainUrl = domainUrl;
    }
    const interfaces = listDataSource.map((item) => {
      if (item.isNew) {
        const { interfaceId, ...otherParams } = item;
        return { ...otherParams };
      } else {
        return item;
      }
    });
    const interfaceServerList = {
      authType: 'NONE',
      namespace: tenantRoleLevel ? tenantNum : namespace,
      ...formDataSource,
      ...nextValues,
      tenantId,
      interfaces,
      httpConfigList: validateHttpConfigList,
    };
    return this.create(interfaceServerList);
  }

  /**
   * 保存
   */
  @Bind()
  handleSave() {
    const value = 'save';
    this.handleInterfaceServer({ value });
  }

  @Bind()
  async handleInterfaceServer({ value }) {
    const {
      currentTenantId,
      tenantRoleLevel,
      services: { publicKey },
    } = this.props;
    const { formDataSource, listDataSource } = this.state;
    const validateHttpConfigList = this.handleHttpConfigList();
    const interfaces = listDataSource.map((item) => {
      if (item.isNew) {
        const { interfaceId, ...otherParams } = item;
        return { ...otherParams };
      } else {
        return item;
      }
    });
    const validate = await this.basicFormDS.validate();
    if (validate) {
      const values = this.basicFormDS.current.toData();
      const { pageInterfaces, ...otherFormDataSource } = formDataSource;
      const tenantId = !tenantRoleLevel ? values.tenantId : currentTenantId;
      const { protocol, domainUrl, ...rest } = values;
      const nextValues = rest;
      if (values.serviceCategory === 'EXTERNAL' && domainUrl && protocol) {
        nextValues.domainUrl = `${protocol}${domainUrl}`;
      } else {
        nextValues.domainUrl = domainUrl;
      }
      if (values.soapPassword) {
        nextValues.soapPassword = encryptPwd(values.soapPassword, publicKey);
      }
      if (otherFormDataSource.clientSecret) {
        otherFormDataSource.clientSecret = encryptPwd(otherFormDataSource.clientSecret, publicKey);
      }
      if (otherFormDataSource.authPassword) {
        otherFormDataSource.authPassword = encryptPwd(otherFormDataSource.authPassword, publicKey);
      }
      const interfaceServerList = {
        authType: 'NONE',
        ...otherFormDataSource,
        ...nextValues,
        tenantId,
        interfaces,
        httpConfigList: validateHttpConfigList,
      };
      if (value === 'save') {
        this.edit(interfaceServerList);
      } else {
        this.release(interfaceServerList);
      }
    } else {
      notification.error({
        message: intl.get('hitf.services.view.message.validate').d('请先完善必输内容'),
      });
    }
  }

  /**
   * 删除行
   * @param {Array} interfaceIds
   */
  @Bind()
  handleDeleteLines(interfaceIds) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'services/deleteLines',
      interfaceIds,
    }).then((res) => {
      if (res && !res.failed) {
        notification.success();
        this.fetchDetail();
      } else {
        notification.error({ description: res.message });
      }
    });
  }

  @Bind()
  onInterfaceListChange(params = {}) {
    const { current = 1, pageSize = 10 } = params;
    this.fetchDetail({ page: current - 1, size: pageSize });
  }

  @Bind()
  onInterfaceListRowSelectionChange(selectedRowKeys, selectedRows) {
    this.setState({
      interfaceListSelectedRows: selectedRows,
    });
  }

  @Bind()
  handleServiceTypeChange(value) {
    const { formDataSource = {} } = this.state;
    this.basicFormDS.current.init('soapNamespace', null);
    // this.basicFormDS.current.init('soapDataNode', null);
    this.setState({
      currentServiceType: value,
      formDataSource: { ...formDataSource, serviceType: value },
    });
  }

  @Bind()
  async openAuthenticationServiceModal() {
    const { interfaceServerId } = this.state.formDataSource;
    const { isHistory, isPublished } = this.state;
    const isDisabled = isHistory || isPublished;
    const isNew = isUndefined(interfaceServerId);
    if (!isNew && !isUndefined(this.basicFormDS.current.data.httpAuthorization)) {
      await (this.formCode = this.basicFormDS.current.data.httpAuthorization.authType || 'NONE');
    }
    await this.fetchLine();
    this._modal = Modal.open({
      key: Modal.key(),
      title: intl.get('hitf.services.view.button.authConfig').d('服务认证配置'),
      children: <>{this.modalForm()}</>,
      footer: (okBtn, cancelBtn) => (
        <div>
          {!isUndefined(this.formCode) && this.formCode.startsWith('OAUTH2', 0) && (
            <Button key="test" onClick={this.handleTestOAuth2Url}>
              {intl.get('hitf.services.view.button.test').d('测试')}
            </Button>
          )}
          {!isDisabled && okBtn}
          {cancelBtn}
        </div>
      ),
      onOk: () => this.handleOk({ isNew }),
      style: {
        width: 1000,
      },
    });
  }

  @Bind()
  handleChangeListTenant(record) {
    const { listDataSource } = this.state;
    const newDataSource = listDataSource.map((item) => ({ ...item, tenantId: record.tenantId }));
    this.setState({
      listDataSource: newDataSource,
      namespace: isEmpty(record) ? null : record.tenantNum,
    });
  }

  @Bind()
  handleChangeState(key, value) {
    this.setState({ [key]: value });
  }

  /**
   * 测试配置是否通过
   */
  @Bind()
  handleTestOAuth2Url() {
    const currentDetailDS = this.authenticationDetailDS.toData()[
      this.authenticationDetailDS.toData().length - 1
    ];
    const lineForm = this.lineDS.toData();
    lineForm.sort((a, b) => a.orderSeq - b.orderSeq);
    const newAuthenticationObj = {};
    lineForm
      .filter((line) => !isUndefined(currentDetailDS[line.itemCode]))
      .forEach((line) => {
        newAuthenticationObj[line.itemCode] = currentDetailDS[line.itemCode];
      });
    const { httpAuthorization = {} } = this.basicFormDS.toData()[0];
    httpAuthorization.authType = this.formCode;
    httpAuthorization.authJson = JSON.stringify(newAuthenticationObj);

    return new Promise((resolve, reject) => {
      testOAuth2(httpAuthorization).then((res) => {
        if (res) {
          if (res.failed) {
            setTimeout(() => {
              reject(
                notification.error({
                  message: res.message,
                })
              );
            }, 1000);
          } else {
            setTimeout(() => {
              reject(
                notification.success({
                  message: intl.get('hitf.services.view.message.test.success').d('测试成功'),
                })
              );
            }, 1000);
          }
        }
      });
    });
  }

  /**
   * 查询映射类
   */
  @Bind()
  fetchMappingClass() {
    const { dispatch } = this.props;
    return dispatch({
      type: 'services/queryMappingClass',
    });
  }

  /**
   * 测试映射类
   * @param {number} interfaceId - 接口id
   * @param {string} template - 映射类代码
   */
  @Bind()
  testMappingClass(interfaceId, template) {
    const { dispatch } = this.props;
    const payload = { template };
    if (!isNull(interfaceId)) {
      payload.interfaceId = interfaceId;
    }
    return dispatch({
      type: 'services/testMappingClass',
      payload,
    });
  }

  /**
   * 显示HTTP配置弹窗
   */
  @Bind()
  handleOpenHttpModal() {
    this.setState({ isShowHttpModal: true });
  }

  /**
   * 关闭HTTP配置弹窗
   */
  @Bind()
  handleCloseHttpModal() {
    const { formDataSource = {} } = this.state;
    const { httpConfigList = [] } = formDataSource;
    this.setState({
      isShowHttpModal: false,
      httpConfigList,
    });
  }

  /**
   * 新建HTTP配置列
   */
  @Bind()
  handleCreateConfigLine() {
    const { httpConfigList } = this.state;
    const validateDataSource = getEditTableData(httpConfigList);
    // 报错时不能新建
    if (httpConfigList.some((item) => '_status' in item) && isEmpty(validateDataSource)) return;
    this.setState({
      httpConfigList: [
        {
          propertyCode: undefined,
          propertyValue: '',
          httpConfigId: uuidv4(),
          _status: 'create',
        },
        ...httpConfigList,
      ],
    });
  }

  /**
   * 清除新增行数据
   * @param {Objec} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record) {
    const { httpConfigList } = this.state;
    const newList = httpConfigList.filter((item) => item.httpConfigId !== record.httpConfigId);
    this.setState({ httpConfigList: newList });
  }

  /**
   * 编辑HTTP配置参数行
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const { httpConfigList } = this.state;
    const newList = httpConfigList.map((item) =>
      item.httpConfigId === record.httpConfigId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    this.setState({ httpConfigList: newList });
  }

  /**
   * 保存HTTP配置参数
   */
  @Bind()
  handleSaveHttpConfig() {
    const { httpConfigList } = this.state;
    const isHaveStatus = httpConfigList.some((item) => '_status' in item); // 判断是否有编辑或新建行
    const validateDataSource = getEditTableData(httpConfigList); // 判断编辑或新建行中是否有报错
    const isHaveDeleteLine = httpConfigList.some((item) => item.deleteFlag === 1);
    // 报错时不能保存
    if (
      !isEmpty(httpConfigList) &&
      !isHaveDeleteLine &&
      isEmpty(validateDataSource) &&
      isHaveStatus
    ) {
      return;
    }
    this.setState({ isShowHttpModal: false });
  }

  /**
   * 删除HTTP配置行
   * @param {object} record - http配置行
   */
  @Bind()
  handleDeleteHttpConfigLine(record) {
    const { httpConfigList } = this.state;
    const newList = httpConfigList.map((item) =>
      item.httpConfigId === record.httpConfigId ? { ...item, deleteFlag: 1 } : item
    );
    this.setState({ httpConfigList: newList });
  }

  /**
   * 接口参数识别
   * @param {number} interfaceId - 参数ID
   */
  @Bind()
  handleRecognizeParam(interfaceId) {
    const { dispatch } = this.props;
    const {
      formDataSource: { tenantId },
    } = this.state;
    dispatch({
      type: 'services/recognizeParam',
      payload: { interfaceId, organizationId: tenantId },
      interfaceId,
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * 服务参数识别
   */
  @Bind()
  handleRecognizeServiceParam() {
    const { dispatch, match = {} } = this.props;
    dispatch({
      type: 'services/recognizeServiceParam',
      interfaceServerId: match.params.id,
    }).then((res) => {
      if (res) {
        notification.success();
      }
    });
  }

  /**
   * @function getTemplateServerDetail - 运维配置-消息模板账户关联：查询消息模板关联的服务明细
   */
  @Bind()
  getTemplateServerDetail(params) {
    const { dispatch } = this.props;
    return dispatch({
      type: 'services/getTemplateServerDetail',
      params,
    });
  }

  /**
   * 切换服务类别
   * @param {string} value - 选中值
   */
  @Bind()
  handleChangeCategory(value) {
    if (value === 'INTERNAL') {
      this.basicFormDS.current.set('serviceType', 'REST');
    }
    this.basicFormDS.current.init('domainUrl', null);
    this.setState({
      currentInterfaceType: value,
      showNonDSField: value !== 'DS',
    });
  }

  /**
   * 协议变化
   */
  @Bind()
  handleChangeProtocol(value) {
    if (value === 'http://') {
      this.basicFormDS.current.init('enabledCertificateFlag', 0);
    } else if (value === 'https://') {
      this.setState({ showHttpsInfo: true });
    }
  }

  /**
   * 加密类型变化
   */
  @Bind()
  handleSoapWssPasswordTypeChange(value) {
    this.setState({ showSoapUserInfo: value !== 'NONE' });
  }

  /**
   * 证书标记变化
   */
  @Bind()
  handleCertificateFlagChange(value) {
    this.setState({ showCertificate: value === 1 });
  }

  render() {
    const {
      match,
      currentTenantId,
      tenantRoleLevel,
      queryInterfacesListDetailLoading,
      services = {},
      queryMonitorLoading,
      updateMonitorLoading,
      createMonitorLoading,
      saveInterfacesLoading,
      editing,
      deleteLinesLoading = false,
      fetchModalLoading,
      saveBatchInterfacesLoading,
      fetchMappingClassLoading = false,
      testMappingClassLoading = false,
      recognizeServiceParamLoading,
    } = this.props;
    const { enumMap = {}, formChangeFlag, code = {}, internalList, internalPagination } = services;
    const {
      formDataSource = {},
      listDataSource = [],
      listPagination,
      interfaceListSelectedRows = {},
      currentInterfaceType,
      currentServiceType,
      isShowHttpModal,
      httpConfigList,
      showHttpsInfo,
      showSoapUserInfo,
      showCertificate,
      showNonDSField,
      queryInterfaceDetailLoading,
      isHistory,
      isPublished,
    } = this.state;
    const { path } = match;
    const {
      serviceTypes = [], // 服务类型值集、发布类型？
      interfaceStatus = [], // 接口状态
      operatorList,
      assertionSubjects,
      logTypes,
    } = enumMap;
    const { interfaceServerId } = formDataSource;
    const editable = !isUndefined(interfaceServerId);
    // const title = intl.get(`hitf.services.view.message.title.detail`).d('服务注册详情');
    const title = (
      <QuestionPopover text={getLang('DETAIL_TITLE')} message={getLang('DETAIL_TITLE_TIP')} />
    );
    const listProps = {
      logTypes,
      operatorList,
      assertionSubjects,
      serviceTypes,
      interfaceStatus,
      match,
      fetchModalLoading,
      saveBatchInterfacesLoading,
      currentInterfaceType,
      currentTenantId,
      interfaceServerId,
      processing: {
        fetchInterfaceDetail: queryInterfaceDetailLoading,
        queryInterfacesListDetailLoading,
        queryMonitorLoading,
        updateMonitorLoading,
        createMonitorLoading,
        saveInterfacesLoading,
        deleteLinesLoading,
      },
      onChangeState: this.handleChangeState,
      dataSource: listDataSource,
      pagination: listPagination,
      selectedRowKeys: interfaceListSelectedRows.map((n) => n.interfaceId),
      onChange: this.onInterfaceListChange,
      onRowSelectionChange: this.onInterfaceListRowSelectionChange,
      deleteLines: this.handleDeleteLines,
      type: formDataSource.serviceType,
      serverCode: formDataSource.serverCode,
      namespace: formDataSource.namespace,
      tenantId: formDataSource.tenantId,
      codeArr: code['HITF.HTTP_CONFIG_PROPERTY'] || [], // http配置参数名可选值
      onRecognize: this.handleRecognizeParam,
      authenticationData: {
        accessTokenUrl: formDataSource.accessTokenUrl,
        authType: formDataSource.authType,
        clientId: formDataSource.clientId,
        clientSecret: formDataSource.clientSecret,
        grantType: formDataSource.grantType,
      },
      onRef: (node) => {
        this.interfaceForm = node;
      },
      fetchMonitor: this.fetchMonitor,
      createMonitor: this.createMonitor,
      updateMonitor: this.updateMonitor,
      editable,
      code,
      fetchInternalInterface: this.fetchInternalInterface,
      internalList,
      internalPagination,
      saveBatchInterfaces: this.saveBatchInterfaces,
      fetchMappingClass: this.fetchMappingClass,
      testMappingClass: this.testMappingClass,
      fetchMappingClassLoading,
      testMappingClassLoading,
      getTemplateServerDetail: this.getTemplateServerDetail,
      onFetchDetail: this.fetchDetail,
      isHistory: this.state.isHistory,
    };

    const httpConfigModalProps = {
      visible: isShowHttpModal,
      dataSource: httpConfigList,
      isHistory: this.state.isHistory,
      isPublished: this.state.isPublished,
      codeArr: code['HITF.HTTP_CONFIG_PROPERTY'] || [], // 参数名可选值
      onCreate: this.handleCreateConfigLine,
      onCleanLine: this.handleCleanLine,
      onEditLine: this.handleEditLine,
      onCancel: this.handleCloseHttpModal,
      onOk: this.handleSaveHttpConfig,
      onDelete: this.handleDeleteHttpConfigLine,
      ref: (node) => {
        this.headHttpRef = node;
      },
    };
    return (
      <>
        <Header title={title} backPath="/hitf/services/list" isChange={formChangeFlag}>
          {editable ? (
            <>
              {!isHistory && !isPublished && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.save`,
                      type: 'button',
                      meaning: '服务注册-保存',
                    },
                  ]}
                  icon="save"
                  type="c7n-pro"
                  color="primary"
                  disabled={queryInterfaceDetailLoading || isHistory}
                  loading={editing}
                  onClick={this.handleSave.bind(this)}
                >
                  {intl.get('hzero.common.button.save').d('保存')}
                </ButtonPermission>
              )}

              {this.state.isNew && !isHistory && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.release`,
                      type: 'button',
                      meaning: '服务注册-发布',
                    },
                  ]}
                  type="c7n-pro"
                  color="primary"
                  icon="publish"
                  loading={this.state.beingRelease}
                  disabled={queryInterfaceDetailLoading}
                  onClick={this.handleRelease.bind(this)}
                >
                  {intl.get('hzero.common.button.release').d('发布')}
                </ButtonPermission>
              )}

              {this.state.isOffline && !isHistory && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.online`,
                      type: 'button',
                      meaning: '服务注册-上线',
                    },
                  ]}
                  type="c7n-pro"
                  icon="arrow_upward"
                  color="primary"
                  loading={this.state.beingRelease}
                  disabled={queryInterfaceDetailLoading}
                  onClick={this.handleRelease.bind(this)}
                >
                  {intl.get('hitf.services.view.button.online').d('上线')}
                </ButtonPermission>
              )}

              {isPublished && !isHistory && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.offline`,
                      type: 'button',
                      meaning: '服务注册-下线',
                    },
                  ]}
                  type="c7n-pro"
                  icon="arrow_downward"
                  color="primary"
                  loading={this.state.beingOffline}
                  disabled={queryInterfaceDetailLoading}
                  onClick={this.handleOffline.bind(this)}
                >
                  {intl.get('hitf.services.view.button.offline').d('下线')}
                </ButtonPermission>
              )}

              {isHistory && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.revert`,
                      type: 'button',
                      meaning: '服务注册-版本回退',
                    },
                  ]}
                  type="c7n-pro"
                  icon="arrow_back"
                  color="primary"
                  loading={this.state.beingRollback}
                  disabled={queryInterfaceDetailLoading}
                  onClick={this.handleRollback.bind(this)}
                >
                  {intl.get('hitf.services.view.message.override.version').d('版本回退')}
                </ButtonPermission>
              )}

              {isHistory && (
                <ButtonPermission
                  permissionList={[
                    {
                      code: `${path}.button.revertToLastest`,
                      type: 'button',
                      meaning: '服务注册-回到最新版本',
                    },
                  ]}
                  type="c7n-pro"
                  color="primary"
                  icon="arrow_forward"
                  disabled={queryInterfaceDetailLoading}
                  onClick={this.handleNewest.bind(this)}
                >
                  {intl.get('hitf.services.view.message.newest.version').d('最新版本')}
                </ButtonPermission>
              )}

              {showNonDSField && !(isHistory || isPublished) && (
                <Tooltip
                  title={intl
                    .get('hitf.services.view.message.tip.recognize')
                    .d('拉取swagger的参数')}
                  placement="top"
                >
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${path}.button.parameterIdentifier`,
                        type: 'button',
                        meaning: '服务注册-参数识别',
                      },
                    ]}
                    type="c7n-pro"
                    icon="scanner"
                    loading={recognizeServiceParamLoading}
                    disabled={queryInterfaceDetailLoading}
                    onClick={this.handleRecognizeServiceParam}
                  >
                    {intl.get('hitf.services.view.button.recognize').d('参数识别')}
                  </ButtonPermission>
                </Tooltip>
              )}
            </>
          ) : (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.register`,
                  type: 'button',
                  meaning: '服务注册-注册',
                },
              ]}
              icon="save-o"
              type="c7n-pro"
              color="primary"
              disabled={queryInterfaceDetailLoading}
              onClick={this.handleCreate}
            >
              {intl.get('hitf.services.view.button.create').d('注册')}
            </ButtonPermission>
          )}
          {showNonDSField && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.authConfig`,
                  type: 'button',
                  meaning: '服务注册-服务认证配置',
                },
              ]}
              type="c7n-pro"
              icon="settings_applications-o"
              disabled={queryInterfaceDetailLoading}
              onClick={this.openAuthenticationServiceModal}
            >
              {intl.get('hitf.services.view.button.authConfig').d('服务认证配置')}
            </ButtonPermission>
          )}
          {showNonDSField && currentInterfaceType !== SERVICE_CONSTANT.INTERNAL && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.httpConnectConfig`,
                  type: 'button',
                  meaning: '服务注册-HTTP连接配置',
                },
              ]}
              type="c7n-pro"
              icon="attachment"
              disabled={queryInterfaceDetailLoading}
              onClick={this.handleOpenHttpModal}
            >
              {intl.get('hitf.services.view.button.httpConnectConfig').d('HTTP连接配置')}
            </ButtonPermission>
          )}
          {this.state.isHaveHistory && (
            <Select
              dataSet={this.historyDS}
              value=""
              placeholder={this.state.historyVersion}
              disabled={queryInterfaceDetailLoading}
              onChange={(value) => this.handleInterfaceServerHistoryChange({ value })}
            >
              {this.historyDS.toData().map((hv) => {
                return <Select.Option value={hv.version}>{hv.formatVersion}</Select.Option>;
              })}
            </Select>
          )}
        </Header>
        <Content>
          <Spin spinning={queryInterfaceDetailLoading || false}>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hitf.services.view.title.basicInfo').d('基本信息')}</h3>}
            >
              <Form dataSet={this.basicFormDS} columns={3} labelWidth={125}>
                {!tenantRoleLevel && (
                  <Lov
                    name="tenantLov"
                    onChange={this.handleChangeListTenant}
                    disabled={editable || isHistory || isPublished}
                  />
                )}
                <TextField name="serverCode" disabled={editable || isHistory || isPublished} />
                <TextField name="serverName" disabled={isHistory || isPublished} />
                {editable && <TextField name="namespace" disabled />}
                <Select
                  name="serviceType"
                  onChange={this.handleServiceTypeChange}
                  disabled={
                    currentInterfaceType === SERVICE_CONSTANT.INTERNAL || isHistory || isPublished
                  }
                />
                <Select
                  name="serviceCategory"
                  onChange={this.handleChangeCategory}
                  disabled={editable || isHistory || isPublished}
                />
                <Switch name="publicFlag" disabled={editable || isHistory || isPublished} />
                {showNonDSField && currentInterfaceType === SERVICE_CONSTANT.INTERNAL && (
                  <Lov name="addressLov" disabled={editable || isHistory || isPublished} />
                )}
                {showNonDSField && currentInterfaceType === SERVICE_CONSTANT.EXTERNAL && (
                  <Input.Group newLine={!editable} compact name="protocolGroup" colSpan={2}>
                    <Select
                      name="protocol"
                      style={{ width: '20%' }}
                      onChange={this.handleChangeProtocol}
                      disabled={isHistory || isPublished}
                    />
                    <TextField
                      name="domainUrl"
                      style={{ width: '80%' }}
                      disabled={isHistory || isPublished}
                    />
                  </Input.Group>
                )}
                {showNonDSField &&
                  currentInterfaceType === SERVICE_CONSTANT.EXTERNAL &&
                  showHttpsInfo && (
                    <Switch
                      name="enabledCertificateFlag"
                      onChange={this.handleCertificateFlagChange}
                    />
                  )}
                {showNonDSField &&
                  currentInterfaceType === SERVICE_CONSTANT.EXTERNAL &&
                  showHttpsInfo &&
                  showCertificate && (
                    <Lov name="certificateLov" disabled={isHistory || isPublished} />
                  )}
                {showNonDSField && currentServiceType === SERVICE_CONSTANT.REST && (
                  <TextField name="swaggerUrl" disabled={isHistory || isPublished} />
                )}
                <Switch name="enabledFlag" disabled={isHistory || isPublished} />
                {showNonDSField && currentServiceType === SERVICE_CONSTANT.SOAP && (
                  <TextField name="soapNamespace" disabled={isHistory || isPublished} />
                )}
                {showNonDSField && currentServiceType === SERVICE_CONSTANT.SOAP && (
                  <TextField name="soapElementPrefix" disabled={isHistory || isPublished} />
                )}
                {showNonDSField && currentServiceType === SERVICE_CONSTANT.SOAP && (
                  <Select
                    name="soapWssPasswordType"
                    onChange={this.handleSoapWssPasswordTypeChange}
                    disabled={isHistory || isPublished}
                  />
                )}
                {showNonDSField &&
                  currentServiceType === SERVICE_CONSTANT.SOAP &&
                  showSoapUserInfo && (
                    <TextField name="soapUsername" disabled={isHistory || isPublished} />
                  )}
                {showNonDSField &&
                  currentServiceType === SERVICE_CONSTANT.SOAP &&
                  showSoapUserInfo && (
                    <Password name="soapPassword" disabled={isHistory || isPublished} />
                  )}
                <TextField name="requestContentType" disabled={isHistory || isPublished} />
                <TextField name="responseContentType" disabled={isHistory || isPublished} />
                {currentServiceType === SERVICE_CONSTANT.SOAP && (
                  <TextField name="soapDataNode" disabled={isHistory || isPublished} />
                )}
                <Switch name="invokeVerifySignFlag" disabled={isHistory || isPublished} />
                {/* <TextField name="formatVersion" disabled /> */}
                {editable && !this.state.isNew && <TextField name="formatVersion" disabled />}
                {editable && <Select name="status" disabled />}
              </Form>
            </Card>
            <Card
              bordered={false}
              className={DETAIL_CARD_CLASSNAME}
              title={<h3>{intl.get('hitf.services.view.title.detailInterfaces').d('接口配置')}</h3>}
            >
              <InterfaceList {...listProps} />
            </Card>
          </Spin>
        </Content>
        <HttpConfigModal {...httpConfigModalProps} />
      </>
    );
  }
}
