/**
 * @email WY <yang.wang06@hand-china.com>
 * @creationDate 2019/12/19
 * @copyright HAND ® 2019
 */
import React from 'react';
import { getEnvConfig } from 'utils/iocUtils';

import DefaultMenuTabs from '../../../../../components/DefaultMenuTabs/index';
import IM from '../../../../../components/IM/index';

import { getClassName as getCommonLayoutClassName } from '../../../../utils';

const { IM_ENABLE } = getEnvConfig();
interface ContentProps {
  components: {};
  getClassName?: (cls: string) => string;
}

const Content: React.FC<ContentProps> = ({ getClassName = getCommonLayoutClassName }) => {
  const isImEnable = React.useMemo(() => {
    let imEnable;
    try {
      imEnable = JSON.parse(IM_ENABLE);
    } catch (e) {
      imEnable = false;
    }
    return imEnable;
  }, []);
  return (
    <div className={getClassName('content')}>
      <DefaultMenuTabs />
      {isImEnable && <IM />}
    </div>
  );
};

export default Content;
