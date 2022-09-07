/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-09 19:54:14
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备管理-新建设备页面
 */
import React from 'react';
import { Icon, Tooltip, Card } from 'choerodon-ui';
import {
  Button,
  Form,
  TextField,
  Row,
  Col,
  Lov,
  TextArea,
  Tabs,
  Select,
  Table,
  DateTimePicker,
  DataSet,
  IntlField,
} from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { Tooltip as HTooltip, Icon as HIcon } from 'hzero-ui';

import { Header, Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { deviceManageDS, dataPointInfoDS, formConfigDS } from '@/stores/deviceManageDS';

const { TabPane } = Tabs;

@formatterCollections({
  code: ['hiot.dataPointTemplate', 'hiot.iotWarnTemplate', 'hiot.deviceManage', 'hiot.common'],
})
export default class Create extends React.Component {
  constructor(props) {
    super(props);
    this.deviceManageDS = new DataSet(deviceManageDS());
    this.dataPointInfoDS = new DataSet(dataPointInfoDS());
    this.state = {
      isDisabled: false,
      visible: false,
      formConfigList: [],
    };
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
          }
        },
      },
    });
  }

  componentDidMount() {
    this.formConfigDs.query();
    this.deviceManageDS.create({}, 0);
  }

  // 数据点信息的表格列
  get dataPointInfoColumns() {
    return [
      { name: 'propertyModelCode' },
      { name: 'propertyModelName' },
      { name: 'categoryMeaning' },
      { name: 'dataTypeMeaning' },
      { name: 'unitCode' },
      { name: 'minValue' },
      { name: 'maxValue' },
      { name: 'reportInterval' },
    ];
  }

  /**
   * 保存新建设备记录
   */
  @Bind()
  async handleDeviceManageSave() {
    const { formConfigList = [] } = this.state;
    const formatData = {};
    try {
      const data = this.deviceManageDS.current.toData();
      if (formConfigList.length) {
        formConfigList.forEach((item) => {
          formatData[item.itemCode] = data[item.itemCode];
        });
        this.deviceManageDS.current.set('additionInfo', JSON.stringify(formatData));
      }
      const res = await this.deviceManageDS.submit();
      const thingName = this.deviceManageDS.current.get('thingName');
      const thingCode = this.deviceManageDS.current.get('thingCode');
      const thingGroupId = this.deviceManageDS.current.get('thingGroupId');

      if (!res) {
        if (!this.deviceManageDS.current.get('configName')) {
          notification.error({
            message: intl.get('hiot.common.view.validation.authInfoMsg').d('请填写认证信息'),
          });
        }
        if (!thingName || !thingCode || !thingGroupId) {
          notification.error({
            message: intl
              .get('hiot.common.view.validation.baseInfoMsg')
              .d('请填写基本信息的必输信息'),
          });
        }
        return false;
      }
      this.props.history.push('/hiot/device/manage/list');
    } catch (err) {
      // const errTitle = intl.get('hzero.common.notification.error').d('操作失败');
      // notification.error({ message: `${errTitle}:${err.message}` });
    }
  }

  // 无用的按钮 由于table组件必需有按钮 否则报错
  get tableButtons() {
    return [
      <Button icon="revocation" style={{ display: 'none' }}>
        {intl.get('hzero.common.button.cancel').d('取消')}
      </Button>,
    ];
  }

  /**
   * 更改设备模版
   * @param value 当前值
   */
  @Bind()
  changeThingModel(value) {
    let id;
    let name;
    let newPlatform;
    let newPlatformName;
    const isDisabled = !!value;
    if (value) {
      const { thingModelId, configId, configName, platformMeaning, platform } = value;
      id = configId;
      name = configName;
      newPlatformName = platformMeaning;
      newPlatform = platform;
      this.dataPointInfoDS.setQueryParameter('thingTmpltId', thingModelId);
      this.dataPointInfoDS.query();
    } else {
      this.dataPointInfoDS.loadData([]);
    }
    this.deviceManageDS.records[0].set('configId', id);
    this.deviceManageDS.records[0].set('configName', name);
    this.deviceManageDS.records[0].set('platformMeaning', newPlatformName);
    this.deviceManageDS.records[0].set('platform', newPlatform);
    this.setState({
      isDisabled,
      isPrivateCloud: value && value.platform === 'OWN',
    });
  }

  /**
   * 下拉框变化时触发
   */
  @Bind()
  onSelectChange(value) {
    const isJWT = value ? value === 'JWT' : false;
    const isRedis = value ? value === 'REDIS' : false;
    this.setState({
      isJWT,
      isRedis,
    });
  }

  @Bind()
  handleVisible() {
    const { visible } = this.state;
    this.setState({
      visible: !visible,
    });
  }

  render() {
    const { isDisabled, isPrivateCloud, isJWT, isRedis, visible, formConfigList = [] } = this.state;
    return (
      <>
        <Header
          title={intl.get('hiot.deviceManage.view.title.create.header').d('新建设备')}
          backPath="/hiot/device/manage/list"
        >
          <Button color="primary" icon="save" onClick={this.handleDeviceManageSave}>
            {intl.get('hzero.common.button.save').d('保存')}
          </Button>
        </Header>
        <Content>
          <Tabs defaultActiveKey="baseInfo">
            <TabPane tab={intl.get('hiot.common.view.baseInfo').d('基本信息')} key="baseInfo">
              <Row>
                <Col span={16}>
                  <Form dataSet={this.deviceManageDS} columns={2}>
                    <IntlField name="thingName" />
                    <TextField name="thingCode" />
                    <Lov name="project" noCache />
                    <TextField name="version" />
                    <Lov
                      noCache
                      name="thingModel"
                      onChange={(value) => this.changeThingModel(value)}
                    />
                    <TextField name="category" disabled />
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
                          <TextField name={item.itemCode} label={item.itemName} />
                        ))}
                      </Form>
                    )}
                  </Card>
                </Col>
              </Row>
            </TabPane>
            <TabPane
              tab={intl.get('hiot.deviceManage.view.title.authInfo').d('认证信息')}
              key="authInfo"
            >
              <Row>
                <Col span={16}>
                  <Form columns={2} dataSet={this.deviceManageDS} labelWidth={110}>
                    <Lov
                      name="configLov"
                      disabled={isDisabled}
                      onChange={(value) => {
                        this.setState({
                          isPrivateCloud: value && value.platform === 'OWN',
                        });
                      }}
                    />
                    <TextField name="configName" disabled />
                    {isPrivateCloud && <Select name="authType" onChange={this.onSelectChange} />}
                    {isJWT && <TextField name="secret" />}
                    {isJWT && <Select name="from" />}
                    {isRedis && (
                      <TextField
                        name="username"
                        disabled
                        label={
                          <React.Fragment>
                            {intl.get('hiot.common.model.common.username').d('用户名')}
                            <Tooltip
                              title={intl
                                .get('hiot.deviceManage.view.title.autoGeneration')
                                .d('自动生成')}
                            >
                              <Icon type="help_outline" style={{ marginTop: -2, marginLeft: 2 }} />
                            </Tooltip>
                          </React.Fragment>
                        }
                      />
                    )}
                    {isRedis && (
                      <TextField
                        name="password"
                        label={
                          <React.Fragment>
                            {intl.get('hiot.common.model.common.password').d('密码')}
                            <Tooltip
                              title={intl
                                .get('hiot.deviceManage.view.title.autoGeneration')
                                .d('自动生成')}
                            >
                              <Icon type="help_outline" style={{ marginTop: -2, marginLeft: 2 }} />
                            </Tooltip>
                          </React.Fragment>
                        }
                        disabled
                      />
                    )}
                  </Form>
                </Col>
              </Row>
            </TabPane>
          </Tabs>
          <Tabs defaultActiveKey="dataPointInfo">
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
          </Tabs>
        </Content>
      </>
    );
  }
}
