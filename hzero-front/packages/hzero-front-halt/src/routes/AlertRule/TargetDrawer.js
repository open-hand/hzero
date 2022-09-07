/**
 * 告警规则配置 - 目标侧滑
 * @date: 2020-5-25
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */
import React, { useState, useEffect } from 'react';
import { Form, Spin, Lov, TextField, Select, SelectBox } from 'choerodon-ui/pro';
import { Tabs } from 'choerodon-ui';
import { isNull } from 'lodash';

import intl from 'utils/intl';

const { TabPane } = Tabs;

const TargetDrawer = (props) => {
  const { alertId, targetDs, onUpdate } = props;
  const [isSpin, setIsSpin] = useState(false);
  const [activeKey, setActiveKey] = useState('');
  const [MQDisable, setMQDisable] = useState(false);
  const [msgDisable, setMsgDisable] = useState(false);
  const [apiDisable, setApiDisable] = useState(false);

  useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);

  // 查询目标数据
  const queryData = async () => {
    targetDs.alertId = alertId;
    try {
      await targetDs.query().then((res) => {
        if (res) {
          const { alertTargetType, alertRouteType } = res;
          let nextAlertTargetType = alertTargetType.split(',');
          if (alertRouteType) {
            if (alertRouteType === 'SIMPLE') {
              nextAlertTargetType = nextAlertTargetType.filter((item) => item !== 'AM');
              targetDs.current.set('alertTargetType', nextAlertTargetType);
              setActiveKey(nextAlertTargetType[0]);
              setApiDisable(nextAlertTargetType.includes('API'));
              setMQDisable(nextAlertTargetType.includes('MQ'));
              setMsgDisable(nextAlertTargetType.includes('MSG'));
            } else {
              targetDs.current.set('alertTargetType', null);
            }
          }
        }
        setIsSpin(false);
      });
    } catch {
      setIsSpin(false);
    }
  };

  // 更新侧滑
  const updateModal = () => {
    onUpdate('target', { alertId, targetDs, onUpdate });
  };

  const handleChangeAlertTargetType = (value) => {
    const newValue = isNull(value) ? [] : value;
    const n = newValue.length === 0 ? 0 : newValue.length - 1;
    setActiveKey(newValue[n]);
    setApiDisable(newValue.includes('API'));
    setMQDisable(newValue.includes('MQ'));
    setMsgDisable(newValue.includes('MSG'));
  };

  return (
    <>
      <Spin spinning={isSpin}>
        <Form dataSet={targetDs}>
          <Select name="alertRouteType" clearButton={false} onChange={updateModal} />
        </Form>
        {targetDs.current && targetDs.current.get('alertRouteType') === 'SIMPLE' && (
          <>
            <Form dataSet={targetDs}>
              <SelectBox name="alertTargetType" onChange={handleChangeAlertTargetType} />
            </Form>
            {(apiDisable || MQDisable || msgDisable) && (
              <Tabs
                // animated={false}
                activeKey={activeKey}
                onChange={(value) => {
                  setActiveKey(value);
                }}
              >
                {apiDisable && (
                  <TabPane
                    key="API"
                    tab={intl.get('halt.alertRule.view.title.ruleMapping').d('API配置')}
                  >
                    <Form dataSet={targetDs}>
                      <TextField name="apiCallback" />
                      <TextField name="apiSignKey" />
                    </Form>
                  </TabPane>
                )}
                {MQDisable && (
                  <TabPane key="MQ" tab={intl.get('halt.alertRule.view.title.mq').d('消息队列')}>
                    <Form dataSet={targetDs}>
                      {/* <TextField name="mqTopic" /> */}
                      <Lov name="mqTopicLov" />
                    </Form>
                  </TabPane>
                )}
                {msgDisable && (
                  <TabPane key="MSG" tab={intl.get('halt.alertRule.view.title.msg').d('消息平台')}>
                    <Form dataSet={targetDs}>
                      <Lov name="sendConfigLov" />
                      <Lov name="receiveGroupLov" />
                    </Form>
                  </TabPane>
                )}
              </Tabs>
            )}
          </>
        )}
      </Spin>
    </>
  );
};

export default TargetDrawer;
