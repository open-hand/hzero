/**
 * @Author: zhengmin.liang <zhengmin.liang@hand-china.com>
 * @Create time: 2019/11/29
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 禁用Lov可触发组件，传入openLogic方法来判断是否可以打开lov弹窗
 */
import React, { useRef } from 'react';
import { Lov, Button } from 'choerodon-ui/pro';

const TriggeredDisableLov = props => {
  const { children, openLogic, ...otherProps } = props;
  const { color, icon } = otherProps;
  const lovEl = useRef(null);
  const showLov = () => {
    if (openLogic()) {
      const { current } = lovEl;
      if (current) current.openModal();
    }
  };
  return (
    <>
      <Button onClick={showLov} color={color} icon={icon}>
        {children}
      </Button>
      <Lov hidden ref={lovEl} {...otherProps} />
    </>
  );
};

export default TriggeredDisableLov;
