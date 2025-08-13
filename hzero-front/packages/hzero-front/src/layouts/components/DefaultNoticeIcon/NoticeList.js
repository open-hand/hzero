import React from 'react';
import { List } from 'hzero-ui';
import classNames from 'classnames';
import styles from './NoticeList.less';

export default function NoticeList({
  data = [],
  onClick,
  emptyImage,
  contentTitleAction,
  contentItemAction, // 进入消息列表
}) {
  let content;

  if (data.length === 0) {
    content = (
      <div className={styles.notFound}>
        <img src={emptyImage} alt="not found" />
      </div>
    );
  } else {
    content = (
      <List className={styles.list}>
        {data.map((item, i) => {
          const itemCls = classNames(styles.item, {
            [styles.read]: item.read,
          });
          return (
            <List.Item className={itemCls} key={item.key || i} onClick={() => onClick(item)}>
              <List.Item.Meta
                className={styles.meta}
                // avatar={item.avatar ? <Avatar className={styles.avatar} src={item.avatar} /> : null}
                title={<div className={styles.title}>{item.title}</div>}
                description={
                  <div className={styles.description}>
                    <div>{item.userMessageTypeMeaning}</div>
                    <div>{item.datetime}</div>
                  </div>
                }
                onClick={e => contentItemAction(e)}
              />
            </List.Item>
          );
        })}
      </List>
    );
  }
  const title = (
    <div className={styles.content}>
      <span className={styles['content-action']}>{contentTitleAction}</span>
    </div>
  );
  return (
    <div>
      {content}
      {title}
    </div>
  );
}
