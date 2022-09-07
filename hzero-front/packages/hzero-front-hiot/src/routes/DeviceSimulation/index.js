/**
 * 设备模拟上报
 * @date: 2020-7-7
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo, useState } from 'react';
import {
  Form,
  Modal,
  Button,
  Table,
  DataSet,
  Select,
  NumberField,
  TextField,
  TextArea,
  Lov,
  Spin,
  DatePicker,
  SelectBox,
  DateTimePicker,
  Tooltip,
} from 'choerodon-ui/pro';
import { Row, Col, Tabs, Card, Badge, Divider, Icon } from 'choerodon-ui';
import { isNull, isEmpty } from 'lodash';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId } from 'utils/utils';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { tableDS, otaDS, formDS, customDS, dataPointDS } from '@/stores/DeviceSimulationDS';
import { DATA_TYPE } from '@/utils/constants';
import { fetchForm, fetchMessageReport } from '@/services/deviceSimulationService';
import Drawer from '../DataReportLog/Drawer';
import styles from './index.less';

const organizationId = getCurrentOrganizationId();

const { TabPane } = Tabs;
const { Option } = SelectBox;
const DeviceSimulation = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const dataListDs = useMemo(() => new DataSet({}), []);
  const otaDs = useMemo(() => new DataSet(otaDS()), []);
  const dataPointDs = useMemo(() => new DataSet(dataPointDS()), []);
  const customDs = useMemo(() => new DataSet(customDS()), []);
  const formDs = useMemo(() => new DataSet(formDS()), []);
  const [formList, setFormList] = useState([]);
  const [tabKey, setTabKey] = useState('dataPoint');
  const [currentQos, setQos] = useState('0');
  const [isGateway, setIsGateway] = useState(false);
  const [otaType, setOtaType] = useState('');
  const [currentThingType, setThingType] = useState('');
  const [currentThingId, setThingId] = useState('');
  const [loading, setLoading] = useState(false);
  const [disabled, setDisabled] = useState(true);
  const columns = useMemo(
    () => [
      { name: 'serviceInstIp', width: 200 },
      { name: 'topicName' },
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
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detail`,
                    type: 'button',
                    meaning: '设备模拟上报-详情',
                  },
                ]}
                onClick={() => {
                  handleDetail(record);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  // 查看详情
  const handleDetail = (record) => {
    const currentEditData = record && record.toData();
    Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 650 },
      title: intl.get('hzero.common.status.detail').d('查看详情'),
      children: <Drawer currentEditData={currentEditData} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  // 设备Lov变化时触发
  const onLovChange = (value) => {
    let id = '';
    let checkedId = '';
    let checkedType = '';
    if (value) {
      setDisabled(false);
      const { guid, thingType, thingId } = value;
      id = guid;
      checkedId = thingId;
      checkedType = thingType;
      setIsGateway(guid.indexOf('g_') === 0); // g_开头为网关设备
      if (guid.indexOf('g_') === 0 && tabKey === 'dataPoint') {
        setTabKey('ota');
      }
    } else {
      setIsGateway(false);
      setDisabled(true);
      setFormList([]);
    }
    otaDs.current.reset();
    dataPointDs.current.reset();
    customDs.current.reset();
    setThingId(checkedId);
    setThingType(checkedType);
    tableDs.setQueryParameter('guid', id);
    tableDs.query();
  };

  // Qos变化时触发
  const onSelectChange = (value) => {
    setQos(value);
  };

  // ota 上报模板Lov变化时触发
  const onOtaLovChange = (value) => {
    const type = value ? value.templateTypeCode : '';
    setOtaType(type);
    const otaRecord = otaDs.current;
    if (value) {
      const { templateTypeCode } = value;
      if (templateTypeCode === 'UP_OTA_STEP') {
        otaRecord.set('version', '');
      } else {
        otaRecord.set('otaTask', {});
        otaRecord.set('otaStep', '');
        otaRecord.set('otaDesc', '');
      }
    } else {
      otaRecord.reset();
    }
  };

  // 数据点 上报模板Lov变化时触发
  const onDataLovChange = (value) => {
    if (value) {
      fetchFormItem(value);
    } else {
      setFormList([]);
    }
  };

  // 获取表单
  const fetchFormItem = (value) => {
    const { templateId } = value;
    const params = {
      thingId: currentThingId,
      templateId,
    };
    fetchForm(params).then((res) => {
      if (res && !res.failed) {
        dataListDs.loadData(res);
        setFormList(res);
      }
    });
  };
  // 渲染表单
  const renderForm = () => {
    return dataListDs.map((record) => {
      const { dataType, minValue, maxValue, propertyName, guid } = record.toData();
      let options = record.get('options');
      let paramItem;
      switch (dataType) {
        case DATA_TYPE.BOOL:
          try {
            options = JSON.parse(options);
          } catch (e) {
            options = [];
          }
          paramItem = (
            <SelectBox key={guid} record={record} label={propertyName} name="value">
              {options.map(({ code, name }) => (
                <SelectBox.Option key={code} value={code}>
                  {name}
                </SelectBox.Option>
              ))}
            </SelectBox>
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
  };

  // 重置
  const handleReset = () => {
    dataListDs.reset();
    if (!formList) {
      dataPointDs.current.reset();
    }
  };

  // 消息上报
  const handleMessageReport = async (type) => {
    let paramData = {
      tenantId: organizationId,
      thingId: currentThingId,
      thingType: currentThingType,
      qos: currentQos,
    };
    const properties = {};
    const { templateLov, __dirty, ...other } = dataPointDs.current && dataPointDs.current.toData();
    const { otaTask, templateLov: otaLov, __dirty: dirty, ...otaData } =
      otaDs.current && otaDs.current.toData();
    let validate = true;
    if (type === 'dataPoint') {
      validate = await dataPointDs.current.validate(true);
    } else if (type === 'ota') {
      validate = await otaDs.current.validate(true);
    }
    if (validate) {
      setLoading(true);
      switch (type) {
        case 'dataPoint':
          dataListDs.toData().forEach((item) => {
            const { guid, value } = item;
            if (!isNull(value)) {
              if (item.dataType === DATA_TYPE.DATE) {
                const arr = value.split(' ') || [];
                // eslint-disable-next-line prefer-destructuring
                properties[guid] = arr[0];
              } else {
                properties[guid] = value;
              }
            }
          });
          paramData = {
            ...paramData,
            ...other,
            properties,
          };
          break;
        case 'ota':
          paramData = {
            ...paramData,
            ...otaData,
          };
          break;
        case 'custom':
          paramData = {
            ...paramData,
            ...customDs.current.toData(),
          };
          break;
        default:
          break;
      }
      fetchMessageReport(paramData).then((res) => {
        setLoading(false);
        if (res && !res.failed) {
          notification.success();
        }
        if (res && res.failed) {
          notification.error({
            message: res.message,
          });
        }
      });
    }
  };

  // 渲染自定义tab页
  const renderCustomTab = () => {
    return (
      <Form dataSet={customDs} style={{ margin: '0 15px' }}>
        <TextArea name="customContent" style={{ height: '350px' }} />
        <div newLine>
          <div className={styles['test-btns-wrap']}>
            <Button type="reset">{intl.get('hzero.common.button.reset').d('重置')}</Button>
            <Button
              style={{ marginLeft: 8 }}
              color="primary"
              onClick={() => handleMessageReport('custom')}
              disabled={disabled}
            >
              {intl.get('hiot.DeviceSimulation.view.button.messageReport').d('消息上报')}
            </Button>
          </div>
        </div>
      </Form>
    );
  };

  return (
    <>
      <Header
        title={intl.get('hiot.DeviceSimulation.view.message.DeviceSimulation').d('设备模拟上报')}
      />
      <Content>
        <Row>
          <Col span={24}>
            <Form dataSet={formDs} columns={3}>
              <Lov name="guidLov" onChange={(value) => onLovChange(value)} />
              <SelectBox
                name="qos"
                onChange={(value) => onSelectChange(value)}
                label={
                  <React.Fragment>
                    <Tooltip
                      placement="top"
                      title={intl
                        .get('hiot.DeviceSimulation.view.message.helpMsg')
                        .d(
                          'QoS（Quality of Service）代表MQTT协议发送报文时的服务质量等级：QoS=0，协议对此等级应用信息不要求回应确认，也没有重发机制；QoS=1，确保信息到达，但消息重复可能发生。'
                        )}
                    >
                      <Icon type="help_outline" style={{ fontSize: 14, margin: '0 2px 2px 0' }} />
                    </Tooltip>
                    Qos
                  </React.Fragment>
                }
              >
                <Option value="0">0</Option>
                <Option value="1">1</Option>
              </SelectBox>
            </Form>
          </Col>
        </Row>
        <div className={styles['device-simulation-cards-box']}>
          <div className={styles['left-card-wrap']} style={{ flex: 1 }}>
            <Tabs
              style={{ height: '100%', marginTop: '0.14rem' }}
              activeKey={tabKey}
              onChange={(val) => {
                setTabKey(val);
              }}
            >
              <TabPane
                tab={intl.get('hiot.DeviceSimulation.view.button.dataPoint').d('数据点上报')}
                key="dataPoint"
                disabled={isGateway}
              >
                <Spin spinning={loading}>
                  <Form dataSet={dataPointDs} style={{ margin: '0 15px' }}>
                    <Lov name="templateLov" onChange={onDataLovChange} />
                  </Form>
                  <div>
                    <Divider orientation="left">
                      {intl.get('hiot.DeviceSimulation.view.title.dataPointText').d('数据点')}
                    </Divider>
                    <div>
                      {!isEmpty(formList) ? (
                        <Form style={{ margin: '0 15px' }}>{renderForm()}</Form>
                      ) : (
                        <h3 style={{ color: 'gray', textAlign: 'center' }}>
                          {intl
                            .get('hiot.DeviceSimulation.view.button.NoDataPoint')
                            .d('暂无数据点')}
                        </h3>
                      )}
                    </div>
                  </div>
                  <div className={styles['test-btns-wrap']}>
                    <Button onClick={() => handleReset()}>
                      {intl.get('hzero.common.button.reset').d('重置')}
                    </Button>
                    <Button
                      style={{ marginLeft: 8 }}
                      color="primary"
                      disabled={disabled}
                      onClick={() => handleMessageReport('dataPoint')}
                    >
                      {intl.get('hiot.DeviceSimulation.view.button.messageReport').d('消息上报')}
                    </Button>
                  </div>
                </Spin>
              </TabPane>
              <TabPane
                tab={intl.get('hiot.DeviceSimulation.view.button.ota').d('OTA上报')}
                key="ota"
              >
                <Spin spinning={loading}>
                  <Form dataSet={otaDs} style={{ margin: '0 15px' }}>
                    <Lov name="templateLov" onChange={onOtaLovChange} />
                    {otaType === 'UP_OTA_STEP' && <Lov name="otaTask" />}
                    {otaType === 'UP_OTA_STEP' && <NumberField name="otaStep" />}
                    {otaType === 'UP_OTA_STEP' && <TextField name="otaDesc" />}
                    {otaType === 'UP_OTA_REQUEST' && <TextField name="version" />}
                    <div newLine>
                      <div className={styles['test-btns-wrap']}>
                        <Button
                          type="reset"
                          onClick={() => {
                            setOtaType('');
                            otaDs.current.reset();
                          }}
                        >
                          {intl.get('hzero.common.button.reset').d('重置')}
                        </Button>
                        <Button
                          style={{ marginLeft: 8 }}
                          color="primary"
                          disabled={disabled}
                          onClick={() => handleMessageReport('ota')}
                        >
                          {intl
                            .get('hiot.DeviceSimulation.view.button.messageReport')
                            .d('消息上报')}
                        </Button>
                      </div>
                    </div>
                  </Form>
                </Spin>
              </TabPane>
              <TabPane
                tab={intl.get('hiot.DeviceSimulation.view.button.custom').d('自定义上报')}
                key="custom"
              >
                <Spin spinning={loading}>{renderCustomTab()}</Spin>
              </TabPane>
            </Tabs>
          </div>
          <div className={styles['card-wrap']} style={{ marginLeft: 16, flex: 2 }}>
            <Card
              title={intl
                .get('hiot.DeviceSimulation.view.message.DeviceReportLog')
                .d('设备上报日志')}
              extra={
                <Button
                  icon="autorenew"
                  onClick={() => {
                    tableDs.query();
                  }}
                >
                  {intl.get('hzero.common.button.refresh').d('刷新')}
                </Button>
              }
              className={styles['device-simulation-card']}
            >
              <Table dataSet={tableDs} columns={columns} />
            </Card>
          </div>
        </div>
      </Content>
    </>
  );
};

export default formatterCollections({
  code: ['hiot.deviceSimulation', 'hiot.dataReport', 'hiot.common'],
})(DeviceSimulation);
