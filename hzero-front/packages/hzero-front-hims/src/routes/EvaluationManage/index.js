/**
 * 评价管理 - 列表页
 * @date: 2020-09-17
 * @author: LiLin <lin.li03@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useMemo } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import Drawer from './Drawer';
import { tableDS, drawerDS } from '../../stores/EvaluationManageDS';

const EvaluationManage = () => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);

  let drawerDs = null;

  const columns = useMemo(
    () => [
      {
        name: 'evaluationTypeMeaning',
        width: 220,
      },
      {
        name: 'csUserName',
        width: 220,
      },
      {
        name: 'userName',
        width: 220,
      },

      {
        name: 'score',
        align: 'left',
        width: 80,
      },

      {
        name: 'remark',
      },
      {
        name: 'createAt',
        width: 200,
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        lock: 'right',

        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'view',
            ele: (
              <a
                onClick={() => {
                  handleView(record);
                }}
              >
                {intl.get('hzero.common.button.view').d('查看')}
              </a>
            ),
            len: 2,
            title: intl.get('hzero.common.button.view').d('查看'),
          });
          return operatorRender(operators);
        },
      },
    ],
    []
  );

  // 查看
  const handleView = (record) => {
    drawerDs = new DataSet(drawerDS());
    drawerDs.create({});
    const currentEditData = record && record.toData();
    const title = intl.get('hzero.common.button.view').d('查看');
    const drawerProps = {
      drawerDs,
      currentEditData,
    };
    Modal.open({
      drawer: true,
      key: 'viewDrawer',
      destroyOnClose: true,
      style: { width: 600 },
      closable: true,
      title,
      children: <Drawer {...drawerProps} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  return (
    <>
      <Header title={intl.get('hims.evaluation.view.title.evaluateManage').d('评价管理')} />
      <Content>
        <Table dataSet={tableDs} columns={columns} queryFieldsLimit={3} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hims.evaluation'] })(EvaluationManage);
