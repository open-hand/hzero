/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/25
 * @copyright HAND ® 2019
 */

import request from 'utils/request';
import { getEnvConfig } from 'utils/iocUtils';
import { getResponse } from 'utils/utils';

const { HZERO_PLATFORM } = getEnvConfig();

/**
 * 获取加密公钥
 */
export async function getPublicKey() {
  const res = request(`${HZERO_PLATFORM}/v1/tool/pass/public-key`, {
    method: 'GET',
  });

  // FIXME: @WJC utils need fix
  // @ts-ignore
  return getResponse(res);
}
