import React, { useState, useEffect } from 'react';
import { Spin } from 'choerodon-ui/pro';
import { Card } from 'choerodon-ui';
import { isEmpty, isObject } from 'lodash';

import intl from 'utils/intl';

import { fetchLog } from '@/services/directiveIssuedLogService';
import styles from './index.less';

const Drawer = (props) => {
  const { currentEditData } = props;
  const [isSpin, setIsSpin] = useState(false);
  const [errorMsg, setErrorMsg] = useState([{}, {}, {}, {}]);
  const [processDataMsg, setProcessData] = useState('');
  const [serviceIp, setServiceIp] = useState('');
  useEffect(() => {
    setIsSpin(true);
    queryData();
  }, []);
  // 查询详情数据
  const queryData = async () => {
    const { downLogId, serviceInstIp } = currentEditData;
    setServiceIp(serviceInstIp);
    await fetchLog(downLogId).then((res) => {
      if (!isEmpty(res)) {
        const { logMsgDownData = {} } = res;
        const { errorMessage, processData } = logMsgDownData;
        setProcessData(processData);
        setErrorMsg(errorMessage);
      }
      setIsSpin(false);
    });
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
      {serviceIp && (
        <h3 style={{ marginLeft: 20, marginBottom: 10 }}>
          {intl.get('hiot.directiveIssued.model.dirIssued.serviceInstIps').d('服务实例IP: ')}
          <span>{serviceIp}</span>
        </h3>
      )}
      <Card title={intl.get('hiot.directiveIssued.view.message.processPacket').d('下发报文')}>
        <div className={styles['directive-issued--log-card-content']}>
          <pre style={{ margin: 0, overflow: 'visible' }}>{renderText(processDataMsg)}</pre>
        </div>
      </Card>
      {errorMsg && (
        <Card
          style={{ marginTop: 16 }}
          title={intl.get('hiot.directiveIssued.view.message.errorMessage').d('错误信息')}
        >
          <div className={styles['directive-issued--log-card-content']}>
            <pre
              style={{ margin: 0, overflow: 'visible' }}
              dangerouslySetInnerHTML={{ __html: errorMsg }}
            />
          </div>
        </Card>
      )}
    </Spin>
  );
};

export default Drawer;
