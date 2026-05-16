declare module 'axios' {
  export interface AxiosResponse<T = unknown> {
    data: T;
  }

  export interface AxiosInstance {
    get<T = unknown>(url: string, config?: unknown): Promise<AxiosResponse<T>>;
    post<T = unknown>(url: string, data?: unknown, config?: unknown): Promise<AxiosResponse<T>>;
    put<T = unknown>(url: string, data?: unknown, config?: unknown): Promise<AxiosResponse<T>>;
    delete<T = unknown>(url: string, config?: unknown): Promise<AxiosResponse<T>>;
    interceptors: {
      request: {
        use(handler: (config: { headers: Record<string, string> }) => { headers: Record<string, string> }): void;
      };
    };
  }

  interface AxiosStatic {
    create(config?: unknown): AxiosInstance;
  }

  const axios: AxiosStatic;
  export default axios;
}
