/**
 * 告警规则配置 - 来源侧滑
 * @date: 2020-5-25
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useState, useEffect, useMemo } from 'react';
import {
  Form,
  Switch,
  DateTimePicker,
  Spin,
  Select,
  NumberField,
  Lov,
  Table,
  TextArea,
  Tooltip,
} from 'choerodon-ui/pro';
// import { getResponse } from 'utils/utils';
import { Tabs, Icon, Divider } from 'choerodon-ui';
import { groupBy } from 'lodash';
import intl from 'utils/intl';
import AddonTable from './AddonTable';
import LoggingForm from './LoggingForm';
import styles from './index.less';

const { TabPane } = Tabs;

const TargetDrawer = (props) => {
  const {
    sourceDs,
    onUpdate,
    alertId,
    triggerTagTableDs,
    triggerCommentTableDs,
    applicationDs,
    infrastructureDs,
    dataHandleTableDs,
    dataSetDs,
    paramTableDs,
    loggingDs,
  } = props;
  const [isSpin, setIsSpin] = useState(true);
  const [tabKey, setTabKey] = useState('DS');
  const [isEdit, setIsEdit] = useState(false);
  const [infrastructureEdit, setInfrastructureEdit] = useState(false);
  const [isReloadDisabled, setIsReloadDisabled] = useState(false);
  const [isReloadParamDisabled, setIsReloadParamDisabled] = useState(false);

  useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);

  // 查询来源
  const queryData = async () => {
    try {
      await sourceDs.query().then((res) => {
        if (res) {
          const {
            alertAddonList = [],
            alertDataset,
            alertRuleLog,
            alertRuleZabbix,
            alertRulePrometheus,
            alertRuleParamMapList = [],
            alertSourceType,
            alertRuleCode,
            alertSourceId, // 数据来源id
            alertDatasetId, // 数据集id
          } = res;
          if (!alertSourceType) {
            sourceDs.current.set('alertSourceType', 'DIRECT');
            setIsEdit(false);
          } else {
            setIsEdit(true);
            if (alertSourceType === 'INFRASTRUCTURE') {
              setInfrastructureEdit(true);
            } else {
              setInfrastructureEdit(false);
            }
          }

          // 初始化标签、注解表格
          const groupByAddonTypeData = groupBy(alertAddonList, 'addonType');
          triggerTagTableDs.loadData(groupByAddonTypeData.LABEL || []);
          triggerCommentTableDs.loadData(groupByAddonTypeData.ANNOTATION || []);
          // 初始化数据处理表格
          dataHandleTableDs.loadData(alertRuleParamMapList);
          if (alertRuleCode) {
            dataHandleTableDs.setQueryParameter('isRefresh', 'true');
            dataHandleTableDs.setQueryParameter('alertSourceId', alertSourceId);
          }
          // 初始化数据集
          if (alertDataset) {
            dataSetDs.create(alertDataset);
          }

          // 初始化应用日志数据
          if (alertRuleLog) {
            loggingDs.create(alertRuleLog);
          }

          // 初始化应用中间件数据
          if (alertRulePrometheus) {
            applicationDs.create(alertRulePrometheus);
          }

          // 初始化基础设施数据
          if (alertRuleZabbix) {
            infrastructureDs.create(alertRuleZabbix);
          }

          // 初始化数据集参数信息
          if (alertDataset.alertDatasetParams) {
            paramTableDs.loadData(alertDataset.alertDatasetParams);
            paramTableDs.setQueryParameter('isRefreshParam', true);
            paramTableDs.setQueryParameter('alertDatasetId', alertDatasetId);
          } else {
            paramTableDs.loadData([]);
          }
        } else {
          sourceDs.create({ alertSourceType: 'DIRECT' });
          triggerTagTableDs.loadData([]);
          triggerCommentTableDs.loadData([]);
          dataHandleTableDs.loadData([]);
          dataSetDs.create({});
          paramTableDs.loadData([]);
          loggingDs.create({});
          applicationDs.create({});
          infrastructureDs.create({ ruleTargetType: 'HOST' });
        }
        sourceDs.current.set('alertId', alertId);
        setIsSpin(false);
      });
    } catch {
      setIsSpin(false);
    }
  };

  /**
   * 数据处理表格列
   */
  const dataHandleTableColumns = useMemo(() => [
    { name: 'sourceKey', editor: true },
    { name: 'targetKey' },
  ]);

  // 数据集参数列
  const paramColumns = useMemo(
    () => [{ name: 'paramName' }, { name: 'paramType' }, { name: 'paramValue', editor: true }],
    []
  );

  // 侧滑更新
  const handleChangeAlertSourceType = () => {
    onUpdate('source', {
      sourceDs,
      alertId,
      onUpdate,
      triggerTagTableDs,
      triggerCommentTableDs,
      applicationDs,
      infrastructureDs,
      dataHandleTableDs,
      dataSetDs,
      paramTableDs,
      loggingDs,
    });
  };

  // 切换数据集tab页时
  const onTabChange = (value) => {
    setTabKey(value);
  };

  // 基础设施切换应用目标
  const handleChangeRuleTargetType = () => {
    infrastructureDs.current.set('hostLov', null);
    infrastructureDs.current.set('templateLov', null);
    handleChangeAlertSourceType();
  };

  const datasetTabsDisabled = dataSetDs.current && !dataSetDs.current.get('datasetCode');

  const handleChangeRuleLov = (record) => {
    if (record) {
      setIsReloadDisabled(false);
      dataHandleTableDs.setQueryParameter('isRefresh', 'false');
      dataHandleTableDs.setQueryParameter('ruleCode', record.ruleCode);
      dataHandleTableDs.query();
    } else {
      setIsReloadDisabled(true);
      dataHandleTableDs.setQueryParameter('ruleCode', '');
      dataHandleTableDs.loadData([]);
    }
  };

  /**
   * 切换数据集代码
   * @param {object} record - 数据集行数据
   */
  const handleChangeDataSetLov = (record) => {
    if (record) {
      setIsReloadParamDisabled(false);
      paramTableDs.setQueryParameter('isRefreshParam', false);
      paramTableDs.setQueryParameter('datasetCode', record.datasetCode);
      paramTableDs.query();
    } else {
      setIsReloadParamDisabled(true);
      paramTableDs.setQueryParameter('datasetCode', '');
      paramTableDs.loadData([]);
    }
  };

  // 重新查询数据处理列表
  const handleReloadAlertRuleParamMapList = async () => {
    const { alertRuleCode, alertSourceId } = sourceDs.current.toData();
    const { isRefresh } = dataHandleTableDs.queryParameter;
    if (isRefresh === 'true') {
      dataHandleTableDs.setQueryParameter('alertSourceId', alertSourceId);
      dataHandleTableDs.query();
    } else if (isRefresh === 'false') {
      dataHandleTableDs.setQueryParameter('ruleCode', alertRuleCode);
      dataHandleTableDs.query();
    }
  };

  // 重新查询数据集参数列表
  const handleReloadAlertDatasetParams = async () => {
    const { datasetCode, alertDatasetId } = dataSetDs.current.toData();
    const { isRefreshParam } = paramTableDs.queryParameter;
    if (isRefreshParam) {
      paramTableDs.setQueryParameter('alertDatasetId', alertDatasetId);
      paramTableDs.query();
    } else {
      paramTableDs.setQueryParameter('datasetCode', datasetCode);
      paramTableDs.query();
    }
  };

  const optionRenderer = ({ record, text }) => {
    if (record.get('enable')) {
      return <div style={{ width: '100%' }}>{text}</div>;
    } else {
      return (
        <Tooltip
          placement="left"
          title={intl.get('halt.alertRule.view.message.source.disabled').d('该数据来源已被禁用')}
        >
          <div className={styles['option-disabled']}>{text}</div>
        </Tooltip>
      );
    }
  };

  const handleOption = ({ record }) => {
    return {
      disabled: record.get('enable') === false,
    };
  };

  return (
    <div className={styles['alert-manage-drawer']}>
      <Spin spinning={isSpin}>
        <Tabs animated={false}>
          <TabPane
            tab={intl.get('halt.alertRule.view.title.dataSource').d('数据来源')}
            key="dataSource"
          >
            <Form dataSet={sourceDs}>
              <Select
                name="alertSourceType"
                clearButton={false}
                onChange={handleChangeAlertSourceType}
                disabled={isEdit}
                optionRenderer={optionRenderer}
                onOption={handleOption}
              />
            </Form>
            <Divider orientation="left">
              <h3>{intl.get('halt.alertRule.view.title.data.rule').d('数据处理规则')}</h3>
            </Divider>
            <Form dataSet={sourceDs} columns={3}>
              <Lov name="ruleLov" onChange={handleChangeRuleLov} />
              <Switch name="autoRecoverFlag" />
              <Switch name="upgradeFlag" />
            </Form>
            <div className={styles['data-handle-container']}>
              <a onClick={handleReloadAlertRuleParamMapList} disabled={isReloadDisabled}>
                {intl.get('hzero.common.button.reload').d('重新加载')}
                <Icon
                  type="autorenew"
                  style={{ marginLeft: '3px', marginTop: '-3px', fontSize: '15px' }}
                />
              </a>
              <div className={styles.clear} />
              <Table dataSet={dataHandleTableDs} columns={dataHandleTableColumns} />
              {sourceDs.current && sourceDs.current.get('alertSourceType') !== 'DIRECT' && (
                <Divider orientation="left">
                  {sourceDs.current && sourceDs.current.get('alertSourceType') === 'DATASET' && (
                    <h3>{intl.get('halt.alertRule.view.title.source.config').d('数据来源配置')}</h3>
                  )}
                </Divider>
              )}
              {sourceDs.current && sourceDs.current.get('alertSourceType') === 'DATASET' && (
                <>
                  <Form dataSet={dataSetDs}>
                    <Lov name="datasetLov" onChange={handleChangeDataSetLov} />
                  </Form>
                  <Tabs
                    disabled={dataSetDs.current && !dataSetDs.current.get('datasetCode')}
                    animated={false}
                    activeKey={tabKey}
                    onChange={(value) => {
                      onTabChange(value);
                    }}
                    tabBarExtraContent={
                      tabKey === 'paramInfo' && (
                        <a
                          style={{ marginRight: '5px' }}
                          onClick={handleReloadAlertDatasetParams}
                          disabled={isReloadParamDisabled}
                        >
                          {intl.get('hzero.common.button.reload').d('重新加载')}
                          <Icon
                            type="autorenew"
                            style={{ marginLeft: '3px', marginTop: '-3px', fontSize: '15px' }}
                          />
                        </a>
                      )
                    }
                  >
                    <TabPane
                      key="DS"
                      tab={intl.get('halt.alertRule.view.title.schedulerConfig').d('调度配置')}
                      disabled={datasetTabsDisabled}
                    >
                      <Form dataSet={dataSetDs}>
                        <DateTimePicker name="startTime" />
                        <DateTimePicker name="endTime" />
                        <Select name="intervalType" />
                        <NumberField name="intervalNumber" min={0} />
                        <NumberField name="intervalHour" min={0} />
                        <NumberField name="intervalMinute" min={0} />
                        <NumberField name="intervalSecond" min={0} />
                      </Form>
                    </TabPane>
                    <TabPane
                      key="paramInfo"
                      tab={intl.get('halt.alertRule.view.title.paramInfo').d('参数信息')}
                      disabled={datasetTabsDisabled}
                    >
                      <Table dataSet={paramTableDs} columns={paramColumns} />
                    </TabPane>
                  </Tabs>
                </>
              )}
              {sourceDs.current && sourceDs.current.get('alertSourceType') === 'INFRASTRUCTURE' && (
                <Form dataSet={infrastructureDs} columns={2}>
                  <Select
                    name="ruleTargetType"
                    onChange={handleChangeRuleTargetType}
                    clearButton={false}
                    disabled={infrastructureEdit}
                  />
                  {infrastructureDs.current &&
                    infrastructureDs.current.get('ruleTargetType') === 'HOST' && (
                      <Lov name="hostLov" noCache />
                    )}
                  {infrastructureDs.current &&
                    infrastructureDs.current.get('ruleTargetType') === 'TEMPLATE' && (
                      <Lov name="templateLov" noCache />
                    )}
                  <TextArea name="alertExpression" colSpan={2} />
                </Form>
              )}
              {sourceDs.current && sourceDs.current.get('alertSourceType') === 'APPLICATION' && (
                <Form dataSet={applicationDs} columns={2}>
                  <NumberField name="forDuration" />
                  <Select name="durationUnit" />
                  <TextArea name="promQl" newLine colSpan={2} />
                </Form>
              )}
              {sourceDs.current && sourceDs.current.get('alertSourceType') === 'LOGGING' && (
                <LoggingForm ds={loggingDs} onUpdate={handleChangeAlertSourceType} />
              )}
            </div>
          </TabPane>
          <TabPane tab={intl.get('halt.alertRule.view.title.tag').d('设置标签')} key="tag">
            <AddonTable dataSet={triggerTagTableDs} addonType="tag" />
          </TabPane>
          <TabPane tab={intl.get('halt.alertRule.view.title.comment').d('设置注解')} key="comment">
            <AddonTable dataSet={triggerCommentTableDs} addonType="comment" />
          </TabPane>
        </Tabs>
      </Spin>
    </div>
  );
};

export default TargetDrawer;
