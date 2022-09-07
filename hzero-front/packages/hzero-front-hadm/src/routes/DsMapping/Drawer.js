import React from 'react';
import { Form, TextField, Spin, Lov, Password, TextArea, NumberField } from 'choerodon-ui/pro';

const Drawer = props => {
  const { currentEditData, detailDs, isEdit } = props;

  const [isSpin, setIsSpin] = React.useState(false);

  React.useEffect(() => {
    if (isEdit) {
      setIsSpin(true);
      queryData();
    }
  }, []);

  const queryData = async () => {
    const { serviceDatasourceId } = currentEditData;
    detailDs.setQueryParameter('serviceDatasourceId', serviceDatasourceId);
    await detailDs.query().then(res => {
      if (res) {
        setIsSpin(false);
      }
    });
  };

  const handleChange = () => {
    if(isEdit) {
      detailDs.current.set('serviceVersion', '');
      detailDs.current.set('dsUsername', '');
      detailDs.current.set('dsUrl', '');
      detailDs.current.set('dsPassword', '');
    }
  };

  return (
    <>
      <Spin spinning={isSpin}>
        <Form dataSet={detailDs}>
          <Lov name="serviceCodeLov" onChange={handleChange} />
          <TextField name="serviceVersion" />
          <TextArea name="dsUrl" />
          <TextField name="dsUsername" />
          <Password name="dsPassword" />
          <NumberField name="connectionTimeoutMilliseconds" step={1} min={0} />
          <NumberField name="idleTimeoutMilliseconds" step={1} min={0} />
          <NumberField name="maxLifetimeMilliseconds" step={1} min={0} />
          <NumberField name="maxPoolSize" step={1} min={0} />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
