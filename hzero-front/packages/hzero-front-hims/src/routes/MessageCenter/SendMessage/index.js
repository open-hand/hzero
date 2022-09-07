import React from 'react';
import { Bind } from 'lodash-decorators';
import { Input, Form, Button, message, Popover } from 'hzero-ui';
import axios from 'axios';
import notification from 'utils/notification';

import Upload from 'components/Upload/UploadButton';

import intl from 'utils/intl';
import { HZERO_FILE } from 'utils/config';

import styles from './index.less';

@Form.create({ fieldNameProp: null })
export default class SendMessage extends React.Component {
  state = { emojiVisible: false };

  // componentDidMount() {
  // document
  //   .querySelector(styles['message-center-send-message-area-fixed'])
  //   .addEventListener('paste', this.handleUpload, false);
  // }

  @Bind()
  handleUpload(e) {
    const { clipboardData } = e;
    let i = 0;
    let types = [];
    if (clipboardData) {
      const { items } = clipboardData;

      if (!items) {
        return;
      }

      let item = items[0];
      types = clipboardData.types || [];

      for (; i < types.length; i++) {
        if (types[i] === 'Files') {
          item = items[i];
          break;
        }
      }

      // 判断是否为图片
      if (item && item.kind === 'file' && item.type.match(/^image\//i)) {
        this.renderImg(item);
      }
    }
  }

  @Bind()
  renderImg(item) {
    const { organizationId } = this.props;

    const data = new FormData();
    const blob = item.getAsFile();

    data.append('bucketName', 'hims');
    data.append('directory', 'hims01');
    data.append('file', blob);
    axios({
      url: `${HZERO_FILE}/v1/${organizationId}/files/multipart`,
      method: 'POST',
      data,
    })
      .then((res) => {
        if (res) {
          const content = `<img src="${res.data}" alt="" class="im-chat-img" />`;
          this.handleSend();
          this.handleSend(content);
        }
      })
      .catch((err) => {
        notification.error({
          message: err.message,
        });
      });
  }

  @Bind()
  handleSend(content) {
    const {
      form,
      id, // 客服人员id（当前用户id）
      currentKey,
      groupKey,
      ws,
    } = this.props;
    let value = '';
    form.validateFields((err, values) => {
      // 内容输入不为空
      if ((!err && values.content) || content) {
        value = values.content;
        if (content) {
          value = content;
        }
        if (value.replace(/\n*/, '') !== '') {
          const obj = {
            from: id,
            to: currentKey,
            contentType: '0', // 内容类型，通知/聊天
            messageType: '1', // 消息类型，text(1)/image(xxx)
            chatType: '4', // 消息对象类型，用户(1)/群组(2)/临时会话(3)
            groupId: groupKey, // groupKey
            content: value,
          };
          try {
            ws.send(JSON.stringify(obj));
          } catch (evt) {
            // webSocket已断开连接
            message.error(
              intl.get('hims.messageCenter.view.message.error.notConnect').d('websocket 已断开连接')
            );
          }
          // 清空输入框
          if (!content) {
            form.resetFields();
          }
        }
      }
    });
  }

  @Bind()
  renderEmoji() {
    const emojis = [];
    for (let i = 1; i <= 20; i++) {
      emojis.push(<span onClick={() => this.addEmoji(i)} key={i} className={`emoji${i}`} />);
    }
    return <div className="emoji-wrapper">{emojis}</div>;
  }

  @Bind()
  handleEmojiVisible(visible) {
    this.setState({ emojiVisible: visible });
  }

  @Bind()
  addEmoji(data) {
    const { form } = this.props;
    const inputContent = form.getFieldValue('content') || '';
    const emoji = `[emoji${data}]`;
    form.setFieldsValue({ content: inputContent + emoji });
    this.setState({ emojiVisible: false });
  }

  render() {
    const { form, defaultStatus } = this.props;
    const { getFieldDecorator, getFieldValue } = form;
    const { emojiVisible } = this.state;
    return (
      <div className={styles['message-center-send-message']}>
        <div className={styles['message-bar']}>
          <Popover
            visible={emojiVisible}
            onVisibleChange={this.handleEmojiVisible}
            placement="topLeft"
            content={this.renderEmoji()}
            trigger="click"
          >
            <Button
              shape="circle"
              icon="smile-o"
              size="small"
              className={styles['message-bar-item']}
            />
          </Popover>
          <Upload
            name="file"
            accept="image/jpeg,image/png"
            showUploadList={false}
            fileType="image/jpeg,image/png"
            bucketDirectory="hims01"
            bucketName="hims"
            onUploadSuccess={(res) => {
              if (res) {
                const content = `<img src="${res.response}"  alt="" class="im-chat-img" />`;
                this.handleSend(content);
              }
            }}
          >
            <Button
              shape="circle"
              icon="picture"
              size="small"
              className={styles['message-bar-item']}
            />
          </Upload>
          <Upload
            name="file"
            bucketDirectory="hims01"
            bucketName="hims"
            showUploadList={false}
            onUploadSuccess={(res) => {
              if (res) {
                const content = `<div class="im-file-container"><div>${res.name}</div><div>${(
                  res.size /
                  (1024 * 1024)
                ).toFixed(2)}MB</div><a href="${
                  res.response
                }" alt="" target="_blank" class="im-file-download" >${intl
                  .get('hzero.common.button.download')
                  .d('下载')}</a></div>`;
                this.handleSend(content);
              }
            }}
          >
            <Button
              shape="circle"
              icon="file"
              size="small"
              className={styles['message-bar-item']}
            />
          </Upload>
        </div>

        <Form onSubmit={this.handleSend}>
          <Form.Item>
            {getFieldDecorator('content')(
              <Input.TextArea
                maxLength={200}
                autosize={false}
                rows={3}
                readOnly={defaultStatus === 'closed'}
                onKeyUp={(e) => {
                  if (e.keyCode === 13 && defaultStatus !== 'closed') {
                    this.handleSend();
                  }
                }}
                placeholder={intl
                  .get('hims.messageCenter.view.message.title.pleaseInput')
                  .d('请输入内容....')}
                className={styles['message-center-send-message-area']}
                onPaste={this.handleUpload}
              />
            )}
          </Form.Item>
        </Form>
        <Button
          className={styles['message-center-send-btn']}
          onClick={() => {
            this.handleSend();
          }}
          disabled={!getFieldValue('content') || defaultStatus === 'closed'}
          type="primary"
        >
          {intl.get('hims.message.view.message.title.enter').d('发送（Enter）')}
        </Button>
      </div>
    );
  }
}
