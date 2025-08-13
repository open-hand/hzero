import React from 'react';
import Container from '@hzero-front-ui/cfg/lib/components/Container';
import LayoutTheme from '../../components/UedTheme';

const DefaultContainer = ({ children, ...rest }) => {
  return (
    <Container {...rest}>
      <LayoutTheme />
      {children}
    </Container>
  );
};

export default DefaultContainer;
