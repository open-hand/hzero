import { notification } from 'hzero-ui';
import { ArgsProps } from 'hzero-ui/es/notification';
import { getEnvConfig } from 'utils/iocUtils';
import intl from './intl';

const {
  configureParams: { notificationDuration },
} = getEnvConfig();

type NotificationOptions = Partial<ArgsProps>;

/**
 * 操作成功通知提示
 * @function success
 * @param {NotificationOptions} options - 默认属性
 * @param {?string} [options.message=操作成功] - 提示信息
 * @param {?string} [options.description] - 详细描述
 */
function success(options: NotificationOptions) {
  const { message, description, ...others } = options || {};
  notification.success({
    message: message || intl.get('hzero.common.notification.success').d('操作成功'),
    description,
    duration: notificationDuration || 2.5,
    className: 'success',
    ...others,
  });
}

/**
 * 操作失败通知提示
 * @function error
 * @param {NotificationOptions} options - 默认属性
 * @param {?string} [options.message=操作失败] - 提示信息
 * @param {?string} [options.description] - 详细描述
 */
function error(options: NotificationOptions) {
  const { message, description, ...others } = options || {};
  notification.error({
    message: message || intl.get('hzero.common.notification.error').d('操作失败'),
    description,
    duration: notificationDuration || 2.5,
    className: 'error',
    ...others,
  });
}

/**
 * 操作异常通知提示
 * @function warning
 * @param {NotificationOptions} options - 默认属性
 * @param {?string} [options.message=操作异常] - 提示信息
 * @param {?string} [options.description] - 详细描述
 */
function warning(options: NotificationOptions) {
  const { message, description, ...others } = options || {};
  notification.warning({
    message: message || intl.get('hzero.common.notification.warn').d('操作异常'),
    description,
    duration: notificationDuration || 2.5,
    className: 'warn',
    ...others,
  });
}

/**
 * 操作信息通知提示
 * @function info
 * @param {NotificationOptions} options - 默认属性
 * @param {?string} [options.message] - 提示信息
 * @param {?string} [options.description] - 详细描述
 */
function info(options: NotificationOptions) {
  const { message, description, ...others } = options || {};
  notification.info({
    message,
    description,
    duration: notificationDuration || 2.5,
    className: 'info',
    ...others,
  });
}

export default {
  success,
  error,
  warning,
  info,
};
