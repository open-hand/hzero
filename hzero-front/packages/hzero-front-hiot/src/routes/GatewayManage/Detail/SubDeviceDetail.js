/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-11 16:21:25
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 网关管理-子设备详情
 * 包括绑定子设备与编辑子设备
 * 通过url中的action字段来区分edit代表编辑 detail代表详情 bind代表绑定
 * 同时在数据点解析tab页中的操作列也是由action来控制 当action为edit/bind时显示
 * 还有页面右侧的操作按钮(显示保存或者编辑)
 */
import React from 'react';
import {
  Col,
  DataSet,
  Form,
  Lov,
  Modal,
  NumberField,
  Row,
  Select,
  Table,
  TextField,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isNull, isEmpty } from 'lodash';
import queryString from 'querystring';

import { Content, Header } from 'components/Page';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import { getCurrentTenant, getCurrentOrganizationId } from 'utils/utils';

import intl from 'utils/intl';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';

import { Button as ButtonPermission } from 'components/Permission';
import {
  BIND_ACTION,
  DETAIL_ACTION,
  EDGINK_TYPE,
  EDIT_ACTION,
  NEW_ACTION,
} from '@/utils/constants';

import {
  edginkTableDS,
  modbusTableDS,
  subDeviceEdglinkDS,
  subDeviceModbusDS,
  subDeviceOperatorDS,
} from '@/stores/gatewayManageDS';
import TriggeredDisableLov from '@/routes/components/TriggeredDisableLov';

const modalKey = Modal.key();

const { Option } = Select;

const { tenantNum } = getCurrentTenant();

const dataLengthOption = [1, 8, 16, 32];
@formatterCollections({
  code: ['hiot.dataPointTemplate', 'hiot.deviceManage', 'hiot.gatewayManage', 'hiot.common'],
})
export default class SubDeviceDetail extends React.Component {
  constructor(props) {
    super(props);
    const { match: { params = {} } = {}, location: { state = {}, search = {} } = {} } = this.props;
    const { action, gatewayId, thingId } = params;
    const {
      subDeviceInfo: { endpointProtocol, terminalType, configId, gatewayAction, thingGroupId } = {},
    } = state;
    const { gatewayCode } = queryString.parse(search.substring(1));
    this.subDeviceEdglinkDS = new DataSet(subDeviceEdglinkDS()); // Edglink
    this.subDeviceModbusDS = new DataSet(subDeviceModbusDS()); // modbus
    this.edginkTableDS = new DataSet({
      paging: action === 'detail',
      ...edginkTableDS(),
    }); // edgink协议的数据点解析表格的ds
    this.modbusTableDS = new DataSet({
      paging: action === 'detail',
      ...modbusTableDS(),
    }); // modbus协议的数据点解析-表格的ds
    this.subDeviceOperatorDS = new DataSet(subDeviceOperatorDS()); // 子设备发送请求使用
    this.dataPointParseDS = new DataSet({
      data: [{}],
      fields: [
        {
          name: 'property',
          type: 'object',
          lovCode: 'HIOT.LOV.PROPERTY',
          label: intl.get('hiot.common.data.point.name').d('数据点名称'),
          multiple: true,
          lovPara: {
            tenantId: getCurrentOrganizationId(),
            isParse: 1,
          },
          cascadeMap: { thingId: 'thingId' },
        },
      ],
    });
    this.collectItemsDs = new DataSet({
      data: [{}],
      fields: [
        {
          name: 'property',
          type: 'object',
          lovCode: 'HIOT.EGK_DC_DEVICE_TAG',
          multiple: true,
          lovPara: {
            tenantId: getCurrentOrganizationId(),
            gatewayId,
          },
          cascadeMap: { thingId: 'thingId' },
        },
      ],
    });
    this.state = {
      action,
      gatewayId,
      endpointProtocol: endpointProtocol || EDGINK_TYPE, // 协议类型
      thingId,
      rtuShow: false,
      configId,
      gatewayAction,
      thingGroupId,
      edginkDeleteList: [],
      modbusDeleteList: [],
      gatewayCode,
    };
    this.terminalType = terminalType;
    this.connectionMode = null;
  }

