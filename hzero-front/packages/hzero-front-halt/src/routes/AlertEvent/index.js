/**
 * 告警事件管理 - Provider
 * @date: 2020-5-20
 * @author: hulingfangzi <lingfangzi.hu01@hand-china.com>
 * @version: 0.0.1
 * @copyright Copyright (c) 2020, Hand
 */

import React from 'react';
import { StoreProvider } from './stores';
import ListPage from './List/ListPage';

export default (props) => (
  <StoreProvider {...props}>
    <ListPage />
  </StoreProvider>
);
