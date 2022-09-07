import React from 'react';
import { Icon } from 'hzero-ui';

import intl from 'utils/intl';

function getRegExpStatus(flagCount, regStr, value) {
  const regStrArr = [];
  for (let i = 0; i < flagCount; i++) {
    regStrArr.push(regStr);
  }
  const reg = new RegExp(`${regStrArr.join('')}`);
  return reg.test(value);
}

function renderMsgContent(msg, flag) {
  return (
    <>
      {flag ? (
        <Icon type="check-circle-o" style={{ color: '#52c41a' }} />
      ) : (
        <Icon type="close-circle-o" style={{ color: '#f5222d' }} />
      )}
      <span style={{ marginLeft: 2, color: flag ? '#52c41a' : '#f5222d' }}>{msg}</span>
    </>
  );
}

export function validatePasswordRule(value = '', callback = e => e, validData = {}) {
  const {
    loginName,
    digitsCount,
    lowercaseCount,
    maxLength,
    minLength,
    notUsername,
    specialCharCount,
    uppercaseCount,
  } = validData;
  const msg = [];
  let allFlag = false;
  if (minLength && maxLength) {
    const flag = value.length < minLength || value.length > maxLength;
    allFlag = !flag;
    msg.push(
      renderMsgContent(
        intl
          .get('hzero.common.validation.passwordLength', {
            min: minLength,
            max: maxLength,
          })
          .d(`${minLength}-${maxLength}个字符`),
        !flag
      )
    );
  }
  if (!notUsername) {
    const flag = value === loginName;
    allFlag = !flag && allFlag;
    if (flag) {
      msg.push(
        renderMsgContent(
          intl.get('hzero.common.validation.notUsername').d(`密码不能与登录名相同`),
          false
        )
      );
    }
  }
  if (lowercaseCount) {
    const flag = getRegExpStatus(lowercaseCount, '([a-z].*)', value);
    allFlag = flag && allFlag;
    msg.push(
      renderMsgContent(
        intl
          .get('hzero.common.validation.lowercaseCount', {
            lower: lowercaseCount,
          })
          .d(`至少包含${lowercaseCount}个小写字符`),
        flag
      )
    );
  }
  if (uppercaseCount) {
    const flag = getRegExpStatus(uppercaseCount, '([A-Z].*)', value);
    allFlag = flag && allFlag;
    msg.push(
      renderMsgContent(
        intl
          .get('hzero.common.validation.uppercaseCount', {
            upper: uppercaseCount,
          })
          .d(`至少包含${uppercaseCount}个大写字符`),
        flag
      )
    );
  }
  if (digitsCount) {
    const flag = getRegExpStatus(digitsCount, '([0-9].*)', value);
    allFlag = flag && allFlag;
    msg.push(
      renderMsgContent(
        intl
          .get('hzero.common.validation.digits.min', { digitsCount })
          .d(`至少包含${digitsCount}个数字`),
        flag
      )
    );
  }
  if (specialCharCount) {
    const flag = getRegExpStatus(specialCharCount, '([~`@#$%^&*/\\-_=+|/()<>,.;:!].*)', value);
    allFlag = flag && allFlag;
    msg.push(
      renderMsgContent(
        intl
          .get('hzero.common.validation.specialChart.min', { specialCount: specialCharCount })
          .d(`至少包含${specialCharCount}个特殊字符 ([~\`@#$%^&*/\\-_=+|/()<>,.;:!].*)`),
        flag
      )
    );
  }
  if (value) {
    if (msg.length > 0) {
      const msgDom = msg.map(item => <div key={item}>{item}</div>);
      callback(allFlag ? undefined : msgDom);
    } else {
      callback();
    }
  } else {
    callback();
  }
}