  componentDidMount() {
    const { action, endpointProtocol, thingId, configId, thingGroupId, gatewayId } = this.state;
    this.subDeviceEdglinkDS.getField('thingName').setLovPara('configId', configId);
    this.subDeviceEdglinkDS.getField('thingName').setLovPara('thingGroupId', thingGroupId);
    this.subDeviceModbusDS.getField('thingName').setLovPara('configId', configId);
    this.subDeviceModbusDS.getField('thingName').setLovPara('thingGroupId', thingGroupId);
    this.subDeviceEdglinkDS.getField('reportedTopicLov').setLovPara('gatewayId', gatewayId);
    if (action !== BIND_ACTION) {
      if (endpointProtocol === EDGINK_TYPE) {
        // 查询子设备信息
        this.subDeviceEdglinkDS.queryParameter = { deviceType: EDGINK_TYPE, thingId };
        this.subDeviceEdglinkDS.query().then((resp) => {
          const { thingName, thingCode, thingId: edglinkThingId, edginkDcDevice } = resp;
          this.subDeviceEdglinkDS
            .get(0)
            .set('thingName', { thingName, thingCode, thingId: edglinkThingId });
          this.collectItemsDs.getField('property').setLovPara('dcDeviceCode', edginkDcDevice);
          this.initAddDataPointParseThingId(resp);
        });
        // 查询子设备解析信息
        this.edginkTableDS.queryParameter = { deviceType: EDGINK_TYPE, thingId };
        this.edginkTableDS.query().then(({ content }) => {
          this.collectItemsDs.get(0).set('property', content);
        });
      } else {
        // 查询子设备信息
        this.subDeviceModbusDS.queryParameter = { deviceType: 'MODBUS', thingId };
        this.subDeviceModbusDS.query().then((resp) => {
          const { thingName, thingCode, connectionMode, thingId: modbusThingId } = resp;
          this.setState({
            rtuShow: connectionMode === 'RTU',
          });
          this.subDeviceModbusDS
            .get(0)
            .set('thingName', { thingName, thingCode, thingId: modbusThingId });
          this.initAddDataPointParseThingId(resp);
        });
        // 查询子设备解析信息
        this.modbusTableDS.queryParameter = { deviceType: 'MODBUS', thingId };
        this.modbusTableDS.query().then(({ content }) => {
          this.dataPointParseDS.get(0).set('property', content);
        });
      }
    } else if (thingGroupId) {
      this.subDeviceEdglinkDS.create({ thingGroupId }, 0);
      this.subDeviceModbusDS.create({ thingGroupId }, 0);
    } else {
      this.back();
    }
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
   * 删除表格中的数据
   * @param record
   * @param dataSet
   */
  @Bind()
  handleDelete(record, dataSet) {
    const { endpointProtocol, edginkDeleteList, modbusDeleteList } = this.state;
    const parserId = record.get('parserId');
    if (parserId) {
      if (endpointProtocol === EDGINK_TYPE) {
        this.setState({
          edginkDeleteList: !edginkDeleteList.includes(parserId)
            ? [...edginkDeleteList, parserId]
            : edginkDeleteList,
        });
      } else {
        this.setState({
          modbusDeleteList: !modbusDeleteList.includes(parserId)
            ? [...modbusDeleteList, parserId]
            : modbusDeleteList,
        });
      }
    }

    const delPropId = record.get('propertyId');
    const { property } = this.dataPointParseDS.toData()[0];
    const exitsProperty = property.filter(({ propertyId }) => propertyId !== delPropId);
    if (endpointProtocol === EDGINK_TYPE) {
      this.collectItemsDs.get(0).set('property', exitsProperty);
    } else {
      this.dataPointParseDS.get(0).set('property', exitsProperty);
    }
    dataSet.remove(record);
    const dataList = dataSet.toData();
    dataSet.loadData([]);
    dataList.forEach((item) => {
      dataSet.create(item);
    });
  }

  /**
   * 根据页面(编辑/详情)来判断是否显示操作按钮
   * @returns {{renderer: (function({record: *}): *), name: string}}
   */
  @Bind()
  operationColumn() {
    const {
      match: { path },
    } = this.props;
    const { action } = this.state;
    if ([EDIT_ACTION, BIND_ACTION].includes(action)) {
      return {
        name: 'operation',
        width: 80,
        lock: 'right',
        header: <span>{intl.get('hzero.common.button.action').d('操作')}</span>,
        align: 'center',
        renderer: ({ record, dataSet }) => (
          <span className="action-link">
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '子设备详情-删除',
                },
              ]}
              onClick={() => this.handleDelete(record, dataSet)}
            >
              {intl.get('hzero.common.button.delete').d('删除')}
            </ButtonPermission>
          </span>
        ),
      };
    }
  }

  // 数据点类型为edgink的table列
  get edginkColumns() {
    return [
      { name: 'edginkItem' },
      { name: 'dcDeviceTagAddress' },
      { name: 'propertyName' },
      { name: 'requestInterval', align: 'left' },
      this.operationColumn(),
    ];
  }

  // 数据点类型为modbus-tcp/rtu的table列
  get modbusColumns() {
    const { action } = this.state;
    const editor = [EDIT_ACTION, BIND_ACTION].includes(action);
    return [
      { name: 'propertyName' },
      { name: 'propertyCode' },
      { name: 'propertyModelCode' },
      { name: 'dataType', editor, width: 150 },
      { name: 'dataLength', editor, width: 150 },
      { name: 'operationCode', editor, width: 150 },
      {
        name: 'registerStartAddress',
        width: 150,
        editor: (record) => {
          const startNum = Number(record.get('operationCode')) - 1;
          return <TextField prefix={String(startNum || '')} />;
        },
      },
      { name: 'readLength', editor, width: 150 },
      {
        name: 'bitSegment',
        editor,
      },
      { name: 'requestInterval' },
      this.operationColumn(),
    ];
  }

  /**
   * 获取页面title 当操作类型为绑定子设备时调用该方法
   */
  get headerTitle() {
    const { action } = this.state;
    if (action === EDIT_ACTION) {
      return intl.get('hiot.dataPointTemplate.view.editSubDevice').d('编辑子设备');
    }
    return intl.get('hiot.dataPointTemplate.view.addSubDevice').d('绑定子设备');
  }

  /**
   * 保存子设备信息
   */
  @Bind()
  async handleSubDeviceSave() {
    const { endpointProtocol } = this.state;
    if (endpointProtocol === EDGINK_TYPE) {
      this.edginkTableDS.validate().then((res) => {
        if (res) {
          this.validateFormData(endpointProtocol, this.subDeviceEdglinkDS, this.edginkTableDS);
        }
      });
    } else {
      this.modbusTableDS.validate().then((res) => {
        if (res) {
          this.validateFormData(endpointProtocol, this.subDeviceModbusDS, this.modbusTableDS);
        }
      });
    }
  }

  /**
   * 校验子设备表单数据
   * @param type 协议类型
   * @param subDeviceDS 子设备的DS
   * @param parseDS 子设备解析规则DS
   */
  @Bind()
  async validateFormData(type, subDeviceDS, parseDS) {
    const {
      endpointProtocol,
      gatewayId,
      action,
      configId,
      edginkDeleteList,
      modbusDeleteList,
    } = this.state;
    const subDevice = {
      type: action,
      gwThingRel: {
        endpointProtocol,
        gatewayId,
        terminalType: this.terminalType,
        thingId: 0,
        configId,
      },
      gwEdgink: {},
      gwModbus: {},
      gwEdginkParsers: [],
      gwModbusParsers: [],
      gwEdginkParserIds: [], // 删除Edgink的解析规则Id
      gwModbusParserIds: [], // 删除Modbus的解析规则Id
    };
    subDeviceDS.current.validate(true).then((resp) => {
      if (resp) {
        const { thingName, thingId, ...restFields } = subDeviceDS.toData()[0]; // 获取子设备信息
        subDevice.gwThingRel.thingId = thingId;
        const { data: parseRecords } = parseDS;
        const allParseData = parseRecords.map(({ data }) => data);
        if (type === EDGINK_TYPE) {
          subDevice.gwEdgink = { ...restFields };
          subDevice.gwEdginkParsers = allParseData.map((edglinkParse) => {
            const { property, thingId: noUseThingId, ...edglinkRestFieds } = edglinkParse;
            return {
              ...property,
              ...edglinkRestFieds,
            };
          });
          subDevice.gwEdginkParserIds = edginkDeleteList;
        } else {
          subDevice.gwModbus = { ...restFields };
          subDevice.gwModbusParsers = allParseData.map((parse) => {
            const { property, thingId: noUseThingId, ...restParseFields } = parse;
            return {
              ...property,
              ...restParseFields,
            };
          });
          subDevice.gwModbusParserIds = modbusDeleteList;
        }
        this.sendBindSubDeviceRequest(subDevice);
      }
    });
  }

  @Bind()
  back() {
    const { history } = this.props;
    const { gatewayAction, gatewayId } = this.state;
    history.push(`/hiot/gateway/manage/${gatewayAction}/${gatewayId}`);
  }

  /**
   * 发送绑定子设备的请求
   * @param subDevice 请求参数
   */
  @Bind()
  async sendBindSubDeviceRequest(subDevice) {
    const {
      location: { search },
    } = this.props;
    const { hubType } = queryString.parse(search.substring(1));
    const { gatewayAction, gatewayId } = this.state;
    this.subDeviceOperatorDS.create(subDevice, 0);
    const resp = await this.subDeviceOperatorDS.submit();
    if (resp) {
      this.props.history.push({
        pathname: `/hiot/gateway/manage/${gatewayAction}/${gatewayId}`,
        state: hubType,
      });
    }
  }

  /**
   * 设备协议类型为edgink的单条数据点解析的新建/编辑
   * @param action 标识是新建还是编辑
   */
  @Bind()
  renderEdginkParseForm(action) {
    return (
      <Form dataSet={this.edginkTableDS} columns={1}>
        <Lov name="property" disabled={action === EDIT_ACTION} />
        <TextField name="propertyCode" disabled />
        <TextField name="requestInterval" disabled />
        <TextField name="edginkItem" labelWidth={140} />
      </Form>
    );
  }

  /**
   * 设备协议类型为modbus的单条数据点解析的新建/编辑
   * @param action 标识是新建还是编辑
   */
  @Bind()
  renderModbusParseForm(action) {
    const operationCodeOption = [
      intl
        .get('hiot.gatewayManage.view.dataLengthOption.coilStatus')
        .d('01 线圈状态(Coil Status)(0x)'),
      intl
        .get('hiot.gatewayManage.view.dataLengthOption.inputStatus')
        .d('02 输入状态 (Input Status)(1x)'),
      intl
        .get('hiot.gatewayManage.view.dataLengthOption.holdingRegister')
        .d('03 保持寄存器(Holding Register)(4x)'),
      intl
        .get('hiot.gatewayManage.view.dataLengthOption.inputRegister')
        .d('04 输入寄存器(Input Register)(3x)'),
    ];

    return (
      <Form dataSet={this.modbusTableDS} columns={2}>
        <Lov name="property" disabled={action === EDIT_ACTION} labelWidth={120} />
        <TextField name="propertyCode" disabled labelWidth={120} />
        <TextField name="requestInterval" disabled />
        <TextField name="propertyModelCode" disabled />
        <Select name="dataType" />
        <Select name="dataLength">
          {dataLengthOption.map((item) => (
            <Option key={item} value={item}>
              {item}
            </Option>
          ))}
        </Select>
        <Select name="operationCode" disabled={action === EDIT_ACTION}>
          {operationCodeOption.map((item, index) => {
            const value = index + 1;
            return (
              <Option key={value} value={value}>
                {item}
              </Option>
            );
          })}
        </Select>
        <TextField name="registerStartAddress" disabled={action === EDIT_ACTION} />
        <NumberField name="readLength" step={1} disabled={action === EDIT_ACTION} />
        <NumberField name="bitSegment" step={1} />
      </Form>
    );
  }

  /**
   * lov取消选择数据点后将相关数据从表格中移除
   * @param lovRecs
   */
  @Bind()
  deleteTableRecs(lovRecs) {
    const { modbusDeleteList = [] } = this.state;
    const newList = [...modbusDeleteList];
    const selectedIds = new Set(lovRecs.map(({ propertyId }) => propertyId));
    this.modbusTableDS.forEach((record) => {
      const recId = record.get('propertyId');
      if (!selectedIds.has(recId)) {
        const parserId = record.get('parserId');
        if (parserId && !modbusDeleteList.includes(parserId)) {
          newList.push(parserId);
        }
        this.modbusTableDS.remove(record);
      }
    });
    const dataList = this.modbusTableDS.toData();
    this.setState({
      modbusDeleteList: newList,
    });
    this.modbusTableDS.loadData([]);
    dataList.forEach((item) => {
      this.modbusTableDS.create(item);
    });
  }

  @Bind()
  deleteCollectItems(lovRecs) {
    const { edginkDeleteList } = this.state;
    const newList = [...edginkDeleteList];
    const selectedIds = new Set(lovRecs.map(({ dcDeviceTagId }) => dcDeviceTagId));
    this.edginkTableDS.forEach((record) => {
      const recId = record.get('dcDeviceTagId');
      const parserId = record.get('parserId');
      if (!selectedIds.has(recId)) {
        if (parserId && !edginkDeleteList.includes(parserId)) {
          newList.push(parserId);
        }
        this.edginkTableDS.remove(record);
      }
    });
    this.setState({
      edginkDeleteList: newList,
    });
    const dataList = this.edginkTableDS.toData();
    this.edginkTableDS.loadData(dataList);
  }

  /**
   * 处理添加数据点解析
   * @param lovRecs
   */
  @Bind()
  handleAddDataPoint(lovRecs) {
    const newLovRecs = !isNull(lovRecs) ? lovRecs : [];
    this.deleteTableRecs(newLovRecs);
    newLovRecs.forEach((rec) => {
      const existsId = new Set(this.modbusTableDS.toData().map(({ propertyId }) => propertyId));
      if (!existsId.has(rec.propertyId)) {
        this.modbusTableDS.create({ editing: true, ...rec }, 0);
      }
    });
  }

  // 添加采集项
  @Bind()
  handleCollectItems(list) {
    const newLovRecs = !isNull(list) ? list : [];
    this.deleteCollectItems(newLovRecs);
    newLovRecs.forEach((rec) => {
      const existsId = new Set(
        this.edginkTableDS.toData().map(({ dcDeviceTagId }) => dcDeviceTagId)
      );
      if (!existsId.has(rec.dcDeviceTagId)) {
        this.edginkTableDS.create({ editing: true, ...rec }, 0);
      }
    });
  }

  @Bind()
  openAddDataPointModal() {
    const { endpointProtocol } = this.state;
    let thingId;
    if (endpointProtocol === EDGINK_TYPE) {
      thingId = this.collectItemsDs.get(0).get('thingId');
    } else {
      thingId = this.dataPointParseDS.get(0).get('thingId');
    }
    if (thingId === undefined) {
      notification.warning({
        message: intl.get('hiot.gatewayManage.view.not.choose.device').d('请选择设备'),
      });
      return false;
    }
    return true;
  }

  get commonButtons() {
    return [
      <TriggeredDisableLov
        openLogic={this.openAddDataPointModal}
        onChange={this.handleAddDataPoint}
        dataSet={this.dataPointParseDS}
        clearButton={false}
        key="addDataPoint"
        name="property"
        mode="button"
        icon="add"
      >
        {intl.get('hiot.gatewayManage.view.add.parser').d('添加数据点解析')}
      </TriggeredDisableLov>,
    ];
  }

  get edginkButtons() {
    return [
      <TriggeredDisableLov
        openLogic={this.openAddDataPointModal}
        onChange={this.handleCollectItems}
        dataSet={this.collectItemsDs}
        clearButton={false}
        key="addDataPoint"
        name="property"
        mode="button"
        icon="add"
      >
        {intl.get('hiot.gatewayManage.view.button.addCollect').d('添加采集项')}
      </TriggeredDisableLov>,
    ];
  }

  /**
   * 打开新建/编辑解析modal框
   * @param action new 表示新建解析 edit表示编辑解析
   */
  @Bind()
  openParseModal(action) {
    const { endpointProtocol } = this.state;
    let children = null;
    if (endpointProtocol === EDGINK_TYPE) {
      children = this.renderEdginkParseForm(action);
      const { thingName } = this.subDeviceEdglinkDS.toData()[0];
      if (action === NEW_ACTION && !isEmpty(thingName)) {
        const { thingId } = thingName;
        this.edginkTableDS.create({}, 0);
        this.edginkTableDS.get(0).set('thingId', thingId);
      }
    } else {
      children = this.renderModbusParseForm(action);
      const { thingName } = this.subDeviceModbusDS.toData()[0];
      if (action === NEW_ACTION && !isEmpty(thingName)) {
        const { thingId } = thingName;
        this.modbusTableDS.create({}, 0);
        this.modbusTableDS.get(0).set('thingId', thingId);
      }
    }
    Modal.open({
      key: modalKey,
      drawer: true,
      style: {
        width: 600,
      },
      title:
        action === NEW_ACTION
          ? intl.get('hiot.gatewayManage.view.title.newParser').d('新建解析')
          : intl.get('hiot.gatewayManage.view.title.editParser').d('编辑解析'),
      children,
      onOk: () => {
        if (action === NEW_ACTION) {
          if (endpointProtocol === EDGINK_TYPE) {
            return this.edginkTableDS.validate();
          }
          return this.modbusTableDS.validate();
        }
      },
      onCancel: () => {
        this.closeModal(action, endpointProtocol);
      },
    });
  }

  /**
   * 关闭Modal框方法
   * @param action
   * @param endpointProtocol
   */
  @Bind()
  closeModal(action, endpointProtocol) {
    if (action === NEW_ACTION) {
      if (endpointProtocol === EDGINK_TYPE) {
        this.edginkTableDS.remove(this.edginkTableDS.get(0));
      } else {
        this.modbusTableDS.remove(this.modbusTableDS.get(0));
      }
    }
  }

  @Bind()
  handleConnectionModeChange(value) {
    this.connectionMode = value;
    this.setState({
      rtuShow: value === 'RTU',
    });
  }

  /**
   * 处理协议（连接方式）变化
   * @param value
   */
  @Bind()
  handleProtocolChange(value) {
    this.setState({ endpointProtocol: value });
    this.subDeviceEdglinkDS.get(0).set('endpointProtocol', value);
    this.subDeviceModbusDS.get(0).set('endpointProtocol', value);
    this.clearDataPointParseTable();
  }

  /**
   * 处理设备（名称）变化
   * @param lovRec
   */
  @Bind()
  handleDeviceChange(lovRec) {
    const { thingId } = lovRec || {};
    this.subDeviceEdglinkDS.get(0).set('thingName', lovRec);
    this.subDeviceModbusDS.get(0).set('thingName', lovRec);
    this.dataPointParseDS.get(0).set('property', []);
    this.dataPointParseDS.get(0).set('thingId', thingId);
    this.collectItemsDs.get(0).set('property', []);
    this.collectItemsDs.get(0).set('thingId', thingId);
    this.clearDataPointParseTable();
  }

  /**
   * 清空数据点解析表格
   */
  @Bind()
  clearDataPointParseTable() {
    this.edginkTableDS.reset();
    this.modbusTableDS.reset();
  }

  /**
   * 初始化“添加数据点解析”lov的查询参数thingId
   * @param data
   */
  @Bind()
  initAddDataPointParseThingId(data) {
    const { thingId } = data;
    this.dataPointParseDS.get(0).set('thingId', thingId);
    this.collectItemsDs.get(0).set('thingId', thingId);
  }

  @Bind()
  lovChange(value) {
    const { gatewayCode } = this.state;
    if (value) {
      const { dcDeviceCode } = value;
      this.subDeviceEdglinkDS.current.set(
        'reportedTopic',
        `edgink/ReadPlc/${tenantNum}/${gatewayCode}/${dcDeviceCode}/+`
      );
      this.collectItemsDs.getField('property').setLovPara('dcDeviceCode', dcDeviceCode);
    } else {
      this.subDeviceEdglinkDS.current.set('reportedTopic', '');
      this.collectItemsDs.getField('property').setLovPara('dcDeviceCode', '');
    }
  }

  /**
   * 子设备协议类型为edgink
   */
  @Bind()
  renderEdginkForm() {
    const { action } = this.state;
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.subDeviceEdglinkDS} columns={2} labelWidth={140}>
            <Lov
              name="thingName"
              disabled={action !== BIND_ACTION}
              onChange={this.handleDeviceChange}
            />
            <TextField name="thingCode" disabled />
            <Select
              name="endpointProtocol"
              disabled={action !== BIND_ACTION}
              onChange={this.handleProtocolChange}
            />
            <TextField name="edginkName" disabled={action === DETAIL_ACTION} />
            <Select name="reportedWay" disabled={action === DETAIL_ACTION} />
            {[EDIT_ACTION, DETAIL_ACTION].includes(action) ? (
              <TextField name="reportedTopic" disabled />
            ) : (
              <Lov name="reportedTopicLov" onChange={this.lovChange} />
            )}
            <Select name="desiredWay" disabled={action === DETAIL_ACTION} />
            <TextField name="desiredTopic" disabled required />
            <TextField name="description" disabled={action === DETAIL_ACTION} />
          </Form>
        </Col>
      </Row>
    );
  }

  /**
   * 子设备协议类型为modbus
   */
  @Bind()
  renderModbusForm() {
    const { action, rtuShow } = this.state;
    const tcpFields = [
      <TextField key="tcpHost" name="tcpHost" disabled={action !== BIND_ACTION} />,
      <NumberField key="tcpPort" name="tcpPort" disabled={action !== BIND_ACTION} />,
    ];
    const rtuFields = [
      <TextField key="rtuSerialPort" name="rtuSerialPort" disabled={action !== BIND_ACTION} />,
      <NumberField key="rtuBaudRate" name="rtuBaudRate" disabled={action !== BIND_ACTION} />,
      <Select key="rtuDataBit" name="rtuDataBit" disabled={action !== BIND_ACTION} defaultValue={8}>
        <Option value={7}>7</Option>
        <Option value={8}>8</Option>
      </Select>,
      <Select
        key="rtuCalibrationMethod"
        name="rtuCalibrationMethod"
        disabled={action !== BIND_ACTION}
      />,
      <Select key="rtuStopBit" name="rtuStopBit" disabled={action !== BIND_ACTION} defaultValue={1}>
        <Option value={1}>1</Option>
        <Option value={2}>2</Option>
      </Select>,
    ];
    return (
      <Row>
        <Col span={16}>
          <Form dataSet={this.subDeviceModbusDS} columns={2} labelWidth={140}>
            <Lov
              name="thingName"
              disabled={action !== BIND_ACTION}
              onChange={this.handleDeviceChange}
            />
            <TextField name="thingCode" disabled />
            <Select
              name="endpointProtocol"
              disabled={action !== BIND_ACTION}
              onChange={this.handleProtocolChange}
            />
            <Select
              name="connectionMode"
              defaultValue="TCP"
              disabled={action !== BIND_ACTION}
              onChange={this.handleConnectionModeChange}
            />
            <NumberField name="slaveStation" disabled={action !== BIND_ACTION} />
            {rtuShow ? rtuFields : tcpFields}
            <TextField name="description" disabled={action === DETAIL_ACTION} />
          </Form>
        </Col>
      </Row>
    );
  }

  /**
   * 子协议类型为edgink的表格
   */
  @Bind()
  renderEdginkTable() {
    const { action } = this.state;
    return [
      <Table
        key="edglinkTable"
        selectionMode="click"
        columns={this.edginkColumns}
        dataSet={this.edginkTableDS}
        buttons={action !== DETAIL_ACTION && this.edginkButtons}
      />,
    ];
  }

  /**
   * 子协议类型为modbus-rtu的表格
   */
  @Bind()
  renderModbusTable() {
    const { action } = this.state;
    return [
      <Table
        key="modbusTable"
        selectionMode="click"
        columns={this.modbusColumns}
        dataSet={this.modbusTableDS}
        buttons={action !== DETAIL_ACTION && this.commonButtons}
      />,
    ];
  }

  /**
   * 根据协议类型来选择性渲染表格
   */
  @Bind()
  renderTable() {
    const { endpointProtocol } = this.state;
    if (endpointProtocol === EDGINK_TYPE) {
      return this.renderEdginkTable();
    }
    return this.renderModbusTable();
  }

  /**
   * 根据协议类型来选择性渲染表单
   */
  @Bind()
  renderBasicForm = () => {
    const { endpointProtocol } = this.state;
    if (endpointProtocol === EDGINK_TYPE) {
      return this.renderEdginkForm();
    }
    return this.renderModbusForm();
  };

  render() {
    const {
      match: { path },
      location: { search },
    } = this.props;
    const { hubType } = queryString.parse(search.substring(1));
    const { action, gatewayId, gatewayAction, endpointProtocol } = this.state;
    const title = this.headerTitle;
    return (
      <>
        <Header
          title={title}
          backPath={`/hiot/gateway/manage/${gatewayAction}/${gatewayId}?hubType=${hubType}`}
        >
          {[EDIT_ACTION, BIND_ACTION].includes(action) ? (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.save`,
                  type: 'button',
                  meaning: '子设备详情-保存',
                },
              ]}
              color="primary"
              icon="save"
              onClick={this.handleSubDeviceSave}
            >
              {intl.get('hzero.common.button.save').d('保存')}
            </ButtonPermission>
          ) : (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}.button.edit`,
                  type: 'button',
                  meaning: '子设备详情-编辑',
                },
              ]}
              color="primary"
              icon="mode_edit"
              onClick={this.handleEdit}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          )}
        </Header>
        <Content>
          <Card
            className={DETAIL_CARD_CLASSNAME}
            bordered={false}
            title={intl.get('hiot.common.view.baseInfo').d('基本信息')}
          >
            {this.renderBasicForm()}
          </Card>
          {(endpointProtocol !== EDGINK_TYPE ||
            (endpointProtocol === EDGINK_TYPE && hubType === 'OWN')) && (
            <Card
              className={DETAIL_CARD_CLASSNAME}
              bordered={false}
              title={intl
                .get('hiot.gatewayManage.view.message.title.dataPointParse')
                .d('数据点解析')}
            >
              {this.renderTable()}
            </Card>
          )}
        </Content>
      </>
    );
  }
}
