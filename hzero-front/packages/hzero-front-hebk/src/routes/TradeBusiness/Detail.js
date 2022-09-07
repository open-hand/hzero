import React from 'react';
import { DataSet, Form, TextField } from 'choerodon-ui/pro';
import { Card } from 'hzero-ui';

import { Content, Header } from 'components/Page';
import intl from 'utils/intl';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';

import { detailDs as DetailDs } from '../../stores/TradeBusinessDS';
import styles from './index.less';

const Detail = (props) => {
  const detailDs = React.useMemo(() => new DataSet(DetailDs()), []);

  React.useEffect(() => {
    const {
      match: {
        params: { businessId },
      },
    } = props;
    detailDs.setQueryParameter('businessId', businessId);
    detailDs.query();
  }, []);

  return (
    <>
      <Header
        title={intl.get('hebk.tradeBusiness.view.message.tradeBusiness').d('金融类交易记录')}
        backPath="/hebk/trade-business"
      />
      <Content>
        <Card
          className={DETAIL_CARD_CLASSNAME}
          bordered={false}
          title={intl.get('hebk.tradeBusiness.view.message.tradeBusiness').d('金融类交易记录')}
        >
          <Form
            className={styles['draft-form-label']}
            labelWidth={130}
            columns={3}
            dataSet={detailDs}
          >
            <TextField name="directionMeaning" disabled />
            <TextField name="businessTypeMeaning" disabled />
            <TextField name="bankBusinessType" disabled />
            <TextField name="bankMarkMeaning" disabled />
            <TextField name="referenceOrder" disabled />
            <TextField name="tradeAmount" disabled />
            <TextField name="currency" disabled />
            <TextField name="tradeDate" disabled />
            <TextField name="statusMeaning" disabled />
            <TextField name="bankStatusCode" disabled />
            <TextField name="bankStatusDesc" disabled />
            <TextField name="tradeBankCode" disabled />
            <TextField name="tradeAccountNumber" disabled />
            <TextField name="tradeAccountName" disabled />
            <TextField name="tradeAccountBankName" disabled />
            <TextField name="oppositeBankCode" disabled />
            <TextField name="oppositeAccountNumber" disabled />
            <TextField name="oppositeAccountName" disabled />
            <TextField name="oppositeAccountBankName" disabled />
            <TextField name="oppositeBankCode" disabled />
            <TextField name="oppositeAccountNumber" disabled />
            <TextField name="oppositeAccountName" disabled />
            <TextField name="oppositeAccountBankName" disabled />
            <TextField name="agentBankCode" disabled />
            <TextField name="agentAccountNumber" disabled />
            <TextField name="agentAccountName" disabled />
            <TextField name="agentAccountBankName" disabled />
            <TextField name="transactionSerialNumber" disabled />
            <TextField name="accountBalance" disabled />
            <TextField name="availableBalance" disabled />
            <TextField name="frozenAmount" disabled />
            <TextField name="overdrawnAmount" disabled />
            <TextField name="availableOverdrawnAmount" disabled />
            <TextField name="purpose" disabled />
            <TextField name="postscript" disabled />
            <TextField name="feeAccount" disabled />
            <TextField name="feeAmount" disabled />
            <TextField name="feeCurrency" disabled />
            <TextField name="interestDate" disabled />
            <TextField name="voucherNumber" disabled />
            <TextField name="exchangeRate" disabled />
            <TextField name="exchangeRate" disabled />
          </Form>
        </Card>
      </Content>
    </>
  );
};

export default Detail;
