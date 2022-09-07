/*
 * DeviceCollection 设备采集
 * @date: 2020-06-02
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { Component } from 'react';

import intl from 'utils/intl';
import { Bind } from 'lodash-decorators';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import { action } from 'mobx';
import { routerRedux } from 'dva/router';
import { Table, DataSet, Switch, Modal } from 'choerodon-ui/pro';

import { enableRender, operatorRender } from 'utils/renderer';
import { Header, Content } from 'components/Page';
import { tableDS } from '../../stores/DeviceCollectionDS';
import Drawer from './Drawer';
import ConfirmModal from './confirmModal';
import styles from './index.less';

const modelPrompt = 'hiot.deviceCollection.view';

@formatterCollections({ code: ['hiot.deviceCollection', 'hiot.common'] })
export default class DeviceCollection extends Component {
  constructor(props) {
    super(props);
    this.state = {};
  }

  drawerModal;

  tableDs = new DataSet(tableDS());

  componentDidMount = () => {
    this.tableDs.queryDataSet.addEventListener('update', this.handleChange);
  };

  handleChange = ({ name }) => {
    if (name === 'gatewayCodeObject') {
      this.tableDs.query();
    }
  };

  refTable = async () => {
    await this.tableDs.query();
  };

  get columns() {
    return [
      {
        name: 'gatewayCode',
        width: 120,
      },
      {
        name: 'dcDeviceCode',
        width: 150,
        renderer: ({ value, record }) => {
          return (
            <a
              className="action-link"
              onClick={() => this.handleOpen(false, record.get('dcDeviceId'))}
            >
              {value}
            </a>
          );
        },
      },
      {
        name: 'description',
      },
      {
        name: 'heartbeatCycle',
        width: 120,
      },
      { name: 'packageName', width: 200 },
      { name: 'connectInfo', width: 300 },
      {
        name: 'simulatorFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'center',
      },
      {
        name: 'enableFlag',
        renderer: ({ value }) => enableRender(value),
        align: 'center',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        lock: 'right',
        width: 100,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <a onClick={() => this.onCollection(record)}>
                {intl.get(`${modelPrompt}.collectionInfo`).d('采集项配置')}
              </a>
            ),
            len: 5,
            title: intl.get(`${modelPrompt}.collectionInfo`).d('采集项配置'),
          });
          return operatorRender(operators);
        },
      },
    ].filter(Boolean);
  }

  @action
  onEdit = () => {
    this.editor = !this.editor;
  };

  confirms = () => {
    const msgTitle = intl.get(`${modelPrompt}.message.deleteMsg`).d('确认要删除？');
    const msgContent = intl
      .get(`${modelPrompt}.message.deleteInfo`)
      .d('删除该设备，会将设备下的所有采集项配置删除，是否确认？');

    return <ConfirmModal msgTitle={msgTitle} msgContent={msgContent} />;
  };

  deleteAll = async () => {
    const selects = this.tableDs.selected;
    try {
      await this.tableDs.delete(selects, this.confirms());
      this.tableDs.query();
    } catch (err) {
      //
    }
  };

  renderSwitch = (name) => {
    return <Switch name={name} checkedValue={1} unCheckedValue={0} />;
  };

  onCollection = (record) => {
    const { dispatch } = this.props;
    const id = record.get('dcDeviceId');

    if (id) {
      const pathname = `/hiot/device-collection/detail/${id}`;

      dispatch(
        routerRedux.push({
          pathname,
        })
      );
    }
  };

  @Bind()
  handleOpen(isNew, dcDeviceId) {
    this.drawerModal = Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 450 },
      className: styles['dev-collection-modal'],
      title: isNew
        ? intl.get(`${modelPrompt}.title.NewDcDevice`).d('新建设备')
        : intl.get(`${modelPrompt}.title.EditDcDevice`).d('编辑设备'),
      children: (
        <Drawer
          isNew={isNew}
          dcDeviceId={dcDeviceId}
          refTable={this.refTable}
          onCancel={this.handleCloseModal}
        />
      ),
      footer: null,
    });
  }

  @Bind()
  handleCloseModal() {
    this.drawerModal.close();
  }

  render() {
    const {
      match: { path },
    } = this.props;
    return (
      <>
        <Header title={intl.get(`hiot.deviceCollection.view.title.deviceCollection`).d('设备采集')}>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.create`,
                type: 'button',
                meaning: '设备采集-新建',
              },
            ]}
            color="primary"
            icon="add"
            // onClick={this.onNew}
            onClick={() => this.handleOpen(true)}
          >
            {intl.get('hzero.common.button.create').d('新建')}
          </ButtonPermission>
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}.button.delete`,
                type: 'button',
                meaning: '设备采集-删除',
              },
            ]}
            icon="delete"
            onClick={this.deleteAll}
          >
            {intl.get('hzero.common.button.delete').d('删除')}
          </ButtonPermission>
        </Header>
        <Content>
          <Table dataSet={this.tableDs} columns={this.columns} queryFieldsLimit={3} />
        </Content>
      </>
    );
  }
}
