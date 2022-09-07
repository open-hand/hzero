import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';
import queryString from 'query-string';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';

import { flowDs as FlowDs } from '../../stores/DraftDS';

const Flow = (props) => {
  const flowDs = React.useMemo(() => new DataSet(FlowDs()), []);

  React.useEffect(() => {
    const {
      location: { search },
    } = props;
    const { bankMark, accountNumber, draftNumber } = queryString.parse(search);
    flowDs.setQueryParameter('bankMark', bankMark);
    flowDs.setQueryParameter('accountNumber', accountNumber);
    flowDs.setQueryParameter('draftNumber', draftNumber);
    flowDs.query();
  }, []);

  const columns = React.useMemo(
    () => [
      { name: 'sequenceNumber', width: 220 },
      { name: 'transactionTypeMeaning' },
      { name: 'applicantName', width: 220 },
      { name: 'signerName', width: 220 },
      { name: 'overdueReason', width: 220 },
      {
        name: 'transferFlag',
        width: 220,
      },
      { name: 'applicationDate', width: 220 },
      { name: 'openDate', width: 220 },
      { name: 'dueDate', width: 220 },
      { name: 'discountType', width: 220 },
    ],
    []
  );

  return (
    <>
      <Header
        title={intl.get('hebk.draft.view.message.flow').d('票据流转信息')}
        backPath="/hebk/draft"
      />
      <Content>
        <Table dataSet={flowDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default Flow;
