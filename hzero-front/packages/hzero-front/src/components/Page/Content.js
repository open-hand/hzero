/**
 * 工作区 Conent
 *
 * @date: 2018-6-30
 * @author: niujiaqing <njq.niu@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React from 'react';
import classNames from 'classnames';

const PageContent = props => {
  const {
    title,
    description,
    children,
    style,
    wrapperStyle,
    wrapperClassName, // 包裹的className
    className, // 真正的 Content 的样式
    noCard = false,
  } = props;
  const classString = classNames('page-content-wrap', wrapperClassName, {
    'page-content-wrap-no-card': noCard,
  });
  const contentClassString = classNames('page-content', className);
  return (
    <div className={classString} style={wrapperStyle}>
      <div className={contentClassString} style={style}>
        {title || description ? (
          <div className="page-content-header" key="page-content-header">
            <div className="title">{title}</div>
            <div className="description">{description}</div>
          </div>
        ) : null}
        <React.Fragment key="page-content-content">{children}</React.Fragment>
      </div>
    </div>
  );
};

export default PageContent;
