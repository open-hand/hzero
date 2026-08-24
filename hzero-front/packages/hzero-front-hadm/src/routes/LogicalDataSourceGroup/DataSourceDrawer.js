import uuid from 'uuid/v4';
import { isEmpty } from 'lodash';
import React, { useState } from 'react';
import { Tabs, Card, Icon, Tooltip, Popconfirm } from 'choerodon-ui';
import { Form, Spin, Select, Password, SelectBox, TextField, NumberField } from 'choerodon-ui/pro';

import intl from 'utils/intl';

const { TabPane } = Tabs;
const { Option } = SelectBox;

const DataSourceDrawer = (props) => {
  const { isEdit, drawerDs, currentEditData, dataSourceFormDs } = props;
  const [uuidKey, setUuid] = useState(1);
  const [slaveCards, setSlaveCards] = useState([0]);
  const [tabKey, setTabKey] = useState('');

  React.useEffect(() => {
    if (!isEdit) {
      drawerDs.current.set('dataType', 'single');
      setTabKey('single');
    } else {
      queryData();
    }
  }, []);

  // 查询数据
  const queryData = () => {
    try {
      const { datasourceId } = currentEditData;
      drawerDs.setQueryParameter('datasourceId', datasourceId);
      drawerDs.query().then((res) => {
        if (res) {
          const { masterDatasource = {}, slaveDatasource } = res;
          masterDatasource.isRequired = true;
          if (!isEmpty(slaveDatasource)) {
            slaveDatasource[0].isRequired = true;
          }
          const dataType = isEmpty(slaveDatasource) ? 'single' : 'masterSlave';
          drawerDs.current.set('dataType', dataType);
          drawerDs.current.set('isRequired', dataType === 'masterSlave');
          setTabKey(dataType);
          const slaveList = isEmpty(slaveDatasource) ? [] : slaveDatasource;
          const data = [masterDatasource, ...slaveList];
          dataSourceFormDs.loadData(data);
          setUuid(uuidKey + 1);
        }
      });
    } catch (e) {
      //
    }
  };

  // 添加卡片
  const handleAdd = () => {
    setSlaveCards([...slaveCards, uuidKey]);
    setUuid(uuidKey + 1);
    dataSourceFormDs.create({});
  };

  // 删除卡片
  const handleDelete = (record) => {
    dataSourceFormDs.remove(record);
    setUuid(uuidKey - 1);
  };

  // 渲染表单节点
  const renderFormDom = (record) => {
    return (
      <Form record={record}>
        <TextField name="url" />
        <TextField name="username" />
        <Password name="password" />
        <NumberField name="connectionTimeoutMilliseconds" />
        <NumberField name="idleTimeoutMilliseconds" />
        <NumberField name="maxLifetimeMilliseconds" />
        <NumberField name="maxPoolSize" />
      </Form>
    );
  };

  // 渲染卡片
  const renderCards = () => {
    return (
      <>
        {dataSourceFormDs.data.map((item, index) => {
          return (
            <Card
              style={{ marginBottom: 15 }}
              key={uuid()}
              title={
                index
                  ? intl.get('hadm.LogicalDataSource.view.title.slaveDataSource').d('从数据源')
                  : intl.get('hadm.LogicalDataSource.view.title.masterDataSource').d('主数据源')
              }
              extra={
                index > 1 ? (
                  <>
                    <Tooltip title={intl.get('hzero.common.button.delete').d('删除')}>
                      <Popconfirm
                        title={intl
                          .get('hzero.common.message.confirm.delete')
                          .d('是否删除此条记录？')}
                        onConfirm={() => handleDelete(item)}
                      >
                        <Icon style={{ fontSize: 20, cursor: 'pointer' }} type="close" />
                      </Popconfirm>
                    </Tooltip>
                  </>
                ) : (
                  ''
                )
              }
            >
              {renderFormDom(item)}
            </Card>
          );
        })}
      </>
    );
  };

  // 变化时触发
  const selectChange = (value) => {
    setTabKey(value);
    drawerDs.current.set('isRequired', value === 'masterSlave');
    if (dataSourceFormDs.data[1]) {
      dataSourceFormDs.data[1].set('isRequired', value === 'masterSlave');
    } else if (value === 'masterSlave') {
      dataSourceFormDs.create({ isRequired: true });
    }
  };

  return (
    <>
      <Spin dataSet={drawerDs}>
        <Form dataSet={drawerDs}>
          <SelectBox name="dataType" onChange={selectChange}>
            <Option value="single">
              {intl.get('hadm.LogicalDataSource.view.title.singleDataSource').d('单数据源')}
            </Option>
            <Option value="masterSlave">
              {intl.get('hadm.LogicalDataSource.view.title.masterSlaveData').d('主从数据源')}
            </Option>
          </SelectBox>
        </Form>
      </Spin>
      <Tabs activeKey={tabKey}>
        <TabPane
          tab={intl.get('hadm.LogicalDataSource.view.title.singleDataSource').d('单数据源')}
          key="single"
          disabled={tabKey === 'masterSlave'}
        >
          <Form record={dataSourceFormDs.data[0]}>
            <TextField name="url" />
            <TextField name="username" />
            <Password name="password" />
            <NumberField name="connectionTimeoutMilliseconds" />
            <NumberField name="idleTimeoutMilliseconds" />
            <NumberField name="maxLifetimeMilliseconds" />
            <NumberField name="maxPoolSize" />
          </Form>
        </TabPane>
        <TabPane
          tab={intl.get('hadm.LogicalDataSource.view.title.masterSlaveData').d('主从数据源')}
          key="masterSlave"
          disabled={tabKey === 'single'}
        >
          <Form dataSet={drawerDs}>
            <Select name="loadBalanceAlgorithmType" />
          </Form>
          {uuidKey && renderCards()}
          <Card style={{ marginBottom: 20, cursor: 'pointer' }} onClick={handleAdd}>
            <Icon type="add" style={{ fontSize: 150, marginLeft: '30%', color: 'grey' }} />
          </Card>
        </TabPane>
      </Tabs>
    </>
  );
};

export default DataSourceDrawer;
