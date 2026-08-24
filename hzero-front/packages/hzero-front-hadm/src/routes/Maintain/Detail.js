/* eslint-disable no-nested-ternary */
/*
 * Maintain 在线运维
 * @date: 2020-06-2
 * @author XL <liang.xiong@hand-china.com>
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import {
  Table,
  DataSet,
  Form,
  TextField,
  ModalContainer,
  Button,
  Spin,
  Output,
  Modal,
} from 'choerodon-ui/pro';
import { Card, Tag } from 'choerodon-ui';
import axios from 'axios';

import { Button as ButtonPermission } from 'components/Permission';
import { Header, Content } from 'components/Page';
import intl from 'utils/intl';
import formatterCollections from 'utils/intl/formatterCollections';
import { DETAIL_CARD_CLASSNAME } from 'utils/constants';
import notification from 'utils/notification';
import { isTenantRoleLevel, getCurrentOrganizationId } from 'utils/utils';
import { HZERO_ADM } from 'utils/config';
import { downloadFile } from 'services/api';
import Upload from 'components/Upload/UploadButton';

import { detailDS as detailDs, detailTableDS as detailTableDs } from '../../stores/MaintainDS';
import Drawer from './Drawer';

const Detail = (props) => {
  const {
    match: {
      path,
      params: { maintainId },
    },
    history,
  } = props;

  let modal = '';

  const detailTableDS = React.useMemo(() => new DataSet(detailTableDs()), []);

  const detailDS = React.useMemo(() => new DataSet(detailDs()), []);

  const [loading, setLoading] = React.useState(false);

  const [state, setState] = React.useState('UNUSED');

  React.useEffect(() => {
    detailDS.setQueryParameter('maintainId', maintainId);
    detailTableDS.setQueryParameter('maintainId', maintainId);
    // 查询头后 设置状态
    if (maintainId !== 'create') {
      setLoading(true);
      detailDS.query().then((res) => {
        if (res) {
          setState(res.state);
        }
      });
      detailTableDS.query();
      setLoading(false);
    }
  }, []);

  const columns = React.useMemo(
    () => [
      { name: 'serviceLov', width: 150, editor: (record) => record.getState('editing') },
      { name: 'tableName', width: 150, editor: (record) => record.getState('editing') },
      {
        name: 'maintainMode',
        editor: (record) => record.getState('editing'),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 180,
        renderer: ({ record }) => commands(record),
        lock: 'right',
        align: 'center',
      },
    ],
    []
  );

  const commands = (record) => {
    const btns = [];
    if (record.getState('editing')) {
      btns.push(
        <a onClick={() => handleCancel(record)}>
          {intl.get('hzero.common.button.cancel').d('取消')}
        </a>
      );
    } else {
      btns.push(
        <a onClick={() => handleEdit(record)} disabled={record.status === 'delete'}>
          {intl.get('hzero.common.button.edit').d('编辑')}
        </a>
      );
    }
    btns.push(
      <a onClick={() => handleDelete(record)} disabled={record.status === 'delete'}>
        {intl.get('hzero.common.button.delete').d('删除')}
      </a>
    );
    return [<span className="action-link">{btns}</span>];
  };

  const buttons = [
    <Button icon="save" onClick={() => handleSaveLines()} key="save">
      {intl.get('hzero.common.button.save').d('保存')}
    </Button>,
    <Button icon="playlist_add" onClick={() => handleAdd()} key="add">
      {intl.get('hzero.common.button.add').d('新增')}
    </Button>,
    <Button icon="lock_outline" onClick={() => handleRead()} key="read">
      {intl.get('hadm.common.button.write').d('全选禁止写')}
    </Button>,
    <Button icon="lock" onClick={() => handleWrite()} key="write">
      {intl.get('hadm.common.button.read').d('全选禁止读写')}
    </Button>,
    <Button icon="lock_open" onClick={() => handleClearRead()} key="clearRead">
      {intl.get('hadm.maintain.button.clearRead').d('清空禁止写')}
    </Button>,
    <Button icon="unlock" onClick={() => handleClearWrite()} key="clearWrite">
      {intl.get('hadm.maintain.button.clearWrite').d('清空禁止读写')}
    </Button>,
  ];

  // 自定义行内 新建
  const handleAdd = () => {
    const record = detailTableDS.create({}, 0);
    record.setState('editing', true);
  };

  // 批量保存
  const handleSaveLines = async () => {
    const res = await detailTableDS.submit();
    if (res) {
      detailTableDS.query();
    }
  };

  // 全选禁止读， 禁止读写优先级高于禁止读
  const handleWrite = () => {
    detailTableDS.forEach((record) => {
      if (record.getState('editing') === true) {
        if (record.get('maintainMode').includes('READ')) {
          record.set('maintainMode', ['READ', 'WRITE']);
        } else {
          record.set('maintainMode', ['WRITE']);
        }
      }
    });
  };

  /**
   * 全选禁止读写
   */
  const handleRead = () => {
    detailTableDS.forEach((record) => {
      if (record.getState('editing') === true) {
        if (record.get('maintainMode').includes('WRITE')) {
          record.set('maintainMode', ['READ', 'WRITE']);
        } else {
          record.set('maintainMode', ['READ']);
        }
      }
    });
  };

  /**
   * 清空禁止写
   */
  const handleClearRead = () => {
    detailTableDS.forEach((record) => {
      if (record.getState('editing') === true) {
        const data = record.get('maintainMode');
        record.set(
          'maintainMode',
          data.filter((item) => item === 'WRITE')
        );
      }
    });
  };

  /**
   * 清空禁止读写
   */
  const handleClearWrite = () => {
    detailTableDS.forEach((record) => {
      if (record.getState('editing') === true) {
        const data = record.get('maintainMode');
        record.set(
          'maintainMode',
          data.filter((item) => item === 'READ')
        );
      }
    });
  };

  /**
   * 取消行内编辑状态
   * @param {*} record
   */
  const handleCancel = (record) => {
    if (record.status === 'add') {
      detailTableDS.remove(record);
    } else {
      record.reset();
      record.setState('editing', false);
    }
  };

  const handleEdit = (record) => {
    record.setState('editing', true);
  };

  const handleDelete = async (record) => {
    await detailTableDS.delete(record);
  };

  /**
   * 头保存
   */
  const handleSave = async () => {
    await detailDS.submit().then((res) => {
      if (res && res.content) {
        if (maintainId === 'create') {
          history.push(`/hadm/maintain/detail/${res.content[0].maintainId}`);
          detailDS.setQueryParameter('maintainId', res.content[0].maintainId);
          detailTableDS.setQueryParameter('maintainId', res.content[0].maintainId);
          detailDS.query();
        }
      }
    });
  };

  /**
   * 开始运维
   */
  const handleMaintain = async () => {
    /**
     * 更新运维状态
     */
    await axios({
      url: `${
        isTenantRoleLevel() ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}` : `${HZERO_ADM}/v1`
      }/maintains/state`,
      method: 'POST',
      params: {
        maintainId,
        from: state,
        to: 'ACTIVE',
      },
    })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      })
      .then((res) => {
        if (res) {
          detailDS.query().then((r) => {
            if (r) {
              setState(r.state);
            }
            axios({
              url: `${
                isTenantRoleLevel()
                  ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}`
                  : `${HZERO_ADM}/v1`
              }/maintains/services`,
              method: 'GET',
              params: {
                maintainId,
              },
            })
              .then((result) => {
                if (res) {
                  modal = Modal.open({
                    title: intl.get('hadm.maintain.view.message.title.startMaintain').d('开始运维'),
                    drawer: true,
                    closable: false,
                    width: 520,
                    children: (
                      <Drawer
                        service={result}
                        maintainId={maintainId}
                        isStop={false}
                        onHandleStartEnd={() => modal.close()}
                        onHandleClose={() => modal.close()}
                      />
                    ),
                    // onOk: () => true,
                    // onCancel: () => true,
                    footer: null,
                    onClose: () => true,
                  });
                }
              })
              .catch((err) => {
                notification.error({
                  message: err.message,
                });
              });
          });
          /**
           * 查询服务列表
           */
        }
      });
  };

  /**
   * 停止运维
   */
  const handleStopMaintain = async () => {
    /**
     * 查询服务列表
     */
    await axios({
      url: `${
        isTenantRoleLevel() ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}` : `${HZERO_ADM}/v1`
      }/maintains/services`,
      method: 'GET',
      params: {
        maintainId,
      },
    })
      .then((res) => {
        if (res) {
          modal = Modal.open({
            title: intl.get('hadm.maintain.view.message.title.stopMaintain').d('停止运维'),
            drawer: true,
            closable: false,
            width: 520,
            children: (
              <Drawer
                service={res}
                maintainId={maintainId}
                state={state}
                isStop
                onHandleStopEnd={handleOk}
                onHandleClose={handleOk}
              />
            ),
            // onOk: () => handleOk(),
            // onCancel: () => handleOk(),
            footer: null,
            // afterClose: () => handleOk(),
          });
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  };

  /**
   * 停止运维后，保存需要更新运维状态， 并更新到state
   */
  const handleOk = async () => {
    await axios({
      url: `${
        isTenantRoleLevel() ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}` : `${HZERO_ADM}/v1`
      }/maintains/state`,
      method: 'POST',
      params: {
        maintainId,
        from: state,
        to: 'USED',
      },
    }).catch((err) => {
      notification.error({
        message: err.message,
      });
    });
    await detailDS.query().then((res) => {
      if (res) {
        setState(res.state);
      }
    });
    modal.close();
    return true;
  };

  /**
   * 下载导入模版
   */
  const handleDownload = () => {
    const requestUrl = `${
      isTenantRoleLevel() ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}` : `${HZERO_ADM}/v1`
    }/maintain-tables/download-template`;
    downloadFile({ requestUrl, method: 'POST' });
  };

  /**
   * 导入表数据
   * @param {*} file
   */
  const beforeUpload = (file) => {
    setLoading(true);
    const data = new FormData();
    data.append('importData', file, file.name);
    axios({
      url: `${
        isTenantRoleLevel() ? `${HZERO_ADM}/v1/${getCurrentOrganizationId()}` : `${HZERO_ADM}/v1`
      }/maintain-tables/import-data`,
      method: 'POST',
      params: {
        maintainId,
      },
      data,
    })
      .then(() => {
        setLoading(false);
        notification.success();
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
        setLoading(false);
      });
  };

  const uploadProps = {
    accept: '.properties',
    beforeUpload,
    showUploadList: false,
    style: {
      margin: '0 8px',
    },
    bucketName: 'hadm',
    bucketDirectory: 'hadm01',
    disabled: state === 'ACTIVE',
  };

  /**
   * 运维状态的改变
   * USED -> ACTIVE
   * UNUSED -> ACTIVE
   * ACTIVE -> USED
   */
  return (
    <Spin spinning={loading}>
      <Header
        title={intl.get('hadm.maintain.view.message.title.maintain').d('在线运维')}
        backPath="/hadm/maintain"
      >
        <Spin dataSet={detailDS}>
          {maintainId !== 'create' && state !== 'ACTIVE' && (
            <>
              <ButtonPermission
                type="c7n-pro"
                permissionList={[
                  {
                    code: `${path}/download`,
                    type: 'button',
                    meaning: '在线运维-下载模版',
                  },
                ]}
                icon="cloud_download"
                onClick={handleDownload}
                loading={loading}
                disabled={state === 'ACTIVE'}
              >
                {intl.get('hadm.maintain.view.message.download').d('下载模版')}
              </ButtonPermission>
              <Upload {...uploadProps}>
                <ButtonPermission
                  type="c7n-pro"
                  permissionList={[
                    {
                      code: `${path}/upload`,
                      type: 'button',
                      meaning: '在线运维-导入表数据',
                    },
                  ]}
                  icon="cloud_upload"
                  loading={loading}
                  disabled={state === 'ACTIVE'}
                >
                  {intl.get('hadm.maintain.view.message.upload').d('导入表数据')}
                </ButtonPermission>
              </Upload>
            </>
          )}
          {maintainId !== 'create' && (state === 'UNUSED' || state === 'USED') && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/maintain`,
                  type: 'button',
                  meaning: '在线运维-开始运维',
                },
              ]}
              icon="settings"
              onClick={handleMaintain}
              loading={loading}
            >
              {intl.get('hadm.maintain.button.maintain').d('开始运维')}
            </ButtonPermission>
          )}
          {maintainId !== 'create' && state === 'ACTIVE' && (
            <ButtonPermission
              type="c7n-pro"
              permissionList={[
                {
                  code: `${path}/stop-maintain`,
                  type: 'button',
                  meaning: '在线运维-停止运维',
                },
              ]}
              icon="settings"
              onClick={handleStopMaintain}
              loading={loading}
            >
              {intl.get('hadm.maintain.button.stopMaintain').d('停止运维')}
            </ButtonPermission>
          )}
          <ButtonPermission
            type="c7n-pro"
            permissionList={[
              {
                code: `${path}/save`,
                type: 'button',
                meaning: '在线运维-保存',
              },
            ]}
            icon="save"
            color="primary"
            onClick={handleSave}
            loading={loading}
          >
            {intl.get('hzero.common.button.save').d('保存')}
          </ButtonPermission>
        </Spin>
      </Header>
      <Content>
        <Card
          bordered={false}
          className={DETAIL_CARD_CLASSNAME}
          title={<h3>{intl.get('hadm.common.title.baseInfo').d('基本信息')}</h3>}
        >
          <Form dataSet={detailDS} columns={3}>
            <TextField name="maintainVersion" />
            <TextField name="description" />
            {maintainId !== 'create' && (
              <Output
                name="state"
                renderer={({ value }) => {
                  if (value === 'UNUSED') {
                    return (
                      <Tag color="red">
                        {intl.get('hadm.maintain.view.message.tag.unused').d('未运维')}
                      </Tag>
                    );
                  } else if (value === 'ACTIVE') {
                    return (
                      <Tag color="green">
                        {intl.get('hadm.maintain.view.message.tag.active').d('运维中')}
                      </Tag>
                    );
                  } else {
                    return (
                      <Tag color="blue">
                        {intl.get('hadm.maintain.view.message.tag.use').d('已运维')}
                      </Tag>
                    );
                  }
                }}
              />
            )}
          </Form>
        </Card>
        {maintainId !== 'create' && (
          <Card
            bordered={false}
            className={DETAIL_CARD_CLASSNAME}
            title={<h3>{intl.get('hadm.maintain.view.message.title.service').d('服务')}</h3>}
          >
            <Table
              dataSet={detailTableDS}
              columns={columns}
              queryFieldsLimit={2}
              buttons={buttons}
            />
          </Card>
        )}
        <ModalContainer location={location} />
      </Content>
    </Spin>
  );
};

export default formatterCollections({ code: ['hadm.maintain'] })(Detail);
