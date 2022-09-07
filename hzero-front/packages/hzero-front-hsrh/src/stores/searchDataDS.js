import intl from 'utils/intl';

const detailFormDS = () => ({
  fields: [
    {
      name: 'methodType',
      type: 'string',
      lookupCode: 'HSRH.REQUEST_TYPE',
      label: intl.get('hsrh.searchData.model.searchData.requireType').d('请求方式'),
    },
    {
      name: 'indexCodeSet',
      type: 'object',
      lovCode: 'HSRH.INDEX_VERSIONS_LIST',
      label: intl.get('hsrh.inquiryConfig.model.inquiryConfig.indexCodeSet').d('索引'),
      ignore: 'always',
      noCache: true,
    },
    {
      name: 'indexCode',
      type: 'string',
      bind: 'indexCodeSet.indexCode',
    },
    {
      name: 'requestUrl',
      type: 'string',
      label: intl.get('hsrh.searchData.model.searchData.requestUrl').d('请求URL'),
    },
    {
      name: 'requestBody',
      type: 'string',
      label: intl.get('hsrh.searchData.model.searchData.requestBody').d('请求参数'),
    },
    {
      name: 'respondParam',
      type: 'string',
      label: intl.get('hsrh.searchData.model.searchData.respondParam').d('返回参数'),
    },
  ],
  // transport: {
  //   submit: config => {
  //     const { data = [] } = config;
  //     const url = `${HZERO_HSRH}/v1/${organizationId}/request`;
  //     return {
  //       httpRequestDTO: data[0],
  //       url,
  //       method: 'POST',
  //     };
  //   },
  // },
});

export { detailFormDS };
