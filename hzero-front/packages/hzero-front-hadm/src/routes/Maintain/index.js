/*
 * Maintain 在线运维
 * @date: 2020-06-2
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { Table, DataSet } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';

import { tableDS as tableDs } from '../../stores/MaintainDS';

const Maintain = (props) => {
  const {
    match: { path },
  } = props;

  const tableDS = React.useMemo(() => new DataSet(tableDs()), []);

  const columns = React.useMemo(
    () => [
      { name: 'maintainVersion', width: 300 },
      { name: 'description', width: 300 },
      {
        name: 'stateMeaning',
        align: 'left',
        renderer: ({ record }) => {
          return (
            // eslint-disable-next-line no-nested-ternary
            record.get('state') === 'UNUSED' ? (
              <Tag color="red">{intl.get('hadm.maintain.view.message.tag.unused').d('未运维')}</Tag>
            ) : record.get('state') === 'ACTIVE' ? (
              <Tag color="green">
                {intl.get('hadm.maintain.view.message.tag.active').d('运维中')}
              </Tag>
            ) : (
              <Tag color="blue">{intl.get('hadm.maintain.view.message.tag.use').d('已运维')}</Tag>
            )
          );
        },
      },
      { name: 'creationDate', width: 200 },
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
                      meaning: '在线运维-编辑',
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
              key: 'delete',
              ele: (
                <span className="action-link">
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.target`,
                        type: 'button',
                        meaning: '在线运维-删除',
                      },
                    ]}
                    onClick={() => handleDelete(record)}
                  >
                    {intl.get('hzero.common.button.delete').d('删除')}
                  </ButtonPermission>
                </span>
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

  /**
   *
   * @param {*} record
   * 删除
   */
  const handleDelete = async (record) => {
    await tableDS.delete(record);
  };

  /**
   *
   * @param {*} isEdit
   * @param {*} record
   * 路由规则 编辑 && 新建
   */
  const handleEdit = (isEdit, record) => {
    // 跳转到自路由
    const { history } = props;
    const maintainId = isEdit ? record.get('maintainId') : 'create';
    tableDS.setQueryParameter('maintainId', maintainId);
    history.push(`/hadm/maintain/detail/${maintainId}`);
  };

  return (
    <>
      <Header title={intl.get('hadm.maintain.view.message.title.maintain').d('在线运维')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/create`,
              type: 'button',
              meaning: '在线运维-新建',
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
        <Table dataSet={tableDS} columns={columns} queryFieldsLimit={2} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.maintain', 'hadm.common'] })(Maintain);
