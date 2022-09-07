/**
 * 告警规则配置
 * @date: 2020-3-24
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useMemo } from 'react';
import { DataSet, Table, Modal, Tooltip } from 'choerodon-ui/pro';
import { Tag } from 'choerodon-ui';
import { isEmpty, unionWith } from 'lodash';
import intl from 'utils/intl';
import notification from 'utils/notification';
import { enableRender, operatorRender } from 'utils/renderer';
import formatterCollections from 'utils/intl/formatterCollections';
import { Content } from 'components/Page';
import { Button as ButtonPermission } from 'components/Permission';
import {
  alertRuleDS,
  targetDS,
  sourceDS,
  triggerTableDS,
  dataHandleTableDS,
  dataSetDS,
  paramTableDS,
  infrastructureDS,
  applicationDS,
  loggingDS,
} from '@/stores/AlertRuleDS';
import {
  startSchedule,
  stopSchedule,
  pauseSchedule,
  resumeSchedule,
} from '@/services/alertRuleService';
import { HeaderButtons } from './HeaderButtons';
import Drawer from './Drawer';
import SourceDrawer from './SourceDrawer';
import TargetDrawer from './TargetDrawer';
import styles from './index.less';

const modalKey = Modal.key();
let ruleModal;
let sourceModal;
let targetModal;

const AlertRule = ({ match: { path } }) => {
  const alertRuleDs = useMemo(() => new DataSet(alertRuleDS()), []);
  let triggerTagTableDs;
  let triggerCommentTableDs;
  let applicationDs;
  let infrastructureDs;
  let dataHandleTableDs;
  let dataSetDs;
  let paramTableDs;
  let loggingDs;

  const columns = useMemo(() => [
    { name: 'alertCode', width: 250 },
    { name: 'alertName', width: 250 },
    { name: 'alertLevel' },
    {
      name: 'alertSourceTypeMeaning',
      renderer: ({ record, value }) => {
        if (record.get('alertSourceTypeEnabledFlag') === 1) {
          return <span>{value}</span>;
        } else {
          return (
            <Tooltip
              title={intl
                .get('halt.alertRule.view.message.source.disabled')
                .d('该数据来源已被禁用')}
            >
              <span className={styles['source-disabled']}>{value}</span>
            </Tooltip>
          );
        }
      },
    },
    {
      name: 'alertTargetType',
      width: 280,
      align: 'left',
      renderer: ({ value }) => {
        if (value) {
          const tags = [];
          const values = value.split(',');
          values.forEach((item) => {
            const { tag, meaning } = alertRuleDs.getField('alertTargetType').getLookupData(item);
            tags.push(<Tag color={tag}>{meaning}</Tag>);
          });
          return tags;
        }
      },
    },
    {
      name: 'status',
      align: 'left',
      renderer: ({ value, record }) => {
        const { meaning } = record.getField('status').getLookupData(value);
        let color = 'color';
        switch (value) {
          case 'CREATE':
            color = 'grey';
            break;
          case 'STARTUP':
            color = '#87d068';
            break;
          case 'PAUSE':
            color = 'volcano';
            break;
          case 'STOP':
            color = 'red';
            break;
          default:
            return;
        }
        return <Tag color={color}>{meaning}</Tag>;
      },
    },
    { name: 'errorMsg' },
    { name: 'enabledFlag', width: 120, renderer: ({ value }) => enableRender(value) },
    {
      header: intl.get('hzero.common.button.action').d('操作'),
      width: 220,
      lock: 'right',
      renderer: ({ record }) => {
        const isDisabled = record.get('alertSourceTypeEnabledFlag') === 0;
        const operators = [];
        operators.push(
          {
            key: 'edit',
            ele: (
              <ButtonPermission
                type="text"
                disabled={isDisabled}
                permissionList={[
                  {
                    code: `${path}.button.edit`,
                    type: 'button',
                    meaning: '告警规则配置-编辑',
                  },
                ]}
                onClick={() => {
                  handleEdit(true);
                }}
              >
                {intl.get('hzero.common.button.edit').d('编辑')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('hzero.common.button.edit').d('编辑'),
          },
          {
            key: 'source',
            ele: (
              <ButtonPermission
                type="text"
                disabled={isDisabled}
                permissionList={[
                  {
                    code: `${path}.button.source`,
                    type: 'button',
                    meaning: '告警规则配置-来源',
                  },
                ]}
                onClick={() => {
                  handleSource(record);
                }}
              >
                {intl.get('halt.alertRule.view.button.source').d('来源')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('halt.alertRule.view.button.source').d('来源'),
          },
          {
            key: 'target',
            ele: (
              <ButtonPermission
                type="text"
                disabled={isDisabled}
                permissionList={[
                  {
                    code: `${path}.button.target`,
                    type: 'button',
                    meaning: '告警规则配置-目标',
                  },
                ]}
                onClick={() => {
                  handleTarget(record);
                }}
              >
                {intl.get('halt.alertRule.view.button.target').d('目标')}
              </ButtonPermission>
            ),
            len: 2,
            title: intl.get('halt.alertRule.view.button.target').d('目标'),
          }
        );
        if (record.get('schedulerButtonFlag') === 1 && !isDisabled) {
          const { status } = record.toData();
          const butList = [
            ['CREATE', 'STOP'].includes(status) && {
              key: 'start',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.start`,
                      type: 'button',
                      meaning: '预警配置-启动调度',
                    },
                  ]}
                  onClick={() => {
                    handleSchedule(record, 'start');
                  }}
                >
                  {intl.get('halt.alertRule.view.button.start').d('启动')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('halt.alertRule.view.button.start').d('启动'),
            },
            status === 'STARTUP' && {
              key: 'pause',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.pause`,
                      type: 'button',
                      meaning: '预警配置-暂停调度',
                    },
                  ]}
                  onClick={() => {
                    handleSchedule(record, 'pause');
                  }}
                >
                  {intl.get('halt.alertRule.view.button.pause').d('暂停')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('halt.alertRule.view.button.pause').d('暂停'),
            },
            status === 'PAUSE' && {
              key: 'recover',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.recover`,
                      type: 'button',
                      meaning: '预警配置-恢复调度',
                    },
                  ]}
                  onClick={() => {
                    handleSchedule(record, 'resume');
                  }}
                >
                  {intl.get('halt.alertRule.view.button.recover').d('恢复')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('halt.alertRule.view.button.recover').d('恢复'),
            },
            ['STARTUP', 'PAUSE'].includes(status) && {
              key: 'terminate',
              ele: (
                <ButtonPermission
                  type="text"
                  permissionList={[
                    {
                      code: `${path}.button.terminate`,
                      type: 'button',
                      meaning: '预警配置-终止调度',
                    },
                  ]}
                  onClick={() => {
                    handleSchedule(record, 'stop');
                  }}
                >
                  {intl.get('halt.alertRule.view.button.terminate').d('终止')}
                </ButtonPermission>
              ),
              len: 2,
              title: intl.get('halt.alertRule.view.button.terminate').d('终止'),
            },
          ].filter(Boolean);
          operators.push({
            key: 'dispatch',
            ele: operatorRender(butList, record, {
              limit: 0,
              label: intl.get('halt.alertRule.view.button.dispatch').d('调度'),
            }),
            len: 3,
            title: intl.get('halt.alertRule.view.button.dispatch').d('调度'),
          });
        }
        return operatorRender(operators, record, { limit: 4 });
      },
    },
  ]);

  // 启动/暂停/终止/恢复 调度
  const handleSchedule = async (record, type) => {
    const params = {
      alertId: record.get('alertId'),
    };
    await handleScheduleRequest(params, type).then((res) => {
      if (!isEmpty(res) && res.failed && res.message) {
        notification.error({
          message: res.message,
        });
        return false;
      }
      notification.success();
    });
    alertRuleDs.query();
  };

  // 发送调度请求
  const handleScheduleRequest = async (params, type) => {
    switch (type) {
      case 'start':
        return startSchedule(params);
      case 'stop':
        return stopSchedule(params);
      case 'pause':
        return pauseSchedule(params);
      case 'resume':
        return resumeSchedule(params);
      default:
        return false;
    }
  };

  /**
   * 侧滑更新
   * @param {object} component - 侧滑组件
   * @param {object} props - 组件属性
   */
  const handleUpdate = (component, props) => {
    switch (component) {
      case 'rule':
        ruleModal.update({ children: <Drawer {...props} /> });
        break;
      case 'target':
        targetModal.update({ children: <TargetDrawer {...props} /> });
        break;
      case 'source':
        sourceModal.update({ children: <SourceDrawer {...props} /> });
        break;
      default:
        break;
    }
  };

  /**
   * 弹出新建/编辑侧滑
   * @param {boolean} isEdit - 是否编辑
   */
  const handleEdit = (isEdit) => {
    if (!isEdit) {
      alertRuleDs.create({});
    }
    const title = isEdit
      ? intl.get('halt.alertRule.view.title.edit').d('编辑告警规则配置')
      : intl.get('halt.alertRule.view.title.create').d('新建告警规则配置');
    ruleModal = Modal.open({
      drawer: true,
      key: modalKey,
      destroyOnClose: true,
      closable: true,
      title,
      children: <Drawer isEdit={isEdit} drawerDs={alertRuleDs} />,
      onOk: handleOk,
      onClose: () => alertRuleDs.reset(),
    });
  };

  // 创建/修改告警规则
  const handleOk = async () => {
    const res = await alertRuleDs.submit();
    if (!isEmpty(res) && res.success) {
      alertRuleDs.query();
      return true;
    } else if (res === undefined) {
      notification.warning({
        message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
      });
      return false;
    } else if (res === false) {
      notification.error({
        message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
      });
      return false;
    } else {
      return false;
    }
  };

  /**
   * 显示来源侧滑
   * @param {object} record  - 行数据
   */
  const handleSource = (record) => {
    triggerTagTableDs = new DataSet(triggerTableDS());
    triggerCommentTableDs = new DataSet(triggerTableDS());
    applicationDs = new DataSet(applicationDS());
    infrastructureDs = new DataSet(infrastructureDS());
    dataHandleTableDs = new DataSet(dataHandleTableDS());
    dataSetDs = new DataSet(dataSetDS());
    paramTableDs = new DataSet(paramTableDS());
    loggingDs = new DataSet(loggingDS());
    const currentData = record && record.toData();
    const sourceDs = new DataSet(sourceDS(currentData.alertId));
    const sourceProps = {
      sourceDs,
      triggerTagTableDs,
      triggerCommentTableDs,
      applicationDs,
      dataHandleTableDs,
      dataSetDs,
      paramTableDs,
      infrastructureDs,
      loggingDs,
      alertId: currentData.alertId,
    };
    sourceModal = Modal.open({
      drawer: true,
      key: 'source',
      destroyOnClose: true,
      closable: true,
      style: { width: 750 },
      title: intl.get('halt.warnConfig.view.title.source').d('来源'),
      children: <SourceDrawer {...sourceProps} onUpdate={handleUpdate} />,
      onOk: () => handleSaveSource(sourceDs),
      onClose: () => {
        sourceDs.removeAll();
      },
    });
  };

  /**
   * 保存来源
   * @param {object} sourceDs - 数据源DS
   */
  const handleSaveSource = async (sourceDs) => {
    const alertSourceType = sourceDs.current.get('alertSourceType');
    const tagValidate = await triggerTagTableDs.validate();
    const commentValidate = await triggerCommentTableDs.validate();

    if (tagValidate && commentValidate) {
      const tagData = triggerTagTableDs.toJSONData();
      const commentData = triggerCommentTableDs.toJSONData();
      const alertAddonList = unionWith(tagData, commentData);
      sourceDs.current.set('alertAddonList', alertAddonList);
    } else {
      return false;
    }

    // 应用中间件
    if (alertSourceType === 'APPLICATION') {
      const applicationDsValidate = await applicationDs.validate();
      if (applicationDsValidate) {
        const alertRulePrometheus = applicationDs.current.toData();
        sourceDs.current.set('alertRulePrometheus', alertRulePrometheus);
      } else {
        return false;
      }
    }

    // 基础设施
    if (alertSourceType === 'INFRASTRUCTURE') {
      const infrastructureDsValidate = await infrastructureDs.validate();
      if (infrastructureDsValidate) {
        const alertRuleZabbix = infrastructureDs.current.toJSONData();
        sourceDs.current.set('alertRuleZabbix', alertRuleZabbix);
      } else {
        return false;
      }
    }

    // 数据集
    if (alertSourceType === 'DATASET') {
      const dataSetDsValidate = await dataSetDs.validate();
      if (dataSetDsValidate) {
        const alertDatasetParams = paramTableDs.toData();
        dataSetDs.current.set('alertDatasetParams', alertDatasetParams);
        const alertDataset = dataSetDs.current.toJSONData();
        sourceDs.current.set('alertDataset', alertDataset);
      } else {
        return false;
      }
    }

    // 日志
    if (alertSourceType === 'LOGGING') {
      const loggingDsValidate = await loggingDs.validate();
      if (loggingDsValidate) {
        const alertRuleLog = loggingDs.current.toJSONData();
        sourceDs.current.set('alertRuleLog', alertRuleLog);
      } else {
        return false;
      }
    }

    // 数据处理表格
    const alertRuleParamMapList = dataHandleTableDs.toData();
    sourceDs.current.set('alertRuleParamMapList', alertRuleParamMapList);

    const res = await sourceDs.submit();
    if (!isEmpty(res) && res.success) {
      alertRuleDs.query();
      return true;
    } else if (res === undefined) {
      notification.warning({
        message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
      });
      return false;
    } else if (res === false) {
      notification.error({
        message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
      });
      return false;
    } else {
      return false;
    }
  };

  /**
   * 显示目标侧滑
   * @param {object} record - 行数据
   */
  const handleTarget = (record) => {
    const alertId = record && record.get('alertId');
    const targetDs = new DataSet(targetDS(alertId));
    targetDs.create({});
    const targetProps = {
      targetDs,
      alertId,
      onUpdate: handleUpdate,
    };
    targetModal = Modal.open({
      title: intl.get('halt.warnConfig.view.title.target').d('目标'),
      drawer: true,
      key: 'target',
      destroyOnClose: true,
      closable: true,
      children: <TargetDrawer {...targetProps} />,
      onOk: () => handleSaveTarget(targetDs),
      onClose: () => {
        targetDs.removeAll();
      },
    });
  };

  /**
   * 保存目标
   * @param {object} targetDs - 目标ds
   */
  const handleSaveTarget = async (targetDs) => {
    const submitData = targetDs.current.toData();
    const { alertRouteType, alertTargetType } = submitData;
    if (alertRouteType === 'SIMPLE') {
      if (isEmpty(alertTargetType)) {
        notification.error({
          message: intl
            .get('halt.alertRule.view.message.alertTargetType.warning')
            .d('请选择至少一种通知方式'),
        });
        return false;
      }
    }
    const res = await targetDs.submit();
    if (!isEmpty(res) && res.success) {
      alertRuleDs.query();
      return true;
    } else if (res === undefined) {
      notification.warning({
        message: intl.get('halt.common.view.message.form.noChange').d('表单未做修改'),
      });
      return false;
    } else if (res === false) {
      notification.error({
        message: intl.get('halt.common.view.message.required').d('存在必输字段未填写'),
      });
      return false;
    } else {
      return false;
    }
  };

  return (
    <>
      <HeaderButtons dataSet={alertRuleDs} path={path} onCreate={handleEdit} />
      <Content>
        <Table dataSet={alertRuleDs} columns={columns} queryFieldsLimit={3} pristine />
      </Content>
    </>
  );
};

export default formatterCollections({ code: ['halt.common', 'halt.alertRule'] })(AlertRule);
