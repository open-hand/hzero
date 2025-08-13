import React from 'react';
import { Icon, Tooltip } from 'hzero-ui';
import { withRouter } from 'dva/router';
import { connect } from 'dva';

import intl from 'utils/intl';
import { openTab } from 'utils/menuTab';
import notification from 'utils/notification';
import { setSession } from 'utils/utils';

import { loadTraceLogAsync, watchTraceLogSetComponent } from '../../../customize/traceLog';

const TraceComp = props => {
  const { onTrace = e => e, hasHims, isStart } = props;
  return hasHims ? (
    <Tooltip
      title={
        !isStart
          ? intl.get('hadm.traceLog.view.title.start').d('启动日志追溯分析')
          : intl.get('hadm.traceLog.view.title.end').d('结束日志追溯分析')
      }
    >
      <Icon
        type={isStart ? 'pause-circle-o' : 'play-circle-o'}
        onClick={onTrace}
        style={{
          color: isStart ? '#ff0000' : '#fff',
          fontSize: '18px',
          cursor: 'pointer',
          lineHeight: 1,
          marginRight: '12px',
        }}
      />
    </Tooltip>
  ) : null;
};

class DefaultTraceLog extends React.Component {
  state = {
    hasHims: false,
  };

  componentDidMount() {
    this.unListener = watchTraceLogSetComponent(() => {
      this.importCard().then(res => {
        if (res) {
          this.setState({
            hasHims: true,
          });
        } else {
          this.setState({
            hasHims: false,
          });
        }
      });
    }, true);
  }

  componentWillUnmount() {
    if (this.unListener) {
      this.unListener();
    }
  }

  handleTrace = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/getTraceStatus',
    }).then(res => {
      if (res) {
        this.handleTraceEnd();
      } else {
        this.handleTraceStart();
      }
    });
  };

  handleTraceStart = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/startTrace',
    }).then(res => {
      if (res) {
        // 启动成功后，此时trace状态为启动,并清除历史记录
        sessionStorage.removeItem('traceLog');
        dispatch({
          type: 'global/updateState',
          payload: { traceGroupId: undefined, traceStatus: true },
        });
        notification.success();
      }
    });
  };

  handleTraceEnd = () => {
    const { dispatch } = this.props;
    dispatch({
      type: 'global/endTrace',
    }).then(res => {
      if (res) {
        const { traceList = [], traceGroupId } = res;
        // 缓存数据到session
        setSession('traceLog', traceList);
        notification.success();
        // 启动结束后，此时trace状态为false
        dispatch({
          type: 'global/updateState',
          payload: { traceGroupId, traceStatus: false },
        });
        // 清除  traceGroupId，5分钟后控制导出不可点击
        setTimeout(() => {
          dispatch({
            type: 'global/updateState',
            payload: { traceGroupId: undefined },
          });
        }, 300000);
        openTab({
          key: '/hadm/trace-log',
          path: '/hadm/trace-log/list',
          title: intl.get('hadm.traceLog.view.title').d('日志追溯分析'),
        });
      }
    });
  };

  importCard = async () => {
    let loadCard = null;
    try {
      loadCard = await loadTraceLogAsync('TraceLog');
    } catch (e) {
      loadCard = null;
    }
    return loadCard;
  };

  render() {
    const { traceStatus } = this.props;
    const { hasHims } = this.state;
    return <TraceComp hasHims={hasHims} onTrace={this.handleTrace} isStart={traceStatus} />;
  }
}

export default withRouter(
  connect(({ global }) => ({
    traceStatus: global.traceStatus,
  }))(DefaultTraceLog)
);
