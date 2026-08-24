import React from 'react';
import {
  Lov,
  Form,
  Spin,
  Select,
  Switch,
  TextArea,
  UrlField,
  TextField,
  Password,
  IntlField,
} from 'choerodon-ui/pro';

import intl from 'utils/intl';

const Drawer = (props) => {
  const { drawerDs, isEdit, isTenant, currentEditData, isCopy } = props;

  React.useEffect(() => {
    if (isEdit) {
      drawerDs.current.set('isCreate', false);
      queryData();
    }
    if (isCopy) {
      drawerDs.get(0).set('serverCode', currentEditData.serverCode);
      drawerDs.get(0).set('serverName', currentEditData.serverName);
      drawerDs.get(0).set('serverType', currentEditData.serverType);
      drawerDs.get(0).set('enabledFlag', currentEditData.enabledFlag);
      drawerDs.get(0).set('description', currentEditData.description);
    }
  }, []);

  const queryData = async () => {
    try {
      const { serverId } = currentEditData;
      drawerDs.setQueryParameter('serverId', serverId);
      await drawerDs.query();
    } catch (e) {
      //
    }
  };

  return (
    <>
      <Spin dataSet={drawerDs}>
        <Form dataSet={drawerDs} labelWidth={110}>
          {!isTenant && <Lov name="tenantLov" disabled={isEdit} />}
          <TextField name="serverCode" disabled={isEdit} />
          <IntlField name="serverName" />
          <Select name="serverType" />
          <UrlField
            name="webhookAddress"
            placeholder={
              isEdit
                ? intl.get('hzero.common.validation.notChange').d('未更改')
                : 'https://example.com'
            }
          />
          <Password
            name="secret"
            placeholder={isEdit ? intl.get('hzero.common.validation.notChange').d('未更改') : ''}
          />
          <TextArea name="description" />
          <Switch name="enabledFlag" />
        </Form>
      </Spin>
    </>
  );
};

export default Drawer;
