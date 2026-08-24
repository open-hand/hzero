import { IConfig } from 'umi'; // ref: https://umijs.org/config/

const config: IConfig = {
  presets: ['hzero-cli-preset-ui'],
  mock: {
    exclude: ['mock/**/*.[jt]s'],
  },
};

export default config;
