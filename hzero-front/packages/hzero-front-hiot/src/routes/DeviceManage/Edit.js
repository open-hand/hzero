/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-10 14:57:59
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备管理-设备编辑页面
 */
import React from 'react';
import {
  Col,
  Lov,
  Row,
  Form,
  Tabs,
  Modal,
  Table,
  Select,
  Switch,
  Button,
  DataSet,
  TextArea,
  TextField,
  IntlField,
  DatePicker,
  NumberField,
  DateTimePicker,
} from 'choerodon-ui/pro';
import moment from 'moment';
import { isEmpty } from 'lodash';
import queryString from 'query-string';
import { Bind } from 'lodash-decorators';
import { Badge, Tag, Popover, Card, Icon, Divider } from 'choerodon-ui';
import { Tooltip, Icon as HIcon } from 'hzero-ui';

import intl from 'utils/intl';
import { Content } from 'components/Page';
import notification from 'utils/notification';
import webSocketManager from 'utils/webSoket';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import {
  DATA_TYPE,
  DEVICE_CONNECT_TYPE,
  DEVICE_REAL_TIME_TYPE,
  MONITOR_REAL_TIME_TYPE,
} from '@/utils/constants';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import {
  warnInfoDS,
  warnRuleDS,
  alertRuleDS,
  formConfigDS,
  addDataPointDS,
  deviceManageDS,
  pointMonitorDS,
  controlParamDS,
  dataPointInfoDS,
  statusMonitorDS,
  alertRuleShowDS,
  issueTemplateDS,
  editRuleDrawerDS,
  alertRuleServerDS,
  deviceManageSaveDS,
  addDataPointDrawerDS,
} from '@/stores/deviceManageDS';
import Spin from '@/routes/components/loading/Spin';
import { registerInfoDS } from '@/stores/gatewayManageDS';
import { dataPointInfoTableDS } from '@/stores/deviceTemplateDS';
import { tableDS as dataReportDS } from '@/stores/DataReportLogDS';
import { tableDS as directiveIssuedDS } from '@/stores/DirectiveIssuedLogDS';
import CustomBackHeader from '@/routes/components/CustomBackHeader';
import styles from './index.less';
import RuleDrawer from './RuleDrawer';
import MeasurePoint from './Detail/MeasurePoint';
import DataReportDrawer from '../DataReportLog/Drawer';
import DirectiveIssuedDrawer from '../DirectiveIssuedLog/Drawer';

const urlPrefix = '/hiot/device/manage';
const { TabPane } = Tabs;
const { Option } = Select;

