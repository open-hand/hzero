/**
 * @date 2019-11-26
 * @author: na.yi <na.yi@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 * 设备卡片
 */
import React from 'react';
import {
  Col,
  DataSet,
  Dropdown,
  Icon,
  Menu,
  Pagination,
  Radio,
  Row,
  Select,
  TextField,
} from 'choerodon-ui/pro';
import { Badge, Tag } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { Button as ButtonPermission } from 'components/Permission';

import { deviceCardsDS } from '@/stores/workbenchDS';

import Description from '../components/Description';
import styles from './index.less';

const searchArr = [
  { key: 'thingName', label: intl.get('hiot.common.device.name').d('设备名称') },
  { key: 'thingCode', label: intl.get('hiot.common.device.code').d('设备编码') },
  { key: 'category', label: intl.get('hiot.common.device.type').d('设备类别') },
  { key: 'thingModelName', label: intl.get('hiot.common.model.device.deviceModel').d('设备模型') },
  {
    key: 'gatewayName',
    label: intl.get(`hiot.common.model.common.belongsGateway`).d('所属网关'),
  },
];

const sortArr = [
  { key: 'gateway', label: intl.get('hiot.workbench.orderBy.gateway.status').d('按照网关状态') },
  { key: 'asc', label: intl.get('hiot.workbench.orderBy.warn.count.asc').d('按照告警数顺序') },
  { key: 'desc', label: intl.get('hiot.workbench.orderBy.warn.count.desc').d('按照告警数倒序') },
];

