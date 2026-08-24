import request from 'utils/request';
import { HZERO_FILE, HZERO_IAM, HZERO_PLATFORM } from 'utils/config';
import { getCurrentOrganizationId, isTenantRoleLevel } from 'utils/utils';

/**
 * 获取当前用户的信息
 */
export async function userInfoQuerySelf(query) {
  return request(`${HZERO_IAM}/hzero/v1/users/self/detail`, {
    method: 'GET',
    query,
  });
}

/**
 * userInfoUpdateRealName-修改 昵称
 * @param {String} realName - 昵称
 */
export async function userInfoUpdateRealName(realName) {
  return request(`${HZERO_IAM}/hzero/v1/users/real-name`, {
    method: 'PUT',
    query: {
      realName,
    },
  });
}

/**
 * userInfoQueryRoleDataSource-查询当前登录用户所拥有的角色
 */
export async function userInfoQueryRoleDataSource() {
  return request(`${HZERO_IAM}/hzero/v1/member-roles/self-roles`, {
    method: 'GET',
  });
}

/**
 * userInfoUpdateDefaultRole-修改 默认角色
 * @param {Number} organizationId - 租户id
 * @param {String|Number} roleId - 修改的 默认角色id
 */
export async function userInfoUpdateDefaultRole(organizationId, roleId) {
  return request(`${HZERO_IAM}/hzero/v1/users/default-role`, {
    method: 'PUT',
    query: {
      roleId,
      tenantId: organizationId,
    },
  });
}

/**
 * userInfoQueryCompanyDataSource-查询当前登录用户所拥有的公司
 * @param {!Number} organizationId - 当前登录用户的租户id
 */
export async function userInfoQueryCompanyDataSource(organizationId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/companies/list-by-current-user`, {
    method: 'GET',
    query: {
      enabledFlag: 1,
    },
  });
}

/**
 * userInfoUpdateDefaultCompany-修改 默认公司
 * @param {String|Number} companyId - 修改的 默认公司id
 */
export async function userInfoUpdateDefaultCompany(organizationId, companyId) {
  return request(`${HZERO_IAM}/hzero/v1/users/default-company`, {
    method: 'PUT',
    query: {
      companyId,
      tenantId: organizationId,
    },
  });
}

/**
 * 修改密码
 * @param {Object} params 验证信息
 * @param {String} params.password 新密码
 * @param {String} params.originalPassword 旧密码
 */
export async function userInfoUpdatePassword({
  password,
  originalPassword,
  phone,
  captcha,
  captchaKey,
  businessScope,
}) {
  return request(`${HZERO_IAM}/hzero/v1/users/password`, {
    method: 'PUT',
    body: {
      originalPassword,
      password,
      phone,
      captcha,
      captchaKey,
      businessScope,
    },
  });
}

/**
 * 验证 身份
 * @param {Object} params 校验信息
 * @param {String} params.captcha 验证码
 * @param {String} params.captchaKey 当前验证码的 key
 */
export async function userInfoValidatePre(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/captcha/pre-validate`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 向对应的手机号发送验证码
 * @param {Object} params 验证手机号
 * @param {String} params.phone 手机号
 */
export async function userInfoPostOldPhoneCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 向对应的手机号发送验证码修改密码
 * @param {Object} params 验证手机号
 * @param {String} params.phone 手机号
 */
export async function userInfoVerifyPhoneCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'UPDATE_PASSWORD',
    },
  });
}

/**
 * 绑定未绑定的手机号
 */
export async function userInfoValidateUnCheckedPhone(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone/authenticate`, {
    method: 'PUT',
    query: params,
  });
}

/**
 * 向旧邮箱发送验证码
 */
export async function userInfoPostOldEmailCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/email/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 向对应新的邮箱发送验证码
 * @param {Object} params 邮箱
 * @param {String} params.email 邮箱
 */
export async function userInfoPostNewEmailCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/email-new/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 绑定未绑定的邮箱
 */
export async function userInfoValidateUnCheckedEmail(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/email/authenticate`, {
    method: 'PUT',
    query: params,
  });
}

/**
 * 验证新的 邮箱 和 验证码 是否匹配
 * @param {Object} params 验证email 与 对应的code
 * @param {String} params.email 邮箱
 * @param {String} params.captcha 验证码
 * @param {String} params.captchaKey 原手机号的校验码
 * @param {String} params.lastCheckKey 原手机号和验证码的校验码
 */
export async function userInfoValidateNewEmail(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/email-new/validate-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 验证登录密码是不是正确的
 * @param {Object} params 验证信息
 * @param {String} params.password 密码
 */
export async function userInfoValidatePrePassword(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/password/pre-validate`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 向对应新的手机号发送验证码
 * @param {Object} params 验证手机号
 * @param {String} params.phone 手机号
 */
export async function userInfoPostNewPhoneCaptcha(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone-new/send-captcha`, {
    method: 'GET',
    query: {
      ...params,
      businessScope: 'self',
    },
  });
}

