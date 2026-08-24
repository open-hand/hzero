import React, { useState, useEffect, useMemo } from 'react';
import {
  Form,
  Spin,
  Lov,
  Table,
  Modal,
  DataSet,
  TextField,
  NumberField,
  Button,
  Select,
  ModalContainer,
} from 'choerodon-ui/pro';
import { Card, Divider } from 'choerodon-ui';
import { message } from 'hzero-ui';
import axios from 'axios';
import copy from 'copy-to-clipboard';
import { isEmpty } from 'lodash';

import { Content, Header } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';

import intl from 'utils/intl';
import { operatorRender } from 'utils/renderer';
import { HZERO_ADM } from 'utils/config';
import notification from 'utils/notification';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';
import formatterCollections from 'utils/intl/formatterCollections';

import Drawer from './Drawer';
import styles from './index.less';

import {
  detailDS,
  detailTableDS,
  createConfigDS,
  useConfigDS,
  useProxyDS,
  bindDS,
  addBroadcastDS,
  removeBroadcastDS,
} from '../../../stores/ruleConfigDS';

const TargetDrawer = (props) => {
  // const [count] = useState([1,2,3,4,5,6,7,8,9]);
  const detailTableDs = useMemo(
    () =>
      new DataSet({
        ...detailTableDS(),
        fields: [...detailTableDS().fields],
        events: {
          select: ({ dataSet }) => {
            setDisabledFlag(
              dataSet.updated.concat(dataSet.created).length > 0 || dataSet.selected.length > 1
            );
          },
          unSelect: ({ dataSet }) => {
            setDisabledFlag(
              dataSet.updated.concat(dataSet.created).length > 0 || dataSet.selected.length !== 1
            );
          },
        },
      }),
    []
  );
  const createConfigDs = useMemo(() => new DataSet(createConfigDS), []);
  const useConfigDs = useMemo(() => new DataSet(useConfigDS()), []);
  const useProxyDs = useMemo(() => new DataSet(useProxyDS), []);
  const bindDs = useMemo(() => new DataSet(bindDS), []);
  const addBroadcastDs = useMemo(() => new DataSet(addBroadcastDS), []);
  const removeBroadcastDs = useMemo(() => new DataSet(removeBroadcastDS), []);
  const detailDs = useMemo(() => new DataSet(detailDS()), []);

  const [isCreate, setIsCreate] = useState(null);
  const [disabledFlag, setDisabledFlag] = useState(true);
  const [, setLibraryDisabled] = useState(true);
  const [, setTableDisabled] = useState(true);
  const [numLibraryFlag, setNumLibraryFlag] = useState(false);
  const [numTableFlag, setNumTableFlag] = useState(false);

  const {
    match: {
      path,
      params: { id },
    },
  } = props;

  useEffect(() => {
    if (id === 'create') {
      detailDs.create({});
    } else {
      detailDs.ruleHeaderId = id;
      detailTableDs.ruleHeaderId = id;
      detailTableDs.setQueryParameter('ruleHeaderId', id);
      handleFetch();
    }
  }, [isCreate]);

  // 查询详情
  const handleFetch = async () => {
    await detailDs.query();

    if (detailDs.current) {
      const subLibraryFieldArray = isEmpty(detailDs.current.get('dbShardingColumn'))
        ? null
        : detailDs.current.get('dbShardingColumn')[0].split(',');
      const subTableFieldArrqy = isEmpty(detailDs.current.get('tableShardingColumn'))
        ? null
        : detailDs.current.get('tableShardingColumn')[0].split(',');
      detailDs.current.set('dbShardingColumn', subLibraryFieldArray);
      detailDs.current.set('tableShardingColumn', subTableFieldArrqy);
      const subLibraryField = !isEmpty(detailDs.current.get('dbShardingColumn')) ? '2' : '1';
      const subTableField = !isEmpty(detailDs.current.get('tableShardingColumn')) ? '1' : '2';
      const type = detailDs.current.get('type') === '3' ? '' : detailDs.current.get('type');
      detailDs.current.set(
        'type',
        type || (subLibraryField === subTableField ? subLibraryField : '3')
      );
      const subTableRuleType = detailDs.current.get('tableShardingType');
      const subLibraryRuleType = detailDs.current.get('dbShardingType');
      if (subLibraryRuleType) {
        detailDs.current.getField('dbShardingTemplate').set('lookupCode', subLibraryRuleType);
      }
      if (subTableRuleType) {
        detailDs.current.getField('tableShardingTemplate').set('lookupCode', subTableRuleType);
      }
      if (subLibraryRuleType && subLibraryRuleType !== 'HADM.SHARDING_RULE_NONE') {
        setLibraryDisabled(false);
      }
      if (subTableRuleType && subTableRuleType !== 'HADM.SHARDING_RULE_NONE') {
        setTableDisabled(false);
      }
      setNumLibraryFlag(subLibraryRuleType === 'HADM.SHARDING_RULE_INLINE');
      setNumTableFlag(subTableRuleType === 'HADM.SHARDING_RULE_INLINE');
      detailTableDs.unSelectAll();
      await detailTableDs.query();
      setDisabledFlag(true);
      detailDs.getField('bind').setLovPara('ruleHeaderId', id);
    }
  };

  const handleChangeSubLibraryRuleType = (value) => {
    detailDs.current.set('dbShardingTemplate', '');
    setNumLibraryFlag(false);
    setLibraryDisabled(false);
    if (value === 'HADM.SHARDING_RULE_NONE' || !value) {
      detailDs.current.getField('dbShardingTemplate').set('lookupCode', value);
      setLibraryDisabled(true);
    } else if (value === 'HADM.SHARDING_RULE_INLINE') {
      setNumLibraryFlag(true);
      detailDs.current.getField('dbShardingTemplate').set('lookupCode', value);
    } else if (value) {
      detailDs.current.getField('dbShardingTemplate').set('lookupCode', value);
    }
  };

  const handleChangeSubTableRuleType = (value) => {
    detailDs.current.set('tableShardingTemplate', '');
    setNumTableFlag(false);
    setTableDisabled(false);
    if (value === 'HADM.SHARDING_RULE_NONE' || !value) {
      setTableDisabled(true);
      detailDs.current.getField('tableShardingTemplate').set('lookupCode', value);
    } else if (value === 'HADM.SHARDING_RULE_INLINE') {
      setNumTableFlag(true);
      detailDs.current.getField('tableShardingTemplate').set('lookupCode', value);
    } else if (value) {
      detailDs.current.getField('tableShardingTemplate').set('lookupCode', value);
    }
  };

  const columns = useMemo(
    () => [
      { name: 'tableName' },
      { name: 'actualDataNodes', tooltip: 'overflow' },
      { name: 'dbShardingExp', tooltip: 'overflow' },
      { name: 'tableShardingExp', tooltip: 'overflow' },
      { name: 'keyGeneratorExp', tooltip: 'overflow' },
      { name: 'bindingTables', tooltip: 'overflow' },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 120,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '规则配置详情-编辑',
                  },
                ]}
                onClick={() => {
                  handleUpdate(record, false);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          });
          operators.push({
            key: 'delete',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.delete`,
                    type: 'button',
                    meaning: '规则配置详情-删除',
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
          return operatorRender(operators, record, { limit: 2 });
        },
        lock: 'right',
      },
    ],
    []
  );

  // 删除用户
  const handleDelete = async (record) => {
    if (record.get('ruleLineId') && id) {
      await detailTableDs.delete(record);
      await detailTableDs.query();
    } else {
      detailTableDs.remove(record);
    }
  };

  // 保存基本信息
  const handleSave = async () => {
    const spRuleLines = detailTableDs.updated
      .concat(detailTableDs.created)
      .map((item) => item.toData());
    detailDs.spRuleLines = spRuleLines;
    try {
      const validate = await detailDs.validate();
      if (validate) {
        const data = detailDs.toData()[0];
        const {
          serviceObj,
          datasourceGroupObj,
          bindingTableArray,
          broadcastTableArray,
          ...others
        } = data;

        let tableShardingExp = '';
        let dbShardingExp = '';
        if (data.dbShardingType) {
          const prefix = data.dbShardingType
            .substring(data.dbShardingType.lastIndexOf('_') + 1)
            .toLowerCase();
          const dbShardingExpJson = (data.dbShardingTemplate || '')
            .replace('#[column]', data.dbShardingColumn)
            .replace('#[db]', data.datasourceGroupName)
            .replace('#[table]', '')
            .replace('#[shardNum]', data.dbShardingNum);
          if (prefix !== 'none') {
            dbShardingExp = `{"${prefix}":${dbShardingExpJson}}`;
          } else {
            dbShardingExp = `{"${prefix}":${dbShardingExpJson}}` || '{"none":{}}';
          }
        }
        if (data.tableShardingType) {
          const prefix2 = data.tableShardingType
            .substring(data.tableShardingType.lastIndexOf('_') + 1)
            .toLowerCase();

          const tableShardingExpJson = (data.tableShardingTemplate || '')
            .replace('#[column]', data.tableShardingColumn)
            .replace('#[db]', data.datasourceGroupName)
            .replace('#[table]', '')
            .replace('#[shardNum]', data.tableShardingNum);

          if (prefix2 !== 'none') {
            tableShardingExp = `{"${prefix2}":${tableShardingExpJson}}`;
          } else {
            tableShardingExp = `{"${prefix2}":${tableShardingExpJson}}` || '{"none":{}}';
          }
        }

        const keyGeneratorExp = `{"type":"${data.keyGeneratorStrategy}","column":"${data.keyGeneratorField}"}`;

        axios({
          url: `${
            isTenantRoleLevel()
              ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
              : `${HZERO_ADM}/v1`
          }/sp-rule-headers/save`,
          method: 'POST',
          data: {
            ...others,
            dbShardingColumn: others.dbShardingColumn.join(','),
            tableShardingColumn: others.tableShardingColumn.join(','),
            spRuleLines,
            keyGeneratorExp,
            dbShardingExp,
            tableShardingExp,
          },
        })
          .then((res) => {
            if (res) {
              if (id === 'create') {
                notification.success();
                props.history.push(`/hadm/rule-config/detail/${res.ruleHeaderId}`);
                setIsCreate(false);
                setDisabledFlag(false);
              } else {
                notification.success();
                handleFetch();
              }
            }
          })
          .catch((err) => {
            if (err.response) {
              let m = require('hzero-front/lib/assets/icon_page_wrong.svg');
              if (m.__esModule) {
                m = m.default;
              }

              notification.error({
                icon: <></>,
                message: (
                  <>
                    <img src={m} alt="" className="ant-notification-notice-message-img" />
                    <div className="ant-notification-notice-message-content">
                      {intl.get(`hzero.common.requestNotification.${err.response.status}`) ||
                        err.message}
                    </div>
                  </>
                ),
                className: 'request error',
              });
            } else {
              notification.error({
                message: err.message,
              });
            }
          });
      }
    } catch (e) {
      console.log(e);
    }
  };

  const handleUpdate = (record, flag) => {
    if (flag) {
      detailTableDs.create({}, 0);
    }
    const drawerProps = {
      isCreate: flag,
      ds: flag ? detailTableDs.current : record,
    };
    const data = detailTableDs.current.toData();
    const subLibraryField = !isEmpty(drawerProps.ds.get('subLibraryField')) ? '2' : '1';
    const subTableField = !isEmpty(drawerProps.ds.get('subTableField')) ? '1' : '2';
    const subLibraryFieldArray = isEmpty(drawerProps.ds.get('subLibraryField'))
      ? null
      : drawerProps.ds.get('subLibraryField')[0].split(',');
    const subTableFieldArrqy = isEmpty(drawerProps.ds.get('subTableField'))
      ? null
      : drawerProps.ds.get('subTableField')[0].split(',');
    drawerProps.ds.set('subLibraryField', subLibraryFieldArray);
    drawerProps.ds.set('subTableField', subTableFieldArrqy);
    const type = drawerProps.ds.get('type') === '3' ? '' : drawerProps.ds.get('type');
    drawerProps.ds.set('type', type || (subLibraryField === subTableField ? subLibraryField : '3'));
    drawerProps.ds.set('dsGroupNames', detailDs.current.get('datasourceGroupName'));
    Modal.open({
      closable: false,
      key: 'update',
      width: 1000,
      title: flag
        ? intl.get('hzero.common.view.title.create').d('新建')
        : intl.get('hzero.common.view.title.edit').d('编辑'),
      drawer: true,
      children: <Drawer {...drawerProps} />,
      onOk: async () => {
        const drawerFlag = await detailTableDs.current.validate('all');
        if (drawerFlag) {
          const NewData = detailTableDs.current.toData();
          let tableShardingExp = '';
          let dbShardingExp = '';
          if (NewData.subLibraryRuleType) {
            const prefix = NewData.subLibraryRuleType
              ? NewData.subLibraryRuleType
                  .substring(NewData.subLibraryRuleType.lastIndexOf('_') + 1)
                  .toLowerCase()
              : '';
            const dbShardingExpJson = (NewData.dbShardingTemplate || '')
              .replace('#[column]', NewData.subLibraryField)
              .replace('#[db]', NewData.dsGroupNames)
              .replace('#[table]', NewData.tableName)
              .replace('#[shardNum]', NewData.subLibraryNum);
            if (prefix !== 'none') {
              dbShardingExp = NewData.type === '1' ? '' : `{"${prefix}":${dbShardingExpJson}}`;
            } else {
              dbShardingExp = '{"none":{}}';
            }
          }
          if (NewData.subTableRuleType) {
            const prefix2 = NewData.subTableRuleType
              ? NewData.subTableRuleType
                  .substring(NewData.subTableRuleType.lastIndexOf('_') + 1)
                  .toLowerCase()
              : '';
            const tableShardingExpJson = (NewData.tableShardingTemplate || '')
              .replace('#[column]', NewData.subTableField)
              .replace('#[db]', NewData.dsGroupNames)
              .replace('#[table]', NewData.tableName)
              .replace('#[shardNum]', NewData.subTableNum);
            if (prefix2 !== 'none') {
              tableShardingExp =
                NewData.type === '2' ? '' : `{"${prefix2}":${tableShardingExpJson}}`;
            } else {
              tableShardingExp = '{"none":{}}';
            }
          }

          const actualDataNodesPrefix =
            // eslint-disable-next-line no-nested-ternary
            NewData.type === '1'
              ? ''
              : NewData.subLibraryNum === 1
              ? '_0'
              : `_\${0..${NewData.subLibraryNum - 1}}`;
          const actualDataNodesSuffix =
            // eslint-disable-next-line no-nested-ternary
            NewData.type === '2'
              ? `.${NewData.tableName}`
              : NewData.subTableNum === 1
              ? `.${NewData.tableName}_0`
              : `.${NewData.tableName}_\${0..${NewData.subTableNum - 1}}`;
          const actualDataNodes = `${NewData.dsGroupNames}${actualDataNodesPrefix}${actualDataNodesSuffix}`;
          const keyGeneratorExp = `{"type":"${NewData.keyGeneratorStrategy}","column":"${NewData.keyGeneratorField}"}`;

          detailTableDs.current.set('actualDataNodes', actualDataNodes);
          detailTableDs.current.set('dbShardingExp', dbShardingExp);
          detailTableDs.current.set('tableShardingExp', tableShardingExp);
          detailTableDs.current.set('keyGeneratorExp', keyGeneratorExp);
          detailTableDs.current.selectable = false;
          setDisabledFlag(true);
        }
        return drawerFlag;
      },
      onCancel: async () => {
        Object.keys(data).forEach((key) => {
          detailTableDs.current.set(key, data[key]);
        });
        handleCancel(flag);
      },
    });
  };

  const handleCancel = (flag) => {
    if (flag) {
      detailTableDs.shift();
    }
  };

  const handleCopy = (res) => {
    copy(res);
    if (copy(res)) {
      message.success(
        <span>{intl.get('hadm.ruleConfig.view.message.copy').d('复制成功')}</span>,
        'top'
      );
    } else {
      message.error(
        <span>{intl.get('hadm.ruleConfig.view.message.copyFail').d('复制失败')}</span>,
        'top'
      );
    }
  };

  const handleAddBroadCast = async () => {
    addBroadcastDs.setQueryParameter('ruleHeaderId', id);
    addBroadcastDs.loadData(detailTableDs.selected.map((item) => item.toData()));
    await addBroadcastDs.query();
    handleFetch();
  };

  const handleRemoveBroadCast = async () => {
    removeBroadcastDs.setQueryParameter('ruleHeaderId', id);
    removeBroadcastDs.loadData(detailTableDs.selected.map((item) => item.toData()));
    await removeBroadcastDs.query();
    handleFetch();
  };

  const handleProxy = (type) => {
    if (detailTableDs.updated.concat(detailTableDs.created).length === 0) {
      switch (type) {
        case 'createConfig':
          createConfigDs.setQueryParameter('ruleHeaderId', id);
          createConfigDs.query().then((res) => {
            Modal.info({
              title: intl
                .get('hadm.ruleConfig.view.message.shardingProxyConfig')
                .d('sharding-proxy配置'),
              style: { width: 500 },
              children: (
                <>
                  <Button
                    icon="content_copy"
                    onClick={() => {
                      handleCopy(res);
                    }}
                    color="primary"
                  >
                    {intl.get('hzero.common.button.copy').d('复制')}
                  </Button>
                  <pre style={{ 'white-space': 'pre-wrap', height: '300px', minWidth: '450px' }}>
                    {`${res}`}
                  </pre>
                </>
              ),
            });
          });
          break;

        case 'useConfig':
          useConfigDs.ruleHeaderId = id;
          if (detailDs.current) {
            const proxyDsUrl = detailDs.current && detailDs.current.get('proxyDsUrl');
            useConfigDs.create({
              proxyUiIp:
                proxyDsUrl &&
                proxyDsUrl.match(/(\w+):\/\/([^/:]+)(:\d*)?([^# ]*)/) &&
                proxyDsUrl.match(/(\w+):\/\/([^/:]+)(:\d*)?([^# ]*)/)[2],
            });
          } else {
            useConfigDs.create({});
          }
          Modal.open({
            closable: false,
            key: 'useConfig',
            title: intl.get('hadm.ruleConfig.view.message.useConfig').d('应用proxy配置'),
            drawer: false,
            children: (
              <Form dataSet={useConfigDs} labelWidth={150}>
                <TextField name="proxyUiIp" />
                <TextField name="proxyUiPort" restrict="0-999999" />
                <TextField name="username" />
                <TextField name="password" />
              </Form>
            ),
            onOk: async () => {
              const flag = await useConfigDs.submit();
              if (flag) {
                handleFetch();
              }
              return !!flag;
            },
            onCancel: () => {
              useConfigDs.reset();
            },
            onClose: () => {
              useConfigDs.reset();
            },
          });

          break;
        default:
          useProxyDs.setQueryParameter('ruleHeaderId', id);
          useProxyDs.query().then(() => {
            handleFetch();
          });
      }
    } else {
      Modal.info({
        children: intl.get('hadm.ruleConfig.view.message.confirmSave').d('请先保存数据'),
      });
    }
  };

  return (
    <>
      <Header
        backPath="/hadm/rule-config/list"
        title={intl.get('hadm.ruleConfig.view.title.detail').d('proxy规则配置详情')}
      >
        <ButtonPermission
          type="c7n-pro"
          permissionList={[
            {
              code: `${path}-save`,
              type: 'button',
              meaning: '规则配置详情-保存',
            },
          ]}
          icon="save"
          color="primary"
          onClick={handleSave}
        >
          {intl.get('hzero.common.button.save').d('保存')}
        </ButtonPermission>
      </Header>
      <Content className={styles['head-form']}>
        <Spin dataSet={detailDs}>
          <Card bordered={false} title={intl.get('hadm.common.title.baseInfo').d('基本信息')}>
            <Form dataSet={detailDs} columns={2} labelWidth={150}>
              <Lov name="datasourceGroupObj" />
              <TextField name="proxySchemaName" maxLength={60} />
              {id !== 'create' && <TextField name="broadcastTableArray" clearButton={false} />}
            </Form>
            <Divider>
              {intl.get('hadm.ruleConfig.model.ruleConfig.dbTableSharding').d('分表分库')}
            </Divider>
            <Form dataSet={detailDs} columns={2} labelWidth={150}>
              <Select name="dbShardingType" onChange={handleChangeSubLibraryRuleType} />
              {detailDs.current &&
                detailDs.current.get('dbShardingType') !== 'HADM.SHARDING_RULE_NONE' &&
                detailDs.current.get('dbShardingType') &&
                numLibraryFlag && <NumberField name="dbShardingNum" />}
              {detailDs.current &&
                detailDs.current.get('dbShardingType') !== 'HADM.SHARDING_RULE_NONE' &&
                detailDs.current.get('dbShardingType') && <TextField name="dbShardingColumn" />}
              <Select name="dbShardingTemplate" />
            </Form>

            <Form dataSet={detailDs} columns={2} labelWidth={150}>
              <Select name="tableShardingType" onChange={handleChangeSubTableRuleType} />
              {detailDs.current &&
                detailDs.current.get('tableShardingType') !== 'HADM.SHARDING_RULE_NONE' &&
                detailDs.current.get('tableShardingType') &&
                numTableFlag && <NumberField name="tableShardingNum" />}
              {detailDs.current &&
                detailDs.current.get('tableShardingType') !== 'HADM.SHARDING_RULE_NONE' &&
                detailDs.current.get('tableShardingType') && (
                  <TextField name="tableShardingColumn" />
                )}
              <Select name="tableShardingTemplate" />
            </Form>
            <Divider>
              {intl.get('hadm.ruleConfig.model.ruleConfig.keyGeneratorExp').d('主键策略')}
            </Divider>
            <Form dataSet={detailDs} columns={2} labelWidth={150}>
              <Select name="keyGeneratorStrategy" />
              <TextField name="keyGeneratorField" maxLength={50} />
            </Form>
          </Card>
          <Card
            bordered={false}
            title={intl.get('hadm.ruleConfig.view.title.userList').d('行信息')}
            extra={
              <div className={styles['lov-button']}>
                <Lov
                  name="bind"
                  mode="button"
                  icon="relate"
                  placeholder={intl.get('hadm.ruleConfig.view.button.bind').d('绑定')}
                  onChange={(e) => {
                    bindDs.loadData(e);
                    bindDs.ruleLineId = detailTableDs.current.get('ruleLineId');
                    bindDs.query().then(() => {
                      notification.success();
                      handleFetch();
                    });
                    detailDs.current.set('bind', null);
                  }}
                  dataSet={detailDs}
                  disabled={disabledFlag}
                  clearButton={false}
                />
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}-addBroadcast`,
                      type: 'button',
                      meaning: '规则配置详情-添加广播表',
                    },
                  ]}
                  icon="radio-o"
                  onClick={handleAddBroadCast}
                  disabled={detailTableDs.selected.length === 0}
                >
                  {intl.get('hadm.ruleConfig.view.message.addBroadcast').d('添加广播表')}
                </ButtonPermission>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}-removeBroadcast`,
                      type: 'button',
                      meaning: '规则配置详情-移除广播表',
                    },
                  ]}
                  icon="remove_circle"
                  onClick={handleRemoveBroadCast}
                  disabled={detailTableDs.selected.length === 0}
                >
                  {intl.get('hadm.ruleConfig.view.message.removeBroadcast').d('移除广播表')}
                </ButtonPermission>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}-addUser`,
                      type: 'button',
                      meaning: '规则配置详情-生成proxy配置',
                    },
                  ]}
                  icon="settings-o"
                  onClick={() => {
                    handleProxy('createConfig');
                  }}
                >
                  {intl.get('hadm.ruleConfig.view.message.createConfig').d('生成proxy配置')}
                </ButtonPermission>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}-addUser`,
                      type: 'button',
                      meaning: '规则配置详情-应用proxy配置',
                    },
                  ]}
                  icon="application_-general"
                  onClick={() => {
                    handleProxy('useConfig');
                  }}
                >
                  {intl.get('hadm.ruleConfig.view.message.useConfig').d('应用proxy配置')}
                </ButtonPermission>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}-addUser`,
                      type: 'button',
                      meaning: '规则配置详情-新建',
                    },
                  ]}
                  icon="add"
                  onClick={() => {
                    handleUpdate({}, true);
                  }}
                >
                  {intl.get('hzero.common.button.add').d('新增')}
                </ButtonPermission>
              </div>
            }
          >
            <Table dataSet={detailTableDs} columns={columns} />
          </Card>

          <ModalContainer location={props.location} />
        </Spin>
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['hadm.ruleConfig'] })(TargetDrawer);
