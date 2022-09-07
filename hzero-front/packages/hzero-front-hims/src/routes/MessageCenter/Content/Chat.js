import React from 'react';
import { Bind } from 'lodash-decorators';
import moment from 'moment';
import { connect } from 'dva';
import { Avatar, Col, Icon, Row, Spin, Checkbox, Button, Popover } from 'hzero-ui';
import { Scrollbars } from 'react-custom-scrollbars';
import axios from 'axios';

import intl from 'utils/intl';
import { HZERO_FILE, HZERO_IM, BKT_HIMS, BKT_PUBLIC, API_HOST } from 'utils/config';
import { getRequestId } from 'utils/utils';
import notification from 'utils/notification';

import styles from './index.less';

@connect(({ user }) => ({
  user,
}))
export default class Chat extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      checkedData: [],
      aReg: new RegExp(
        /<a([\s]+|[\s]+[^<>]+[\s]+)href=("([^<>"']*)"|'([^<>"']*)')[^<>]*>([\s\S]*)(<\/a>)/
      ),
      imgReg: new RegExp(/<img\b.*?(?:>|\/>)/),
      confirmLoading: false,
    };
    this.scrollbars = React.createRef();
  }

  /**
   * 拉取消息时对滚动条处理操作
   */
  componentDidUpdate() {
    if (this.scrollbars.current) {
      if (this.props.messageListDs.isScrollToBottom) {
        this.scrollHeight = this.scrollbars.current.getScrollHeight();
        this.scrollbars.current.scrollToBottom();
      }
    }
  }

  @Bind()
  handleSelect(item, checked) {
    const { checkedData } = this.state;
    const arr = checkedData;
    if (checked) {
      arr.push(item);
      this.setState({ checkedData: arr });
    } else {
      this.setState({
        checkedData: arr.filter((temp) => temp.id !== item.id),
      });
    }
  }

  @Bind()
  handleScroll() {
    const { messageList, messageListDs, record, groupKey, onUpdate } = this.props;
    if (this.scrollbars.current) {
      // 滚动到顶部时才加载数据
      if (this.scrollbars.current.getScrollTop() === 0) {
        const { id: endId } = messageList[0] || {};
        messageListDs.setQueryParameter('userId', record.get('id'));
        messageListDs.setQueryParameter('groupKey', groupKey);
        messageListDs.setQueryParameter('endId', endId);
        messageListDs.query().then(() => {
          messageListDs.isScrollToBottom = false;
          onUpdate(messageListDs.toData().reverse());
        });
      }
    }
  }

  /**
   * 处理消息内容的显示
   * @param {string} content - 聊天的消息内容
   */
  @Bind()
  renderChatContent(content) {
    if (content) {
      const requestId = getRequestId();
      const { accessToken, organizationId, onPreview } = this.props;
      const reg = new RegExp(/<img\b.*?(?:>|\/>)/);
      const reg2 = new RegExp(
        /<a([\s]+|[\s]+[^<>]+[\s]+)href=("([^<>"']*)"|'([^<>"']*)')[^<>]*>([\s\S]*)(<\/a>)/
      );
      if (content.includes('[emoji')) {
        const emojiList = [
          'emoji1',
          'emoji2',
          'emoji3',
          'emoji4',
          'emoji5',
          'emoji6',
          'emoji7',
          'emoji8',
          'emoji9',
          'emoji10',
          'emoji11',
          'emoji12',
          'emoji13',
          'emoji14',
          'emoji15',
          'emoji16',
          'emoji17',
          'emoji18',
          'emoji19',
          'emoji20',
        ];
        let con = content;
        emojiList.forEach((item) => {
          if (content.includes(item)) {
            con = con.replace(`[${item}]`, `<span class="${item}"></span>`);
          }
        });
        return <div dangerouslySetInnerHTML={{ __html: con }} />;
      } else if (reg.test(content)) {
        const url = content.match(/(http|https)[^"]+/)
          ? content.match(/(http|https)[^"]+/)[0]
          : undefined;
        return (
          <img
            onLoad={() => {
              if (
                this.scrollHeight !== this.scrollbars.current.getScrollHeight() &&
                this.props.messageListDs.isScrollToBottom
              ) {
                this.scrollHeight = this.scrollbars.current.getScrollHeight();
                this.scrollbars.current.scrollToBottom();
              }
            }}
            onClick={() => {
              onPreview(
                `${API_HOST}/hfle/v1/${organizationId}/files/redirect-url?bucketName=hims&H-Request-Id=${requestId}&access_token=${accessToken}&url=${encodeURIComponent(
                  url
                )}`
              );
            }}
            style={{ cursor: 'pointer', width: '100%' }}
            src={`${API_HOST}/hfle/v1/${organizationId}/files/redirect-url?bucketName=${BKT_HIMS}&H-Request-Id=${requestId}&access_token=${accessToken}&url=${encodeURIComponent(
              url
            )}`}
            alt=""
          />
        );
      } else if (reg2.test(content)) {
        const con = content.replace(
          /(http|https)[^"]+/,
          `${API_HOST}/hfle/v1/${organizationId}/files/download?bucketName=${BKT_HIMS}&directory=hims01&H-Request-Id=${requestId}&access_token=${accessToken}&url=$&`
        );

        return <div dangerouslySetInnerHTML={{ __html: con }} />;
      } else if (content.search(/(http|https)[^"]+/) !== -1) {
        const con = content.replace(/(http|https)[^"]+/, '<a href="$&" target="_blank">$&</a>');
        return <div dangerouslySetInnerHTML={{ __html: con }} />;
      } else {
        return content;
      }
    }
  }

  @Bind()
  handleCopy(item, resolve) {
    const { organizationId } = this.props;

    const url = item.content.match(
      /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
    )
      ? item.content.match(
          /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/
        )[0]
      : undefined;
    return axios({
      url: `${HZERO_FILE}/v1/${organizationId}/files/copy-by-url`,
      method: 'POST',
      params: {
        url,
        bucketName: BKT_HIMS,
        destinationBucketName: BKT_PUBLIC,
      },
    })
      .then((res) => {
        if (res) {
          const content = item.content.replace(
            /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])+([\S]+[.]*[\w\-\\@?^=%&/~\\+#])/,
            res
          );
          resolve(content);
        }
      })
      .catch((err) => {
        if (err) {
          notification.error({ message: err.message });
        }
        resolve();
      });
  }

  @Bind()
  async handleOpenModal() {
    const { onOpenModal } = this.props;
    const { checkedData = [], imgReg } = this.state;
    const arr = [];
    const newCheckedData = [];
    this.setState({ confirmLoading: true });
    let flag = true;
    await Promise.all(
      checkedData
        .map((item) => {
          if (item && item.content && flag) {
            if (imgReg.test(item.content)) {
              return new Promise((resolve) => {
                this.handleCopy(item, resolve);
              }).then((res) => {
                if (res) {
                  arr.push({ ...item, content: res });
                  newCheckedData.push(item);
                } else {
                  arr.push({ ...item, content: res, error: 'error' });
                  newCheckedData.push({ ...item, error: 'error' });
                  flag = false;
                }
              });
            }
            arr.push(item);
            newCheckedData.push(item);
            return undefined;
          }
          newCheckedData.push(item);
          return undefined;
        })
        .filter((temp) => temp)
    ).then(() => {
      if (arr.every((item) => item.content) && flag) {
        onOpenModal(arr);
      }
      this.setState({ confirmLoading: false, checkedData: newCheckedData });
    });
  }

  @Bind()
  handleRetract(item) {
    const { messageList, onChange = (e) => e } = this.props;
    axios({
      url: `${HZERO_IM}/v1/message/retract`,
      method: 'POST',
      params: {
        messageId: item.id,
      },
    })
      .then((res) => {
        if (res) {
          const arr = messageList.filter((temp) => temp.id !== item.id);
          onChange(arr);
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  render() {
    const { checkedData = [], aReg, confirmLoading } = this.state;
    const {
      fetchMessageListLoading,
      id,
      isSelected: flag,
      messageList = [],
      record,
      defaultStatus,
      user: {
        currentUser: { imageUrl },
      },
    } = this.props;
    const isSelected = flag && messageList.length > 0;
    const closedStyle = isSelected
      ? { height: '450px', width: '100%' }
      : { height: '500px', width: '100%' };
    return (
      <div className={styles['message-content-group']}>
        <Spin
          wrapperClassName={styles['group-loading']}
          spinning={fetchMessageListLoading}
          indicator={<Icon type="loading" spin />}
        >
          <Row type="flex" justify="space-between" style={{ height: '100%' }}>
            <Col span={24} className={styles.message}>
              <Scrollbars
                onScroll={this.handleScroll}
                style={
                  defaultStatus === 'closed' ? closedStyle : { height: '350px', width: '100%' }
                }
                ref={this.scrollbars}
              >
                {messageList.map((item, index, arr) => {
                  // 本人的消息信息展示
                  if (item && (item.from === id.toString() || item.from === id)) {
                    return (
                      <>
                        {arr.length % index === 0 && (
                          <Row type="flex" justify="center">
                            <Col style={{ color: 'rgb(207,206,206)' }}>
                              {moment(item.createAt).format(
                                intl.get('hims.messageCenter.view.chat.time').d('MM月DD日 HH:mm')
                              )}
                            </Col>
                          </Row>
                        )}
                        <Row
                          type="flex"
                          justify="end"
                          style={
                            // eslint-disable-next-line no-nested-ternary
                            checkedData.some((temp) => temp.id === item.id)
                              ? checkedData.some((temp) => temp.id === item.id && temp.error)
                                ? { margin: '9px', padding: '3px', background: 'rgb(236, 61, 61)' }
                                : { margin: '9px', padding: '3px', background: '#d3d8e4' }
                              : { margin: '9px', padding: '3px' }
                          }
                          key={item.id}
                        >
                          {isSelected && (
                            <Col
                              span={12}
                              style={{ display: 'flex', justifyContent: 'flex-start' }}
                            >
                              <Checkbox
                                disabled={aReg.test(item.content)}
                                onChange={(e) => {
                                  this.handleSelect(item, e.target.checked);
                                }}
                              />
                            </Col>
                          )}

                          <Col span={12} style={{ display: 'flex', justifyContent: 'flex-end' }}>
                            {!isSelected ? (
                              <Popover
                                title={null}
                                trigger="contextMenu"
                                placement="right"
                                style={{ cursor: 'pointer' }}
                                content={
                                  <>
                                    <a onClick={() => this.handleRetract(item)}>
                                      {intl.get('hzero.common.status.revoke').d('撤销')}
                                    </a>
                                  </>
                                }
                              >
                                <span
                                  className={`${styles['user-msg']} ${styles['friend-user-msg']}`}
                                >
                                  {this.renderChatContent(item.content)}
                                </span>
                              </Popover>
                            ) : (
                              <span
                                className={`${styles['user-msg']} ${styles['friend-user-msg']}`}
                              >
                                {this.renderChatContent(item.content)}
                              </span>
                            )}

                            <div>
                              <Avatar src={imageUrl} size="middle" icon="user" />
                            </div>
                          </Col>
                        </Row>
                      </>
                    );
                  } else {
                    // 好友的聊天信息展示
                    return (
                      <>
                        {arr.length % index === 0 && (
                          <Row type="flex" justify="center">
                            <Col style={{ color: 'rgb(207,206,206)' }}>
                              {moment(item.createAt).format(
                                intl.get('hims.messageCenter.view.chat.time').d('MM月DD日 HH:mm')
                              )}
                            </Col>
                          </Row>
                        )}
                        <Row
                          // type="flex"
                          // justify="space-between"
                          style={
                            checkedData.some((temp) => temp.id === item.id)
                              ? { margin: '9px', padding: '3px', background: '#d3d8e4' }
                              : { margin: '9px', padding: '3px' }
                          }
                          key={item.id}
                        >
                          {isSelected && (
                            <Col span={1} style={{ display: 'flex', justifyContent: 'flex-start' }}>
                              <Checkbox
                                disabled={aReg.test(item.content)}
                                onChange={(e) => {
                                  this.handleSelect(item, e.target.checked);
                                }}
                              />
                            </Col>
                          )}
                          <Col span={12} style={{ display: 'flex', justifyContent: 'flex-start' }}>
                            <div>
                              <Avatar src={record.get('userAvatar')} size="middle" icon="user" />
                            </div>

                            <span className={`${styles['chat-msg']} ${styles['friend-chat-msg']}`}>
                              {this.renderChatContent(item.content)}
                            </span>
                          </Col>
                        </Row>
                      </>
                    );
                  }
                })}
              </Scrollbars>
            </Col>
            {isSelected && (
              <Button
                loading={confirmLoading}
                onClick={() => {
                  this.handleOpenModal();
                }}
                className={styles['base-confirm-btn']}
              >
                {intl.get('hzero.common.button.ok').d('确定')}
              </Button>
            )}
          </Row>
        </Spin>
      </div>
    );
  }
}
