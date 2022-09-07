/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-11 14:17:05
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 网关详情/网关编辑界面
 */
import React from 'react';
import {
  Col,
  DataSet,
  Form,
  IntlField,
  Lov,
  Modal,
  Row,
  Select,
  Table,
  Tabs,
  TextArea,
  TextField,
  Spin,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { isObject } from 'lodash';
import { Bind } from 'lodash-decorators';
import queryString from 'query-string';
import { Content, Header } from 'components/Page';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import notification from 'utils/notification';

import { BIND_ACTION, DETAIL_ACTION, EDIT_ACTION, HZERO_HIOT } from '@/utils/constants';
import {
  gatewayConfigDS,
  gatewayManageDS,
  registerInfoDS,
  sendConfigDS,
  subDeviceInfoDS,
  subDeviceTypeDS,
} from '@/stores/gatewayManageDS';

const modalKey = Modal.key;

export default class Detail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {} } = this.props;
    const { action, gatewayId } = params;
    this.state = {
      action,
      gatewayId,
      configId: null,
      isRegister: false,
      // guid: null,
      loading: false,
      hubType: '',
      activeKey: 'baseInfo',
      authType: '',
    };
    this.gatewayManageDS = new DataSet(gatewayManageDS());
    this.subDeviceInfoDS = new DataSet({
      paging: true,
      ...subDeviceInfoDS(),
      events: {
        update: this.handleDataSetChange,
      },
    });
    this.gatewayConfigDS = new DataSet(gatewayConfigDS());
    this.sendConfigDS = new DataSet(sendConfigDS());
    this.subDeviceTypeDS = new DataSet(subDeviceTypeDS());
    this.registerInfoDS = new DataSet(registerInfoDS());
  }

  componentDidMount() {
    this.initData();
  }

  @Bind()
  initData() {
    const {
      location: { state = {}, search },
    } = this.props;
    const { hubType } = state;
    const { gatewayId, action } = this.state;
    const { hubType: searchHubType } = queryString.parse(search.substring(1));
    this.gatewayManageDS.queryParameter = { gatewayId };
    this.gatewayConfigDS.queryParameter = { gatewayId };
    this.gatewayManageDS.query().then((resp) => {
      const {
        gatewayId: thingId,
        guid,
        status,
        configId,
        thingModelId,
        thingModelName,
        thingGroupId,
        platform,
        registerFlag,
        name,
        authType,
      } = resp;
      this.setState({
        configId,
        // guid,
        authType,
        hubType: platform,
        isRegister: !!registerFlag,
      });
      if ((action === DETAIL_ACTION || action === EDIT_ACTION) && status === 'REGISTERED') {
        const hubTypeParam = isObject(hubType) ? searchHubType : hubType;
        this.getGatewayConfig(thingId, guid, hubTypeParam);
      }
      const gatewayRecord = this.gatewayManageDS.get(0);
      gatewayRecord.set('gateway', { thingModelId, thingModelName });
      gatewayRecord.set('project', { thingGroupId, name });
      this.subDeviceInfoDS.configId = configId;
    });
    this.querySubDeviceInfo(this.subDeviceInfoDS);
  }

  /**
   * 查询网关信息
   * @param thingId
   * @param guid
   */
  @Bind()
  getGatewayConfig(thingId, guid, platform) {
    const { hubType } = this.state;
    this.registerInfoDS.queryParameter = {
      thingId,
      hubType: platform || hubType,
      flag: undefined,
      thingType: 'GATEWAY',
    };
    this.registerInfoDS.query();
  }

  @Bind()
  querySubDeviceInfo(dataSet) {
    const { gatewayId } = this.state;
    dataSet.setQueryParameter('gatewayId', gatewayId);
    dataSet.query();
  }

  @Bind()
  handleDataSetChange({ dataSet }) {
    dataSet.submit().then((resp) => {
      if (resp) {
        this.querySubDeviceInfo(dataSet);
      }
    });
  }

  /**
   * 将页面设为可编辑状态
   */
  @Bind()
  handleEdit() {
    this.setState({
      action: EDIT_ACTION,
    });
  }

  /**
   * 打开新建子设备的类型选择框
   */
  @Bind()
  openNewSubDeviceModal() {
    this.subDeviceTypeDS.create({}, 0);
    Modal.open({
      key: modalKey,
      title: intl.get('hiot.dataPointTemplate.view.addSubDevice').d('绑定子设备'),
      children: (
        <Form dataSet={this.subDeviceTypeDS} columns={1}>
          <Select name="terminalType" onChange={this.handleTerminalTypeChange} labelWidth={150} />
          <Select name="endpointProtocol" labelWidth={150} disabled />
        </Form>
      ),
      closable: true,
      onOk: () => {
        this.handleDetailOrEdit(BIND_ACTION, this.subDeviceTypeDS.get(0));
      },
    });
  }

  /**
   * 跳转到对应网关子设备详情/编辑页面的页面
   * @param record 子设备记录
   * @param type edit: 编辑 detail: 详情 bind: 绑定
   */
  @Bind()
  handleDetailOrEdit(type, record) {
    const gatewayCode = this.gatewayManageDS.current.get('gatewayCode');
    const thingGroupId = this.gatewayManageDS.get(0).get('thingGroupId');
    const { gatewayId, configId, action, hubType } = this.state;
    const id = record.get('edginkId') || record.get('modbusId');
    const thingId = type === BIND_ACTION ? '-1' : id;
    const path = `${HZERO_HIOT}/gateway/manage/device/${type}/${gatewayId}/${thingId}`;
    const { data } = record;
    const subDeviceInfo = { ...data, configId, gatewayAction: action, thingGroupId };
    this.props.history.push({
      pathname: path,
      state: { subDeviceInfo },
      search: `hubType=${hubType}&gatewayCode=${gatewayCode}`,
    });
  }

  @Bind()
  handleTerminalTypeChange(value) {
    const record = this.subDeviceTypeDS.get(0);
    if (value === 'SOFTWARE') {
      record.set('endpointProtocol', 'EDGINK'); // 软件
    } else {
      record.set('endpointProtocol', 'MODBUS'); // 硬件
    }
  }

  /**
   * 删除绑定的子设备
   * @param record
   */
  @Bind()
  handleDelete(record) {
    this.subDeviceInfoDS.delete(record);
  }

  // 数据点模板table列
  get subDeviceInfoColumns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'thingCode', width: 150 },
      { name: 'thingName', width: 150 },
      { name: 'terminalType', width: 120 },
      { name: 'endpointProtocol', width: 150 },
      { name: 'bindPropNum', align: 'left', width: 120 },
      { name: 'configName' },
      { name: 'enabled', editor: true },
      {
        name: 'operation',
        lock: 'right',
        width: 120,
        header: <span>{intl.get(`hzero.common.button.action`).d('操作')}</span>,
        renderer: ({ record }) => {
          const { action } = this.state;
          return (
            <span className="action-link">
              {/* <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detail`,
                    type: 'button',
                    meaning: '网关管理-详情',
                  },
                ]}
                onClick={() => this.handleDetailOrEdit(DETAIL_ACTION, record)}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission> */}
              {action === EDIT_ACTION && (
                <>
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: '网关管理-编辑',
                      },
                    ]}
                    onClick={() => this.handleDetailOrEdit(EDIT_ACTION, record)}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.delete`,
                        type: 'button',
                        meaning: '网关管理-删除',
                      },
                    ]}
                    onClick={() => this.handleDelete(record)}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </>
              )}
            </span>
          );
        },
      },
    ];
  }

  /**
   * 保存网关详情
   */
  @Bind()
  async handleGatewayManageSave() {
    const { action } = this.state;
    // 编辑的情况下，判断数据是否有改动
    if (action === 'edit' && !this.gatewayManageDS.isModified()) {
      this.props.history.push('/hiot/gateway/manage/list');
      return;
    }
    const res = await this.gatewayManageDS.submit();
    if (res) {
      this.props.history.push('/hiot/gateway/manage/list');
    }
  }

  /**
   * 导出网关配置
   */
  @Bind()
  handleExportConfig() {
    const { gatewayId } = this.state;
    this.gatewayConfigDS.setQueryParameter('gatewayId', gatewayId);
    this.gatewayConfigDS.query().then((resp) => {
      if (resp) {
        const content = Object.keys(resp)
          .map((contentKey) => `${contentKey}: ${resp[contentKey]}`)
          .join('\n');
        this.downLoad(
          intl.get('hiot.gatewayManage.view.title.download.config').d('网关配置信息'),
          content
        );
      }
    });
  }

  @Bind()
  downLoad(fileName, content) {
    const element = document.createElement('a');
    element.setAttribute('href', `data:text/plain;charset=utf-8,${encodeURIComponent(content)}`);
    element.setAttribute('download', fileName);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
  }

  @Bind()
  handleSendConfig() {
    const { gatewayId, configId, hubType } = this.state;
    this.sendConfigDS.queryParameter = { configId, gatewayId };
    this.sendConfigDS.setQueryParameter('cloudType', hubType);
    this.sendConfigDS.query().then((resp) => {
      if (resp) {
        notification.success({
          message: intl.get('hiot.gatewayManage.message.success.config').d('配置下发成功！'),
        });
      }
    });
  }

  /**
   * 网关注册
   * @param value
   */
  @Bind()
  handleGatewayRegister() {
    const {
      location: { state = {} },
    } = this.props;
    this.setState({ loading: true });
    const { gatewayId: thingId, isRegister, hubType, configId: stateConfigId } = this.state;
    this.registerInfoDS.queryParameter = {
      thingId,
      hubType: hubType || state.hubType,
      thingType: 'GATEWAY',
      flag: !isRegister,
      configId: stateConfigId || state.configId,
    };
    this.registerInfoDS.query().then((resp) => {
      this.setState({ loading: false });
      if (isRegister) {
        this.registerInfoDS.reset();
      } else if (resp) {
        const { configId, platform } = resp;
        this.setState({ configId, hubType: platform });
      }
      this.setState({ isRegister: !isRegister, loading: false });
      this.initData();
      notification.success({
        message: intl.get('hiot.common.notification.success').d('操作成功！'),
      });
      this.setState({ activeKey: 'baseInfo' });
    });
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const { action, isRegister, loading, activeKey, hubType, authType } = this.state;
    const config = { value: {} };
    return (
      <>
        <Header
          title={
            action === EDIT_ACTION
              ? intl.get('hiot.gatewayManage.view.title.header.edit').d('网关编辑')
              : intl.get('hiot.gatewayManage.view.title.header.detail').d('网关详情')
          }
          backPath="/hiot/gateway/manage/list"
        >
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.save`,
                type: 'button',
                meaning: '网关管理-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={this.handleGatewayManageSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
          {/* {isRegister ? ( */}
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.stopRegister`,
                type: 'button',
                meaning: '网关管理-停用注册',
              },
            ]}
            onClick={() => this.handleGatewayRegister(config)}
            loading={loading}
          >
            {isRegister
              ? intl.get('hiot.common.button.stopRegister').d('停用注册')
              : intl.get('hiot.gatewayManage.button.startRegister').d('启用注册')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.exportConfig`,
                type: 'button',
                meaning: '网关管理-导出信息配置',
              },
            ]}
            icon="export"
            onClick={this.handleExportConfig}
            style={{ marginLeft: 8 }}
          >
            {intl.get('hiot.gatewayManage.view.title.exportConfig').d('导出配置信息')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.sendConfig`,
                type: 'button',
                meaning: '网关管理-下发配置',
              },
            ]}
            icon="publish2"
            onClick={this.handleSendConfig}
            disabled={!isRegister}
          >
            {intl.get('hiot.gatewayManage.view.title.sendConfig').d('下发配置')}
          </ButtonPermission>
        </Header>
        <Content>
          <Tabs
            activeKey={activeKey}
            onChange={(key) => {
              this.setState({ activeKey: key });
            }}
          >
            <Tabs.TabPane tab={intl.get('hiot.common.view.baseInfo').d('基本信息')} key="baseInfo">
              <Row>
                <Col span={16}>
                  <Spin dataSet={this.gatewayManageDS}>
                    <Form dataSet={this.gatewayManageDS} columns={2}>
                      <TextField name="gatewayCode" disabled />
                      <IntlField name="gatewayName" disabled />
                      <Lov name="gateway" disabled />
                      <Lov name="project" disabled />
                      <TextField name="guid" disabled />
                      <TextField name="model" disabled={action === DETAIL_ACTION} />
                      <TextField name="version" disabled />
                      <TextField name="gatewayIp" />
                      <TextArea name="description" disabled={action === DETAIL_ACTION} />
                    </Form>
                  </Spin>
                </Col>
              </Row>
            </Tabs.TabPane>
            <Tabs.TabPane
              tab={intl.get('hiot.gatewayManage.view.title.authInfo').d('认证信息')}
              key="authInfo"
            >
              <Row>
                <Col span={16}>
                  <Form dataSet={this.gatewayManageDS} columns={2} labelWidth={110}>
                    <TextField name="platformMeaning" disabled />
                    <TextField name="configName" disabled />
                  </Form>
                  {(hubType === 'ALI' || hubType === 'BAIDU') && (
                    <Form dataSet={this.gatewayManageDS} columns={2} labelWidth={110}>
                      {hubType === 'ALI' && <TextField name="authType" disabled />}
                      {hubType === 'ALI' && <TextField name="deviceName" disabled />}
                      {hubType === 'ALI' && <TextField name="deviceSecret" disabled />}
                      {hubType === 'ALI' && <TextField name="productKey" disabled />}
                      <TextField name="username" disabled />
                      <TextField name="password" disabled />
                      <TextField name="protocol" disabled />
                      <TextField name="port" disabled />
                      <TextField name="clientId" disabled />
                      <TextField name="brokerAddress" disabled />
                    </Form>
                  )}
                  {hubType === 'OWN' && (
                    <Form dataSet={this.gatewayManageDS} columns={2} labelWidth={110}>
                      <TextField name="authType" disabled />
                      {authType === 'REDIS' && <TextField name="username" disabled />}
                      {authType === 'REDIS' && <TextField name="password" disabled />}
                      {authType === 'JWT' && <TextField name="secret" disabled />}
                      {authType === 'JWT' && <Select name="from" disabled />}
                    </Form>
                  )}
                </Col>
              </Row>
            </Tabs.TabPane>
            {/* {action === DETAIL_ACTION && ( */}
            <Tabs.TabPane
              tab={intl.get('hiot.common.view.registerInfo').d('注册信息')}
              key="registerInfo"
              disabled={!isRegister}
            >
              <Row>
                <Col span={16}>
                  <Form columns={2} dataSet={this.registerInfoDS}>
                    <TextField name="platformMeaning" disabled />
                    <TextField name="configName" />
                    <TextField name="upTopic" disabled />
                    <TextField name="downTopic" disabled />
                  </Form>
                </Col>
              </Row>
            </Tabs.TabPane>
            {/* )} */}
          </Tabs>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get('hiot.gatewayManage.view.title.subDeviceInfo').d('子设备信息')}
          >
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.addSubDevice`,
                  type: 'button',
                  meaning: '网关管理-绑定子设备',
                },
              ]}
              // color="primary"
              icon="add"
              style={{ margin: 10 }}
              disabled={!isRegister}
              onClick={() => {
                this.subDeviceTypeDS.create({}, 0);
                this.handleDetailOrEdit(BIND_ACTION, this.subDeviceTypeDS.get(0));
              }}
            >
              {intl.get('hiot.dataPointTemplate.view.addSubDevice').d('绑定子设备')}
            </ButtonPermission>
            <Table
              dataSet={this.subDeviceInfoDS}
              queryFieldsLimit={3}
              columns={this.subDeviceInfoColumns}
            />
          </Card>
        </Content>
      </>
    );
  }
}
