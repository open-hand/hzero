import React from 'react';
import { Button, Tag } from 'hzero-ui';

import intl from 'utils/intl';

import { defaultGetHistoryClassName } from './utils';

const HistoryItem = ({ item, onClick, onCloseClick }) => {
  const eventStopPropagation = React.useCallback(event => {
    event.stopPropagation();
  }, []);
  const handleCloseClick = React.useCallback(() => {
    onCloseClick(item);
  }, [onCloseClick, item]);
  const handleClick = React.useCallback(() => {
    onClick(item);
  }, [onClick, item]);
  return item.name ? (
    <Tag
      closable
      onClick={handleClick}
      onClose={eventStopPropagation}
      afterClose={handleCloseClick}
    >
      {intl.get(item.name)}
    </Tag>
  ) : null;
};

const History = ({
  history = [],
  getClassName = defaultGetHistoryClassName,
  onClearBtnClick,
  onClearItem,
  onGotoHistory,
}) => {
  if (history.length > 0) {
    return (
      <div className={getClassName()}>
        <span className={getClassName('title')}>
          {intl.get('hzero.common.component.search').d('搜索历史')}:
          <Button ghost onClick={onClearBtnClick} className={getClassName('btn-clear')}>
            {intl.get('hzero.common.button.clean').d('清除')}
          </Button>
        </span>
        <ul className={getClassName('content')}>
          {history &&
            history.map(tab => (
              <HistoryItem
                key={tab.key}
                item={tab}
                onClick={onGotoHistory}
                onCloseClick={onClearItem}
              />
            ))}
        </ul>
      </div>
    );
  } else {
    return null;
  }
};

export default History;
