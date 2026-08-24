import React from 'react';
import { Spin } from 'hzero-ui';

export default function Loading({ wrapperStyle, ...rest }) {
  const { delay, indicator, size, spinning, tip, wrapperClassName } = rest;
  const spinProps = { delay, indicator, size, spinning, tip, wrapperClassName };

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
      <Spin {...spinProps} />
    </div>
  );
}
