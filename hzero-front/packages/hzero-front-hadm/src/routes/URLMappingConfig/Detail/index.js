import React, { useState, useEffect, useMemo } from 'react';
import {
  Form,
  Spin,
  Lov,
  Table,
  Modal,
  Output,
  DataSet,
  TextField,
  ModalContainer,
} from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';

import { detailDS, tableDrawerDS, detailTableDS, addDS } from '../../../stores/urlMappingConfigDS';
import styles from './index.less';

const TargetDrawer = (props) => {
  const detailTableDs = useMemo(() => new DataSet(detailTableDS()), []);
  const tableDrawerDs = useMemo(() => new DataSet(tableDrawerDS()), []);
  const addDs = useMemo(() => new DataSet(addDS()), []);
  const detailDs = useMemo(
    () =>
      new DataSet({
        ...detailDS(),
        // children: {
        //   urlRuleUsers: detailTableDs,
        // },
      }),
    []
  );

  const [isCreate, setIsCreate] = useState(false);
  const [addEnable, setAddEnable] = useState(true);
  const [currentId, setCurrentId] = useState(undefined);
  const [deleteEnable, setDeleteEnable] = useState(true);

  const {
    match: {
      path,
      params: { id },
    },
  } = props;

  useEffect(() => {
    if (id === 'create') {
      setIsCreate(true);
      detailDs.create({});
    } else {
      detailDs.urlRuleId = id;
      detailTableDs.urlRuleId = id;
      handleFetch();
    }
  }, []);

  // 查询详情
  const handleFetch = async () => {
    await detailDs.query();
    if (detailDs.current) {
      const sourceTenantId = detailDs.current.get('sourceTenantId');
      setAddEnable(!sourceTenantId);
      setCurrentId(sourceTenantId);
      detailTableDs.sourceTenantId = sourceTenantId;
      await detailTableDs.query();
      setDeleteEnable(isEmpty(detailTableDs.toData()));
    }
  };

  const columns = useMemo(
    () => [
      { name: 'realName' },
      { name: 'loginName' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 220,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
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
          });
          return operatorRender(operators, record, { limit: 1 });
        },
        lock: 'right',
      },
    ],
    []
  );

  const userColumns = useMemo(() => [{ name: 'realName' }, { name: 'loginName' }], []);

  const handleChange = (e, type) => {
    switch (type) {
      case 'sourceService': {
        const data = detailDs.toData();
        const url = `${data[0].sourceService ? data[0].sourceServiceCode : ''}${
          data[0].sourceUrl || ''
        }`;
        detailDs.current.set('fullSourceUrl', url);
        break;
      }
      case 'sourceUrl': {
        const data = detailDs.toData();
        const url = `${data[0].sourceService ? data[0].sourceServiceCode : ''}${
          data[0].sourceUrl || ''
        }`;
        detailDs.current.set('fullSourceUrl', url);
        break;
      }
      case 'targetService': {
        const data = detailDs.toData();
        const url = `${data[0].targetService ? data[0].targetServiceCode : ''}${
          data[0].targetUrl || ''
        }`;
        detailDs.current.set('fullTargetUrl', url);
        break;
      }
      case 'targetUrl': {
        const data = detailDs.toData();
        const url = `${data[0].targetService ? data[0].targetServiceCode : ''}${
          data[0].targetUrl || ''
        }`;
        detailDs.current.set('fullTargetUrl', url);
        break;
      }
      default:
        break;
    }
  };

  // 保存用户
  const handleOk = async () => {
    const selectList = tableDrawerDs.selected.map((record) => record.toData());
    addDs.urlRuleId = id;
    addDs.create({ sourceTenant: selectList }, 0);
    await addDs.submit();
    await detailTableDs.query();
    setDeleteEnable(isEmpty(detailTableDs.toData()));
  };

  // 删除用户
  const handleDelete = async (record) => {
    await detailTableDs.delete(record);
    await detailTableDs.query();
    setDeleteEnable(isEmpty(detailTableDs.toData()));
  };

  const handleBatchDetele = async () => {
    await detailTableDs.delete(detailTableDs.selected);
    await detailTableDs.query();
    setDeleteEnable(isEmpty(detailTableDs.toData()));
  };

  // 保存基本信息
  const handleSave = async () => {
    const validate = await detailDs.submit();
    if (!validate) {
      return false;
    } else if (isCreate) {
      props.history.push('/hadm/url-mapping-config/list');
    } else {
      const sourceTenantId = detailDs.current.get('sourceTenantId');
      setAddEnable(!sourceTenantId);
      setCurrentId(sourceTenantId);
      detailDs.query();
      await detailTableDs.query();
      setDeleteEnable(isEmpty(detailTableDs.toData()));
    }
  };

  const handleAddUser = async () => {
    tableDrawerDs.sourceTenantId = currentId;
    tableDrawerDs.urlRuleId = id;
    Modal.open({
      key: 'addUser',
      destroyOnClose: true,
      closable: true,
      // className: styles['url-mapping-modal'],
      title: intl.get('hadm.urlMappingConfig.button.urlMapping.addUser').d('新建用户'),
      children: <Table dataSet={tableDrawerDs} columns={userColumns} queryFieldsLimit={2} />,
      onOk: handleOk,
      onCancel: handleCancel,
      onClose: handleCancel,
    });
    await tableDrawerDs.query();
  };

  const handleCancel = () => {
    tableDrawerDs.selected.forEach((record) => {
      tableDrawerDs.unSelect(record);
    });
  };

  return (
    <>
      <Header
        backPath="/hadm/url-mapping-config/list"
        title={intl.get('hadm.urlMappingConfig.view.title.detail').d('URL管理详情')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-save`,
              type: 'button',
              meaning: 'URL映射配置-保存',
            },
          ]}
          icon="save"
          color="primary"
          onClick={handleSave}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
      </Header>
      <Content>
        <Spin dataSet={detailDs}>
          <Card bordered={false} title={intl.get('hadm.common.title.baseInfo').d('基本信息')}>
            <Form dataSet={detailDs} columns={3}>
              <TextField name="urlRuleCode" disabled={!isCreate} />
              <TextField name="description" />
              <Lov name="sourceTenant" />
              <Lov
                name="sourceService"
                onChange={(e) => {
                  handleChange(e, 'sourceService');
                }}
              />
              <TextField
                name="sourceUrl"
                onChange={(e) => {
                  handleChange(e, 'sourceUrl');
                }}
              />
              <Output name="fullSourceUrl" />
              <Lov
                name="targetService"
                onChange={(e) => {
                  handleChange(e, 'targetService');
                }}
              />
              <TextField
                name="targetUrl"
                onChange={(e) => {
                  handleChange(e, 'targetUrl');
                }}
              />
              <Output name="fullTargetUrl" />
            </Form>
          </Card>
          {!isCreate && (
            <Card
              bordered={false}
              title={intl.get('hadm.urlMappingConfig.view.title.userList').d('用户列表')}
              className={styles['url-mapping-config-card']}
              extra={
                <div>
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}-deleteUsr`,
                        type: 'button',
                        meaning: 'URL映射配置-批量删除',
                      },
                    ]}
                    icon="delete"
                    onClick={handleBatchDetele}
                    className={styles['add-button']}
                    disabled={deleteEnable}
                  >
                    {intl.get('hadm.urlMappingConfig.button.urlMapping.deleteUser').d('删除用户')}
                  </ButtonPermission>
                  <ButtonPermission
                    type="c7n-pro"
                    permissionList={[
                      {
                        code: `${path}-addUser`,
                        type: 'button',
                        meaning: 'URL映射配置-保存',
                      },
                    ]}
                    icon="add"
                    onClick={handleAddUser}
                    className={styles['add-button']}
                    disabled={addEnable}
                    color="primary"
                  >
                    {intl.get('hadm.urlMappingConfig.button.urlMapping.addUser').d('新建用户')}
                  </ButtonPermission>
                </div>
              }
            >
              <div className={styles['detail-table-wrap']}>
                <Table dataSet={detailTableDs} columns={columns} queryFieldsLimit={2} />
              </div>
            </Card>
          )}
          <ModalContainer location={props.location} />
        </Spin>
      </Content>
    </>
  );
};
export default formatterCollections({ code: ['hadm.urlMappingConfig', 'hadm.common'] })(
  TargetDrawer
);
