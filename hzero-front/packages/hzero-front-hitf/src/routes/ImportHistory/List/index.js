/**
 * ImportHistory - 导入历史
 * @date: 2019-12-2
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { Component } from 'react';
import { DataSet, Table, Modal, Button } from 'choerodon-ui/pro';
import { Bind } from 'lodash-decorators';

import { isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';

import ImportHistoryDS from '../Stores/ImportHistoryDS';

const viewModalKey = Modal.key();

let modal;

@formatterCollections({ code: ['hitf.importHistory'] })
export default class ImportHistoryListPage extends Component {
  importHistoryDS = new DataSet({
    ...ImportHistoryDS(),
  });

  /**
   * 关闭消息弹窗
   */
  @Bind()
  closeModal() {
    modal.close();
  }

  /**
   * 查看导入消息
   * @param {string} value - 导入消息
   */
  @Bind()
  handleViewMessage(value) {
    modal = Modal.open({
      title: intl.get('hitf.importHistory.model.importHistory.importMessage').d('导入消息'),
      closable: true,
      key: viewModalKey,
      children: <div style={{ maxWidth: 472, maxHeight: 400, overflowY: 'scroll' }}>{value}</div>,
      footer: (
        <Button onClick={this.closeModal}>{intl.get('hzero.common.button.close').d('关闭')}</Button>
      ),
    });
  }

  get columns() {
    return [
      !isTenantRoleLevel() && { name: 'tenantName' },
      { name: 'requestNum', width: 250 },
      { name: 'importUser' },
      { name: 'importUrl', width: 450 },
      {
        name: 'importMessage',
        renderer: ({ value }) => <a onClick={() => this.handleViewMessage(value)}>{value}</a>,
      },
      { name: 'importStatus' },
    ].filter(Boolean);
  }

  render() {
    return (
      <>
        <Header
          title={intl.get('hitf.importHistory.view.message.title.importHistory').d('导入历史')}
        />
        <Content>
          <Table dataSet={this.importHistoryDS} columns={this.columns} />
        </Content>
      </>
    );
  }
}
