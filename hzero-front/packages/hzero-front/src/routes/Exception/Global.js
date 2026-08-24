import React from 'react';
import { connect } from 'dva';
import { Table } from 'hzero-ui';

import { Header, Content } from 'components/Page';

import intl from 'utils/intl';

class GlobalException extends React.Component {
  getColumns() {
    return [
      {
        title: intl.get('hzero.common.message.errorMessage').d('错误信息'),
        dataIndex: 'errorMessage',
      },
      {
        title: intl.get('hzero.common.message.errorLocation').d('错误页面'),
        dataIndex: 'errorLocation',
      },
    ];
  }

  render() {
    const { exceptionHistory } = this.props;
    const exceptionColumns = this.getColumns();
    return (
      <>
        <Header
          backPath="/workplace"
          title={intl.get('hzero.common.message.globalError').d('很抱歉！出现预料之外的错误')}
        />
        <Content>
          <Table
            rowKey={(record, index) => index}
            bordered
            columns={exceptionColumns}
            dataSource={exceptionHistory}
            pagination={false}
            rowSelection={null}
          />
        </Content>
      </>
    );
  }
}

export default connect(({ error = {} }) => {
  const { globalException: { history = [] } = {} } = error;
  return {
    exceptionHistory: history,
  };
})(GlobalException);
