/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/20
 * @copyright HAND ® 2019
 */
export interface MenuItem {
  type: string; // dir root menu link inner-link and so on
  children: any[]; // all is array, and only three level
  id: number; // all menu should have id, remain id
  path?: string; // only menu have path
  parent?: any; // root don't have parent
  name: string; // menu's name
  icon?: string; // root menu have icon
  key: string; // generator for all menu, uniq
  pathToRegexp?: RegExp; // for route match
  quickIndex?: string;
}

export interface OriginMenu {
  name: string;
  icon?: string;
  path?: string;
  id: number;
  parentId?: number;
  quickIndex?: string; // not required on menu
  type?: string; // low-code 没有type
  children: any[];
}
