import React from 'react';
import Exception from 'components/Exception';
import OfflineBg from '../../../assets/market/offline-bg.png';

/**
 * OfflinePage
 * 市场客户端离线页面
 */
export default function OfflinePage() {
  return (
    <Exception
      type="404"
      img={OfflineBg}
      title="无网络"
      desc={
        <span>
          抱歉，您的网络环境暂不支持获取最新的版本信息
          <br />
          请切换外网尝试！
        </span>
      }
    />
  );
}
