import React from 'react';
import { List } from 'hzero-ui';
import classNames from 'classnames';
import styles from './NoticeList.less';

export default function NoticeList({
  data = [],
  onClick,
  locale,
  emptyText,
  emptyImage,
  contentTitle, // 标题
  contentTitleAction, // 进入消息列表
}) {
  let content;

  if (data.length === 0) {
    content = (
      <div className={styles.notFound}>
        {emptyImage ? <img src={emptyImage} alt="not found" /> : null}
        <div>{emptyText || locale.emptyText}</div>
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
                title={
                  <div className={styles.title}>
                    {item.title}
                    <div className={styles.extra}>{item.extra}</div>
                  </div>
                }
                description={
                  <div>
                    <div className={styles.description} title={item.description}>
                      {item.description}
                    </div>
                    <div className={styles.datetime}>{item.datetime}</div>
                  </div>
                }
              />
            </List.Item>
          );
        })}
      </List>
    );
  }
  const title = (
    <div className={styles.content}>
      <span className={styles['content-title']}>{contentTitle}</span>
      <span className={styles['content-action']}>{contentTitleAction}</span>
    </div>
  );
  return (
    <div>
      {title}
      {content}
    </div>
  );
}
