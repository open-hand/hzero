/*
 * @Description: 事件处理定义
 * @version: 0.0.1
 * @Author: heqiheng <qiheng.he@hand-china.com>
 * @Date: 2020-06-09 13:47:29
 * @Copyright: Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, Form, Output, Button, Modal, DataSet } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';
import qs from 'querystring';
import { isEmpty } from 'lodash';
import { Content, Header } from 'components/Page';
import notification from 'utils/notification';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { isTenantRoleLevel } from 'utils/utils';
import { enableRender } from 'utils/renderer';
import EditModal from './EditModal';
import { EventHandleDS, EventHandleServiceDS } from '@/stores/EventDS';

const modalKey = Modal.key();

@formatterCollections({ code: ['hevt.eventHandle', 'hevt.common'] })
export default class eventHandle extends React.PureComponent {
  constructor(props) {
    super(props);
    const { eventId = undefined } = qs.parse(props.history.location.search.substr(1));
    this.eventHandleDS = new DataSet(EventHandleDS(eventId));
    this.eventHandleServiceDS = new DataSet(EventHandleServiceDS(eventId));
    this.state = {
      eventId,
    };
  }

  get columns() {
    return [
      { name: 'serviceCode' },
      { name: 'serviceName' },
      { name: 'groupId' },
      { name: 'enabledFlag', renderer: ({ value }) => enableRender(value) },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 175,
        renderer: ({ record }) => {
          return (
            <span className="action-link">
              <a onClick={() => this.handlService(record)}>
                {intl.get('hzero.common.button.edit').d('编辑')}
              </a>
            </span>
          );
        },
        lock: 'right',
      },
    ];
  }

  /**
   * 获取平台事件数据
   */
  @Bind()
  fetchEventData() {
    const { dispatch } = this.props;
    const { eventId } = this.state;
    dispatch({
      type: 'eventHandle/fetchEventData',
      payload: {
        eventId,
      },
    });
  }

  /**
   *处理服务
   *
   * @param {*} record 当前行
   * @memberof eventHandle
   */
  @Bind()
  handlService(record) {
    const modalPropertys = {
      title: !record
        ? intl.get('hevt.eventHandle.view.title.eventHandle.create').d('新建处理服务')
        : intl.get('hevt.eventHandle.view.title.eventHandle.edit').d('编辑处理服务'),
      drawer: true,
      closable: true,
      key: modalKey,
      style: {
        width: 700,
      },
      children: (
        <EditModal
          eventId={this.state.eventId}
          eventHandleServiceId={record ? record.toData().eventHandleServiceId : ''}
          isCreate={!record}
          onRef={(node) => {
            this.serviceModal = node;
          }}
        />
      ),
      onCancel: () => this.serviceModal.eventHandleServiceDS.reset(),
      onClose: () => this.serviceModal.eventHandleServiceDS.reset(),
      onOk: async () => {
        const res = await this.serviceModal.eventHandleServiceDS.submit();
        if (!isEmpty(res) && res.success) {
          this.eventHandleServiceDS.query();
        } else if (res === undefined) {
          notification.warning({
            message: intl.get('hevt.common.view.message.form.noChange').d('表单未做修改'),
          });
          return false;
        } else if (res === false) {
          notification.error({
            message: intl.get('hevt.common.view.message.required').d('存在必输字段未填写'),
          });
          return false;
        } else {
          return false;
        }
      },
    };
    Modal.open(modalPropertys);
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hevt.eventHandle.view.eventHandle.title').d('事件处理')}
          backPath={isTenantRoleLevel() ? '/hevt/event/list' : '/hevt/event/list'}
        >
          <Button icon="add" color="primary" onClick={() => this.handlService()}>
            {intl.get('hzero.common.button.create').d('新建')}
          </Button>
        </Header>
        <Content>
          <div className="table-list-search">
            <Form dataSet={this.eventHandleDS} columns={2}>
              <Output name="eventCode" />
              <Output name="eventName" />
            </Form>
          </div>
          <Table dataSet={this.eventHandleServiceDS} columns={this.columns} queryBar="none" />
        </Content>
      </>
    );
  }
}
