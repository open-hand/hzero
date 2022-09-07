import React from 'react';
import { Timeline, Tag, Tooltip, Icon } from 'choerodon-ui';
import { Modal, ModalContainer } from 'choerodon-ui/pro';
import { take } from 'lodash';

import intl from 'utils/intl';

import DetailDrawer from './DetailDrawer';

const AuditLogTimeLine = (props) => {
  const { dataSource = {}, onMore, width } = props;

  const { number, totalPages } = dataSource;

  // const [key, setKey] = React.useState([]);

  const [page, setPage] = React.useState(number);

  // const ids = [];

  React.useEffect(() => {
    setPage(number);
  }, [number]);

  /**
   * 收起
   * @param {*} logId
   */
  // const handleUp = (logId) => {
  //   const k = key.filter((item) => item !== logId);
  //   setKey(k);
  // };

  /**
   * 展开
   * @param {*} logId
   */
  // const handleDown = (logId) => {
  //   ids.push(logId, ...key);
  //   setKey(ids);
  // };

  /**
   * 查看详情
   * @param {*} logId
   */
  // const handleDetail = (logId) => {
  //   const { onClose } = props;
  //   history.push(`/hmnt/audit-query?logId=${logId}`);
  //   onClose();
  // };

  /**
   * 加载更多
   */
  const handleMore = () => {
    setPage(page + 1);
    onMore(page + 1);
  };

  const handleDetail = (record) => {
    const title = intl.get('hzero.common.status.detail').d('查看详情');
    Modal.open({
      drawer: true,
      key: 'documentAuditLog',
      destroyOnClose: true,
      closable: true,
      title,
      style: {
        right: width + 1,
      },
      children: <DetailDrawer record={record} />,
      footer: null,
    });
  };

  return (
    <div style={{ height: '90%' }}>
      <Timeline
        pending={
          totalPages === page ? (
            <a>{intl.get('hzero.common.view.message.noMore').d('没有更多了')}....</a>
          ) : (
            <a onClick={handleMore}>
              {intl.get('hzero.common.view.message.loadMore').d('加载更多')}
            </a>
          )
        }
      >
        {dataSource.content &&
          dataSource.content.map((item) => {
            return (
              <Timeline.Item>
                <p>
                  <b>{item.auditContent}</b>
                </p>
                <p>
                  <span style={{ marginRight: 12 }}>{item.auditDatetime}</span>
                  <span style={{ marginRight: 6 }}>
                    {item.auditResult === 'SUCCESS' ? (
                      <Tag color="green">{intl.get('hzero.common.status.success').d('成功')}</Tag>
                    ) : (
                      <Tag color="red">{intl.get('hzero.common.status.error').d('失败')}</Tag>
                    )}
                  </span>
                  {item.menuName && (
                    <span>
                      <Tooltip title="菜单">
                        <Tag color="blue">{item.menuName}</Tag>
                      </Tooltip>
                    </span>
                  )}
                </p>
                <p>
                  {/* {item.remark && item.remark.length !== 0 && (
                    <a
                      onClick={
                        key.includes(item.logId)
                          ? () => handleUp(item.logId)
                          : () => handleDown(item.logId)
                      }
                    >
                      {key.includes(item.logId)
                        ? intl.get('hzero.common.button.up').d('收起')
                        : intl.get('hzero.common.button.down').d('展开')}
                      <Icon
                        style={{ marginBottom: 4 }}
                        type={
                          key.includes(item.logId)
                            ? 'baseline-arrow_drop_up'
                            : 'baseline-arrow_drop_down'
                        }
                      />
                    </a>
                  )} */}
                </p>
                {/* {key.includes(item.logId) && ( */}
                <div>
                  {item.remark &&
                    item.remark.map((i) => {
                      return (
                        <>
                          <p>
                            <Icon
                              type="work_log"
                              style={{ marginRight: 6, marginBottom: 2, color: '#80d4de' }}
                            />
                            {i.remarkHeader}
                            {i.remarkLines && i.remarkLines.length > 3 && (
                              <a
                                onClick={() => handleDetail(i.remarkLines)}
                                style={{ marginLeft: 12 }}
                              >
                                {intl.get('hzero.common.status.detail').d('查看详情')}
                              </a>
                            )}
                          </p>
                          {i.remarkLines &&
                            take(i.remarkLines, 3).map((r) => {
                              return (
                                <p>
                                  <Icon
                                    type="mode_edit"
                                    style={{
                                      marginRight: 6,
                                      marginBottom: 2,
                                      marginLeft: 20,
                                      color: '#80d4de',
                                    }}
                                  />
                                  {r}
                                </p>
                              );
                            })}
                        </>
                      );
                    })}
                </div>
                {/* )} */}
              </Timeline.Item>
            );
          })}
      </Timeline>
      <ModalContainer location={location} />
    </div>
  );
};

export default AuditLogTimeLine;
