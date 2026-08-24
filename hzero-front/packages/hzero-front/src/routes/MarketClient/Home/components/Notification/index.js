import React, { useEffect, useRef, useState } from 'react';
import { Badge, Icon, Pagination, Spin, Modal, Tooltip } from 'hzero-ui';
import { HZERO_ADM } from 'utils/config';
import formatterCollections from 'utils/intl/formatterCollections';
import intl from 'utils/intl';
import { downloadFile } from 'services/api';
import RichText from '../../../components/RichTextTool';
import ImgEmpty from '../../../../../assets/market/empty.svg';
import ImgModalTitle from '../../../../../assets/market/modal-title.svg';
import MessageIcon from '../../../../../assets/market/message-icon.svg';
import {
  queryMarketMessage,
  queryMarketMessageDetail,
  deleteMarketMessage,
  readAllMarketMessage,
  queryMarketUnreadMessage,
} from '../../services';
import styles from './index.less';

const defaultPageSize = 8;
const WORK_ORDER = '工单';
const DIVIDE_WORD = '_TJXQ_';

const Notification = ({ isLogin = false, marketUserInfo = {} }) => {
  const { loginName = '' } = marketUserInfo;
  const [messageInfo, setMessageInfo] = useState({}); // 通知列表
  const [messageLoading, setMessageLoading] = useState(false);
  const [isShowDetailModal, setIsShowDetailModal] = useState(false); // 是否显示消息详情弹框
  const [messageDetail, setMessageDetail] = useState(''); // 消息详情内容
  const [unReadCount, setUnReadCount] = useState(0); // 未读的消息数量

  const currentMessagePage = useRef({ page: 0, size: defaultPageSize }); // 当前消息分页查询的位置

  useEffect(() => {
    if (isLogin) {
      _init();
    }
  }, [isLogin]);

  const _init = () => {
    getMarketMessage();
    queryMarketUnreadMessage().then((res) => {
      if (res && !res.failed) {
        setUnReadCount(res.unreadMessageCount);
      }
    });
  };

  const getMarketMessage = (page) => {
    if (typeof page === 'number') {
      currentMessagePage.current.page = page;
    }
    setMessageLoading(true);
    queryMarketMessage(currentMessagePage.current).then((res) => {
      setMessageLoading(false);
      if (res && !res.failed) {
        setMessageInfo(res);
      }
    });
  };

  const onPageChange = (page) => {
    getMarketMessage(page - 1);
  };

  const onDeleteMessage = (userMessageId) => {
    Modal.confirm({
      title: intl.get('hadm.marketclient.view.home.notification.delete').d('是否确认删除该消息?'),
      style: { marginTop: '13%' },
      onOk() {
        deleteMarketMessage({ userMessageIds: [userMessageId] }).then((res) => {
          if (res && !res.failed) {
            _init();
          }
        });
      },
    });
  };

  const onMessageClick = (userMessageId) => {
    setMessageLoading(true);
    queryMarketMessageDetail({ userMessageId }).then((res) => {
      setMessageLoading(false);
      if (res && !res.failed) {
        setMessageDetail(res);
        setIsShowDetailModal(true);
      }
    });
  };

  const onMessageDetailOk = () => {
    setIsShowDetailModal(false);
    _init();
  };

  const handleReadAll = () => {
    Modal.confirm({
      title: intl
        .get('hadm.marketclient.view.home.notification.readAll')
        .d('是否确认标记全部消息为已读?'),
      style: { marginTop: '13%' },
      onOk() {
        readAllMarketMessage({ readAll: true }).then((res) => {
          if (res && !res.failed) {
            _init();
          }
        });
      },
    });
  };

  const renderEmpty = (text) => {
    return (
      <div className={styles.empty}>
        <img src={ImgEmpty} alt="empty" />
        <div>{text}</div>
      </div>
    );
  };

  function renderFeedBackIntoHTML(str = '') {
    function renderJsonIntoStr(json) {
      try {
        return JSON.parse(json).value;
      } catch (e) {
        return '';
      }
    }

    if (str.includes('</feedback>')) {
      return str.replace(/<feedback>(.*)<\/feedback>/g, (_, $1) => renderJsonIntoStr($1));
    } else {
      return str;
    }
  }

  function jumpToDoc(i) {
    window.open(
      `https://open.hand-china.com/document-center/doc/${i.documentType}/${i.documentHeaderId}/${i.documentVersionId}`
    );
  }

  function handleDownloadFile(fileKey) {
    const queryParams = [{ name: 'fileKey', value: fileKey }];
    downloadFile({
      requestUrl: `${HZERO_ADM}/v1/market/work-order/download`,
      queryParams,
    });
  }

  const workOrderRender = () => {
    const list = messageDetail.content.split('\n').filter((v) => v);
    const parseList = list
      .map((item) => {
        if (item.includes('</feedback>')) {
          const [label, jsonStr] = item
            .replace(/<p>(.*)<feedback>(.*)<\/feedback><\/p>/, (_, $1, $2) =>
              [$1, $2].join(DIVIDE_WORD)
            )
            .split(DIVIDE_WORD);
          try {
            return {
              ...JSON.parse(jsonStr),
              label,
            };
          } catch (e) {
            return null;
          }
        } else if (item.includes('</a>')) {
          // 燕千云跳转链接加参数code=user.loginName
          return item.replace(/<a(.*)href="(.*?")(.*)<\/a>/, ($1, $2, $3, $4) => {
            return `<a target="_blank"${$2}href="${$3.substr(
              0,
              $3.length - 1
            )}&code=${loginName}"${$4}</a>`;
          });
        } else {
          return item;
        }
      })
      .filter((v) => v);
    return (
      <div className={styles['work-order-content']}>
        {parseList.map((item, index) => {
          if (typeof item === 'string') {
            return <div dangerouslySetInnerHTML={{ __html: item.replace('&nbsp;', '') }} />;
          } else {
            switch (item.type) {
              case 'text':
                return (
                  // eslint-disable-next-line react/no-array-index-key
                  <div key={`text${index}`} className={styles.row}>
                    <div className={`${styles['left-col']} ${styles.col}`}>{item.label}</div>
                    <div className={`${styles['right-col']} ${styles.col}`}>
                      {item.value}
                      {item.documentType && item.sourceId > 0 && (
                        <p>
                          <a
                            onClick={() => {
                              jumpToDoc(item);
                            }}
                          >
                            文档链接
                          </a>
                        </p>
                      )}
                    </div>
                  </div>
                );
              case 'richText':
                return (
                  // eslint-disable-next-line react/no-array-index-key
                  <div key={`richText${index}`} className={styles.row}>
                    <div className={`${styles['left-col']} ${styles.col}`}>{item.label}</div>
                    <div className={`${styles['right-col']} ${styles.col}`}>
                      <RichText
                        mode="view"
                        content={item.value}
                        className={styles.richtextContainer}
                        userInfo={marketUserInfo}
                      />
                    </div>
                  </div>
                );
              case 'file':
                return (
                  // eslint-disable-next-line react/no-array-index-key
                  <div key={`file${index}`} className={styles.row}>
                    <div className={`${styles['left-col']} ${styles.col}`}>{item.label}</div>
                    <div className={`${styles['right-col']} ${styles.col}`}>
                      {item.list.map((k) => {
                        return (
                          <div key={k.fileKey}>
                            <a onClick={() => handleDownloadFile(k.fileKey)}>{k.fileName}</a>
                          </div>
                        );
                      })}
                    </div>
                  </div>
                );
              default:
                return null;
            }
          }
        })}
      </div>
    );
  };

  const renderContent = () => {
    return (messageDetail.subject || '').includes(WORK_ORDER) ? (
      workOrderRender()
    ) : (
      <div dangerouslySetInnerHTML={{ __html: messageDetail.content }} />
    );
  };

  return (
    <div className={styles.wrapper}>
      <div className={styles.head}>
        <span className={styles.title}>
          <span className={styles.text}>
            {intl.get('hadm.marketclient.view.home.market.notification').d('市场通知')}
          </span>
          {isLogin && (
            <Badge
              showZero
              count={unReadCount}
              style={{
                backgroundColor: '#29BDCD',
                fontSize: '8px',
                height: '14px',
                lineHeight: '14px',
              }}
            />
          )}
        </span>
        {isLogin && (
          <span className={styles.operation}>
            <span className={styles.refresh}>
              <Tooltip title={intl.get('hzero.common.button.refresh').d('刷新')}>
                <Icon type="sync" spin={messageLoading} onClick={_init} />
              </Tooltip>
            </span>
            <Tooltip title={intl.get('hzero.common.basicLayout.option.allRead').d('全部已读')}>
              <span className={styles['all-read']} onClick={handleReadAll} />
            </Tooltip>
          </span>
        )}
      </div>
      <div className={styles.content}>
        {isLogin ? (
          <>
            <Spin spinning={messageLoading}>
              <div className={styles.message}>
                {(messageInfo.content || []).map((message) => {
                  return (
                    <div className={styles.card} key={message.userMessageId}>
                      <div
                        className={styles.content}
                        onClick={() => onMessageClick(message.userMessageId)}
                      >
                        <div className={styles.text}>
                          <div className={styles.dot}>
                            <img src={MessageIcon} alt="" />
                            {!message.readFlag && (
                              <Badge
                                className={styles['dot-top']}
                                style={{ color: '#ff6734' }}
                                dot
                              />
                            )}
                          </div>
                          <div className={styles.title}>
                            {renderFeedBackIntoHTML(message.subject)}
                          </div>
                        </div>
                        <div className={styles.time}>{message.creationDate}</div>
                      </div>
                      <div className={styles.close}>
                        <span>
                          <Icon
                            type="close"
                            onClick={() => onDeleteMessage(message.userMessageId)}
                          />
                        </span>
                      </div>
                    </div>
                  );
                })}
                {messageInfo?.totalElements > 0 && (
                  <Pagination
                    defaultCurrent={1}
                    total={messageInfo.totalElements}
                    onChange={onPageChange}
                  />
                )}
              </div>
            </Spin>
            {messageInfo?.totalElements === 0 &&
              renderEmpty(intl.get('hadm.marketclient.view.home.noNotification').d('暂无系统消息'))}
          </>
        ) : (
          renderEmpty(
            intl
              .get('hadm.marketclient.view.home.unlogin.notification')
              .d('您还未登录，登录后查看市场通知')
          )
        )}
      </div>
      <Modal
        width={660}
        title={
          <span>
            <img src={ImgModalTitle} alt="title" style={{ marginRight: '10px' }} />
            {renderFeedBackIntoHTML(messageDetail.subject)}
          </span>
        }
        visible={isShowDetailModal}
        footer={null}
        onCancel={onMessageDetailOk}
      >
        {renderContent()}
      </Modal>
    </div>
  );
};

export default formatterCollections({
  code: ['hadm.marketclient'],
})(Notification);
