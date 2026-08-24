import React from 'react';
import { Form, Spin, Switch, Lov } from 'choerodon-ui/pro';

const Drawer = props => {
  const { currentEditData, detailTableDetailDS, isEdit } = props;

  React.useEffect(() => {
    if (isEdit) {
      queryData();
    } else {
      detailTableDetailDS.current.set({});
    }
  }, []);

  const queryData = async () => {
    detailTableDetailDS.current.set(currentEditData);
  };

  return (
    <>
      <Spin spinning={false}>
        <Form dataSet={detailTableDetailDS}>
          <Lov name="datasourceLov" />
          <Switch name="masterFlag" />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
