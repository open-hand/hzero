/**
 * 事件
 * @since 2020-02-06
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table, Modal } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { Content, Header } from 'components/Page';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';

import Drawer from './Drawer';
import { tableDs, detailDs } from '../../stores/EventDS';

@formatterCollections({ code: ['hebk.event'] })
export default class Event extends React.Component {
  tableDs = new DataSet(tableDs());

  detailDs = new DataSet(detailDs());

  get columns() {
    return [
      { name: 'apiVersion', width: 120 },
      { name: 'serviceName' },
      { name: 'code', width: 250 },
      { name: 'source', width: 210 },
      { name: 'time', width: 180 },
      { name: 'requestNumber', width: 180 },
      { name: 'errorCode', width: 120 },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a
                onClick={() => {
                  this.handleEdit(record);
                }}
              >
                {intl.get('hzero.common.button.view').d('查看')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.view').d('查看'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ].filter(Boolean);
  }

  @Bind()
  handleEdit(record) {
    this.detailDs.create({});
    const currentEditData = record && record.toData();
    const title = intl.get('hzero.common.view.title.view').d('查看');
    Modal.open({
      drawer: true,
      key: 'seeEvent',
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer currentEditData={currentEditData} detailDs={this.detailDs} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onClose: () => {
        this.detailDs.removeAll();
      },
      footer: null,
    });
  }

  render() {
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hebk.event.view.message.title.event').d('审计事件')} />
        <Content>
          <Table dataSet={this.tableDs} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
