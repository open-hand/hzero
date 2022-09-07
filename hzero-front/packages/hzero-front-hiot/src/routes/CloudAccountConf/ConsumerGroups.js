/*
 * ConsumerGroups 消费者组
 * @date: 2020-04-26
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo } from 'react';
import { Table, Modal, Button, DataSet, TextField, ModalContainer } from 'choerodon-ui/pro';
import { uniqBy, isUndefined } from 'lodash';
import intl from 'utils/intl';
import { Content, Header } from 'components/Page';
import { yesOrNoRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import {
  consumerTableDS,
  bindThingDS,
  drawerTableDS,
  startMonitorDS,
} from '../../stores/cloudAccountConfDS';

const ConsumerGroups = (props) => {
  const {
    match: { params, path },
  } = props;
  const { configId } = params;
  const tableDs = useMemo(
    () =>
      new DataSet({
        ...consumerTableDS(),
        queryParameter: {
          configId,
        },
      }),
    []
  );
  const bindThingDs = useMemo(() => new DataSet(bindThingDS()), []);
  const startMonitorDs = useMemo(() => new DataSet(startMonitorDS()), []);
  let drawerTableDs = null;
  let dataList = []; // 用来存储drawerTableDs返回数据中被选中的数据

  const columns = useMemo(
    () => [
      {
        name: 'code',
        width: 180,
        editor: (record) => {
          return isUndefined(record.get('nonEditable')) && record.getState('editing');
        },
      },
      {
        name: 'name',
        width: 200,
        editor: (record) =>
          record.getState('editing') && record.get('nonEditable') !== 1 && <TextField />,
      },
      {
        name: 'guid',
        width: 150,
        editor: (record) =>
          record.getState('editing') && record.get('nonEditable') !== 1 && <TextField />,
      },
      {
        name: 'description',
        editor: (record) => record.getState('editing'),
      },
      {
        name: 'enableListenerFlag',
        width: 120,
        align: 'center',
        renderer: ({ value }) => yesOrNoRender(value),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        align: 'center',
        width: 260,
        renderer: ({ record }) => {
          return commands(record);
        },
        lock: 'right',
      },
    ],
    []
  );

  const drawerTableColumns = useMemo(
    () => [{ name: 'thingModelCode' }, { name: 'thingModelName' }, { name: 'consumerName' }],
    []
  );

  const commands = (record) => {
    const operators = [];
    if (record.getState('editing')) {
      operators.push(
        {
          key: 'save',
          ele: <a onClick={handleSubmit}>{intl.get('hzero.common.button.save').d('保存')}</a>,
          len: 2,
          title: intl.get('hzero.common.button.save').d('保存'),
        },
        {
          key: 'cancel',
          ele: (
            <a onClick={() => handleCancel(record)}>
              {intl.get('hzero.common.button.cancel').d('取消')}
            </a>
          ),
          len: 2,
          title: intl.get('hzero.common.button.save').d('保存'),
        }
      );
    } else {
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
                  meaning: '消费者组-编辑',
                },
              ]}
              onClick={() => handleEdit(record)}
              disabled={record.status === 'delete'}
            >
              {intl.get('hzero.common.button.edit').d('编辑')}
            </ButtonPermission>
          ),
          len: 2,
          title: intl.get('hzero.common.button.edit').d('编辑'),
        },
        {
          key: 'assignTemplate',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.assignTemplate`,
                  type: 'button',
                  meaning: '消费者组-分配模板',
                },
              ]}
              onClick={() => {
                handleDetailView(record);
              }}
            >
              {intl.get('hiot.consumerGroups.view.button.assignTemplate').d('分配模板')}
            </ButtonPermission>
          ),
          len: 4,
          title: intl.get('hiot.consumerGroups.view.button.assignTemplate').d('分配模板'),
        },
        {
          key: 'enable',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.monitor`,
                  type: 'button',
                  meaning: '消费者组-监听',
                },
              ]}
              onClick={() => {
                handleStartMonitor(record);
              }}
              disabled={record.get('enableListenerFlag') === 1}
            >
              {intl.get('hiot.consumerGroups.view.button.startMonitor').d('启动监听')}
            </ButtonPermission>
          ),
          len: 4,
          title: intl.get('hiot.consumerGroups.view.button.startMonitor').d('启动监听'),
        }
      );
      if (record.get('enableListenerFlag') !== 1) {
        operators.push({
          key: 'delete',
          ele: (
            <ButtonPermission
              type="text"
              permissionList={[
                {
                  code: `${path}.button.delete`,
                  type: 'button',
                  meaning: '消费者组-删除',
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
        });
      }
    }

    return operatorRender(operators, record, { limit: 4 });
  };

  // 查看详情
  const handleDetailView = (record) => {
    drawerTableDs = new DataSet({
      ...drawerTableDS(),
      events: {
        load: ({ dataSet }) => {
          const { consumerGroupId } = dataSet.queryParameter;
          const selectData = [];
          dataSet.data.forEach((item) => {
            if (item.get('consumerGroupId') === consumerGroupId) {
              dataSet.select(item);
              selectData.push(item.get('thingModelId'));
            }
          });
          dataList = uniqBy([...dataList, ...selectData]);
        },
      },
    });
    const consumerGroupId = record.get('consumerGroupId');
    drawerTableDs.setQueryParameter('consumerGroupId', consumerGroupId);
    drawerTableDs.query();
    Modal.open({
      key: 'thingModel',
      destroyOnClose: true,
      closable: true,
      style: { width: 600 },
      title: intl.get('hiot.consumerGroups.view.button.assignTemplate').d('分配模板'),
      children: <Table dataSet={drawerTableDs} columns={drawerTableColumns} queryFieldsLimit={2} />,
      onOk: () => handleOk(record),
    });
  };

  // 保存绑定设备模板
  const handleOk = async (record) => {
    try {
      const thingModelIds = drawerTableDs.selected.map((item) => item.get('thingModelId'));
      const deleteModelIds = dataList.filter((item) => !thingModelIds.includes(item));
      const createModelIds = thingModelIds.filter((item) => !dataList.includes(item));
      const data = record.toData();
      bindThingDs.create({ ...data, createModelIds, deleteModelIds }, 0);
      const validate = await bindThingDs.submit();
      if (!validate) {
        return false;
      }
    } catch {
      dataList = [];
      return false;
    }
    tableDs.query();
    dataList = [];
  };

  // 保存新建或编辑
  const handleSubmit = async () => {
    const validate = await tableDs.submit();
    if (validate) {
      tableDs.query();
    }
  };

  // 编辑
  const handleEdit = (record) => {
    record.setState('editing', true);
  };

  // 新建
  const handleAdd = () => {
    const record = tableDs.create({ configId }, 0);
    record.setState('editing', true);
  };

  // 取消保存
  const handleCancel = (record) => {
    if (record.status === 'add') {
      tableDs.remove(record);
    } else {
      record.reset();
      record.setState('editing', false);
    }
  };

  // 删除
  const handleDelete = async (record) => {
    await tableDs.delete(record);
    tableDs.query();
  };

  // 启动监听
  const handleStartMonitor = async (record) => {
    const data = {
      ...record.toData(),
      configId,
    };
    startMonitorDs.create(data, 0);
    try {
      const validate = await startMonitorDs.submit();
      if (validate) {
        tableDs.query();
      }
    } catch (e) {
      // return e
    }
  };

  return (
    <>
      <Header
        backPath="/hiot/cloud-account/config/list"
        title={intl.get('hiot.consumerGroups.view.title.consumerGroups').d('消费者组')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}/create`,
              type: 'button',
              meaning: '消费者组-新建',
            },
          ]}
          icon="add"
          color="primary"
          onClick={handleAdd}
        >
          {intl.get('hzero.common.button.create').d('新建')}
        </ButtonPermission>
        <Button icon="autorenew" onClick={() => tableDs.query()}>
          {intl.get('hzero.common.button.reload').d('重新加载')}
        </Button>
      </Header>
      <Content>
        <Table dataSet={tableDs} columns={columns} />
        <ModalContainer location={location} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hiot.consumerGroups'] })(ConsumerGroups);
