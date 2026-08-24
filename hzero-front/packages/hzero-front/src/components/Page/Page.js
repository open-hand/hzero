import React from 'react';
import classNames from 'classnames';

const Page = props => {
  const classString = classNames('page-container', props.className);
  return <div {...props} className={classString} />;
};

export default Page;
