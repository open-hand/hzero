/**
 * 标签模版管理
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2019/11/29
 * @copyright 2019 ® HAND
 */

import React from 'react';
import { DataSet, Modal, Table } from 'choerodon-ui/pro';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import { operatorRender } from 'utils/renderer';

import { labelTemplateListDS } from '@/stores/labelTemplateDS';

import AssignPermission from './AssignPermission';
import PrintSettings from './PrintSettings';
import Preview from './Preview';

const permissionModalKey = Modal.key();
const printModalKey = Modal.key();

const LabelTemplate = ({ history, match }) => {
  const isTenant = isTenantRoleLevel();
  const organizationId = getCurrentOrganizationId();
  let copy = false;
  const { path } = match;
  // dataset
  const ds = React.useMemo(() => {
    const datasetConfig = labelTemplateListDS();
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
          case 'datasetLov':
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
    }
    return new DataSet(datasetConfig);
  }, [isTenant]);
  const handleGoToDetail = React.useCallback(
    (record) => {
      history.push({
        pathname: `/hrpt/label-template/detail/${record.get('labelTemplateId')}`,
      });
    },
    [history]
  );
  const handleAssignPermission = React.useCallback((record) => {
    Modal.open({
      key: permissionModalKey,
      title: intl.get('hrpt.common.view.assignPermission').d('分配权限'),
      children: <AssignPermission labelTemplate={record.toJSONData()} match={match} />,
      drawer: true,
      style: { width: 1000 },
    });
  }, []);
  const handlePrintSetting = React.useCallback((record) => {
    Modal.open({
      key: printModalKey,
      title: intl.get('hrpt.labelTemplate.view.title.printSetting').d('打印设置'),
      children: (
        <PrintSettings labelTemplate={record.toJSONData()} tenantId={record.get('tenantId')} />
      ),
      drawer: true,
      style: { width: 520 },
    });
  }, []);
  const handlePreview = React.useCallback((record) => {
    Modal.open({
      key: printModalKey,
      title: intl.get('hrpt.labelTemplate.view.title.preview').d('预览'),
      children: (
        <Preview
          labelTemplateCode={record.toJSONData().templateCode}
          tenantId={record.get('tenantId')}
        />
      ),
      drawer: true,
      style: { width: 1000 },
      footer: (okBtn) => okBtn,
    });
  }, []);
  const handlePrint = React.useCallback((record) => {
    history.push({
      pathname: `/hrpt/label-template/print/${record.get('templateCode')}`,
      search: `tenantId=${record.get('tenantId')}`,
    });
  }, []);
  const handleCreate = React.useCallback(() => {
    ds.create();
  }, [ds]);
  const handleCopy = React.useCallback(
    (record) => {
      ds.create(record.data);
      copy = true;
    },
    [ds]
  );
  const handleSave = React.useCallback(async () => {
    debugger;
    const valid = await ds.validate();
    if (copy) {
      ds.setQueryParameter('copy', true);
    }
    if (valid) {
      await ds.submit();
      await ds.query(ds.currentPage).then((res) => {
        if (res) {
          copy = false;
        }
      });
    }
  }, [ds]);
  const handleDelete = React.useCallback(
    async (record) => {
      await ds.delete(record);
      await ds.query(ds.currentPage);
    },
    [ds]
  );
  const handleClear = React.useCallback(
    (record) => {
      ds.delete([record]);
    },
    [ds]
  );
  const columns = React.useMemo(() => {
    const editor = ({ status }) => status === 'add';
    return [
      { editor, width: 120, name: 'templateCode' },
      { editor, name: 'templateName' },
      !isTenant && { editor, width: 200, name: 'tenantLov' },
      { editor, width: 200, name: 'datasetLov' },
      { editor, width: 100, name: 'templateWidth', aligh: 'left' },
      { editor, width: 100, name: 'templateHigh', aligh: 'left' },
      { editor, width: 100, name: 'enabledFlag' },
      {
        name: 'operator',
        width: 220,
        lock: 'right',
        align: 'left',
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
                      meaning: '标签汇总查询-清除',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
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
            const editTitle = intl.get('hzero.common.button.edit').d('编辑');
            actions.push({
              key: 'edit',
              title: editTitle,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/edit`,
                      type: 'button',
                      meaning: '标签打印-编辑',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handleGoToDetail(record);
                  }}
                >
                  {editTitle}
                </ButtonPermission>
              ),
              len: 2,
            });
            const copyTitle = intl.get('hzero.common.button.copy').d('复制');
            actions.push({
              key: 'copy',
              title: copyTitle,
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/copy`,
                      type: 'button',
                      meaning: '标签打印-复制',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handleCopy(record);
                  }}
                >
                  {copyTitle}
                </ButtonPermission>
              ),
              len: 2,
            });
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
                      meaning: '标签打印-删除',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handleDelete(record);
                  }}
                >
                  {deleteTitle}
                </ButtonPermission>
              ),
              len: 2,
            });
            // 只要能查询出来, 就能分配权限
            const assignPermissionTitle = intl
              .get('hrpt.common.view.assignPermission')
              .d('分配权限');
            actions.push({
              key: 'assignPermission',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/assignPermission`,
                      type: 'button',
                      meaning: '标签打印-分配权限',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handleAssignPermission(record);
                  }}
                >
                  {assignPermissionTitle}
                </ButtonPermission>
              ),
              title: assignPermissionTitle,
              len: 4,
            });
            const printSettingsTitle = intl
              .get('hrpt.labelTemplate.view.button.printSettings')
              .d('打印配置');
            actions.push({
              key: 'printSettings',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/printSettings`,
                      type: 'button',
                      meaning: '标签打印-打印配置',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handlePrintSetting(record);
                  }}
                >
                  {printSettingsTitle}
                </ButtonPermission>
              ),
              title: printSettingsTitle,
              len: 4,
            });
            const previewTitle = intl.get('hrpt.labelTemplate.view.button.preview').d('模板预览');
            actions.push({
              key: 'preview',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/previewTitle`,
                      type: 'button',
                      meaning: '标签打印-标签预览',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handlePreview(record);
                  }}
                >
                  {previewTitle}
                </ButtonPermission>
              ),
              title: previewTitle,
              len: 2,
            });
            const printTitle = intl.get('hrpt.labelTemplate.view.button.print').d('标签打印');
            actions.push({
              key: 'print',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}/print`,
                      type: 'button',
                      meaning: '标签打印-标签打印',
                    },
                  ]}
                  style={{ cursor: 'pointer' }}
                  onClick={() => {
                    handlePrint(record);
                  }}
                >
                  {printTitle}
                </ButtonPermission>
              ),
              title: previewTitle,
              len: 2,
            });
          }
          return operatorRender(actions, record, { limit: 4 });
        },
      },
    ].filter(Boolean);
  }, [
    handleDelete,
    handleGoToDetail,
    handleAssignPermission,
    handlePrintSetting,
    handlePreview,
    handleClear,
  ]);
  return (
    <>
      <Header title={intl.get('hrpt.labelTemplate.view.title.labelTemplate').d('标签打印管理')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/add`,
              type: 'button',
              meaning: '标签打印-新增',
            },
          ]}
          icon="add"
          // className="ant-btn-primary"
          // style={{ color: '#fff' }}
          color="primary"
          onClick={handleCreate}
          disabled={ds.created.length !== 0}
        >
          {intl.get('hzero.common.button.add').d('新增')}
        </ButtonPermission>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/save`,
              type: 'button',
              meaning: '标签打印-保存',
            },
          ]}
          icon="save"
          onClick={handleSave}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={ds} columns={columns} editMode="inline" />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hrpt.labelTemplate', 'hrpt.common'] })(LabelTemplate);
