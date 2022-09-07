import { DataSet } from 'choerodon-ui/pro';

const typeOptionDs = new DataSet({
  selection: 'single',
  data: [
    {
      value: 'asc',
      meaning: '升序',
    },
    {
      value: 'desc',
      meaning: '降序',
    },
  ],
});

export default fieldArr => ({
  paging: false,
  fields: [
    {
      name: 'fieldName',
      type: 'string',
      label: '字段',
      unique: true,
      options: new DataSet({
        selection: 'single',
        paging: false,
        data: fieldArr,
      }),
      required: true,
    },
    { name: 'direction', type: 'string', label: '类型', options: typeOptionDs, required: true },
    { name: 'searchOrderId', type: 'number' },
  ],
});
