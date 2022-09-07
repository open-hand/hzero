import intl from 'utils/intl';

export const FRONTAL_MACHINE_STATUS = {
  EDIT: ['NEW', 'DISABLED'],
  ENABLE: ['DISABLED'],
  DISABLE: ['ONLINE', 'OFFLINE'],
};

export const FRONTAL_JOB_JOB_STATUS = {
  EDIT: ['NEW', 'MODIFIED', 'PUBLISHED'],
  PUBLISH: ['NEW', 'MODIFIED'],
  DISABLE: ['PUBLISHED'],
  ENABLE: ['DISABLED'],
};

export const FRONTAL_SERVER_TAG_STATUS = [
  {
    status: 'NEW',
    color: 'green',
    text: intl.get('hfnt.preposedMachine.model.status.new').d('新建'),
  },
  {
    status: 'ONLINE',
    color: 'green',
    text: intl.get('hfnt.preposedMachine.model.status.online').d('已上线'),
  },
  {
    status: 'OFFLINE',
    color: 'red',
    text: intl.get('hfnt.preposedMachine.model.status.offline').d('已断开'),
  },
  {
    status: 'DISABLED',
    color: 'red',
    text: intl.get('hfnt.preposedMachine.model.status.stop').d('已停用'),
  },
];

export const FRONTAL_JOB_TAG_JOB_STATUS = [
  {
    status: 'NEW',
    color: 'green',
    text: intl.get('hfnt.scheduledTask.model.status.new').d('新建'),
  },
  {
    status: 'MODIFIED',
    color: 'gold',
    text: intl.get('hfnt.scheduledTask.model.status.modefied').d('修改中'),
  },
  {
    status: 'PUBLISHED',
    color: 'green',
    text: intl.get('hfnt.scheduledTask.model.status.published').d('已发布'),
  },
  {
    status: 'DISABLED',
    color: 'red',
    text: intl.get('hfnt.scheduledTask.model.status.disabed').d('已失效'),
  },
];
