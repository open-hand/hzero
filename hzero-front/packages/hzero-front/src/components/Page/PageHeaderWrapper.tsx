import React from 'react';
import Header from './Header';
import Content from './Content';

interface PageHeaderWrapperProps {
  children?: React.ReactNode | string;
  title: React.ReactNode | string;
  header?: React.ReactNode | string;
  headerProps?: {
    backPath: any;
    backTooltip: any;
    isChange: any;
  };
  contentProps?: {
    title?: React.ReactNode | string;
    description?: React.ReactNode | string;
    children?: React.ReactNode | string;
    style: React.CSSProperties;
    wrapperStyle: string;
    wrapperClassName?: string; // 包裹的className
    className?: string; // 真正的 Content 的样式
    noCard?: boolean;
  };
}

const PageHeaderWrapper: React.SFC<PageHeaderWrapperProps> = ({
  title = '',
  header,
  headerProps,
  contentProps,
  children,
}) => {
  return (
    <>
      <Header title={title} {...headerProps}>
        {header}
      </Header>
      <Content {...contentProps}>{children}</Content>
    </>
  );
};

export default PageHeaderWrapper;
