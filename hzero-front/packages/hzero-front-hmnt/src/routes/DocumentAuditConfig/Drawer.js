import React from 'react';
import { Form, TextField, Switch, Spin, Lov, TextArea, ModalContainer } from 'choerodon-ui/pro';

import { isTenantRoleLevel } from 'utils/utils';

import PlatformTable from './PlatformTable';
import TenantTable from './TenantTable';

const isTenant = isTenantRoleLevel();
const Drawer = (props) => {
  const { currentEditData, detailDs, isEdit, path, isView } = props;

  React.useEffect(() => {
    if (isEdit) {
      queryData();
    }
  }, []);

  /**
   * 查询头行信息
   */
  const queryData = async () => {
    const { auditDocumentId } = currentEditData;
    detailDs.auditDocumentId = auditDocumentId;
    await detailDs.query();
  };

  return (
    <>
      <Spin dataSet={detailDs}>
        <Form dataSet={detailDs}>
          {!isTenant && <Lov name="tenantLov" disabled={isEdit} />}
          <TextField name="documentCode" disabled={isEdit} />
          <TextField name="documentName" disabled={isEdit} />
          <TextArea name="documentDescription" disabled={isView} />
          <Switch name="enabledFlag" disabled={isView} />
        </Form>
        {isEdit &&
          (!isTenant ? (
            <PlatformTable
              currentEditData={currentEditData}
              detailDs={detailDs}
              path={path}
              isView={isView}
            />
          ) : (
            <TenantTable currentData={currentEditData} isView={isView} />
          ))}
        <ModalContainer location={location} />
      </Spin>
    </>
  );
};

export default Drawer;
