import React from 'react';
import { Table } from 'choerodon-ui/pro';

const AddDrawer = (props) => {
  const { addLineDs, auditDocumentId } = props;

  React.useEffect(() => {
    addLineDs.setQueryParameter('auditDocumentId', auditDocumentId);
    addLineDs.query();
  }, []);

  const columns = React.useMemo(
    () => [
      { name: 'tenantName', width: 220 },
      { name: 'auditTypeMeaning', width: 200 },
      {
        name: 'auditType',
        width: 220,
        renderer: ({ record }) => {
          if (record.get('auditType') === 'API') {
            return record.get('description');
          } else if (record.get('auditType') === 'USER') {
            return record.get('username');
          } else if (record.get('auditType') === 'ROLE') {
            return record.get('roleName');
          } else if (record.get('auditType') === 'CLIENT') {
            return record.get('clientName');
          }
        },
      },
      { name: 'auditContent' },
    ],
    []
  );

  return (
    <>
      <Table dataSet={addLineDs} columns={columns} queryFieldsLimit={2} />
    </>
  );
};

export default AddDrawer;
