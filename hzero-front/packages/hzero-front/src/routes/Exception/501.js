/**
 * 如果 页面刷新, 不能保存当前的状态 使用 UnKnow 和 UnKnow 作为默认消息
 * 501错误页面
 * @author WY <yang.wang06@hand-china.com>
 * @date 2019-07-17
 * @copyright 2019-07-17 © HAND
 */
import React from 'react';
import { Link } from 'dva/router';
import Exception from 'components/Exception';
import { connect } from 'dva';

function Normal501Exception(props) {
  const { normal501: { status, message } = {} } = props;
  return (
    <Exception
      type="500"
      style={{ minHeight: 500, height: '80%' }}
      linkElement={Link}
      title={status || 'UnKnow'}
      desc={message || 'UnKnow Message'}
    />
  );
}

export default connect(({ error = {} }) => {
  const { normal501 = {} } = error;
  return {
    normal501,
  };
})(Normal501Exception);
