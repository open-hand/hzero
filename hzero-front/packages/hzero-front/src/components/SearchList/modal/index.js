import React from 'react';
import { StoreProvider } from './stores';
import SearchModal from './SearchModal';

export default props => (
  <StoreProvider {...props}>
    <SearchModal />
  </StoreProvider>
);
