/**
 *
 * 覆盖文件
 */
import React from 'react';
import { Avatar } from 'hzero-ui';

const customSuspensionComponent = (style) => {
  const map = new Map([
    [
      'TEST1',
      <Avatar
        size="large"
        style={{
          background: '#16263e',
          lineHeight: '36px',
          ...style,
        }}
      >
        测试用
      </Avatar>,
    ],
  ]);
  return map;
};

// // 自定义系统名称
// const systemNameConfig = () => {
//   return [
//     { title: 'HZERO开发环境', lang: 'zh_CN' },
//     { title: 'HZERO DEVELOP ENV', lang: 'en_US' },
//   ];
// };

// // 自定义手机号码校验
// const phoneReg = () => {
//   return /^134[0-8]\d{7}$|^13[^4]\d{8}$|^14[5-9]\d{8}$|^15[^4]\d{8}$|^16[6]\d{8}$|^17[0-8]\d{8}$|^18[\d]{9}$|^19[8,9]\d{8}$/;
// };

// ；

const layoutExtraHeader = () => {
  const map = new Map([['TEST6', <div style={{ color: 'white' }}>1234</div>]]);

  return map;
};

const configureParams = () => ({
  // hzer-ui 弹窗移动全局配置属性
  modalMovable: true,
  // hzero-ui 弹窗居中全局配置数据
  modalAutoCenter: true,
  // notification 关闭延时时间
  // notificationDuration: 6,
  // 默认UED主题
  // defaultTheme: 'theme1',
});

export { customSuspensionComponent, layoutExtraHeader, configureParams };
