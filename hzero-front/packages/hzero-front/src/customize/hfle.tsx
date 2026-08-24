/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/26
 * @copyright HAND ® 2019
 */

import React from 'react';

import intl from 'utils/intl';
import { componentMapCustomizeBuilder, FeatureMapStore } from 'utils/customize/helpers';

/**
 * LowCode 组件没有加载成功
 * @param componentCode
 * @returns {*}
 * @constructor
 */
const ComponentNotLoad: React.FC<{ componentCode: string }> = ({ componentCode }) => {
  const hzeroModule = 'hfle';
  return (
    <div>
      {intl
        .get('hzero.common.error.message.componentModuleMiss', {
          componentCode,
          service: hzeroModule,
        })
        .d(`<${componentCode} /> 组件加载失败, ${hzeroModule}服务未安装`)}
    </div>
  );
};

/**
 * LowCode 组件加载中
 * @returns {null}
 * @constructor
 */
const ComponentLoading = () => {
  return null;
};

interface SharedComponentProps {
  componentCode: string;
  componentProps: any;
}

interface CustomizeComponent {
  setComponent: (componentCode: string, factory: any) => FeatureMapStore;
  SharedComponent: (
    props: SharedComponentProps
  ) => React.FunctionComponentElement<React.SuspenseProps & any>;
}

const customizeComponent: CustomizeComponent = componentMapCustomizeBuilder('hlcd', 'component', {
  NotFound: ComponentNotLoad,
  Loading: ComponentLoading,
});

const { setComponent, SharedComponent } = customizeComponent;

export { setComponent, SharedComponent };

// type
