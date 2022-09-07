import React from 'react';
import { withRouter } from 'react-router';
import { Table } from 'choerodon-ui/pro';

@withRouter
export default class BankDrawer extends React.PureComponent {
  async componentDidMount() {
    const {
      currentBankData: { proxyId },
      bankDs,
    } = this.props;
    bankDs.proxyId = proxyId;
    await bankDs.query();
  }

  get columns() {
    return [{ name: 'bankMark', width: 220 }, { name: 'bankMarkMeaning' }];
  }

  render() {
    const { bankDs } = this.props;
    return (
      <>
        <Table dataSet={bankDs} columns={this.columns} queryBar="none" />
      </>
    );
  }
}
