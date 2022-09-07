/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/29
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: <a />标签Lov组件
 * @UseExample:
 *   import TinyLov...
 *   <TinyLov name={...} dataSet={...}>注册</TinyLov>
 */
import React, { useRef } from 'react';
import { Lov } from 'choerodon-ui/pro';

const TinyLov = props => {
  const { children, ...otherProps } = props;
  const lovEl = useRef(null);
  const showLov = () => {
    const { current } = lovEl;
    if (current) current.openModal();
  };
  return (
    <>
      <a onClick={showLov}>{children}</a>
      <Lov hidden ref={lovEl} {...otherProps} />
    </>
  );
};

export default TinyLov;
