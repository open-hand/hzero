import { IHzeroConfig as IHzeroBootConfig } from 'hzero-boot';

export interface IHzeroConfig extends IHzeroBootConfig {
  /**
   * hzero 默认布局 Header 上面的额外组件
   */
  layoutExtraHeader: () =>
    | React.FunctionComponentElement<any>
    | React.FunctionComponentElement<any>;

  /**
   * 请求后端接口时，额外附带的 headers
   */
  patchRequestHeader: () => any;

  /**
   * 设置 cookie token 时额外的 cookie 参数
   */
  patchToken: () => any;
}
