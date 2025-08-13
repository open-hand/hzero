/**
 * regExp.js
 * @date 2018/11/5
 * @author WY yang.wang06@hand-china.com
 * @copyright Copyright (c) 2018, Hand
 */

import { getConfig } from 'hzero-boot';

let phone = '';
const phoneReg = getConfig('phoneReg');
if (phoneReg) {
  if (typeof phoneReg === 'function') {
    phone = phoneReg();
  } else {
    phone = phoneReg;
  }
}

/**
 * 验证 邮箱
 * 提示: intl.get('hzero.common.validation.email').d('邮箱格式不正确')
 */
export const EMAIL = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;

/**
 * 验证 手机,
 * 提示: intl.get('hzero.common.validation.phone').d('手机格式不正确')
 */
export const PHONE =
  phone ||
  /^134[0-8]\d{7}$|^13[^4]\d{8}$|^14[5-9]\d{8}$|^15[^4]\d{8}$|^16[6]\d{8}$|^17[0-8]\d{8}$|^18[\d]{9}$|^19[8,9]\d{8}$/;

/**
 * 验证 字符串 前后 必须为 非空白字符
 * 提示: intl.get('hzero.common.validate.trim').d('前后不能为空')
 */
export const TRIM = /^\S$|^\S.*\S$/;

/**
 * 验证 密码
 * 提示: intl.get('hzero.common.validation.password').d('至少包含数字/字母/字符2种组合,长度至少为6个字符')
 */
export const PASSWORD = /^(?![0-9]+$)(?![a-zA-Z]+$)(?![a-z]+$)(?![!@#$%^&*=]+$)[0-9A-Za-z!@#$%^&*=]{6,}$/;

/**
 * 验证 网址，必须带有http | https
 * 提示: intl.get('hzero.common.validation.httpUrl').d('请输入以“http/https”开头的正确网址')
 */
export const STRICT_URL = /(http|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\\.,@?^=%&:/~\\+#]*[\w\-\\@?^=%&/~\\+#])?/;

/**
 * 验证 身份证号码
 * 提示: intl.get('hzero.common.validation.identityCard').d('身份证号码格式不正确')
 */
export const IDENTITY_CARD = /^(^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$)|(^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])((\d{4})|\d{3}[Xx])$)$/;

/**
 * 验证 护照号码
 * 提示: intl.get('hzero.common.validation.passport').d('护照号码格式不正确')
 */
export const PASSPORT = /^1[45][0-9]{7}$|(^[P|p|S|s]\d{7}$)|(^[S|s|G|g|E|e]\d{8}$)|(^[Gg|Tt|Ss|Ll|Qq|Dd|Aa|Ff]\d{8}$)|(^[H|h|M|m]\d{8,10}$)/;

/**
 * 验证 编码 大小写限制
 * 提示：intl.get('hzero.common.validation.code').d('大小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”')
 */
export const CODE = /^[a-zA-Z0-9][a-zA-Z0-9-_./]*$/;

/**
 * 验证 编码 小写限制
 * 提示：intl.get('hzero.common.validation.codeLower').d('全小写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”')
 */
export const CODE_LOWER = /^[a-z0-9][a-z0-9-_./]*$/;

/**
 * 验证 编码 大写限制
 * 提示：intl.get('hzero.common.validation.codeUpper').d('全大写及数字，必须以字母、数字开头，可包含“-”、“_”、“.”、“/”')
 */
export const CODE_UPPER = /^[A-Z0-9][A-Z0-9-_./]*$/;

/**
 * 非+86手机号码 仅且仅能包含数字
 * 验证 非+86手机号码
 * 提示: intl.get('hzero.common.validation.phone').d('手机格式不正确')
 */
export const NOT_CHINA_PHONE = /^\d+$/;
