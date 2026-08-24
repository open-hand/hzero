import { BaseProvider } from 'what-di/lib/base-provider';

export interface UedProviderState {
  Container?: any;
}

export class UedProvider extends BaseProvider<UedProviderState> {
  public registerContainer(Container: any) {
    this.setState({ ...this.state, Container });
  }

  get Container(): any {
    return this.state.Container;
  }
}
