// /**
//  * 后端传过来的组件类型
//  * @type {{DATE: string, NUMBER: string, LOV: string, DATETIME: string, LOOKUP: string, STRING: string, BOOLEAN: string}}
//  */
// export const COMPONENT_TYPE = {
//   STRING: 'string',
//   BOOLEAN: 'boolean',
//   NUMBER: 'number',
//   DATE: 'date',
//   TIME: 'time',
//   DATETIME: 'datetime',
//   LOV: 'lov',
//   LOOKUP: 'lookup',
// };

/**
 * 比较符
 * @type {{}}
 */
export const OPERATOR = {
  EQUAL: { value: 'EQUAL', meaning: '等于' },
  NOT_EQUAL: { value: 'NOT_EQUAL', meaning: '不等于' },
  BETWEEN: { value: 'BETWEEN', meaning: '介于' },
  GREATER_THAN: { value: 'GREATER_THAN', meaning: '大于' },
  GREATER_THAN_OR_EQUAL_TO: { value: 'GREATER_THAN_OR_EQUAL_TO', meaning: '大于等于' },
  LESS_THAN: { value: 'LESS_THAN', meaning: '小于' },
  LESS_THAN_OR_EQUAL_TO: { value: 'LESS_THAN_OR_EQUAL_TO', meaning: '小于等于' },
  IN: { value: 'IN', meaning: '包含于' },
  NOT_IN: { value: 'NOT_IN', meaning: '不包含于' },
  IS_NULL: { value: 'IS_NULL', meaning: '为空' },
  IS_NOT_NULL: { value: 'IS_NOT_NULL', meaning: '不为空' },
  START_WITH: { value: 'START_WITH', meaning: '以此开始' },
  NOT_START_WITH: { value: 'NOT_START_WITH', meaning: '不以此开始' },
  END_WITH: { value: 'END_WITH', meaning: '以此结束' },
  NOT_END_WITH: { value: 'NOT_END_WITH', meaning: '不以此结束' },
  LIKE: { value: 'LIKE', meaning: '相似于' },
  NOT_LIKE: { value: 'NOT_LIKE', meaning: '不相似于' },
};

/**
 * 操作列表
 * @type {{DATE: *[], NUMBER: *[], LOV: *[], DATETIME: *[], LOOKUP: *[], STRING: *[], BOOLEAN: *[]}}
 */
export const OPERATOR_TYPE = {
  // 等于、不等于、相似于、不相似于、空、非空
  // 多选：包含于、不包含于、空、非空
  STRING: [
    OPERATOR.EQUAL,
    OPERATOR.NOT_EQUAL,
    OPERATOR.LIKE,
    OPERATOR.NOT_LIKE,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、不等于、相似于、不相似于、空、非空
  // 多选：包含于、不包含于、空、非空
  INTL: [
    OPERATOR.EQUAL,
    OPERATOR.NOT_EQUAL,
    OPERATOR.LIKE,
    OPERATOR.NOT_LIKE,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、不等于
  BOOLEAN: [OPERATOR.EQUAL, OPERATOR.NOT_EQUAL],
  // 等于、不等于、大于、大于等于、小于、小于等于、空、非空
  NUMBER: [
    OPERATOR.EQUAL,
    OPERATOR.NOT_EQUAL,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、介于、大于、大于等于、小于、小于等于、空、非空
  DATE: [
    OPERATOR.EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、介于、大于、大于等于、小于、小于等于、空、非空
  YEAR: [
    OPERATOR.EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、介于、大于、大于等于、小于、小于等于、空、非空
  MONTH: [
    OPERATOR.EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、介于、大于、大于等于、小于、小于等于、空、非空
  WEEK: [
    OPERATOR.EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、大于、大于等于、小于、小于等于、空、非空
  TIME: [
    OPERATOR.EQUAL,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、介于、大于、大于等于、小于、小于等于、空、非空
  DATETIME: [
    OPERATOR.EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
  ],
  // 等于、不等于、空、非空
  // 多选：包含于、不包含于、空、非空
  LOV: [OPERATOR.EQUAL, OPERATOR.NOT_EQUAL, OPERATOR.IS_NULL, OPERATOR.IS_NOT_NULL],
  // 等于、不等于、空、非空
  // 多选：包含于、不包含于、空、非空
  LOOKUP: [OPERATOR.EQUAL, OPERATOR.NOT_EQUAL, OPERATOR.IS_NULL, OPERATOR.IS_NOT_NULL],
  ALL: [
    OPERATOR.EQUAL,
    OPERATOR.NOT_EQUAL,
    // OPERATOR.BETWEEN,
    OPERATOR.GREATER_THAN,
    OPERATOR.GREATER_THAN_OR_EQUAL_TO,
    OPERATOR.LESS_THAN,
    OPERATOR.LESS_THAN_OR_EQUAL_TO,
    OPERATOR.IN,
    OPERATOR.NOT_IN,
    OPERATOR.IS_NULL,
    OPERATOR.IS_NOT_NULL,
    OPERATOR.START_WITH,
    OPERATOR.NOT_START_WITH,
    OPERATOR.END_WITH,
    OPERATOR.NOT_END_WITH,
    OPERATOR.LIKE,
    OPERATOR.NOT_LIKE,
  ],
};
