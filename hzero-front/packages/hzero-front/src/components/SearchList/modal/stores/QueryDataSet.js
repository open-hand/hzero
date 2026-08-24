import { DataSet } from 'choerodon-ui/pro';
import { map, filter } from 'lodash';
import { OPERATOR_TYPE } from '../../constant';

/**
 * 字段改变事件
 * @param record
 * @param name
 * @param value
 * @param fieldArr
 */
function handleFieldChange({ record, name, value }, fieldArr) {
  if (name === 'fieldName') {
    record.set('comparator', '');
    map(fieldArr, field => {
      if (value === field.value) {
        record.getField('comparator').set(
          'options',
          new DataSet({
            selection: 'single',
            data: filter(
              OPERATOR_TYPE[field.type.toUpperCase()],
              type => type.value.indexOf('NULL') === -1
            ),
          })
        );
      }
    });
  }
}

export default fieldArr => ({
  paging: false,
  fields: [
    {
      name: 'fieldName',
      type: 'string',
      label: '字段',
      unique: 'group',
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: fieldArr,
      }),
      required: true,
    },
    {
      name: 'comparator',
      type: 'string',
      label: '比较符',
      unique: 'group',
      required: true,
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: OPERATOR_TYPE.ALL,
      }),
    },
    {
      name: 'searchQueryId',
      type: 'number',
    },
  ],
  events: {
    update: props => handleFieldChange(props, fieldArr),
  },
});
