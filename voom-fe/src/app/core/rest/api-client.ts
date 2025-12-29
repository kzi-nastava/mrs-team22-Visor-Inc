import {ApiResponse, MultiPart, RequestConfig} from './rest.model';
import {Observable} from 'rxjs';


export abstract class ApiClient {
  public abstract get<TRequest, TResponse>(path: string, config?: RequestConfig): Observable<ApiResponse<TResponse>>;
  public abstract post<TRequest, TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>>;
  public abstract put<TRequest, TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<ApiResponse<TResponse>>;
  public abstract delete<TRequest, TResponse>(path: string, config?: RequestConfig): Observable<ApiResponse<TResponse>>;
  // public abstract postMultipart<TRequest extends MultiPart[], TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<TResponse>;
  // public abstract putMultipart<TRequest extends MultiPart[], TResponse>(path: string, body?: TRequest, config?: RequestConfig): Observable<TResponse>;
}
