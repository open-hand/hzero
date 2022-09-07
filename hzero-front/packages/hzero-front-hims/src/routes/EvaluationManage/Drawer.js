import React, { useEffect } from 'react';
import { Form, Spin, Output, TextArea } from 'choerodon-ui/pro';

const Drawer = (props) => {
  const { drawerDs, currentEditData } = props;

  useEffect(() => {
    queryData();
  }, []);

  const queryData = async () => {
    try {
      const { id } = currentEditData;
      drawerDs.setQueryParameter('id', id);
      await drawerDs.query();
    } catch (e) {
      console.log('e: ', e);
    }
  };

  return (
    <>
      <Spin dataSet={drawerDs}>
        <Form dataSet={drawerDs}>
          <Output name="evaluationTypeMeaning" />
          <Output name="csUserName" />
          <Output name="userName" />
          <Output name="score" />
          <Output name="createAt" />
          <TextArea name="remark" rows={25} resize="vertical" />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
