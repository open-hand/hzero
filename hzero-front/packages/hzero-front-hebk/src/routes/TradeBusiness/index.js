/**
 * 金融类交易记录
 * @since 2020-02-12
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2019, Hand
 */
import React from 'react';
import { DataSet, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import { downloadFile } from 'services/api';
import { HZERO_FILE, BKT_PUBLIC } from 'utils/config';
import { getCurrentOrganizationId } from 'utils/utils';

import { tableDs as TableDs } from '../../stores/TradeBusinessDS';

const TradeBusiness = ({ match: { path }, history }) => {
  const tableDs = React.useMemo(() => new DataSet(TableDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'directionMeaning', width: 220 },
      { name: 'businessTypeMeaning', width: 220 },
      { name: 'bankMarkMeaning' },
      { name: 'referenceOrder', width: 220 },
      { name: 'tradeAmount', width: 220 },
      { name: 'currency', width: 220 },
      { name: 'tradeDate', width: 220 },
      { name: 'accountBalance', width: 220 },
      { name: 'statusMeaning', width: 220 },
      { name: 'bankStatusDesc', width: 220 },
      { name: 'oppositeAccountBankName', width: 220 },
      { name: 'oppositeAccountName', width: 220 },
      { name: 'oppositeAccountNumber', width: 220 },
      { name: 'agentAccountNumber', width: 220 },
      { name: 'agentAccountName', width: 220 },
      { name: 'tradeAccountBankName', width: 220 },
      { name: 'tradeAccountName', width: 220 },
      { name: 'tradeAccountNumber', width: 220 },
      { name: 'transactionSerialNumber', width: 220 },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 150,
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'view',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/view`,
                      type: 'button',
                      meaning: '金融类交易记录-查看',
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
              key: 'backList',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/backList`,
                      type: 'button',
                      meaning: '金融类交易记录-回单',
                    },
                  ]}
                  onClick={() => {
                    handleBack(record);
                  }}
                  disabled={!record.get('receiptUrl')}
                >
                  {intl.get('hebk.tradeBusiness.button.backList').d('回单')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hebk.tradeBusiness.button.backList').d('回单'),
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
    const businessId = record.get('businessId');
    history.push(`/hebk/trade-business/detail/${businessId}`);
  };

  const handleBack = (record) => {
    //   // 回单
    const api = `${HZERO_FILE}/v1/${getCurrentOrganizationId()}/files/download`;
    downloadFile({
      requestUrl: api,
      queryParams: [
        { name: 'bucketName', value: BKT_PUBLIC },
        { name: 'directory', value: 'hpfm06' },
        { name: 'url', value: record.get('receiptUrl') },
      ],
    });
  };

  return (
    <>
      <Header
        title={intl.get('hebk.tradeBusiness.view.message.tradeBusiness').d('金融类交易记录')}
      />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hebk.tradeBusiness'] })(TradeBusiness);
