/*
 * index - 服务注册接口页面
 * @date: 2018-10-25
 * @author: HB <bin.huang02@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import React, { PureComponent } from 'react';
import { Card, Row, Col, Spin } from 'choerodon-ui';
import {
  Modal,
  Form,
  TextField,
  DataSet,
  TextArea,
  Table,
  Select,
  Lov,
  NumberField,
  Switch,
  Tooltip,
  Icon,
  Output,
} from 'choerodon-ui/pro';
import { isEmpty, isNull, isUndefined, includes } from 'lodash';
import { Bind } from 'lodash-decorators';
import uuidv4 from 'uuid/v4';
import { operatorRender } from 'utils/renderer';
import { getCurrentOrganizationId, getEditTableData, getResponse } from 'utils/utils';
import { DETAIL_CARD_CLASSNAME, DETAIL_CARD_TABLE_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import {
  basicFormDS,
  mainConfigFormDS,
  attrListDS,
  paramListDS,
  retryFormDs,
  historyDS,
} from '@/stores/Services/interfaceDS';
import getLang from '@/langs/serviceLang';
import MappingClassModal from '@/components/MappingClassModal';
import HttpConfigModal from '@/components/HttpConfigModal';
import {
  fetchTable,
  fetchColumn,
  sqlParser,
  saveInterfaces,
  release,
  offline,
  rollbackHistory,
  deleteFieldAll,
  deleteParamAll,
  batchCreateAndUpdateField,
  batchCreateParam,
  batchUpdateParam,
  deleteParam,
} from '@/services/servicesService';
import {
  SERVICE_CONSTANT,
  STRING_LIST,
  NUMBER_LIST,
  DATE_LIST,
  DATETIME_LIST,
} from '@/constants/constants';
import QuestionPopover from '@/components/QuestionPopover';
import FieldMapping from './FieldMapping';
import DataMapping from './DataMapping';
import AssertionCard from './AssertionCard';

export default class Editor extends PureComponent {
  constructor(props) {
    super(props);
    props.onRef(this);
    this.state = {
      dataSource: props.dataSource || {},
      tenantId: props.tenantId,
      currentTenantId: getCurrentOrganizationId(),
      currentHttpConfigList: [],
      currentCode: '',
      isShowModal: false,
      isShowHttpModal: false,
      tableList: [], // 表或视图数据源
      columnList: [], // 表字段
      detailLoading: false,
      sqlLoading: false,
      selectedAttrRows: [],
      selectedParamRows: [],
      showSqlBtn: true,
      isHistory: false,
      historyVersion: intl.get('hitf.services.view.message.history.version').d('历史版本'),
      isHaveHistory: false,
    };

    this.basicFormDS = new DataSet({
      ...basicFormDS({ currentInterfaceType: props.currentInterfaceType }),
    });

    this.retryFormDs = new DataSet({
      ...retryFormDs(),
    });

    this.mainConfigFormDS = new DataSet(mainConfigFormDS());

    this.columnDs = new DataSet({
      data: [],
    });

    this.historyDS = new DataSet(historyDS());

    this.attrListDS = new DataSet({
      pageSize: 10,
      ...attrListDS({
        onFieldSelect: this.handleFieldSelect,
        unFieldSelect: this.handleFieldUnselect,
        onFieldSelectAll: this.handleFieldSelectAll,
        unFieldSelectAll: this.handleFieldUnSelectAll,
        onAttrLoad: this.handleFieldLoad,
        columnDs: this.columnDs,
      }),
    });

    this.paramListDS = new DataSet({
      pageSize: 10,
      ...paramListDS({
        modelFieldDs: this.attrListDS,
        modelDs: this.mainConfigFormDS,
        onParamSelect: this.handleParamSelect,
        unParamSelect: this.handleParamUnselect,
        onParamSelectAll: this.handleParamSelectAll,
        unParamSelectAll: this.handleParamUnSelectAll,
      }),
    });
  }

  async componentDidMount() {
    const { interfaceListActionRow = {} } = this.props;
    const { interfaceId } = interfaceListActionRow;
    if (!isUndefined(interfaceId)) {
      this.handleFetchDetail(interfaceId);
    } else {
      this.basicFormDS.current.set('status', SERVICE_CONSTANT.NEW);
    }
  }

  @Bind
  async handleInterfaceHistoryChange({ value }) {
    if (value === null) {
      return;
    }
    const { interfaceListActionRow = {} } = this.props;
    const { interfaceId } = interfaceListActionRow;
    this.basicFormDS.setQueryParameter('version', value);
    this.basicFormDS.setQueryParameter('history', true);
    this.basicFormDS.setQueryParameter('organizationId', this.state.currentTenantId);
    const formatVersion = 'V'.concat(value, '.0');
    this.setState({ historyVersion: formatVersion, isHistory: true });
    this.handleFetchDetail(interfaceId);
    await this.basicFormDS.query();
  }

  @Bind
  async handleRollbackHistory() {
    return new Promise((resolve, reject) => {
      const validate = Promise.all([
        this.basicFormDS.validate(),
        this.mainConfigFormDS.validate(),
        this.attrListDS.validate(),
        this.paramListDS.validate(),
      ]);
      if (includes(validate, false)) {
        notification.error({ message: getLang('NOTIFICATION_ERROR_MODEL') });
        reject(validate);
      }
      rollbackHistory({
        ...this.basicFormDS.toData()[0],
      }).then((res) => {
        this.setState({
          isHistory: false,
          historyVersion: intl.get('hitf.services.view.message.history.version').d('历史版本'),
        });
        if (getResponse(res)) {
          this.handleFetchDetail(res.interfaceId);
          this.props.onFetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  @Bind()
  handleNewest() {
    this.setState({ isHistory: false });
    const { interfaceListActionRow = {} } = this.props;
    const { interfaceId } = interfaceListActionRow;
    this.handleFetchDetail(interfaceId);
  }

  @Bind()
  handleFieldSelect({ record }) {
    const { selectedAttrRows, stopFieldSelectEvent } = this.state;
    const temp = record.toData();
    this.setState({
      selectedAttrRows: [...selectedAttrRows, { ...temp }],
    });
    if (stopFieldSelectEvent) {
      return;
    }
    const {
      creationDate,
      createdBy,
      lastUpdateDate,
      lastUpdatedBy,
      objectVersionNumber,
      _token,
      ...other
    } = temp;
    const data = {
      ...other,
      paramName: other.fieldName,
      paramType: this.handleTypeTransfer(other.fieldType),
      requiredFlag: false,
    };
    const paramRecord = this.paramListDS.create(data);
    paramRecord.set('seqNum', paramRecord.index + 1);
  }

  @Bind()
  async handleFieldUnselect(fieldRecord) {
    const { selectedAttrRows, stopFieldUnSelectEvent } = this.state;
    this.setState({
      selectedAttrRows: selectedAttrRows.filter((record) => record.key !== fieldRecord.data.key),
    });
    if (stopFieldUnSelectEvent) {
      return;
    }
    const deletedParamRecord = this.paramListDS.find(
      (record) => record.data.key === fieldRecord.data.key
    );
    const confirm = await Modal.confirm({
      children: <p>{getLang('PARAM_CANCEL_CHECK')}</p>,
    });
    if (confirm === 'ok') {
      if (deletedParamRecord.status !== 'add') {
        await deleteParam([{ ...deletedParamRecord.toData() }]);
      }
      this.paramListDS.data = this.paramListDS.filter(
        (record) => record.data.key !== fieldRecord.data.key
      );
    } else {
      this.setState({ stopFieldSelectEvent: true }, () => {
        this.attrListDS.select(fieldRecord);
        this.setState({ stopFieldSelectEvent: false });
      });
    }
  }

  @Bind()
  handleFieldSelectAll({ dataSet }) {
    this.setState({ selectedAttrRows: dataSet.toData() });
    const dates = dataSet.toData().map((item) => {
      const {
        creationDate,
        createdBy,
        lastUpdateDate,
        lastUpdatedBy,
        objectVersionNumber,
        _token,
        ...other
      } = item;
      return {
        ...other,
        paramName: other.fieldName,
        paramType: this.handleTypeTransfer(other.fieldType),
        requiredFlag: false,
        paramDesc: other.fieldDesc,
      };
    });
    this.paramListDS.reset();
    dates.forEach((item) => {
      const paramRecord = this.paramListDS.create({ ...item });
      paramRecord.set('seqNum', paramRecord.index + 1);
    });
  }

  @Bind()
  handleFieldUnSelectAll() {
    this.setState({ selectedAttrRows: [] });
    this.paramListDS.reset();
  }

  /**
   * 类型转换
   */
  handleTypeTransfer(type) {
    if (STRING_LIST.includes(type)) {
      return 'STRING';
    }
    if (NUMBER_LIST.includes(type)) {
      return 'NUMBER';
    }
    if (DATE_LIST.includes(type)) {
      return 'DATE';
    }
    if (DATETIME_LIST.includes(type)) {
      return 'DATETIME';
    }
  }

  /**
   * 查询结果后，根据参数列表主动勾选上属性列表的勾选框
   */
  fieldSelectedStatus(record) {
    const selectedRecord = this.attrListDS.records.find(
      (item) => item.get('fieldId') === record.get('fieldId')
    );
    this.attrListDS.select(selectedRecord);
  }

  /**
   * 批量勾选
   */
  batchFieldSelectedStatus(records) {
    this.setState({ stopFieldSelectEvent: true }, () => {
      records.forEach((record) => {
        this.fieldSelectedStatus(record);
      });
    });
  }

  /**
   * 新建数据根据key过滤，否则根据fieldId过滤
   */
  cleanFieldSelectedStatus(record) {
    let unSelectedRecord = {};
    if (record.status === 'add') {
      unSelectedRecord = this.attrListDS.find((item) => item.get('key') === record.get('key'));
    } else {
      unSelectedRecord = this.attrListDS.find(
        (item) => item.get('fieldId') === record.get('fieldId')
      );
    }
    this.setState({ stopFieldUnSelectEvent: true }, () => {
      this.attrListDS.unSelect(unSelectedRecord);
      this.setState({ stopFieldUnSelectEvent: false });
    });
  }

  /**
   * 批量过滤
   */
  batchCleanFieldSelectedStatus(selectedRecords) {
    selectedRecords.forEach((record) => {
      this.cleanFieldSelectedStatus(record);
    });
  }

  @Bind()
  handleParamSelect({ record }) {
    const temp = record.toData();
    const { selectedParamRows } = this.state;
    this.setState({
      selectedParamRows: [...selectedParamRows, { ...temp }],
    });
  }

  @Bind()
  handleParamUnselect(fieldRecord) {
    const { selectedParamRows } = this.state;
    this.setState({
      selectedParamRows: selectedParamRows.filter((record) => record.key !== fieldRecord.data.key),
    });
  }

  @Bind()
  handleParamSelectAll({ dataSet }) {
    this.setState({ selectedParamRows: dataSet.toData() });
  }

  @Bind()
  handleParamUnSelectAll() {
    this.setState({ selectedParamRows: [] });
  }

  /**
   * 格式化参数列表
   */
  handleFormatParam() {
    this.paramListDS.records.forEach((item) => {
      const attrRecord = this.attrListDS.find(
        (temp) => item.get('fieldId') === temp.get('fieldId')
      );
      item.set('key', attrRecord.get('key'));
    });
  }

  @Bind()
  async handleFetchDetail(interfaceId) {
    const { currentInterfaceType } = this.props;
    this.basicFormDS.setQueryParameter('interfaceId', interfaceId);
    this.setState({ detailLoading: true });
    const res = await this.basicFormDS.query();
    this.historyDS.setQueryParameter('interfaceId', interfaceId);
    const historyRes = await this.historyDS.query();
    if (!isEmpty(historyRes)) {
      this.setState({ isHaveHistory: true });
    }
    if (res) {
      this.handleUpdateModalButton(res);
      this.setState({
        dataSource: res,
        currentHttpConfigList: res.httpConfigList,
      });
    }
    const { retryTimes, retryInterval, assertJson = '[]' } = this.basicFormDS.current.toData();
    this.retryFormDs.loadData([{ retryTimes, retryInterval }]);
    this.assertionDS.loadData(JSON.parse(assertJson));
    if (currentInterfaceType === 'DS') {
      this.mainConfigFormDS.setQueryParameter('interfaceId', interfaceId);
      const mainConfigRes = await this.mainConfigFormDS
        .query()
        .catch(() => this.setState({ detailLoading: false }));
      if (mainConfigRes) {
        this.attrListDS.setQueryParameter('modelId', mainConfigRes.modelId);
        this.paramListDS.setQueryParameter('modelId', mainConfigRes.modelId);
        await Promise.all([this.attrListDS.query(), this.paramListDS.query()]).catch(() =>
          this.setState({ detailLoading: false })
        );
        this.attrListDS.records.forEach((item) => {
          item.init('key', uuidv4());
        });
        this.handleFormatParam();
        this.batchFieldSelectedStatus(this.paramListDS.records);
        this.setState({ stopFieldSelectEvent: false });
        this.queryColumns({
          datasourceId: mainConfigRes.datasourceId,
          datasourceCode: mainConfigRes.datasourceCode,
          dsPurposeCode: mainConfigRes.dsPurposeCode,
          table: mainConfigRes.exprContent,
        })
          .then((columnRes) => {
            if (getResponse(columnRes)) {
              this.columnDs.data = [];
              this.setState({ columnList: columnRes }, () => {
                this.state.columnList.forEach((item) => this.columnDs.create({ ...item }));
              });
            }
          })
          .catch();
        this.setState({
          exprType: mainConfigRes.exprType,
          showSqlBtn: isEmpty(mainConfigRes.exprContent),
        });
      }
    }
    this.setState({ detailLoading: false });
  }

  cancel() {
    const { onCancel = (e) => e } = this.props;
    this.setState({
      dataSource: {},
    });
    onCancel();
  }

  // 处理HTTP配置
  @Bind()
  handleHttpConfigList() {
    const { currentHttpConfigList } = this.state;
    let unChangedHttpConfigList = [];
    let validateHttpConfigList = [];
    if (!isEmpty(currentHttpConfigList)) {
      unChangedHttpConfigList = currentHttpConfigList.filter((item) => !('_status' in item));
      validateHttpConfigList = getEditTableData(currentHttpConfigList);
      validateHttpConfigList = validateHttpConfigList.map((item) => {
        if (item._status === 'create') {
          const { _status, httpConfigId, ...others } = item;
          return others;
        } else {
          const { _status, ...others } = item;
          return others;
        }
      });
    }
    return [...unChangedHttpConfigList, ...validateHttpConfigList];
  }

  /**
   * Detail/List组件中的handleOk调用此方法
   * 新增时
   */
  @Bind()
  onModalCloseValidate() {
    return new Promise((resolve, reject) => {
      const { idModifiedFlag = false } = this.state;
      if (idModifiedFlag) {
        Modal.warning(getLang('MAPPING_MESSAGE_CONFIRM'));
        reject();
      } else {
        resolve();
      }
    });
  }

  /**
   * Detail/List组件中的handleOk调用此方法
   */
  @Bind()
  async handleOk() {
    // 关闭弹窗关闭的校验
    this.setState({ idModifiedFlag: false });
    const { interfaceServerId, currentInterfaceType, onFetchDetail } = this.props;
    const { currentTenantId } = this.state;
    const validate = await this.handleValidate();
    const mappingClass = this.getCurrentCode();
    const validateHttpConfigList = this.handleHttpConfigList();
    if (validate) {
      const basicData = this.basicFormDS.current.toData();
      const retryData = this.retryFormDs.length > 0 ? this.retryFormDs.current.toData() : {};
      const assertionData = this.assertionDS.toData();
      let modelConfig = {};
      if (currentInterfaceType === 'DS') {
        const { dataSourceLov, ...other } = this.mainConfigFormDS.current.toData();
        modelConfig = other;
      }
      const { interfaceId } = basicData;
      const targetItem = interfaceId
        ? {
            ...basicData,
            mappingClass,
            modelConfig,
            ...retryData,
            assertJson: JSON.stringify(assertionData),
            httpConfigList: validateHttpConfigList,
          }
        : {
            ...basicData,
            tenantId: currentTenantId,
            modelConfig,
            ...retryData,
            assertJson: JSON.stringify(assertionData),
          };
      const result = await saveInterfaces(interfaceServerId, targetItem).then((res) => res);
      if (getResponse(result)) {
        let stopCloseFlag = false;
        if (currentInterfaceType === 'DS') {
          const fieldResult = await this.handleBatchCreateAndUpdateField({
            tenantId: result.tenantId,
            modelId: result.modelConfig.modelId,
          });
          let saveParamFlag = false;
          if (!getResponse(fieldResult)) {
            stopCloseFlag = true;
          } else {
            saveParamFlag = await this.handleBatchCreateAndUpdateParam(
              {
                tenantId: result.tenantId,
                modelId: result.modelConfig.modelId,
              },
              fieldResult
            );
          }
          if (saveParamFlag) {
            this.attrListDS.setQueryParameter('modelId', result.modelConfig.modelId);
            await this.attrListDS.query();
            stopCloseFlag = true;
          }
        }
        if (stopCloseFlag) {
          this.basicFormDS.setQueryParameter('interfaceId', result.interfaceId);
          this.mainConfigFormDS.setQueryParameter('interfaceId', result.interfaceId);
          this.setState({ detailLoading: true });
          await Promise.all([this.basicFormDS.query(), this.mainConfigFormDS.query()]);
          this.setState({ detailLoading: false });
          return Promise.reject();
        }
        onFetchDetail();
        notification.success();
        this.cancel();
      } else {
        return Promise.reject();
      }
    } else {
      notification.error({
        message: getLang('SAVE_VALIDATE'),
      });
      return Promise.reject();
    }
  }

  async handleValidate() {
    const { currentInterfaceType } = this.props;
    const validFlag = await Promise.all([
      this.basicFormDS.validate(),
      this.assertionDS.validate(),
      this.mainConfigFormDS.validate(),
      this.attrListDS.validate(),
      this.paramListDS.validate(),
    ]);
    if (currentInterfaceType !== 'DS') {
      return validFlag[0] && validFlag[1];
    }
    if (validFlag.includes(false)) {
      return false;
    }
    return true;
  }

  @Bind()
  async handleSourceLovChange(value, oldValue, record) {
    record.init('exprContent', null);
  }

  /*
   * 根据数据源查询table
   */
  @Bind()
  async queryTable(params) {
    const response = await fetchTable(params);
    if (getResponse(response)) {
      this.setState({ tableList: response });
    }
  }

  @Bind()
  async queryColumns(params) {
    const response = await fetchColumn(params);
    if (getResponse(response)) {
      return response.map((item) => ({
        fieldName: item.fieldName,
        seqNum: item.seqNum,
        fieldDesc: item.fieldDesc,
        fieldType: item.fieldType,
        fieldExpr: `$${item.seqNum}`,
        requiredFlag: false,
        privacyLevel: 0,
        key: uuidv4(),
      }));
    }
  }

  /*
   * 解析sql
   */
  @Bind()
  async handleSqlAnalysis(record) {
    let confirm = 'ok';
    if (this.attrListDS.length > 0 || this.paramListDS.length > 0) {
      confirm = await Modal.confirm({
        title: getLang('ANALYSIS_SQL'),
        children: <p>{getLang('ANALYSIS')}</p>,
      });
    }
    if (confirm === 'ok') {
      this.deleteAllFieldAndParam();
      const sql = record.get('exprContent');
      if (!isNull(sql)) {
        this.setState({ sqlLoading: true });
        const response = await sqlParser({ exprContent: sql });
        this.setState({ sqlLoading: false });
        if (getResponse(response)) {
          const { modelField = [], requestParam = [] } = response;
          notification.success();
          this.attrListDS.data = [];
          this.paramListDS.data = [];
          this.columnDs.data = [];
          modelField.forEach((item) => {
            this.attrListDS.create(item);
            this.columnDs.create(item);
          });
          requestParam.forEach((item) => {
            const paramRecord = this.paramListDS.create(item);
            paramRecord.set('seqNum', paramRecord.index + 1);
          });
        }
      } else {
        notification.error({
          message: getLang('SQL_ERROR'),
        });
      }
    }
    return false;
  }

  /**
   * 新建HTTP配置列
   */
  @Bind()
  handleCreateConfigLine() {
    const { currentHttpConfigList } = this.state;
    const validateDataSource = getEditTableData(currentHttpConfigList);
    // 报错时不能新建
    if (currentHttpConfigList.some((item) => '_status' in item) && isEmpty(validateDataSource)) {
      return;
    }
    this.setState({
      currentHttpConfigList: [
        {
          propertyCode: undefined,
          propertyValue: '',
          httpConfigId: uuidv4(),
          _status: 'create',
        },
        ...currentHttpConfigList,
      ],
    });
  }

  /**
   * 清除新增行数据
   * @param {Objec} record - 待清除的数据对象
   */
  @Bind()
  handleCleanLine(record) {
    const { currentHttpConfigList } = this.state;
    const newList = currentHttpConfigList.filter(
      (item) => item.httpConfigId !== record.httpConfigId
    );
    this.setState({ currentHttpConfigList: newList });
  }

  /**
   * 编辑HTTP配置参数行
   * @param {Object} record - 备选值行数据
   * @param {Boolean} flag - 编辑/取消标记
   */
  @Bind()
  handleEditLine(record, flag) {
    const { currentHttpConfigList } = this.state;
    const newList = currentHttpConfigList.map((item) =>
      item.httpConfigId === record.httpConfigId ? { ...item, _status: flag ? 'update' : '' } : item
    );
    this.setState({ currentHttpConfigList: newList });
  }

  /**
   * 删除HTTP配置行
   * @param {object} record - http配置行
   */
  @Bind()
  handleDeleteLine(record) {
    const { currentHttpConfigList } = this.state;
    const newList = currentHttpConfigList.map((item) =>
      item.httpConfigId === record.httpConfigId ? { ...item, deleteFlag: 1 } : item
    );
    this.setState({ currentHttpConfigList: newList });
  }

  @Bind()
  handleCloseHttpModal() {
    const { dataSource } = this.state;
    this.setState({ currentHttpConfigList: dataSource.httpConfigList });
    this.handleCancelHttpModal();
  }

  /**
   * 获取映射类内容
   */
  @Bind()
  getCurrentCode() {
    const { dataSource = {}, currentCode } = this.state;
    const { mappingClass } = dataSource;
    return currentCode || mappingClass;
  }

  /**
   * 显示映射类弹窗
   */
  @Bind()
  handleOpenMappingClassModal() {
    const { onFetchMappingClass = () => {} } = this.props;
    const { currentCode, dataSource = {} } = this.state;
    const { mappingClass } = dataSource;
    let code = '';
    if (currentCode) {
      code = currentCode;
    } else if (mappingClass) {
      code = mappingClass;
    } else {
      onFetchMappingClass().then((res) => {
        if (res) {
          this.setState({
            currentCode: res.template,
            isShowModal: true,
          });
        }
      });
    }
    this.setState({
      currentCode: code,
      isShowModal: true,
    });
  }

  /**
   * 关闭映射类弹窗
   */
  @Bind()
  handleCloseMappingClassModal(value) {
    this.setState({
      isShowModal: false,
      currentCode: value,
    });
  }

  /**
   * 测试映射类
   * @param {string} value - 映射类代码
   */
  @Bind()
  handleTestMappingClass(value, cb = (e) => e) {
    const {
      onTestMappingClass = () => {},
      fetchMappingClassLoading,
      testMappingClassLoading,
      editable,
    } = this.props;
    const { dataSource = {} } = this.state;
    const { interfaceId } = dataSource;
    this.setState({
      currentCode: value,
    });
    if (fetchMappingClassLoading || testMappingClassLoading) return;
    const tempInterfaceId = editable ? interfaceId : null;
    onTestMappingClass(tempInterfaceId, value).then((res) => {
      if (res) {
        cb(res);
      }
    });
  }

  /**
   * 保存接口HTTP配置参数
   */
  @Bind()
  handleSaveHttpConfig() {
    const { currentHttpConfigList } = this.state;
    const isHaveStatus = currentHttpConfigList.some((item) => '_status' in item); // 判断是否有编辑或新建行
    const validateDataSource = getEditTableData(currentHttpConfigList);
    const isHaveDeleteLine = currentHttpConfigList.some((item) => item.deleteFlag === 1);
    // 报错时不能保存
    if (
      !isEmpty(currentHttpConfigList) &&
      !isHaveDeleteLine &&
      isEmpty(validateDataSource) &&
      isHaveStatus
    ) {
      return;
    }
    this.handleCancelHttpModal();
  }

  /**
   * 显示http配置弹窗
   */
  @Bind()
  handleOpenHttpConfigModal() {
    this.setState({ isShowHttpModal: true });
  }

  @Bind()
  handleCancelHttpModal() {
    this.setState({ isShowHttpModal: false });
  }

  /**
   * 表达式类型
   */
  @Bind()
  handleExpressionTypeChange(value) {
    this.mainConfigFormDS.current.init('exprContent', null);
    this.setState({
      exprType: value,
      showSqlBtn: true,
    });
  }

  /**
   * 清除新建的属性字段时，清除相应的新建的请求参数
   * @param record
   */
  @Bind()
  handleFieldClean(record) {
    this.attrListDS.remove(record);
    this.paramListDS.data = this.paramListDS.filter(
      (paramItem) => paramItem.toData().fieldObj.fieldName !== record.toData().fieldName
    );
  }

  /**
   * 属性删除
   */
  async handleFieldDelete(record) {
    const confirmMessage = <span>{getLang('FIELD_DELETE_CONFIRM')}</span>;
    try {
      await this.attrListDS.delete(record, confirmMessage);
      const modelId = this.mainConfigFormDS.current.get('modelId');
      this.paramListDS.setQueryParameter('modelId', modelId);
      await this.paramListDS.query();
      this.handleFormatParam();
    } catch {
      return false;
    }
  }

  @Bind()
  async handleTableFocus(record) {
    const exprType = record.get('exprType');
    const datasourceId = record.get('datasourceId');
    if (exprType === 'DBO' && !isUndefined(datasourceId)) {
      await this.queryTable({
        datasourceId,
        datasourceCode: record.get('datasourceCode'),
        dsPurposeCode: record.get('dsPurposeCode'),
      });
    }
  }

  /**
   * 表或视图切换
   */
  @Bind()
  async handleTableChange(value, oldValue, record) {
    const datasourceId = record.get('datasourceId');
    const datasourceCode = record.get('datasourceCode');
    const dsPurposeCode = record.get('dsPurposeCode');
    let confirm = 'ok';
    if (this.attrListDS.length > 0 || this.paramListDS.length > 0) {
      confirm = await Modal.confirm({
        title: getLang('CHANGE_TABLE'),
        children: <p>{getLang('CHANGE_VIEW')}</p>,
      });
    }
    if (confirm === 'ok') {
      this.deleteAllFieldAndParam();
      if (!isNull(value)) {
        this.queryColumns({ datasourceId, datasourceCode, dsPurposeCode, table: value }).then(
          (res) => {
            if (res) {
              this.setState({ columnList: res }, () => {
                this.attrListDS.data = [];
                this.paramListDS.data = [];
                this.columnDs.data = [];
                this.state.columnList.forEach((item) => {
                  this.attrListDS.create({ ...item });
                });
                this.state.columnList.forEach((item) => this.columnDs.create({ ...item }));
              });
            }
          }
        );
      }
    } else {
      this.mainConfigFormDS.current.set('exprContent', oldValue);
    }
  }

  /**
   * 属性列表单独保存
   */
  async handleFieldSave() {
    if (this.basicFormDS.status === 'add') {
      return notification.error({
        message: getLang('ATTR_SAVE_ERROR'),
      });
    }
    try {
      const validate = await this.attrListDS.validate();
      if (validate) {
        const modelId = this.mainConfigFormDS.current.get('modelId');
        const tenantId = this.mainConfigFormDS.current.get('tenantId');
        this.attrListDS.records.forEach((record) => {
          record.set('tenantId', tenantId);
          record.set('modelId', modelId);
        });
        await this.attrListDS.submit();
        await this.attrListDS.query();
      }
    } catch {
      return false;
    }
    return false;
  }

  /**
   * 属性列表批量删除
   */
  async handleFieldBachDelete() {
    try {
      const confirmMessage = <span>{getLang('FIELD_DELETE_CONFIRM')}</span>;
      await this.attrListDS.delete(this.attrListDS.selected, confirmMessage);
      const modelId = this.mainConfigFormDS.current.get('modelId');
      this.paramListDS.setQueryParameter('modelId', modelId);
      await this.paramListDS.query();
      this.handleFormatParam();
      this.setState({ selectedAttrRows: [] });
    } catch (error) {
      return false;
    }
  }

  /**
   * 参数列表单独保存
   */
  async handleParamSave() {
    if (this.basicFormDS.status === 'add') {
      return notification.error({
        message: getLang('ATTR_SAVE_ERROR'),
      });
    }
    try {
      const validate = await this.paramListDS.validate();
      if (validate) {
        const modelId = this.mainConfigFormDS.current.get('modelId');
        const tenantId = this.mainConfigFormDS.current.get('tenantId');
        this.paramListDS.records.forEach((record) => {
          const field = this.attrListDS.find(
            (item) => record.get('fieldName') === item.get('fieldName')
          );
          record.set('tenantId', tenantId);
          record.set('fieldId', field.get('fieldId'));
          record.set('modelId', modelId);
        });
        await this.paramListDS.submit();
        await this.paramListDS.query();
        this.handleFormatParam();
      }
    } catch {
      return false;
    }
    return false;
  }

  /**
   * 参数删除
   */
  @Bind
  async handleParamDelete(record) {
    const res = await this.paramListDS.delete(record);
    if (res) {
      this.cleanFieldSelectedStatus(record);
    }
  }

  /**
   * 参数清空
   */
  @Bind
  handleParamClean(record) {
    this.paramListDS.remove(record);
    this.cleanFieldSelectedStatus(record);
  }

  /**
   * 参数列表批量删除
   */
  async handleParamBachDelete() {
    const selectedRecords = this.paramListDS.selected;
    try {
      await this.paramListDS.delete(selectedRecords);
      this.batchCleanFieldSelectedStatus(selectedRecords);
      this.setState({ selectedParamRows: [] });
    } catch (error) {
      return false;
    }
  }

  /**
   * 删除所有的属性列表和参数列表
   */
  deleteAllFieldAndParam() {
    const modelId = this.mainConfigFormDS.current.get('modelId');
    if (modelId) {
      deleteFieldAll({ modelId });
      deleteParamAll({ modelId });
    }
  }

  /**
   * 发布
   */
  @Bind()
  async handleRelease() {
    return new Promise((resolve, reject) => {
      const validate = Promise.all([
        this.basicFormDS.validate(),
        this.mainConfigFormDS.validate(),
        this.attrListDS.validate(),
        this.paramListDS.validate(),
      ]);
      if (includes(validate, false)) {
        notification.error({ message: getLang('NOTIFICATION_ERROR_MODEL') });
        reject(validate);
      }
      release({
        ...this.basicFormDS.toData()[0],
      }).then((res) => {
        if (getResponse(res)) {
          this.handleFetchDetail(res.interfaceId);
          this.props.onFetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  /**
   * 下线
   */
  @Bind()
  async handleOffline() {
    return new Promise((resolve, reject) => {
      const validate = Promise.all([
        this.basicFormDS.validate(),
        this.mainConfigFormDS.validate(),
        this.attrListDS.validate(),
        this.paramListDS.validate(),
      ]);
      if (includes(validate, false)) {
        notification.error({ message: getLang('NOTIFICATION_ERROR_MODEL') });
        reject(validate);
      }
      offline({
        ...this.basicFormDS.toData()[0],
      }).then((res) => {
        if (getResponse(res)) {
          this.handleFetchDetail(res.interfaceId);
          this.props.onFetchDetail();
          notification.success();
          resolve(res);
        } else {
          reject(res);
        }
      });
    });
  }

  /**
   * 批量创建和更新参数列表
   */
  handleBatchCreateAndUpdateField(param = {}) {
    const attrList = this.attrListDS.toData();
    let result = {};
    if (!isEmpty(attrList)) {
      const params = attrList.map((item) => {
        const { fieldObj, ...other } = item;
        return {
          ...other,
          ...param,
        };
      });
      result = batchCreateAndUpdateField(params).then((res) => res);
    }
    return result;
  }

  /**
   * 批量创建和更新参数列表
   */
  async handleBatchCreateAndUpdateParam(param = {}, fieldResult = []) {
    const paramList = this.paramListDS.toData();
    const params = paramList.map((item) => {
      const { fieldObj, ...other } = item;
      const field = fieldResult.find((temp) => temp.fieldName === item.fieldName);
      return {
        ...other,
        ...param,
        fieldId: field.fieldId,
      };
    });
    const createdList = params.filter((item) => isUndefined(item.paramId));
    const updatedList = params.filter((item) => !isUndefined(item.paramId));
    let flag = false;
    if (!isEmpty(createdList)) {
      const res = await batchCreateParam(createdList);
      if (!getResponse(res)) {
        flag = true;
      }
    }
    if (!isEmpty(updatedList)) {
      const res = await batchUpdateParam(updatedList);
      if (!getResponse(res)) {
        flag = true;
      }
    }
    return flag;
  }

  @Bind()
  handleSqlChange() {
    this.setState({
      showSqlBtn: isEmpty(this.mainConfigFormDS.current.get('exprContent')),
    });
  }

  /**
   * 属性列表-绑定属性变更
   */
  @Bind()
  handleFieldChange(value, record) {
    const fieldType = this.handleTypeTransfer(value.fieldObj.fieldType);
    record.set('paramType', fieldType);
  }

  /**
   * 参数新增
   */
  @Bind()
  handleParamCreate() {
    const record = this.paramListDS.create();
    record.set('seqNum', record.index + 1);
  }

  /**
   * 更新滑窗按钮
   */
  handleUpdateModalButton(res) {
    const {
      match: { path },
    } = this.props;
    const disabledFlag =
      res.status === SERVICE_CONSTANT.ENABLED ||
      res.status === SERVICE_CONSTANT.DISABLED_INPROGRESS;
    this.props.modal.update({
      title: (
        <>
          {this.props.title}&nbsp;&nbsp;
          {this.state.isHaveHistory && (
            <Select
              dataSet={this.historyDS}
              value=""
              placeholder={this.state.historyVersion}
              onChange={(value) => this.handleInterfaceHistoryChange({ value })}
            >
              {this.historyDS.toData().map((hv) => {
                return <Select.Option value={hv.version}>{hv.formatVersion}</Select.Option>;
              })}
            </Select>
          )}
        </>
      ),
      footer: (_okBtn, cancelBtn) => (
        <div style={{ textAlign: 'right' }}>
          {!this.state.isHistory && !(res.status === SERVICE_CONSTANT.ENABLED) && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.saveLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置保存',
                },
              ]}
              type="c7n-pro"
              color="primary"
              disabled={disabledFlag || this.state.isHistory}
              onClick={this.handleOk}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          )}

          {this.state.isHistory && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.revertLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置版本回退',
                },
              ]}
              type="c7n-pro"
              color="primary"
              onClick={this.handleRollbackHistory}
            >
              {intl.get('hitf.services.view.message.override.version').d('版本回退')}
            </ButtonPermission>
          )}

          {this.state.isHistory && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.revertToLastestLine`,
                  type: 'button',
                  meaning: '服务注册-回到最新版本',
                },
              ]}
              type="c7n-pro"
              loading={this.state.beingRollback}
              onClick={this.handleNewest.bind(this)}
            >
              {intl.get('hitf.services.view.message.newest.version').d('最新版本')}
            </ButtonPermission>
          )}

          {res.status === SERVICE_CONSTANT.NEW && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.releaseLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置发布',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleRelease}
            >
              {intl.get('hzero.common.button.release').d('发布')}
            </ButtonPermission>
          )}
          {res.status === SERVICE_CONSTANT.ENABLED && !this.state.isHistory && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.offlineLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置下线',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleOffline}
            >
              {intl.get('hitf.services.view.button.offline').d('下线')}
            </ButtonPermission>
          )}
          {res.status === SERVICE_CONSTANT.DISABLED && !this.state.isHistory && (
            <ButtonPermission
              permissionList={[
                {
                  code: `${path}.button.onlineLine`,
                  type: 'button',
                  meaning: '服务注册-接口配置上线',
                },
              ]}
              type="c7n-pro"
              onClick={this.handleRelease}
            >
              {intl.get('hitf.services.view.button.online').d('上线')}
            </ButtonPermission>
          )}
          {cancelBtn}
        </div>
      ),
    });
  }

  /**
   * 打开字段映射弹窗
   */
  @Bind()
  handleOpenFieldMappingModal(transformIdName, level) {
    const { match, serverCode, namespace, tenantId } = this.props;
    const readOnly =
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.ENABLED ||
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.DISABLED_INPROGRESS;
    const fieldMappingProps = {
      match,
      readOnly,
      tenantId,
      transformIdName,
      namespace,
      serverCode,
      transformLevel: level,
      interfaceId: this.basicFormDS.current.get('interfaceId'),
      interfaceCode: this.basicFormDS.current.get('interfaceCode'),
      transformId: this.basicFormDS.current.get(transformIdName),
      publishType: this.basicFormDS.current.get('publishType'),
      onWriteBack: this.writeBack,
    };
    Modal.open({
      title: getLang('MAINTAIN_FIELD_MAPPING'),
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1300 },
      children: <FieldMapping {...fieldMappingProps} />,
    });
  }

  /**
   * 打开数据映射弹窗
   */
  @Bind()
  handleOpenDataMappingModal(castHeaderIdName, level) {
    const { match, serverCode, namespace, type: dataType, tenantId } = this.props;
    const readOnly =
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.ENABLED ||
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.DISABLED_INPROGRESS;
    const dataMappingProps = {
      match,
      readOnly,
      tenantId,
      castHeaderIdName,
      dataType,
      namespace,
      serverCode,
      castLevel: level,
      interfaceId: this.basicFormDS.current.get('interfaceId'),
      interfaceCode: this.basicFormDS.current.get('interfaceCode'),
      castHeaderId: this.basicFormDS.current.get(castHeaderIdName),
      publishType: this.basicFormDS.current.get('publishType'),
      onWriteBack: this.writeBack,
    };
    Modal.open({
      title: getLang('MAINTAIN_DATA_MAPPING'),
      drawer: true,
      closable: true,
      key: Modal.key(),
      style: { width: 1200 },
      children: <DataMapping {...dataMappingProps} />,
    });
  }

  /**
   * 会写id
   */
  @Bind()
  writeBack(id, idValue, nameValue) {
    const name = id.replace(/Id/, 'Name');
    this.basicFormDS.current.set(id, idValue);
    this.basicFormDS.current.set(name, nameValue);
    this.setState({ idModifiedFlag: true });
  }

  @Bind()
  getFieldColumns(expType, status) {
    const {
      match: { path },
    } = this.props;
    const editable =
      status !== SERVICE_CONSTANT.ENABLED && status !== SERVICE_CONSTANT.DISABLED_INPROGRESS;
    return [
      {
        name: 'fieldObj',
        width: 150,
        editor: editable && <Select />,
        lock: 'left',
      },
      {
        name: 'fieldType',
        width: 150,
        editor: editable && <Select />,
      },
      {
        name: 'fieldExpr',
        width: 100,
        editor: editable && <TextField />,
      },
      {
        name: 'seqNum',
        width: 100,
        minWidth: 100,
        editor: editable && <NumberField />,
      },
      {
        name: 'fieldDesc',
        editor: editable && <TextField />,
      },
      {
        name: 'privacyLevel',
        width: 120,
        lock: 'right',
        editor: editable && <Select />,
      },
      {
        header: getLang('OPERATOR'),
        align: 'center',
        width: 100,
        lock: 'right',
        renderer: ({ record }) => {
          const actions = [
            record.status === 'add' && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cleanAttr`,
                      type: 'button',
                      meaning: '属性列表-接口配置-属性清空',
                    },
                  ]}
                  onClick={() => this.handleFieldClean(record)}
                  disabled={
                    status === SERVICE_CONSTANT.ENABLED ||
                    status === SERVICE_CONSTANT.DISABLED_INPROGRESS
                  }
                >
                  {getLang('BUTTON_CLEAN')}
                </ButtonPermission>
              ),
              key: 'clean',
              len: 2,
              title: getLang('BUTTON_CLEAN'),
            },
            record.status !== 'add' && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.deleteAttr`,
                      type: 'button',
                      meaning: '属性列表-接口配置-属性删除',
                    },
                  ]}
                  onClick={() => this.handleFieldDelete(record)}
                  disabled={
                    status === SERVICE_CONSTANT.ENABLED ||
                    status === SERVICE_CONSTANT.DISABLED_INPROGRESS
                  }
                >
                  {getLang('BUTTON_DELETE')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: getLang('BUTTON_DELETE'),
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
  }

  @Bind
  getParamColumns(dsType, expType, status) {
    const {
      match: { path },
    } = this.props;
    const editable =
      status !== SERVICE_CONSTANT.ENABLED && status !== SERVICE_CONSTANT.DISABLED_INPROGRESS;
    const columns = [
      {
        name: 'paramName',
        width: 160,
        editor: editable && <TextField />,
        lock: 'left',
      },
      {
        name: 'paramType',
        width: 150,
        editor: editable && <Select />,
      },
      {
        name: 'operatorCode',
        width: 120,
        editor: editable && <Select />,
      },
      {
        name: 'fieldObj',
        width: 150,
        editor: (record) =>
          editable && <Select onChange={(value) => this.handleFieldChange(value, record)} />,
      },
      {
        name: 'seqNum',
        width: 100,
        editor: editable && <NumberField />,
      },
      {
        name: 'requiredFlag',
        width: 100,
        editor: editable && <Switch />,
      },
      {
        name: 'paramDesc',
        width: 180,
        editor: editable && <TextField />,
      },
      {
        name: 'defaultValue',
        width: 120,
        lock: 'right',
        editor: editable && <TextField />,
      },
      {
        header: getLang('OPERATOR'),
        align: 'center',
        width: 100,
        lock: 'right',
        renderer: ({ record }) => {
          const actions = [
            record.status === 'add' && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.cleanParam`,
                      type: 'button',
                      meaning: '参数列表-接口配置-参数清空',
                    },
                  ]}
                  onClick={() => this.handleParamClean(record)}
                  disabled={
                    status === SERVICE_CONSTANT.ENABLED ||
                    status === SERVICE_CONSTANT.DISABLED_INPROGRESS
                  }
                >
                  {getLang('BUTTON_CLEAN')}
                </ButtonPermission>
              ),
              key: 'clean',
              len: 2,
              title: getLang('BUTTON_CLEAN'),
            },
            record.status !== 'add' && {
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.deleteParam`,
                      type: 'button',
                      meaning: '参数列表-接口配置-参数删除',
                    },
                  ]}
                  onClick={() => this.handleParamDelete(record)}
                  disabled={
                    status === SERVICE_CONSTANT.ENABLED ||
                    status === SERVICE_CONSTANT.DISABLED_INPROGRESS
                  }
                >
                  {getLang('BUTTON_DELETE')}
                </ButtonPermission>
              ),
              key: 'delete',
              len: 2,
              title: getLang('BUTTON_DELETE'),
            },
          ];
          return operatorRender(actions);
        },
      },
    ];
    return columns;
  }

  render() {
    const {
      type,
      match,
      currentInterfaceType,
      fetchMappingClassLoading,
      testMappingClassLoading,
      codeArr,
      operatorList,
      assertionSubjects,
    } = this.props;
    const {
      currentHttpConfigList = [],
      isShowModal,
      currentCode,
      isShowHttpModal,
      exprType,
      tableList,
      detailLoading,
      sqlLoading,
      selectedAttrRows,
      selectedParamRows,
      showSqlBtn,
      isHistory,
    } = this.state;
    const record = this.mainConfigFormDS.current;
    const dsType = record.get('dsType');
    const disabledFlag =
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.ENABLED ||
      this.basicFormDS.current.get('status') === SERVICE_CONSTANT.DISABLED_INPROGRESS;
    const { path } = match;
    const httpConfigModalProps = {
      visible: isShowHttpModal,
      dataSource: currentHttpConfigList,
      codeArr,
      isHistory: isHistory || disabledFlag,
      onCreate: this.handleCreateConfigLine,
      onCleanLine: this.handleCleanLine,
      onEditLine: this.handleEditLine,
      onCancel: this.handleCloseHttpModal,
      onOk: this.handleSaveHttpConfig,
      onDelete: this.handleDeleteLine,
      ref: (node) => {
        this.interfaceHttpRef = node;
      },
    };
    const assertionCardProps = {
      operatorList,
      assertionSubjects,
      disabledFlag,
      isHistory,
      basicFormDS: this.basicFormDS,
      onRef: (ref) => {
        this.assertionDS = ref.assertionDS;
      },
    };
    const fieldButtons = disabledFlag
      ? []
      : [
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.createAttr`,
                type: 'button',
                meaning: '服务注册-接口配置-属性新增',
              },
            ]}
            type="c7n-pro"
            key="fieldCreate"
            funcType="flat"
            icon="add"
            disabled={isEmpty(this.mainConfigFormDS.current.get('exprContent'))}
            onClick={() => this.attrListDS.create()}
          >
            {getLang('BUTTON_ADD')}
          </ButtonPermission>,
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.saveAttr`,
                type: 'button',
                meaning: '服务注册-接口配置-属性保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            key="fieldSave"
            funcType="flat"
            icon="save"
            disabled={isEmpty(this.mainConfigFormDS.current.get('exprContent'))}
            onClick={() => this.handleFieldSave()}
          >
            {getLang('SAVE')}
          </ButtonPermission>,
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.batchDeleteAttr`,
                type: 'button',
                meaning: '服务注册-接口配置-属性批量删除',
              },
            ]}
            type="c7n-pro"
            key="batchDelete"
            funcType="flat"
            icon="delete"
            disabled={isEmpty(selectedAttrRows)}
            onClick={() => this.handleFieldBachDelete()}
          >
            {getLang('BUTTON_BATCH_DELETE')}
          </ButtonPermission>,
        ];

    const paramButtons = disabledFlag
      ? []
      : [
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.createParam`,
                type: 'button',
                meaning: '服务注册-接口配置-参数新增',
              },
            ]}
            type="c7n-pro"
            key="paramCreate"
            funcType="flat"
            icon="add"
            disabled={this.attrListDS.length === 0}
            onClick={this.handleParamCreate}
          >
            {getLang('BUTTON_ADD')}
          </ButtonPermission>,
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.saveParam`,
                type: 'button',
                meaning: '服务注册-接口配置-参数保存',
              },
            ]}
            type="c7n-pro"
            color="primary"
            key="paramSave"
            funcType="flat"
            icon="save"
            disabled={this.attrListDS.length === 0}
            onClick={() => this.handleParamSave()}
          >
            {getLang('SAVE')}
          </ButtonPermission>,
          <ButtonPermission
            permissionList={[
              {
                code: `${path}.button.batchDeleteParam`,
                type: 'button',
                meaning: '服务注册-接口配置-参数批量删除',
              },
            ]}
            type="c7n-pro"
            key="batchDelete"
            funcType="flat"
            icon="delete"
            disabled={isEmpty(selectedParamRows)}
            onClick={() => this.handleParamBachDelete()}
          >
            {getLang('BUTTON_BATCH_DELETE')}
          </ButtonPermission>,
        ];

    const isNew = this.basicFormDS.current.status === 'add';
    return (
      <Spin spinning={detailLoading}>
        {/* 抽屉编辑表单 */}
        {/* {this.state.isHaveHistory && (
          <Select
            dataSet={this.historyDS}
            name="historyVersion"
            placeholder={this.state.historyVersion}
            onChange={(value) => this.handleInterfaceHistoryChange({ value })}
          >
            {this.historyDS.toData().map((hv) => {
              return <Select.Option value={hv.version}>{hv.formatVersion}</Select.Option>;
            })}
          </Select>
        )} */}
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{getLang('BASIC_ATTR')}</h3>}
        >
          <Form dataSet={this.basicFormDS} columns={2} labelWidth={130}>
            <TextField name="interfaceCode" disabled={!isNew} />
            <TextField name="interfaceName" disabled={disabledFlag || isHistory} />
            {currentInterfaceType !== 'DS' && currentInterfaceType !== 'COMPOSITE' && (
              <TextField name="interfaceUrl" disabled={disabledFlag || isHistory} />
            )}
            {type === 'SOAP' && (
              <Select
                label={
                  <Tooltip title={getLang('SOAP_VERSION_TOOLTIP')} theme="light">
                    <span>
                      SOAP版本
                      <Icon type="help" />
                    </span>
                  </Tooltip>
                }
                name="soapVersion"
                disabled={disabledFlag || isHistory}
              />
            )}
            {type === 'REST' && (
              <Select name="requestMethod" disabled={disabledFlag || isHistory} />
            )}
            {(type === 'REST' || type === 'SOAP') && (
              <Select name="requestHeader" disabled={disabledFlag || isHistory} />
            )}
            <Select name="publishType" disabled={disabledFlag || isHistory} />
            {type === 'SOAP' && (
              <TextField name="soapAction" disabled={disabledFlag || isHistory} />
            )}
            {type === 'SOAP' && (
              <Switch name="bodyNamespaceFlag" disabled={disabledFlag || isHistory} />
            )}
            {currentInterfaceType !== 'DS' && (
              <Output
                name="mappingClass"
                renderer={() => (
                  <a onClick={this.handleOpenMappingClassModal} disabled={isHistory}>
                    {getLang('VIEW_MAPPING_CLASS')}
                  </a>
                )}
              />
            )}
            <Select name="status" disabled />
            {currentInterfaceType !== 'DS' && (
              <Output
                name="httpConfig"
                renderer={() => (
                  <a onClick={this.handleOpenHttpConfigModal} disabled={isHistory}>
                    {getLang('VIEW_HTTP_CONFIG')}
                  </a>
                )}
              />
            )}
            <Output
              newLine
              name="requestTransformId"
              renderer={() => (
                <a
                  onClick={() => this.handleOpenFieldMappingModal('requestTransformId', 'REQUEST')}
                  disabled={isHistory}
                >
                  {getLang('MAINTAIN_REQUEST_MAPPING')}
                  {/* {isUndefined(value)
                    ? getLang('MAINTAIN_REQUEST_MAPPING')
                    : subRecord.get('requestTransformName')} */}
                </a>
              )}
            />
            <Output
              name="responseTransformId"
              renderer={() => (
                <a
                  onClick={() =>
                    this.handleOpenFieldMappingModal('responseTransformId', 'RESPONSE')
                  }
                  disabled={isHistory}
                >
                  {getLang('MAINTAIN_RESPONSE_MAPPING')}
                  {/* {isUndefined(value)
                    ? getLang('MAINTAIN_RESPONSE_MAPPING')
                    : subRecord.get('responseTransformName')} */}
                </a>
              )}
            />
            <Output
              name="requestCastId"
              renderer={() => (
                <a
                  disabled={isHistory}
                  onClick={() => this.handleOpenDataMappingModal('requestCastId', 'REQUEST')}
                >
                  {getLang('MAINTAIN_REQUEST_DATA_MAPPING')}
                  {/* {isUndefined(value)
                    ? getLang('MAINTAIN_REQUEST_DATA_MAPPING')
                    : subRecord.get('requestCastName')} */}
                </a>
              )}
            />
            <Output
              name="responseCastId"
              renderer={() => (
                <a
                  disabled={isHistory}
                  onClick={() => this.handleOpenDataMappingModal('responseCastId', 'RESPONSE')}
                >
                  {getLang('MAINTAIN_RESPONSE_DATA_MAPPING')}
                  {/* {isUndefined(value)
                    ? getLang('MAINTAIN_RESPONSE_DATA_MAPPING')
                    : subRecord.get('responseCastName')} */}
                </a>
              )}
            />
            <Switch name="asyncFlag" disabled={disabledFlag || isHistory} />
            <TextField disabled name="publishUrl" newLine colSpan={2} />
            {!isNew && <TextField name="formatVersion" disabled />}
            {/* {this.state.isHaveHistory && (
              <Select
                dataSet={this.historyDS}
                name="historyVersion"
                placeholder={this.state.historyVersion}
                onChange={(value) => this.handleInterfaceHistoryChange({ value })}
              >
                {this.historyDS.toData().map((hv) => {
                  return <Select.Option value={hv.version}>{hv.formatVersion}</Select.Option>;
                })}
              </Select>
            )} */}
          </Form>
        </Card>
        {currentInterfaceType === 'DS' && (
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{getLang('MAIN_CONFIG')}</h3>}
          >
            <Form dataSet={this.mainConfigFormDS} columns={2} labelWidth={130}>
              <Lov
                name="dataSourceLov"
                disabled={disabledFlag || isHistory}
                onChange={(value, oldValue) => this.handleSourceLovChange(value, oldValue, record)}
              />
              <Select name="dsType" disabled />
              <Select
                disabled={disabledFlag || isHistory}
                name="exprType"
                onChange={this.handleExpressionTypeChange}
              />
              <TextField disabled={disabledFlag || isHistory} name="remark" />
              {exprType === 'SQL' && (
                <TextArea
                  name="exprContent"
                  resize="vertical"
                  colSpan={2}
                  rows={12}
                  label={getLang('SQL')}
                  help={getLang('SQL_EXTRA')}
                  disabled={disabledFlag || isHistory}
                  onChange={this.handleSqlChange}
                />
              )}
              {exprType === 'DBO' && (
                <Select
                  name="exprContent"
                  disabled={disabledFlag || isHistory}
                  label={getLang('VIEW')}
                  onFocus={() => this.handleTableFocus(record)}
                  onChange={(value, oldValue) => this.handleTableChange(value, oldValue, record)}
                >
                  {tableList.map((item) => (
                    <Select.Option value={item} key={item}>
                      {item}
                    </Select.Option>
                  ))}
                </Select>
              )}
            </Form>
            {exprType === 'SQL' && !disabledFlag && (
              <Row>
                <Col push={3} span={3} style={{ marginLeft: 10 }}>
                  <ButtonPermission
                    permissionList={[
                      {
                        code: `${path}.button.analysis`,
                        type: 'button',
                        meaning: '服务注册-接口配置-解析',
                      },
                    ]}
                    type="c7n-pro"
                    color="primary"
                    loading={sqlLoading}
                    onClick={() => this.handleSqlAnalysis(record)}
                    disabled={showSqlBtn || isHistory}
                  >
                    {getLang('BUTTON_ANALYSIS')}
                  </ButtonPermission>
                </Col>
              </Row>
            )}
          </Card>
        )}
        {currentInterfaceType === 'DS' && (
          <Card
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{getLang('ATTR_LIST')}</h3>}
          >
            <Table
              key="modelField"
              buttons={fieldButtons}
              dataSet={this.attrListDS}
              disabled={isHistory}
              columns={this.getFieldColumns(exprType, this.basicFormDS.current.get('status'))}
              selectionMode={disabledFlag ? 'none' : 'rowbox'}
            />
          </Card>
        )}
        {currentInterfaceType === 'DS' && (
          <Card
            bordered={false}
            className={DETAIL_CARD_TABLE_CLASSNAME}
            title={<h3>{getLang('PARAM_LIST')}</h3>}
          >
            <Table
              key="requestParam"
              buttons={paramButtons}
              dataSet={this.paramListDS}
              disabled={isHistory}
              columns={this.getParamColumns(
                dsType,
                exprType,
                this.basicFormDS.current.get('status')
              )}
              selectionMode={disabledFlag ? 'none' : 'rowbox'}
            />
          </Card>
        )}
        <Card
          bordered={false}
          className={DETAIL_CARD_TABLE_CLASSNAME}
          title={<h3>{getLang('RETRY')}</h3>}
        >
          <Form dataSet={this.retryFormDs} columns={2} labelWidth={130} disabled={disabledFlag}>
            <TextField
              name="retryTimes"
              restrict="0-99"
              addonAfter={getLang('TIMES')}
              disabled={isHistory}
              addonBefore={<Icon type="crop_free" />}
            />
            <TextField
              name="retryInterval"
              restrict="0-99999"
              disabled={isHistory}
              addonAfter={getLang('SECONDS')}
              addonBefore={<Icon type="av_timer" />}
            />
          </Form>
        </Card>
        <Card
          bordered={false}
          className={DETAIL_CARD_TABLE_CLASSNAME}
          title={
            <h3>
              {<QuestionPopover text={getLang('ASSERTION')} message={getLang('ASSERTION_TIP')} />}
            </h3>
          }
        >
          <AssertionCard {...assertionCardProps} />
        </Card>
        <MappingClassModal
          data={currentCode}
          loading={fetchMappingClassLoading}
          testLoading={testMappingClassLoading}
          visible={isShowModal}
          onCancel={this.handleCloseMappingClassModal}
          onTest={this.handleTestMappingClass}
          readOnly={disabledFlag}
        />
        <HttpConfigModal {...httpConfigModalProps} />
      </Spin>
    );
  }
}
