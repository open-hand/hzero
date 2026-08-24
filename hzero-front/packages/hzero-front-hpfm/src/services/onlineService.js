import request from 'utils/request';

/**
 * 登出
 */
export async function logout(record) {
  const { loginName } = record;
  const url = '/admin/token';
  return request(url, {
    method: 'DELETE',
    body: [loginName],
  });
}
