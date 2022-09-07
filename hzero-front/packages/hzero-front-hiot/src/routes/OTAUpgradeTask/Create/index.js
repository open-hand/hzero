/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/27
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 创建升级任务
 */
import React, { Component } from 'react';
import { Bind } from 'lodash-decorators';
import {
  DataSet,
  TextField,
  Form,
  Select,
  DateTimePicker,
  Button,
  Col,
  Row,
  Radio,
  Table,
  Lov,
  IntlField,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import intl from 'utils/intl';
import { closeTab } from 'utils/menuTab';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import {
  fields,
  otaUpgradeTaskCreateDS,
  otaUpgradeTaskDeviceFilterList,
  otaUpgradeTaskGatewayFilterList,
} from '@/stores/otaUpgradeTaskDS';
import LoadingButton from '@/routes/components/loading/Button';
import Content from '@/routes/components/loading/Content';
import Header from '@/routes/components/CustomBackHeader';

const prefix = 'hiot.ota';

@formatterCollections({
  code: [prefix, 'hiot.common'],
})
export default class Create extends Component {
  constructor(props) {
    super(props);
    const { state } = this.props.location;
    const { backPath, template } = state || {};
    this.template = template;
    this.upgradeCategory = template && template[[fields.upgradeCategory().name]];
    this.otaUpgradeTaskCreateDS = new DataSet(otaUpgradeTaskCreateDS());
    this.otaUpgradeTaskDeviceFilterList = new DataSet(otaUpgradeTaskDeviceFilterList());
    this.otaUpgradeTaskGatewayFilterList = new DataSet(otaUpgradeTaskGatewayFilterList());
    this.state = {
      isDevice: this.upgradeCategory !== 'GATEWAY',
      isRange: true,
      fromPackage: !!template, // 由OTA升级包页面而来
      backPath: backPath || '/hiot/ota-upgrade/task/list',
      thingRange: 0,
      isShow: false,
      isDisable: false,
    };
  }

  componentDidMount() {
    const { template } = this;
    const { pathname } = this.props.location;
    const noCacheCreateUrl = '/hiot/ota-upgrade/task/create';
    if (!template && pathname !== noCacheCreateUrl) {
      this.props.history.push(noCacheCreateUrl);
    }
    const pageData = {
      [fields.templateName().name]: template,
      [fields.upgradeCategory().name]: this.upgradeCategory,
      version: template ? template.currentVersion : '',
    };
    this.otaUpgradeTaskCreateDS.create(pageData, 0);
  }

  @Bind()
  goBack() {
    const { history } = this.props;
    const { backPath, fromPackage } = this.state;
    history.push(backPath);
    if (fromPackage) {
      closeTab('/hiot/ota-upgrade/task');
    }
  }

  /**
   * 处理下发对象变化
   * @param value
   */
  @Bind()
  handleRangeChange(value) {
    this.setState({ isRange: value === 0, thingRange: value });
  }

  /**
   * 处理保存操作
   */
  @Bind()
  async handleSave() {
    const { thingRange } = this.state;
    const subRecord = this.otaUpgradeTaskCreateDS.get(0);
    // sendMethod：0 特定范围，1 全量下发
    const thingIds = [
      ...this.otaUpgradeTaskDeviceFilterList.toData().map(({ thingId }) => thingId),
      ...this.otaUpgradeTaskGatewayFilterList.toData().map(({ gatewayId }) => gatewayId),
    ];
    subRecord.set('thingIds', !Number(thingRange) ? thingIds : undefined);
    subRecord.set('thingRange', thingRange);
    const res = await this.otaUpgradeTaskCreateDS.submit();
    if (res) {
      this.goBack();
    }
  }

  @Bind()
  handleLovSelectChange(lovRecs) {
    const { isDevice } = this.state;
    this.otaUpgradeTaskDeviceFilterList.reset();
    this.otaUpgradeTaskGatewayFilterList.reset();
    lovRecs.forEach((lovRec) => {
      if (isDevice) {
        this.otaUpgradeTaskDeviceFilterList.create(lovRec, 0);
      } else {
        this.otaUpgradeTaskGatewayFilterList.create(lovRec, 0);
      }
    });
  }

  /**
   * 获取lov button当前选择的数据
   */
  @Bind()
  lovButtonData() {
    const { isDevice } = this.state;
    const deviceFieldName = fields.addDevice().name;
    const gatewayFieldName = fields.addGateway().name;
    return this.otaUpgradeTaskCreateDS.get(0).toData()[
      isDevice ? deviceFieldName : gatewayFieldName
    ];
  }

  /**
   * 设置需要被选中的lov记录
   * @param selectedLovRecs 需要被选中的lov记录
   */
  @Bind()
  setSelectedLovRecs(selectedLovRecs) {
    const { isDevice } = this.state;
    const deviceFieldName = fields.addDevice().name;
    const gatewayFieldName = fields.addGateway().name;
    if (isDevice) {
      this.otaUpgradeTaskCreateDS.get(0).set(deviceFieldName, selectedLovRecs);
    } else {
      this.otaUpgradeTaskCreateDS.get(0).set(gatewayFieldName, selectedLovRecs);
    }
  }

  /**
   * 处理表格记录移除操作
   * @param removeRec 单行移除时需要需要指定的行数据
   */
  @Bind()
  handleTableRemove(removeRec) {
    const dsSelectedData = [
      ...this.otaUpgradeTaskDeviceFilterList.selected,
      ...this.otaUpgradeTaskGatewayFilterList.selected,
    ];
    const selectedData = removeRec ? [removeRec] : dsSelectedData;
    const selectedIds = new Set(
      selectedData.map((record) => {
        const { thingModelId } = record.toData();
        return thingModelId;
      })
    );
    const retainLovRecs = this.lovButtonData().filter(
      ({ thingModelId }) => !selectedIds.has(thingModelId)
    );
    this.otaUpgradeTaskDeviceFilterList.remove(selectedData);
    this.otaUpgradeTaskGatewayFilterList.remove(selectedData);
    this.setSelectedLovRecs(retainLovRecs);
  }

  @Bind()
  lovChange(value) {
    const data = value || {};
    const { configId, configName, platformMeaning } = data;
    if (configId || configName || platformMeaning) {
      this.otaUpgradeTaskCreateDS.records[0].set('platformName', platformMeaning);
      this.otaUpgradeTaskCreateDS.records[0].set('configId', configId);
      this.otaUpgradeTaskCreateDS.records[0].set('configName', configName);
    }
    this.setState({
      isDisable: !!(configId || configName || platformMeaning),
    });

    const { thingRange, isRange } = this.state;
    if (value) {
      const isShow = value.thingModelId > -1;
      this.setState({
        isShow,
        thingRange: isShow ? thingRange : 0,
        isRange: isShow ? isRange : true,
      });
    } else {
      this.setState({
        isShow: false,
        isRange: true,
      });
    }
  }

  render() {
    const { isRange, isDevice, fromPackage, thingRange, isShow = true, isDisable } = this.state;
    const columns = [
      ...(isDevice
        ? otaUpgradeTaskDeviceFilterList()
        : otaUpgradeTaskGatewayFilterList()
      ).fields.map(({ name }) => ({ name })),
      {
        name: 'action',
        width: 150,
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          const deleteView = intl.get('hzero.common.button.delete').d('删除');
          const operators = [
            {
              key: 'delete',
              len: 2,
              title: deleteView,
              ele: <a onClick={() => this.handleTableRemove(record)}>{deleteView}</a>,
            },
          ];
          return operatorRender(operators);
        },
      },
    ];
    const buttons = [
      <Lov
        name={isDevice ? fields.addDevice().name : fields.addGateway().name}
        dataSet={this.otaUpgradeTaskCreateDS}
        onChange={this.handleLovSelectChange}
        disabled={isDevice === undefined}
        clearButton={false}
        color="primary"
        mode="button"
        icon="add"
        key="add"
        noCache
      >
        {isDevice ? fields.addDevice().label : fields.addGateway().label}
      </Lov>,
      <Button color="red" key="delete" onClick={() => this.handleTableRemove()}>
        {intl.get('hzero.common.button.delete').d('删除')}
      </Button>,
    ];
    return (
      <>
        <Header
          backPath={this.goBack}
          title={intl.get(`${prefix}.view.create.upgrade.task`).d('创建升级任务')}
        >
          <LoadingButton
            dataSet={this.otaUpgradeTaskCreateDS}
            color="primary"
            icon="save"
            onClick={this.handleSave}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </LoadingButton>
          <Button icon="revocation" onClick={this.goBack}>
            {intl.get('hzero.common.button.cancel').d('取消')}
          </Button>
        </Header>
        <Content dataSet={this.otaUpgradeTaskCreateDS}>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
          >
            <Row>
              <Col span={16}>
                <Form columns={2} dataSet={this.otaUpgradeTaskCreateDS}>
                  <Lov name={fields.cloudPlatform().name} noCache disabled={isDisable} />
                  <TextField name={fields.cloudAccount().name} disabled />
                  <IntlField name={fields.taskName().name} />
                  <TextField name={fields.taskCode().name} />
                  <Select
                    disabled={fromPackage}
                    name={fields.upgradeCategory().name}
                    onChange={(value) => {
                      this.setState({ isDevice: value === 'THING' });
                      this.otaUpgradeTaskCreateDS.get(0).set({
                        [fields.templateName().name]: {},
                      });
                      this.otaUpgradeTaskDeviceFilterList.reset();
                    }}
                  />
                  <Lov
                    disabled={fromPackage}
                    name={fields.templateName().name}
                    onChange={this.lovChange}
                    noCache
                  />
                  <TextField disabled name="thingModelName" />
                  <TextField disabled name={fields.templateCode().name} />
                  <TextField name={fields.versionNum().name} disabled required />
                  <Lov name="templateLov" />
                  <DateTimePicker name={fields.startTime().name} />
                  <TextField name={fields.description().name} />
                </Form>
              </Col>
            </Row>
          </Card>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get(`${prefix}.view.device.filter`).d('设备筛选')}
          >
            <Row>
              {isShow && (
                <Radio
                  onChange={this.handleRangeChange}
                  name={fields.sendMethod().name}
                  checked={thingRange === 1}
                  value={1}
                >
                  {intl.get(`${prefix}.full.amount.send`).d('全量下发')}
                </Radio>
              )}
              <Radio
                onChange={this.handleRangeChange}
                name={fields.sendMethod().name}
                checked={thingRange === 0}
                value={0}
              >
                {intl.get(`${prefix}.target.range`).d('特定范围')}
              </Radio>
            </Row>
            {isRange && (
              <Row style={{ marginTop: 10 }}>
                <Table
                  buttons={buttons}
                  columns={columns}
                  dataSet={
                    isDevice
                      ? this.otaUpgradeTaskDeviceFilterList
                      : this.otaUpgradeTaskGatewayFilterList
                  }
                />
              </Row>
            )}
          </Card>
        </Content>
      </>
    );
  }
}
