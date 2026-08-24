import {
  createRootContainer,
  getRoot,
  registerRootProviders,
  inject,
  ClassProvider,
} from 'what-di';
import { BaseProvider } from 'what-di/lib/base-provider';
import DefaultContainer from './DefaultContainer';
import * as defaultConfig from '../config';
import { UedProvider } from './UedProvider';

export class ConfigProvider extends BaseProvider<any> {
  private _config: any;

  constructor() {
    super();
    this._config = { ...defaultConfig };
  }

  public extends(config: any) {
    this._config = { ...this.config, ...config };
  }

  public get config() {
    return this._config;
  }
}

const configProvider: ClassProvider = {
  provide: 'config',
  useClass: ConfigProvider,
};

export function initIoc() {
  const root = getRoot();
  if (!root) {
    createRootContainer({
      providers: [],
    });
  }
  // 初始化环境变量
  const conf = inject('config');
  if (!conf) {
    registerRootProviders([configProvider]);
  }

  // 初始化UED
  const ued = inject(UedProvider);
  if (!ued) {
    // @ts-ignore
    registerRootProviders([UedProvider]);
    const u = inject<UedProvider>(UedProvider);
    u.registerContainer(DefaultContainer);
  }
}
