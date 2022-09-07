import React from 'react';
import { Tag } from 'hzero-ui';

export function processStatusRender(processStatus, currentStatus) {
  if (!currentStatus) {
    return <Tag color="geekblue">{processStatus.APPROVAL || ''}</Tag>;
  }
  switch (currentStatus) {
    case 'APPROVED':
      return <Tag color="green">{processStatus.APPROVED || ''}</Tag>;
    case 'REJECTED':
      return <Tag color="red">{processStatus.REJECTED || ''}</Tag>;
    case 'STOP':
      return <Tag>{processStatus.STOP || ''}</Tag>;
    case 'REVOKE':
      return <Tag color="magenta">{processStatus.REVOKE || ''}</Tag>;
    case 'SUSPENDED':
      return <Tag color="orange">{processStatus.SUSPENDED || ''}</Tag>;
    default:
      return <Tag color="geekblue">{processStatus.APPROVAL || ''}</Tag>;
  }
}
