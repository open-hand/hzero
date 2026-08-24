/**
 * sqlExecute - SQL执行界面
 * @date: 2018-9-27
 * @author: LZY <zhuyan.luo@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2018, Hand
 */
import React, { PureComponent } from 'react';
import { Content } from 'components/Page';

import formatterCollections from 'utils/intl/formatterCollections';

import TreeTable from './TreeTable';
import ExecutePage from './ExecutePage';
import style from './index.less';

@formatterCollections({ code: ['hpfm.sqlExecute'] })
export default class SqlExecute extends PureComponent {
  render() {
    return (
      <React.Fragment>
        <Content className={style['sql-execute']}>
          <div className={style['sql-execute-left']}>
            <TreeTable />
          </div>
          <div className={style['sql-execute-right']}>
            <ExecutePage />
          </div>
        </Content>
      </React.Fragment>
    );
  }
}
