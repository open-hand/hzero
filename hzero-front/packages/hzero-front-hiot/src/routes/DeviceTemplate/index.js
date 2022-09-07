import React, { Component } from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import { isEmpty } from 'lodash';

import { operatorRender } from 'utils/renderer';
import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { deviceTemplateListDS } from '@/stores/deviceTemplateDS';

@formatterCollections({ code: ['hiot.deviceTemplate', 'hiot.common'] })
export default class DeviceTemplate extends Component {
  constructor(props) {
    super(props);
    this.tableDS = new DataSet(deviceTemplateListDS());
  }

  /**
   * 处理页面跳转
   * @param path 跳转路径
   */
  @Bind()
  pageJump(path) {
    const { history } = this.props;
    history.push(path);
  }

  @Bind()
  handleOperation(record, operation) {
    const path =
      operation === 'new'
        ? `/hiot/device-temp/new`
        : `/hiot/device-temp/${operation}/${record.get('thingModelId')}`;
    this.pageJump(path);
  }

  @Bind()
  async handleDelete() {
    try {
      const { selected } = this.tableDS;
      if (isEmpty(selected)) {
        notification.warning({
          message: intl.get('hzero.common.validation.atLeastOneRecord').d('请至少选择一条数据'),
        });
        return;
      }
      // 删除的数据中是否有被引用的设备模板
      if (this.tableDS.toJSONData(true, true).some(({ isReferred }) => isReferred === 1)) {
        notification.warning({
          message: intl
            .get('hiot.deviceTemplate.message.deviceTemplate.removed')
            .d('被引用的设备模板不能被删除！'),
        });
        return;
      }
      const resp = await this.tableDS.delete(selected);
      if (resp) {
        this.tableDS.query();
      }
    } catch (error) {
      //
    }
  }

  render() {
    const {
      match: { path },
    } = this.props;
    const columns = [
      { name: 'thingModelCode', minWidth: 200 },
      { name: 'thingModelName', minWidth: 240 },
      { name: 'categoryMeaning' },
      { name: 'platformMeaning' },
      { name: 'configName' },
      { name: 'enabled', width: 80 },
      { name: 'statusMeaning', width: 100 },
      { name: 'description' },
      { name: 'consumerName' },
      { name: 'isReferred', width: 100 },
      {
        name: 'operation',
        width: 70,
        lock: 'right',
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
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
                onClick={() => this.handleOperation(record, 'edit')}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          return operatorRender(operators, record);
        },
      },
    ];

    return (
      <>
        <Header title={intl.get('hiot.common.model.device.deviceModel').d('设备模型')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '设备模版-新建',
              },
            ]}
            icon="add"
            color="primary"
            onClick={() => this.handleOperation({}, 'new')}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '设备模版-删除',
              },
            ]}
            icon="delete"
            onClick={this.handleDelete}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table queryFieldsLimit={3} columns={columns} dataSet={this.tableDS} />
        </Content>
      </>
    );
  }
}
