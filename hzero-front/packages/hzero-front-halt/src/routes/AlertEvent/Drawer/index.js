/**
 * 告警事件管理 - 详情侧滑
 * @date: 2020-5-20
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React, { useState, useEffect } from 'react';
import { Spin } from 'choerodon-ui/pro';
import { Steps, Card } from 'choerodon-ui';
import { isArray } from 'lodash';
import intl from 'utils/intl';
import { queryMapIdpValue } from 'services/api';
import { fetchLog } from '@/services/alertEventServices';
import styles from './index.less';

const { Step } = Steps;

const Drawer = (props) => {
  const { currentEditData } = props;

  const [isSpin, setIsSpin] = useState(false);
  const [listData, setListData] = useState([]);
  const [processData, setProcessData] = useState('');
  const [processMessage, setProcessMessage] = useState('');
  const [stageStatusMeaning, setStageStatusMeaning] = useState('');
  const [checkedStep, setCheckedStep] = useState('');
  const [stageMap, setStageMap] = useState([]); // 阶段的值集

  useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);

  // 查询详情数据
  const queryData = async () => {
    const { alertEventId } = currentEditData;
    Promise.all([
      fetchLog(alertEventId),
      queryMapIdpValue({ TPLPROCESSSTAGE: 'HALT.PROCESS_STAGE' }),
    ]).then(([res, stageMapRes]) => {
      const data = {};
      if (isArray(stageMapRes.TPLPROCESSSTAGE) && stageMapRes.TPLPROCESSSTAGE.length) {
        stageMapRes.TPLPROCESSSTAGE.forEach((item) => {
          let status = '';
          if (res[item.value]) {
            switch (res[item.value].processStatus) {
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
            data[item.value] = {
              ...res[item.value],
              status,
            };
          }
        });
        const index = stageMapRes.TPLPROCESSSTAGE[0].value;
        if (data[index].status && data[index].status !== 'wait') {
          setCheckedStep(stageMapRes.TPLPROCESSSTAGE[0]);
        }
        setProcessData(data[index].processData);
        setProcessMessage(data[index].processMessage);
        setStageStatusMeaning(data[index].stageStatusMeaning);
        setIsSpin(false);
        setListData(data);
      }
      setIsSpin(false);
      setStageMap(stageMapRes.TPLPROCESSSTAGE);
    });
  };

  /**
   * 点击步骤
   * @param {number} index - 索引
   * @param {string} value - 当前步骤
   */
  const handleClick = (index, value) => {
    if (listData[value].status !== 'wait') {
      setCheckedStep(value);
      setProcessData(listData[value].processData);
      setProcessMessage(listData[value].processMessage);
      setStageStatusMeaning(listData[value].stageStatusMeaning);
    }
  };

  const getData = (value) => {
    try {
      return JSON.parse(value);
    } catch (error) {
      return value;
    }
  };

  return (
    <Spin spinning={isSpin}>
      <div className={styles['warn-events-drawer']}>
        <Steps direction="vertical">
          {stageMap.map((stage, i) => {
            return (
              listData[stage.value] && (
                <Step
                  className={
                    // eslint-disable-next-line no-nested-ternary
                    checkedStep === stage.value
                      ? listData[stage.value].status !== 'finish'
                        ? styles['warn-events-step-checked-error']
                        : styles['warn-events-step-checked']
                      : ''
                  }
                  status={listData[stage.value]?.status}
                  title={stage.meaning}
                  onClick={() => {
                    handleClick(i, stage.value);
                  }}
                />
              )
            );
          })}
        </Steps>
        <div className={styles['warn-events-card-warp']}>
          {processData || processMessage || stageStatusMeaning ? (
            <>
              {processData && (
                <Card
                  type="inner"
                  title={intl.get('halt.alertEvent.view.message.processPacket').d('处理报文')}
                >
                  <div className={styles['warn-events-card-content']}>
                    <pre style={{ margin: 0, overflow: 'visible' }}>
                      {JSON.stringify(getData(processData), 0, 2)}
                    </pre>
                  </div>
                </Card>
              )}
              {stageStatusMeaning && (
                <Card
                  style={{ marginTop: 16 }}
                  type="inner"
                  title={intl.get('halt.alertEvent.view.message.processResult').d('处理结果')}
                >
                  <div style={{ margin: '0.16rem 0.24rem' }}>{stageStatusMeaning}</div>
                </Card>
              )}
              {processMessage && (
                <Card
                  style={{ marginTop: 16 }}
                  type="inner"
                  title={intl.get('halt.alertEvent.view.message.errorMessage').d('错误信息')}
                >
                  <div className={styles['warn-events-card-content-last']}>{processMessage}</div>
                </Card>
              )}
            </>
          ) : (
            <h3 style={{ color: 'gray' }}>
              {/* {intl.get('hiam.securityGroup.view.title.secGrp.empty').d('暂无数据')} */}
            </h3>
          )}
        </div>
      </div>
    </Spin>
  );
};

export default Drawer;