@formatterCollections({
  code: ['hiot.dataPointTemplate', 'hiot.iotWarnTemplate', 'hiot.deviceManage', 'hiot.common'],
})
export default class Edit extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {} } = this.props;
    const { deviceId } = params;
    this.state = {
      deviceId,
      thingName: '',
      authType: '',
      chartData: [], // 图表数据
      monitorIndexChart: [], // 监测指标图表数据
      deviceMonitorShowFlag: false, // 当前是否显示了设备监控界面 如果是则隐藏下面的tabs
      visible: false,
      renderList: [], // 指令下发tab页面的表单数据
      configFormData: {}, // 动态表单字段对应数据
      formConfigList: [], // 动态表单数据
    };
    this.warnRuleDS = new DataSet(warnRuleDS());
    this.warnInfoDS = new DataSet(warnInfoDS());
    this.alertRuleDs = new DataSet(alertRuleDS());
    this.pointMonitorDS = new DataSet(pointMonitorDS());
    this.deviceManageDS = new DataSet(deviceManageDS());
    this.controlParamDS = new DataSet(controlParamDS());
    this.dataPointInfoDS = new DataSet(dataPointInfoDS());
    this.statusMonitorDS = new DataSet(statusMonitorDS());
    this.alertRuleShowDs = new DataSet(alertRuleShowDS());
    this.editRuleDrawerDs = new DataSet(editRuleDrawerDS());
    this.dataPointTableDS = new DataSet(dataPointInfoTableDS());
    this.addDataPointDs = new DataSet(addDataPointDS());
    this.alertRuleServerDs = new DataSet(alertRuleServerDS());
    this.addDataPointDrawerDs = new DataSet(addDataPointDrawerDS());
    this.registerInfoDS = new DataSet({
      data: [{ hubType: 'BAIDU' }],
      ...registerInfoDS(),
      autoQuery: false,
    });
    this.directiveIssuedDs = new DataSet({
      ...directiveIssuedDS(),
      autoQuery: false,
      queryFields: directiveIssuedDS().queryFields.filter(
        (item) => item.name !== 'guidLov' && item.name !== 'guid'
      ),
    });
    this.dataReportDs = new DataSet({
      ...dataReportDS(),
      autoQuery: false,
      queryFields: dataReportDS().queryFields.filter(
        (item) => item.name !== 'guidLov' && item.name !== 'guid'
      ),
    });
    this.issueTemplateDs = new DataSet(issueTemplateDS());
    this.formConfigDs = new DataSet({
      ...formConfigDS(),
      feedback: {
        loadSuccess: (data) => {
          if (Array.isArray(data)) {
            data.forEach((item) => {
              const field = {
                name: item.itemCode,
                label: item.itemName,
                type: 'string',
                required: item.requiredFlag === 1,
              };
              this.deviceManageDS.addField(item.itemCode, field);
            });
            this.setState({
              formConfigList: data,
            });
            // const initData = this.deviceManageDS.current.toData();
            // this.deviceManageDS.loadData([initData]);
          }
        },
      },
    });
  }

  componentDidMount() {
    this.handleQuery();
    webSocketManager.addListener(DEVICE_CONNECT_TYPE, this.handleOnlineStatus);
    webSocketManager.addListener(DEVICE_REAL_TIME_TYPE, this.handleChartRealTimeData);
    webSocketManager.addListener(MONITOR_REAL_TIME_TYPE, this.handleMonitorRealTimeData);
  }

  componentWillUnmount() {
    webSocketManager.removeListener(DEVICE_CONNECT_TYPE, this.handleOnlineStatus);
    webSocketManager.removeListener(DEVICE_REAL_TIME_TYPE, this.handleChartRealTimeData);
    webSocketManager.removeListener(MONITOR_REAL_TIME_TYPE, this.handleMonitorRealTimeData);
  }

  /**
   * 查询
   */
  @Bind()
  handleQuery() {
    const { location = {}, workbenchGuid } = this.props;
    const { search = {} } = location;
    const { deviceId } = this.state;
    const { guid } = queryString.parse(search);
    const newGuid = workbenchGuid || guid;
    this.alertRuleDs.thingId = deviceId;
    this.warnInfoDS.setQueryParameter('guid', newGuid);
    this.dataReportDs.setQueryParameter('guid', newGuid);
    this.warnInfoDS.setQueryParameter('thingId', deviceId); // 查询告警情况
    this.controlParamDS.setQueryParameter('thingId', deviceId); // 查询
    this.pointMonitorDS.setQueryParameter('thingId', deviceId);
    this.deviceManageDS.setQueryParameter('thingId', deviceId); // 查询设备详情
    this.alertRuleShowDs.setQueryParameter('thingId', deviceId); // 查询预警规则
    this.dataPointInfoDS.setQueryParameter('thingId', deviceId); // 查询预警规则
    this.statusMonitorDS.setQueryParameter('thingId', deviceId);
    this.addDataPointDs.setQueryParameter('thingId', deviceId);
    this.addDataPointDrawerDs.setQueryParameter('thingId', deviceId);
    this.directiveIssuedDs.setQueryParameter('guid', newGuid);
    this.warnInfoDS.query();
    // this.deviceDetailQuery();
    this.dataReportDs.query();
    this.directiveIssuedDs.query();
    this.alertRuleShowDs.query();
    this.dataPointInfoDS.query();
    this.statusMonitorDS.query();
    // this.controlParamDS.query();
    this.pointMonitorDS.query().then(() => {
      this.setState({ chartData: this.pointMonitorDS.toData() });
    });
    this.formConfigDs.query();
    this.deviceManageDS.query().then((resp) => {
      const {
        status,
        platform,
        thingId,
        thingName,
        connected,
        thingModelId,
        configId,
        authType,
        registerFlag,
        guid: NewGuid,
        thingAttribute = {},
      } = resp;
      const { additionInfo } = thingAttribute;
      let formatData = {};
      if (additionInfo) {
        const other = `${additionInfo}`;
        formatData = JSON.parse(`${other}`);
      }
      this.setState({
        configId,
        status,
        platform,
        authType,
        thingId,
        thingName,
        registerFlag,
        guid: NewGuid,
        configFormData: formatData,
        isOnline: Number(connected) === 1,
      });
      if (registerFlag === 1) {
        this.queryRegisterInfo();
      }

      this.dataPointInfoDS.setQueryParameter('thingModelId', thingModelId); // 查询数据点信息
    });
  }

  /**
   * 查询设备注册信息
   */
  @Bind()
  queryRegisterInfo() {
    const { configId, thingId, platform: hubType } = this.state;
    if (configId) {
      this.registerInfoDS.queryParameter = { configId, thingId, hubType, thingType: 'THING' };
      this.registerInfoDS.query();
    }
  }

  /**
   * 控制参数--提交
   */
  @Bind()
  async handleSubmitControlParams() {
    const res = await this.issueTemplateDs.current.validate(true);
    if (res) {
      const { configId, deviceId } = this.state;
      this.controlParamDS.thingId = deviceId;
      this.controlParamDS.msgTemplateCode = this.issueTemplateDs.current.get('msgTemplateCode');
      this.controlParamDS.map((record) => record.set('configId', configId));
      this.controlParamDS.submit();
    }
  }

  /**
   * 处理实时在线状态显示
   * @param message
   */
  @Bind()
  handleOnlineStatus({ message }) {
    const { guid, connected } = JSON.parse(message);
    const isNotValid = !guid;
    if (isNotValid) return;

    const dsData = this.deviceManageDS.toData()[0];
    const { guid: currGuid } = dsData || {};
    if (guid === currGuid) {
      this.setState({ isOnline: Number(connected) === 1 });
    }
  }

  /**
   * 设备详情更新
   */
  @Bind()
  async handleDeviceManageSave() {
    try {
      const formatData = {};
      const { formConfigList = [] } = this.state;

      let baseInfo = this.deviceManageDS.toData()[0];
      const ruleData = this.alertRuleShowDs.toData();
      const dataPoint = this.dataPointInfoDS.updated.map(({ data }) => data);
      const warnRule = this.warnRuleDS.created.map(({ data }) => data);
      const destroyedData = this.warnRuleDS.destroyed.map(
        ({ data: { predictRuleId } }) => predictRuleId
      );
      if (isEmpty(baseInfo) && isEmpty(dataPoint) && isEmpty(warnRule) && isEmpty(destroyedData)) {
        return;
      }
      if (formConfigList.length) {
        formConfigList.forEach((item) => {
          formatData[item.itemCode] = baseInfo[item.itemCode];
        });

        baseInfo = {
          ...baseInfo,
          additionInfo: JSON.stringify(formatData),
        };
      }

      const deviceManageSaveDSObj = new DataSet(deviceManageSaveDS());
      deviceManageSaveDSObj.create(
        {
          thing: {
            ...baseInfo,

            buyingTime:
              baseInfo &&
              baseInfo.buyingTime &&
              moment(baseInfo.buyingTime).format('YYYY-MM-DD HH:mm:ss'),
          },
          updateProperties: [], // 数据点信息更新
          relList: ruleData,
        },
        0
      );
      const validate = await this.deviceManageDS.validate();

      if (validate) {
        const res = await deviceManageSaveDSObj.submit();
        if (!res) {
          return false;
        }
        this.props.history.push('/hiot/device/manage/list');
      }
    } catch (err) {
      // const errTitle = intl.get('hzero.common.notification.error').d('操作失败');
      // notification.error({ message: `${errTitle}:${err.message}` });
    }
  }

  /**
   * 跳转到数据点编辑页面
   */
  @Bind()
  handleEdit(pointId, record) {
    const { deviceId } = this.state;
    const deviceInfo = this.deviceManageDS.toData()[0];
    this.props.history.push({
      pathname: `/hiot/device/manage/edit/data-point/${deviceId}/${pointId}`,
      state: { dataPoint: { ...record, propertyId: record.propertyId }, deviceInfo },
    });
  }

  // 数据点信息的表格列
  get dataPointInfoColumns() {
    return [
      { name: 'propertyCode', width: 150 },
      { name: 'guid', width: 150 },
      { name: 'propertyName', width: 150 },
      { name: 'categoryMeaning' },
      { name: 'dataTypeMeaning' },
      { name: 'unitCode' },
      { name: 'minValue' },
      { name: 'maxValue' },
      { name: 'reportInterval' },
      {
        name: 'operation',
        width: 80,
        lock: 'right',
        renderer: ({ record }) => {
          const { category } = record.toData();
          return isEmpty(category) ? (
            ''
          ) : (
            <div>
              <a onClick={() => this.handleEdit(record.get('propertyId'), record.toData())}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            </div>
          );
        },
      },
    ];
  }

  /**
   * 删除预警规则或者编辑预警规则
   * @param record
   */
  @Bind()
  async handleWarnRuleOption(record) {
    await this.alertRuleShowDs.delete(record);
    this.alertRuleShowDs.query();
  }

  // 预警规则的表格列
  get warnRuleColumns() {
    return [
      { name: 'alertCode', width: 200 },
      { name: 'alertName', width: 200 },
      {
        name: 'itemMappingList',
        align: 'left',
        renderer: ({ record }) => {
          const { itemMappingList } = record.toData();
          return (
            <span>
              {itemMappingList &&
                itemMappingList.map((item) => {
                  const { sourceCode } = item;
                  return sourceCode ? <Tag color="geekblue">{sourceCode}</Tag> : '';
                })}
            </span>
          );
        },
      },
      { name: 'alertLevelMeaning' },
      {
        name: 'sourceTypeMeaning',
        width: 120,
        align: 'left',
      },
      {
        name: 'targetTypeList',
        width: 280,
        align: 'left',
        renderer: ({ record }) => {
          const { targetTypeList } = record.toData();
          return (
            <span>
              {targetTypeList &&
                targetTypeList.map((item) => {
                  let color = 'color';
                  let label = '';
                  switch (item) {
                    case 'MQ':
                      color = 'gold';
                      label = intl
                        .get('hiot.deviceManage.view.deviceManage.messageMiddleware')
                        .d('消息中间件');
                      break;
                    case 'API':
                      color = 'purple';
                      label = intl.get('hiot.deviceManage.view.deviceManage.api').d('API回调');
                      break;
                    case 'MSG':
                      color = 'magenta';
                      label = intl
                        .get('hiot.deviceManage.view.deviceManage.messagePlatform')
                        .d('消息平台');
                      break;
                    default:
                      return;
                  }
                  return <Tag color={color}>{label}</Tag>;
                })}
            </span>
          );
        },
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 120,
        lock: 'right',
        renderer: ({ record }) => (
          <span className="action-link">
            <a onClick={() => this.handleWarnRuleOption(record)}>
              {intl.get('hzero.common.button.delete').d('删除')}
            </a>
            <a onClick={() => this.handleWarnRule(false, record)}>
              {intl.get('hzero.common.button.edit').d('编辑')}
            </a>
          </span>
        ),
      },
    ];
  }

  /**
   * 判断指定数据不存在于本地列表
   * @param data
   * @returns {boolean}
   */
  @Bind
  dataIsNotLocal(data) {
    return this.warnRuleDS.toData().filter(({ predictCode }) => predictCode === data).length === 0;
  }

  @Bind()
  editWarningRule(oldRecord) {
    if (this.warningRule.validateExpression() === false) {
      return false;
    }
    const {
      state: { expression, expressionFactorList },
    } = this.warningRule;
    const { formularJson } = oldRecord.data;
    const newExpression = JSON.stringify(expressionFactorList);
    if (formularJson !== newExpression) {
      const { formulaStr, dataPointsStr } = expression;
      oldRecord.set('formularJson', newExpression);
      oldRecord.set('formular', formulaStr);
      oldRecord.set('propertyNames', dataPointsStr);
    }
  }

  /**
   * 打开新建、编辑预警规则Modal
   */
  @Bind()
  async handleWarnRule(isCreate, record) {
    const modalKey = Modal.key();
    this.editRuleDrawerDs.create({});
    const data = record && record.toData();
    const ruleProps = {
      data,
      isCreate,
      alertRuleDs: this.alertRuleDs,
      editRuleDrawerDs: this.editRuleDrawerDs,
    };
    Modal.open({
      key: modalKey,
      title: !isCreate
        ? intl.get(`hiot.deviceManage.view.temp.edit.warn-rule`).d('编辑预警规则')
        : intl.get('hiot.deviceManage.view.new.warn.rule').d('新建预警规则'),
      style: { width: '700px ' },
      destroyOnClose: true,
      closable: true,
      children: <RuleDrawer {...ruleProps} />,
      onOk: () => this.handleRuleSave(isCreate, record),
      onCancel: () => {
        this.editRuleDrawerDs.removeAll();
        this.alertRuleDs.loadData([]);
      },
      onClose: () => {
        this.editRuleDrawerDs.removeAll();
        this.alertRuleDs.loadData([]);
      },
    });
  }

  /**
   * 对于设备进行注册、停用注册操作
   * @param thingId
   * @param hubType
   * @param isRegistered
   * @param configId
   */
  @Bind()
  handleRegister(thingId, hubType, isRegistered, configId) {
    const thingType = 'THING';
    this.registerInfoDS.queryParameter = {
      thingId,
      hubType,
      thingType,
      flag: isRegistered,
      configId,
    };
    // 执行注册或停止注册操作
    this.registerInfoDS.query().then((res) => {
      this.iotKeyParse(res);
      notification.success(intl.get('hzero.common.notification.success').d('操作成功'));
      this.deviceDetailQuery();
    });
  }

  /**
   * 从后端接口返回数据中解析出IoT Key
   * @param response
   */
  @Bind()
  iotKeyParse(response) {
    const { addition } = response;
    const { key } = (addition && JSON.parse(addition)) || {};
    this.registerInfoDS.create({ ...response, key }, 0);
  }

  /**
   * 设备详情查询
   */
  @Bind()
  deviceDetailQuery() {
    const { deviceId } = this.state;
    this.deviceManageDS.setQueryParameter('thingId', deviceId); // 查询设备详情
    this.deviceManageDS.query().then((resp) => {
      const {
        status,
        platform,
        thingId,
        thingName,
        connected,
        configId,
        guid,
        registerFlag,
      } = resp;
      this.setState({
        configId,
        status,
        platform,
        thingId,
        thingName,
        guid,
        isOnline: Number(connected) === 1,
      });
      if (registerFlag) {
        this.queryRegisterInfo();
      }
    });
  }

  // 保存预警规则
  @Bind()
  async handleRuleSave(isCreate, record) {
    try {
      const validate = await this.editRuleDrawerDs.current.validate();
      if (!validate) {
        return false;
      }
      if (this.alertRuleDs.current) {
        const ruleValidate = await this.alertRuleDs.current.validate('all');
        if (!ruleValidate) {
          notification.error({
            message: intl
              .get('hiot.deviceManage.view.validation.require-ruleTableMsg')
              .d('请输入参数类别和来源标识'),
          });
          return false;
        }
      }
    } catch {
      return false;
    }
    const { __dirty, ...other } = this.editRuleDrawerDs.current.toData() || {};
    const { deviceId } = this.state;
    const listData = this.alertRuleDs.toData() || [];
    const list = listData.map((item) => {
      const { sourceLov, ...otherItem } = item;
      return otherItem;
    });
    const existingData = this.alertRuleShowDs.toData().map((item) => item.alertCode);
    if (isCreate) {
      let newData = {
        ...other,
        thingId: deviceId,
        itemMappingList: list,
      };
      if (list[0]) {
        const { sourceTypeCode, targetTypeList, sourceTypeMeaning } = list[0];
        newData = {
          ...newData,
          sourceTypeCode,
          sourceTypeMeaning,
          targetTypeList,
        };
      }
      if (existingData.indexOf(newData.alertCode) === -1) {
        this.alertRuleServerDs.create(newData);
        await this.alertRuleServerDs.submit();
        this.alertRuleShowDs.query();
      } else {
        notification.error({
          message: intl
            .get('hiot.deviceManage.view.validation.creatRuleTableMsg')
            .d('设备已分配该规则'),
        });
      }
    } else {
      record.set('itemMappingList', list);
      await this.alertRuleShowDs.submit();
      this.alertRuleShowDs.query();
    }
  }

  @Bind()
  onLovChange(value) {
    if (value) {
      this.controlParamDS.setQueryParameter('templateId', value.templateId);
      this.controlParamDS.query().then((res) => {
        if (res && !res.failed) {
          this.setState({
            renderList: res,
          });
        }
      });
    } else {
      this.setState({
        renderList: [],
      });
      this.controlParamDS.loadData([]);
    }
  }

  /**
   * 渲染告警规则搜索框
   */
  @Bind()
  renderWarnRuleBar() {
    return (
      <ButtonPermission
        type="c7n-pro"
        permissionList={[
          {
            code: `${this.props.match.path}.allotRule`,
            type: 'button',
            meaning: '设备管理-分配规则',
          },
        ]}
        icon="box"
        onClick={() => {
          this.handleWarnRule(true);
        }}
        style={{ marginBottom: 10 }}
      >
        {intl.get('hiot.deviceManage.view.button.allotRule').d('分配规则')}
      </ButtonPermission>
    );
  }

  /**
   * 修改当前设备监控显示状态
   * @param activeKey
   */
  @Bind()
  changeShowFlag(activeKey) {
    this.setState({
      deviceMonitorShowFlag: activeKey !== 'deviceDetail',
    });
  }

  /**
   * 设备监控tab页
   * @returns {*}
   */
  @Bind()
  renderDeviceMonitorTab() {
    const { chartData } = this.state;
    return (
      <div style={{ marginLeft: 40 }}>
        <div>
          <div className={styles['monitor-title']}>
            <span>{intl.get('hiot.gatewayManage.view.title.pointMonitor').d('测量点监控')}</span>
          </div>
          <div style={{ display: 'flex', flexWrap: 'wrap' }}>
            {chartData.map((record) => (
              <MeasurePoint key={record.guid} {...record} />
            ))}
          </div>
        </div>
        <div>
          <div className={styles['monitor-title']}>
            <span>{intl.get('hiot.gatewayManage.view.title.statusMonitor').d('状态点监控')}</span>
          </div>
          <div className={styles['status-container']}>
            {this.statusMonitorDS.map((record) => {
              // const dataId = record.get('dataId');
              const dataName = record.get('propertyName');
              const color = record.get('color') || 'gray';
              const name = record.get('name');
              const propertyNameShow = <span className={styles['status-text']}>{dataName}</span>;
              const guid = record.get('guid');
              return (
                <div key={guid} className={styles['status-container']}>
                  <span className={styles['status-circle']} style={{ backgroundColor: color }} />
                  {name ? <Popover content={name}>{propertyNameShow}</Popover> : propertyNameShow}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    );
  }

  // 指令下发tab页
  @Bind()
  renderDirectiveIssuedTab() {
    const { configId, renderList } = this.state;
    const { workbenchGuid, isButton = true } = this.props;
    return (
      <div style={{ marginLeft: 40 }}>
        <div className={styles['monitor-wrap']}>
          <div>
            <div className={styles['monitor-title']}>
              <span>{intl.get('hiot.gatewayManage.view.title.controlParam').d('控制参数')}</span>
            </div>
            <Row>
              <Col span={18}>
                <Form dataSet={this.issueTemplateDs} columns={2} labelWidth={200}>
                  <Lov name="templateLov" onChange={this.onLovChange} />
                </Form>
              </Col>
            </Row>
            <Row>
              <Col span={18}>
                <Divider orientation="left">
                  {intl.get('hiot.DeviceSimulation.view.title.dataPointText').d('数据点')}
                </Divider>
              </Col>
            </Row>
            <Row>
              <Col span={18}>
                {!isEmpty(renderList) ? (
                  <Form dataSet={this.issueTemplateDs} columns={2} labelWidth={200}>
                    {this.renderControlParamsForm()}
                  </Form>
                ) : (
                  <h3 style={{ color: 'gray', textAlign: 'center' }}>
                    {intl.get('hiot.DeviceSimulation.view.button.NoDataPoint').d('暂无数据点')}
                  </h3>
                )}
              </Col>
            </Row>
          </div>
          {!workbenchGuid && isButton && (
            <div style={{ marginTop: 40, marginLeft: '40%' }}>
              <Button onClick={this.handleResetControlParams}>
                {intl.get('hzero.common.button.reset').d('重置')}
              </Button>
              <Button disabled={!configId} color="primary" onClick={this.handleSubmitControlParams}>
                {intl.get('hzero.common.button.submit').d('提交')}
              </Button>
            </div>
          )}
        </div>
      </div>
    );
  }

  /**
   * 渲染控制参数的表单 通过dataType字段来判断使用哪种表单
   */
  @Bind()
  renderControlParamsForm() {
    return this.controlParamDS.map((record) => {
      const { dataType, minValue, maxValue, propertyName, guid } = record.toData();
      let options = record.get('options');
      let paramItem;
      let checked = intl.get('hiot.common.view.checkbox.open').d('开');
      let unChecked = intl.get('hiot.common.view.checkbox.close').d('关');
      switch (dataType) {
        case DATA_TYPE.BOOL:
          try {
            options = JSON.parse(options);
          } catch (e) {
            options = [];
          }
          if (!isEmpty(options)) {
            checked = options[1].name;
            unChecked = options[0].name;
            this.checkedValue = Number(options[1].code);
          }
          paramItem = (
            <Switch
              name="value"
              label={propertyName}
              checkedChildren={checked}
              unCheckedChildren={unChecked}
              record={record}
            />
          );
          break;
        case DATA_TYPE.NUMBER:
          paramItem = (
            <NumberField
              key={guid}
              record={record}
              name="value"
              label={propertyName}
              max={maxValue ? Number(maxValue) : undefined}
              min={minValue ? Number(minValue) : undefined}
              step={record.get('step') || 1}
              addonAfter={record.get('unitCodeMeaning')}
            />
          );
          break;
        case DATA_TYPE.ENUM:
          paramItem = (
            <Select key={guid} record={record} label={propertyName} name="value">
              {options &&
                JSON.parse(options).map(({ code, name }) => (
                  <Option key={code} value={code}>
                    {name}
                  </Option>
                ))}
            </Select>
          );
          break;
        case DATA_TYPE.DATE:
          paramItem = <DatePicker key={guid} label={propertyName} name="value" record={record} />;
          break;
        case DATA_TYPE.DATE_TIME:
          paramItem = (
            <DateTimePicker key={guid} label={propertyName} name="value" record={record} />
          );
          break;
        default:
          break;
      }
      return paramItem;
    });
  }

  /**
   * 控制参数--重置
   */
  @Bind()
  handleResetControlParams() {
    this.controlParamDS.reset();
  }

  /**
   * 新建数据点
   */
  @Bind()
  handleAddDataPoint() {
    this.addDataPointDrawerDs.query();
    Modal.open({
      key: 'addDataPoint',
      title: intl.get('hiot.deviceManage.view.button.addDataPoint').d('添加数据点'),
      // style: { width: '700px ' },
      destroyOnClose: true,
      closable: true,
      children: (
        <Table
          dataSet={this.addDataPointDrawerDs}
          columns={this.dataPointColumns}
          queryFieldsLimit={2}
        />
      ),
      onOk: this.handleSaveDataPoint,
      onCancel: () => {
        this.addDataPointDrawerDs.clearCachedSelected();
        this.addDataPointDrawerDs.unSelectAll();
      },
      onClose: () => {
        this.addDataPointDrawerDs.clearCachedSelected();
        this.addDataPointDrawerDs.unSelectAll();
      },
    });
  }

  @Bind()
  async handleSaveDataPoint() {
    const list = this.addDataPointDrawerDs.selected;
    if (list.length) {
      const propertyModelIds = list.map((item) => item.get('propertyModelId'));
      this.addDataPointDs.setQueryParameter(
        'platform',
        this.deviceManageDS.current.get('platform')
      );
      this.addDataPointDs.create(
        {
          propertyModelIds,
        },
        0
      );
      const res = await this.addDataPointDs.submit();
      if (res) {
        this.dataPointInfoDS.query();
      }
    }
  }

  /**
   * 告警情况的表格列
   * @returns {*[]}
   */
  get warnInfoColumns() {
    return [
      { name: 'thingName' },
      { name: 'guid' },
      { name: 'alertCode' },
      { name: 'alertLevelMeaning', width: 100 },
      { name: 'eventTime' },
      {
        name: 'recoveredFlagMeaning',
        width: 100,
        align: 'center',
        renderer: ({ value, record }) => (
          <Badge status={record.get('recoveredFlag') === 1 ? 'success' : 'error'} text={value} />
        ),
      },
    ];
  }

  get dataPointColumns() {
    return [{ name: 'propertyModelCode' }, { name: 'propertyModelName' }];
  }

  // 查看日志详情
  @Bind()
  handleLogDetail(record, isDataReport) {
    const currentEditData = record && record.toData();
    Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 650 },
      title: intl.get('hzero.common.status.detail').d('查看详情'),
      children: isDataReport ? (
        <DataReportDrawer currentEditData={currentEditData} />
      ) : (
        <DirectiveIssuedDrawer currentEditData={currentEditData} />
      ),
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  }

  get directiveIssuedColumns() {
    return [
      { name: 'serviceInstIp' },
      { name: 'topicName' },
      {
        name: 'platformMeaning',
      },
      {
        name: 'deviceName',
        header: intl.get('hiot.common.device.name').d('设备名称'),
      },
      {
        name: 'deviceCode',
        header: intl.get('hiot.common.device.code').d('设备编码'),
      },
      {
        name: 'processStatus',
        width: 100,
        align: 'center',
        renderer: ({ value }) => (
          <Badge
            status={value === '1' ? 'success' : 'error'}
            text={
              value === '1'
                ? intl.get('hzero.common.status.success').d('成功')
                : intl.get('hzero.common.status.error').d('失败')
            }
          />
        ),
      },
      { name: 'creationDate' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <a
                onClick={() => {
                  this.handleLogDetail(record, false);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ];
  }

  get dataReportColumns() {
    return [
      { name: 'serviceInstIp', width: 200 },
      { name: 'topicName' },
      {
        name: 'deviceName',
        header: intl.get('hiot.common.device.name').d('设备名称'),
      },
      {
        name: 'deviceCode',
        header: intl.get('hiot.common.device.code').d('设备编码'),
      },
      {
        name: 'processStatus',
        width: 100,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={value === '1' ? 'success' : 'error'}
            text={
              value === '1'
                ? intl.get('hzero.common.status.success').d('成功')
                : intl.get('hzero.common.status.error').d('失败')
            }
          />
        ),
      },
      { name: 'creationDate' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <a
                onClick={() => {
                  this.handleLogDetail(record, true);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ];
  }

  @Bind()
  handleVisible() {
    const { visible } = this.state;
    this.setState({
      visible: !visible,
    });
  }

  render() {
    const {
      deviceMonitorShowFlag,
      thingName,
      isOnline,
      status,
      authType,
      platform,
      thingId,
      configId,
      visible,
      renderList,
      formConfigList = [],
    } = this.state;
    const {
      mode,
      match: { path },
    } = this.props;
    return (
      <>
        <CustomBackHeader
          title={
            <>
              <span>{thingName}</span>
              {isOnline ? (
                <span className={styles['device-status']} style={{ background: '#4caf82' }}>
                  {intl.get('hiot.common.view.title.online').d('在线')}
                </span>
              ) : (
                <span className={styles['device-status']} style={{ background: '#f5554a' }}>
                  {intl.get('hiot.common.view.title.offline').d('离线')}
                </span>
              )}
            </>
          }
          backPath={urlPrefix}
        >
          {mode ? (
            ''
          ) : (
            <>
              <Button color="primary" icon="save" onClick={this.handleDeviceManageSave}>
                {intl.get('hzero.common.button.save').d('保存')}
              </Button>
              <Spin dataSet={this.registerInfoDS}>
                <Button
                  onClick={() =>
                    this.handleRegister(thingId, platform, status === 'NON_REGISTERED', configId)
                  }
                >
                  {status === 'NON_REGISTERED'
                    ? intl.get('hiot.gatewayManage.button.startRegister').d('启用注册')
                    : intl.get('hiot.common.button.stopRegister').d('停用注册')}
                </Button>
              </Spin>
            </>
          )}
        </CustomBackHeader>
        <Content>
          <Tabs
            defaultActiveKey="deviceDetail"
            onChange={(activeKey) => this.changeShowFlag(activeKey)}
          >
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.deviceDetail').d('设备详情')}
              key="deviceDetail"
            >
              <Row>
                <Col span={16}>
                  <Spin dataSet={this.deviceManageDS}>
                    <Form dataSet={this.deviceManageDS} columns={2}>
                      <IntlField name="thingName" />
                      <TextField name="thingCode" disabled />
                      <TextField name="guid" disabled />
                      <TextField name="version" disabled />
                      <TextField name="name" disabled />
                      <TextField name="thingModelName" disabled />
                      <TextField name="categoryMeaning" disabled />
                      <TextArea name="description" rowSpan={2} />
                    </Form>
                    <Card
                      bordered={false}
                      className={DETAIL_CARD_CLASSNAME}
                      title={
                        <>
                          <h3>
                            {intl.get('hiot.deviceManage.view.title.attribute').d('设备属性')}
                            <a onClick={this.handleVisible} style={{ float: 'right' }}>
                              {visible ? (
                                <>
                                  {intl.get('hzero.common.button.up').d('收起')}
                                  <Icon type="expand_less" />
                                </>
                              ) : (
                                <>
                                  {intl.get('hzero.common.button.expand').d('展开')}
                                  <Icon type="expand_more" />
                                </>
                              )}
                            </a>
                          </h3>
                        </>
                      }
                    >
                      {visible && (
                        <Form dataSet={this.deviceManageDS} columns={2}>
                          <TextField name="equipment" />
                          <TextField name="manufacturer" />
                          <DateTimePicker name="buyingTime" />
                          <TextField
                            name="longitude"
                            label={
                              <>
                                {intl.get(`hiot.common.model.device.longitude`).d('经度')}
                                <Tooltip
                                  title={intl
                                    .get('hiot.common.view.message.title.longitude.tooptip')
                                    .d('负坐标代表西经，正坐标代表东经')}
                                >
                                  <HIcon type="question-circle" />
                                </Tooltip>
                              </>
                            }
                            min={-180}
                            max={180}
                          />
                          <TextField
                            name="latitude"
                            min={-90}
                            max={90}
                            label={
                              <>
                                {intl.get(`hiot.common.model.device.latitude`).d('纬度')}
                                <Tooltip
                                  title={intl
                                    .get('hiot.common.view.message.title.latitude.tooptip')
                                    .d('负坐标代表南纬，正坐标代表北纬')}
                                >
                                  <HIcon type="question-circle" />
                                </Tooltip>
                              </>
                            }
                          />
                          {formConfigList.map((item) => (
                            <TextField name={item.itemCode} />
                          ))}
                        </Form>
                      )}
                    </Card>
                  </Spin>
                </Col>
              </Row>
            </TabPane>
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.authInfo').d('认证信息')}
              key="authInfo"
            >
              <Row>
                <Col span={16}>
                  <Form dataSet={this.deviceManageDS} columns={2} labelWidth={110}>
                    <TextField name="platformMeaning" disabled />
                    <TextField name="configName" disabled />
                  </Form>
                  {(platform === 'ALI' || platform === 'BAIDU') && (
                    <Form dataSet={this.deviceManageDS} columns={2} labelWidth={110}>
                      {platform === 'ALI' && <TextField name="authType" disabled />}
                      {platform === 'ALI' && <TextField name="deviceName" disabled />}
                      {platform === 'ALI' && <TextField name="deviceSecret" disabled />}
                      {platform === 'ALI' && <TextField name="productKey" disabled />}
                      <TextField name="username" disabled />
                      <TextField name="password" disabled />
                      <TextField name="protocol" disabled />
                      <TextField name="port" disabled />
                      <TextField name="clientId" disabled />
                      <TextField name="brokerAddress" disabled />
                    </Form>
                  )}
                  {platform === 'OWN' && (
                    <Form dataSet={this.deviceManageDS} columns={2} labelWidth={110}>
                      <TextField name="authType" disabled />
                      {authType === 'REDIS' && <TextField name="username" disabled />}
                      {authType === 'REDIS' && <TextField name="password" disabled />}
                      {authType === 'JWT' && <TextField name="secret" disabled />}
                      {authType === 'JWT' && <Select name="from" disabled />}
                    </Form>
                  )}
                </Col>
              </Row>
            </TabPane>

            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.deviceMonitor').d('设备监控')}
              key="deviceMonitor"
            >
              {this.renderDeviceMonitorTab()}
            </TabPane>
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.directiveIssued').d('指令下发')}
              key="directiveIssued"
            >
              {renderList && this.renderDirectiveIssuedTab()}
            </TabPane>
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.alarmCondition').d('告警情况')}
              key="alarmCondition"
            >
              <Table
                selectionMode="click"
                dataSet={this.warnInfoDS}
                columns={this.warnInfoColumns}
                queryFieldsLimit={3}
              />
            </TabPane>
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.logMonitoring').d('日志监控')}
              key="logMonitoring"
            >
              <Tabs defaultActiveKey="dataReport">
                <TabPane
                  tab={intl.get('hiot.deviceManage.view.title.dataReport').d('数据上报')}
                  key="dataReport"
                >
                  <Table
                    selectionMode="click"
                    dataSet={this.dataReportDs}
                    columns={this.dataReportColumns}
                    queryFieldsLimit={3}
                  />
                </TabPane>
                <TabPane
                  tab={intl.get('hiot.deviceManage.view.title.instructIssued').d('指令下发')}
                  key="instructIssued"
                >
                  <Table
                    selectionMode="click"
                    dataSet={this.directiveIssuedDs}
                    columns={this.directiveIssuedColumns}
                    queryFieldsLimit={3}
                  />
                </TabPane>
              </Tabs>
            </TabPane>
            <TabPane
              tab={intl.get('hiot.common.view.registerInfo').d('注册信息')}
              key="registerInfo"
              disabled={status === 'NON_REGISTERED'}
            >
              <Row>
                <Col span={16}>
                  <Form dataSet={this.registerInfoDS} columns={2}>
                    <TextField name="platformMeaning" disabled />
                    <TextField name="configName" disabled />
                    <TextField name="upTopic" disabled />
                    <TextField name="downTopic" disabled />
                  </Form>
                </Col>
              </Row>
            </TabPane>
          </Tabs>
          <Tabs
            defaultActiveKey="dataPointInfo"
            style={{ display: deviceMonitorShowFlag ? 'none' : 'block' }}
          >
            <TabPane
              tab={intl.get('hiot.common.view.data.point.info').d('数据点信息')}
              key="dataPointInfo"
            >
              {platform === 'OWN' && status === 'NON_REGISTERED' && (
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.createDataPoint`,
                      type: 'button',
                      meaning: '设备管理-添加数据点信息',
                    },
                  ]}
                  icon="add"
                  onClick={this.handleAddDataPoint}
                  style={{ marginBottom: 10 }}
                >
                  {intl.get('hiot.deviceManage.view.button.addDataPoint').d('添加数据点')}
                </ButtonPermission>
              )}
              <Table
                // mode="tree"
                selectionMode="click"
                dataSet={this.dataPointInfoDS}
                columns={this.dataPointInfoColumns}
                buttons={this.tableButtons}
              />
            </TabPane>
            <TabPane tab={intl.get('hiot.common.warn.rule').d('预警规则')} key="warnRule">
              <Table
                selectionMode="click"
                dataSet={this.alertRuleShowDs}
                columns={this.warnRuleColumns}
                queryBar={this.renderWarnRuleBar}
                filterBarFieldName="warnAlarmRule"
              />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
