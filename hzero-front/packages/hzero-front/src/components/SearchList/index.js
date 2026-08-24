import React from 'react';
import { StoreProvider } from './stores';
import SearchList from './SearchList';

export default props => (
  <StoreProvider {...props}>
    <SearchList />
  </StoreProvider>
);
