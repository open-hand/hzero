/*
 * LogicalDataSourceGroupDetail 逻辑数据源组详情
 * @date: 2020-05-06
 * @author: LiLin <lin.li03@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo, useState, useEffect } from 'react';
import queryString from 'querystring';
import { Card, Icon } from 'choerodon-ui';
import { Form, Spin, Table, Modal, Button, DataSet, TextField } from 'choerodon-ui/pro';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { Content, Header } from 'components/Page';
import formatterCollections from 'utils/intl/formatterCollections';
import { Button as ButtonPermission } from 'components/Permission';

import {
  formDS,
  orderDS,
  drawerDS,
  setDefaultDS,
  dataSourceListDS,
  dataSourceFormDS,
} from '../../stores/LogicalDataSourceGroupDS';
import styles from './index.less';
import DataSourceDrawer from './DataSourceDrawer';

const Detail = (props) => {
  const {
    match: { path },
  } = props;

  const [isCreate, setIsCreate] = useState(true);
  const formDs = useMemo(() => new DataSet(formDS()), []);
  const orderDs = useMemo(() => new DataSet(orderDS()), []);
  const drawerDs = useMemo(() => new DataSet(drawerDS()), []);
  const setDefaultDs = useMemo(() => new DataSet(setDefaultDS()), []);
  const dataSourceListDs = useMemo(() => new DataSet(dataSourceListDS()), []);
  const dataSourceFormDs = useMemo(() => new DataSet(dataSourceFormDS()), []);

  useEffect(() => {
    const {
      location: { search },
      match: { params },
    } = props;
    const { type } = queryString.parse(search.substring(1));
    setIsCreate(type === 'create');
    if (type === 'edit') {
      const { id } = params;
      queryData(id);
    }
  }, []);

  const columns = useMemo(
    () => [
      { name: 'datasourceOrder', width: 80, align: 'left' },
      { name: 'datasourceName', width: 200 },
      {
        name: 'datasourceInfo',
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 240,
        lock: 'right',
        renderer: ({ record }) => {
          const operators = [];
          const { totalCount } = dataSourceListDs;
          const order = record.get('datasourceOrder');
          // 判断该数据是否为新建数据
          if (totalCount > 1) {
            // 当数据不为一条时，可以更改排序
            if (order > 0 && order <= totalCount - 1) {
              // 除最后一个都可以向上移动(不包括新建未保存的数据)
              operators.push({
                key: 'up',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.up`,
                        type: 'button',
                        meaning: '逻辑数据源组管理详情-向上移动',
                      },
                    ]}
                    onClick={() => {
                      handleChangeOrder(record, 'up');
                    }}
                  >
                    <Icon type="arrow_upward" style={{ marginTop: '-4px' }} />
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hadm.LogicalDataSource.view.button.up').d('上移'),
              });
            }
            if (order < totalCount - 1) {
              // 除最后一个都可以向下移动(不包括新建未保存的数据)
              operators.push({
                key: 'down',
                ele: (
                  <ButtonPermission
                    type="text"
                    permissionList={[
                      {
                        code: `${path}.button.down`,
                        type: 'button',
                        meaning: '逻辑数据源组管理详情-向下移动',
                      },
                    ]}
                    onClick={() => {
                      handleChangeOrder(record, 'down');
                    }}
                  >
                    <Icon type="arrow_downward" style={{ marginTop: '-4px' }} />
                  </ButtonPermission>
                ),
                len: 2,
                title: intl.get('hadm.LogicalDataSource.view.button.down').d('下移'),
              });
            }
          }
          operators.push(
            {
              key: 'setDefault',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.setDefault`,
                      type: 'button',
                      meaning: '逻辑数据源组管理详情-设为默认',
                    },
                  ]}
                  onClick={() => {
                    handleSetDefault(record);
                  }}
                >
                  {intl.get('hadm.LogicalDataSource.view.button.setDefault').d('设为默认')}
                </ButtonPermission>
              ),
              len: 4,
              title: intl.get('hadm.LogicalDataSource.view.button.setDefault').d('设为默认'),
            },
            {
              key: 'edit',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.edit`,
                      type: 'button',
                      meaning: '逻辑数据源组管理详情-编辑',
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
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.delete`,
                      type: 'button',
                      meaning: '逻辑数据源组管理详情-删除',
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
          return operatorRender(operators, record, { limit: 4 });
        },
      },
    ],
    []
  );

  const queryData = async (id) => {
    try {
      formDs.setQueryParameter('datasourceGroupId', id);
      dataSourceListDs.setQueryParameter('datasourceGroupId', id);
      formDs.query();
      await dataSourceListDs.query();
    } catch (e) {
      //
    }
  };

  // 新建或编辑
  const handleEdit = (isEdit, record) => {
    drawerDs.create({});
    dataSourceFormDs.create({ isRequired: true });
    dataSourceFormDs.create({}); // 为了创建两条数据所以使用两次create
    const currentEditData = record && record.toData();
    const title = !isEdit
      ? intl.get('hadm.LogicalDataSource.view.title.createDataSourceList').d('新建逻辑数据源')
      : intl.get('hadm.LogicalDataSource.view.title.editDataSourceList').d('编辑逻辑数据源');
    const drawerProps = {
      isEdit,
      drawerDs,
      currentEditData,
      dataSourceFormDs,
    };
    Modal.open({
      drawer: true,
      key: 'editDrawer',
      destroyOnClose: true,
      closable: true,
      title,
      children: <DataSourceDrawer {...drawerProps} />,
      okText: intl.get('hzero.common.button.save').d('保存'),
      onOk: () => handleOk(isEdit, record),
      onCancel: () => {
        drawerDs.removeAll();
        dataSourceFormDs.removeAll();
      },
      onClose: () => {
        drawerDs.removeAll();
        dataSourceFormDs.removeAll();
      },
    });
  };

  // 模态框保存
  const handleOk = async (isEdit, record) => {
    const {
      match: { params },
    } = props;
    const { id } = params;
    try {
      const drawerValidate = await drawerDs.current.validate(true);
      if (!drawerValidate) {
        return false;
      }
      const validate = await dataSourceFormDs.submit();
      if (validate === false) {
        return false;
      }
    } catch {
      return false;
    }
    const { dataType, loadBalanceAlgorithmType } = drawerDs.current.toData();
    const [masterDatasource, ...slaveDatasource] = dataSourceFormDs.toData();
    let data = {
      datasourceGroupId: id,
      datasourceOrder: dataSourceListDs.toData().length,
    };
    if (dataType === 'masterSlave') {
      // 为主从数据
      data = {
        ...data,
        slaveDatasource,
        masterDatasource,
        loadBalanceAlgorithmType,
      };
    } else {
      // 为单数据
      data = {
        ...data,
        masterDatasource,
      };
    }
    if (isEdit) {
      const list = dataType === 'masterSlave' ? slaveDatasource : [];
      record.set('slaveDatasource', list);
      record.set('masterDatasource', masterDatasource);
      record.set('loadBalanceAlgorithmType', loadBalanceAlgorithmType);
    } else {
      dataSourceListDs.create(data);
    }
    await dataSourceListDs.submit();
    dataSourceListDs.query();
  };

  // 删除
  const handleDelete = async (record) => {
    await dataSourceListDs.delete(record);
    dataSourceListDs.query();
  };

  const handleSave = async () => {
    const { history } = props;
    if (isCreate) {
      try {
        const validate = await formDs.submit();
        if (!validate) {
          return false;
        }
      } catch {
        return false;
      }
      history.push('/hadm/logical-data-source-group/list');
    } else {
      // formDs.current.set('spDatasourceList', dataSourceListDs.toData());
      try {
        const validate = await formDs.submit();
        if (!validate) {
          return false;
        }
        await formDs.query();
        // await dataSourceListDs.query();
      } catch {
        return false;
      }
    }
  };

  // 更改数据源行数据序号
  const handleChangeOrder = async (record, direction) => {
    const page = dataSourceListDs.currentPage;
    const order = record.get('datasourceOrder');
    orderDs.create(
      {
        dstOrder: direction === 'up' ? order - 1 : order + 1,
        srcDatasourceId: record.get('datasourceId'),
      },
      0
    );
    await orderDs.submit();
    dataSourceListDs.query(page);
  };

  // 设为默认
  const handleSetDefault = async (record) => {
    const datasourceGroupId = formDs.current.get('datasourceGroupId');
    const datasourceName = record.get('datasourceName');
    setDefaultDs.create(
      {
        datasourceGroupId,
        defaultDatasourceName: datasourceName,
      },
      0
    );
    await setDefaultDs.submit();
    formDs.query();
    dataSourceListDs.query();
  };

  return (
    <>
      <Header
        title={
          isCreate
            ? intl.get('hzero.common.view.title.create').d('新建')
            : intl.get('hzero.common.view.title.edit').d('编辑')
        }
        backPath="/hadm/logical-data-source-group/list"
      >
        <Button color="primary" icon="save" onClick={handleSave}>
          {intl.get(`hzero.common.button.save`).d('保存')}
        </Button>
      </Header>
      <Content>
        <Card
          bordered={false}
          title={intl.get('hadm.LogicalDataSource.view.title.dataSourceHeader').d('逻辑数据源头')}
        >
          <Spin dataSet={formDs}>
            <Form dataSet={formDs} columns={3}>
              <TextField name="datasourceGroupName" />
              {!isCreate && <TextField name="defaultDatasourceName" disabled />}
              <TextField name="description" />
            </Form>
          </Spin>
        </Card>
        {!isCreate && (
          <Card
            bordered={false}
            title={intl.get('hadm.LogicalDataSource.view.title.dataSourceList').d('逻辑数据源行')}
          >
            <div className={styles['detail-table-wrap']}>
              <div>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}.button.create`,
                      type: 'button',
                      meaning: '逻辑数据源详情-新建',
                    },
                  ]}
                  icon="add"
                  style={{ marginBottom: 10, float: 'right' }}
                  onClick={() => handleEdit(false)}
                >
                  {intl.get('hzero.common.button.create').d('新建')}
                </ButtonPermission>
              </div>
              <Table dataSet={dataSourceListDs} columns={columns} />
            </div>
          </Card>
        )}
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.LogicalDataSource'] })(Detail);
