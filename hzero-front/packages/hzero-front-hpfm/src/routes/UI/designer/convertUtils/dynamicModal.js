/**
 * Extra: 存储数据    转化为  设计器数据
 * Parse: 设计器数据  转化为    存储数据
 */

import { noop } from './common';

const dynamicModal = {
  initExtra: noop,
  initExtraField: noop,
  dirtyExtra: noop,
  dirtyParse: noop,
};

export default dynamicModal;