/**
 * 验证新的 手机号 和 验证码 是否匹配
 * @param {Object} params 验证手机号 与 对应的code
 * @param {String} params.phone 手机号
 * @param {String} params.code 验证码
 */
export async function userInfoValidateNewPhone(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/phone-new/validate-captcha`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 查询当前登陆用户的 默认公司
 * @async
 * @param {Number} organizationId - 登陆用户的租户id
 * @param {Number} companyId - 登陆用户的默认公司Id
 */
export async function userInfoCompanyQuery(organizationId, companyId) {
  return request(`${HZERO_PLATFORM}/v1/${organizationId}/companies/${companyId}`, {
    method: 'GET',
  });
}

/**
 * 查询系统支持的语言数据
 * @async
 * @function queryLanguageData
 * @returns fetch Promise
 */
export async function queryLanguageData() {
  return request(`${HZERO_PLATFORM}/v1/languages/list`, {
    method: 'GET',
  });
}

/**
 * 变更用户的默认语言选项
 * @async
 * @function updateDefaultLanguage
 * @param {!string} params.languageCode - 语言编码
 * @returns fetch Promise
 */
export async function updateDefaultLanguage(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/default-language`, {
    method: 'PUT',
    query: { ...params },
  });
}

/**
 * 变更用户的默认菜单布局选项
 * @async
 * @function updateDefaultMenu
 * @param {!string} params.
 * @returns fetch Promise
 */
export async function updateDefaultMenu(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/config-items`, {
    method: 'PUT',
    query: { ...params },
  });
}

/**
 * 变更用户的默认时间/日期选项
 * @async
 * @function updateDefaultDate
 * @param {!string} params.dateFormat - 日期格式
 * @param {!string} params.timeFormat - 时间格式
 * @returns fetch Promise
 */
export async function updateDefaultDate(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/datetime-format`, {
    method: 'PUT',
    query: { ...params },
  });
}

/**
 * 变更用户的默认时区选项
 * @async
 * @function updateDefaultTimeZone
 * @param {!string} params.timeZone - 时区编码
 * @returns fetch Promise
 */
export async function updateDefaultTimeZone(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/time-zone`, {
    method: 'PUT',
    query: { ...params },
  });
}

/**
 * 验证邮箱是否已注册
 * @param {String} params.email - 邮箱
 */
export async function validateEmailRegisterService(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/validation/email`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 验证手机是否已注册
 * @param {String} params.phone - 手机
 */
export async function validatePhoneRegisterService(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/validation/phone`, {
    method: 'GET',
    query: params,
  });
}

/**
 * 获取可上传文件的类型和大小
 * @param {Number} params.tenantId
 * @param {String} params.bucketName
 */
export async function fetchEnabledFile(params) {
  return request(
    `${HZERO_FILE}/v1/${
      isTenantRoleLevel() ? `${getCurrentOrganizationId()}/` : ''
    }upload-configs/detail`,
    {
      method: 'GET',
      query: params,
    }
  );
}

/**
 * 上传裁剪好的头像
 * @async
 * @function uploadAvatar
 * @returns {object} fetch Promise
 */
export async function uploadAvatar(params) {
  const formData = new FormData();
  formData.append('bucketName', params.bucketName);
  formData.append('directory', params.directory);
  formData.append('fileName', params.uploadImgName);
  formData.append('file', params.image, params.uploadImgName);
  return request(
    `${HZERO_FILE}/v1/${isTenantRoleLevel() ? `${params.organizationId}/` : ''}files/multipart`,
    {
      method: 'POST',
      type: 'FORM',
      processData: false, // 不会将 data 参数序列化字符串
      body: formData,
      responseType: 'text',
    }
  );
}

/**
 * 保存头像
 * @async
 * @function saveAvatar
 * @param {String} params - 保存参数
 */
export async function saveAvatar(params) {
  return request(`${HZERO_IAM}/hzero/v1/users/avatar`, {
    method: 'PUT',
    query: { avatar: params },
  });
}

/**
 * 获取第三方绑定
 * @async
 * @function saveAvatar
 * @param {String} params - 保存参数
 */
export async function fetchOpenAccountList() {
  return request(`${HZERO_IAM}/hzero/v1/user-open-account`, {
    method: 'GET',
  });
}

/**
 * 解除第三方绑定
 * @async
 * @function unBindOpenAccount
 * @param {String} params - 保存参数
 */
export async function unBindOpenAccount(params) {
  return request(`${HZERO_IAM}/hzero/v1/user-open-account/unbind`, {
    method: 'POST',
    body: params,
  });
}

/**
 * 绑定三方账户之前获取认证地址
 * @async
 * @function saveAvatar
 * @param {String} params - 保存参数
 */
export async function beforeBind(params) {
  return request(`${HZERO_IAM}/hzero/v1/user-open-account/before-bind`, {
    method: 'GET',
    query: params,
  });
}
