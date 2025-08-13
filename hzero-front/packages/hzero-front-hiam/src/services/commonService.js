import { HZERO_IAM } from 'utils/config';
import request from 'utils/request';

export async function getPasswordRule(params = {}) {
  return request(`${HZERO_IAM}/v1/${params.organizationId}/password-policies/query`, {
    method: 'GET',
  });
}
