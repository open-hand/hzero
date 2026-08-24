/**
 * 页面路由配置项
 */
export interface RouterConfigItem {
  /**
   * 路由 pathname
   */
  path: string;
  /**
   * 路由 key
   */
  key?: string;
  /**
   * React页面组件
   */
  component: string | any;
  /**
   * 页面组件依赖的 dva Models 可以指定多个。
   */
  models?: string[] | any[];
  /**
   * 如果没有菜单时，还想要调试页面，必须加下面两个配置，才能不会 404
   */
  authorized?: boolean;
  exact?: boolean;
  /**
   * 标题
   */
  title?: string;
  /**
   * 优先级, 如果有两个 routeConfig 的 path 一样， 优先级高的配置会覆盖优先级低的配置
   */
  priority?: number;
  /**
   * 页面类型， 默认为值 react;  可选值: 'react' | 'vue'
   */
  type?: 'react' | 'vue';
}

/**
 * 路由列表配置项
 */
export interface RoutersConfigItem {
  /**
   * 父路由 pathname
   */
  path: string;
  exact?: boolean;
  /**
   * 路由 key
   */
  key?: string;
  /**
   * 子路由集和
   */
  components: RouterConfigItem[];
  /**
   * 如果没有菜单时，还想要调试页面，必须加下面两个配置，才能不会 404
   */
  authorized?: boolean;
  /**
   * 标题
   */
  title?: string;
}

/**
 * 路由配置项
 */
// @ts-ignore
export type RouterConfig = RouterConfigItem | RoutersConfigItem;

/**
 * 路由配置
 */
// @ts-ignore
export type RoutersConfig = RouterConfig[];
