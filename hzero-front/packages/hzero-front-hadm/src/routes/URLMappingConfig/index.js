/*
 * URLMappingConfig URL映射配置
 * @date: 2020-04-13
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo } from 'react';
import { DataSet, ModalContainer, Table } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import { tableDS, enabledDS } from '../../stores/urlMappingConfigDS';

const WarnConfig = ({ match: { path }, history }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const enabledDs = useMemo(() => new DataSet(enabledDS()), []);
  const columns = useMemo(
    () => [
      { name: 'urlRuleCode', width: 200 },
      { name: 'fullSourceUrl', width: 200 },
      { name: 'fullTargetUrl', width: 150 },
      { name: 'sourceTenantName', width: 150 },
      { name: 'description' },
      {
        name: 'enabledFlag',
        align: 'left',
        renderer: ({ value }) => enableRender(value),
        width: 100,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        renderer: ({ record }) => {
          const operators = [];
          operators.push(
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: 'URL映射配置-编辑',
                    },
                  ]}
                  onClick={() => {
                    handleEdit(true, record);
                  }}
                >
                  {intl.get('hzero.common.button.edit').d('编辑')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.edit').d('编辑'),
            },
            {
              key: 'enable',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enable`,
                      type: 'button',
                      meaning: 'URL映射配置-启用',
                    },
                  ]}
                  onClick={() => {
                    handleEnable(record);
                  }}
                >
                  {record.get('enabledFlag')
                    ? intl.get('hzero.common.button.disable').d('禁用')
                    : intl.get('hzero.common.button.enable').d('启用')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.enable').d('启用'),
            },
            {
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.target`,
                      type: 'button',
                      meaning: 'URL映射配置-目标',
                    },
                  ]}
                  onClick={() => {
                    handleDelete(record);
                  }}
                >
                  {intl.get('hzero.common.button.delete').d('删除')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.delete').d('删除'),
            }
          );
          return operatorRender(operators, record, { limit: 3 });
        },
        lock: 'right',
      },
    ],
    []
  );

  // 新建或编辑
  const handleEdit = (isEdit, record) => {
    if (isEdit) {
      history.push(`/hadm/url-mapping-config/detail/${record.get('urlRuleId')}`);
    } else {
      history.push('/hadm/url-mapping-config/detail/create');
    }
  };

  // 启用/禁用
  const handleEnable = async (record) => {
    if (record) {
      const data = record.toData();
      enabledDs.create(data, 0);
      await enabledDs.submit();
      tableDs.query();
    }
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  return (
    <>
      <Header title={intl.get('hadm.urlMappingConfig.view.title.urlMapping').d('URL映射配置')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-create`,
              type: 'button',
              meaning: 'URL映射配置-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.urlMappingConfig', 'hadm.common'] })(WarnConfig);
