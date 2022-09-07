import React from 'react';
import { Form, TextField, Switch, Spin, Select, Lov } from 'choerodon-ui/pro';

const Drawer = (props) => {
  const { currentEditData, detailDs, isEdit } = props;

  const [isSpin, setIsSpin] = React.useState(false);

  React.useEffect(() => {
    if (isEdit) {
      setIsSpin(true);
      queryData();
    }
  }, []);

  const queryData = async () => {
    const { accountId } = currentEditData;
    detailDs.accountId = accountId;
    await detailDs.query().then((res) => {
      if (res) {
        setIsSpin(false);
      }
    });
  };

  return (
    <>
      <Spin spinning={isSpin}>
        <Form dataSet={detailDs}>
          <Select name="bankMark" />
          <TextField name="accountNumber" />
          <TextField name="bankCode" />
          <TextField name="bankNumber" />
          <TextField name="accountName" />
          <TextField name="currencyCode" />
          <TextField name="groupNumber" />
          <Select name="groupProductType" />
          <TextField name="branchId" />
          <TextField name="bankName" />
          <Switch name="childrenFlag" />
          <Lov
            name="parentAccountIdLov"
            // min={-9223372036854775808}
            // max={9223372036854775807}
          />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
