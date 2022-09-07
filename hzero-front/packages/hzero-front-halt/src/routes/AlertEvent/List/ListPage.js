/**
 * 告警事件管理 - 列表页
 * @date: 2020-5-20
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useContext, useMemo } from 'react';
import { Badge, Icon, Tooltip } from 'choerodon-ui';
import { Form, Modal, Select, Table } from 'choerodon-ui/pro';
import intl from 'utils/intl';
import { Header, Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import formatterCollections from 'utils/intl/formatterCollections';
import { operatorRender } from 'utils/renderer';
import Store from '../stores';
import Drawer from '../Drawer';

const modalKey = Modal.key();

const ListPage = () => {
  const {
    alertEventDS,
    logDS,
    match: { path },
  } = useContext(Store);

  const columns = useMemo(
    () => [
      { name: 'alertCode', width: 200 },
      { name: 'alertName', width: 200 },
      {
        name: 'alertRangeCode',
        header: (
          <>
            {intl.get('halt.alertEvent.model.alertEvent.alertRangeCode').d('告警级别')}
            <Tooltip
              placement="top"
              title={intl
                .get('halt.alertEvent.view.message.alertRangeMsg')
                .d('展示本批次中的最高告警等级(正常 < 提示 < 警告 < 错误 < 高危)')}
            >
              <Icon
                type="info"
                style={{
                  marginBottom: '3px',
                  marginLeft: '2px',
                  fontSize: '15px',
                  color: '#958a8a',
                }}
              />
            </Tooltip>
          </>
        ),
        width: 110,
        align: 'left',
      },
      { name: 'dataSource' },
      { name: 'alertRouteType' },
      { name: 'alertTime', width: 160 },
      { name: 'processedTime', width: 160 },
      {
        name: 'alertResult',
        width: 100,
        align: 'left',
        renderer: ({ value }) => (
          <Badge
            status={value === '1' ? 'success' : 'error'}
            text={
              value === '1'
                ? intl.get('halt.alertEvent.view.message.success').d('成功')
                : intl.get('halt.alertEvent.view.message.failed').d('失败')
            }
          />
        ),
      },
      {
        header: intl.get('hzero.common.button.action').d('操作'),
        width: 80,
        renderer: ({ record }) => {
          const operators = [];
          operators.push({
            key: 'detail',
            ele: (
              <ButtonPermission
                type="text"
                permissionList={[
                  {
                    code: `${path}.button.detail`,
                    type: 'button',
                    meaning: '预警事件-详情',
                  },
                ]}
                onClick={() => {
                  handleDetail(record);
                }}
              >
                {intl.get('hzero.common.button.detail').d('详情')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.detail').d('详情'),
          });
          return operatorRender(operators);
        },
        lock: 'right',
      },
    ],
    []
  );

  // 查看详情
  const handleDetail = (record) => {
    const currentEditData = record && record.toData();
    Modal.open({
      drawer: true,
      key: 'detail',
      destroyOnClose: true,
      closable: true,
      style: { width: 650 },
      title: intl.get('halt.alertEvent.view.message.view.detail').d('查看详情'),
      children: <Drawer currentEditData={currentEditData} />,
      okCancel: false,
      okText: intl.get('hzero.common.button.close').d('关闭'),
    });
  };

  // 显示时间清理弹窗
  const handleClearLogs = () => {
    Modal.open({
      key: modalKey,
      destroyOnClose: true,
      closable: true,
      title: intl.get('halt.alertEvent.view.button.clearLogs').d('事件清理'),
      children: (
        <Form dataSet={logDS}>
          <Select name="clearType" />
        </Form>
      ),
      onOk: handleOk,
      onClose: () => {
        logDS.reset();
      },
    });
  };

  // 时间清理
  const handleOk = async () => {
    try {
      const validate = await logDS.submit();
      if (!validate) {
        return false;
      }
    } catch {
      return false;
    }
    alertEventDS.query();
  };

  return (
    <>
      <Header title={intl.get('halt.alertEvent.view.message.alertEvent').d('告警事件管理')}>
        <ButtonPermission
          icon="delete"
          type="c7n-pro"
          onClick={() => handleClearLogs()}
          permissionList={[
            {
              code: `${path}.button.clearLogs`,
              type: 'button',
              meaning: '预警事件-事件清理',
            },
          ]}
        >
          {intl.get('halt.alertEvent.view.button.clearLogs').d('事件清理')}
        </ButtonPermission>
      </Header>
      <Content>
        <Table queryFieldsLimit={3} dataSet={alertEventDS} columns={columns} />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['halt.common', 'halt.alertEvent'] })(ListPage);
