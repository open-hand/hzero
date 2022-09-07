import React from 'react';
import { Form, NumberField, Select } from 'choerodon-ui/pro';

import formatterCollections from 'utils/intl/formatterCollections';

const LineForm = props => {
  const { record } = props;

  const handleCode = value => {
    if (value) {
      record.set('interfaceCode', '');
    }
  };

  return (
    <Form columns={1} record={record}>
      <NumberField name="orderSeq" />
      <Select name="serverCode" onChange={handleCode} />
      <Select name="interfaceCode" />
    </Form>
  );
};

export default formatterCollections({
  code: ['hivc.apiConfig'],
})(LineForm);
