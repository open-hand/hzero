/**
 * 个性化组件
 * @date: 2020-3-11
 * @version: 0.0.1
 * @author: zhaotong <tong.zhao@hand-china.com>
 * @copyright Copyright (c) 2020, Hands
 */

import { IntlField, Upload } from 'choerodon-ui/pro';
import { HZERO_PLATFORM } from 'utils/config';
import React from 'react';
import { isNil } from 'lodash';
import { getCurrentOrganizationId, getUserOrganizationId } from 'utils/utils';

export function FlexLink(options, extra = {}) {
  const { currentData = {} } = extra;
  const { linkTitle, linkHref, linkNewWindow } = options;
  let newHref = linkHref || '';
  let newTitle = linkTitle || '';
  const mappings = newHref.match(/{([^{}]*)}/g);
  const titleMappings = newTitle.match(/{([^{}]*)}/g);

  if (mappings) {
    newHref = replace(mappings, currentData, newHref);
  }
  if (titleMappings) {
    newTitle = replace(titleMappings, currentData, newTitle);
  }
  return (
    <a href={newHref} target={linkNewWindow ? '_blank' : '_self'} rel="noopener noreferrer">
      {newTitle}
    </a>
  );
}

export function FlexUpload(props) {
  return <Upload {...props} />;
}
export function FlexIntlField(props) {
  const tlsUrl = `${HZERO_PLATFORM}/v1/multi-language`;
  return <IntlField tlsUrl={tlsUrl} {...props} />;
}

function replace(mappings, values, targetString) {
  let newString = targetString;
  for (let i = 0; i < mappings.length; i++) {
    if (mappings[i] === '{organizationId}' || mappings[i] === '{tenantId}') {
      // eslint-disable-next-line no-continue
      continue;
    }
    const key = mappings[i].match(/{([^{}]*)}/)[1];
    const value = isNil(values[key]) ? '' : values[key];
    newString = newString.replace(`{${key}}`, value);
  }
  newString = newString.replace(/{organizationId}/, getCurrentOrganizationId());
  newString = newString.replace(/{tenantId}/, getUserOrganizationId());
  return newString;
}
