/*
 * DataFlowPipeline 数据流管道
 * @date: 2020-07-15
 * @author JMY <mingyang.jin@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo, useEffect } from 'react';
import { DataSet, Table, Modal } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import notification from 'utils/notification';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import {
  tableDS,
  enabledDS,
  drawerDS,
  instanceDS,
  CheckDataDS,
  logViewDS,
  logFormDS,
} from '../../stores/DataFlowPipelineDS';
import Drawer from './Drawer';
import LogViewDrawer from './LogViewDrawer';

const DataFlowPipeline = ({ match: { path } }) => {
  const tableDs = useMemo(() => new DataSet(tableDS()), []);
  const enabledDs = useMemo(() => new DataSet(enabledDS()), []);
  const drawerDs = useMemo(() => new DataSet(drawerDS()), []);
  const instanceDs = useMemo(() => new DataSet(instanceDS()), []);
  const CheckDataDs = useMemo(() => new DataSet(CheckDataDS()), []);
  const logFormDs = useMemo(() => new DataSet(logFormDS()), []);
  const logViewDs = useMemo(
    () =>
      new DataSet({
        ...logViewDS(),
        // queryDataSet: logFormDs,
        events: {
          query: ({ dataSet }) => {
            return !dataSet.interceptQuery;
          },
        },
      }),
    []
  );
  useEffect(() => {
    tableDs.query();
  }, []);

  const columns = useMemo(
    () => [
      { name: 'pipelineCode', width: 200 },
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
                      meaning: '数据流管道-编辑',
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
              key: 'restart',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.restart`,
                      type: 'button',
                      meaning: '数据流管道-重启',
                    },
                  ]}
                  onClick={() => {
                    handleEnable(record);
                  }}
                >
                  {intl.get('hzero.common.button.restart').d('重启')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hzero.common.button.restart').d('重启'),
            },
            {
              key: 'instance',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.instance`,
                      type: 'button',
                      meaning: '数据流管道-查看实例',
                    },
                  ]}
                  onClick={() => {
                    handleInStance(record);
                  }}
                >
                  {intl.get('hdsc.dataFlowPipeline.button.instance').d('查看实例')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hdsc.dataFlowPipeline.button.instance').d('查看实例'),
            },
            {
              key: 'delete',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '数据流管道-删除',
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
            },
            {
              key: 'logView',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.logView`,
                      type: 'button',
                      meaning: '数据流管道-查看日志',
                    },
                  ]}
                  onClick={() => {
                    handleLogView(record);
                  }}
                >
                  {intl.get('hdsc.dataFlowPipeline.view.button.logView').d('查看日志')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('hdsc.dataFlowPipeline.view.button.logView').d('查看日志'),
            },
            {
              key: 'enableCheck',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.enableCheck`,
                      type: 'button',
                      meaning: '数据流管道-开启数据核对',
                    },
                  ]}
                  onClick={() => {
                    handleCheckData(record, true);
                  }}
                >
                  {intl.get('hdsc.dataFlowPipeline.view.button.enableCheck').d('开启数据核对')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hdsc.dataFlowPipeline.view.button.enableCheck').d('开启数据核对'),
            },
            {
              key: 'disableCheck',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.disableCheck`,
                      type: 'button',
                      meaning: '数据流管道-停止数据核对',
                    },
                  ]}
                  onClick={() => {
                    handleCheckData(record, false);
                  }}
                >
                  {intl.get('hdsc.dataFlowPipeline.view.button.disableCheck').d('停止数据核对')}
                </ButtonPermission>
              ),
              len: 6,
              title: intl.get('hdsc.dataFlowPipeline.view.button.disableCheck').d('停止数据核对'),
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
    const pipelineId = isEdit ? record.get('pipelineId') : 0;
    Modal.open({
      title: isEdit
        ? intl.get('hdsc.dataFlowPipeline.view.title.edit').d('编辑')
        : intl.get('hdsc.dataFlowPipeline.view.title.create').d('新建'),
      drawer: true,
      width: 520,
      children: <Drawer drawerDs={drawerDs} isEdit={isEdit} pipelineId={pipelineId} path={path} />,
      onOk: async () => {
        const flag = await drawerDs.validate();
        if (flag) {
          await drawerDs.submit();
          tableDs.query();
        }
        return flag;
      },
      afterClose: () => {
        drawerDs.reset();
      },
    });
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

  // 启用/禁用核对数据
  const handleCheckData = async (record, isEnable) => {
    const data = record.toData();
    CheckDataDs.isEnable = isEnable;
    CheckDataDs.create(data, 0);
    try {
      const validate = await CheckDataDs.submit();
      if (validate) {
        tableDs.query();
      }
    } catch (e) {
      // return e
    }
  };

  const handleInStance = async (record) => {
    if (record) {
      instanceDs.setQueryParameter('pipelineId', record.get('pipelineId'));
      instanceDs.query().then((res) => {
        if (res && res.status !== 204) {
          Modal.open({
            key: 'instance',
            title: intl.get('hdsc.dataFlowPipeline.view.title.instance').d('当前实例'),
            drawer: false,
            children: (
              <>
                {res.map((item) => {
                  return <p>{item}</p>;
                })}
              </>
            ),
            footer: null,
            closable: true,
          });
        } else {
          notification.info({
            message: intl.get('hdsc.dataFlowPipeline.view.message.instance').d('当前没有实例'),
          });
        }
      });
    }
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  // 查看日志
  const handleLogView = (record) => {
    const pipelineCode = record.get('pipelineCode');
    Modal.open({
      key: 'logView',
      title: intl.get('hdsc.dataFlowPipeline.view.button.logView').d('查看日志'),
      drawer: true,
      style: { width: 900 },
      destroyOnClose: true,
      children: (
        <LogViewDrawer pipelineCode={pipelineCode} logFormDs={logFormDs} logViewDs={logViewDs} />
      ),
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
      closable: true,
      onOk: () => {
        logViewDs.interceptQuery = true;
        logFormDs.current.reset();
      },
    });
  };

  return (
    <>
      <Header title={intl.get('hdsc.dataFlowPipeline.view.title.service').d('数据流管道')}>
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-create`,
              type: 'button',
              meaning: '数据流管道-新建',
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
        <Table dataSet={tableDs} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hdsc.dataFlowPipeline'] })(DataFlowPipeline);
