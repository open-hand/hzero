import uuid from 'uuid/v4';

import DataType from '../DataType';

export const dynamicTablePropTypes = [
  {
    attributeName: 'col',
    attributeType: DataType.Number,
    getDefaultValue: () => 2,
  },
  {
    attributeName: 'templateCode',
    attributeType: DataType.String,
    getDefaultValue: () => uuid(),
  },
];

export const dynamicTabsPropTypes = [
  {
    attributeName: 'templateCode',
    attributeType: DataType.String,
    getDefaultValue: () => uuid(),
  },
];
