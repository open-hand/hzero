import React, { useEffect, useState, useMemo } from 'react';
import { Form, Button, Select, DateTimePicker, TextField } from 'choerodon-ui/pro';
import { Row, Col } from 'choerodon-ui';
import uuidv4 from 'uuid/v4';

import intl from 'utils/intl';
import styles from './index.less';

let queryKey = '';
let isManualStop = false;
const LogViewDrawer = (props) => {
  let data = [];
  let lock = false;
  const { pipelineCode, logFormDs, logViewDs } = props;
  const [list, setList] = useState([]);
  const [isSearching, setIsSearching] = useState(false);
  const [manualStop, setManualStop] = useState(false);

  let current;
  useEffect(() => {
    logViewDs.setQueryParameter('pipelineCode', pipelineCode);
    const key = uuidv4();
    queryKey = key;
  }, []);

  useMemo(() => {
    current = document.getElementById('log-view-content');
    if (current) {
      current.addEventListener('scroll', () => {
        const { scrollHeight, scrollTop, clientHeight } = current;
        if (scrollHeight - scrollTop === clientHeight) {
          lock = false;
          return false;
        }
        lock = true;
      });
    }
  }, list);

  // 查询表格数据
  const handleQuery = (key) => {
    if (key === queryKey) {
      // key 用来避免用已被拦截的数据的id进行查询
      logViewDs.query().then(
        (res) => {
          if (res) {
            const { content } = res;
            if (Array.isArray(content)) {
              const len = content.length;
              if (len > 0) {
                data = data.concat(content);
                setList(data);
                const { pipelineLogId } = content[len - 1];

                if (!lock && current) {
                  handleChangeScroll();
                }
                logViewDs.setQueryParameter('pipelineLogId', pipelineLogId);
                handleQuery(key, pipelineLogId);
              } else {
                logViewDs.setQueryParameter('pipelineLogId', '');
                setIsSearching(false);
              }
            }
            if (isManualStop) {
              setIsSearching(false);
              isManualStop = false;
            }
          }
        },
        () => {
          setIsSearching(false);
          isManualStop = false;
        }
      );
    }
  };

  // 表单查询
  const handleSearch = () => {
    const { after = '', logLevel = '', namespace = '' } = logFormDs.current.toData();
    if (!manualStop) {
      logViewDs.setQueryParameter('pipelineLogId', '');
      data = [];
      setList(data);
    }
    setManualStop(false);
    logViewDs.setQueryParameter('after', after);
    logViewDs.setQueryParameter('logLevel', logLevel);
    logViewDs.setQueryParameter('namespace', namespace);
    setIsSearching(true);
    logViewDs.interceptQuery = false;
    const key = uuidv4();
    queryKey = key;
    handleQuery(key);
  };

  // 重置
  const handleReset = () => {
    logFormDs.current.reset();
  };

  // 停止查询
  const handleStopSearch = () => {
    logViewDs.interceptQuery = true;
    setManualStop(true);
    isManualStop = true;
  };

  /**
   * 控制滚动条位置
   * @param {string} direction - 方向
   * @return
   */
  const handleChangeScroll = (direction) => {
    current = document.getElementById('log-view-content');
    if (current) {
      const { scrollHeight, clientHeight } = current;
      current.scrollTop = direction === 'top' ? 0 : scrollHeight - clientHeight;
    }
  };

  /**
   * 清除日志
   */
  const handleClearLog = () => {
    data = [];
    setList([]);
  };

  return (
    <>
      <div className={styles['log-view-wrap']}>
        <div className={styles['log-view-search']}>
          <Row gutter={16}>
            <Col span={18}>
              <Form dataSet={logFormDs} columns={3}>
                <TextField name="namespace" />
                <Select name="logLevel" />
                <DateTimePicker name="after" />
              </Form>
            </Col>
            <Col span={6}>
              <div className={styles['log-view-btn-search']}>
                <Button onClick={handleReset}>
                  {intl.get('hzero.common.button.reset').d('重置')}
                </Button>
                {!isSearching ? (
                  <Button color="primary" onClick={handleSearch}>
                    {intl.get('hzero.common.button.search').d('查询')}
                  </Button>
                ) : (
                  <Button color="primary" onClick={handleStopSearch}>
                    {intl.get('hdsc.dataFlowPipeline.view.button.stopSearch').d('停止查询')}
                  </Button>
                )}
              </div>
            </Col>
          </Row>
        </div>
        <div className={styles['log-view-content-btns-wrap']}>
          <div className={styles['log-view-content-btns']}>
            <Button
              className={styles['log-vie-btn-item']}
              icon="delete_forever"
              onClick={handleClearLog}
            >
              {intl.get('hdsc.dataFlowPipeline.view.button.clearLog').d('清除日志')}
            </Button>
            <Button
              className={styles['log-vie-btn-item']}
              icon="publish"
              onClick={() => handleChangeScroll('top')}
            >
              {intl.get('hdsc.dataFlowPipeline.view.button.toTop').d('回到顶部')}
            </Button>
            <Button
              className={styles['log-vie-btn-item']}
              icon="get_app"
              onClick={handleChangeScroll}
            >
              {intl.get('hdsc.dataFlowPipeline.view.button.toBottom').d('回到底部')}
            </Button>
          </div>
        </div>
        <div id="log-view-content" className={styles['log-view-content']}>
          <div>
            {list.length > 0 ? (
              list.map((item) => {
                const { logLevel = '', logContent = '', createdDate = '' } = item;
                return (
                  <div className={styles['log-view-item']}>
                    {createdDate}&nbsp;&nbsp;{logLevel}&nbsp;{logContent}&nbsp;
                  </div>
                );
              })
            ) : (
              <h3 className={styles['log-view-no-data']}>
                {intl.get('hdsc.dataFlowPipeline.view.button.noLogView').d('暂无日志')}
              </h3>
            )}
          </div>
        </div>
      </div>
    </>
  );
};
export default LogViewDrawer;
