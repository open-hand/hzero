/* eslint-disable no-bitwise */
/**
 * 密码相关
 * @date: 2019-12-25
 * @author: wjc <jiacheng.wang@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */

import { JSEncrypt } from 'jsencrypt';

/**
 * 加密密码
 * @param {String} password - 需要加密的密码
 */
export function encryptPwd(password, publicKey) {
  /* 如果没有 password 字段，则不进行加密 */
  if (!password) {
    return password;
  }
  if (publicKey) {
    /* 有公钥 使用 rsa 加密, 否则使用 md5 加密 */
    // 初始化加密器
    const encrypt = new JSEncrypt();
    // 设置公钥
    encrypt.setPublicKey(publicKey);
    // 加密
    return encrypt.encrypt(password);
  }
  return password;
}

// /**
//  * 加密密码
//  * @param {String} password - 需要加密的密码
//  */
// export function encryptMd5(password) {
//   const keyStr = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=';
//   let output = '';
//   let chr1;
//   let chr2;
//   let chr3;
//   let enc1;
//   let enc2;
//   let enc3;
//   let enc4;
//   let i = 0;
//   do {
//     chr1 = password.charCodeAt(i++);
//     chr2 = password.charCodeAt(i++);
//     chr3 = password.charCodeAt(i++);
//     enc1 = chr1 >> 2;
//     enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
//     enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
//     enc4 = chr3 & 63;
//     if (isNaN(chr2)) {
//       enc3 = 64;
//       enc4 = 64;
//     } else if (isNaN(chr3)) {
//       enc4 = 64;
//     }
//     output =
//       output +
//       keyStr.charAt(enc1) +
//       keyStr.charAt(enc2) +
//       keyStr.charAt(enc3) +
//       keyStr.charAt(enc4);
//     chr1 = '';
//     chr2 = '';
//     chr3 = '';
//     enc1 = '';
//     enc2 = '';
//     enc3 = '';
//     enc4 = '';
//   } while (i < password.length);
//   return output;
// }
// /* eslint-enable no-bitwise */
