import { Button, Tag } from 'hzero-ui';
import React from 'react';

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

interface HistoryItemData {
  key: string;
}

interface HistoryProps {
  history: HistoryItemData[];
  getClassName?: (...clses: string[]) => string;
  onClearBtnClick: () => void;
  onClearItem: (item: HistoryProps) => void;
  onGotoHistory: (item: HistoryProps) => void;
}

const History: React.FC<HistoryProps> = ({
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
            history.map(tab => {
              return (
                <HistoryItem
                  key={tab.key}
                  item={tab}
                  onClick={onGotoHistory}
                  onCloseClick={onClearItem}
                />
              );
            })}
        </ul>
      </div>
    );
  }
  return null;
};

export default History;
