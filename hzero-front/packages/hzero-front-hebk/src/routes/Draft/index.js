import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';

import { tableDs as TableDs } from '../../stores/DraftDS';

const Draft = ({ match: { path }, history }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'bankMarkMeaning', width: 220 },
      { name: 'bankCode' },
      { name: 'accountNumber', width: 220 },
      { name: 'draftTypeMeaning', width: 220 },
      { name: 'draftNumber', width: 220 },
      { name: 'draftStatusMeaning', width: 220 },
      { name: 'amount', width: 220 },
      { name: 'date', width: 220 },
      { name: 'dueDate', width: 220 },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/view`,
                      type: 'button',
                      meaning: '银行票据-查看',
                    },
                  ]}
                  onClick={() => {
                    handleView(record);
                  }}
                >
                  {intl.get('hzero.common.button.view').d('查看')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.view').d('查看'),
            },
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/flow`,
                      type: 'button',
                      meaning: '银行票据-流转',
                    },
                  ]}
                  onClick={() => {
                    handleFlow(record);
                  }}
                >
                  {intl.get('hebk.draft.button.flow').d('流转')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hebk.draft.button.flow').d('流转'),
            }
          );
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  const handleView = (record) => {
    const draftId = record.get('draftId');
    history.push(`/hebk/draft/detail/${draftId}`);
  };

  const handleFlow = (record) => {
    const bankMark = record.get('bankMark');
    const accountNumber = record.get('accountNumber');
    const draftNumber = record.get('draftNumber');
    history.push(
      `/hebk/draft/flow?bankMark=${bankMark}&accountNumber=${accountNumber}&draftNumber=${draftNumber}`
    );
  };

  return (
    <>
      <Header title={intl.get('hebk.draft.view.message.draft').d('银行票据')} />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hebk.draft'] })(Draft);