export default class DeviceCards extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      radioValue: '', // 在线离线
      page: 0, // 当前页
      total: 0, // 总数
      dataList: [], // 卡片数据
      selectedValue: '', // 选中的查询条件
      inputValue: '', // 输入的查询条件值
    };
    this.deviceCardsDS = new DataSet(deviceCardsDS());
    this.searchParams = {
      thingName: undefined,
      category: undefined,
      thingCode: undefined,
      gatewayName: undefined,
      thingModelName: undefined,
      orderBy: undefined,
      thingOnline: undefined,
    };
  }

  componentDidMount() {
    const { thingGroupId, infoDS } = this.props;
    const infoData = infoDS.toData();
    if (isEmpty(infoData)) {
      this.loadDeviceCards(0, thingGroupId);
    } else {
      // 初始化查询条件
      const { thingGroupId: prevProjectId, page: prevPage, searchParams } = infoData[0];
      const searchObj = { selectedValue: '', inputValue: '', radioValue: '' };
      const { thingOnline } = searchParams;
      Object.keys(searchParams).forEach((key) => {
        if (key !== 'thingOnline' && key !== 'orderBy' && searchParams[key]) {
          searchObj.selectedValue = key;
          searchObj.inputValue = searchParams[key];
        }
      });
      if (thingOnline) {
        searchObj.radioValue = thingOnline;
      }
      this.searchParams = searchParams;
      this.setState(searchObj);
      this.loadDeviceCards(prevPage, prevProjectId, searchParams);
    }
  }

  // TODO: UNSAFE
  // eslint-disable-next-line camelcase
  UNSAFE_componentWillReceiveProps(nextProps) {
    const { thingGroupId: prevProjectId } = this.props;
    const { thingGroupId } = nextProps;
    if (prevProjectId && prevProjectId !== thingGroupId) {
      this.reloadDeviceCards(thingGroupId);
    }
  }

  @Bind()
  reloadDeviceCards(thingGroupId) {
    this.setState({
      page: 0,
      total: 0,
      radioValue: '',
      dataList: [],
      inputValue: '',
      selectedValue: '',
    });
    this.searchParams = {
      thingName: undefined,
      category: undefined,
      thingCode: undefined,
      gatewayName: undefined,
      thingModelName: undefined,
      orderBy: undefined,
      thingOnline: undefined,
    };
    this.loadDeviceCards(0, thingGroupId, this.searchParams);
  }

  /**
   * 加载设备卡片数据
   * @param page 页数
   * @param projectId 项目Id
   * @param searchParams 查询条件
   */
  @Bind()
  loadDeviceCards(page, thingGroupId, searchParams) {
    this.deviceCardsDS.queryParameter = { page, thingGroupId, ...searchParams };
    this.deviceCardsDS
      .query()
      .then((resp) => {
        if (resp) {
          const { totalElements, number, content = [] } = resp;
          this.setState({
            page: number,
            total: totalElements,
            dataList: content,
          });
        }
      })
      .catch(() => {
        notification.error({
          message: intl
            .get('hiot.workbench.message.error.device.cards.search')
            .d('设备卡片查询失败！'),
        });
      });
  }

  @Bind()
  handleRadioChange(value) {
    const { thingGroupId } = this.props;
    this.searchParams.thingOnline = value;
    this.setState({ radioValue: value, page: 0, total: 0 });
    this.loadDeviceCards(0, thingGroupId, this.searchParams);
  }

  @Bind()
  handleRadioClick(e) {
    const {
      target: { value },
    } = e;
    const { thingOnline } = this.searchParams;
    if (thingOnline === value) {
      this.searchParams.thingOnline = undefined;
      const { thingGroupId } = this.props;
      this.setState({ radioValue: '', page: 0, total: 0 });
      this.loadDeviceCards(0, thingGroupId, this.searchParams);
    }
  }

  @Bind()
  handleSortClick(value) {
    const { key } = value;
    const { thingGroupId } = this.props;
    this.searchParams.orderBy = key;
    this.setState({ page: 0, total: 0 });
    this.loadDeviceCards(0, thingGroupId, this.searchParams);
  }

  @Bind()
  handlePaginationChange(page) {
    const { thingGroupId } = this.props;
    this.setState({ page: page - 1 });
    this.loadDeviceCards(page - 1, thingGroupId, this.searchParams);
  }

  @Bind()
  handleTextChange(value) {
    this.setState({ inputValue: value });
  }

  @Bind()
  handleTextInput(e) {
    const {
      target: { value },
    } = e;
    this.setState({ inputValue: value });
  }

  @Bind()
  handleSearch() {
    const { inputValue, selectedValue } = this.state;
    if (!selectedValue) {
      notification.warning({
        message: intl
          .get('hiot.workbench.device.cards.message.select.search.condition')
          .d('请选择查询条件！'),
      });
      return;
    }
    const { thingGroupId } = this.props;
    this.searchParams[selectedValue] = inputValue;
    // 清空输入框的其他查询条件
    Object.keys(this.searchParams).forEach((key) => {
      if (key !== selectedValue && key !== 'orderBy' && key !== 'thingOnline') {
        this.searchParams[key] = undefined;
      }
    });
    this.setState({ page: 0, total: 0 });
    this.loadDeviceCards(0, thingGroupId, this.searchParams);
  }

  @Bind()
  handleSelectedChange(value) {
    this.setState({ selectedValue: value });
  }

  /**
   * 跳转到设备详情
   * 保存当前页面的查询条件到InfoDS中
   * @param deviceId 设备ID
   */
  @Bind()
  linkToDeviceDetail(deviceId, guid) {
    const { onLinkToDevice, infoDS, thingGroupId } = this.props;
    const { page } = this.state;
    onLinkToDevice(deviceId, 'device', 'deviceCard', guid);
    infoDS.removeAll();
    infoDS.create({ tabKey: '2', page, searchParams: this.searchParams, thingGroupId }, 0);
  }

  render() {
    const { path } = this.props;
    const { radioValue, page, total, dataList, selectedValue, inputValue } = this.state;
    const menu = (
      <Menu onClick={this.handleSortClick}>
        {sortArr.map((item) => (
          <Menu.Item key={item.key}>{item.label}</Menu.Item>
        ))}
      </Menu>
    );
    return (
      <>
        <Row>
          <Col span={12}>
            <Radio
              name="base"
              value="1"
              onChange={this.handleRadioChange}
              onClick={this.handleRadioClick}
              checked={radioValue === '1'}
            >
              {intl.get('hiot.common.view.title.online').d('在线')}
            </Radio>
            <Radio
              name="base"
              value="0"
              onChange={this.handleRadioChange}
              onClick={this.handleRadioClick}
              checked={radioValue === '0'}
            >
              {intl.get('hiot.common.view.title.offline').d('离线')}
            </Radio>
          </Col>
          <Col span={10}>
            <Select onChange={this.handleSelectedChange} value={selectedValue}>
              {searchArr.map((item) => (
                <Select.Option key={item.key} value={item.key}>
                  {item.label}
                </Select.Option>
              ))}
            </Select>
            <TextField
              value={inputValue}
              onInput={this.handleTextInput}
              onChange={this.handleTextChange}
              onEnterDown={this.handleSearch}
            />
          </Col>
          <Col span={2}>
            <Dropdown overlay={menu} trigger={['hover']}>
              <Icon type="swap_vert" />
            </Dropdown>
          </Col>
        </Row>
        <Row type="flex" justify="start" className={styles['workbench-device-cards-row']}>
          {dataList.map((device) => {
            const {
              guid,
              thingId,
              thingName,
              thingCode,
              thingOnline,
              thingModelName,
              categoryMeaning,
              gatewayOnline,
              name,
              gatewayName,
              faultNum,
              predictNum,
              abnormalNum,
            } = device;
            return (
              <Col span={8} key={thingId}>
                <div className={styles['workbench-device-cards-box']}>
                  <div style={{ marginRight: 5 }}>
                    <Badge status={thingOnline === '0' ? 'error' : 'success'} />
                  </div>
                  <div style={{ width: '100%' }}>
                    <div className={styles['workbench-device-cards-title']}>
                      <p style={{ fontSize: '0.16rem' }}>{thingName}</p>
                      <ButtonPermission
                        type="text"
                        permissionList={[
                          {
                            code: `${path}.button.detail`,
                            type: 'button',
                            meaning: '设备卡片-详情',
                          },
                        ]}
                        role="none"
                        style={{ marginRight: 22 }}
                        onClick={() => this.linkToDeviceDetail(thingId, guid)}
                      >
                        {intl.get('hzero.common.button.detail').d('详情')}
                      </ButtonPermission>
                    </div>
                    <Description label={intl.get('hiot.common.device.code').d('设备编码')}>
                      {thingCode}
                    </Description>
                    <Description
                      label={intl
                        .get(`hiot.common.model.device.parentDeviceName`)
                        .d('所属设备分组')}
                    >
                      {name}
                    </Description>
                    <Description label={intl.get('hiot.common.device.type').d('设备类别')}>
                      {categoryMeaning}
                    </Description>
                    <Description
                      label={intl.get('hiot.common.model.device.deviceModel').d('设备模型')}
                    >
                      {thingModelName}
                    </Description>
                    <Description
                      label={intl.get(`hiot.common.model.common.belongsGateway`).d('所属网关')}
                    >
                      {gatewayName}
                    </Description>
                    <Description
                      label={intl.get('hiot.gatewayManage.model.gateway.status').d('网关状态')}
                      mode="node"
                    >
                      {gatewayOnline ? (
                        <>
                          {gatewayOnline === '0' ? (
                            <Tag color="#D50000">
                              {intl.get('hiot.common.view.title.offline').d('离线')}
                            </Tag>
                          ) : (
                            <Tag color="#00CC00">
                              {intl.get('hiot.common.view.title.online').d('在线')}
                            </Tag>
                          )}
                        </>
                      ) : (
                        <Tag color="#FFFFFF" />
                      )}
                    </Description>
                    <Description
                      label={intl
                        .get('hiot.workbench.device.cards.not.recovered.warn')
                        .d('未恢复告警情况')}
                      mode="node"
                    />
                    <Row>
                      <Col span={8}>
                        <span>{intl.get('hiot.workbench.device.cards.faultNum').d('故障数')}</span>
                      </Col>
                      <Col span={8}>
                        <span>
                          {intl.get('hiot.workbench.device.cards.abnormalNum').d('异常数')}
                        </span>
                      </Col>
                      <Col span={8}>
                        <span>
                          {intl.get('hiot.workbench.device.cards.predictNum').d('预警数')}
                        </span>
                      </Col>
                    </Row>
                    <Row>
                      <Col span={8}>
                        <span className={styles['workbench-device-cards-warn-value']}>
                          {faultNum}
                        </span>
                        <span>{intl.get('hzero.common.unit.piece').d('件')}</span>
                      </Col>
                      <Col span={8}>
                        <span className={styles['workbench-device-cards-warn-value']}>
                          {abnormalNum}
                        </span>
                        <span>{intl.get('hzero.common.unit.piece').d('件')}</span>
                      </Col>
                      <Col span={8}>
                        <span className={styles['workbench-device-cards-warn-value']}>
                          {predictNum}
                        </span>
                        <span>{intl.get('hzero.common.unit.piece').d('件')}</span>
                      </Col>
                    </Row>
                  </div>
                </div>
              </Col>
            );
          })}
        </Row>
        <Pagination
          style={{ float: 'right' }}
          showSizeChanger={false}
          showPager
          total={total}
          pageSize={9}
          page={page + 1}
          onChange={this.handlePaginationChange}
        />
      </>
    );
  }
}
