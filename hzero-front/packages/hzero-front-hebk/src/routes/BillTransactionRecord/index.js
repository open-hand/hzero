/**
 * 票据交易记录
 * @since 2020-02-12
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, ModalContainer, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';

import { tableDs } from '../../stores/BillTransactionRecordDS';

@formatterCollections({ code: ['hebk.billTR'] })
export default class BillTransactionRecord extends React.Component {
  tableDs = new DataSet(tableDs());

  get columns() {
    return [
      { name: 'bankMarkMeaning', width: 180 },
      { name: 'draftNumber', width: 200 },
      { name: 'bankCode' },
      { name: 'accountNumber', width: 200 },
      { name: 'statusMeaning', width: 200 },
      { name: 'bankStatusCode', width: 200 },
      { name: 'bankTransactionId', width: 200 },
    ];
  }

  render() {
    const { location } = this.props;
    return (
      <>
        <Header title={intl.get('hebk.billTR.view.message.billTR').d('票据交易记录')} />
        <Content>
          <Table dataSet={this.tableDs} columns={this.columns} queryFieldsLimit={3} />
          <ModalContainer location={location} />
        </Content>
      </>
    );
  }
}
