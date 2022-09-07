/**
 * 导入服务
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2020/1/13
 * @copyright HAND ® 2020
 */

import React from 'react';

import { componentMapCustomizeBuilder, FeatureMapStore } from 'utils/customize/helpers';
import intl from 'utils/intl';

/**
 * Himp 组件没有加载成功
 * @param componentCode
 * @returns {*}
 * @constructor
 */
const ComponentNotLoad: React.FC<{ componentCode: string }> = ({ componentCode }) => {
  const hzeroModule = 'himp';
  return React.createElement(
    'div',
    {},
    intl
      .get('hzero.common.error.message.componentModuleMiss', {
        componentCode,
        service: hzeroModule,
      })
      .d(`<${componentCode} /> 组件加载失败, ${hzeroModule}服务未安装`)
  );
};

/**
 * Himp 组件加载中
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
const customizeComponent: CustomizeComponent = componentMapCustomizeBuilder('himp', 'component', {
  NotFound: ComponentNotLoad,
  Loading: ComponentLoading,
});

const { setComponent, SharedComponent } = customizeComponent;

export { setComponent, SharedComponent };
