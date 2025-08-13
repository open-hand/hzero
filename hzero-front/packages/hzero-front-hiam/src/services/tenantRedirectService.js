import { HZERO_IAM, VERSION_IS_OP } from 'utils/config';
import request from 'utils/request';

export async function tenantRedirect(params) {
  const reqUrl = VERSION_IS_OP
    ? `${HZERO_IAM}/v1/user/roles/redirect/page`
    : `${HZERO_IAM}/v1/users/tenant/redirect/page`;
  return request(reqUrl, {
    method: 'GET',
    query: params,
  });
}
