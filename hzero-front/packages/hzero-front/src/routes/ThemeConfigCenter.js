import React from 'react';
import ConfigCenter from '@hzero-front-ui/cfg/lib/pages';
import formatterCollections from 'utils/intl/formatterCollections';

const ThemeConfigCenter = formatterCollections({ code: ['hzero.hzeroTheme'] })(props => (
  <ConfigCenter lang={props.language} />
));

export default ThemeConfigCenter;
