// 格式化处理系统名称
import { getConfig } from 'hzero-boot';

export const getSystemName = (language) => {
  let systemName = '';
  const systemNameConfig = getConfig('systemNameConfig');

  if (systemNameConfig) {
    if (typeof systemNameConfig === 'function') {
      // 为函数形式
      const data = systemNameConfig();
      if (Array.isArray(data)) {
        if (data.length > 0) {
          const filterData = data.filter((item) => item.lang === language);
          systemName = filterData.length > 0 ? filterData[0].title : data[0].title;
        }
      } else if (typeof data === 'string') {
        try {
          const formatData = JSON.parse(data);
          if (Array.isArray(formatData) && formatData.length > 0) {
            const filterData = formatData.filter((item) => item.lang === language);
            systemName = filterData.length > 0 ? filterData[0].title : formatData[0].title;
          }
        } catch {
          console.log(
            'systemNameConfig should be the correct JSON format, array, or function that returns the value JSON or array'
          );
        }
      }
    } else if (Array.isArray(systemNameConfig)) {
      // 为数组形式
      if (systemNameConfig.length > 0) {
        const filterData = systemNameConfig.filter((item) => item.lang === language);
        systemName = filterData.length > 0 ? filterData[0].title : systemNameConfig[0].title;
      }
    } else if (typeof systemNameConfig === 'string') {
      // 为字符串json形式
      try {
        const formatData = JSON.parse(systemNameConfig);
        if (Array.isArray(formatData) && formatData.length > 0) {
          const filterData = formatData.filter((item) => item.lang === language);
          systemName = filterData.length > 0 ? filterData[0].title : formatData[0].title;
        }
      } catch {
        console.log(
          'systemNameConfig should be the correct JSON format, array, or function that returns the value JSON or array'
        );
      }
    }
  }

  return systemName;
};
