import React, { useState } from 'react';
import { Icon, Popover } from 'hzero-ui';
import styles from './index.less';

const LogTips = ({ content, text }) => {
  const [popoverTipsVisible, setPopoverTipsVisible] = useState(false);

  return (
    <Popover
      content={content}
      trigger="click"
      placement="bottom"
      getPopupContainer={(triggerNode) => triggerNode.parentElement}
      onVisibleChange={(visible) => setPopoverTipsVisible(visible)}
    >
      <span className={styles.tips} style={{ color: popoverTipsVisible ? '#ff8a2b' : '#8A909A' }}>
        <Icon
          type="question-circle-o"
          style={{
            color: popoverTipsVisible ? '#ff8a2b' : '#8A909A',
            marginRight: '4px',
          }}
        />
        {text}
      </span>
    </Popover>
  );
};

export default LogTips;
