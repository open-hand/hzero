import React from 'react';
import { Form, TextField, Spin } from 'choerodon-ui/pro';

const BalanceDrawer = props => {
  const { currentBalanceData, balanceDs } = props;

  const [isSpin, setIsSpin] = React.useState(false);

  React.useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);

  const queryData = async () => {
    const { accountNumber, bankMark } = currentBalanceData;
    balanceDs.setQueryParameter('accountNumber', accountNumber);
    balanceDs.setQueryParameter('bankMark', bankMark);
    await balanceDs.query().then(res => {
      if (res) {
        setIsSpin(false);
      }
    });
  };

  return (
    <>
      <Spin spinning={isSpin}>
        <Form dataSet={balanceDs}>
          <TextField name="bankMarkMeaning" disabled />
          <TextField name="bankCode" disabled />
          <TextField name="accountName" disabled />
          <TextField name="accountNumber" disabled />
          <TextField name="currencyCode" disabled />
          <TextField name="bookBalance" disabled />
          <TextField name="availableBalance" disabled />
          <TextField name="overdrawnAmount" disabled />
          <TextField name="creditLoadAmount" disabled />
          <TextField name="frozenAmount" disabled />
          <TextField name="effectiveQuota" disabled />
          <TextField name="effectiveUsedQuota" disabled />
          <TextField name="effectiveUnusedQuota" disabled />
          <TextField name="excessStartDate" disabled />
          <TextField name="excessEndDate" disabled />
          <TextField name="effectiveQuota" disabled />
          <TextField name="excessUsedQuota" disabled />
          <TextField name="excessAvailableQuota" disabled />
        </Form>
      </Spin>
    </>
  );
};

export default BalanceDrawer;
