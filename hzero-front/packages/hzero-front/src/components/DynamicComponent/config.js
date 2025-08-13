// context 属性的开头
export const contextPrefix = 'this.';

// Form 和 Table Toolbar 独有的
// 字段名称的key
export const fieldNameProp = 'fieldName';
// 字段描述的key
export const fieldLabelProp = 'fieldLabel';

// 属性使用 对象
// // 属性名称的key
// export const attributeNameProp = 'name';
// // 属性值的key
// export const attributeValueProp = 'value';

// 组件类型的key
export const componentTypeProp = 'componentType';
// 子组件的key
export const childrenProp = 'children';

export const dynamicComponentOmitProps = ['code', 'template'];

export const dynamicComponentOmitConfigProps = ['templateCode', 'templateType', 'props'];

export const composeFormOmitProps = [
  'editable',
  'editType',
  'form',
  'dataSource',
  'loading',
  'layout',
  'col',
  'context',
  'disableStyle',
  'fieldLabelWidth',
  'onRef',
  'fields',
  'componentType',
  'submit',
  'submitUrl',
  'reset',
  'queryUrl',
  'rowKey',
  // tpl 公共的属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

export const dynamicTableOmitProps = [
  'scroll',
  'addable',
  'removable',
  'onAdd',
  'onRemove',
  'pagination',
  'rowSelection',
  'onRef',
  'dataSource',
  'loadUrl',
  // tpl 公共的属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

export const dynamicToolBarOmitProps = [
  'onRef',
  // tpl 公共的属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

export const searchFormOmitProps = [
  'onRef',
  'onSearch',
  'searchBtnText',
  'resetBtnText',
  'className',
  // 通用的 omit 属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

export const dynamicModalOmitProps = [
  'type',
  'fields',
  'beforeShow',
  'show',
  'afterShow',
  'beforeHide',
  'hide',
  'afterHide',
  'onOk',
  'afterOk',
  // 通用的 omit 属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

export const dynamicTabsOmitProps = [
  'components',
  'fields',
  // 通用的 omit 属性
  'enabledFlag',
  'templateType',
  'childNode',
  'rootNode',
  'orderSeq',
  'templateId',
];

// 有些组件的属性需要在运行时确定

/**
 * 所有Form自身处理的属性
 * @type {Array<string>}
 */
export const contextOmitDynamicFormProps = [];

/**
 * 所有Toolbar自身处理的属性
 * @type {Array<string>}
 */
export const contextOmitDynamicToolbarProps = [];

/**
 * 所有Table自身处理的属性
 * @type {Array<string>}
 */
export const contextOmitDynamicTableProps = ['queryForm'];

// 按钮相关的配置

// 按钮动作类型的编码
export const ACTION_CODE = {
  page: 'page',
  action: 'action',
};

// 按钮动作的描述
export const clickActionOptions = [
  { value: ACTION_CODE.page, meaning: '打开页面' },
  { value: ACTION_CODE.action, meaning: '执行动作' },
];

// 打开页面的方式
export const ACTION_PAGE_TYPE = {
  open: 'open', // 页面跳转
  modal: 'modal', // 模态框(弹出)
  drawer: 'drawer', // 模态框(弹出)
};

// 打开页面方式描述
export const openPageOptions = [
  { value: ACTION_PAGE_TYPE.open, meaning: '跳转' },
  { value: ACTION_PAGE_TYPE.modal, meaning: '模态框(弹出)' },
  { value: ACTION_PAGE_TYPE.drawer, meaning: '模态框(滑出)' },
];

// 打开页面带来的级联选项
// drawer 使用写死的逻辑
// const PAGE_TYPE_DRAWER = {
//   520: 520,
//   1000: 1000,
// };
// const PAGE_TYPE_DRAWER = [
//   {value: '520', meaning: '520'},
//   {value: '1000', meaning: '1000'},
// ];

export const PAGE_TYPE_MODAL = {
  w1: 'w1',
  w2: 'w2',
  w3: 'w3',
  w4: 'w4',
};
// 打开页面带来的级联选项描述
export const pageTypeModalOptions = [
  { value: PAGE_TYPE_MODAL.w1, meaning: '720*360' },
  { value: PAGE_TYPE_MODAL.w2, meaning: '760*600' },
  { value: PAGE_TYPE_MODAL.w3, meaning: '860*600' },
  { value: PAGE_TYPE_MODAL.w4, meaning: '960*600' },
];

/**
 * todo
 * 公共的参数类型 和 通过extraParams传入的参数类型
 * 所有的 页面参数都需要在 这里记录
 * fixParam     'a' 固定参数
 * urlParam     'b' 页面参数
 * columnParam  'c' 列参数       // table传入
 */

// 参数
export const PAGE_PARAM = {
  fixParam: 'a',
  urlParam: 'b',
};
// 参数
export const pageParamOptions = [
  { value: PAGE_PARAM.fixParam, meaning: '固定参数' },
  { value: PAGE_PARAM.urlParam, meaning: '页面参数' },
];

export const paramSep = ','; // 参数的分割线
export const modalBtnSep = ','; // Modal 按钮的分割线
export const subEventSep = ','; // 订阅事件的分隔线

export const modalBtnPrefix = '[modalBtns]'; // btnName,btnType,btnEvent
export const subEventPrefix = '[subEvents]'; // subEventListen,subEventAction

// 按钮类型
export const btnTypeOptions = [
  { value: 'primary', meaning: '主要' },
  { value: '', meaning: '不重要' },
  { value: 'danger', meaning: '危险' },
];

// 模态框的订阅事件
export const modalSubEvents = [{ value: 'afterClose', meaning: 'Modal关闭后' }];

// 查询请求的模板字符 eg: {organizationId}
export const queryUrlInterpolate = /{([\s\S]+?)}/g;
