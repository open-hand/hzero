/**
 * 文件相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import qs from 'query-string';

import { HZERO_FILE } from '../config';
import { filterNullValueObject, getRequestId } from './common';
import { getAccessToken } from './token';
import { getCurrentOrganizationId, isTenantRoleLevel } from './user';

/**
 * 通过文件服务器的接口获取可访问的文件URL
 *
 * @export
 * @param {String} url 上传接口返回的 Url
 * @param {String} bucketName 桶名
 * @param {Number} tenantId 租户Id
 * @param {String} bucketDirectory 文件目录
 * @param {String} storageCode 存储配置编码
 */
// @ts-ignore
export function getAttachmentUrl(url, bucketName, tenantId, bucketDirectory, storageCode) {
  const accessToken = getAccessToken();
  const requestId = getRequestId();
  const params = qs.stringify(
    filterNullValueObject({
      bucketName,
      storageCode,
      access_token: accessToken,
      'H-Request-Id': requestId,
      directory: bucketDirectory,
    })
  );
  const newUrl = !isTenantRoleLevel()
    ? `${HZERO_FILE}/v1/files/download?${params}&url=${encodeURIComponent(url)}`
    : `${HZERO_FILE}/v1/${getCurrentOrganizationId()}/files/download?${params}&url=${encodeURIComponent(
        url
      )}`;
  return newUrl;
}
