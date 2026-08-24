import React from 'react';

import intl from 'utils/intl';
import { componentMapCustomizeBuilder } from 'utils/customize/helpers';

/**
 * LowCode 组件没有加载成功
 * @param componentCode
 * @returns {*}
 * @constructor
 */
function LowCodeComponentNotLoad({ componentCode }) {
  return (
    <div>
      {intl
        .get('hzero.common.error.message.lowCodeComponentLoadError', { componentCode })
        .d(`low-code <${componentCode} /> 组件加载失败`)}
    </div>
  );
}

/**
 * LowCode 组件加载中
 * @returns {null}
 * @constructor
 */
function LowCodeComponentLoading() {
  return null;
  // return (
  //   <div>
  //     {intl
  //       .get('hzero.common.error.message.componentLoading', { componentCode })
  //       .d(`low-code <${componentCode} /> 正在加载中`)}
  //   </div>
  // );
}

const { setComponent, SharedComponent } = componentMapCustomizeBuilder('hlcd', 'component', {
  NotFound: LowCodeComponentNotLoad,
  Loading: LowCodeComponentLoading,
});

export { setComponent, SharedComponent as LowCodeSharedComponent };
