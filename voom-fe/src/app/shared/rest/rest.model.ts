export interface ApiResponse<TResponse> {
  data: TResponse | null;
  headers: HttpHeaders;
}

export type HttpHeaders = {
  [key: string]: string | null;
};

export interface RequestConfig {
  headers?: RequestHeaders;
  params?: {

  };
  authenticated?: boolean;
}

export type ResponseType = 'blob' | 'json' | 'text';

export interface RequestHeaders {
  accept?: string;
  contentType?: string;
  responseType?: ResponseType;
  otherHeaders?: Record<string, string>;
}

export interface MultiPart {
  name: string;
  content: string | File;
}
