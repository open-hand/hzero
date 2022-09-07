/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-10 10:36:11
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备管理-设备详情界面
 */
import React from 'react';
import {
  Button,
  Col,
  DataSet,
  DateTimePicker,
  Form,
  Lov,
  Row,
  Modal,
  Select,
  Table,
  Tabs,
  TextArea,
  TextField,
} from 'choerodon-ui/pro';
import { Popover, Tag, Badge, Card, Icon } from 'choerodon-ui';
import { Tooltip as HTooltip, Icon as HIcon } from 'hzero-ui';

import { Bind } from 'lodash-decorators';
import queryString from 'query-string';

import { Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import webSocketManager from 'utils/webSoket';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import {
  dataPointInfoDS,
  deviceManageDS,
  pointMonitorDS,
  statusMonitorDS,
  warnInfoDS,
  alertRuleDS,
  alertRuleShowDS,
  editRuleDrawerDS,
  formConfigDS,
} from '@/stores/deviceManageDS';
import {
  DEVICE_CONNECT_TYPE,
  DEVICE_REAL_TIME_TYPE,
  EDIT_ACTION,
  MONITOR_REAL_TIME_TYPE,
} from '@/utils/constants';
import Spin from '@/routes/components/loading/Spin';
import { registerInfoDS } from '@/stores/gatewayManageDS';
import { tableDS as dataReportDS } from '@/stores/DataReportLogDS';
import { tableDS as directiveIssuedDS } from '@/stores/DirectiveIssuedLogDS';
import CustomBackHeader from '@/routes/components/CustomBackHeader';
import styles from './index.less';
import RuleDrawer from './RuleDrawer';
import MeasurePoint from './Detail/MeasurePoint';
import DataReportDrawer from '../DataReportLog/Drawer';
import DirectiveIssuedDrawer from '../DirectiveIssuedLog/Drawer';

const { TabPane } = Tabs;
const urlPrefix = '/hiot/device/manage';
const orgId = getCurrentOrganizationId();

@formatterCollections({
  code: ['hiot.dataPointTemplate', 'hiot.deviceManage', 'hiot.iotWarnEvent', 'hiot.common'],
})
export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {}, workbenchDeviceId } = this.props;
    const { deviceId } = params;
    this.deviceManageDS = new DataSet(deviceManageDS());
    this.dataPointInfoDS = new DataSet(dataPointInfoDS());
    this.alertRuleShowDs = new DataSet(alertRuleShowDS());
    this.warnInfoDS = new DataSet(warnInfoDS());
    this.alertRuleDs = new DataSet(alertRuleDS());
    this.statusMonitorDS = new DataSet(statusMonitorDS());
    this.pointMonitorDS = new DataSet(pointMonitorDS());
    this.editRuleDrawerDs = new DataSet(editRuleDrawerDS());
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
    this.state = {
      deviceId: deviceId || workbenchDeviceId,
      thingName: '',
      authType: '',
      chartData: [], // 图表数据
      monitorIndexChart: [], // 监测指标图表数据
      // thingModelId: -1,
      deviceMonitorShowFlag: false, // 当前是否显示了设备监控界面 如果是则隐藏下面的tabs
      visible: false,
      bMap: false, // 是否由设备地图跳转过来
      formConfigList: [],
    };
  }

  componentDidMount() {
    const { onRef, location = {}, workbenchGuid, match: { params = {} } = {} } = this.props;
    const { deviceId } = params;
    const { search = {} } = location;
    const { guid } = queryString.parse(search);
    const newGuid = workbenchGuid || guid;
    this.alertRuleDs.thingId = deviceId;
    this.warnInfoDS.setQueryParameter('guid', newGuid);
    this.dataReportDs.setQueryParameter('guid', newGuid);
    this.directiveIssuedDs.setQueryParameter('guid', newGuid);
    this.formConfigDs.query();
    if (onRef) {
      onRef(this);
    }
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
   * 处理查询
   */
  @Bind()
  handleQuery() {
    const { location = {} } = this.props;
    const { search = {} } = location;
    const { deviceId } = this.state;
    const { bMap = false } = queryString.parse(search);
    this.warnInfoDS.setQueryParameter('thingId', deviceId); // 查询告警情况
    this.statusMonitorDS.setQueryParameter('thingId', deviceId);
    this.dataPointInfoDS.setQueryParameter('thingId', deviceId);
    this.pointMonitorDS.setQueryParameter('thingId', deviceId);
    this.alertRuleShowDs.setQueryParameter('thingId', deviceId);
    this.deviceDetailQuery();
    this.alertRuleShowDs.query();
    this.warnInfoDS.query();
    this.dataPointInfoDS.query();
    this.statusMonitorDS.query();
    this.dataReportDs.query();
    this.directiveIssuedDs.query();
    this.registerInfoDS = new DataSet({
      data: [{ hubType: 'BAIDU' }],
      ...registerInfoDS(),
    });
    this.pointMonitorDS.query().then(() => {
      this.setState({ chartData: this.pointMonitorDS.toData() });
    });
    this.setState({ bMap });
  }

  /**
   * 设备详情查询
   */
  @Bind()
  deviceDetailQuery() {
    const { deviceId } = this.state;
    this.deviceManageDS.setQueryParameter('thingId', deviceId); // 查询设备详情
    this.deviceManageDS.query().then((resp) => {
      const { status, platform, thingId, thingName, connected, configId, authType } = resp;
      this.setState(
        {
          configId,
          status,
          platform,
          thingId,
          authType,
          thingName,
          isOnline: Number(connected) === 1,
        },
        this.queryRegisterInfo
      );
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
   * 处理图表实时数据
   * @param message
   */
  @Bind()
  handleChartRealTimeData({ message }) {
    const { thing: { guid: thingGuid } = {}, properties = [] } = JSON.parse(message);
    const { guid: currGuid } = this.deviceManageDS.toData()[0];

    if (thingGuid === currGuid) {
      const { chartData } = this.state;
      const realTimeData = properties
        .map((item) => ({ [item.guid]: { ...item } }))
        .reduce((sumItem, item) => ({ ...sumItem, ...item }));
      const newChartData = chartData.map((item) => {
        const currentVal = realTimeData[item.guid].val;
        return {
          ...item,
          val: currentVal,
          yAxis: [...(item.yAxis || []), currentVal],
        };
      });
      this.setState({ chartData: newChartData });
    }
  }

  @Bind()
  handleMonitorRealTimeData({ message }) {
    const { thingId, ...properties } = JSON.parse(message);
    const { deviceId } = this.state;

    if (thingId === deviceId) {
      const { monitorIndexChart } = this.state;
      const newChartData = monitorIndexChart.map((item) => {
        const currentVal = Number(properties.indexValue);
        if (item.guid === properties.indexCode) {
          return {
            ...item,
            val: currentVal,
            yAxis: [...(item.yAxis || []), currentVal],
          };
        }
        return item;
      });
      this.setState({ monitorIndexChart: newChartData });
    }
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

  /**
   * 跳转到编辑页面
   */
  @Bind()
  handleEdit() {
    const { deviceId } = this.state;
    this.props.history.push(`${urlPrefix}/${EDIT_ACTION}/${deviceId}`);
  }

  // 无用的按钮 由于table组件必需有按钮 否则报错
  get tableButtons() {
    return [
      <Button funcType="raised" icon="revocation" key="rollback" style={{ display: 'none' }}>
        {intl.get('hzero.common.button.cancel').d('取消')}
      </Button>,
    ];
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
    ];
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

  // 预警规则的表格列
  get warnRuleColumns() {
    return [
      { name: 'alertCode' },
      { name: 'alertName' },
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
        name: 'itemMappingList',
        minWidth: 180,
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
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => (
          <span className="action-link">
            <a onClick={() => this.handleWarnRule(record)}>
              {intl.get('hzero.common.button.detail').d('详情')}
            </a>
          </span>
        ),
      },
    ];
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
        align: 'left',
        renderer: ({ value, record }) => (
          <Badge status={record.get('recoveredFlag') === 1 ? 'success' : 'error'} text={value} />
        ),
      },
    ];
  }

  /**
   * 渲染预警规则搜索框
   * columns设置为4 除了显示三个搜索条件外 后面显示查询和重置按钮
   * @param queryFields
   * @param buttons
   * @param dataSet
   * @param queryDataSet
   */
  @Bind()
  renderWarnRuleQueryBar({ queryFields, buttons, dataSet, queryDataSet }) {
    const { mode } = this.props;
    if (queryDataSet) {
      return (
        <Form columns={mode ? 3 : 4} dataSet={queryDataSet}>
          {queryFields}
          <div newLine={mode}>
            <Button
              onClick={() => {
                queryDataSet.current.reset();
              }}
            >
              {intl.get('hzero.common.button.reset').d('重置')}
            </Button>
            <Button
              dataSet={null}
              color="primary"
              onClick={() => {
                dataSet.query();
              }}
            >
              {intl.get('hzero.common.button.search').d('查询')}
            </Button>
            {buttons}
          </div>
        </Form>
      );
    }
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

  @Bind()
  async handleWarnRule(record) {
    this.editRuleDrawerDs.create({});
    const data = record && record.toData();
    const ruleProps = {
      data,
      isCreate: false,
      isOnlyView: true,
      alertRuleDs: this.alertRuleDs,
      editRuleDrawerDs: this.editRuleDrawerDs,
    };
    Modal.open({
      key: 'detail-drawer',
      title: intl.get('hiot.deviceManage.view.title.alertRuleView').d('预警规则详情'),
      style: { width: '700px ' },
      destroyOnClose: true,
      closable: true,
      children: <RuleDrawer {...ruleProps} />,
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

  /**
   * 用于返回导览工作台
   */
  @Bind()
  handleBackBtnClick() {
    const { onBackBtnClick } = this.props;
    onBackBtnClick();
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
      platform,
      thingId,
      configId,
      authType,
      visible,
      bMap = false,
      formConfigList = [],
    } = this.state;
    const { mode } = this.props;
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
          backPath={
            // eslint-disable-next-line no-nested-ternary
            bMap
              ? ''
              : // eslint-disable-next-line no-nested-ternary
              mode
              ? mode === 'deviceWorkbench'
                ? undefined
                : this.handleBackBtnClick
              : urlPrefix
          }
        >
          {mode || bMap ? (
            ''
          ) : (
            <>
              <Button icon="mode_edit" onClick={this.handleEdit} color="primary">
                {intl.get('hzero.common.button.edit').d('编辑')}
              </Button>
              <Spin dataSet={this.registerInfoDS}>
                {status === 'NON_REGISTERED' ? (
                  <Lov
                    noCache
                    mode="button"
                    clearButton={false}
                    name="startRegister"
                    dataSet={
                      new DataSet({
                        fields: [
                          {
                            name: 'startRegister',
                            type: 'object',
                            lovCode: 'HIOT.LOV.CLOUD_ACCOUNT',
                            lovPara: { tenantId: orgId },
                          },
                        ],
                      })
                    }
                    onChange={({ configId: lovConfId, platform: hubType }) => {
                      this.handleRegister(thingId, hubType, true, lovConfId);
                    }}
                  >
                    {intl.get('hiot.gatewayManage.button.startRegister').d('启用注册')}
                  </Lov>
                ) : (
                  <Button onClick={() => this.handleRegister(thingId, platform, false, configId)}>
                    {intl.get('hiot.common.button.stopRegister').d('停用注册')}
                  </Button>
                )}
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
                    <Form disabled dataSet={this.deviceManageDS} columns={2}>
                      <TextField name="thingName" />
                      <TextField name="thingCode" />
                      <Lov name="name" noCache />
                      <TextField name="guid" />
                      <TextField name="version" disabled />
                      <TextField name="name" disabled />
                      <TextField name="thingModelName" />
                      <TextField name="platformMeaning" disabled />
                      <TextField name="configName" disabled />
                      <TextField name="categoryMeaning" />
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
                        <Form dataSet={this.deviceManageDS} columns={2} disabled>
                          <TextField name="equipment" />
                          <TextField name="manufacturer" />
                          <DateTimePicker name="buyingTime" />
                          <TextField
                            name="longitude"
                            label={
                              <>
                                {intl.get(`hiot.common.model.device.longitude`).d('经度')}
                                <HTooltip
                                  title={intl
                                    .get('hiot.common.view.message.title.longitude.tooptip')
                                    .d('负坐标代表西经，正坐标代表东经')}
                                >
                                  <HIcon type="question-circle" />
                                </HTooltip>
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
                                <HTooltip
                                  title={intl
                                    .get('hiot.common.view.message.title.latitude.tooptip')
                                    .d('负坐标代表南纬，正坐标代表北纬')}
                                >
                                  <HIcon type="question-circle" />
                                </HTooltip>
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
                  <Form dataSet={this.deviceManageDS} columns={2}>
                    <TextField name="platformMeaning" disabled />
                    <TextField name="configName" disabled />
                  </Form>
                  {(platform === 'ALI' || platform === 'BAIDU') && (
                    <Form dataSet={this.deviceManageDS} columns={2}>
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
                    <Form dataSet={this.deviceManageDS} columns={2}>
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
              disabled={!configId}
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
              <Table
                mode="tree"
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
                // queryBar={this.renderWarnRuleQueryBar}
                columns={this.warnRuleColumns}
                buttons={this.tableButtons}
              />
            </TabPane>
          </Tabs>
        </Content>
      </>
    );
  }
}
