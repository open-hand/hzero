/*
 * @Description: 事件处理弹框
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-06-05 15:53:15
 * @Copyright: Copyright (c) 2020, Hand
 */

import React, { Component } from 'react';
import { Form, TextField, Lov, Table, DataSet, Switch, CheckBox } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { Divider } from 'choerodon-ui';
import { Bind } from 'lodash-decorators';
import notification from 'utils/notification';
import { isTenantRoleLevel } from 'utils/utils';
import { EventHandleWaysDS, EventHandleServiceDS, EventHandleParamsDS } from '@/stores/EventDS';

export default class RenderForm extends Component {
  constructor(props) {
    super(props);
    const { eventId, isCreate, eventHandleServiceId } = props;
    this.eventHandleServiceDS = new DataSet({
      ...EventHandleServiceDS(eventId, eventHandleServiceId, 'edit'),
      autoQuery: !isCreate,
      children: {
        eventHandleMethods: new DataSet({ ...EventHandleWaysDS(eventId) }),
        eventConfigs: new DataSet({ ...EventHandleParamsDS(eventHandleServiceId) }),
      },
    });
    props.onRef(this);
  }

  get columns() {
    return [
      {
        name: 'handleFunctionLov',
        width: 300,
        editor: () => {
          return true;
        },
      },
      {
        name: 'orderSeq',
        width: 100,
        editor: () => {
          return true;
        },
      },
      {
        name: 'levelCode',
        width: 100,
      },
      {
        name: 'enabledFlag',
        width: 100,
        align: 'center',
        editor: <CheckBox />,
      },
    ];
  }

  get columsParams() {
    return [
      {
        name: 'configCodeLov',
        width: 300,
        editor: () => {
          return <Lov />;
        },
      },
      {
        name: 'configValue',
        width: 100,
        editor: true,
      },
    ];
  }

  @Bind()
  async handleDelete() {
    if (isTenantRoleLevel()) {
      const { selected } = this.eventHandleServiceDS.children.eventHandleMethods;
      if (selected.some((item) => item.get('levelCode') === 'GLOBAL')) {
        notification.warning({
          message: intl
            .get('hevt.eventHandle.view.message.deleted')
            .d('勾选数据包含平台级数据，不可删除'),
        });
      } else {
        this.eventHandleServiceDS.children.eventHandleMethods.delete(selected);
      }
    } else {
      this.eventHandleServiceDS.children.eventHandleMethods.delete(
        this.eventHandleServiceDS.children.eventHandleMethods.selected
      );
    }
  }

  render() {
    return (
      <div>
        <Form dataSet={this.eventHandleServiceDS} columns={2}>
          <Lov name="serviceObj" />
          <TextField name="serviceCode" disabled />
          <TextField name="groupId" />
          <Switch name="enabledFlag" />
        </Form>
        <Divider orientation="left">
          <h3>{intl.get('hevt.eventHandle.view.title.handleWay').d('处理方法')}</h3>
        </Divider>
        <Table
          buttons={['add', ['delete', { onClick: () => this.handleDelete() }]]}
          dataSet={this.eventHandleServiceDS.children.eventHandleMethods}
          queryBar="none"
          columns={this.columns}
        />
        <Divider orientation="left">
          <h3>{intl.get('hevt.eventHandle.view.title.handleParams').d('处理参数配置')}</h3>
        </Divider>
        <Table
          buttons={['add', ['delete']]}
          dataSet={this.eventHandleServiceDS.children.eventConfigs}
          queryBar="none"
          columns={this.columsParams}
        />
      </div>
    );
  }
}
