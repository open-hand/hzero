/**
 * @Author: guanglong.sun <guanglong.sun@hand-china.com>
 * @Create time: 2019-09-25 09:37:16
 * @Copyright: Copyright(c) 2019, Hand
 * @Description: 常量
 */
import { API_HOST, HZERO_HIOT } from 'utils/config';

const STATUS_TYPE = 'STATUS_POINT'; // 数据点类型--状态 来源于HIOT.PROPERTY_TYPE_CATEGORY
const POINT_TYPE = 'MEASURING_POINT'; // 数据点类型--测量点 来源于HIOT.PROPERTY_TYPE_CATEGORY
const CONTROL_TYPE = 'CONTROL_PARAMETER'; // 数据点类型--控制参数 来源于HIOT.PROPERTY_TYPE_CATEGORY
const CONTROL_INT_TYPE = 'CONTROL_PARAMETER'; // 数据点类型--控制参数-数值
const CONTROL_BOOL_TYPE = 'CONTROL_BOOL'; // 数据点类型--控制参数-布尔
const EDGINK_TYPE = 'EDGINK'; // edgink协议
const MODBUS_TCP = 'MODBUS_TCP'; // modbus-tcp协议
const MODBUS_RTU = 'MODBUS_RTU'; // modbus-rtu协议
const EDIT_ACTION = 'edit'; // 编辑操作
const DETAIL_ACTION = 'detail'; // 查看详情操作
const DELETE_ACTION = 'delete'; // 删除操作
const BIND_ACTION = 'bind'; // 绑定操作
const NEW_ACTION = 'create'; // 新建操作
const REGISTER_ACTION = 'register'; // 注册操作
const DEVICE_DEFAULT_STATUS = 'NON_REGISTERED'; // 设备默认状态
const DEVICE_DEFAULT_CONNECT = 0; // 设备默认连接状态
const DATA_TYPE = {
  BOOL: 'BOOL',
  NUMBER: 'NUMBER',
  ENUM: 'ENUM',
  DATE: 'DATE',
  DATE_TIME: 'DATE_TIME',
}; // 数据点类型
const DEVICE_CONNECT_TYPE = 'hzero-iot:38678a04954b4e9497848022660ae542_web_connected_message';
const DEVICE_REAL_TIME_TYPE = 'hzero-iot:38678a04954b4e9497848022660ae542_web_data';
const MONITOR_REAL_TIME_TYPE = 'hzero-iot:38678a04954b4e9497848022660ae542_web_index_date';
const API_PREFIX = `${API_HOST}${HZERO_HIOT}`;
const CODE_REG = /^[a-zA-Z][a-zA-Z0-9_]{2,30}$/; // ^[a-zA-Z][\w+$]{1,30}$
const VERSION_REG = /^([0-9]\d|[0-9])(\.([0-9]\d|\d)){2}$/;
const CODE_UPPER_REG = /^[A-Z0-9][A-Z0-9_]*$/;
export {
  STATUS_TYPE,
  POINT_TYPE,
  CONTROL_BOOL_TYPE,
  CONTROL_INT_TYPE,
  HZERO_HIOT,
  EDGINK_TYPE,
  MODBUS_TCP,
  MODBUS_RTU,
  EDIT_ACTION,
  BIND_ACTION,
  DELETE_ACTION,
  DETAIL_ACTION,
  NEW_ACTION,
  REGISTER_ACTION,
  DEVICE_DEFAULT_STATUS,
  DEVICE_DEFAULT_CONNECT,
  API_PREFIX,
  CONTROL_TYPE,
  DATA_TYPE,
  DEVICE_CONNECT_TYPE,
  DEVICE_REAL_TIME_TYPE,
  MONITOR_REAL_TIME_TYPE,
  CODE_REG,
  VERSION_REG,
  CODE_UPPER_REG,
};
