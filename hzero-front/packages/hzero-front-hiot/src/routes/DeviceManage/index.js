/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-10-09 19:23:33
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 设备管理列表页
 */
import React from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import intl from 'utils/intl';
import notification from 'utils/notification';
import { operatorRender } from 'utils/renderer';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';

import { deviceManageDS } from '@/stores/deviceManageDS';
import { EDIT_ACTION, NEW_ACTION } from '@/utils/constants';

@formatterCollections({
  code: [
    'hiot.dataPointTemplate',
    'hiot.deviceManage',
    'hiot.iotWarnEvent',
    'hiot.iotTemplate',
    'hiot.common',
  ],
})
export default class DeviceManage extends React.Component {
  constructor(props) {
    super(props);
    this.deviceManageDS = new DataSet(deviceManageDS());
  }

  componentDidMount() {
    this.deviceManageDS.setQueryParameter('thingId', undefined); // 为了解决进入详情页面 退出来之后查询参数未清除导致数据查询的问题
    this.deviceManageDS.query();
  }

  /**
   * 跳转到对应数据模板详情/编辑页面的页面
   * @param type 操作类型
   * @param record 当前记录
   * @param type edit: 编辑 detail: 详情
   */
  @Bind()
  handleDetailOrCreate(type, record) {
    const path =
      type === NEW_ACTION
        ? `/hiot/device/manage/${NEW_ACTION}`
        : `/hiot/device/manage/${type}/${record.get('thingId')}`;

    this.props.history.push({
      pathname: path,
      search: record ? `?guid=${record.get('guid')}` : '',
    });
  }

  /**
   * 删除设备(可批量) 根据thingId
   */
  @Bind()
  async handleDeviceManageDelete() {
    if (this.deviceManageDS.selected.length > 0) {
      try {
        const res = await this.deviceManageDS.delete(this.deviceManageDS.selected);
        if (!res) {
          return false;
        }
        await this.deviceManageDS.query();
      } catch (err) {
        // const errContent = intl.get('hzero.common.notification.error').d('操作失败');
        // notification.error({ message: `${errContent}:${err.message}` });
      }
    } else {
      notification.warning({
        message: intl.get('hiot.common.view.message.pleaseSelectItem').d('请先选择数据'),
      });
    }
  }

  // 设备列表table列
  get columns() {
    const {
      match: { path },
    } = this.props;
    return [
      { name: 'thingCode', sortable: true, width: 150 },
      { name: 'thingName', width: 150 },
      {
        name: 'connected',
        renderer: ({ value }) => {
          const tmpValue = Number(value);
          return (
            <span style={{ color: tmpValue === 1 ? 'green' : 'red' }}>
              {tmpValue === 1
                ? intl.get('hiot.common.view.title.online').d('在线')
                : intl.get('hiot.common.view.title.offline').d('离线')}
            </span>
          );
        },
      },
      { name: 'statusMeaning' },
      { name: 'platformMeaning' },
      { name: 'configName', width: 150 },
      { name: 'gatewayName', width: 150 },
      { name: 'name', width: 150 },
      { name: 'thingModelName', width: 150 },
      { name: 'categoryMeaning' },

      { name: 'description', width: 180 },
      {
        name: 'operation',
        lock: 'right',
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
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '设备管理-编辑',
                  },
                ]}
                onClick={() => this.handleDetailOrCreate(EDIT_ACTION, record)}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators);
        },
      },
    ];
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get('hiot.deviceManage.view.title.header').d('设备维护')}>
          <ButtonPermission
            type="c7n-pro"
            icon="add"
            color="primary"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '设备管理-新建',
              },
            ]}
            onClick={() => this.handleDetailOrCreate(NEW_ACTION)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '设备管理-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleDeviceManageDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.deviceManageDS} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
