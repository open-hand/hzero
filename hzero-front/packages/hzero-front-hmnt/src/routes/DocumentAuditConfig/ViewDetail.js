import React, { useEffect, useState } from 'react';

import { Form, TextField, Switch, Spin, DataSet, Table, Select } from 'choerodon-ui/pro';

import { isTenantRoleLevel } from 'utils/utils';

import { viewFormDS, viewTableDS } from '@/stores/DocumentAuditConfigDS';

const isTenant = isTenantRoleLevel();
const ViewDetail = (props) => {
  const viewFormDs = React.useMemo(() => new DataSet(viewFormDS()), []);

  const viewTableDs = React.useMemo(() => new DataSet(viewTableDS()), []);

  const [type, setType] = useState('');
  const { currentData = {} } = props;

  useEffect(() => {
    const { auditOpConfigId, tenantId } = currentData;
    viewFormDs.auditOpConfigId = auditOpConfigId;
    viewTableDs.setQueryParameter('auditOpConfigId', auditOpConfigId);
    viewTableDs.setQueryParameter('tenantId', tenantId);
    viewTableDs.query();
    viewFormDs.query().then((res) => {
      if (res) {
        setType(res.auditType);
      }
    });
  }, []);
  const columns = React.useMemo(
    () =>
      [
        !isTenantRoleLevel && {
          name: 'tenantName',
        },
        {
          name: 'tableName',
        },
        {
          name: 'displayName',
        },
      ].filter(Boolean),
    []
  );

  return (
    <>
      <Spin dataSet={viewFormDs}>
        <Form dataSet={viewFormDs} disabled>
          {!isTenant && <TextField name="tenantName" />}
          <Select name="auditType" />
          {type === 'API' && <TextField name="serviceName" />}
          <TextField name="description" />
          {type === 'USER' && <TextField name="username" />}
          {type === 'ROLE' && <TextField name="roleName" />}
          {type === 'CLIENT' && <TextField name="clientName" />}
          <Switch name="auditArgsFlag" />
          <Switch name="auditResultFlag" />
          <Switch name="auditDataFlag" />
          <TextField name="businessKey" />
          <TextField name="auditContent" />
        </Form>
      </Spin>
      <Table dataSet={viewTableDs} columns={columns} />
    </>
  );
};

export default ViewDetail;
