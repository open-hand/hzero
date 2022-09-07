import React from 'react';
import ORCHESTRATION_LANG from '@/langs/orchestrationLang';
import {
  HttpDrawer,
  FieldMappingDrawer,
  DataTransformDrawer,
  ConditionDrawer,
  CommonNodeCard,
} from './Modals';

// 构建Drawer
const renderChildren = (type, commonCardProps, drawerProps) => {
  let drawer = <></>;
  switch (type) {
    case 'HTTP-NODE':
      drawer = <HttpDrawer {...drawerProps} />;
      break;
    case 'TRANSFORM-NODE':
      drawer = <FieldMappingDrawer {...drawerProps} />;
      break;
    case 'CAST-NODE':
      drawer = <DataTransformDrawer {...drawerProps} />;
      break;
    case 'CONDITIONS-NODE':
      drawer = <ConditionDrawer {...drawerProps} />;
      break;
    default:
  }
  return (
    <>
      <CommonNodeCard {...commonCardProps} />
      {drawer}
    </>
  );
};

// Modal相应的属性
const nodeDrawerProps = {
  'HTTP-NODE': {
    title: ORCHESTRATION_LANG.HTTP_TITLE,
    style: {
      width: 900,
    },
  },
  'TRANSFORM-NODE': {
    title: ORCHESTRATION_LANG.FIELD_MAPPING,
    style: {
      width: 1100,
    },
  },
  'CAST-NODE': {
    title: ORCHESTRATION_LANG.DATA_TRANSFORM,
    style: {
      width: 1100,
    },
  },
  'CONDITIONS-NODE': {
    title: ORCHESTRATION_LANG.GATEWAY_TITLE,
    style: {
      width: 900,
    },
  },
};

export { renderChildren, nodeDrawerProps };
