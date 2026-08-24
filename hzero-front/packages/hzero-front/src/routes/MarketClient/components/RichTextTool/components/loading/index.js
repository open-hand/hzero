import React from 'react';
import { Spin } from 'hzero-ui';

export default function Loading({ wrapperStyle, ...rest }) {
  return (
    <div
      style={{
        minHeight: 150,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        ...wrapperStyle,
      }}
    >
      <Spin {...rest} />
    </div>
  );
}
