/**
 * 标签模版预览
 * @author WY <yang.wang06@hand-china.com>
 * @creationDate 2020/1/15
 * @copyright HAND ® 2020
 */

import React from 'react';
import { Spin } from 'choerodon-ui/pro';

import request from 'utils/request';
import { isTenantRoleLevel, getCurrentOrganizationId, getResponse } from 'utils/utils';
import { HZERO_RPT } from 'utils/config';

const isTenant = isTenantRoleLevel();
const organizationId = getCurrentOrganizationId();

const Preview = ({ labelTemplateCode, tenantId }) => {
  const [previewResult, setPreviewResult] = React.useState({});
  const [fetchResultLoading, setFetchResultLoading] = React.useState(false);
  React.useEffect(() => {
    setFetchResultLoading(true);
    request(
      isTenant
        ? `${HZERO_RPT}/v1/${organizationId}/label-prints/view`
        : `${HZERO_RPT}/v1/label-prints/view`,
      {
        method: 'GET',
        query: { labelTemplateCode, labelTenantId: isTenant ? '' : tenantId },
      }
    ).then((res) => {
      const data = getResponse(res);
      if (data) {
        setPreviewResult(data);
        setFetchResultLoading(false);
      }
    });
  }, [labelTemplateCode]);

  return (
    <Spin spinning={fetchResultLoading}>
      <div dangerouslySetInnerHTML={{ __html: previewResult.label }} />
    </Spin>
  );
};

export default Preview;
