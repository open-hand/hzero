/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import React from 'react';

import { getClassName as getCommonLayoutClassName } from '../../utils';

import Content from './components/Content';

interface ContentWrapProps {
  components: {};
  getClassName?: (cls: string) => string;
}

const ContentWrap: React.FC<ContentWrapProps> = ({ getClassName = getCommonLayoutClassName }) => {
  return (
    <div className={getClassName('content-wrap')}>
      <Content components={{}} />
    </div>
  );
};

export default ContentWrap;
