/**
 * 通用模板管理 - 列表页
 * @date: 2020-8-5
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { enableRender, operatorRender } from 'utils/renderer';
import { isTenantRoleLevel } from 'utils/utils';
import { tableDS } from '../../stores/GeneralTemplateDS';

const isTenant = isTenantRoleLevel();
const GeneralTemplate = ({ history, match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  const columns = useMemo(
    () =>
      [
        !isTenant && { name: 'tenantName' },
        { name: 'templateCode' },
        { name: 'templateName' },
        { name: 'templateCategoryCode' },
        { name: 'lang' },
        {
          name: 'enabledFlag',
          renderer: ({ value }) => enableRender(value),
          align: 'center',
          width: 100,
        },
        { name: 'creationDate' },
        {
          header: intl.get('hzero.common.button.action').d('操作'),
          width: 80,
          renderer: ({ record }) => {
            const operators = [
              {
                key: 'edit',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.edit`,
                        type: 'button',
                        meaning: '通用模板管理-编辑',
                      },
                    ]}
                    onClick={() => {
                      handleEdit(true, record, 'edit');
                    }}
                  >
                    {intl.get('hzero.common.button.edit').d('编辑')}
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hzero.common.button.edit').d('编辑'),
              },
            ];
            return operatorRender(operators);
          },
          lock: 'right',
        },
      ].filter(Boolean),
    []
  );

  // 新建/编辑
  const handleEdit = (isEdit, record, type) => {
    if (isEdit) {
      history.push({
        pathname: `/hpfm/general-template/detail/${type}/${record.get('templateId')}`,
      });
    } else {
      history.push('/hpfm/general-template/detail/create/0');
    }
  };

  return (
    <>
      <Header
        title={intl.get('hpfm.generalTemplate.view.message.generalTemplate').d('通用模板管理')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}.button.create`,
              type: 'button',
              meaning: '通用模板管理-新建',
            },
          ]}
          color="primary"
          icon="add"
          onClick={() => handleEdit(false)}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table queryFieldsLimit={3} dataSet={tableDs} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hpfm.generalTemplate'] })(GeneralTemplate);
