import React, { useState, useEffect } from 'react';
import { Spin } from 'choerodon-ui/pro';
import { Card, Steps } from 'choerodon-ui';
import { isNumber, isArray, isObject } from 'lodash';

import intl from 'utils/intl';

import { fetchLog } from '@/services/dataReportLogService';
import styles from './index.less';

const { Step } = Steps;

const Drawer = (props) => {
  const { currentEditData } = props;

  const [isSpin, setIsSpin] = useState(false);
  const [listData, setListData] = useState([{}, {}, {}]);
  const [processData, setProcessData] = useState('');
  const [processMessage, setProcessMessage] = useState('');
  const [checkedStep, setCheckedStep] = useState('');
  const [serviceIp, setServiceIp] = useState('');
  const [dateTime, setDateTime] = useState('');
  const [topName, setTopName] = useState('');

  useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);

  // 查询详情数据
  const queryData = async () => {
    const { upLogId } = currentEditData;
    await fetchLog(upLogId).then((res) => {
      const arr = [{}, {}, {}];
      if (isArray(res) && res.length) {
        res.forEach((item) => {
          const { logMsgUp } = item;
          const logData = logMsgUp || {};
          let status = '';
          const { processStatus, processStage } = logData;
          switch (processStatus) {
            case 1:
              status = 'finish';
              break;
            case 0:
              status = 'error';
              break;
            default:
              status = 'wait';
              break;
          }

          if (isNumber(processStage)) {
            arr[processStage] = {
              ...item,
              status,
            };
          }
        });
        if (arr[0].status && arr[0].status !== 'wait') {
          setCheckedStep('message');
        }
        const { logMsgUpData, logMsgUp } = arr[0];
        const data = logMsgUpData || {};
        const newLogUp = logMsgUp || {};
        const { creationDate = '', serviceInstIp = '', topicName = '' } = newLogUp;
        setServiceIp(serviceInstIp);
        setDateTime(creationDate);
        setTopName(topicName);
        setProcessData(data.processData);
        setProcessMessage(data.errorMessage);
        setIsSpin(false);
        setListData(arr);
      }
      setIsSpin(false);
    });
  };

  /**
   * 点击步骤
   * @param {number} index - 索引
   * @param {string} value - 当前步骤
   */
  const handleClick = (index, value) => {
    const { status = 'wait' } = listData[index];
    if (status !== 'wait') {
      const { logMsgUpData, logMsgUp } = listData[index];
      const data = logMsgUpData || {};
      const newLogUp = logMsgUp || {};
      const { creationDate = '', serviceInstIp = '', topicName = '' } = newLogUp;
      setServiceIp(serviceInstIp);
      setDateTime(creationDate);
      setTopName(topicName);
      setCheckedStep(value);
      setProcessData(data.processData);
      setProcessMessage(data.errorMessage);
    }
  };

  const getData = (value) => {
    try {
      return JSON.parse(value);
    } catch (error) {
      return value;
    }
  };

  const renderText = (value) => {
    const content = getData(value);
    if (isObject(content)) {
      return JSON.stringify(content, 0, 2);
    } else {
      return content;
    }
  };

  return (
    <Spin spinning={isSpin}>
      <div className={styles['data-report-log-drawer']}>
        <Steps direction="vertical">
          <Step
            className={
              // eslint-disable-next-line no-nested-ternary
              checkedStep === 'message'
                ? listData[0].status !== 'finish'
                  ? styles['data-report-log-step-checked-error']
                  : styles['data-report-log-step-checked']
                : ''
            }
            status={listData[0].status}
            title={intl.get('hiot.dataReport.view.message.mqttSubscriber').d('mqtt订阅')}
            onClick={() => {
              handleClick(0, 'message');
            }}
          />
          <Step
            className={
              // eslint-disable-next-line no-nested-ternary
              checkedStep === 'data'
                ? listData[1].status !== 'finish'
                  ? styles['data-report-log-step-checked-error']
                  : styles['data-report-log-step-checked']
                : ''
            }
            status={listData[1].status}
            title={intl.get('hiot.dataReport.view.message.dataTransfer').d('数据转换')}
            onClick={() => {
              handleClick(1, 'data');
            }}
          />
          <Step
            status={listData[2].status}
            title={intl.get('hiot.dataReport.view.message.dataProcessing').d('数据处理')}
            className={
              // eslint-disable-next-line no-nested-ternary
              checkedStep === 'rule'
                ? listData[2].status !== 'finish'
                  ? styles['data-report-log-step-checked-error']
                  : styles['data-report-log-step-checked']
                : ''
            }
            onClick={() => {
              handleClick(2, 'rule');
            }}
          />
        </Steps>
        <div className={styles['data-report-log-card-warp']}>
          {(serviceIp || dateTime || topName) && (
            <Card
              type="inner"
              title={intl.get('hiot.dataReport.view.message.upLogTitle').d('上报日志')}
            >
              <div className={styles['data-report-log-card-content']}>
                {serviceIp && (
                  <div>
                    {intl.get('hiot.dataReport.model.dirIssued.serviceInstIps').d('服务实例IP: ')}
                    <span>{serviceIp}</span>
                  </div>
                )}
                {dateTime && (
                  <div style={{ marginTop: 10 }}>
                    {intl.get('hiot.dataReport.model.dirIssued.dateTimes').d('记录日期: ')}
                    <span>{dateTime}</span>
                  </div>
                )}
                {topName && (
                  <div style={{ marginTop: 10 }}>
                    {intl.get('hiot.dataReport.model.dirIssued.topNames').d('Topic名称: ')}
                    <span style={{ wordBreak: 'break-all' }}>{topName}</span>
                  </div>
                )}
              </div>
            </Card>
          )}
          {processData && (
            <Card
              type="inner"
              style={{ marginTop: 16 }}
              title={intl.get('hiot.dataReport.view.message.processPacket').d('处理报文')}
            >
              <div className={styles['data-report-log-card-content']}>
                <pre style={{ margin: 0, overflow: 'visible' }}>{renderText(processData)}</pre>
              </div>
            </Card>
          )}
          {processMessage && (
            <Card
              type="inner"
              style={{ marginTop: 16 }}
              title={intl.get('hiot.dataReport.view.message.errorMessage').d('错误信息')}
            >
              <div className={styles['data-report-log-card-content']}>
                <pre
                  style={{ margin: 0, overflow: 'visible' }}
                  dangerouslySetInnerHTML={{ __html: processMessage }}
                />
                <div />
              </div>
            </Card>
          )}
        </div>
      </div>
    </Spin>
  );
};

export default Drawer;
