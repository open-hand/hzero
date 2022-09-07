/**
 * sqlExecute - SQL执行界面/右侧
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { withRouter } from 'dva/router';
import ExecuteResult from './ExecuteResult';
import ExecuteForm from './ExecuteForm';

@withRouter
export default class SqlExecute extends PureComponent {
  render() {
    return (
      <React.Fragment>
        <ExecuteForm />
        <ExecuteResult />
      </React.Fragment>
    );
  }
}
