/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/17
 * @copyright HAND ® 2019
 */

import React from 'react';
import { DataSet, Form, Lov, Select, Table, TextField } from 'choerodon-ui/pro';

import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';
import { TABLE_OPERATOR_CLASSNAME } from 'utils/constants';

import { labelTemplateAssignPermissionConfig } from '@/stores/labelTemplateDS';

const AssignPermission = ({ match, labelTemplate, modal }) => {
  const isTenant = isTenantRoleLevel();
  const organizationId = getCurrentOrganizationId();
  const { path } = match;
  const { labelPermissionDS, labelTemplateDS } = React.useMemo(() => {
    const datasetConfig = labelTemplateAssignPermissionConfig(labelTemplate);
    if (isTenant) {
      const newQueryFields = [];
      const newFields = [];
      datasetConfig.queryFields.forEach((queryField) => {
        switch (queryField.name) {
          case 'tenantLov':
            break;
          case 'tenantId':
            break;
          default:
            newQueryFields.push(queryField);
            break;
        }
      });
      datasetConfig.fields.forEach((field) => {
        switch (field.name) {
          case 'tenantLov':
            break;
          case 'tenantId':
            break;
          case 'tenantName':
            break;
          // 租户去掉 租户字段
          case 'roleLov':
            newFields.push({
              ...field,
              cascadeMap: undefined,
              lovPara: {
                tenantId: organizationId,
              },
            });
            break;
          default:
            newFields.push(field);
            break;
        }
      });
      datasetConfig.queryFields = newQueryFields;
      datasetConfig.fields = newFields;
    } else {
      const roleLovField = datasetConfig.fields.find((field) => field.name === 'roleLov');
      roleLovField.required = false;
    }
    const { queryFields } = datasetConfig;
    delete datasetConfig.queryFields;
    return {
      labelTemplateDS: new DataSet({
        data: [labelTemplate],
        fields: queryFields,
      }),
      labelPermissionDS: new DataSet(datasetConfig),
    };
  }, [labelTemplate]);
  const handleDelete = React.useCallback(
    async (record) => {
      await labelPermissionDS.delete([record]);
      await labelPermissionDS.query();
    },
    [labelPermissionDS]
  );
  const handleClear = React.useCallback(
    (record) => {
      labelPermissionDS.delete([record]);
    },
    [labelPermissionDS]
  );
  const handlePermissionAdd = React.useCallback(() => {
    labelPermissionDS.create({ labelTemplateId: labelTemplate.labelTemplateId });
  }, [labelPermissionDS, labelTemplate]);
  const permissionColumns = React.useMemo(
    () =>
      [
        !isTenant && { editor: (record) => record.status === 'add', width: 120, name: 'tenantLov' },
        {
          editor: (record) =>
            // toJSONData because tenantId is now in tenantLov
            record.status === 'add' &&
            (record.toJSONData().tenantId === organizationId || isTenant),
          width: 120,
          name: 'roleLov',
        },
        { editor: true, width: 160, name: 'startDate' },
        { editor: true, width: 160, name: 'endDate' },
        { editor: true, name: 'remark' },
        {
          name: 'operator',
          lock: 'right',
          width: 80,
          header: intl.get('hzero.common.button.action').d('操作'),
          renderer({ record }) {
            const actions = [];
            if (record.status === 'add') {
              const clearTitle = intl.get('hzero.common.button.clear').d('清空');
              actions.push({
                key: 'clear',
                title: clearTitle,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}/clear`,
                        type: 'button',
                        meaning: '分配权限-清除',
                      },
                    ]}
                    onClick={() => {
                      handleClear(record);
                    }}
                  >
                    {clearTitle}
                  </ButtonPermission>
                ),
                len: 2,
              });
            } else {
              const deleteTitle = intl.get('hzero.common.button.delete').d('删除');
              actions.push({
                key: 'delete',
                title: deleteTitle,
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}/delete`,
                        type: 'button',
                        meaning: '分配权限-删除',
                      },
                    ]}
                    onClick={() => {
                      handleDelete(record);
                    }}
                  >
                    {deleteTitle}
                  </ButtonPermission>
                ),
                len: 2,
              });
            }
            return operatorRender(actions);
          },
        },
      ].filter(Boolean),
    []
  );
  React.useEffect(() => {
    const handleOk = async () => {
      const valid = await labelPermissionDS.validate();
      if (valid) {
        const rr = await labelPermissionDS.submit();
        return (rr || {}).success || false;
      }
      return false;
    };
    modal.handleOk(handleOk);
    return () => {
      // not method to cancel hook
    };
  }, [modal.handleOk, labelPermissionDS]);
  return (
    <>
      <Form dataSet={labelTemplateDS} columns={2}>
        {!isTenant && <Lov name="tenantLov" disabled />}
        <TextField name="templateCode" disabled />
        <TextField name="templateName" disabled />
        <Lov name="datasetLov" disabled />
        <Select name="enabledFlag" disabled />
      </Form>

      <div className={TABLE_OPERATOR_CLASSNAME}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/add`,
              type: 'button',
              meaning: '分配权限-新增',
            },
          ]}
          color="primary"
          onClick={handlePermissionAdd}
        >
          {intl.get('hzero.common.button.add').d('新增')}
        </ButtonPermission>
      </div>
      <Table dataSet={labelPermissionDS} columns={permissionColumns} editMode="inline" />
    </>
  );
};

export default AssignPermission;
