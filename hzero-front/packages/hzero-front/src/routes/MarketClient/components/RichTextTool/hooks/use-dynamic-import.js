import React, { useEffect, useState } from 'react';
import Loading from '../components/loading';

/* eslint-disable react-hooks/exhaustive-deps */

/**
 * 动态导入node_modules中的模块和自定义的模块，减小主文件打包体积
 * 适用于模块较大且只需要运行时加载的情况
 */
export default function useDynamicImport({ loader, loaders, onLoad } = {}) {
  const [loading, setLoading] = useState(true);
  const [modules, setModules] = useState(null);

  useEffect(() => {
    let unmounted = false;
    if (loader) {
      (async function () {
        const res = await loader();
        if (unmounted) return;
        setModules([res]);
        setLoading(false);
      })();
    } else if (loaders) {
      (async function () {
        const res = await Promise.all(loaders.map((loaderValue) => loaderValue()));
        if (unmounted) return;
        setModules(res);
        setLoading(false);
      })();
    } else {
      throw new Error('useDynamicImport: loader or loaders must exist.');
    }
    // eslint-disable-next-line no-return-assign
    return () => (unmounted = true);
  }, []);

  useEffect(() => {
    if (!loading && onLoad) onLoad(modules);
  }, [loading]);

  function renderModule(renderFunction) {
    if (loading) return <Loading />;
    return renderFunction(modules[0]);
  }

  function renderModules(renderFunction) {
    if (loading) return <Loading />;
    return renderFunction(modules);
  }

  return { loading, modules, renderModule, renderModules };
}
